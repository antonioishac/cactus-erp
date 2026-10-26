"""Gera a migration dos catálogos oficiais de IBS/CBS (CST e cClassTrib) a partir da tabela da SVRS.

Uso (quando a tabela oficial mudar):
  1. Baixe a página https://dfe-portal.svrs.rs.gov.br/DFE/TabelaClassificacaoTributaria (pública).
  2. Extraia o JSON da variável JavaScript `dadosOriginais` da página para um arquivo (ver extrair_json abaixo).
  3. python gerar_catalogo_ibs_cbs.py tabela.json ../src/main/resources/db/migration/VNN__ibs_cbs_catalogos_atualizacao.sql
     e ajuste o SQL gerado para UPSERT/delta (a V13 é a carga inicial; migration aplicada não se edita).

Origem da V13__ibs_cbs_catalogos.sql: este script, sobre a tabela publicada até 2026-10-01, extraída em 2026-10-25.
"""

import json, sys

dados = json.load(open(sys.argv[1], encoding='utf-8'))
saida = sys.argv[2]

def s(v):
    if v is None:
        return 'NULL'
    return "'" + str(v).replace("\\", "\\\\").replace("'", "''") + "'"

def b(v):
    return '1' if v else '0'

def d(v):
    return 'NULL' if not v else "'" + v[:10] + "'"

def n(v):
    return 'NULL' if v is None else format(float(v), '.4f')

linhas = []
linhas.append('''-- Reforma Tributária do Consumo (EC 132/2023, LC 214/2025) — catálogos oficiais do IBS/CBS.
--
-- FONTE: tabela oficial "Código de Classificação Tributária do IBS e da CBS" publicada pela SVRS
-- (Conformidade Fácil), https://dfe-portal.svrs.rs.gov.br/DFE/TabelaClassificacaoTributaria,
-- extraída em 2026-10-25 (publicação mais recente da tabela: 2026-10-01): 18 CST e 173 cClassTrib.
-- Gerada por script a partir do JSON oficial da página, sem digitação manual. Atualização da tabela
-- oficial = nova migration (catálogo de leitura, não editável pela API).
--
-- Alíquotas por ano: NT 2025.002-RTC v1.50 (jun/2026), regras UB18-10 (pIBSUF), UB37-10 (pIBSMun)
-- e UB56-10 (pCBS), arts. 343 e 346 da LC 214/2025. Só os anos com alíquota definida em lei/NT são
-- semeados; ano sem linha aqui = resolução recusada com mensagem (nunca alíquota inventada).

CREATE TABLE cst_ibs_cbs (
    codigo                  CHAR(3)       NOT NULL,
    descricao               VARCHAR(150)  NOT NULL,
    exige_tributacao        TINYINT(1)    NOT NULL COMMENT 'IndExigeTrib — exige o grupo gIBSCBS com valores',
    reducao_aliquota        TINYINT(1)    NOT NULL COMMENT 'IndReducaoAliq — grupo gRed (pRedAliq/pAliqEfet)',
    reducao_base_calculo    TINYINT(1)    NOT NULL COMMENT 'IndReducaoBc',
    diferimento             TINYINT(1)    NOT NULL COMMENT 'IndDiferimento — grupo gDif',
    monofasica              TINYINT(1)    NOT NULL COMMENT 'IndMonofasica — grupo gIBSCBSMono',
    transferencia_credito   TINYINT(1)    NOT NULL COMMENT 'IndTransferenciaCred — grupo gTransfCred',
    credito_presumido_zfm   TINYINT(1)    NOT NULL COMMENT 'IndCredPresIbsZfm',
    ajuste_competencia      TINYINT(1)    NOT NULL COMMENT 'IndAjusteCompet',
    inicio_vigencia         DATE          NOT NULL,
    fim_vigencia            DATE          NULL,
    PRIMARY KEY (codigo)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE classificacao_tributaria_ibs_cbs (
    codigo                  CHAR(6)       NOT NULL COMMENT 'cClassTrib — os 3 primeiros dígitos são o CST',
    cst                     CHAR(3)       NOT NULL,
    descricao               VARCHAR(1000) NOT NULL,
    nome_reduzido           VARCHAR(150)  NULL,
    tributacao_regular      TINYINT(1)    NOT NULL COMMENT 'IndTribRegular — exige grupo gTribRegular',
    permite_credito_presumido TINYINT(1)  NOT NULL,
    percentual_reducao_ibs  DECIMAL(7,4)  NOT NULL COMMENT 'PercRedIbs (%), vira pRedAliq do gIBSUF/gIBSMun',
    percentual_reducao_cbs  DECIMAL(7,4)  NOT NULL COMMENT 'PercRedCbs (%), vira pRedAliq do gCBS',
    tipo_aliquota           TINYINT       NOT NULL COMMENT 'TipoAliq da tabela oficial',
    aceita_nfe              TINYINT(1)    NOT NULL COMMENT 'IndNfe — pode ser usado em NF-e (modelo 55)',
    aceita_nfce             TINYINT(1)    NOT NULL COMMENT 'IndNfce — pode ser usado em NFC-e (modelo 65)',
    url_legislacao          VARCHAR(255)  NULL,
    data_publicacao         DATE          NOT NULL,
    inicio_vigencia         DATE          NOT NULL,
    fim_vigencia            DATE          NULL,
    PRIMARY KEY (codigo),
    KEY idx_classificacao_ibs_cbs_cst (cst),
    CONSTRAINT fk_classificacao_ibs_cbs_cst FOREIGN KEY (cst) REFERENCES cst_ibs_cbs (codigo)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE aliquota_ibs_cbs_vigencia (
    ano                     SMALLINT      NOT NULL,
    aliquota_ibs_uf         DECIMAL(7,4)  NOT NULL,
    aliquota_ibs_municipio  DECIMAL(7,4)  NOT NULL,
    aliquota_cbs            DECIMAL(7,4)  NOT NULL,
    fundamento              VARCHAR(255)  NOT NULL,
    PRIMARY KEY (ano)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

INSERT INTO aliquota_ibs_cbs_vigencia (ano, aliquota_ibs_uf, aliquota_ibs_municipio, aliquota_cbs, fundamento) VALUES
    (2025, 0.1000, 0.0000, 0.9000, 'LC 214/2025 arts. 343 e 346; NT 2025.002-RTC v1.50 UB18-10/UB37-10/UB56-10'),
    (2026, 0.1000, 0.0000, 0.9000, 'LC 214/2025 arts. 343 e 346; NT 2025.002-RTC v1.50 UB18-10/UB37-10/UB56-10');

-- Regra tributária IBS/CBS da célula grupo tributário × operação fiscal (1:1, mesmo padrão de pis/cofins/ipi).
CREATE TABLE ibs_cbs_configuracao (
    id                          BIGINT       NOT NULL AUTO_INCREMENT,
    configuracao_tributaria_id  BIGINT       NOT NULL,
    classificacao_tributaria    CHAR(6)      NOT NULL,
    data_criacao                DATETIME(6)  NOT NULL,
    data_atualizacao            DATETIME(6)  NOT NULL,
    criado_por                  VARCHAR(100) NULL,
    atualizado_por              VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ibs_cbs_configuracao (configuracao_tributaria_id),
    CONSTRAINT fk_ibs_cbs_config_configuracao FOREIGN KEY (configuracao_tributaria_id) REFERENCES configuracao_tributaria (id),
    CONSTRAINT fk_ibs_cbs_config_classificacao FOREIGN KEY (classificacao_tributaria) REFERENCES classificacao_tributaria_ibs_cbs (codigo)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- "Resolvido padrão" do produto (mesmo papel de cst_icms_padrao): classificação vigente na célula padrão.
ALTER TABLE produto
    ADD COLUMN cst_ibs_cbs_padrao             CHAR(3) NULL,
    ADD COLUMN classificacao_ibs_cbs_padrao   CHAR(6) NULL;
''')

