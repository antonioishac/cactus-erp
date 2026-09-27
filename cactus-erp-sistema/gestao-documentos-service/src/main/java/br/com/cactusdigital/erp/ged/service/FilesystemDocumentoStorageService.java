package br.com.cactusdigital.erp.ged.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.ged.exception.GedErrorCode;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Implementação em disco local do {@link DocumentoStorageService} — diretório
 * configurável via {@code cactus.ged.storage.base-path}. Cada chave é validada contra
 * o diretório base pra nunca escapar dele (proteção contra path traversal vindo de um
 * {@code nomeArquivoOriginal} malicioso, ex: {@code "../../etc/passwd"}).
 */
@Service
public class FilesystemDocumentoStorageService implements DocumentoStorageService {

    private final Path basePath;

    public FilesystemDocumentoStorageService(@Value("${cactus.ged.storage.base-path}") String basePathConfigurado) {
        this.basePath = Path.of(basePathConfigurado).toAbsolutePath().normalize();
        try {
            Files.createDirectories(basePath);
        } catch (IOException e) {
            throw new BusinessException(GedErrorCode.FALHA_ARMAZENAMENTO,
                    "Não foi possível criar o diretório de armazenamento: " + e.getMessage());
        }
    }

    @Override
    public String armazenar(InputStream conteudo, String prefixoChave, String nomeArquivoOriginal) {
        String chave = prefixoChave + "-" + UUID.randomUUID() + extensaoDe(nomeArquivoOriginal);
        Path destino = resolverDentroDoBasePath(chave);
        try {
            Files.createDirectories(destino.getParent());
            Files.copy(conteudo, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException(GedErrorCode.FALHA_ARMAZENAMENTO, "Falha ao gravar arquivo: " + e.getMessage());
        }
        return chave;
    }

    @Override
    public Resource recuperar(String chaveArmazenamento) {
        Path caminho = resolverDentroDoBasePath(chaveArmazenamento);
        if (!Files.exists(caminho)) {
            throw new BusinessException(GedErrorCode.FALHA_ARMAZENAMENTO, "Arquivo não encontrado no armazenamento: " + chaveArmazenamento);
        }
        return new FileSystemResource(caminho);
    }

    @Override
    public void remover(String chaveArmazenamento) {
        try {
            Files.deleteIfExists(resolverDentroDoBasePath(chaveArmazenamento));
        } catch (IOException e) {
            throw new BusinessException(GedErrorCode.FALHA_ARMAZENAMENTO, "Falha ao remover arquivo: " + e.getMessage());
        }
    }

    private Path resolverDentroDoBasePath(String chave) {
        Path caminho = basePath.resolve(chave).normalize();
        if (!caminho.startsWith(basePath)) {
            throw new BusinessException(GedErrorCode.FALHA_ARMAZENAMENTO, "Chave de armazenamento inválida");
        }
        return caminho;
    }

    private String extensaoDe(String nomeArquivo) {
        if (nomeArquivo == null) {
            return "";
        }
        int ponto = nomeArquivo.lastIndexOf('.');
        return ponto >= 0 ? nomeArquivo.substring(ponto) : "";
    }
}
