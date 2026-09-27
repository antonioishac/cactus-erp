package br.com.cactusdigital.erp.ged.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.ged.dto.ConteudoDocumentoResponse;
import br.com.cactusdigital.erp.ged.dto.DocumentoRequest;
import br.com.cactusdigital.erp.ged.dto.DocumentoResponse;
import br.com.cactusdigital.erp.ged.dto.DocumentoVersaoResponse;
import br.com.cactusdigital.erp.ged.entity.Documento;
import br.com.cactusdigital.erp.ged.entity.DocumentoVersao;
import br.com.cactusdigital.erp.ged.entity.TipoAcaoVersao;
import br.com.cactusdigital.erp.ged.entity.TipoDocumento;
import br.com.cactusdigital.erp.ged.exception.GedErrorCode;
import br.com.cactusdigital.erp.ged.mapper.GedMapper;
import br.com.cactusdigital.erp.ged.repository.DocumentoRepository;
import br.com.cactusdigital.erp.ged.repository.DocumentoVersaoRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Orquestra {@link Documento}/{@link DocumentoVersao}/{@link DocumentoStorageService} —
 * é aqui que upload/nova versão/download/exclusão lógica acontecem, nunca no controller.
 * O hash SHA-256 é calculado sobre o conteúdo inteiro em memória
 * ({@link MultipartFile#getBytes()}) — aceitável pro volume esperado de documentos de
 * gestão (contrato, nota fiscal, foto), não um streaming de arquivo multi-GB.
 */
@Service
@RequiredArgsConstructor
public class DocumentoService {

    private final DocumentoRepository repository;
    private final DocumentoVersaoRepository versaoRepository;
    private final TipoDocumentoService tipoDocumentoService;
    private final PastaDocumentoService pastaDocumentoService;
    private final DocumentoStorageService storageService;
    private final GedMapper mapper;

    @Transactional(readOnly = true)
    public Page<DocumentoResponse> listar(String entidadeTipo, Long entidadeId, Pageable pageable) {
        Page<Documento> pagina = repository.findByEntidadeTipoAndEntidadeId(entidadeTipo, entidadeId, pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, Documento::getId, repository::buscarComVersoes, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public DocumentoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<DocumentoVersaoResponse> listarVersoes(Long documentoId) {
        buscarEntidade(documentoId);
        return versaoRepository.findByDocumentoIdOrderByNumeroVersaoDesc(documentoId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public DocumentoResponse criar(DocumentoRequest request, MultipartFile arquivo, Long usuarioId) {
        TipoDocumento tipoDocumento = tipoDocumentoService.buscarEntidade(request.tipoDocumentoId());
        validarTamanho(arquivo, tipoDocumento);

        Documento documento = Documento.builder()
                .pasta(request.pastaId() != null ? pastaDocumentoService.buscarEntidade(request.pastaId()) : null)
                .tipoDocumento(tipoDocumento)
                .nome(request.nome())
                .descricao(request.descricao())
                .palavrasChave(request.palavrasChave())
                .entidadeTipo(request.entidadeTipo())
                .entidadeId(request.entidadeId())
                .permiteExclusao(request.permiteExclusao() == null || request.permiteExclusao())
                .permiteAlteracao(request.permiteAlteracao() == null || request.permiteAlteracao())
                .assinado(request.assinado() != null && request.assinado())
                .dataFimVigencia(request.dataFimVigencia())
                .situacao(Situacao.ATIVO)
                .build();
        documento = repository.save(documento);

        armazenarNovaVersao(documento, arquivo, TipoAcaoVersao.INCLUSAO, usuarioId, 1, null);
        return mapper.toResponse(documento);
    }

    @Transactional
    public DocumentoVersaoResponse adicionarVersao(Long documentoId, MultipartFile arquivo, String observacao, Long usuarioId) {
        Documento documento = buscarEntidade(documentoId);
        if (!documento.isPermiteAlteracao()) {
            throw new BusinessException(GedErrorCode.DOCUMENTO_ALTERACAO_NAO_PERMITIDA);
        }
        validarTamanho(arquivo, documento.getTipoDocumento());

        int proximaVersao = versaoRepository.findFirstByDocumentoIdOrderByNumeroVersaoDesc(documentoId)
                .map(v -> v.getNumeroVersao() + 1)
                .orElse(1);
        DocumentoVersao versao = armazenarNovaVersao(documento, arquivo, TipoAcaoVersao.ALTERACAO, usuarioId, proximaVersao, observacao);
        return mapper.toResponse(versao);
    }

    @Transactional
    public void excluir(Long documentoId, Long usuarioId) {
        Documento documento = buscarEntidade(documentoId);
        if (!documento.isPermiteExclusao()) {
            throw new BusinessException(GedErrorCode.DOCUMENTO_EXCLUSAO_NAO_PERMITIDA);
        }
        if (documento.getSituacao() == Situacao.INATIVO) {
            throw new ConflictException(GedErrorCode.DOCUMENTO_SITUACAO_INVALIDA);
        }

        int proximaVersao = versaoRepository.findFirstByDocumentoIdOrderByNumeroVersaoDesc(documentoId)
                .map(v -> v.getNumeroVersao() + 1)
                .orElse(1);
        versaoRepository.save(DocumentoVersao.builder()
                .documento(documento)
                .usuarioId(usuarioId)
                .numeroVersao(proximaVersao)
                .tipoAcao(TipoAcaoVersao.EXCLUSAO)
                .build());
        documento.setSituacao(Situacao.INATIVO);
    }

    @Transactional(readOnly = true)
    public ConteudoDocumentoResponse baixarUltimaVersao(Long documentoId) {
        DocumentoVersao versao = versaoRepository.findFirstByDocumentoIdOrderByNumeroVersaoDesc(documentoId)
                .orElseThrow(() -> NotFoundException.of("Versão de documento", documentoId));
        return baixarConteudoDaVersao(versao);
    }

    @Transactional(readOnly = true)
    public ConteudoDocumentoResponse baixarVersao(Long documentoId, Long versaoId) {
        DocumentoVersao versao = versaoRepository.findById(versaoId)
                .filter(candidata -> candidata.getDocumento().getId().equals(documentoId))
                .orElseThrow(() -> NotFoundException.of("Versão de documento", versaoId));
        return baixarConteudoDaVersao(versao);
    }

    private ConteudoDocumentoResponse baixarConteudoDaVersao(DocumentoVersao versao) {
        if (versao.getChaveArmazenamento() == null) {
            throw new BusinessException(GedErrorCode.DOCUMENTO_VERSAO_SEM_CONTEUDO);
        }
        Resource conteudo = storageService.recuperar(versao.getChaveArmazenamento());
        return new ConteudoDocumentoResponse(conteudo, versao.getNomeArquivoOriginal(), versao.getContentType());
    }

    private DocumentoVersao armazenarNovaVersao(
            Documento documento, MultipartFile arquivo, TipoAcaoVersao tipoAcao, Long usuarioId, int numeroVersao, String observacao) {
        byte[] conteudo = lerConteudo(arquivo);
        String prefixoChave = documento.getId() + "/" + numeroVersao;
        String chave = storageService.armazenar(new ByteArrayInputStream(conteudo), prefixoChave, arquivo.getOriginalFilename());

        DocumentoVersao versao = DocumentoVersao.builder()
                .documento(documento)
                .usuarioId(usuarioId)
                .numeroVersao(numeroVersao)
                .tipoAcao(tipoAcao)
                .hashArquivo(calcularHashSha256(conteudo))
                .chaveArmazenamento(chave)
                .nomeArquivoOriginal(arquivo.getOriginalFilename())
                .tamanhoBytes(arquivo.getSize())
                .contentType(arquivo.getContentType())
                .observacao(observacao)
                .build();
        return versaoRepository.save(versao);
    }

    private void validarTamanho(MultipartFile arquivo, TipoDocumento tipoDocumento) {
        BigDecimal tamanhoMb = BigDecimal.valueOf(arquivo.getSize()).divide(BigDecimal.valueOf(1024L * 1024L), 6, RoundingMode.HALF_UP);
        if (tamanhoMb.compareTo(tipoDocumento.getTamanhoMaximoMb()) > 0) {
            throw new BusinessException(GedErrorCode.DOCUMENTO_TAMANHO_EXCEDIDO);
        }
    }

    private byte[] lerConteudo(MultipartFile arquivo) {
        try {
            return arquivo.getBytes();
        } catch (IOException e) {
            throw new BusinessException(GedErrorCode.FALHA_ARMAZENAMENTO, "Falha ao ler o arquivo enviado: " + e.getMessage());
        }
    }

    private String calcularHashSha256(byte[] conteudo) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(conteudo));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 não disponível na JVM", e);
        }
    }

    Documento buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Documento", id));
    }
}
