package br.com.cactusdigital.erp.ged.service;

import java.io.InputStream;
import org.springframework.core.io.Resource;

/**
 * Armazenamento abstraído por interface — o resto do módulo nunca sabe se o arquivo
 * está em disco local, S3, Azure Blob ou MinIO, só conhece a {@code chaveArmazenamento}
 * opaca que esta interface devolve. Única implementação hoje: {@link
 * FilesystemDocumentoStorageService} (disco local, funcional sem credencial externa).
 * S3/Azure/MinIO ficam registrados como extensão futura — sem ambiente de produção real
 * com credencial pra validar de verdade, não construídos agora.
 * <p>
 * Contrato sem exceção checada de propósito — falha de infraestrutura vira
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException} com
 * {@link br.com.cactusdigital.erp.ged.exception.GedErrorCode#FALHA_ARMAZENAMENTO},
 * mesmo padrão de tradução de falha de infra usado em {@code EstoqueClient}
 * (gestao-compras-service).
 */
public interface DocumentoStorageService {

    /**
     * @param prefixoChave organiza fisicamente o arquivo (ex: {@code "42/1"} —
     *                     documentoId/numeroVersao); a implementação decide o resto do
     *                     formato da chave (tipicamente acrescenta um sufixo único).
     */
    String armazenar(InputStream conteudo, String prefixoChave, String nomeArquivoOriginal);

    Resource recuperar(String chaveArmazenamento);

    void remover(String chaveArmazenamento);
}
