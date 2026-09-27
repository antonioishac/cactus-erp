package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.ProdutoImagem;
import br.com.cactuserp.pdv.repository.ProdutoImagemRepository;
import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class ProdutoImagemService {

    private static final Logger log = LoggerFactory.getLogger(ProdutoImagemService.class);

    @Value("${cactus.pdv.imagens.pasta-base}")
    private String pastaBase;

    @Autowired
    private ProdutoImagemRepository imagemRepository;

    /** Cache de imagens já carregadas, evita ler do disco a cada clique. */
    private final Map<Long, Image> cacheImagens = new ConcurrentHashMap<>();

    /** Imagem padrão usada quando produto não tem foto. Carregada uma vez. */
    private Image imagemPadrao;

    @PostConstruct
    public void init() {
        try {
            this.imagemPadrao = new Image(
                    getClass().getResourceAsStream("/fxml/imagens/produto-sem-imagem.png")
            );
        } catch (Exception e) {
            log.warn("Imagem padrão não encontrada, será usada imagem nula");
        }
    }

    /**
     * Busca e carrega a imagem principal de um produto.
     * @param idProduto ID do produto
     * @return Image carregada, ou imagem padrão se não houver/falhar
     */
    public Image buscarImagemProduto(Long idProduto) {
        // 1. Verifica cache
        Image cached = cacheImagens.get(idProduto);
        if (cached != null) {
            return cached;
        }

        // 2. Busca no banco
        Optional<ProdutoImagem> optImagem = imagemRepository.findImagemPrincipal(idProduto);
        if (optImagem.isEmpty()) {
            log.debug("Produto {} sem imagem cadastrada", idProduto);
            return imagemPadrao;
        }

        // 3. Resolve caminho físico
        ProdutoImagem produtoImagem = optImagem.get();
        Path caminhoFisico = Paths.get(pastaBase, produtoImagem.getCaminho());

        // 4. Verifica existência
        if (!Files.exists(caminhoFisico)) {
            log.warn("Arquivo de imagem não encontrado: {}", caminhoFisico);
            return imagemPadrao;
        }

        // 5. Carrega
        try {
            Image img = new Image(caminhoFisico.toUri().toString());
            cacheImagens.put(idProduto, img);
            return img;
        } catch (Exception e) {
            log.error("Erro ao carregar imagem do produto {}: {}", idProduto, e.getMessage());
            return imagemPadrao;
        }
    }

    /**
     * Versão assíncrona — recomendada para listas grandes.
     * Retorna a imagem padrão imediatamente e dispara o carregamento em background.
     */
    public Image buscarImagemProdutoAsync(Long idProduto, Consumer<Image> aoCarregar) {
        Image cached = cacheImagens.get(idProduto);
        if (cached != null) {
            return cached;
        }

        // Carrega em thread separada para não travar a UI
        CompletableFuture.supplyAsync(() -> buscarImagemProduto(idProduto))
                .thenAccept(img -> Platform.runLater(() -> aoCarregar.accept(img)));

        return imagemPadrao;
    }

    /** Limpa o cache. Útil quando uma imagem é atualizada. */
    public void invalidarCache(Long idProduto) {
        cacheImagens.remove(idProduto);
    }

    public void limparCacheCompleto() {
        cacheImagens.clear();
    }
}