linhas.append('INSERT INTO cst_ibs_cbs (codigo, descricao, exige_tributacao, reducao_aliquota, reducao_base_calculo, diferimento, monofasica, '
              'transferencia_credito, credito_presumido_zfm, ajuste_competencia, inicio_vigencia, fim_vigencia) VALUES')
vals = []
for c in dados:
    vals.append('    (' + ', '.join([s(c['Cst']), s(c['NomeCst']), b(c['IndExigeTrib']), b(c['IndReducaoAliq']), b(c['IndReducaoBc']),
                                    b(c['IndDiferimento']), b(c['IndMonofasica']), b(c['IndTransferenciaCred']), b(c['IndCredPresIbsZfm']),
                                    b(c['IndAjusteCompet']), d(c['DthIniVig']), d(c['DthFimVig'])]) + ')')
linhas.append(',\n'.join(vals) + ';\n')

linhas.append('INSERT INTO classificacao_tributaria_ibs_cbs (codigo, cst, descricao, nome_reduzido, tributacao_regular, permite_credito_presumido, '
              'percentual_reducao_ibs, percentual_reducao_cbs, tipo_aliquota, aceita_nfe, aceita_nfce, url_legislacao, data_publicacao, '
              'inicio_vigencia, fim_vigencia) VALUES')
vals = []
for c in dados:
    for t in c['ClassificacoesTributarias']:
        vals.append('    (' + ', '.join([s(t['CodClassTrib']), s(t['Cst']), s(t['NomeClassTrib'].strip()), s((t['NomeReduzido'] or '').strip() or None),
                                        b(t['IndTribRegular']), b(t['IndPermiteCredPres']), n(t['PercRedIbs']), n(t['PercRedCbs']),
                                        str(t['TipoAliq']), b(t['IndNfe']), b(t['IndNfce']), s(t['TexUrlLegislacao']),
                                        d(t['DthPublicacao']), d(t['DthIniVig']), d(t['DthFimVig'])]) + ')')
linhas.append(',\n'.join(vals) + ';\n')
open(saida, 'w', encoding='utf-8').write('\n'.join(linhas))
print('ok', len(vals))


def extrair_json(caminho_html, caminho_json):
    """Extrai `dadosOriginais` (JSON oficial embutido na página da SVRS)."""
    html = open(caminho_html, encoding='utf-8', errors='replace').read()
    inicio = html.index('var dadosOriginais = ') + len('var dadosOriginais = ')
    dados, _ = json.JSONDecoder().raw_decode(html[inicio:])
    json.dump(dados, open(caminho_json, 'w', encoding='utf-8'), ensure_ascii=False, indent=1)
