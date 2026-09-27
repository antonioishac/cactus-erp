package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.fiscal.AcbrMonitorClient;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.fiscal.client.CadastroClient;
import br.com.cactusdigital.erp.fiscal.client.LojaVirtualClient;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaConfiguracaoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.ItemPedidoLojaClienteDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.LojaClienteDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.PedidoLojaClienteDTO;
import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalConsumidorResponse;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalConsumidor;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalConsumidorItem;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import br.com.cactusdigital.erp.fiscal.mapper.FiscalMapper;
import br.com.cactusdigital.erp.fiscal.repository.NotaFiscalConsumidorRepository;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Compõe e emite uma NFC-e a partir de um {@code Pedido} (loja-virtual-service) já
 * {@code PAGO} — ver Javadoc de {@link NotaFiscalConsumidor} pro desenho completo
 * (só Simples Nacional, sem seção de pagamento ainda).
 */
@Service
@RequiredArgsConstructor
public class NotaFiscalConsumidorService {

    private final NotaFiscalConsumidorRepository repository;
    private final LojaVirtualClient lojaVirtualClient;
    private final CadastroClient cadastroClient;
    private final AcbrMonitorClient acbrMonitorClient;
    private final NfceIniBuilder iniBuilder;
    private final FiscalMapper mapper;

    @Value("${cactus.acbr.host}")
    private String acbrHost;

    @Value("${cactus.acbr.porta}")
    private int acbrPorta;

    @Transactional(readOnly = true)
    public Page<NotaFiscalConsumidorResponse> listar(Pageable pageable) {
        Page<NotaFiscalConsumidor> pagina = repository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, NotaFiscalConsumidor::getId, repository::buscarComItens, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public NotaFiscalConsumidorResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    /**
     * Compõe a NFC-e (RASCUNHO) a partir do Pedido — busca pedido+loja (loja-virtual-service),
     * exige {@code lojaId} com {@code empresaId} vinculado e todo item com NCM/CSOSN
     * configurado (senão {@code ITEM_SEM_TRIBUTACAO_CONFIGURADA}). Reserva o próximo
     * número de NFC-e da série configurada. Não chama o ACBr Monitor ainda.
     */
    @Transactional
    public NotaFiscalConsumidorResponse gerarDePedido(Long pedidoId) {
        if (repository.existsByPedidoId(pedidoId)) {
            throw new ConflictException(FiscalErrorCode.PEDIDO_JA_TEM_NOTA_CONSUMIDOR);
        }

        PedidoLojaClienteDTO pedido = lojaVirtualClient.buscarPedido(pedidoId);
        if (!"PAGO".equals(pedido.situacao())) {
            throw new BusinessException(FiscalErrorCode.PEDIDO_SITUACAO_INVALIDA_PARA_NOTA_CONSUMIDOR);
        }

        LojaClienteDTO loja = lojaVirtualClient.buscarLoja(pedido.lojaId());
        if (loja.empresaId() == null) {
            throw new BusinessException(FiscalErrorCode.LOJA_SEM_EMPRESA_VINCULADA);
        }

        for (ItemPedidoLojaClienteDTO itemPedido : pedido.itens()) {
            if (itemPedido.ncmSnapshot() == null || itemPedido.csosnSnapshot() == null) {
                throw new BusinessException(FiscalErrorCode.ITEM_SEM_TRIBUTACAO_CONFIGURADA);
            }
        }

        EmpresaConfiguracaoFiscalDTO configFiscal = cadastroClient.reservarProximoNumeroNfce(loja.empresaId());

        NotaFiscalConsumidor nota = NotaFiscalConsumidor.builder()
                .pedidoId(pedido.id())
                .lojaId(pedido.lojaId())
                .empresaId(loja.empresaId())
                .clienteLojaId(pedido.clienteLojaId())
                .serie(configFiscal.serieNfce())
                .numero(configFiscal.proximoNumeroNfce())
                .ambiente("PRODUCAO".equalsIgnoreCase(configFiscal.ambienteFiscal()) ? AmbienteFiscal.PRODUCAO : AmbienteFiscal.HOMOLOGACAO)
                .valorTotal(pedido.valorTotal())
                .situacao(SituacaoNotaFiscal.RASCUNHO)
                .build();

        int numeroItem = 1;
        for (ItemPedidoLojaClienteDTO itemPedido : pedido.itens()) {
            nota.getItens().add(NotaFiscalConsumidorItem.builder()
                    .notaFiscalConsumidor(nota)
                    .numeroItem(numeroItem++)
                    .varianteProdutoLojaId(itemPedido.varianteProdutoLojaId())
                    .descricao(itemPedido.nomeSnapshot())
                    .ncm(itemPedido.ncmSnapshot())
                    .csosn(itemPedido.csosnSnapshot())
                    .quantidade(itemPedido.quantidade())
                    .valorUnitario(itemPedido.precoUnitario())
                    .valorTotal(itemPedido.valorTotal())
                    .build());
        }

        return mapper.toResponse(repository.save(nota));
    }

    /** Monta o comando {@code NFE.CriarEnviarNFe} (via {@link NfceIniBuilder}) e chama o ACBr Monitor. Nunca invocado ao vivo nesta sessão. */
    @Transactional
    public NotaFiscalConsumidorResponse emitir(Long id) {
        NotaFiscalConsumidor nota = buscarEntidade(id);
        if (nota.getSituacao() != SituacaoNotaFiscal.RASCUNHO && nota.getSituacao() != SituacaoNotaFiscal.ERRO) {
            throw new ConflictException(FiscalErrorCode.NOTA_FISCAL_SITUACAO_INVALIDA);
        }

        EmpresaFiscalDTO emitente = cadastroClient.buscarEmpresa(nota.getEmpresaId());
        PedidoLojaClienteDTO pedido = lojaVirtualClient.buscarPedido(nota.getPedidoId());

        nota.setDataEmissao(Instant.now());
        String comando = iniBuilder.construirComando(nota, emitente, pedido, 1);

        nota.setSituacao(SituacaoNotaFiscal.ENVIADA);
        try {
            String resposta = acbrMonitorClient.enviarComando(acbrHost, acbrPorta, comando);
            if (acbrMonitorClient.respostaComErro(resposta)) {
                nota.setSituacao(SituacaoNotaFiscal.REJEITADA);
                nota.setMensagemSefaz(truncar(resposta, 500));
                return mapper.toResponse(nota);
            }
            nota.setSituacao(SituacaoNotaFiscal.AUTORIZADA);
            nota.setChaveAcesso(extrair(resposta, "ChDFe", 44));
            nota.setProtocoloAutorizacao(extrair(resposta, "NProt", 30));
            nota.setMensagemSefaz(truncar("Autorizada.", 500));
            return mapper.toResponse(nota);
        } catch (IOException ex) {
            nota.setSituacao(SituacaoNotaFiscal.ERRO);
            nota.setMensagemSefaz(truncar("Falha de comunicação com o ACBr Monitor: " + ex.getMessage(), 500));
            return mapper.toResponse(nota);
        }
    }

    private String truncar(String texto, int max) {
        if (texto == null) {
            return null;
        }
        return texto.length() <= max ? texto : texto.substring(0, max);
    }

    private String extrair(String texto, String chave, int maxLength) {
        if (texto == null) {
            return null;
        }
        var matcher = java.util.regex.Pattern.compile("(?im)^\\s*" + chave + "\\s*=\\s*(.+?)\\s*$").matcher(texto);
        return matcher.find() ? truncar(matcher.group(1), maxLength) : null;
    }

    NotaFiscalConsumidor buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Nota fiscal de consumidor", id));
    }
}
