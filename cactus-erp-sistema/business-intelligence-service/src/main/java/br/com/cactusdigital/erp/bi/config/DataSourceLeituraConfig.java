package br.com.cactusdigital.erp.bi.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 4 fontes de leitura somente-leitura (schema de outro serviço, acessado direto via
 * JDBC — ETL em lote só precisa de {@code SELECT} agregando, não de grafo de entidade;
 * exceção deliberada ao padrão de referência solta do resto do monorepo, ver Javadoc de
 * {@code EtlService}) + o {@code DataSource} PRIMÁRIO deste serviço ({@code cactus_bi},
 * usado por JPA/Flyway).
 * <p>
 * O primário precisa ser um {@code @Bean} explícito marcado {@link Primary} — assim que
 * qualquer outro {@code DataSource} é declarado no contexto, a autoconfiguração padrão
 * do Boot pra {@code spring.datasource.*} recua ({@code @ConditionalOnMissingBean}), e
 * sem isso o schema próprio (`cactus_bi`) nunca teria um {@code DataSource} de verdade.
 * <p>
 * <b>Achado real</b>: com 5 beans {@code DataSource} no contexto (o primário + as 4 de
 * leitura), injeção por parâmetro (`JdbcTemplate cadastroJdbcTemplate(DataSource
 * cadastroDataSource)`) NÃO resolve por nome de parâmetro — o algoritmo de autowire do
 * Spring prioriza {@code @Primary} ANTES de tentar casar por nome, então toda injeção
 * ambígua de {@code DataSource} caía silenciosamente no primário (`cactus_bi`), fazendo
 * o ETL falhar com "Table 'cactus_bi.produto' doesn't exist". Corrigido chamando os
 * métodos {@code @Bean} diretamente uns dos outros (o proxy CGLIB do
 * {@code @Configuration} garante que isso devolve o singleton já criado, não uma
 * instância nova) — sem passar pelo autowire ambíguo.
 * <p>
 * {@code setReadOnly(true)} nas 4 fontes de leitura não é só documentação — o driver
 * JDBC do MySQL rejeita de verdade qualquer instrução de escrita numa conexão marcada
 * assim, então mesmo um bug futuro que tentasse dar {@code UPDATE}/{@code INSERT} numa
 * dessas fontes falharia em tempo de execução, não silenciosamente.
 */
@Configuration
public class DataSourceLeituraConfig {

    @Value("${spring.datasource.url}")
    private String urlPrincipal;
    @Value("${spring.datasource.username}")
    private String usernamePrincipal;
    @Value("${spring.datasource.password}")
    private String passwordPrincipal;

    @Value("${cactus.bi.fontes.cadastro.url}")
    private String urlCadastro;
    @Value("${cactus.bi.fontes.cadastro.username}")
    private String usernameCadastro;
    @Value("${cactus.bi.fontes.cadastro.password}")
    private String passwordCadastro;

    @Value("${cactus.bi.fontes.estoque.url}")
    private String urlEstoque;
    @Value("${cactus.bi.fontes.estoque.username}")
    private String usernameEstoque;
    @Value("${cactus.bi.fontes.estoque.password}")
    private String passwordEstoque;

    @Value("${cactus.bi.fontes.compras.url}")
    private String urlCompras;
    @Value("${cactus.bi.fontes.compras.username}")
    private String usernameCompras;
    @Value("${cactus.bi.fontes.compras.password}")
    private String passwordCompras;

    @Value("${cactus.bi.fontes.vendas.url}")
    private String urlVendas;
    @Value("${cactus.bi.fontes.vendas.username}")
    private String usernameVendas;
    @Value("${cactus.bi.fontes.vendas.password}")
    private String passwordVendas;

    @Bean
    @Primary
    public DataSource dataSource() {
        return construir("bi-principal", urlPrincipal, usernamePrincipal, passwordPrincipal, false);
    }

    @Bean
    public DataSource cadastroDataSource() {
        return construir("bi-leitura-cadastro", urlCadastro, usernameCadastro, passwordCadastro, true);
    }

    @Bean
    public JdbcTemplate cadastroJdbcTemplate() {
        return new JdbcTemplate(cadastroDataSource());
    }

    @Bean
    public DataSource estoqueDataSource() {
        return construir("bi-leitura-estoque", urlEstoque, usernameEstoque, passwordEstoque, true);
    }

    @Bean
    public JdbcTemplate estoqueJdbcTemplate() {
        return new JdbcTemplate(estoqueDataSource());
    }

    @Bean
    public DataSource comprasDataSource() {
        return construir("bi-leitura-compras", urlCompras, usernameCompras, passwordCompras, true);
    }

    @Bean
    public JdbcTemplate comprasJdbcTemplate() {
        return new JdbcTemplate(comprasDataSource());
    }

    @Bean
    public DataSource vendasDataSource() {
        return construir("bi-leitura-vendas", urlVendas, usernameVendas, passwordVendas, true);
    }

    @Bean
    public JdbcTemplate vendasJdbcTemplate() {
        return new JdbcTemplate(vendasDataSource());
    }

    private DataSource construir(String nomePool, String url, String username, String password, boolean somenteLeitura) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setReadOnly(somenteLeitura);
        dataSource.setPoolName(nomePool);
        dataSource.setMaximumPoolSize(3);
        return dataSource;
    }
}
