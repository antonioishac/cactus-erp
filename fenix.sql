-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema fenix
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema fenix
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `fenix` DEFAULT CHARACTER SET utf8 ;
USE `fenix` ;

-- -----------------------------------------------------
-- Table `fenix`.`PESSOA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`PESSOA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NOME` VARCHAR(150) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o nome da Pessoa\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `TIPO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo\",\"labelText\":\"Tipo\",\"tooltip\":\"Tipo de Pessoa\",\"hintText\":\"Tipo de pessoa: Física ou Jurídica\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"Física\",\"itens\":[{\"dropDownButtonItem\":\"Física\"},{\"dropDownButtonItem\":\"Jurídica\"}]}}\n',
  `SITE` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Site\",\"labelText\":\"Site\",\"tooltip\":\"Site\",\"hintText\":\"Informe o site da Pessoa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"url\",\"mascara\":\"\"}}\n',
  `EMAIL` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"E-Mail\",\"labelText\":\"E-Mail\",\"tooltip\":\"E-Mail\",\"hintText\":\"Informe o e-mail da Pessoa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"emailAddress\",\"mascara\":\"\"}}\n',
  `CLIENTE` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"É Cliente\",\"labelText\":\"Pessoa é Cliente\",\"tooltip\":\"Pessoa é Cliente\",\"hintText\":\"Pessoa é Cliente?\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}\n',
  `FORNECEDOR` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"É Fornecedor\",\"labelText\":\"Pessoa é Fornecedor\",\"tooltip\":\"Pessoa é Fornecedor\",\"hintText\":\"Pessoa é Fornecedor?\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}\n',
  `TRANSPORTADORA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"É Transportadora\",\"labelText\":\"Pessoa é Transportadora\",\"tooltip\":\"Pessoa é Transportadora\",\"hintText\":\"Pessoa é Transportadora?\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}\n',
  `COLABORADOR` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"É Colaborador\",\"labelText\":\"Pessoa é Colaborador\",\"tooltip\":\"Pessoa é Colaborador\",\"hintText\":\"Pessoa é Colaborador?\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}\n',
  `CONTADOR` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"É Contador\",\"labelText\":\"Pessoa é Contador\",\"tooltip\":\"Pessoa é Contador\",\"hintText\":\"Pessoa é Contador?\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
COMMENT = 'Essa tabela vai armazenar os dados da entidade PESSOA';


-- -----------------------------------------------------
-- Table `fenix`.`NIVEL_FORMACAO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NIVEL_FORMACAO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome do Nível de Formação\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Nível de Formação\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`ESTADO_CIVIL`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`ESTADO_CIVIL` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NOME` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome do Estado Civil\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Estado Civil\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`PESSOA_FISICA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`PESSOA_FISICA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_PESSOA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}\n',
  `ID_NIVEL_FORMACAO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nível de Formação\",\"labelText\":\"Nível de Formação\",\"tooltip\":\"Nível de Formação\",\"hintText\":\"Importe o Nível de Formação Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"nivel_formacao\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_ESTADO_CIVIL` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Estado Civil\",\"labelText\":\"Estado Civil\",\"tooltip\":\"Estado Civil\",\"hintText\":\"Importe o Estado Civil Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"estado_civil\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CPF` VARCHAR(11) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CPF\",\"labelText\":\"CPF\",\"tooltip\":\"CPF\",\"hintText\":\"Informe o CPF da Pessoa\",\"validacao\":\"CPF\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CPF\"}}\n',
  `RG` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"RG\",\"labelText\":\"RG\",\"tooltip\":\"RG\",\"hintText\":\"Informe o RG da Pessoa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `ORGAO_RG` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Órgão RG\",\"labelText\":\"Órgão RG\",\"tooltip\":\"Órgão RG\",\"hintText\":\"Informe o Órgão de Emissão do RG\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DATA_EMISSAO_RG` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Emissão\",\"labelText\":\"Data Emissão\",\"tooltip\":\"Data Emissão\",\"hintText\":\"Informe a Data de Emissão do RG\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `DATA_NASCIMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Nascimento\",\"labelText\":\"Data Nascimento\",\"tooltip\":\"Data Nascimento\",\"hintText\":\"Informe a Data de Nascimento da Pessoa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `SEXO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Sexo\",\"labelText\":\"Sexo\",\"tooltip\":\"Sexo\",\"hintText\":\"Informe o Sexo da Pessoa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Masculino\"},{\"dropDownButtonItem\":\"Feminino\"}]}}\n',
  `RACA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Raça\",\"labelText\":\"Raça\",\"tooltip\":\"Raça\",\"hintText\":\"Informe a Raça da Pessoa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Branco\"},{\"dropDownButtonItem\":\"Moreno\"},{\"dropDownButtonItem\":\"Negro\"},{\"dropDownButtonItem\":\"Pardo\"},{\"dropDownButtonItem\":\"Amarelo\"},{\"dropDownButtonItem\":\"Indígena\"},{\"dropDownButtonItem\":\"Outro\"}]}}\n',
  `NACIONALIDADE` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nacionalidade\",\"labelText\":\"Nacionalidade\",\"tooltip\":\"Nacionalidade\",\"hintText\":\"Informe a Nacionalidade da Pessoa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `NATURALIDADE` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Naturalidade\",\"labelText\":\"Naturalidade\",\"tooltip\":\"Naturalidade\",\"hintText\":\"Informe a Naturalidade da Pessoa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `NOME_PAI` VARCHAR(200) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome Pai\",\"labelText\":\"Nome Pai\",\"tooltip\":\"Nome Pai\",\"hintText\":\"Informe o Nome do Pai da Pessoa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `NOME_MAE` VARCHAR(200) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome Mãe\",\"labelText\":\"Nome Mãe\",\"tooltip\":\"Nome Mãe\",\"hintText\":\"Informe o Nome da Mãe da Pessoa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_PESSOA_FISICA_PESSOA1_idx` (`ID_PESSOA` ASC),
  INDEX `fk_PESSOA_FISICA_NIVEL_FORMACAO1_idx` (`ID_NIVEL_FORMACAO` ASC),
  INDEX `fk_PESSOA_FISICA_ESTADO_CIVIL1_idx` (`ID_ESTADO_CIVIL` ASC),
  CONSTRAINT `fk_PESSOA_FISICA_PESSOA1`
    FOREIGN KEY (`ID_PESSOA`)
    REFERENCES `fenix`.`PESSOA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PESSOA_FISICA_NIVEL_FORMACAO1`
    FOREIGN KEY (`ID_NIVEL_FORMACAO`)
    REFERENCES `fenix`.`NIVEL_FORMACAO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PESSOA_FISICA_ESTADO_CIVIL1`
    FOREIGN KEY (`ID_ESTADO_CIVIL`)
    REFERENCES `fenix`.`ESTADO_CIVIL` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Tabela que armazena os dados específicos para Pessoas Físicas';


-- -----------------------------------------------------
-- Table `fenix`.`PESSOA_JURIDICA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`PESSOA_JURIDICA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_PESSOA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}\n',
  `CNPJ` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ\",\"labelText\":\"CNPJ\",\"tooltip\":\"CNPJ\",\"hintText\":\"Informe o CNPJ da Pessoa\",\"validacao\":\"CNPJ\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}\n',
  `NOME_FANTASIA` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome de Fantasia\",\"labelText\":\"Nome de Fantasia\",\"tooltip\":\"Nome de Fantasia\",\"hintText\":\"Informe o Nome de Fantasia da Pessoa Jurídica\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `INSCRICAO_ESTADUAL` VARCHAR(45) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Inscrição Estadual\",\"labelText\":\"Inscrição Estadual\",\"tooltip\":\"Inscrição Estadual\",\"hintText\":\"Informe a Inscrição Estadual da Pessoa Jurídica\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `INSCRICAO_MUNICIPAL` VARCHAR(45) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Inscrição Municipal\",\"labelText\":\"Inscrição Municipal\",\"tooltip\":\"Inscrição Municipal\",\"hintText\":\"Informe a Inscrição Municipal da Pessoa Jurídica\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DATA_CONSTITUICAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Constituição\",\"labelText\":\"Data Constituição\",\"tooltip\":\"Data Constituição\",\"hintText\":\"Informe a Data de Constituição da Pessoa Jurídica\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `TIPO_REGIME` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Regime\",\"labelText\":\"Tipo Regime\",\"tooltip\":\"Tipo Regime\",\"hintText\":\"Informe o Tipo de Regime da Pessoa Jurídica\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"1-Lucro REAL\"},{\"dropDownButtonItem\":\"2-Lucro presumido\"},{\"dropDownButtonItem\":\"3-Simples nacional\"}]}}\n',
  `CRT` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CRT\",\"labelText\":\"CRT\",\"tooltip\":\"CRT\",\"hintText\":\"Informe o Código de Regime Tributário\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"1-Simples Nacional\"},{\"dropDownButtonItem\":\"2-Simples Nacional - excesso de sublimite da receita bruta\"},{\"dropDownButtonItem\":\"3-Regime Normal\"}]}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_PESSOA_JURIDICA_PESSOA1_idx` (`ID_PESSOA` ASC),
  CONSTRAINT `fk_PESSOA_JURIDICA_PESSOA1`
    FOREIGN KEY (`ID_PESSOA`)
    REFERENCES `fenix`.`PESSOA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`TABELA_PRECO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`TABELA_PRECO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PRINCIPAL` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Principal\",\"labelText\":\"Principal\",\"tooltip\":\"Principal\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Informar se esta tabela é a principal - nao pode ser deletada.\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}',
  `COEFICIENTE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Coeficiente\",\"labelText\":\"Coeficiente\",\"tooltip\":\"Coeficiente\",\"hintText\":\"Informe o Coeficiente\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"vai multiplicar o valor do item pelo valor do coeficiente para chegar ao preço da tabela\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Armazena as tabelas de preço da aplicação.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CLIENTE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CLIENTE` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_PESSOA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Pessoa\",\"labelText\":\"Pessoa\",\"tooltip\":\"Pessoa\",\"hintText\":\"Importe a Pessoa Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"pessoa\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `ID_TABELA_PRECO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tabela de Preços\",\"labelText\":\"Tabela de Preços\",\"tooltip\":\"Tabela de Preços\",\"hintText\":\"Importe a Tabela de Preços Vinculada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"TABELA_PRECO\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESDE` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Desde\",\"labelText\":\"É Cliente Desde\",\"tooltip\":\"Cliente Desde\",\"hintText\":\"É Cliente Desde\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `DATA_CADASTRO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Cadastro\",\"labelText\":\"Data de Cadastro\",\"tooltip\":\"Data de Cadastro\",\"hintText\":\"Informe a Data de Cadastro do Cliente\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `TAXA_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa de Desconto\",\"labelText\":\"Taxa de Desconto\",\"tooltip\":\"Taxa de Desconto\",\"hintText\":\"Taxa de Desconto Padrão Aplicada para o Cliente\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}\n',
  `LIMITE_CREDITO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Limite de Crédito\",\"labelText\":\"Limite de Crédito\",\"tooltip\":\"Limite de Crédito\",\"hintText\":\"Informe o Limite de Crédito Disponível para o Cliente\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `OBSERVACAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_CLIENTE_PESSOA_idx` (`ID_PESSOA` ASC),
  INDEX `fk_CLIENTE_TABELA_PRECO1_idx` (`ID_TABELA_PRECO` ASC),
  CONSTRAINT `fk_CLIENTE_PESSOA`
    FOREIGN KEY (`ID_PESSOA`)
    REFERENCES `fenix`.`PESSOA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_CLIENTE_TABELA_PRECO1`
    FOREIGN KEY (`ID_TABELA_PRECO`)
    REFERENCES `fenix`.`TABELA_PRECO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`FORNECEDOR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FORNECEDOR` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_PESSOA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Pessoa\",\"labelText\":\"Pessoa\",\"tooltip\":\"Pessoa\",\"hintText\":\"Importe a Pessoa Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"pessoa\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESDE` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Desde\",\"labelText\":\"É Fornecedor Desde\",\"tooltip\":\"Fornecedor Desde\",\"hintText\":\"É Fornecedor Desde\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `DATA_CADASTRO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Cadastro\",\"labelText\":\"Data de Cadastro\",\"tooltip\":\"Data de Cadastro\",\"hintText\":\"Informe a Data de Cadastro do Fornecedor\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `OBSERVACAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_FORNECEDOR_PESSOA1_idx` (`ID_PESSOA` ASC),
  CONSTRAINT `fk_FORNECEDOR_PESSOA1`
    FOREIGN KEY (`ID_PESSOA`)
    REFERENCES `fenix`.`PESSOA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`TRANSPORTADORA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`TRANSPORTADORA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_PESSOA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Pessoa\",\"labelText\":\"Pessoa\",\"tooltip\":\"Pessoa\",\"hintText\":\"Importe a Pessoa Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"pessoa\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_CADASTRO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Cadastro\",\"labelText\":\"Data de Cadastro\",\"tooltip\":\"Data de Cadastro\",\"hintText\":\"Informe a Data de Cadastro da Transportadora\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `OBSERVACAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_TRANSPORTADORA_PESSOA1_idx` (`ID_PESSOA` ASC),
  CONSTRAINT `fk_TRANSPORTADORA_PESSOA1`
    FOREIGN KEY (`ID_PESSOA`)
    REFERENCES `fenix`.`PESSOA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`CONTADOR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CONTADOR` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_PESSOA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Pessoa\",\"labelText\":\"Pessoa\",\"tooltip\":\"Pessoa\",\"hintText\":\"Importe a Pessoa Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"pessoa\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CRC_INSCRICAO` VARCHAR(15) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Inscrição CRC\",\"labelText\":\"Inscrição CRC\",\"tooltip\":\"Inscrição CRC\",\"hintText\":\"Informe a Inscrição no CRC\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CRC_UF` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF CRC\",\"labelText\":\"UF CRC\",\"tooltip\":\"UF CRC\",\"hintText\":\"Informe a UF do CRC\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_CONTADOR_PESSOA1_idx` (`ID_PESSOA` ASC),
  CONSTRAINT `fk_CONTADOR_PESSOA1`
    FOREIGN KEY (`ID_PESSOA`)
    REFERENCES `fenix`.`PESSOA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`CARGO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CARGO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome do Cargo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Cargo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `SALARIO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Salário\",\"labelText\":\"Valor Salário\",\"tooltip\":\"Valor Salário\",\"hintText\":\"Informe o Valor do Salário para o Cargo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `CBO_1994` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CBO 1994\",\"labelText\":\"CBO 1994\",\"tooltip\":\"CBO 1994\",\"hintText\":\"Informe o CBO 1994\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"cbo\",\"campoLookup\":\"codigo\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CBO_2002` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CBO 2002\",\"labelText\":\"CBO 2002\",\"tooltip\":\"CBO 2002\",\"hintText\":\"Informe o CBO 2002\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"cbo\",\"campoLookup\":\"codigo\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`SETOR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`SETOR` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome do Setor\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Setor\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`COLABORADOR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`COLABORADOR` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_PESSOA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Pessoa\",\"labelText\":\"Pessoa\",\"tooltip\":\"Pessoa\",\"hintText\":\"Importe a Pessoa Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"pessoa\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `ID_CARGO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cargo\",\"labelText\":\"Cargo\",\"tooltip\":\"Cargo\",\"hintText\":\"Importe o Cargo Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cargo\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `ID_SETOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Setor\",\"labelText\":\"Setor\",\"tooltip\":\"Setor\",\"hintText\":\"Importe o Setor Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"setor\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `MATRICULA` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Matrícula\",\"labelText\":\"Matrícula\",\"tooltip\":\"Matrícula\",\"hintText\":\"Informe a Matrícula do Colaborador\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DATA_CADASTRO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Cadastro\",\"labelText\":\"Data de Cadastro\",\"tooltip\":\"Data de Cadastro\",\"hintText\":\"Informe a Data de Cadastro\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `DATA_ADMISSAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Admissão\",\"labelText\":\"Data de Admissão\",\"tooltip\":\"Data de Admissão\",\"hintText\":\"Informe a Data de Admissão\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `DATA_DEMISSAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Demissão\",\"labelText\":\"Data de Demissão\",\"tooltip\":\"Data de Demissão\",\"hintText\":\"Informe a Data de Demissão\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `CTPS_NUMERO` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número CTPS\",\"labelText\":\"Número CTPS\",\"tooltip\":\"Número CTPS\",\"hintText\":\"Informe o Número da Carteira Profissional\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CTPS_SERIE` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Série CTPS\",\"labelText\":\"Série CTPS\",\"tooltip\":\"Série CTPS\",\"hintText\":\"Informe a Série da Carteira Profissional\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CTPS_DATA_EXPEDICAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Expedição\",\"labelText\":\"Data de Expedição\",\"tooltip\":\"Data de Expedição\",\"hintText\":\"Informe a Data de Expedição da CTPS\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `CTPS_UF` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF CTPS\",\"labelText\":\"UF CTPS\",\"tooltip\":\"UF CTPS\",\"hintText\":\"Selecione a UF da Carteira Profissional\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}\n',
  `OBSERVACAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_COLABORADOR_PESSOA1_idx` (`ID_PESSOA` ASC),
  INDEX `fk_COLABORADOR_CARGO1_idx` (`ID_CARGO` ASC),
  INDEX `fk_COLABORADOR_SETOR1_idx` (`ID_SETOR` ASC),
  CONSTRAINT `fk_COLABORADOR_PESSOA1`
    FOREIGN KEY (`ID_PESSOA`)
    REFERENCES `fenix`.`PESSOA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_COLABORADOR_CARGO1`
    FOREIGN KEY (`ID_CARGO`)
    REFERENCES `fenix`.`CARGO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_COLABORADOR_SETOR1`
    FOREIGN KEY (`ID_SETOR`)
    REFERENCES `fenix`.`SETOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`COMISSAO_PERFIL`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`COMISSAO_PERFIL` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Caso não queira utilizaros perfis de comissão, basta usar a tabela VENDA_COMISSAO, forma mais simplificada.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`VENDEDOR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`VENDEDOR` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_COLABORADOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_COMISSAO_PERFIL` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Perfil Comissão\",\"labelText\":\"Perfil Comissão\",\"tooltip\":\"Perfil Comissão\",\"hintText\":\"Importe o Perfil Comissão Vinculado\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"comissao_perfil\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `COMISSAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Comissão\",\"labelText\":\"Taxa Comissão\",\"tooltip\":\"Taxa Comissão\",\"hintText\":\"Informe a Taxa de Comissão do Vendedor\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}\n',
  `META_VENDA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Meta de Venda\",\"labelText\":\"Meta de Venda\",\"tooltip\":\"Meta de Venda\",\"hintText\":\"Informe o Valor da Meta de Vendas do Vendedor\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_VENDEDOR_COLABORADOR1_idx` (`ID_COLABORADOR` ASC),
  INDEX `fk_VENDEDOR_COMISSAO_PERFIL1_idx` (`ID_COMISSAO_PERFIL` ASC),
  CONSTRAINT `fk_VENDEDOR_COLABORADOR1`
    FOREIGN KEY (`ID_COLABORADOR`)
    REFERENCES `fenix`.`COLABORADOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_VENDEDOR_COMISSAO_PERFIL1`
    FOREIGN KEY (`ID_COMISSAO_PERFIL`)
    REFERENCES `fenix`.`COMISSAO_PERFIL` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`PESSOA_ENDERECO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`PESSOA_ENDERECO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_PESSOA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `LOGRADOURO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Logradouro\",\"labelText\":\"Logradouro\",\"tooltip\":\"Logradouro\",\"hintText\":\"Informe o Logradouro\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `NUMERO` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `BAIRRO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Bairro\",\"labelText\":\"Bairro\",\"tooltip\":\"Bairro\",\"hintText\":\"Informe o Bairro do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `MUNICIPIO_IBGE` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município IBGE\",\"labelText\":\"Município IBGE\",\"tooltip\":\"Código IBGE do Município\",\"hintText\":\"Informe o Código IBGE do Município\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `UF` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}\n',
  `CEP` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CEP\",\"labelText\":\"CEP\",\"tooltip\":\"CEP\",\"hintText\":\"Informe o CEP do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CEP\"}}\n',
  `CIDADE` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cidade\",\"labelText\":\"Cidade\",\"tooltip\":\"Cidade\",\"hintText\":\"Informe o Nome da Cidade do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `COMPLEMENTO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Complemento\",\"labelText\":\"Complemento\",\"tooltip\":\"Complemento\",\"hintText\":\"Informe o Complemento do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `PRINCIPAL` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"É Endereço Principal\",\"labelText\":\"É Endereço Principal\",\"tooltip\":\"É Endereço Principal\",\"hintText\":\"É Endereço Principal?\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}\n',
  `ENTREGA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"É Endereço de Entrega\",\"labelText\":\"É Endereço de Entrega\",\"tooltip\":\"É Endereço de Entrega\",\"hintText\":\"É Endereço de Entrega?\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}\n',
  `COBRANCA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"É Endereço de Cobrança\",\"labelText\":\"É Endereço de Cobrança\",\"tooltip\":\"É Endereço de Cobrança\",\"hintText\":\"É Endereço de Cobrança?\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}\n',
  `CORRESPONDENCIA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"É Endereço de Correspondência\",\"labelText\":\"É Endereço de Correspondência\",\"tooltip\":\"É Endereço de Correspondência\",\"hintText\":\"É Endereço de Correspondência?\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_PESSOA_ENDERECO_PESSOA1_idx` (`ID_PESSOA` ASC),
  CONSTRAINT `fk_PESSOA_ENDERECO_PESSOA1`
    FOREIGN KEY (`ID_PESSOA`)
    REFERENCES `fenix`.`PESSOA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`PESSOA_CONTATO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`PESSOA_CONTATO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_PESSOA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `NOME` VARCHAR(150) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome do Contato\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `EMAIL` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"E-Mail\",\"labelText\":\"E-Mail\",\"tooltip\":\"E-Mail\",\"hintText\":\"Informe o E-Mail do Contato\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"emailAddress\",\"mascara\":\"\"}}\n',
  `OBSERVACAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_PESSOA_CONTATO_PESSOA1_idx` (`ID_PESSOA` ASC),
  CONSTRAINT `fk_PESSOA_CONTATO_PESSOA1`
    FOREIGN KEY (`ID_PESSOA`)
    REFERENCES `fenix`.`PESSOA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`PESSOA_TELEFONE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`PESSOA_TELEFONE` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_PESSOA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `TIPO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo\",\"labelText\":\"Tipo\",\"tooltip\":\"Tipo\",\"hintText\":\"Informe o Tipo de Telefone\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"indice\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Residencial\"},{\"dropDownButtonItem\":\"Comercial\"},{\"dropDownButtonItem\":\"Celular\"},{\"dropDownButtonItem\":\"Outro\"}]}}\n',
  `NUMERO` VARCHAR(15) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número do Telefone\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TELEFONE\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_PESSOA_TELEFONE_PESSOA1_idx` (`ID_PESSOA` ASC),
  CONSTRAINT `fk_PESSOA_TELEFONE_PESSOA1`
    FOREIGN KEY (`ID_PESSOA`)
    REFERENCES `fenix`.`PESSOA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`PAPEL`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`PAPEL` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome do Papel\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Papel\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
COMMENT = 'Armazena os papéis (roles) da aplicação. Por exemplo: admnistrativo, financeiro, cobrança, estoque, etc.';


-- -----------------------------------------------------
-- Table `fenix`.`USUARIO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`USUARIO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_COLABORADOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}\n',
  `ID_PAPEL` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Papel\",\"labelText\":\"Papel\",\"tooltip\":\"Papel\",\"hintText\":\"Importe o Papel Vinculado\",\"validacao\":\"\",\"tabelaLookup\":\"papel\",\"campoLookup\":\"nome\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `LOGIN` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Login\",\"labelText\":\"Login\",\"tooltip\":\"Login\",\"hintText\":\"Informe o Login ou Nome de Usuário\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `SENHA` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Senha\",\"labelText\":\"Senha\",\"tooltip\":\"Senha\",\"hintText\":\"Informe a Senha do Usuário\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `ADMINISTRADOR` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"É Administrador\",\"labelText\":\"É Administrador\",\"tooltip\":\"É Administrador\",\"hintText\":\"É Administrador?\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}\n',
  `DATA_CADASTRO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Cadastro\",\"labelText\":\"Data de Cadastro\",\"tooltip\":\"Data de Cadastro\",\"hintText\":\"Informe a Data de Cadastro\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_USUARIO_COLABORADOR1_idx` (`ID_COLABORADOR` ASC),
  INDEX `fk_USUARIO_PAPEL1_idx` (`ID_PAPEL` ASC),
  CONSTRAINT `fk_USUARIO_COLABORADOR1`
    FOREIGN KEY (`ID_COLABORADOR`)
    REFERENCES `fenix`.`COLABORADOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_USUARIO_PAPEL1`
    FOREIGN KEY (`ID_PAPEL`)
    REFERENCES `fenix`.`PAPEL` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`FUNCAO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FUNCAO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome da Função\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição da Função\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
COMMENT = 'Armazena as funções da aplicação. Pode ser uma tela. Exemplos: Cadastro do Cliente. Pode ser um botão específico. Exemplo: Calcular Fluxo de Caixa.';


-- -----------------------------------------------------
-- Table `fenix`.`PAPEL_FUNCAO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`PAPEL_FUNCAO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_PAPEL` INT NOT NULL,
  `ID_FUNCAO` INT NOT NULL,
  `HABILITADO` CHAR(1) NULL COMMENT 'Valores possíveis: S=Sim | N=Não',
  `PODE_INSERIR` CHAR(1) NULL COMMENT 'Valores possíveis: S=Sim | N=Não',
  `PODE_ALTERAR` CHAR(1) NULL COMMENT 'Valores possíveis: S=Sim | N=Não',
  `PODE_EXCLUIR` CHAR(1) NULL COMMENT 'Valores possíveis: S=Sim | N=Não',
  PRIMARY KEY (`ID`),
  INDEX `fk_PAPEL_FUNCAO_PAPEL1_idx` (`ID_PAPEL` ASC),
  INDEX `fk_PAPEL_FUNCAO_FUNCAO1_idx` (`ID_FUNCAO` ASC),
  CONSTRAINT `fk_PAPEL_FUNCAO_PAPEL1`
    FOREIGN KEY (`ID_PAPEL`)
    REFERENCES `fenix`.`PAPEL` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PAPEL_FUNCAO_FUNCAO1`
    FOREIGN KEY (`ID_FUNCAO`)
    REFERENCES `fenix`.`FUNCAO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`EMPRESA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`EMPRESA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `RAZAO_SOCIAL` VARCHAR(150) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Razão Social\",\"labelText\":\"Razão Social\",\"tooltip\":\"Razão Social\",\"hintText\":\"Informe a Razão Social da Empresa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `NOME_FANTASIA` VARCHAR(150) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome Fantasia\",\"labelText\":\"Nome Fantasia\",\"tooltip\":\"Nome Fantasia\",\"hintText\":\"Informe o Nome de Fantasia da Empresa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CNPJ` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ\",\"labelText\":\"CNPJ\",\"tooltip\":\"CNPJ\",\"hintText\":\"Informe o CNPJ da Empresa\",\"validacao\":\"CNPJ\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}\n',
  `INSCRICAO_ESTADUAL` VARCHAR(45) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Inscrição Estadual\",\"labelText\":\"Inscrição Estadual\",\"tooltip\":\"Inscrição Estadual\",\"hintText\":\"Informe a Inscrição Estadual da Empresa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `INSCRICAO_MUNICIPAL` VARCHAR(45) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Inscrição Municipal\",\"labelText\":\"Inscrição Municipal\",\"tooltip\":\"Inscrição Municipal\",\"hintText\":\"Informe a Inscrição Municipal da Empresa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `TIPO_REGIME` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Regime\",\"labelText\":\"Tipo Regime\",\"tooltip\":\"Tipo Regime\",\"hintText\":\"Informe o Tipo de Regime da Empresa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"1-Lucro REAL\"},{\"dropDownButtonItem\":\"2-Lucro presumido\"},{\"dropDownButtonItem\":\"3-Simples nacional\"}]}}',
  `CRT` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CRT\",\"labelText\":\"CRT\",\"tooltip\":\"CRT\",\"hintText\":\"Informe o Código de Regime Tributário\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"1-Simples Nacional\"},{\"dropDownButtonItem\":\"2-Simples Nacional - excesso de sublimite da receita bruta\"},{\"dropDownButtonItem\":\"3-Regime Normal\"}]}}',
  `EMAIL` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"E-Mail\",\"labelText\":\"E-Mail\",\"tooltip\":\"E-Mail\",\"hintText\":\"Informe o e-mail da Empresa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"emailAddress\",\"mascara\":\"\"}}',
  `SITE` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Site\",\"labelText\":\"Site\",\"tooltip\":\"Site\",\"hintText\":\"Informe o site da Empresa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"url\",\"mascara\":\"\"}}',
  `CONTATO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome Contato\",\"labelText\":\"Nome Contato\",\"tooltip\":\"Nome Contato\",\"hintText\":\"Informe o Nome do Contato da Empresa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DATA_CONSTITUICAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Constituição\",\"labelText\":\"Data Constituição\",\"tooltip\":\"Data Constituição\",\"hintText\":\"Informe a Data de Constituição da Empresa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `TIPO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo\",\"labelText\":\"Tipo\",\"tooltip\":\"Tipo\",\"hintText\":\"Selecione o Tipo: Matriz ou Filial\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Matriz\"},{\"dropDownButtonItem\":\"Filial\"}]}}\n',
  `INSCRICAO_JUNTA_COMERCIAL` VARCHAR(30) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Inscrição Junta Comercial\",\"labelText\":\"Inscrição Junta Comercial\",\"tooltip\":\"Inscrição Junta Comercial\",\"hintText\":\"Informa a Inscrição na Junta Comercial\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DATA_INSC_JUNTA_COMERCIAL` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Inscrição Junta Comercial\",\"labelText\":\"Data Inscrição Junta Comercial\",\"tooltip\":\"Data Inscrição Junta Comercial\",\"hintText\":\"Informa a Data de Inscrição na Junta Comercial\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `CODIGO_IBGE_CIDADE` INT NULL DEFAULT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código IBGE Cidade\",\"labelText\":\"Código IBGE Cidade\",\"tooltip\":\"Código IBGE Cidade\",\"hintText\":\"Informe o Código IBGE da Cidade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `CODIGO_IBGE_UF` INT NULL DEFAULT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código IBGE UF\",\"labelText\":\"Código IBGE UF\",\"tooltip\":\"Código IBGE UF\",\"hintText\":\"Informe o Código IBGE da UF\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `CEI` VARCHAR(12) NULL DEFAULT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código CEI\",\"labelText\":\"Código CEI\",\"tooltip\":\"Código CEI\",\"hintText\":\"Informe o CEI (Cadastro Específico do INSS)\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CODIGO_CNAE_PRINCIPAL` VARCHAR(7) NULL DEFAULT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNAE Principal\",\"labelText\":\"CNAE Principal\",\"tooltip\":\"CNAE Principal\",\"hintText\":\"Informe o CNAE Principal\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`PRODUTO_GRUPO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`PRODUTO_GRUPO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome do Grupo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Grupo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`PRODUTO_SUBGRUPO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`PRODUTO_SUBGRUPO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_PRODUTO_GRUPO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Grupo\",\"labelText\":\"Grupo\",\"tooltip\":\"Grupo\",\"hintText\":\"Importe o Grupo de Produto Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"produto_grupo\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome do Subgrupo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Subgrupo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_PRODUTO_SUBGRUPO_PRODUTO_GRUPO1_idx` (`ID_PRODUTO_GRUPO` ASC),
  CONSTRAINT `fk_PRODUTO_SUBGRUPO_PRODUTO_GRUPO1`
    FOREIGN KEY (`ID_PRODUTO_GRUPO`)
    REFERENCES `fenix`.`PRODUTO_GRUPO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`PRODUTO_MARCA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`PRODUTO_MARCA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome da Marca\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição da Marca\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`PRODUTO_UNIDADE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`PRODUTO_UNIDADE` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `SIGLA` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Sigla\",\"labelText\":\"Sigla\",\"tooltip\":\"Sigla\",\"hintText\":\"Informe a Sigla da Unidade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição da Unidade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `PODE_FRACIONAR` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Pode Fracionar\",\"labelText\":\"Pode Fracionar\",\"tooltip\":\"Pode Fracionar\",\"hintText\":\"Pode Fracionar?\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`TRIBUT_ICMS_CUSTOM_CAB`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`TRIBUT_ICMS_CUSTOM_CAB` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `DESCRICAO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do ICMS Customizado\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ORIGEM_MERCADORIA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Origem da Mercadoria\",\"labelText\":\"Origem da Mercadoria\",\"tooltip\":\"Origem da Mercadoria\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Campo \'orig\' da NF-e\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"0-Nacional\"},{\"dropDownButtonItem\":\"1-Estrangeira - Importação direta\"},{\"dropDownButtonItem\":\"2-Estrangeira - Adquirida no mercado interno\"}]}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Caso a configuração do ICMS precise ser \'customizada\' para determinado produto, utiliza-se essa tabela e sua \'filha\'.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`TRIBUT_GRUPO_TRIBUTARIO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`TRIBUT_GRUPO_TRIBUTARIO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `DESCRICAO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Grupo Tributário\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ORIGEM_MERCADORIA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Origem da Mercadoria\",\"labelText\":\"Origem da Mercadoria\",\"tooltip\":\"Origem da Mercadoria\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Campo \'orig\' da NF-e\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"0-Nacional\"},{\"dropDownButtonItem\":\"1-Estrangeira - Importação direta\"},{\"dropDownButtonItem\":\"2-Estrangeira - Adquirida no mercado interno\"}]}}\n',
  `OBSERVACAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Esta tabela irá armazenar os Perfis tributários diferentes existentes dentro do mix de produtos da empresa. Ou seja, os diferentes conjuntos de produtos cuja tributação sofrem variações. Definindo o GRUPO TRIBUTÁRIO (independente da classificação de grupo gerencial empregada pelo cliente). Isto é, produtos com tributações idênticas ficariam todos em um grupo independentemente suas características intrínsecas.\n\nExemplos:\n1. Produtos de fabricação própria (sujeitos ao ICMS ST)\n2. Produtos de Revenda (sujeitos ao Regime do ICMS ST)\n3. Produtos de Revenda Não sujeitos ao ICMS ST\n4. Produtos com Suspensao de IPI\n5. Produtos com Suspensao de PIS, COFINS\n6. etc.\n'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`PRODUTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`PRODUTO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_PRODUTO_SUBGRUPO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Subgrupo\",\"tooltip\":\"Subgrupo\",\"labelText\":\"Subgrupo\",\"hintText\":\"Importe o Subgrupo de Produto Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"produto_subgrupo\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_PRODUTO_MARCA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Marca\",\"tooltip\":\"Marca\",\"labelText\":\"Marca\",\"hintText\":\"Importe a Marca Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"produto_marca\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_PRODUTO_UNIDADE` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Unidade\",\"tooltip\":\"Unidade\",\"labelText\":\"Unidade\",\"hintText\":\"Importe a Unidade Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"produto_unidade\",\"campoLookup\":\"sigla\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_TRIBUT_ICMS_CUSTOM_CAB` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"ICMS Customizado\",\"tooltip\":\"ICMS Customizado\",\"labelText\":\"ICMS Customizado\",\"hintText\":\"Importe o ICMS Customizado Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"tribut_icms_custom_cab\",\"campoLookup\":\"descricao\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `ID_TRIBUT_GRUPO_TRIBUTARIO` INT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Grupo Tributário\",\"tooltip\":\"Grupo Tributário\",\"labelText\":\"Grupo Tributário\",\"hintText\":\"Importe o Grupo Tributário Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"tribut_grupo_tributario\",\"campoLookup\":\"descricao\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome do Produto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Produto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `GTIN` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"GTIN\",\"labelText\":\"GTIN\",\"tooltip\":\"GTIN\",\"hintText\":\"Informe o GTIN do Produto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CODIGO_INTERNO` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Interno\",\"labelText\":\"Código Interno\",\"tooltip\":\"Código Interno\",\"hintText\":\"Informe o Código Interno do Produto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `VALOR_COMPRA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Compra\",\"labelText\":\"Valor Compra\",\"tooltip\":\"Valor Compra\",\"hintText\":\"Informe o Valor da Compra do Produto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_VENDA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Venda\",\"labelText\":\"Valor Venda\",\"tooltip\":\"Valor Venda\",\"hintText\":\"Informe o Valor da Venda do Produto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `CODIGO_NCM` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código NCM\",\"labelText\":\"Código NCM\",\"tooltip\":\"Código NCM\",\"hintText\":\"Importe o Código NCM\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"ncm\",\"campoLookup\":\"codigo\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ESTOQUE_MINIMO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Estoque Mínimo\",\"labelText\":\"Estoque Mínimo\",\"tooltip\":\"Estoque Mínimo\",\"hintText\":\"Informe a Quantidade de Estoque Mínimo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}\n',
  `ESTOQUE_MAXIMO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Estoque Máximo\",\"labelText\":\"Estoque Máximo\",\"tooltip\":\"Estoque Máximo\",\"hintText\":\"Informe a Quantidade de Estoque Máximo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}\n',
  `QUANTIDADE_ESTOQUE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade em Estoque\",\"labelText\":\"Quantidade em Estoque\",\"tooltip\":\"Quantidade em Estoque\",\"hintText\":\"Informe a Quantidade em Estoque\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}\n',
  `DATA_CADASTRO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Cadastro\",\"labelText\":\"Data de Cadastro\",\"tooltip\":\"Data de Cadastro\",\"hintText\":\"Informe a Data de Cadastro\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_PRODUTO_PRODUTO_SUBGRUPO1_idx` (`ID_PRODUTO_SUBGRUPO` ASC),
  INDEX `fk_PRODUTO_PRODUTO_MARCA1_idx` (`ID_PRODUTO_MARCA` ASC),
  INDEX `fk_PRODUTO_PRODUTO_UNIDADE1_idx` (`ID_PRODUTO_UNIDADE` ASC),
  INDEX `fk_PRODUTO_TRIBUT_ICMS_CUSTOM_CAB1_idx` (`ID_TRIBUT_ICMS_CUSTOM_CAB` ASC),
  INDEX `fk_PRODUTO_TRIBUT_GRUPO_TRIBUTARIO1_idx` (`ID_TRIBUT_GRUPO_TRIBUTARIO` ASC),
  CONSTRAINT `fk_PRODUTO_PRODUTO_SUBGRUPO1`
    FOREIGN KEY (`ID_PRODUTO_SUBGRUPO`)
    REFERENCES `fenix`.`PRODUTO_SUBGRUPO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PRODUTO_PRODUTO_MARCA1`
    FOREIGN KEY (`ID_PRODUTO_MARCA`)
    REFERENCES `fenix`.`PRODUTO_MARCA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PRODUTO_PRODUTO_UNIDADE1`
    FOREIGN KEY (`ID_PRODUTO_UNIDADE`)
    REFERENCES `fenix`.`PRODUTO_UNIDADE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PRODUTO_TRIBUT_ICMS_CUSTOM_CAB1`
    FOREIGN KEY (`ID_TRIBUT_ICMS_CUSTOM_CAB`)
    REFERENCES `fenix`.`TRIBUT_ICMS_CUSTOM_CAB` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PRODUTO_TRIBUT_GRUPO_TRIBUTARIO1`
    FOREIGN KEY (`ID_TRIBUT_GRUPO_TRIBUTARIO`)
    REFERENCES `fenix`.`TRIBUT_GRUPO_TRIBUTARIO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`BANCO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`BANCO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `CODIGO` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código do Banco\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome do Banco\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `URL` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"URL\",\"labelText\":\"URL\",\"tooltip\":\"URL\",\"hintText\":\"Informe a URL do Banco\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"url\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`BANCO_AGENCIA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`BANCO_AGENCIA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_BANCO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Banco\",\"labelText\":\"Banco\",\"tooltip\":\"Banco\",\"hintText\":\"Importe o Banco Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"banco\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número da Agência\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DIGITO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Dígito\",\"labelText\":\"Dígito\",\"tooltip\":\"Dígito\",\"hintText\":\"Informe o Dígito do Número da Agência\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome da Agência\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `TELEFONE` VARCHAR(15) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Telefone\",\"labelText\":\"Telefone\",\"tooltip\":\"Telefone\",\"hintText\":\"Informe o Número de Telefone da Agência\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TELEFONE\"}}\n',
  `CONTATO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Contato\",\"labelText\":\"Contato\",\"tooltip\":\"Contato\",\"hintText\":\"Informe o Nome do Contato da Agência\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `OBSERVACAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `GERENTE` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Gerente\",\"labelText\":\"Gerente\",\"tooltip\":\"Gerente\",\"hintText\":\"Informe o Nome do Gerente da Agência\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_BANCO_AGENCIA_BANCO1_idx` (`ID_BANCO` ASC),
  CONSTRAINT `fk_BANCO_AGENCIA_BANCO1`
    FOREIGN KEY (`ID_BANCO`)
    REFERENCES `fenix`.`BANCO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`BANCO_CONTA_CAIXA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`BANCO_CONTA_CAIXA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_BANCO_AGENCIA` INT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Agência\",\"labelText\":\"Agência\",\"tooltip\":\"Agência\",\"hintText\":\"Importe o Agência Vinculado\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"banco_agencia\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número da Conta\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DIGITO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Dígito\",\"labelText\":\"Dígito\",\"tooltip\":\"Dígito\",\"hintText\":\"Informe o Dígito da Conta\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome da Conta\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `TIPO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo\",\"labelText\":\"Tipo\",\"tooltip\":\"Tipo\",\"hintText\":\"Selecione o Tipo da Conta\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"indice\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Corrente\"},{\"dropDownButtonItem\":\"Poupança\"},{\"dropDownButtonItem\":\"Investimento\"},{\"dropDownButtonItem\":\"Caixa Interno\"}]}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição da Conta/Caixa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_BANCO_CONTA_CAIXA_BANCO_AGENCIA1_idx` (`ID_BANCO_AGENCIA` ASC),
  CONSTRAINT `fk_BANCO_CONTA_CAIXA_BANCO_AGENCIA1`
    FOREIGN KEY (`ID_BANCO_AGENCIA`)
    REFERENCES `fenix`.`BANCO_AGENCIA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`CEP`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CEP` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NUMERO` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número CEP\",\"labelText\":\"Número CEP\",\"tooltip\":\"Número CEP\",\"hintText\":\"Informe o Número do CEP\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CEP\"}}\n',
  `LOGRADOURO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Logradouro\",\"labelText\":\"Logradouro\",\"tooltip\":\"Logradouro\",\"hintText\":\"Informe o Logradouro\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `COMPLEMENTO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Complemento\",\"labelText\":\"Complemento\",\"tooltip\":\"Complemento\",\"hintText\":\"Informe o Complemento do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `BAIRRO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Bairro\",\"labelText\":\"Bairro\",\"tooltip\":\"Bairro\",\"hintText\":\"Informe o Bairro do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MUNICIPIO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município\",\"labelText\":\"Município\",\"tooltip\":\"Município\",\"hintText\":\"Informe o Nome do Município do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `UF` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}',
  `CODIGO_IBGE_MUNICIPIO` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município IBGE\",\"labelText\":\"Município IBGE\",\"tooltip\":\"Código IBGE do Município\",\"hintText\":\"Informe o Código IBGE do Município\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`UF`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`UF` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `SIGLA` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Sigla\",\"labelText\":\"Sigla\",\"tooltip\":\"Sigla\",\"hintText\":\"Informe a Sigla da UF\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome da UF\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CODIGO_IBGE` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código IBGE UF\",\"labelText\":\"Código IBGE UF\",\"tooltip\":\"Código IBGE UF\",\"hintText\":\"Informe o Código IBGE da UF\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`MUNICIPIO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`MUNICIPIO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_UF` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Importe a UF Vinculada\",\"validacao\":\"\",\"tabelaLookup\":\"uf\",\"campoLookup\":\"sigla\",\"obrigatorio\":true,\"valorPadraoLookup\":\"%\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome do Município\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CODIGO_IBGE` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município IBGE\",\"labelText\":\"Município IBGE\",\"tooltip\":\"Código IBGE do Município\",\"hintText\":\"Informe o Código IBGE do Município\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `CODIGO_RECEITA_FEDERAL` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Receita Federal\",\"labelText\":\"Código Receita Federal\",\"tooltip\":\"Código Receita Federal\",\"hintText\":\"Informe o Código\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `CODIGO_ESTADUAL` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Estadual\",\"labelText\":\"Código Estadual\",\"tooltip\":\"Código Estadual\",\"hintText\":\"Informe o Código\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_MUNICIPIO_UF1_idx` (`ID_UF` ASC),
  CONSTRAINT `fk_MUNICIPIO_UF1`
    FOREIGN KEY (`ID_UF`)
    REFERENCES `fenix`.`UF` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`NCM`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NCM` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `CODIGO` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(1000) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do NCM\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `OBSERVACAO` VARCHAR(1000) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`CFOP`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CFOP` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `CODIGO` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(1000) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do CFOP\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `APLICACAO` VARCHAR(1000) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Aplicação\",\"labelText\":\"Aplicação\",\"tooltip\":\"Aplicação\",\"hintText\":\"Informe a Aplicação do CFOP\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`CST_ICMS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CST_ICMS` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `OBSERVACAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`CST_IPI`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CST_IPI` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `OBSERVACAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`CST_COFINS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CST_COFINS` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `OBSERVACAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`CST_PIS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CST_PIS` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `OBSERVACAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`CSOSN`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CSOSN` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `OBSERVACAO` VARCHAR(1000) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`EMPRESA_ENDERECO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`EMPRESA_ENDERECO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_EMPRESA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `LOGRADOURO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Logradouro\",\"labelText\":\"Logradouro\",\"tooltip\":\"Logradouro\",\"hintText\":\"Informe o Logradouro\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `BAIRRO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Bairro\",\"labelText\":\"Bairro\",\"tooltip\":\"Bairro\",\"hintText\":\"Informe o Bairro do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CIDADE` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cidade\",\"labelText\":\"Cidade\",\"tooltip\":\"Cidade\",\"hintText\":\"Informe o Nome da Cidade do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `UF` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}',
  `CEP` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CEP\",\"labelText\":\"CEP\",\"tooltip\":\"CEP\",\"hintText\":\"Informe o CEP do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CEP\"}}',
  `MUNICIPIO_IBGE` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município IBGE\",\"labelText\":\"Município IBGE\",\"tooltip\":\"Código IBGE do Município\",\"hintText\":\"Informe o Código IBGE do Município\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `COMPLEMENTO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Complemento\",\"labelText\":\"Complemento\",\"tooltip\":\"Complemento\",\"hintText\":\"Informe o Complemento do Endereço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PRINCIPAL` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"É Endereço Principal\",\"labelText\":\"É Endereço Principal\",\"tooltip\":\"É Endereço Principal\",\"hintText\":\"É Endereço Principal?\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}',
  `ENTREGA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"É Endereço de Entrega\",\"labelText\":\"É Endereço de Entrega\",\"tooltip\":\"É Endereço de Entrega\",\"hintText\":\"É Endereço de Entrega?\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}',
  `COBRANCA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"É Endereço de Cobrança\",\"labelText\":\"É Endereço de Cobrança\",\"tooltip\":\"É Endereço de Cobrança\",\"hintText\":\"É Endereço de Cobrança?\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}',
  `CORRESPONDENCIA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"É Endereço de Correspondência\",\"labelText\":\"É Endereço de Correspondência\",\"tooltip\":\"É Endereço de Correspondência\",\"hintText\":\"É Endereço de Correspondência?\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_EMPRESA_ENDERECO_EMPRESA1_idx` (`ID_EMPRESA` ASC),
  CONSTRAINT `fk_EMPRESA_ENDERECO_EMPRESA1`
    FOREIGN KEY (`ID_EMPRESA`)
    REFERENCES `fenix`.`EMPRESA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`EMPRESA_CONTATO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`EMPRESA_CONTATO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_EMPRESA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `NOME` VARCHAR(150) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome do Contato\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `EMAIL` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"E-Mail\",\"labelText\":\"E-Mail\",\"tooltip\":\"E-Mail\",\"hintText\":\"Informe o E-Mail do Contato\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"emailAddress\",\"mascara\":\"\"}}',
  `OBSERVACAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_EMPRESA_CONTATO_EMPRESA1_idx` (`ID_EMPRESA` ASC),
  CONSTRAINT `fk_EMPRESA_CONTATO_EMPRESA1`
    FOREIGN KEY (`ID_EMPRESA`)
    REFERENCES `fenix`.`EMPRESA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`EMPRESA_TELEFONE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`EMPRESA_TELEFONE` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_EMPRESA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `TIPO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo\",\"labelText\":\"Tipoe\",\"tooltip\":\"Tipo\",\"hintText\":\"Informe o Tipo de Telefone\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"indice\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Residencial\"},{\"dropDownButtonItem\":\"Comercial\"},{\"dropDownButtonItem\":\"Celular\"},{\"dropDownButtonItem\":\"Outro\"}]}}',
  `NUMERO` VARCHAR(15) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número do Telefone\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TELEFONE\"}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_EMPRESA_TELEFONE_EMPRESA1_idx` (`ID_EMPRESA` ASC),
  CONSTRAINT `fk_EMPRESA_TELEFONE_EMPRESA1`
    FOREIGN KEY (`ID_EMPRESA`)
    REFERENCES `fenix`.`EMPRESA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`CNAE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CNAE` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `CODIGO` VARCHAR(7) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DENOMINACAO` VARCHAR(1000) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Denominação\",\"labelText\":\"Denominação\",\"tooltip\":\"Denominação\",\"hintText\":\"Informe a Denominação\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`TALONARIO_CHEQUE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`TALONARIO_CHEQUE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_BANCO_CONTA_CAIXA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Conta/Caixa\",\"labelText\":\"Conta/Caixa\",\"tooltip\":\"Conta/Caixa\",\"hintText\":\"Importe a Conta/Caixa Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"banco_conta_caixa\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `TALAO` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código do Talão\",\"labelText\":\"Código do Talão\",\"tooltip\":\"Código do Talão\",\"hintText\":\"Informe o Código do Talão\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `NUMERO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número do Talão\",\"labelText\":\"Número do Talão\",\"tooltip\":\"Número do Talão\",\"hintText\":\"Informe o Número do Talão\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `STATUS_TALAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Status do Talão\",\"labelText\":\"Status do Talão\",\"tooltip\":\"Status do Talão\",\"hintText\":\"Informe o Status do Talão\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Normal\"},{\"dropDownButtonItem\":\"Cancelado\"},{\"dropDownButtonItem\":\"Extraviado\"},{\"dropDownButtonItem\":\"Utilizado\"}]}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_TALONARIO_CHEQUE_BANCO_CONTA_CAIXA1_idx` (`ID_BANCO_CONTA_CAIXA` ASC),
  CONSTRAINT `fk_TALONARIO_CHEQUE_BANCO_CONTA_CAIXA1`
    FOREIGN KEY (`ID_BANCO_CONTA_CAIXA`)
    REFERENCES `fenix`.`BANCO_CONTA_CAIXA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela que armazena os talonario de cheque de determinada empresa.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CHEQUE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CHEQUE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_TALONARIO_CHEQUE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `NUMERO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número do Cheque\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `STATUS_CHEQUE` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Status\",\"labelText\":\"Status\",\"tooltip\":\"Status\",\"hintText\":\"Selecione o Status do Cheque\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"indice\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Em Ser\"},{\"dropDownButtonItem\":\"Baixado\"},{\"dropDownButtonItem\":\"Utilizado\"},{\"dropDownButtonItem\":\"Compensado\"},{\"dropDownButtonItem\":\"Cancelado\"}]}}\n',
  `DATA_STATUS` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data do Status\",\"labelText\":\"Data do Status\",\"tooltip\":\"Data do Status\",\"hintText\":\"Informe a Data do Status\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `FK_TALONARIO_CHEQUE` (`ID_TALONARIO_CHEQUE` ASC),
  CONSTRAINT `fk_{E7B18D4F-5CF4-4A5D-AC05-B57A05559CA6}`
    FOREIGN KEY (`ID_TALONARIO_CHEQUE`)
    REFERENCES `fenix`.`TALONARIO_CHEQUE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela com a relação dos cheques vinculados a determinado talão.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`FIN_FECHAMENTO_CAIXA_BANCO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FIN_FECHAMENTO_CAIXA_BANCO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_BANCO_CONTA_CAIXA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Conta/Caixa\",\"labelText\":\"Conta/Caixa\",\"tooltip\":\"Conta/Caixa\",\"hintText\":\"Importe a Conta/Caixa Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"banco_conta_caixa\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_FECHAMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data do Fechamento\",\"labelText\":\"Data do Fechamento\",\"tooltip\":\"Data do Fechamento\",\"hintText\":\"Informe a Data do Fechamento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `MES_ANO` VARCHAR(7) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Mês/Ano\",\"labelText\":\"Mês/Ano\",\"tooltip\":\"Mês/Ano\",\"hintText\":\"Informe o Mês/Ano\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"comentario\":\"Formato MM/AAAA para facilitar algumas consultas\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"MES_ANO\"}}',
  `MES` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Mês\",\"labelText\":\"Mês\",\"tooltip\":\"Mês\",\"hintText\":\"Informe o Mês\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"MES\"}}\n',
  `ANO` CHAR(4) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Ano\",\"labelText\":\"Ano\",\"tooltip\":\"Ano\",\"hintText\":\"Informe o Ano\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"ANO\"}}\n',
  `SALDO_ANTERIOR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Saldo Anterior\",\"labelText\":\"Valor Saldo Anterior\",\"tooltip\":\"Valor Saldo Anterior\",\"hintText\":\"Informe o Valor do Saldo Anterior\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `RECEBIMENTOS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Total Recebimentos\",\"labelText\":\"Total Recebimentos\",\"tooltip\":\"Total Recebimentos\",\"hintText\":\"Informe o Valor Total de Recebimentos\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `PAGAMENTOS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Total Pagamentos\",\"labelText\":\"Total Pagamentos\",\"tooltip\":\"Total Pagamentos\",\"hintText\":\"Informe o Valor Total de Pagamentos\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `SALDO_CONTA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Saldo em Conta\",\"labelText\":\"Saldo em Conta\",\"tooltip\":\"Saldo em Conta\",\"hintText\":\"Informe o Valor do Saldo em Conta\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `CHEQUE_NAO_COMPENSADO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cheques Não Compensados\",\"labelText\":\"Cheques Não Compensados\",\"tooltip\":\"Cheques Não Compensados\",\"hintText\":\"Informe o Valor Total de Cheques Não Compensados\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `SALDO_DISPONIVEL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Saldo Disponível\",\"labelText\":\"Saldo Disponível\",\"tooltip\":\"Saldo Disponível\",\"hintText\":\"Informe o Valo do Saldo Disponível\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_FIN_FECHAMENTO_CAIXA_BANCO_BANCO_CONTA_CAIXA1_idx` (`ID_BANCO_CONTA_CAIXA` ASC),
  CONSTRAINT `fk_FIN_FECHAMENTO_CAIXA_BANCO_BANCO_CONTA_CAIXA1`
    FOREIGN KEY (`ID_BANCO_CONTA_CAIXA`)
    REFERENCES `fenix`.`BANCO_CONTA_CAIXA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena os dados de um fechamento mensal.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`FIN_EXTRATO_CONTA_BANCO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FIN_EXTRATO_CONTA_BANCO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_BANCO_CONTA_CAIXA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Conta/Caixa\",\"labelText\":\"Conta/Caixa\",\"tooltip\":\"Conta/Caixa\",\"hintText\":\"Importe a Conta/Caixa Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"banco_conta_caixa\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MES_ANO` VARCHAR(7) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Mês/Ano\",\"labelText\":\"Mês/Ano\",\"tooltip\":\"Mês/Ano\",\"hintText\":\"Informe o Mês/Ano\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"MES_ANO\"}}\n',
  `MES` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Mês\",\"labelText\":\"Mês\",\"tooltip\":\"Mês\",\"hintText\":\"Informe o Mês\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"MES\"}}\n',
  `ANO` CHAR(4) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Ano\",\"labelText\":\"Ano\",\"tooltip\":\"Ano\",\"hintText\":\"Informe o Ano\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"ANO\"}}\n',
  `DATA_MOVIMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Movimento\",\"labelText\":\"Data de Movimento\",\"tooltip\":\"Data de Movimento\",\"hintText\":\"Informe a Data de Movimento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `DATA_BALANCETE` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data do Balancete\",\"labelText\":\"Data do Balancete\",\"tooltip\":\"Data do Balancete\",\"hintText\":\"Informe a Data do Balancete\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `HISTORICO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Histórico\",\"labelText\":\"Histórico\",\"tooltip\":\"Histórico\",\"hintText\":\"Informe o Histórico\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DOCUMENTO` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Documento\",\"labelText\":\"Documento\",\"tooltip\":\"Documento\",\"hintText\":\"Informe o Documento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `VALOR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor\",\"labelText\":\"Valor\",\"tooltip\":\"Valor\",\"hintText\":\"Informe o Valor\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `CONCILIADO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Conciliado\",\"labelText\":\"Conciliado\",\"tooltip\":\"Conciliado\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}\n',
  `OBSERVACAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_FIN_EXTRATO_CONTA_BANCO_BANCO_CONTA_CAIXA1_idx` (`ID_BANCO_CONTA_CAIXA` ASC),
  CONSTRAINT `fk_FIN_EXTRATO_CONTA_BANCO_BANCO_CONTA_CAIXA1`
    FOREIGN KEY (`ID_BANCO_CONTA_CAIXA`)
    REFERENCES `fenix`.`BANCO_CONTA_CAIXA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena os dados do extrato bancário.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`FIN_DOCUMENTO_ORIGEM`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FIN_DOCUMENTO_ORIGEM` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código do Documento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `SIGLA` CHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Sigla\",\"labelText\":\"Sigla\",\"tooltip\":\"Sigla\",\"hintText\":\"Informe a Sigla do Documento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Exemplos: NF, CHQ, NFe, DP, NP, CTe, CT, CF, CFe\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DESCRICAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Documento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
COMMENT = 'Tabela para cadastro dos tipo de documentos que podem gerar contas a pagar ou receber: nota fiscal, boleto, recibo, etc.\nO campo SIGLA pode receber valores tais como: NF, CHQ, NFe, DP, NP, CTe, CT, CF, CFe.\nO campo DESCRICAO pode receber valores tais como: NOTA FISCAL | BOLETO | RECIBO | ETC.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`FIN_NATUREZA_FINANCEIRA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FIN_NATUREZA_FINANCEIRA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(4) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código da Natureza Financeira\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição da Natureza Financeira\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `TIPO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo\",\"labelText\":\"Tipo\",\"tooltip\":\"Tipo\",\"hintText\":\"Informe o Tipo: Receita ou Despesa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Receita\"},{\"dropDownButtonItem\":\"Despesa\"}]}}\n',
  `APLICACAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Aplicação\",\"labelText\":\"Aplicação\",\"tooltip\":\"Aplicação\",\"hintText\":\"Informe a que se destina a natureza financeira\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
COMMENT = 'Relação de contas de natureza financeira. \n \nA natureza financeira armazena as contas mais perto do dia-a-dia dos usuarios. \n\nPara contas de Receita deve-se utilizar o padrão 100N (1001, 1002, 1003, etc) e para contas de despesa o padrão 200N (2001, 2002, 2003, etc)'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`FIN_LANCAMENTO_PAGAR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FIN_LANCAMENTO_PAGAR` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_FIN_DOCUMENTO_ORIGEM` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Documento Origem\",\"labelText\":\"Documento Origem\",\"tooltip\":\"Documento Origem\",\"hintText\":\"Importe o Documento de Origem Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"fin_documento_origem\",\"campoLookup\":\"sigla\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_FIN_NATUREZA_FINANCEIRA` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Natureza Financeira\",\"labelText\":\"Natureza Financeira\",\"tooltip\":\"Natureza Financeira\",\"hintText\":\"Importe a Natureza Financeira Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"fin_documento_origem\",\"campoLookup\":\"sigla\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_FORNECEDOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Fornecedor\",\"labelText\":\"Fornecedor\",\"tooltip\":\"Fornecedor\",\"hintText\":\"Importe o Fornecedor Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"fornecedor\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_BANCO_CONTA_CAIXA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Conta/Caixa\",\"labelText\":\"Conta/Caixa\",\"tooltip\":\"Conta/Caixa\",\"hintText\":\"Importe a Conta/Caixa Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"banco_conta_caixa\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE_PARCELA` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade de Parcelas\",\"labelText\":\"Quantidade de Parcelas\",\"tooltip\":\"Quantidade de Parcelas\",\"hintText\":\"Informe a Quantidade de Parcelas\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `VALOR_A_PAGAR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor a Pagar\",\"labelText\":\"Valor a Pagar\",\"tooltip\":\"Valor a Pagar\",\"hintText\":\"Informe o Valor a Pagar\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `DATA_LANCAMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Lançamento\",\"labelText\":\"Data de Lançamento\",\"tooltip\":\"Data de Lançamento\",\"hintText\":\"Informe a Data de Lançamento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `NUMERO_DOCUMENTO` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número do Documento\",\"labelText\":\"Número do Documento\",\"tooltip\":\"Número do Documento\",\"hintText\":\"Informe o Número do Documento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `IMAGEM_DOCUMENTO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Imagem Documento\",\"labelText\":\"Imagem Documento\",\"tooltip\":\"Imagem Documento\",\"hintText\":\"Imagem Documento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Armazena o caminho da imagem do documento, caso seja scaneado.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `PRIMEIRO_VENCIMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data do Primeiro Vencimento\",\"labelText\":\"Data do Primeiro Vencimento\",\"tooltip\":\"Data do Primeiro Vencimento\",\"hintText\":\"Informe a Data do Primeiro Vencimento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"2100-01-01\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `INTERVALO_ENTRE_PARCELAS` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Intervalo Entre Parcelas\",\"labelText\":\"Intervalo Entre Parcelas\",\"tooltip\":\"Intervalo Entre Parcelas\",\"hintText\":\"Informe o Intervalo Entre Parcelas (Dias)\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Quantidade de dias de intervalo entre as parcelas\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DIA_FIXO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Dia Fixo\",\"labelText\":\"Dia Fixo\",\"tooltip\":\"Dia Fixo\",\"hintText\":\"Informe o Dia Fixo para o Pagamento\",\"validacao\":\"DIA\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Caso preenchido, gerará as parcerlas para esse dia fixo, independente do intervalo entre parcelas\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"DIA\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `FK_DOC_ORIG_LCTO_PAGAR` (`ID_FIN_DOCUMENTO_ORIGEM` ASC),
  INDEX `fk_FIN_LANCAMENTO_PAGAR_FIN_NATUREZA_FINANCEIRA1_idx` (`ID_FIN_NATUREZA_FINANCEIRA` ASC),
  INDEX `fk_FIN_LANCAMENTO_PAGAR_FORNECEDOR1_idx` (`ID_FORNECEDOR` ASC),
  INDEX `fk_FIN_LANCAMENTO_PAGAR_BANCO_CONTA_CAIXA1_idx` (`ID_BANCO_CONTA_CAIXA` ASC),
  CONSTRAINT `fk_{78F2F6A7-9A1B-4960-9786-757E3E77873A}`
    FOREIGN KEY (`ID_FIN_DOCUMENTO_ORIGEM`)
    REFERENCES `fenix`.`FIN_DOCUMENTO_ORIGEM` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_FIN_LANCAMENTO_PAGAR_FIN_NATUREZA_FINANCEIRA1`
    FOREIGN KEY (`ID_FIN_NATUREZA_FINANCEIRA`)
    REFERENCES `fenix`.`FIN_NATUREZA_FINANCEIRA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_FIN_LANCAMENTO_PAGAR_FORNECEDOR1`
    FOREIGN KEY (`ID_FORNECEDOR`)
    REFERENCES `fenix`.`FORNECEDOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_FIN_LANCAMENTO_PAGAR_BANCO_CONTA_CAIXA1`
    FOREIGN KEY (`ID_BANCO_CONTA_CAIXA`)
    REFERENCES `fenix`.`BANCO_CONTA_CAIXA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela de lançamentos das contas a pagar.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`FIN_STATUS_PARCELA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FIN_STATUS_PARCELA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `SITUACAO` CHAR(2) NOT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Situação\",\"labelText\":\"Situação\",\"tooltip\":\"Situação\",\"hintText\":\"Informe a Situação\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"01 = Aberto | 02 = Quitado | 03 = Quitado Parcial | 04 = Vencido | 05 = Renegociado\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `DESCRICAO` VARCHAR(30) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Status\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `PROCEDIMENTO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Procedimento\",\"labelText\":\"Procedimento\",\"tooltip\":\"Procedimento\",\"hintText\":\"Informe o Procedimento para o Status\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Procedimento a ser adotado para o status em questão\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
COMMENT = 'Tabela que armazena as possíveis situações de uma parcela.\nStatus padrões: 01 = Aberto | 02 = Quitado | 03 = Quitado Parcial | 04 = Vencido | 05 = Renegociado'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`FIN_TIPO_PAGAMENTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FIN_TIPO_PAGAMENTO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Pagamento\",\"labelText\":\"Código Pagamento\",\"tooltip\":\"Código Pagamento\",\"hintText\":\"Informe o Código do Tipo de Pagamento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO` VARCHAR(30) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Código de Pagamento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Exemplos: 01 = Dinheiro | 02 = Cheque | 03 = Cartao\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Tabela que armazena os tipos de pagamento: DINHEIRO, CARTÃO, CHEQUE, etc.\nTipos padões já cadastrados pelo sistema para toda empresa:\n01 = Dinheiro | 02 = Cheque | 03 = Cartao'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`FIN_CHEQUE_EMITIDO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FIN_CHEQUE_EMITIDO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CHEQUE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cheque\",\"labelText\":\"Cheque\",\"tooltip\":\"Cheque\",\"hintText\":\"Importe o Cheque Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cheque\",\"campoLookup\":\"numero\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_EMISSAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data do Status\",\"labelText\":\"Data do Status\",\"tooltip\":\"Data do Status\",\"hintText\":\"Informe a Data do Status\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Dia em que o cheque foi emitido\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `BOM_PARA` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cheque Bom Para\",\"labelText\":\"Cheque Bom Para\",\"tooltip\":\"Cheque Bom Para\",\"hintText\":\"Informe a Data do Cheque Bom Para\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Dia previsto para a compensação do cheque\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `DATA_COMPENSACAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data da Compensação\",\"labelText\":\"Data da Compensação\",\"tooltip\":\"Data da Compensação\",\"hintText\":\"Informe a Data da Compensação\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Dia em que o cheque foi compensado\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `VALOR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor\",\"labelText\":\"Valor\",\"tooltip\":\"Valor\",\"hintText\":\"Informe o Valor do Cheque\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `NOMINAL_A` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nominal A\",\"labelText\":\"Nominal A\",\"tooltip\":\"Nominal A\",\"hintText\":\"Cheque Nominal A\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `FK_CHEQUE_EMITIDO` (`ID_CHEQUE` ASC),
  CONSTRAINT `fk_{75F21313-9E6B-4319-9C96-B460CCA89916}`
    FOREIGN KEY (`ID_CHEQUE`)
    REFERENCES `fenix`.`CHEQUE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela que faz o controle dos cheque emitidos.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`FIN_PARCELA_PAGAR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FIN_PARCELA_PAGAR` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_FIN_LANCAMENTO_PAGAR` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_FIN_STATUS_PARCELA` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Status Parcela\",\"labelText\":\"Status Parcela\",\"tooltip\":\"Status Parcela\",\"hintText\":\"Importe o Status Parcela Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"fin_status_parcela\",\"campoLookup\":\"descricao\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_FIN_TIPO_PAGAMENTO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Pagamento\",\"labelText\":\"Tipo Pagamento\",\"tooltip\":\"Tipo Pagamento\",\"hintText\":\"Importe o Tipo de Pagamento Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"fin_tipo_pagamento\",\"campoLookup\":\"descricao\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_FIN_CHEQUE_EMITIDO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cheque\",\"labelText\":\"Cheque\",\"tooltip\":\"Cheque\",\"hintText\":\"Importe o Cheque Vinculado\",\"validacao\":\"\",\"tabelaLookup\":\"fin_cheque_emitido\",\"campoLookup\":\"cheque?.numero\",\"campoLookupTipoDado\":\"int\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `NUMERO_PARCELA` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número da Parcela\",\"labelText\":\"Número da Parcela\",\"tooltip\":\"Número da Parcela\",\"hintText\":\"Informe o Número da Parcela\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DATA_EMISSAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Emissão\",\"labelText\":\"Data de Emissão\",\"tooltip\":\"Data de Emissão\",\"hintText\":\"Informe a Data de Emissão\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `DATA_VENCIMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Vencimento\",\"labelText\":\"Data de Vencimento\",\"tooltip\":\"Data de Vencimento\",\"hintText\":\"Informe a Data de Vencimento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `DATA_PAGAMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Pagamento\",\"labelText\":\"Data de Pagamento\",\"tooltip\":\"Data de Pagamento\",\"hintText\":\"Informe a Data de Pagamento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `DESCONTO_ATE` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Desconto Até\",\"labelText\":\"Desconto Até\",\"tooltip\":\"Desconto Até\",\"hintText\":\"Conceder Desconto até a Data Informada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Conceder desconto até a data especificada\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `VALOR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor\",\"labelText\":\"Valor\",\"tooltip\":\"Valor\",\"hintText\":\"Informe o Valor da parcela\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `TAXA_JURO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Juros\",\"labelText\":\"Taxa Juros\",\"tooltip\":\"Taxa Juros\",\"hintText\":\"Informe a Taxa de Juros\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `TAXA_MULTA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Multa\",\"labelText\":\"Taxa Multa\",\"tooltip\":\"Taxa Multa\",\"hintText\":\"Informe a Taxa de Multa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `TAXA_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Desconto\",\"labelText\":\"Taxa Desconto\",\"tooltip\":\"Taxa Desconto\",\"hintText\":\"Informe a Taxa de Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_JURO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Juros\",\"labelText\":\"Valor Juros\",\"tooltip\":\"Valor Juros\",\"hintText\":\"Informe o Valor dos Juros\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_MULTA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Multa\",\"labelText\":\"Valor Multa\",\"tooltip\":\"Valor Multa\",\"hintText\":\"Informe o Valor da Multa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto\",\"labelText\":\"Valor Desconto\",\"tooltip\":\"Valor Desconto\",\"hintText\":\"Informe o Valor do Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_PAGO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Pago\",\"labelText\":\"Valor Pago\",\"tooltip\":\"Valor Pago\",\"hintText\":\"Informe o Valor Pago\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `HISTORICO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Histórico\",\"labelText\":\"Histórico\",\"tooltip\":\"Histórico\",\"hintText\":\"Informe o Histórico\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `FK_STATUS_PARCELA_PAGAR` (`ID_FIN_STATUS_PARCELA` ASC),
  INDEX `FK_LANCAMENTO_PARCELA` (`ID_FIN_LANCAMENTO_PAGAR` ASC),
  INDEX `fk_FIN_PARCELA_PAGAR_FIN_TIPO_PAGAMENTO1_idx` (`ID_FIN_TIPO_PAGAMENTO` ASC),
  INDEX `fk_FIN_PARCELA_PAGAR_FIN_CHEQUE_EMITIDO1_idx` (`ID_FIN_CHEQUE_EMITIDO` ASC),
  CONSTRAINT `fk_{56615208-C2D0-4DD9-802B-2B99BFF4EF53}`
    FOREIGN KEY (`ID_FIN_STATUS_PARCELA`)
    REFERENCES `fenix`.`FIN_STATUS_PARCELA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_{06A2AAB2-9856-4010-AEDB-BB0F0EC86ABF}`
    FOREIGN KEY (`ID_FIN_LANCAMENTO_PAGAR`)
    REFERENCES `fenix`.`FIN_LANCAMENTO_PAGAR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_FIN_PARCELA_PAGAR_FIN_TIPO_PAGAMENTO1`
    FOREIGN KEY (`ID_FIN_TIPO_PAGAMENTO`)
    REFERENCES `fenix`.`FIN_TIPO_PAGAMENTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_FIN_PARCELA_PAGAR_FIN_CHEQUE_EMITIDO1`
    FOREIGN KEY (`ID_FIN_CHEQUE_EMITIDO`)
    REFERENCES `fenix`.`FIN_CHEQUE_EMITIDO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela que garda as parcelas para pagamento. Caso o pagamento seja efetuado de uma vez, a tabela LANCAMENTO_PAGAR gerará uma parcela para ser paga e a mesma será armazenada nesta tabela.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`FIN_TIPO_RECEBIMENTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FIN_TIPO_RECEBIMENTO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Recebimento\",\"labelText\":\"Código Recebimento\",\"tooltip\":\"Código Recebimento\",\"hintText\":\"Informe o Código do Tipo de Recebimento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Tipo de Recebimento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Exemplos: 01 = Dinheiro | 02 = Cheque | 03 = Cartao\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
COMMENT = 'Tabela que armazena os tipos de recebimento: DINHEIRO, CARTÃO, CHEQUE, etc. \nTipos padões já cadastrados pelo sistema para toda empresa: \n01 = Dinheiro | 02 = Cheque | 03 = Cartao'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`FIN_LANCAMENTO_RECEBER`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FIN_LANCAMENTO_RECEBER` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_FIN_DOCUMENTO_ORIGEM` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Documento Origem\",\"labelText\":\"Documento Origem\",\"tooltip\":\"Documento Origem\",\"hintText\":\"Importe o Documento de Origem Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"fin_documento_origem\",\"campoLookup\":\"sigla\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_FIN_NATUREZA_FINANCEIRA` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Natureza Financeira\",\"labelText\":\"Natureza Financeira\",\"tooltip\":\"Natureza Financeira\",\"hintText\":\"Importe a Natureza Financeira Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"fin_documento_origem\",\"campoLookup\":\"sigla\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_CLIENTE` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cliente\",\"labelText\":\"Cliente\",\"tooltip\":\"Cliente\",\"hintText\":\"Importe o Cliente Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cliente\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_BANCO_CONTA_CAIXA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Conta/Caixa\",\"labelText\":\"Conta/Caixa\",\"tooltip\":\"Conta/Caixa\",\"hintText\":\"Importe a Conta/Caixa Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"banco_conta_caixa\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE_PARCELA` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade de Parcelas\",\"labelText\":\"Quantidade de Parcelas\",\"tooltip\":\"Quantidade de Parcelas\",\"hintText\":\"Informe a Quantidade de Parcelas\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `VALOR_A_RECEBER` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor a Receber\",\"labelText\":\"Valor a Receber\",\"tooltip\":\"Valor a Receber\",\"hintText\":\"Informe o Valor a Receber\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `DATA_LANCAMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Lançamento\",\"labelText\":\"Data de Lançamento\",\"tooltip\":\"Data de Lançamento\",\"hintText\":\"Informe a Data de Lançamento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `NUMERO_DOCUMENTO` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número do Documento\",\"labelText\":\"Número do Documento\",\"tooltip\":\"Número do Documento\",\"hintText\":\"Informe o Número do Documento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `PRIMEIRO_VENCIMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data do Primeiro Vencimento\",\"labelText\":\"Data do Primeiro Vencimento\",\"tooltip\":\"Data do Primeiro Vencimento\",\"hintText\":\"Informe a Data do Primeiro Vencimento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"2100-01-01\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `TAXA_COMISSAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa de Comissão\",\"labelText\":\"Taxa de Comissão\",\"tooltip\":\"Taxa de Comissão\",\"hintText\":\"Informe a Taxa de Comissão\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}\n',
  `VALOR_COMISSAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Comissão\",\"labelText\":\"Valor Comissão\",\"tooltip\":\"Valor Comissão\",\"hintText\":\"Informe o Valor da Comissão\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `INTERVALO_ENTRE_PARCELAS` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Intervalo Entre Parcelas\",\"labelText\":\"Intervalo Entre Parcelas\",\"tooltip\":\"Intervalo Entre Parcelas\",\"hintText\":\"Informe o Intervalo Entre Parcelas (Dias)\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Quantidade de dias de intervalo entre as parcelas\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DIA_FIXO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Dia Fixo da Parcela\",\"labelText\":\"Dia Fixo da Parcela\",\"tooltip\":\"Dia Fixo da Parcela\",\"hintText\":\"Informe o Dia Fixo da Parcela\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"dia_parcela\",\"campoLookup\":\"dia\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `FK_DOC_ORI_LANC_RECEBER` (`ID_FIN_DOCUMENTO_ORIGEM` ASC),
  INDEX `fk_FIN_LANCAMENTO_RECEBER_FIN_NATUREZA_FINANCEIRA1_idx` (`ID_FIN_NATUREZA_FINANCEIRA` ASC),
  INDEX `fk_FIN_LANCAMENTO_RECEBER_CLIENTE1_idx` (`ID_CLIENTE` ASC),
  INDEX `fk_FIN_LANCAMENTO_RECEBER_BANCO_CONTA_CAIXA1_idx` (`ID_BANCO_CONTA_CAIXA` ASC),
  CONSTRAINT `fk_{B290D08D-4B6D-4B5C-8C08-29AE67AB180C}`
    FOREIGN KEY (`ID_FIN_DOCUMENTO_ORIGEM`)
    REFERENCES `fenix`.`FIN_DOCUMENTO_ORIGEM` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_FIN_LANCAMENTO_RECEBER_FIN_NATUREZA_FINANCEIRA1`
    FOREIGN KEY (`ID_FIN_NATUREZA_FINANCEIRA`)
    REFERENCES `fenix`.`FIN_NATUREZA_FINANCEIRA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_FIN_LANCAMENTO_RECEBER_CLIENTE1`
    FOREIGN KEY (`ID_CLIENTE`)
    REFERENCES `fenix`.`CLIENTE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_FIN_LANCAMENTO_RECEBER_BANCO_CONTA_CAIXA1`
    FOREIGN KEY (`ID_BANCO_CONTA_CAIXA`)
    REFERENCES `fenix`.`BANCO_CONTA_CAIXA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela de lançamentos das contas a receber.\n'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`FIN_CHEQUE_RECEBIDO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FIN_CHEQUE_RECEBIDO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_PESSOA` INT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Pessoa\",\"labelText\":\"Pessoa\",\"tooltip\":\"Pessoa\",\"hintText\":\"Importe a Pessoa Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"pessoa\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CPF` VARCHAR(11) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CPF\",\"labelText\":\"CPF\",\"tooltip\":\"CPF\",\"hintText\":\"Informe o CPF do Cliente\",\"validacao\":\"CPF\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CPF\"}}',
  `CNPJ` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ\",\"labelText\":\"CNPJ\",\"tooltip\":\"CNPJ\",\"hintText\":\"Informe o CNPJ do Cliente\",\"validacao\":\"CNPJ\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}',
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o nome do Cliente\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CODIGO_BANCO` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Banco\",\"labelText\":\"Código Banco\",\"tooltip\":\"Código Banco\",\"hintText\":\"Informe o Código do Banco\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CODIGO_AGENCIA` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Agência\",\"labelText\":\"Código Agência\",\"tooltip\":\"Código Agência\",\"hintText\":\"Informe o Código da Agência\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CONTA` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Conta\",\"labelText\":\"Conta\",\"tooltip\":\"Conta\",\"hintText\":\"Informe a Conta\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `NUMERO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número do Cheque\",\"labelText\":\"Número do Cheque\",\"tooltip\":\"Número do Cheque\",\"hintText\":\"Informe o Número do Cheque\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DATA_EMISSAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Emissão\",\"labelText\":\"Data de Emissão\",\"tooltip\":\"Data de Emissão\",\"hintText\":\"Informe a Data de Emissão\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `BOM_PARA` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cheque Bom Para\",\"labelText\":\"Cheque Bom Para\",\"tooltip\":\"Cheque Bom Para\",\"hintText\":\"Informe a Data do Cheque Bom Para\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `DATA_COMPENSACAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Compensação\",\"labelText\":\"Data de Compensação\",\"tooltip\":\"Data de Compensação\",\"hintText\":\"Informe a Data de Compensação\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `VALOR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor\",\"labelText\":\"Valor\",\"tooltip\":\"Valor\",\"hintText\":\"Informe o Valor do Cheque\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `CUSTODIA_DATA` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data da Custódia\",\"labelText\":\"Data da Custódia\",\"tooltip\":\"Data da Custódia\",\"hintText\":\"Informe a Data da Custódia\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Data em que o cheque foi custodiado\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}} ',
  `CUSTODIA_TARIFA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tarifa Custódia\",\"labelText\":\"Tarifa Custódia\",\"tooltip\":\"Tarifa Custódia\",\"hintText\":\"Informe o Valor da Tarifa Custódia\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"tarifa em cima do valor deste cheque\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `CUSTODIA_COMISSAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Custódia Comissão\",\"labelText\":\"Custódia Comissão\",\"tooltip\":\"Custódia Comissão\",\"hintText\":\"Informe o Valor da  Comissão da Custódia\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"comissão em cima do valor deste cheque\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `DESCONTO_DATA` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Desconto\",\"labelText\":\"Data de Desconto\",\"tooltip\":\"Data de Desconto\",\"hintText\":\"Informe a Data de Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Data em que o cheque foi descontado [Possibilidade de antecipar o recebimento dos créditos por meio do Desconto de Cheques]\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `DESCONTO_TARIFA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tarifa Desconto\",\"labelText\":\"Tarifa Desconto\",\"tooltip\":\"Tarifa Desconto\",\"hintText\":\"Informe o Valor da Tarifa Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"tarifa em cima do valor deste cheque\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `DESCONTO_COMISSAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Desconto Comissão\",\"labelText\":\"Desconto Comissão\",\"tooltip\":\"Desconto Comissão\",\"hintText\":\"Informe o Valor da  Comissão da Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"comissão em cima do valor deste cheque\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_RECEBIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Recebido\",\"labelText\":\"Valor Recebido\",\"tooltip\":\"Valor Recebido\",\"hintText\":\"Informe o Valor Recebido\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"VALOR - TARIFA(S) - COMISSAO(OES)\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_FIN_CHEQUE_RECEBIDO_PESSOA1_idx` (`ID_PESSOA` ASC),
  CONSTRAINT `fk_FIN_CHEQUE_RECEBIDO_PESSOA1`
    FOREIGN KEY (`ID_PESSOA`)
    REFERENCES `fenix`.`PESSOA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazenar os dados dos cheques recebidos identicando a pessoa que emitiu o cheque, estando cadastrada no BD ou não.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`FIN_PARCELA_RECEBER`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FIN_PARCELA_RECEBER` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_FIN_LANCAMENTO_RECEBER` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_FIN_STATUS_PARCELA` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Status Parcela\",\"labelText\":\"Status Parcela\",\"tooltip\":\"Status Parcela\",\"hintText\":\"Importe o Status Parcela Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"fin_status_parcela\",\"campoLookup\":\"descricao\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_FIN_TIPO_RECEBIMENTO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Recebimento\",\"labelText\":\"Tipo Recebimento\",\"tooltip\":\"Tipo Recebimento\",\"hintText\":\"Importe o Tipo de Recebimento Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"fin_tipo_recebimento\",\"campoLookup\":\"descricao\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_FIN_CHEQUE_RECEBIDO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cheque\",\"labelText\":\"Cheque\",\"tooltip\":\"Cheque\",\"hintText\":\"Importe o Cheque Vinculado\",\"validacao\":\"\",\"tabelaLookup\":\"fin_cheque_recebido\",\"campoLookup\":\"numero\",\"campoLookupTipoDado\":\"int\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `NUMERO_PARCELA` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número da Parcela\",\"labelText\":\"Número da Parcela\",\"tooltip\":\"Número da Parcela\",\"hintText\":\"Informe o Número da Parcela\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DATA_EMISSAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Emissão\",\"labelText\":\"Data de Emissão\",\"tooltip\":\"Data de Emissão\",\"hintText\":\"Informe a Data de Emissão\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `DATA_VENCIMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Vencimento\",\"labelText\":\"Data de Vencimento\",\"tooltip\":\"Data de Vencimento\",\"hintText\":\"Informe a Data de Vencimento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `DATA_RECEBIMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Recebimento\",\"labelText\":\"Data de Recebimento\",\"tooltip\":\"Data de Recebimento\",\"hintText\":\"Informe a Data de Recebimento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `DESCONTO_ATE` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Desconto Até\",\"labelText\":\"Desconto Até\",\"tooltip\":\"Desconto Até\",\"hintText\":\"Conceder Desconto até a Data Informada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Conceder desconto até a data especificada\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `VALOR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor\",\"labelText\":\"Valor\",\"tooltip\":\"Valor\",\"hintText\":\"Informe o Valor da parcela\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `TAXA_JURO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Juros\",\"labelText\":\"Taxa Juros\",\"tooltip\":\"Taxa Juros\",\"hintText\":\"Informe a Taxa de Juros\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `TAXA_MULTA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Multa\",\"labelText\":\"Taxa Multa\",\"tooltip\":\"Taxa Multa\",\"hintText\":\"Informe a Taxa de Multa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `TAXA_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Desconto\",\"labelText\":\"Taxa Desconto\",\"tooltip\":\"Taxa Desconto\",\"hintText\":\"Informe a Taxa de Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_JURO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Juros\",\"labelText\":\"Valor Juros\",\"tooltip\":\"Valor Juros\",\"hintText\":\"Informe o Valor dos Juros\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_MULTA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Multa\",\"labelText\":\"Valor Multa\",\"tooltip\":\"Valor Multa\",\"hintText\":\"Informe o Valor da Multa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto\",\"labelText\":\"Valor Desconto\",\"tooltip\":\"Valor Desconto\",\"hintText\":\"Informe o Valor do Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `EMITIU_BOLETO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Emitiu Boleto\",\"labelText\":\"Emitiu Boleto\",\"tooltip\":\"Emitiu Boleto\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}\n',
  `BOLETO_NOSSO_NUMERO` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Boleto Nosso Número\",\"labelText\":\"Boleto Nosso Número\",\"tooltip\":\"Boleto Nosso Número\",\"hintText\":\"Informe o Nosso Número do Boleto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `VALOR_RECEBIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Recebido\",\"labelText\":\"Valor Recebido\",\"tooltip\":\"Valor Recebido\",\"hintText\":\"Informe o Valor Recebido\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `HISTORICO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Histórico\",\"labelText\":\"Histórico\",\"tooltip\":\"Histórico\",\"hintText\":\"Informe o Histórico\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `FK_LANCAMENTO_PARCELA_RECEBER` (`ID_FIN_LANCAMENTO_RECEBER` ASC),
  INDEX `FK_STATUS_PARCELA_RECEBER` (`ID_FIN_STATUS_PARCELA` ASC),
  INDEX `fk_FIN_PARCELA_RECEBER_FIN_TIPO_RECEBIMENTO1_idx` (`ID_FIN_TIPO_RECEBIMENTO` ASC),
  INDEX `fk_FIN_PARCELA_RECEBER_FIN_CHEQUE_RECEBIDO1_idx` (`ID_FIN_CHEQUE_RECEBIDO` ASC),
  CONSTRAINT `fk_{8779D373-1258-4798-A769-5765822422BB}`
    FOREIGN KEY (`ID_FIN_LANCAMENTO_RECEBER`)
    REFERENCES `fenix`.`FIN_LANCAMENTO_RECEBER` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_{DCA2496F-0D8C-484B-95D4-A7DCDE607642}`
    FOREIGN KEY (`ID_FIN_STATUS_PARCELA`)
    REFERENCES `fenix`.`FIN_STATUS_PARCELA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_FIN_PARCELA_RECEBER_FIN_TIPO_RECEBIMENTO1`
    FOREIGN KEY (`ID_FIN_TIPO_RECEBIMENTO`)
    REFERENCES `fenix`.`FIN_TIPO_RECEBIMENTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_FIN_PARCELA_RECEBER_FIN_CHEQUE_RECEBIDO1`
    FOREIGN KEY (`ID_FIN_CHEQUE_RECEBIDO`)
    REFERENCES `fenix`.`FIN_CHEQUE_RECEBIDO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela que garda as parcelas para recebimento. Caso o recebimento seja efetuado de uma vez, a tabela LANCAMENTO_RECEBER gerará uma parcela para ser recebida e a mesma será armazenada nesta tabela.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`FIN_CONFIGURACAO_BOLETO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`FIN_CONFIGURACAO_BOLETO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_BANCO_CONTA_CAIXA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Conta/Caixa\",\"labelText\":\"Conta/Caixa\",\"tooltip\":\"Conta/Caixa\",\"hintText\":\"Importe a Conta/Caixa Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"banco_conta_caixa\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `INSTRUCAO01` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Instrução 01\",\"labelText\":\"Instrução 01\",\"tooltip\":\"Instrução 01\",\"hintText\":\"Informe a Instrução 01 do Boleto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `INSTRUCAO02` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Instrução 02\",\"labelText\":\"Instrução 02\",\"tooltip\":\"Instrução 02\",\"hintText\":\"Informe a Instrução 02 do Boleto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CAMINHO_ARQUIVO_REMESSA` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Caminho Arquivo Remessa\",\"labelText\":\"Caminho Arquivo Remessa\",\"tooltip\":\"Caminho Arquivo Remessa\",\"hintText\":\"Informe o Caminho Arquivo Remessa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Caminho onde o arquivo de remessa deve ser gerado\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CAMINHO_ARQUIVO_RETORNO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Caminho Arquivo Retorno\",\"labelText\":\"Caminho Arquivo Retorno\",\"tooltip\":\"Caminho Arquivo Retorno\",\"hintText\":\"Informe o Caminho Arquivo Retorno\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Caminho onde o arquivo de retorno deve ser lido\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CAMINHO_ARQUIVO_LOGOTIPO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Caminho Arquivo Logotipo\",\"labelText\":\"Caminho Arquivo Logotipo\",\"tooltip\":\"Caminho Arquivo Logotipo\",\"hintText\":\"Informe o Caminho Arquivo Logotipo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Caminho onde se encontra o logotipo do banco que é impresso no boleto\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CAMINHO_ARQUIVO_PDF` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Caminho Arquivo PDF\",\"labelText\":\"Caminho Arquivo PDF\",\"tooltip\":\"Caminho Arquivo PDF\",\"hintText\":\"Informe o Caminho Arquivo PDF\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Caminho onde o arquivo PDF do boleto deve ser gerado, se for o caso\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `MENSAGEM` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Mensagem\",\"labelText\":\"Mensagem\",\"tooltip\":\"Mensagem\",\"hintText\":\"Informe a Mensagem do Boleto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `LOCAL_PAGAMENTO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Local Pagamento\",\"labelText\":\"Local Pagamento\",\"tooltip\":\"Local Pagamento\",\"hintText\":\"Informe o Local de Pagamento do Boleto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `LAYOUT_REMESSA` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Layout Remessa\",\"labelText\":\"Layout Remessa\",\"tooltip\":\"Layout Remessa\",\"hintText\":\"Informe o Layout de Remessa do Boleto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"indice\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"CNAB 240\"},{\"dropDownButtonItem\":\"CNAB 400\"}]}}\n',
  `ACEITE` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Aceite\",\"labelText\":\"Aceite\",\"tooltip\":\"Aceite\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}\n',
  `ESPECIE` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Espécie\",\"labelText\":\"Espécie\",\"tooltip\":\"Espécie\",\"hintText\":\"Selecione a Espécie do Boleto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"indice\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"DM-Duplicata Mercantil\"},{\"dropDownButtonItem\":\"DS-Duplicata de Serviços\"},{\"dropDownButtonItem\":\"RC-Recibo\"},{\"dropDownButtonItem\":\"NP-Nota Promissória\"}]}}\n',
  `CARTEIRA` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Carteira\",\"labelText\":\"Carteira\",\"tooltip\":\"Carteira\",\"hintText\":\"Informe a Carteira do Boleto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CODIGO_CONVENIO` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Convênio\",\"labelText\":\"Código Convênio\",\"tooltip\":\"Código Convênio\",\"hintText\":\"Informe o Código de Convênio do Boleto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CODIGO_CEDENTE` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Cedente\",\"labelText\":\"Código Cedente\",\"tooltip\":\"Código Cedente\",\"hintText\":\"Informe o Código de Cedente do Boleto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `TAXA_MULTA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Multa\",\"labelText\":\"Taxa Multa\",\"tooltip\":\"Taxa Multa\",\"hintText\":\"Informe a Taxa de Multa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}\n',
  `TAXA_JURO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Juros\",\"labelText\":\"Taxa Juros\",\"tooltip\":\"Taxa Juros\",\"hintText\":\"Informe a Taxa de Juros\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}\n',
  `DIAS_PROTESTO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Dias Protesto\",\"labelText\":\"Dias Protesto\",\"tooltip\":\"Dias Protesto\",\"hintText\":\"Informe a Quantidade de Dias para Protesto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `NOSSO_NUMERO_ANTERIOR` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nosso Número Anterior\",\"labelText\":\"Nosso Número Anterior\",\"tooltip\":\"Nosso Número Anterior\",\"hintText\":\"Informe o Nosso Número Anterior\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `fk_FIN_CONFIGURACAO_BOLETO_BANCO_CONTA_CAIXA1_idx` (`ID_BANCO_CONTA_CAIXA` ASC),
  CONSTRAINT `fk_FIN_CONFIGURACAO_BOLETO_BANCO_CONTA_CAIXA1`
    FOREIGN KEY (`ID_BANCO_CONTA_CAIXA`)
    REFERENCES `fenix`.`BANCO_CONTA_CAIXA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena as configurações dos boletos.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`TRIBUT_OPERACAO_FISCAL`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`TRIBUT_OPERACAO_FISCAL` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `DESCRICAO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição da Operação Fiscal\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO_NA_NF` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição na NF\",\"labelText\":\"Descrição na NF\",\"tooltip\":\"Descrição na NF\",\"hintText\":\"Informe a Descrição na NF da Operação Fiscal\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Sai no campo Natureza da Operação\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CFOP` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CFOP\",\"labelText\":\"CFOP\",\"tooltip\":\"CFOP\",\"hintText\":\"Informe o CFOP\",\"validacao\":\"\",\"campoLookup\":\"codigo\",\"tabelaLookup\":\"cfop\",\"campoLookupTipoDado\":\"int\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `OBSERVACAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Esta tabela deve irá armazenar as diferentes operações presentes na empresa conforme cada tratamento tributário nela existente do ponto de vista do Cliente ou Destinatário. \n\nExemplos: \n1. Venda a  não contribuinte. Ex. Consumidor final, Construtora ou empresas que comprem os produtos para uso próprio.\n2. Venda para Lojistas (Revendedor)\n3. Venda Consumidor fora do Estado\n4. Venda a Contribuinte Usuário final (ex. construtora, empresa que comprem para consumo)\n5. Venda Lojista fora do Estado\n'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`TRIBUT_CONFIGURA_OF_GT`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`TRIBUT_CONFIGURA_OF_GT` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_TRIBUT_GRUPO_TRIBUTARIO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Grupo Tributário\",\"labelText\":\"Grupo Tributário\",\"tooltip\":\"Grupo Tributário\",\"hintText\":\"Importe o Grupo Tributário Vinculado\",\"validacao\":\"\",\"tabelaLookup\":\"tribut_grupo_tributario\",\"campoLookup\":\"descricao\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_TRIBUT_OPERACAO_FISCAL` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Operação Fiscal\",\"labelText\":\"Operação Fiscal\",\"tooltip\":\"Operação Fiscal\",\"hintText\":\"Importe a Operação Fiscal Vinculada\",\"validacao\":\"\",\"tabelaLookup\":\"tribut_operacao_fiscal\",\"campoLookup\":\"descricao\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_OP_FISCAL_CONFIGURA` (`ID_TRIBUT_OPERACAO_FISCAL` ASC),
  INDEX `FK_GRUPO_TRIB_CONFIGURA` (`ID_TRIBUT_GRUPO_TRIBUTARIO` ASC),
  CONSTRAINT `fk_{6AF0FD4D-0B2B-495D-951B-03F72360D2F8}`
    FOREIGN KEY (`ID_TRIBUT_OPERACAO_FISCAL`)
    REFERENCES `fenix`.`TRIBUT_OPERACAO_FISCAL` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_{7992BFAD-59FB-4EF4-9024-FFD46F78BC5E}`
    FOREIGN KEY (`ID_TRIBUT_GRUPO_TRIBUTARIO`)
    REFERENCES `fenix`.`TRIBUT_GRUPO_TRIBUTARIO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Configura Operação Fiscal / Grupo Tributário\n\nArmazena as configurações dos tributos da Operação Fiscal relacionada com o Grupo Tributário'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`TRIBUT_ICMS_UF`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`TRIBUT_ICMS_UF` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_TRIBUT_CONFIGURA_OF_GT` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `UF_DESTINO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF de Destino\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"UF destino para cálculo do ICMS\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}',
  `CFOP` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CFOP\",\"labelText\":\"CFOP\",\"tooltip\":\"CFOP\",\"hintText\":\"Informe o CFOP\",\"validacao\":\"\",\"campoLookup\":\"codigo\",\"tabelaLookup\":\"cfop\",\"campoLookupTipoDado\":\"int\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CSOSN` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CSOSN\",\"labelText\":\"CSOSN\",\"tooltip\":\"CSOSN\",\"hintText\":\"Informe o CSOSN\",\"validacao\":\"\",\"campoLookup\":\"codigo\",\"tabelaLookup\":\"csosn\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CST` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CST\",\"labelText\":\"CST\",\"tooltip\":\"CST\",\"hintText\":\"Informe o CST\",\"validacao\":\"\",\"campoLookup\":\"codigo\",\"tabelaLookup\":\"cst\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MODALIDADE_BC` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modalidade Base Cálculo\",\"labelText\":\"Modalidade Base Cálculo\",\"tooltip\":\"Modalidade Base Cálculo\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"0-Margem Valor Agregado (%); (para operações inter estados)  | 1-Pauta (Valor); (valor do produto)  | 2-Preço Tabelado Máx. (valor); (para setores da economia com valores tabelados, no caso de farmacias)  | 3-Valor da Operação. (no caso de serviço, onde o preço é calculado para a operação)\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"0-Margem Valor Agregado (%)\"},{\"dropDownButtonItem\":\"1-Pauta (Valor)\"},{\"dropDownButtonItem\":\"2-Preço Tabelado Máx. (valor)\"},{\"dropDownButtonItem\":\"3-Valor da Operação\"}]}}',
  `ALIQUOTA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota\",\"labelText\":\"Alíquota\",\"tooltip\":\"Alíquota\",\"hintText\":\"Informe o Alíquota caso Modalidade BC=3\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_PAUTA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Pauta\",\"labelText\":\"Valor Pauta\",\"tooltip\":\"Valor Pauta\",\"hintText\":\"Informe o Valor Pauta, caso Modalidade BC=1\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_PRECO_MAXIMO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Preço Máximo\",\"labelText\":\"Valor Preço Máximo\",\"tooltip\":\"Valor Preço Máximo\",\"hintText\":\"Informe o Valor Preço Máximo, caso Modalidade BC=2\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `MVA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor MVA\",\"labelText\":\"Valor MVA\",\"tooltip\":\"Valor MVA\",\"hintText\":\"Informe o Valor da Margem Valor Agregado\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `PORCENTO_BC` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Porcento Base Cálculo\",\"labelText\":\"Porcento Base Cálculo\",\"tooltip\":\"Porcento Base Cálculo\",\"hintText\":\"Informe o Porcentual da Base de Cálculo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `MODALIDADE_BC_ST` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modalidade Base Cálculo ST\",\"labelText\":\"Modalidade Base Cálculo ST\",\"tooltip\":\"Modalidade Base Cálculo ST\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"0-Preço tabelado ou máximo sugerido; (no caso de medicamentos)  | 1-Lista Negativa (valor); (aplicado a medicamentos)  | 2-Lista Positiva (valor); (aplicado a medicamentos)  | 3-Lista Neutra (valor); (aplicado a medicamentos)  | 4-Margem Valor Agregado (%)  | 5-Pauta (valor); valor do produto\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"0-Preço tabelado ou máximo sugerido\"},{\"dropDownButtonItem\":\"1-Lista Negativa (valor)\"},{\"dropDownButtonItem\":\"2-Lista Positiva (valor)\"},{\"dropDownButtonItem\":\"3-Lista Neutra (valor)\"},{\"dropDownButtonItem\":\"4-Margem Valor Agregado (%)\"},{\"dropDownButtonItem\":\"5-Pauta (valor)\"}]}}',
  `ALIQUOTA_INTERNA_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Interna ST\",\"labelText\":\"Alíquota Interna ST\",\"tooltip\":\"Alíquota Interna ST\",\"hintText\":\"Informe a Alíquota Interna ST\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Utilizado para cálculo em algumas situações do ICMS ST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `ALIQUOTA_INTERESTADUAL_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Interestadual ST\",\"labelText\":\"Alíquota Interestadual ST\",\"tooltip\":\"Alíquota Interestadual ST\",\"hintText\":\"Informe a Alíquota Interestadual ST\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `PORCENTO_BC_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Porcento Base Cálculo ST\",\"labelText\":\"Porcento Base Cálculo ST\",\"tooltip\":\"Porcento Base Cálculo ST\",\"hintText\":\"Informe o Porcentual da Base de Cálculo ST\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `ALIQUOTA_ICMS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota ICMS ST\",\"labelText\":\"Alíquota ICMS ST\",\"tooltip\":\"Alíquota ICMS ST\",\"hintText\":\"Informe o Alíquota ICMS ST\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_PAUTA_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Pauta ST\",\"labelText\":\"Valor Pauta ST\",\"tooltip\":\"Valor Pauta ST\",\"hintText\":\"Informe o Valor Pauta ST, caso Modalidade BC ST=5\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_PRECO_MAXIMO_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Preço Máximo ST\",\"labelText\":\"Valor Preço Máximo ST\",\"tooltip\":\"Valor Preço Máximo ST\",\"hintText\":\"Informe o Valor Preço Máximo ST, caso Modalidade BC ST=0\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_CONFIG_OF_GT_ICMS` (`ID_TRIBUT_CONFIGURA_OF_GT` ASC),
  CONSTRAINT `fk_{19D06CF5-5DC7-480E-A49D-BECF75F651EE}`
    FOREIGN KEY (`ID_TRIBUT_CONFIGURA_OF_GT`)
    REFERENCES `fenix`.`TRIBUT_CONFIGURA_OF_GT` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Esta tabela ira armazenar a tributaçao para cada UF em cada GRUPO TRIBUTARIO dentro de cada operação cadastrada.\n'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`TRIBUT_PIS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`TRIBUT_PIS` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_TRIBUT_CONFIGURA_OF_GT` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CST_PIS` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CST PIS\",\"labelText\":\"CST PIS\",\"tooltip\":\"CST PIS\",\"hintText\":\"Informe o CST PIS\",\"validacao\":\"\",\"campoLookup\":\"codigo\",\"tabelaLookup\":\"cst_pis\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `EFD_TABELA_435` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"EFD 435\",\"labelText\":\"EFD 435\",\"tooltip\":\"EFD 435\",\"hintText\":\"Informe o EFD 435\",\"validacao\":\"\",\"campoLookup\":\"codigo\",\"tabelaLookup\":\"efd_435\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MODALIDADE_BASE_CALCULO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modalidade Base Cálculo\",\"labelText\":\"Modalidade Base Cálculo\",\"tooltip\":\"Modalidade Base Cálculo\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"0-Percentual\"},{\"dropDownButtonItem\":\"1-Unidade\"}]}}',
  `PORCENTO_BASE_CALCULO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Porcento Base Cálculo\",\"labelText\":\"Porcento Base Cálculo\",\"tooltip\":\"Porcento Base Cálculo\",\"hintText\":\"Informe o Porcento da Base de Cálculo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `ALIQUOTA_PORCENTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Porcento\",\"labelText\":\"Alíquota Porcento\",\"tooltip\":\"Alíquota Porcento\",\"hintText\":\"Informe a Alíquota do Porcento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `ALIQUOTA_UNIDADE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Unidade\",\"labelText\":\"Alíquota Unidade\",\"tooltip\":\"Alíquota Unidade\",\"hintText\":\"Informe a Alíquota da Unidade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_PRECO_MAXIMO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Preço Máximo\",\"labelText\":\"Valor Preço Máximo\",\"tooltip\":\"Valor Preço Máximo\",\"hintText\":\"Informe o Valor Preço Máximo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_PAUTA_FISCAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Pauta Fiscal\",\"labelText\":\"Valor Pauta Fiscal\",\"tooltip\":\"Valor Pauta Fiscal\",\"hintText\":\"Informe o Valor da Pauta Fiscal\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_CONFIG_OF_GT_PIS` (`ID_TRIBUT_CONFIGURA_OF_GT` ASC),
  CONSTRAINT `fk_{6400E21E-AE1E-47EB-9DD4-FD8D4F5F00B9}`
    FOREIGN KEY (`ID_TRIBUT_CONFIGURA_OF_GT`)
    REFERENCES `fenix`.`TRIBUT_CONFIGURA_OF_GT` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'O objetivo é relacionar para cada CST_PIS um codigo de apuração conforme a tabela acima, informando ainda a modalidade calculo do PIS que pode ser por percentual ou por unidade de produto.\nCom esta tabela podemos armazenar as inúmeras formas de calculo, combinada com suas respectivas alíquotas e CST_PIS possíves e imagináveis que existem ou venha a existir na nossa complexa legislação.\nDesta forma, podemos fazer este vínculo em TABELA DE GRUPO_TRIBUTACAO e depois vincular este ID_GRUPO_TRIBUTACAO no cadastro de cada um dos produtosp ossibilitando um calculo totalmente automatizado em todo o sistema.\n'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`TRIBUT_COFINS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`TRIBUT_COFINS` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_TRIBUT_CONFIGURA_OF_GT` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CST_COFINS` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CST COFINS\",\"labelText\":\"CST COFINS\",\"tooltip\":\"CST COFINS\",\"hintText\":\"Informe o CST COFINS\",\"validacao\":\"\",\"campoLookup\":\"codigo\",\"tabelaLookup\":\"cst_cofins\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `EFD_TABELA_435` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"EFD 435\",\"labelText\":\"EFD 435\",\"tooltip\":\"EFD 435\",\"hintText\":\"Informe o EFD 435\",\"validacao\":\"\",\"campoLookup\":\"codigo\",\"tabelaLookup\":\"efd_435\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MODALIDADE_BASE_CALCULO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modalidade Base Cálculo\",\"labelText\":\"Modalidade Base Cálculo\",\"tooltip\":\"Modalidade Base Cálculo\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"0-Percentual\"},{\"dropDownButtonItem\":\"1-Unidade\"}]}}',
  `PORCENTO_BASE_CALCULO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Porcento Base Cálculo\",\"labelText\":\"Porcento Base Cálculo\",\"tooltip\":\"Porcento Base Cálculo\",\"hintText\":\"Informe o Porcento da Base de Cálculo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `ALIQUOTA_PORCENTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Porcento\",\"labelText\":\"Alíquota Porcento\",\"tooltip\":\"Alíquota Porcento\",\"hintText\":\"Informe a Alíquota do Porcento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `ALIQUOTA_UNIDADE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Unidade\",\"labelText\":\"Alíquota Unidade\",\"tooltip\":\"Alíquota Unidade\",\"hintText\":\"Informe a Alíquota da Unidade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_PRECO_MAXIMO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Preço Máximo\",\"labelText\":\"Valor Preço Máximo\",\"tooltip\":\"Valor Preço Máximo\",\"hintText\":\"Informe o Valor Preço Máximo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_PAUTA_FISCAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Pauta Fiscal\",\"labelText\":\"Valor Pauta Fiscal\",\"tooltip\":\"Valor Pauta Fiscal\",\"hintText\":\"Informe o Valor da Pauta Fiscal\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_CONFIG_OF_GT_COFINS` (`ID_TRIBUT_CONFIGURA_OF_GT` ASC),
  CONSTRAINT `fk_{F13F47B6-BC52-4363-9CE2-39CED083A615}`
    FOREIGN KEY (`ID_TRIBUT_CONFIGURA_OF_GT`)
    REFERENCES `fenix`.`TRIBUT_CONFIGURA_OF_GT` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'O objetivo é relacionar para cada CST_COFINS um codigo de apuração, informando ainda a modalidade calculo do COFINS que pode ser por percentual ou por unidade de produto.\nCom esta tabela podemos armazenar as inúmeras formas de calculo, combinada com suas respectivas alíquotas e CST_COFINS possíves e imagináveis que existem ou venha a existir na nossa complexa legislação.\nDesta forma, podemos fazer este vínculo em TABELA DE GRUPO_TRIBUTACAO e depois vincular este ID_GRUPO_TRIBUTACAO no cadastro de cada um dos produtos possibilitando um calculo totalmente automatizado em todo o sistema.\n'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`TRIBUT_IPI`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`TRIBUT_IPI` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_TRIBUT_CONFIGURA_OF_GT` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CST_IPI` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CST IPI\",\"labelText\":\"CST IPI\",\"tooltip\":\"CST IPI\",\"hintText\":\"Informe o CST IPI\",\"validacao\":\"\",\"campoLookup\":\"codigo\",\"tabelaLookup\":\"cst_ipi\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MODALIDADE_BASE_CALCULO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modalidade Base Cálculo\",\"labelText\":\"Modalidade Base Cálculo\",\"tooltip\":\"Modalidade Base Cálculo\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"0-Percentual\"},{\"dropDownButtonItem\":\"1-Unidade\"}]}}',
  `PORCENTO_BASE_CALCULO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Porcento Base Cálculo\",\"labelText\":\"Porcento Base Cálculo\",\"tooltip\":\"Porcento Base Cálculo\",\"hintText\":\"Informe o Porcento da Base de Cálculo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `ALIQUOTA_PORCENTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Porcento\",\"labelText\":\"Alíquota Porcento\",\"tooltip\":\"Alíquota Porcento\",\"hintText\":\"Informe a Alíquota do Porcento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `ALIQUOTA_UNIDADE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Unidade\",\"labelText\":\"Alíquota Unidade\",\"tooltip\":\"Alíquota Unidade\",\"hintText\":\"Informe a Alíquota da Unidade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_PRECO_MAXIMO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Preço Máximo\",\"labelText\":\"Valor Preço Máximo\",\"tooltip\":\"Valor Preço Máximo\",\"hintText\":\"Informe o Valor Preço Máximo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_PAUTA_FISCAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Pauta Fiscal\",\"labelText\":\"Valor Pauta Fiscal\",\"tooltip\":\"Valor Pauta Fiscal\",\"hintText\":\"Informe o Valor da Pauta Fiscal\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_CONFIG_OF_GT_IPI` (`ID_TRIBUT_CONFIGURA_OF_GT` ASC),
  CONSTRAINT `fk_{C143DDE4-2A66-4AD3-9C39-E7151907C43F}`
    FOREIGN KEY (`ID_TRIBUT_CONFIGURA_OF_GT`)
    REFERENCES `fenix`.`TRIBUT_CONFIGURA_OF_GT` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Esta tabela irá armazenar as tributações de IPI usadas pelas empresas.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`TRIBUT_ISS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`TRIBUT_ISS` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_TRIBUT_OPERACAO_FISCAL` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Operação Fiscal\",\"labelText\":\"Operação Fiscal\",\"tooltip\":\"Operação Fiscal\",\"hintText\":\"Importe a Operação Fiscal Vinculada\",\"validacao\":\"\",\"tabelaLookup\":\"tribut_operacao_fiscal\",\"campoLookup\":\"descricao\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MODALIDADE_BASE_CALCULO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modalidade Base Cálculo\",\"labelText\":\"Modalidade Base Cálculo\",\"tooltip\":\"Modalidade Base Cálculo\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"0-Valor Operação\"},{\"dropDownButtonItem\":\"9-Outros\"}]}}\n',
  `PORCENTO_BASE_CALCULO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Porcento Base Cálculo\",\"labelText\":\"Porcento Base Cálculo\",\"tooltip\":\"Porcento Base Cálculo\",\"hintText\":\"Informe o Porcento da Base de Cálculo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `ALIQUOTA_PORCENTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Porcento\",\"labelText\":\"Alíquota Porcento\",\"tooltip\":\"Alíquota Porcento\",\"hintText\":\"Informe a Alíquota do Porcento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `ALIQUOTA_UNIDADE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Unidade\",\"labelText\":\"Alíquota Unidade\",\"tooltip\":\"Alíquota Unidade\",\"hintText\":\"Informe a Alíquota da Unidade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_PRECO_MAXIMO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Preço Máximo\",\"labelText\":\"Valor Preço Máximo\",\"tooltip\":\"Valor Preço Máximo\",\"hintText\":\"Informe o Valor Preço Máximo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_PAUTA_FISCAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Pauta Fiscal\",\"labelText\":\"Valor Pauta Fiscal\",\"tooltip\":\"Valor Pauta Fiscal\",\"hintText\":\"Informe o Valor da Pauta Fiscal\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ITEM_LISTA_SERVICO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Item Lista Serviço\",\"labelText\":\"Item Lista Serviço\",\"tooltip\":\"Item Lista Serviço\",\"hintText\":\"Informe o Item da Lista de Serviço\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `CODIGO_TRIBUTACAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Tributação\",\"labelText\":\"Código Tributação\",\"tooltip\":\"Código Tributação\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Normal\"},{\"dropDownButtonItem\":\"Retida\"},{\"dropDownButtonItem\":\"Substituta\"},{\"dropDownButtonItem\":\"Isenta\"}]}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_TRIBUT_OP_FISCAL_ISS` (`ID_TRIBUT_OPERACAO_FISCAL` ASC),
  CONSTRAINT `fk_{63899E57-57EC-423C-9D38-DD9E673F4159}`
    FOREIGN KEY (`ID_TRIBUT_OPERACAO_FISCAL`)
    REFERENCES `fenix`.`TRIBUT_OPERACAO_FISCAL` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena os dados de ISS - Imposto Sobre Serviços.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`COMPRA_TIPO_REQUISICAO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`COMPRA_TIPO_REQUISICAO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Pagamento\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código do Tipo de Requisição\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"comentario\":\"01=Interna | 02=Externa\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME` VARCHAR(30) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o nome do tipo de requisição\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do tipo de requisição\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Armazena os tipos de requisição:\n\n01=INTERNA = requisição onde os itens são utilizados pela própria empresa\n02=EXTERNA = requisição onde os itens são utilizados para venda ao consumidor\n03=MISTA = requisição onde os itens podem ser utilizados de forma interna e também serão vendidos ao consumidor'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`COMPRA_REQUISICAO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`COMPRA_REQUISICAO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_COMPRA_TIPO_REQUISICAO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Requisição\",\"labelText\":\"Tipo Requisição\",\"tooltip\":\"Tipo Requisição\",\"hintText\":\"Importe o Tipo de Requisição Vinculado\",\"validacao\":\"\",\"tabelaLookup\":\"compra_tipo_requisicao\",\"campoLookup\":\"nome\",\"obrigatorio\":true,\"valorPadraoLookup\":\"%\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_COLABORADOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Colaborador\",\"labelText\":\"Colaborador\",\"tooltip\":\"Colaborador\",\"hintText\":\"Importe o Colaborador Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"colaborador\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição da Requisição\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_REQUISICAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data da Requisição\",\"labelText\":\"Data da Requisição\",\"tooltip\":\"Data da Requisição\",\"hintText\":\"Informe a Data da Requisição\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `OBSERVACAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_TIPO_REQ_COMPRA` (`ID_COMPRA_TIPO_REQUISICAO` ASC),
  INDEX `fk_COMPRA_REQUISICAO_COLABORADOR1_idx` (`ID_COLABORADOR` ASC),
  CONSTRAINT `fk_{38C90B1B-4612-4019-8BC4-DB19CB8591F3}`
    FOREIGN KEY (`ID_COMPRA_TIPO_REQUISICAO`)
    REFERENCES `fenix`.`COMPRA_TIPO_REQUISICAO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_COMPRA_REQUISICAO_COLABORADOR1`
    FOREIGN KEY (`ID_COLABORADOR`)
    REFERENCES `fenix`.`COLABORADOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena as requisições de compra. Uma mesma requisição pode gerar várias cotações no decorrer do tempo. É possível que um pedido padrão seja sempre realizado pela empresa, então basta deixa uma requisição salva e ficar fazendo cotações em cima dela ou até mesmo gerar um pedido diretamente a partir de uma requisição.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`COMPRA_REQUISICAO_DETALHE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`COMPRA_REQUISICAO_DETALHE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_COMPRA_REQUISICAO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_PRODUTO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Produto\",\"labelText\":\"Produto\",\"tooltip\":\"Produto\",\"hintText\":\"Importe o Produto Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"produto\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade\",\"labelText\":\"Quantidade\",\"tooltip\":\"Quantidade\",\"hintText\":\"Informe a Quantidade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_REQUISICAO_COMPRA_DETALHE` (`ID_COMPRA_REQUISICAO` ASC),
  INDEX `fk_COMPRA_REQUISICAO_DETALHE_PRODUTO1_idx` (`ID_PRODUTO` ASC),
  CONSTRAINT `fk_{ECA5555D-212D-4D7B-A79C-D5560C161C8F}`
    FOREIGN KEY (`ID_COMPRA_REQUISICAO`)
    REFERENCES `fenix`.`COMPRA_REQUISICAO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_COMPRA_REQUISICAO_DETALHE_PRODUTO1`
    FOREIGN KEY (`ID_PRODUTO`)
    REFERENCES `fenix`.`PRODUTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela com os detalhes da requisição de compra'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`COMPRA_COTACAO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`COMPRA_COTACAO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_COMPRA_REQUISICAO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Requisição\",\"labelText\":\"Requisição\",\"tooltip\":\"Requisição\",\"hintText\":\"Importe a Requisição Vinculada\",\"validacao\":\"\",\"tabelaLookup\":\"compra_requisicao\",\"campoLookup\":\"descricao\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_COTACAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data da Cotação\",\"labelText\":\"Data da Cotação\",\"tooltip\":\"Data da Cotação\",\"hintText\":\"Informe a Data da Cotação\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `DESCRICAO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição da Cotação\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_COMPRA_COTACAO_COMPRA_REQUISICAO1_idx` (`ID_COMPRA_REQUISICAO` ASC),
  CONSTRAINT `fk_COMPRA_COTACAO_COMPRA_REQUISICAO1`
    FOREIGN KEY (`ID_COMPRA_REQUISICAO`)
    REFERENCES `fenix`.`COMPRA_REQUISICAO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena as cotações de compra. Uma cotação pode ter vários fornecedores agregados a ela e pode gerar vários pedidos, se for o caso.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`COMPRA_FORNECEDOR_COTACAO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`COMPRA_FORNECEDOR_COTACAO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_COMPRA_COTACAO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_FORNECEDOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Fornecedor\",\"labelText\":\"Fornecedor\",\"tooltip\":\"Fornecedor\",\"hintText\":\"Importe o Fornecedor Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"fornecedor\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CODIGO` VARCHAR(32) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código da Cotação\",\"labelText\":\"Código da Cotação\",\"tooltip\":\"Código da Cotação\",\"hintText\":\"Código da Cotação Gerado Automaticamente\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Código MD5 gerado utilizando o ID da cotação juntamente com o ID do fornecedor. Esse código será armazenado no pedido, caso essa cotação gere algum pedido.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PRAZO_ENTREGA` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Prazo de Entrega\",\"labelText\":\"Prazo de Entrega\",\"tooltip\":\"Prazo de Entrega\",\"hintText\":\"Informe o Prazo de Entrega\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VENDA_CONDICOES_PAGAMENTO` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Condições de Pagamento\",\"labelText\":\"Condições de Pagamento\",\"tooltip\":\"Condições de Pagamento\",\"hintText\":\"Informe as Condições de Pagamento\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VALOR_SUBTOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Subtotal\",\"labelText\":\"Valor Subtotal\",\"tooltip\":\"Valor Subtotal\",\"hintText\":\"Informe o Valor Subtotal\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `TAXA_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Desconto\",\"labelText\":\"Taxa Desconto\",\"tooltip\":\"Taxa Desconto\",\"hintText\":\"Informe a Taxa de Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}\n',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto\",\"labelText\":\"Valor Desconto\",\"tooltip\":\"Valor Desconto\",\"hintText\":\"Informe o Valor do Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_TOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total\",\"labelText\":\"Valor Total\",\"tooltip\":\"Valor Total\",\"hintText\":\"Informe o Valor Total\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_COTACAO_FORNECEDOR` (`ID_COMPRA_COTACAO` ASC),
  INDEX `fk_COMPRA_FORNECEDOR_COTACAO_FORNECEDOR1_idx` (`ID_FORNECEDOR` ASC),
  CONSTRAINT `fk_{F28D90D4-59C0-49A3-8AF2-94BA6F6B55CE}`
    FOREIGN KEY (`ID_COMPRA_COTACAO`)
    REFERENCES `fenix`.`COMPRA_COTACAO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_COMPRA_FORNECEDOR_COTACAO_FORNECEDOR1`
    FOREIGN KEY (`ID_FORNECEDOR`)
    REFERENCES `fenix`.`FORNECEDOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Uma cotação pode ter vários fornecedores e um fornecedor pode fazer parte de várias cotações.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`COMPRA_COTACAO_DETALHE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`COMPRA_COTACAO_DETALHE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_COMPRA_FORNECEDOR_COTACAO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_PRODUTO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Produto\",\"labelText\":\"Produto\",\"tooltip\":\"Produto\",\"hintText\":\"Importe o Produto Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"produto\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade\",\"labelText\":\"Quantidade\",\"tooltip\":\"Quantidade\",\"hintText\":\"Informe a Quantidade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `VALOR_UNITARIO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Unitário\",\"labelText\":\"Valor Unitário\",\"tooltip\":\"Valor Unitário\",\"hintText\":\"Informe o Valor Unitário\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_SUBTOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Subtotal\",\"labelText\":\"Valor Subtotal\",\"tooltip\":\"Valor Subtotal\",\"hintText\":\"Informe o Valor Subtotal\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TAXA_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Desconto\",\"labelText\":\"Taxa Desconto\",\"tooltip\":\"Taxa Desconto\",\"hintText\":\"Informe a Taxa de Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto\",\"labelText\":\"Valor Desconto\",\"tooltip\":\"Valor Desconto\",\"hintText\":\"Informe o Valor do Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_TOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total\",\"labelText\":\"Valor Total\",\"tooltip\":\"Valor Total\",\"hintText\":\"Informe o Valor Total\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_FORNECEDOR_COTACAO_DETALHE` (`ID_COMPRA_FORNECEDOR_COTACAO` ASC),
  INDEX `fk_COMPRA_COTACAO_DETALHE_PRODUTO1_idx` (`ID_PRODUTO` ASC),
  CONSTRAINT `fk_{2DE08AA3-A7B9-41A3-A33E-5F3FBEF89674}`
    FOREIGN KEY (`ID_COMPRA_FORNECEDOR_COTACAO`)
    REFERENCES `fenix`.`COMPRA_FORNECEDOR_COTACAO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_COMPRA_COTACAO_DETALHE_PRODUTO1`
    FOREIGN KEY (`ID_PRODUTO`)
    REFERENCES `fenix`.`PRODUTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela que armazena os itens da cotação. Uma cotação é gerada a partir de uma requisição. Uma cotação pode gerar vários pedidos.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`COMPRA_TIPO_PEDIDO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`COMPRA_TIPO_PEDIDO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código do Pedido\",\"labelText\":\"Código do Pedido\",\"tooltip\":\"Código do Pedido\",\"hintText\":\"Informe o Código do Pedido\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"comentario\":\"01-Normal | 02-Planejado | 03-Aberto\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME` VARCHAR(30) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome do Pedido\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Pedido\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Pedido Normal: Quando nao existe nenhuma programacao de entrega ou seja, a entrega do material é feita de uma unica vez.\n\nPedido Planejado: O comprador negocia um determinado material mas não quer armazenar o consumo do ano em seu estoque, neste caso o comprador poderia fazer o pedido para o ano todo porém as entregas seriam mensais já que o comprador sabe o consumo mensal. \n\nPedido Aberto: O Comprador negocia um determinado material mas não sabe o consumo mensal durante o ano, neste caso ele gera liberacoes conforme necessidade.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`COMPRA_PEDIDO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`COMPRA_PEDIDO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_COMPRA_TIPO_PEDIDO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Pedido\",\"labelText\":\"Tipo Pedido\",\"tooltip\":\"Tipo Pedido\",\"hintText\":\"Importe o Tipo de Pedido Vinculado\",\"validacao\":\"\",\"tabelaLookup\":\"compra_tipo_pedido\",\"campoLookup\":\"nome\",\"valorPadraoLookup\":\"%\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_FORNECEDOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Fornecedor\",\"labelText\":\"Fornecedor\",\"tooltip\":\"Fornecedor\",\"hintText\":\"Importe o Fornecedor Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"fornecedor\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_COLABORADOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Colaborador\",\"labelText\":\"Colaborador\",\"tooltip\":\"Colaborador\",\"hintText\":\"Importe o Colaborador Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"colaborador\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_PEDIDO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data do Pedido\",\"labelText\":\"Data do Pedido\",\"tooltip\":\"Data do Pedido\",\"hintText\":\"Informe a Data do Pedido\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `DATA_PREVISTA_ENTREGA` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Prevista para Entrega\",\"labelText\":\"Data Prevista para Entrega\",\"tooltip\":\"Data Prevista para Entrega\",\"hintText\":\"Informe a Data Prevista para Entrega\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"now\",\"lastDate\":\"2050-01-01\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `DATA_PREVISAO_PAGAMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Previsão Pagamento\",\"labelText\":\"Data Previsão Pagamento\",\"tooltip\":\"Data Previsão Pagamento\",\"hintText\":\"Informe a Data de Previsão do Pagamento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"now\",\"lastDate\":\"2050-01-01\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `LOCAL_ENTREGA` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Local de Entrega\",\"labelText\":\"Local de Entrega\",\"tooltip\":\"Local de Entrega\",\"hintText\":\"Informe o Local de Entrega\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `LOCAL_COBRANCA` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Local de Cobrança\",\"labelText\":\"Local de Cobrança\",\"tooltip\":\"Local de Cobrança\",\"hintText\":\"Informe o Local de Cobrança\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CONTATO` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome do Contato\",\"labelText\":\"Nome do Contato\",\"tooltip\":\"Nome do Contato\",\"hintText\":\"Informe o Nome do Contato\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VALOR_SUBTOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Subtotal\",\"labelText\":\"Valor Subtotal\",\"tooltip\":\"Valor Subtotal\",\"hintText\":\"Informe o Valor Subtotal\",\"validacao\":\"\",\"readOnly\":true,\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TAXA_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Desconto\",\"labelText\":\"Taxa Desconto\",\"tooltip\":\"Taxa Desconto\",\"hintText\":\"Informe a Taxa de Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto\",\"labelText\":\"Valor Desconto\",\"tooltip\":\"Valor Desconto\",\"hintText\":\"Informe o Valor do Desconto\",\"validacao\":\"\",\"readOnly\":true,\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_TOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total\",\"labelText\":\"Valor Total\",\"tooltip\":\"Valor Total\",\"hintText\":\"Informe o Valor Total\",\"validacao\":\"\",\"readOnly\":true,\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TIPO_FRETE` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo do Frete\",\"labelText\":\"Tipo do Frete\",\"tooltip\":\"Tipo do Frete de Pessoa\",\"hintText\":\"Informe o Tipo do Frete\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"CIF\"},{\"dropDownButtonItem\":\"FOB\"}]}}',
  `FORMA_PAGAMENTO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Forma de Pagamento\",\"labelText\":\"Forma de Pagamento\",\"tooltip\":\"Forma de Pagamento de Pessoa\",\"hintText\":\"Informe o Forma de Pagamento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Campo indPag da NF-e\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"indice\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Pagamento a Vista\"},{\"dropDownButtonItem\":\"Pagamento a Prazo\"},{\"dropDownButtonItem\":\"Outros\"}]}}',
  `BASE_CALCULO_ICMS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Base Cálculo ICMS\",\"labelText\":\"Base Cálculo ICMS\",\"tooltip\":\"Base Cálculo ICMS\",\"hintText\":\"Informe a Base de Cálculo do ICMS\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_ICMS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor do ICMS\",\"labelText\":\"Valor do ICMS\",\"tooltip\":\"Valor do ICMS\",\"hintText\":\"Informe o Valor do ICMS\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `BASE_CALCULO_ICMS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Base Cálculo ICMS ST\",\"labelText\":\"Base Cálculo ICMS ST\",\"tooltip\":\"Base Cálculo ICMS ST\",\"hintText\":\"Informe a Base de Cálculo do ICMS ST\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_ICMS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor do ICMS ST\",\"labelText\":\"Valor do ICMS ST\",\"tooltip\":\"Valor do ICMS ST\",\"hintText\":\"Informe o Valor do ICMS ST\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_TOTAL_PRODUTOS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Produtos\",\"labelText\":\"Valor Total Produtos\",\"tooltip\":\"Valor Total Produtos\",\"hintText\":\"Informe o Valor Total Produtos\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_FRETE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Frete\",\"labelText\":\"Valor Frete\",\"tooltip\":\"Valor Frete\",\"hintText\":\"Informe o Valor do Frete\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_SEGURO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Seguro\",\"labelText\":\"Valor Seguro\",\"tooltip\":\"Valor Seguro\",\"hintText\":\"Informe o Valor do Seguro\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_OUTRAS_DESPESAS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Outras Despesas\",\"labelText\":\"Valor Outras Despesas\",\"tooltip\":\"Valor Outras Despesas\",\"hintText\":\"Informe o Valor de Outras Despesas\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_IPI` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor do IPI\",\"labelText\":\"Valor do IPI\",\"tooltip\":\"Valor do IPI\",\"hintText\":\"Informe o Valor do IPI\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_TOTAL_NF` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total NF\",\"labelText\":\"Valor Total NF\",\"tooltip\":\"Valor Total NF\",\"hintText\":\"Informe o Valor Total da NF\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `QUANTIDADE_PARCELAS` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade de Parcelas\",\"labelText\":\"Quantidade de Parcelas\",\"tooltip\":\"Quantidade de Parcelas\",\"hintText\":\"Informe a Quantidade de Parcelas\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `DIA_PRIMEIRO_VENCIMENTO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Dia Primeiro Vencimento\",\"labelText\":\"Dia Primeiro Vencimento\",\"tooltip\":\"Dia Primeiro Vencimento\",\"hintText\":\"Informe o Dia do Primeiro Vencimento\",\"validacao\":\"DIA\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"DIA\"}}',
  `INTERVALO_ENTRE_PARCELAS` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Intervalo entre Parcelas\",\"labelText\":\"Intervalo entre Parcelas\",\"tooltip\":\"Intervalo entre Parcelas\",\"hintText\":\"Informe a Intervalo entre as Parcelas\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DIA_FIXO_PARCELA` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Dia Fixo da Parcela\",\"labelText\":\"Dia Fixo da Parcela\",\"tooltip\":\"Dia Fixo da Parcela\",\"hintText\":\"Informe o Dia do Fixo da Parcela\",\"validacao\":\"DIA\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Permite informar um dia fixo para as parcelas que serão geradas no Contas a Pagar.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"DIA\"}}',
  `CODIGO_COTACAO` VARCHAR(32) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código da Cotação\",\"labelText\":\"Código da Cotação\",\"tooltip\":\"Código da Cotação\",\"hintText\":\"Informe o Código da Cotação\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Hash MD5 da cotação, caso esse pedido tenha sido gerado a partir de uma cotação.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_TIPO_PEDIDO_COMPRA` (`ID_COMPRA_TIPO_PEDIDO` ASC),
  INDEX `fk_COMPRA_PEDIDO_FORNECEDOR1_idx` (`ID_FORNECEDOR` ASC),
  INDEX `fk_COMPRA_PEDIDO_COLABORADOR1_idx` (`ID_COLABORADOR` ASC),
  CONSTRAINT `fk_{E9033C3C-AFA4-444D-8A06-165419B51DD3}`
    FOREIGN KEY (`ID_COMPRA_TIPO_PEDIDO`)
    REFERENCES `fenix`.`COMPRA_TIPO_PEDIDO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_COMPRA_PEDIDO_FORNECEDOR1`
    FOREIGN KEY (`ID_FORNECEDOR`)
    REFERENCES `fenix`.`FORNECEDOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_COMPRA_PEDIDO_COLABORADOR1`
    FOREIGN KEY (`ID_COLABORADOR`)
    REFERENCES `fenix`.`COLABORADOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela que armazena os pedidos de compra.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`COMPRA_PEDIDO_DETALHE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`COMPRA_PEDIDO_DETALHE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_COMPRA_PEDIDO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_PRODUTO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Produto\",\"labelText\":\"Produto\",\"tooltip\":\"Produto\",\"hintText\":\"Importe o Produto Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"produto\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade\",\"labelText\":\"Quantidade\",\"tooltip\":\"Quantidade\",\"hintText\":\"Informe a Quantidade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `VALOR_UNITARIO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Unitário\",\"labelText\":\"Valor Unitário\",\"tooltip\":\"Valor Unitário\",\"hintText\":\"Informe o Valor Unitário\",\"validacao\":\"\",\"readOnly\":true,\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_SUBTOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Subtotal\",\"labelText\":\"Valor Subtotal\",\"tooltip\":\"Valor Subtotal\",\"hintText\":\"Informe o Valor Subtotal\",\"validacao\":\"\",\"readOnly\":true,\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TAXA_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Desconto\",\"labelText\":\"Taxa Desconto\",\"tooltip\":\"Taxa Desconto\",\"hintText\":\"Informe a Taxa de Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto\",\"labelText\":\"Valor Desconto\",\"tooltip\":\"Valor Desconto\",\"hintText\":\"Informe o Valor do Desconto\",\"validacao\":\"\",\"readOnly\":true,\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_TOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total\",\"labelText\":\"Valor Total\",\"tooltip\":\"Valor Total\",\"hintText\":\"Informe o Valor Total\",\"validacao\":\"\",\"readOnly\":true,\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `CST` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CST\",\"labelText\":\"CST\",\"tooltip\":\"CST\",\"hintText\":\"Informe o CST\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cst_icms\",\"campoLookup\":\"codigo\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `CSOSN` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CSOSN\",\"labelText\":\"CSOSN\",\"tooltip\":\"CSOSN\",\"hintText\":\"Informe o CSOSN\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"csosn\",\"campoLookup\":\"codigo\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CFOP` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CFOP\",\"labelText\":\"CFOP\",\"tooltip\":\"CFOP\",\"hintText\":\"Informe o CFOP\",\"validacao\":\"\",\"campoLookup\":\"codigo\",\"tabelaLookup\":\"cfop\",\"campoLookupTipoDado\":\"int\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `BASE_CALCULO_ICMS` DECIMAL(18,6) NULL,
  `VALOR_ICMS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor do ICMS\",\"labelText\":\"Valor do ICMS\",\"tooltip\":\"Valor do ICMS\",\"hintText\":\"Informe o Valor do ICMS\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_IPI` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor do IPI\",\"labelText\":\"Valor do IPI\",\"tooltip\":\"Valor do IPI\",\"hintText\":\"Informe o Valor do IPI\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ALIQUOTA_ICMS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota do ICMS\",\"labelText\":\"Alíquota do ICMS\",\"tooltip\":\"Alíquota do ICMS\",\"hintText\":\"Informe o Alíquota do ICMS\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}\n',
  `ALIQUOTA_IPI` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota do IPI\",\"labelText\":\"Alíquota do IPI\",\"tooltip\":\"Alíquota do IPI\",\"hintText\":\"Informe o Alíquota do IPI\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `FK_PEDIDO_COMPRA_DETALHE` (`ID_COMPRA_PEDIDO` ASC),
  INDEX `fk_COMPRA_PEDIDO_DETALHE_PRODUTO1_idx` (`ID_PRODUTO` ASC),
  CONSTRAINT `fk_{F87F64FC-8F40-43C6-9A94-7E84A1551C3A}`
    FOREIGN KEY (`ID_COMPRA_PEDIDO`)
    REFERENCES `fenix`.`COMPRA_PEDIDO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_COMPRA_PEDIDO_DETALHE_PRODUTO1`
    FOREIGN KEY (`ID_PRODUTO`)
    REFERENCES `fenix`.`PRODUTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela que armazena os itens do pedido. Uma cotação poderá gerar vários pedidos.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`VENDA_CONDICOES_PAGAMENTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`VENDA_CONDICOES_PAGAMENTO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `NOME` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome da Condição\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"\",\"obrigatorio\":true,\"comentario\":\"Exemplo: BOLETO 20/40/60\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição da Condição\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Texto descritivo da condição de pagamento\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `FATURAMENTO_MINIMO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Faturamento Mínimo\",\"labelText\":\"Faturamento Mínimo\",\"tooltip\":\"Faturamento Mínimo\",\"hintText\":\"Informe o Faturamento Mínimo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Valor mínimo para determinado tipo de pagamento. Exemplo: boleto: delimitar o menor valor de uma venda.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `FATURAMENTO_MAXIMO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Faturamento Máximo\",\"labelText\":\"Faturamento Máximo\",\"tooltip\":\"Faturamento Máximo\",\"hintText\":\"Informe o Faturamento Máximo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Valor máximo para determinado tipo de pagamento. Exemplo: boleto: delimitar o maior valor de uma venda.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `INDICE_CORRECAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Índice de Correção\",\"labelText\":\"Índice de Correção\",\"tooltip\":\"Índice de Correção\",\"hintText\":\"Informe o Índice de Correção\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Índice para acréscimo nas vendas a prazo sobre o preço de tabela no formato 1 + %/ 100. Exemplo: acréscimo de 5% = 1,05. Padrão 1(=100%)\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `DIAS_TOLERANCIA` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Dias de Tolerância\",\"labelText\":\"Dias de Tolerância\",\"tooltip\":\"Dias de Tolerância\",\"hintText\":\"Informe o Número de Dias de Tolerância\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Quantidade de dias de flexibilidade das datas de vencimento dos boletos que podem ser ajustados pelo vendedor para antes ou depois da data definida. Exemplo: a 1ª parcela vence em 20 dias; se houver uma tolerância de 10 dias, o vendedor pode gerar um boleto para 10 até 30 dias. \",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `VALOR_TOLERANCIA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Tolerância\",\"labelText\":\"Valor Tolerância\",\"tooltip\":\"Valor Tolerância\",\"hintText\":\"Informe o Valor da Tolerância\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Permite o ajuste no valor das parcelas de acordo com a tolerância.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `PRAZO_MEDIO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Prazo Médio\",\"labelText\":\"Prazo Médio\",\"tooltip\":\"Prazo Médio\",\"hintText\":\"Prazo Médio\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Calculado pelo sistema de acordo com os prazos definidos para cada parcela. Exemplo: parcela 1: 30 dias, parcela 2: 60 dias, parcela 3: 90 dias. Prazo Médio: 30 dias.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `VISTA_PRAZO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Vista ou Prazo\",\"labelText\":\"Vista ou Prazo\",\"tooltip\":\"Vista ou Prazo\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Vista\"},{\"dropDownButtonItem\":\"Prazo\"}]}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Armazena as condições de pagamento.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`VENDA_ORCAMENTO_CABECALHO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`VENDA_ORCAMENTO_CABECALHO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_VENDA_CONDICOES_PAGAMENTO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Condição Pagamento\",\"labelText\":\"Condição  Pagamento\",\"tooltip\":\"Condição Pagamento\",\"hintText\":\"Importe a Condição de Pagamento Vinculada\",\"validacao\":\"\",\"tabelaLookup\":\"venda_condicoes_pagamento\",\"campoLookup\":\"nome\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_VENDEDOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Vendedor\",\"labelText\":\"Vendedor\",\"tooltip\":\"Vendedor\",\"hintText\":\"Importe o Vendedor Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"vendedor\",\"campoLookup\":\"colaborador?.pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_CLIENTE` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cliente\",\"labelText\":\"Cliente\",\"tooltip\":\"Cliente\",\"hintText\":\"Importe o Cliente Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cliente\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_TRANSPORTADORA` INT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Transportadora\",\"labelText\":\"Transportadora\",\"tooltip\":\"Transportadora\",\"hintText\":\"Importe a Transportadora Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"transportadora\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CODIGO` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código do Orçamento\",\"labelText\":\"Código do Orçamento\",\"tooltip\":\"Código do Orçamento\",\"hintText\":\"Informe o Código do Orçamento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_CADASTRO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Cadastro\",\"labelText\":\"Data de Cadastro\",\"tooltip\":\"Data de Cadastro\",\"hintText\":\"Informe a Data de Cadastro\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `DATA_ENTREGA` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Entrega\",\"labelText\":\"Data de Entrega\",\"tooltip\":\"Data de Entrega\",\"hintText\":\"Informe a Data de Entrega\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"2050-01-01\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `VALIDADE` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Validade\",\"labelText\":\"Data de Validade\",\"tooltip\":\"Data de Validade\",\"hintText\":\"Informe a Data de Validade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"2050-01-01\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `TIPO_FRETE` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo do Frete\",\"labelText\":\"Tipo do Frete\",\"tooltip\":\"Tipo do Frete de Pessoa\",\"hintText\":\"Informe o Tipo do Frete\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"CIF\"},{\"dropDownButtonItem\":\"FOB\"}]}}',
  `VALOR_SUBTOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Subtotal\",\"labelText\":\"Valor Subtotal\",\"tooltip\":\"Valor Subtotal\",\"hintText\":\"Informe o Valor Subtotal\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_FRETE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Frete\",\"labelText\":\"Valor Frete\",\"tooltip\":\"Valor Frete\",\"hintText\":\"Informe o Valor do Frete\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TAXA_COMISSAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Comissão\",\"labelText\":\"Taxa Comissão\",\"tooltip\":\"Taxa Comissão\",\"hintText\":\"Informe a Taxa de Comissão\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_COMISSAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Comissão\",\"labelText\":\"Valor Comissão\",\"tooltip\":\"Valor Comissão\",\"hintText\":\"Informe o Valor da Comissão\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `TAXA_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Desconto\",\"labelText\":\"Taxa Desconto\",\"tooltip\":\"Taxa Desconto\",\"hintText\":\"Informe a Taxa de Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto\",\"labelText\":\"Valor Desconto\",\"tooltip\":\"Valor Desconto\",\"hintText\":\"Informe o Valor do Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_TOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total\",\"labelText\":\"Valor Total\",\"tooltip\":\"Valor Total\",\"hintText\":\"Informe o Valor Total\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `OBSERVACAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_COND_PGTO_ORC_PED_VENDA` (`ID_VENDA_CONDICOES_PAGAMENTO` ASC),
  INDEX `fk_VENDA_ORCAMENTO_CABECALHO_VENDEDOR1_idx` (`ID_VENDEDOR` ASC),
  INDEX `fk_VENDA_ORCAMENTO_CABECALHO_CLIENTE1_idx` (`ID_CLIENTE` ASC),
  INDEX `fk_VENDA_ORCAMENTO_CABECALHO_TRANSPORTADORA1_idx` (`ID_TRANSPORTADORA` ASC),
  CONSTRAINT `fk_{013A2B4A-7CC7-4170-AE92-55132D36367E}`
    FOREIGN KEY (`ID_VENDA_CONDICOES_PAGAMENTO`)
    REFERENCES `fenix`.`VENDA_CONDICOES_PAGAMENTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_VENDA_ORCAMENTO_CABECALHO_VENDEDOR1`
    FOREIGN KEY (`ID_VENDEDOR`)
    REFERENCES `fenix`.`VENDEDOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_VENDA_ORCAMENTO_CABECALHO_CLIENTE1`
    FOREIGN KEY (`ID_CLIENTE`)
    REFERENCES `fenix`.`CLIENTE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_VENDA_ORCAMENTO_CABECALHO_TRANSPORTADORA1`
    FOREIGN KEY (`ID_TRANSPORTADORA`)
    REFERENCES `fenix`.`TRANSPORTADORA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena o cabeçalho do orçamento/pedido de venda.\n\nO usuário informa aqui as condições de pagamentos, mas não gera neste momento as parcelas. As mesmas só serão geradas no momento da confirmação da venda.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NOTA_FISCAL_MODELO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NOTA_FISCAL_MODELO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código do Modelo\",\"labelText\":\"Código do Modelo\",\"tooltip\":\"Código do Modelo\",\"hintText\":\"Informe o Código do Modelo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Modelo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MODELO` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modelo\",\"labelText\":\"Modelo\",\"tooltip\":\"Modelo\",\"hintText\":\"Informe o Modelo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Contém os modelos definidos no Ato Cotepe 46/10.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NOTA_FISCAL_TIPO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NOTA_FISCAL_TIPO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NOTA_FISCAL_MODELO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modelo NF\",\"labelText\":\"Modelo NF\",\"tooltip\":\"Modelo Nota Fiscal\",\"hintText\":\"Importe o Modelo da Nota Fiscal Vinculado\",\"validacao\":\"\",\"tabelaLookup\":\"nota_fiscal_modelo\",\"campoLookup\":\"descricao\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome do Tipo NF\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"\",\"obrigatorio\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Tipo NF\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `SERIE` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Série\",\"labelText\":\"Série\",\"tooltip\":\"Série\",\"hintText\":\"Informe a Série do Tipo da NF\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `SERIE_SCAN` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Série SCAN\",\"labelText\":\"Série SCAN\",\"tooltip\":\"Série SCAN\",\"hintText\":\"Informe a Série SCAN do Tipo da NF\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Sistema de Contingência do Ambiente Nacional – SCAN\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ULTIMO_NUMERO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Último Número\",\"labelText\":\"Último Número\",\"tooltip\":\"Último Número\",\"hintText\":\"Informe o Último Número\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Sistema controla o número do último documento impresso para determinada serie.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NF_TIPO_MODELO` (`ID_NOTA_FISCAL_MODELO` ASC),
  CONSTRAINT `fk_{5365C99B-97BA-432A-A1B4-5139D0F192AA}`
    FOREIGN KEY (`ID_NOTA_FISCAL_MODELO`)
    REFERENCES `fenix`.`NOTA_FISCAL_MODELO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena os tipos de NF utilizados pela empresa e controla a numeração das notas.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`VENDA_ORCAMENTO_DETALHE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`VENDA_ORCAMENTO_DETALHE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_VENDA_ORCAMENTO_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_PRODUTO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Produto\",\"labelText\":\"Produto\",\"tooltip\":\"Produto\",\"hintText\":\"Importe o Produto Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"produto\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade\",\"labelText\":\"Quantidade\",\"tooltip\":\"Quantidade\",\"hintText\":\"Informe a Quantidade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `VALOR_UNITARIO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Unitário\",\"labelText\":\"Valor Unitário\",\"tooltip\":\"Valor Unitário\",\"hintText\":\"Informe o Valor Unitário\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_SUBTOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Subtotal\",\"labelText\":\"Valor Subtotal\",\"tooltip\":\"Valor Subtotal\",\"hintText\":\"Informe o Valor Subtotal\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TAXA_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Desconto\",\"labelText\":\"Taxa Desconto\",\"tooltip\":\"Taxa Desconto\",\"hintText\":\"Informe a Taxa de Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto\",\"labelText\":\"Valor Desconto\",\"tooltip\":\"Valor Desconto\",\"hintText\":\"Informe o Valor do Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_TOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total\",\"labelText\":\"Valor Total\",\"tooltip\":\"Valor Total\",\"hintText\":\"Informe o Valor Total\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_VENDA_ORCAMENTO_CAB_DET` (`ID_VENDA_ORCAMENTO_CABECALHO` ASC),
  INDEX `fk_VENDA_ORCAMENTO_DETALHE_PRODUTO1_idx` (`ID_PRODUTO` ASC),
  CONSTRAINT `fk_{E312F633-27BF-4104-95C5-4A093136448D}`
    FOREIGN KEY (`ID_VENDA_ORCAMENTO_CABECALHO`)
    REFERENCES `fenix`.`VENDA_ORCAMENTO_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_VENDA_ORCAMENTO_DETALHE_PRODUTO1`
    FOREIGN KEY (`ID_PRODUTO`)
    REFERENCES `fenix`.`PRODUTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena os itens do orçamento de venda'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`VENDA_CABECALHO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`VENDA_CABECALHO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_VENDA_ORCAMENTO_CABECALHO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Orçamento\",\"labelText\":\"Orçamento\",\"tooltip\":\"Orçamento\",\"hintText\":\"Importe o Orçamento Vinculado\",\"validacao\":\"\",\"tabelaLookup\":\"venda_orcamento_cabecalho\",\"campoLookup\":\"codigo\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_VENDA_CONDICOES_PAGAMENTO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Condição Pagamento\",\"labelText\":\"Condição  Pagamento\",\"tooltip\":\"Condição Pagamento\",\"hintText\":\"Importe a Condição de Pagamento Vinculada\",\"validacao\":\"\",\"tabelaLookup\":\"venda_condicoes_pagamento\",\"campoLookup\":\"nome\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_NOTA_FISCAL_TIPO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Nota Fiscal\",\"labelText\":\"Tipo Nota Fiscal\",\"tooltip\":\"Tipo Nota Fiscal\",\"hintText\":\"Importe o Tipo da Nota Fiscal Vinculada\",\"validacao\":\"\",\"tabelaLookup\":\"nota_fiscal_tipo\",\"campoLookup\":\"nome\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_CLIENTE` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cliente\",\"labelText\":\"Cliente\",\"tooltip\":\"Cliente\",\"hintText\":\"Importe o Cliente Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cliente\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_TRANSPORTADORA` INT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Transportadora\",\"labelText\":\"Transportadora\",\"tooltip\":\"Transportadora\",\"hintText\":\"Importe a Transportadora Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"transportadora\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_VENDEDOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Vendedor\",\"labelText\":\"Vendedor\",\"tooltip\":\"Vendedor\",\"hintText\":\"Importe o Vendedor Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"vendedor\",\"campoLookup\":\"colaborador?.pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_VENDA` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data da Venda\",\"labelText\":\"Data da Venda\",\"tooltip\":\"Data da Venda\",\"hintText\":\"Informe a Data da Venda\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"2050-01-01\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `DATA_SAIDA` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data da Saída\",\"labelText\":\"Data da Saída\",\"tooltip\":\"Data da Saída\",\"hintText\":\"Informe a Data da Saída\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"2050-01-01\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `HORA_SAIDA` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Hora da Saída\",\"labelText\":\"Hora da Saída\",\"tooltip\":\"Hora da Saída\",\"hintText\":\"Informe a Hora da Saída\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"HORA\"}}',
  `NUMERO_FATURA` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número da Fatura\",\"labelText\":\"Número da Fatura\",\"tooltip\":\"Número da Fatura\",\"hintText\":\"Informe o Número da Fatura\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `LOCAL_ENTREGA` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Local de Entrega\",\"labelText\":\"Local de Entrega\",\"tooltip\":\"Local de Entrega\",\"hintText\":\"Informe o Local de Entrega\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `LOCAL_COBRANCA` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Local de Cobrança\",\"labelText\":\"Local de Cobrança\",\"tooltip\":\"Local de Cobrança\",\"hintText\":\"Informe o Local de Cobrança\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VALOR_SUBTOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Subtotal\",\"labelText\":\"Valor Subtotal\",\"tooltip\":\"Valor Subtotal\",\"hintText\":\"Informe o Valor Subtotal\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TAXA_COMISSAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Comissão\",\"labelText\":\"Taxa Comissão\",\"tooltip\":\"Taxa Comissão\",\"hintText\":\"Informe a Taxa de Comissão\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_COMISSAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Comissão\",\"labelText\":\"Valor Comissão\",\"tooltip\":\"Valor Comissão\",\"hintText\":\"Informe o Valor da Comissão\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TAXA_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Desconto\",\"labelText\":\"Taxa Desconto\",\"tooltip\":\"Taxa Desconto\",\"hintText\":\"Informe a Taxa de Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto\",\"labelText\":\"Valor Desconto\",\"tooltip\":\"Valor Desconto\",\"hintText\":\"Informe o Valor do Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_TOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total\",\"labelText\":\"Valor Total\",\"tooltip\":\"Valor Total\",\"hintText\":\"Informe o Valor Total\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TIPO_FRETE` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo do Frete\",\"labelText\":\"Tipo do Frete\",\"tooltip\":\"Tipo do Frete\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"CIF\"},{\"dropDownButtonItem\":\"FOB\"}]}}',
  `FORMA_PAGAMENTO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Forma de Pagamento\",\"labelText\":\"Forma de Pagamento\",\"tooltip\":\"Forma de Pagamento de Pessoa\",\"hintText\":\"Informe o Forma de Pagamento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Campo indPag da NF-e\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"indice\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Pagamento a Vista\"},{\"dropDownButtonItem\":\"Pagamento a Prazo\"},{\"dropDownButtonItem\":\"Outros\"}]}}',
  `VALOR_FRETE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Frete\",\"labelText\":\"Valor Frete\",\"tooltip\":\"Valor Frete\",\"hintText\":\"Informe o Valor do Frete\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_SEGURO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Seguro\",\"labelText\":\"Valor Seguro\",\"tooltip\":\"Valor Seguro\",\"hintText\":\"Informe o Valor do Seguro\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `OBSERVACAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `SITUACAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Situação\",\"labelText\":\"Situação\",\"tooltip\":\"Situação\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"indice\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Digitação\"},{\"dropDownButtonItem\":\"Produção\"},{\"dropDownButtonItem\":\"Expedição\"},{\"dropDownButtonItem\":\"Faturado\"},{\"dropDownButtonItem\":\"Entregue\"},{\"dropDownButtonItem\":\"Devolução\"}]}}',
  `DIA_FIXO_PARCELA` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Dia Fixo da Parcela\",\"labelText\":\"Dia Fixo da Parcela\",\"tooltip\":\"Dia Fixo da Parcela\",\"hintText\":\"Informe o Dia Fixo da Parcela\",\"validacao\":\"\",\"campoLookup\":\"dia\",\"tabelaLookup\":\"dia_parcela\",\"obrigatorio\":false,\"comentario\":\"Permite informar um dia fixo para as parcelas que serão geradas no Contas a Receber.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_ORCAMENTO_VENDA` (`ID_VENDA_ORCAMENTO_CABECALHO` ASC),
  INDEX `FK_VENDA_CAB_CONDICOES` (`ID_VENDA_CONDICOES_PAGAMENTO` ASC),
  INDEX `FK_TIPO_NF_VENDA_CAB` (`ID_NOTA_FISCAL_TIPO` ASC),
  INDEX `fk_VENDA_CABECALHO_CLIENTE1_idx` (`ID_CLIENTE` ASC),
  INDEX `fk_VENDA_CABECALHO_TRANSPORTADORA1_idx` (`ID_TRANSPORTADORA` ASC),
  INDEX `fk_VENDA_CABECALHO_VENDEDOR1_idx` (`ID_VENDEDOR` ASC),
  CONSTRAINT `fk_{0B6B64FC-0B7F-439D-9BC9-FE77D46D7A07}`
    FOREIGN KEY (`ID_VENDA_ORCAMENTO_CABECALHO`)
    REFERENCES `fenix`.`VENDA_ORCAMENTO_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_{67810BBB-E8C9-4BC1-AA83-E96DC12A98E1}`
    FOREIGN KEY (`ID_VENDA_CONDICOES_PAGAMENTO`)
    REFERENCES `fenix`.`VENDA_CONDICOES_PAGAMENTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_{91559D47-8B0E-4A62-ACEB-4953401CE72A}`
    FOREIGN KEY (`ID_NOTA_FISCAL_TIPO`)
    REFERENCES `fenix`.`NOTA_FISCAL_TIPO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_VENDA_CABECALHO_CLIENTE1`
    FOREIGN KEY (`ID_CLIENTE`)
    REFERENCES `fenix`.`CLIENTE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_VENDA_CABECALHO_TRANSPORTADORA1`
    FOREIGN KEY (`ID_TRANSPORTADORA`)
    REFERENCES `fenix`.`TRANSPORTADORA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_VENDA_CABECALHO_VENDEDOR1`
    FOREIGN KEY (`ID_VENDEDOR`)
    REFERENCES `fenix`.`VENDEDOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela que armazena o cabeçalho das vendas do sistema.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`VENDA_DETALHE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`VENDA_DETALHE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_VENDA_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_PRODUTO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Produto\",\"labelText\":\"Produto\",\"tooltip\":\"Produto\",\"hintText\":\"Importe o Produto Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"produto\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade\",\"labelText\":\"Quantidade\",\"tooltip\":\"Quantidade\",\"hintText\":\"Informe a Quantidade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `VALOR_UNITARIO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Unitário\",\"labelText\":\"Valor Unitário\",\"tooltip\":\"Valor Unitário\",\"hintText\":\"Informe o Valor Unitário\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_SUBTOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Subtotal\",\"labelText\":\"Valor Subtotal\",\"tooltip\":\"Valor Subtotal\",\"hintText\":\"Informe o Valor Subtotal\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TAXA_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Desconto\",\"labelText\":\"Taxa Desconto\",\"tooltip\":\"Taxa Desconto\",\"hintText\":\"Informe a Taxa de Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto\",\"labelText\":\"Valor Desconto\",\"tooltip\":\"Valor Desconto\",\"hintText\":\"Informe o Valor do Desconto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_TOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total\",\"labelText\":\"Valor Total\",\"tooltip\":\"Valor Total\",\"hintText\":\"Informe o Valor Total\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_VENDA_CAB_DET` (`ID_VENDA_CABECALHO` ASC),
  INDEX `fk_VENDA_DETALHE_PRODUTO1_idx` (`ID_PRODUTO` ASC),
  CONSTRAINT `fk_{69C29C33-DFCD-49FA-8F5F-105E19884FEE}`
    FOREIGN KEY (`ID_VENDA_CABECALHO`)
    REFERENCES `fenix`.`VENDA_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_VENDA_DETALHE_PRODUTO1`
    FOREIGN KEY (`ID_PRODUTO`)
    REFERENCES `fenix`.`PRODUTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena os itens da venda.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`VENDA_CONDICOES_PARCELAS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`VENDA_CONDICOES_PARCELAS` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_VENDA_CONDICOES_PAGAMENTO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `PARCELA` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número da Parcela\",\"labelText\":\"Número da Parcela\",\"tooltip\":\"Número da Parcela\",\"hintText\":\"Informe a Número da Parcela\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `DIAS` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Dias\",\"labelText\":\"Dias\",\"tooltip\":\"Dias\",\"hintText\":\"Informe a Quantidade de Dias\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"Quantidade de dias a partir da data da venda\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `TAXA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa da Parcela\",\"labelText\":\"Taxa da Parcela\",\"tooltip\":\"Taxa da Parcela\",\"hintText\":\"Informe a Taxa Refereente à Parcela\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_CONDICOES_PARCELAS` (`ID_VENDA_CONDICOES_PAGAMENTO` ASC),
  CONSTRAINT `fk_{BED0D350-FF66-4FF7-8CE2-3CEE91CB7EBC}`
    FOREIGN KEY (`ID_VENDA_CONDICOES_PAGAMENTO`)
    REFERENCES `fenix`.`VENDA_CONDICOES_PAGAMENTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela matriz para geração de parcelas.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`VENDA_FRETE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`VENDA_FRETE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_VENDA_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Venda\",\"labelText\":\"Venda\",\"tooltip\":\"Venda\",\"hintText\":\"Importe a Venda Vinculada\",\"validacao\":\"\",\"tabelaLookup\":\"venda_cabecalho\",\"campoLookup\":\"id\",\"campoLookupTipoDado\":\"int\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_TRANSPORTADORA` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Transportadora\",\"labelText\":\"Transportadora\",\"tooltip\":\"Transportadora\",\"hintText\":\"Importe a Transportadora Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"transportadora\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CONHECIMENTO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Conhecimento\",\"labelText\":\"Conhecimento\",\"tooltip\":\"Conhecimento\",\"hintText\":\"Informe o Número do Conhecimento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Número do conhecimento de frete\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `RESPONSAVEL` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Responsável\",\"labelText\":\"Responsável\",\"tooltip\":\"Responsável\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"1-Emitente\"},{\"dropDownButtonItem\":\"2-Destinatário\"}]}}',
  `PLACA` VARCHAR(7) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Placa\",\"labelText\":\"Placa\",\"tooltip\":\"Placa\",\"hintText\":\"Informe a Placa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `UF_PLACA` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF da Placa\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}\n',
  `SELO_FISCAL` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Selo Fiscal\",\"labelText\":\"Selo Fiscal\",\"tooltip\":\"Selo Fiscal\",\"hintText\":\"Informe o Número do Selo Fiscal\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `QUANTIDADE_VOLUME` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade de Volumes\",\"labelText\":\"Quantidade de Volumes\",\"tooltip\":\"Quantidade de Volumes\",\"hintText\":\"Informe a Quantidade de Volumes\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Quantidade de volumes da carga\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `MARCA_VOLUME` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Marca Volume\",\"labelText\":\"Marca Volume\",\"tooltip\":\"Marca Volume\",\"hintText\":\"Informe a Marca da Carga\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ESPECIE_VOLUME` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Espécie do Volume\",\"labelText\":\"Espécie do Volume\",\"tooltip\":\"Espécie do Volume\",\"hintText\":\"Informe a Espécie do Volume\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PESO_BRUTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Peso Bruto\",\"labelText\":\"Peso Bruto\",\"tooltip\":\"Peso Bruto\",\"hintText\":\"Informe o Peso Bruto\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `PESO_LIQUIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Peso Líquido\",\"labelText\":\"Peso Líquido\",\"tooltip\":\"Peso Líquido\",\"hintText\":\"Informe o Peso Líquido\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_VENDA_CABECALHO_FRETE` (`ID_VENDA_CABECALHO` ASC),
  INDEX `fk_VENDA_FRETE_TRANSPORTADORA1_idx` (`ID_TRANSPORTADORA` ASC),
  CONSTRAINT `fk_{1ABD2167-8D93-47C8-B224-4799D3AC3986}`
    FOREIGN KEY (`ID_VENDA_CABECALHO`)
    REFERENCES `fenix`.`VENDA_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_VENDA_FRETE_TRANSPORTADORA1`
    FOREIGN KEY (`ID_TRANSPORTADORA`)
    REFERENCES `fenix`.`TRANSPORTADORA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena os dados de frete da venda'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`VENDA_COMISSAO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`VENDA_COMISSAO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_VENDA_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_VENDEDOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Vendedor\",\"labelText\":\"Vendedor\",\"tooltip\":\"Vendedor\",\"hintText\":\"Importe o Vendedor Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"vendedor\",\"campoLookup\":\"colaborador?.pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VALOR_VENDA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Venda\",\"labelText\":\"Valor Venda\",\"tooltip\":\"Valor Venda\",\"hintText\":\"Informe o Valor da Venda\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TIPO_CONTABIL` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Contábil\",\"labelText\":\"Tipo Contábil\",\"tooltip\":\"Tipo Contábil\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Indicar se este lançamento é a DEBITO ou a CREDITO. Ex. se houver uma devolucao de venda haverá um lançamento a DEBITO\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Crédito\"},{\"dropDownButtonItem\":\"Débito\"}]}}',
  `VALOR_COMISSAO` DECIMAL(18,6) ZEROFILL NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Comissão\",\"labelText\":\"Valor Comissão\",\"tooltip\":\"Valor Comissão\",\"hintText\":\"Informe o Valor da Comissão\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `SITUACAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Situação\",\"labelText\":\"Situação\",\"tooltip\":\"Situação\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Q=QUITADA quando a comisão foi efetivamente paga ao vendedor | A= ABERTO, a comissão ainda não foi paga ao vendedor\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Aberto\"},{\"dropDownButtonItem\":\"Quitado\"}]}}',
  `DATA_LANCAMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Lançamento\",\"labelText\":\"Data de Lançamento\",\"tooltip\":\"Data de Lançamento\",\"hintText\":\"Informe a Data de Lançamento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_VENDA_COMISSAO` (`ID_VENDA_CABECALHO` ASC),
  INDEX `fk_VENDA_COMISSAO_VENDEDOR1_idx` (`ID_VENDEDOR` ASC),
  CONSTRAINT `fk_{E8EEED49-135C-4572-A661-9D85DFB65356}`
    FOREIGN KEY (`ID_VENDA_CABECALHO`)
    REFERENCES `fenix`.`VENDA_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_VENDA_COMISSAO_VENDEDOR1`
    FOREIGN KEY (`ID_VENDEDOR`)
    REFERENCES `fenix`.`VENDEDOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Esta tabela deve armazenar as comissões calculadas em cada venda. Desta forma toda vez que houver uma venda com indicaçao de comissao este valor será lançado nesta tabela, com indicaçao do VALOR_VENDA, VALOR_COMISSAO, TIPO (liquidez ou faturamento), e indicador se o lançamento é a debito ou a credito, pois se houver uma devoluaçao desta venda deve haver o debito (estorno da comissao) mediante um lançamento de saldo DEVEDOR para aquele vendedor. Essa tabela é do primeiro ciclo e serve para calcular a comissão dessa forma. Existe a opção de utilizar as tabelas para gerenciamento das comissões através dos perfis cadastrados, feitas no segundo ciclo do ERP.\n'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`REQUISICAO_INTERNA_CABECALHO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`REQUISICAO_INTERNA_CABECALHO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_COLABORADOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Colaborador\",\"labelText\":\"Colaborador\",\"tooltip\":\"Colaborador\",\"hintText\":\"Importe o Colaborador Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"colaborador\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_REQUISICAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data da Requisição\",\"labelText\":\"Data da Requisição\",\"tooltip\":\"Data da Requisição\",\"hintText\":\"Informe a Data da Requisição\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `SITUACAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Situação da Requisição\",\"labelText\":\"Situação da Requisição\",\"tooltip\":\"Situação da Requisição\",\"hintText\":\"Informe o Situação da Requisição\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Aberta\"},{\"dropDownButtonItem\":\"Deferida\"},{\"dropDownButtonItem\":\"Indeferida\"}]}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_REQUISICAO_INTERNA_CABECALHO_COLABORADOR1_idx` (`ID_COLABORADOR` ASC),
  CONSTRAINT `fk_REQUISICAO_INTERNA_CABECALHO_COLABORADOR1`
    FOREIGN KEY (`ID_COLABORADOR`)
    REFERENCES `fenix`.`COLABORADOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela para armazenar os dados das requisições internas de produto. Os itens da requisição interna serão utilizados pela própria empresa. Após o deferimento da requisição, os itens serão baixados do estoque.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`REQUISICAO_INTERNA_DETALHE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`REQUISICAO_INTERNA_DETALHE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_REQUISICAO_INTERNA_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_PRODUTO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Produto\",\"labelText\":\"Produto\",\"tooltip\":\"Produto\",\"hintText\":\"Importe o Produto Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"produto\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE` DECIMAL(18,6) NOT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade\",\"labelText\":\"Quantidade\",\"tooltip\":\"Quantidade\",\"hintText\":\"Informe a Quantidade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_REQ_INTERNA_CAB_DET` (`ID_REQUISICAO_INTERNA_CABECALHO` ASC),
  INDEX `fk_REQUISICAO_INTERNA_DETALHE_PRODUTO1_idx` (`ID_PRODUTO` ASC),
  CONSTRAINT `fk_{34AD1B31-03E7-4FE2-80FC-9EB2E4AF025E}`
    FOREIGN KEY (`ID_REQUISICAO_INTERNA_CABECALHO`)
    REFERENCES `fenix`.`REQUISICAO_INTERNA_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_REQUISICAO_INTERNA_DETALHE_PRODUTO1`
    FOREIGN KEY (`ID_PRODUTO`)
    REFERENCES `fenix`.`PRODUTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela para armazenar os itens das requisições internas de produtos.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`ESTOQUE_REAJUSTE_CABECALHO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`ESTOQUE_REAJUSTE_CABECALHO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_COLABORADOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Colaborador\",\"labelText\":\"Colaborador\",\"tooltip\":\"Colaborador\",\"hintText\":\"Importe o Colaborador Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"colaborador\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_REAJUSTE` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data do Reajuste\",\"labelText\":\"Data do Reajuste\",\"tooltip\":\"Data do Reajuste\",\"hintText\":\"Informe a Data do Reajuste\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `TAXA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Reajuste\",\"labelText\":\"Taxa Reajuste\",\"tooltip\":\"Taxa Reajuste\",\"hintText\":\"Informe a Taxa de Reajuste\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}\n',
  `TIPO_REAJUSTE` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo do Reajuste\",\"labelText\":\"Tipo do Reajuste\",\"tooltip\":\"Tipo do Reajuste de Pessoa\",\"hintText\":\"Informe o Tipo do Reajuste\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Aumentar\"},{\"dropDownButtonItem\":\"Diminuir\"}]}}',
  `JUSTIFICATIVA` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Justificativa\",\"labelText\":\"Justificativa\",\"tooltip\":\"Justificativa\",\"hintText\":\"Informe a Justificativa para o Reajuste\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_ESTOQUE_REAJUSTE_CABECALHO_COLABORADOR1_idx` (`ID_COLABORADOR` ASC),
  CONSTRAINT `fk_ESTOQUE_REAJUSTE_CABECALHO_COLABORADOR1`
    FOREIGN KEY (`ID_COLABORADOR`)
    REFERENCES `fenix`.`COLABORADOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela para armazenar o histórico de reajustes realizados.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`ESTOQUE_REAJUSTE_DETALHE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`ESTOQUE_REAJUSTE_DETALHE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_ESTOQUE_REAJUSTE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_PRODUTO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Produto\",\"labelText\":\"Produto\",\"tooltip\":\"Produto\",\"hintText\":\"Importe o Produto Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"produto\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VALOR_ORIGINAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Original\",\"labelText\":\"Valor Original\",\"tooltip\":\"Valor Original\",\"hintText\":\"Valor Original\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_REAJUSTE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Reajustado\",\"labelText\":\"Valor Reajustado\",\"tooltip\":\"Valor Reajustado\",\"hintText\":\"Valor Reajustado\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_ESTOQUE_REAJUSTE_CAB_DET` (`ID_ESTOQUE_REAJUSTE_CABECALHO` ASC),
  INDEX `fk_ESTOQUE_REAJUSTE_DETALHE_PRODUTO1_idx` (`ID_PRODUTO` ASC),
  CONSTRAINT `fk_{4CC49A7E-B388-4C3C-8F2A-E307C07CE241}`
    FOREIGN KEY (`ID_ESTOQUE_REAJUSTE_CABECALHO`)
    REFERENCES `fenix`.`ESTOQUE_REAJUSTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ESTOQUE_REAJUSTE_DETALHE_PRODUTO1`
    FOREIGN KEY (`ID_PRODUTO`)
    REFERENCES `fenix`.`PRODUTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela para armazenar os produtos que sofreram reajuste.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`ESTOQUE_COR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`ESTOQUE_COR` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(4) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código da Cor\",\"labelText\":\"Código da Cor\",\"tooltip\":\"Código da Cor\",\"hintText\":\"Informe o Código da Cor\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome da Cor\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Controla a cor dos produtos.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`ESTOQUE_TAMANHO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`ESTOQUE_TAMANHO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(4) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código do Tamanho\",\"labelText\":\"Código do Tamanho\",\"tooltip\":\"Código do Tamanho\",\"hintText\":\"Informe o Código do Tamanho\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome do Tamanho\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ALTURA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Altura\",\"labelText\":\"Altura\",\"tooltip\":\"Altura\",\"hintText\":\"Informe a Altura\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `COMPRIMENTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Comprimento\",\"labelText\":\"Comprimento\",\"tooltip\":\"Comprimento\",\"hintText\":\"Informe o Comprimento\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `LARGURA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Largura\",\"labelText\":\"Largura\",\"tooltip\":\"Largura\",\"hintText\":\"Informe a Largura\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Controla o tamanho dos produtos.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`ESTOQUE_SABOR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`ESTOQUE_SABOR` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(4) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código da Sabor\",\"labelText\":\"Código da Sabor\",\"tooltip\":\"Código da Sabor\",\"hintText\":\"Informe o Código da Sabor\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome da Sabor\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Controla a cor dos produtos.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`ESTOQUE_MARCA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`ESTOQUE_MARCA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(4) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código da Marca\",\"labelText\":\"Código da Marca\",\"tooltip\":\"Código da Marca\",\"hintText\":\"Informe o Código da Marca\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome da Marca\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Controla a marca dos produtos, em termos de grade.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`ESTOQUE_GRADE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`ESTOQUE_GRADE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_ESTOQUE_MARCA` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Marca\",\"labelText\":\"Marca\",\"tooltip\":\"Marca\",\"hintText\":\"Importe a Marca Vinculada\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"nome\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_ESTOQUE_SABOR` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Sabor\",\"labelText\":\"Sabor\",\"tooltip\":\"Sabor\",\"hintText\":\"Importe o Sabor Vinculado\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"nome\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_ESTOQUE_TAMANHO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tamanho\",\"labelText\":\"Tamanho\",\"tooltip\":\"Tamanho\",\"hintText\":\"Importe o Tamanho Vinculado\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"nome\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_ESTOQUE_COR` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cor\",\"labelText\":\"Cor\",\"tooltip\":\"Cor\",\"hintText\":\"Importe a Cor Vinculada\",\"validacao\":\"Alfanumerico\",\"campoLookup\":\"nome\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_PRODUTO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Produto\",\"labelText\":\"Produto\",\"tooltip\":\"Produto\",\"hintText\":\"Importe o Produto Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"produto\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CODIGO` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código da Grade\",\"labelText\":\"Código da Grade\",\"tooltip\":\"Código da Grade\",\"hintText\":\"Informe o Código da Grade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"SKU\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade\",\"labelText\":\"Quantidade\",\"tooltip\":\"Quantidade\",\"hintText\":\"Informe a Quantidade\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_ESTOQUE_COR_GRADE` (`ID_ESTOQUE_COR` ASC),
  INDEX `FK_ESTOQUE_TAMANHO_GRADE` (`ID_ESTOQUE_TAMANHO` ASC),
  INDEX `FK_ESTOQUE_SABOR` (`ID_ESTOQUE_SABOR` ASC),
  INDEX `FK_ESTOQUE_MARCA` (`ID_ESTOQUE_MARCA` ASC),
  INDEX `fk_ESTOQUE_GRADE_PRODUTO1_idx` (`ID_PRODUTO` ASC),
  CONSTRAINT `fk_{B599518C-2BAD-4EE0-A146-E41754BEE49D}`
    FOREIGN KEY (`ID_ESTOQUE_COR`)
    REFERENCES `fenix`.`ESTOQUE_COR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_{08CB887D-2458-4189-957A-2B8DC12F08C4}`
    FOREIGN KEY (`ID_ESTOQUE_TAMANHO`)
    REFERENCES `fenix`.`ESTOQUE_TAMANHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_{F4C36775-73AD-4D90-AB42-1DB3E5EDF85D}`
    FOREIGN KEY (`ID_ESTOQUE_SABOR`)
    REFERENCES `fenix`.`ESTOQUE_SABOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_{5E639E1C-5C60-46BE-A862-D3802D631C3C}`
    FOREIGN KEY (`ID_ESTOQUE_MARCA`)
    REFERENCES `fenix`.`ESTOQUE_MARCA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ESTOQUE_GRADE_PRODUTO1`
    FOREIGN KEY (`ID_PRODUTO`)
    REFERENCES `fenix`.`PRODUTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Faz o controle da grade de produtos. A quantidade em estoque é controlada na tabela PRODUTO, mas a quantidade em relação à grade deve/pode ser controlada nesse conjunto de tabelas.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`TRIBUT_ICMS_CUSTOM_DET`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`TRIBUT_ICMS_CUSTOM_DET` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_TRIBUT_ICMS_CUSTOM_CAB` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `UF_DESTINO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF de Destino\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"UF destino para cálculo do ICMS\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}',
  `CFOP` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CFOP\",\"labelText\":\"CFOP\",\"tooltip\":\"CFOP\",\"hintText\":\"Informe o CFOP\",\"validacao\":\"\",\"campoLookup\":\"codigo\",\"tabelaLookup\":\"cfop\",\"campoLookupTipoDado\":\"int\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CSOSN` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CSOSN\",\"labelText\":\"CSOSN\",\"tooltip\":\"CSOSN\",\"hintText\":\"Informe o CSOSN\",\"validacao\":\"\",\"campoLookup\":\"codigo\",\"tabelaLookup\":\"csosn\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CST` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CST\",\"labelText\":\"CST\",\"tooltip\":\"CST\",\"hintText\":\"Informe o CST\",\"validacao\":\"\",\"campoLookup\":\"codigo\",\"tabelaLookup\":\"cst_icms\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MODALIDADE_BC` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modalidade Base Cálculo\",\"labelText\":\"Modalidade Base Cálculo\",\"tooltip\":\"Modalidade Base Cálculo\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"0-Margem Valor Agregado (%); (para operações inter estados)  | 1-Pauta (Valor); (valor do produto)  | 2-Preço Tabelado Máx. (valor); (para setores da economia com valores tabelados, no caso de farmacias)  | 3-Valor da Operação. (no caso de serviço, onde o preço é calculado para a operação)  \",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"0-Margem Valor Agregado (%)\"},{\"dropDownButtonItem\":\"1-Pauta (Valor)\"},{\"dropDownButtonItem\":\"2-Preço Tabelado Máx. (valor)\"},{\"dropDownButtonItem\":\"3-Valor da Operação\"}]}}\n',
  `ALIQUOTA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota\",\"labelText\":\"Alíquota\",\"tooltip\":\"Alíquota\",\"hintText\":\"Informe o Alíquota caso Modalidade BC=3\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_PAUTA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Pauta\",\"labelText\":\"Valor Pauta\",\"tooltip\":\"Valor Pauta\",\"hintText\":\"Informe o Valor Pauta, caso Modalidade BC=1\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_PRECO_MAXIMO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Preço Máximo\",\"labelText\":\"Valor Preço Máximo\",\"tooltip\":\"Valor Preço Máximo\",\"hintText\":\"Informe o Valor Preço Máximo, caso Modalidade BC=2\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `MVA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor MVA\",\"labelText\":\"Valor MVA\",\"tooltip\":\"Valor MVA\",\"hintText\":\"Informe o Valor da Margem Valor Agregado\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `PORCENTO_BC` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Porcento Base Cálculo\",\"labelText\":\"Porcento Base Cálculo\",\"tooltip\":\"Porcento Base Cálculo\",\"hintText\":\"Informe o Porcentual da Base de Cálculo\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `MODALIDADE_BC_ST` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modalidade Base Cálculo ST\",\"labelText\":\"Modalidade Base Cálculo ST\",\"tooltip\":\"Modalidade Base Cálculo ST\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"0-Preço tabelado ou máximo sugerido; (no caso de medicamentos)  | 1-Lista Negativa (valor); (aplicado a medicamentos)  | 2-Lista Positiva (valor); (aplicado a medicamentos)  | 3-Lista Neutra (valor); (aplicado a medicamentos)  | 4-Margem Valor Agregado (%)  | 5-Pauta (valor); valor do produto\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"0-Preço tabelado ou máximo sugerido\"},{\"dropDownButtonItem\":\"1-Lista Negativa (valor)\"},{\"dropDownButtonItem\":\"2-Lista Positiva (valor)\"},{\"dropDownButtonItem\":\"3-Lista Neutra (valor)\"},{\"dropDownButtonItem\":\"4-Margem Valor Agregado (%)\"},{\"dropDownButtonItem\":\"5-Pauta (valor)\"}]}}',
  `ALIQUOTA_INTERNA_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Interna ST\",\"labelText\":\"Alíquota Interna ST\",\"tooltip\":\"Alíquota Interna ST\",\"hintText\":\"Informe a Alíquota Interna ST\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"Utilizado para cálculo em algumas situações do ICMS ST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `ALIQUOTA_INTERESTADUAL_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Interestadual ST\",\"labelText\":\"Alíquota Interestadual ST\",\"tooltip\":\"Alíquota Interestadual ST\",\"hintText\":\"Informe a Alíquota Interestadual ST\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `PORCENTO_BC_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Porcento Base Cálculo ST\",\"labelText\":\"Porcento Base Cálculo ST\",\"tooltip\":\"Porcento Base Cálculo ST\",\"hintText\":\"Informe o Porcentual da Base de Cálculo ST\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `ALIQUOTA_ICMS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota ICMS ST\",\"labelText\":\"Alíquota ICMS ST\",\"tooltip\":\"Alíquota ICMS ST\",\"hintText\":\"Informe o Alíquota ICMS ST\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_PAUTA_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Pauta ST\",\"labelText\":\"Valor Pauta ST\",\"tooltip\":\"Valor Pauta ST\",\"hintText\":\"Informe o Valor Pauta ST, caso Modalidade BC ST=5\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_PRECO_MAXIMO_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Preço Máximo ST\",\"labelText\":\"Valor Preço Máximo ST\",\"tooltip\":\"Valor Preço Máximo ST\",\"hintText\":\"Informe o Valor Preço Máximo ST, caso Modalidade BC ST=0\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_ICMS_CUSTOM_CAB_DET` (`ID_TRIBUT_ICMS_CUSTOM_CAB` ASC),
  CONSTRAINT `fk_{BD28B261-3A95-45B9-9E5F-59D51A1B2B0A}`
    FOREIGN KEY (`ID_TRIBUT_ICMS_CUSTOM_CAB`)
    REFERENCES `fenix`.`TRIBUT_ICMS_CUSTOM_CAB` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Caso a configuração do ICMS precise ser \'customizada\' para determinado produto, utiliza-se essa tabela e sua \'mãe\'.\n'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`DIA_PARCELA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`DIA_PARCELA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `DIA` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Dia\",\"labelText\":\"Dia\",\"tooltip\":\"Dia\",\"hintText\":\"Informe o Dia\",\"validacao\":\"DIA\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"DIA\"}}',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
COMMENT = 'Tabela que armazena os dias que podem ser utilizados para a geração de parcelas.';


-- -----------------------------------------------------
-- Table `fenix`.`NFE_CABECALHO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_CABECALHO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_VENDEDOR` INT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Vendedor\",\"labelText\":\"Vendedor\",\"tooltip\":\"Vendedor\",\"hintText\":\"Importe o Vendedor Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"vendedor\",\"campoLookup\":\"colaborador?.pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_FORNECEDOR` INT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Fornecedor\",\"labelText\":\"Fornecedor\",\"tooltip\":\"Fornecedor\",\"hintText\":\"Importe o Fornecedor Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"fornecedor\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_CLIENTE` INT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cliente\",\"labelText\":\"Cliente\",\"tooltip\":\"Cliente\",\"hintText\":\"Importe o Cliente Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cliente\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_TRIBUT_OPERACAO_FISCAL` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Operação Fiscal\",\"labelText\":\"Operação Fiscal\",\"tooltip\":\"Operação Fiscal\",\"hintText\":\"Importe a Operação Fiscal Vinculada\",\"validacao\":\"\",\"tabelaLookup\":\"tribut_operacao_fiscal\",\"campoLookup\":\"descricao\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_VENDA_CABECALHO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Venda\",\"labelText\":\"Venda\",\"tooltip\":\"Venda\",\"hintText\":\"Importe a Venda Vinculada\",\"validacao\":\"\",\"tabelaLookup\":\"venda_cabecalho\",\"campoLookup\":\"id\",\"campoLookupTipoDado\":\"int\",\"obrigatorio\":true,\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `UF_EMITENTE` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código UF\",\"labelText\":\"Código UF\",\"tooltip\":\"Código UF\",\"hintText\":\"Importe o Código UF\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"uf\",\"campoLookup\":\"codigoIbge\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"comentario\":\"B02 - cUF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CODIGO_NUMERICO` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Numérico\",\"labelText\":\"Código Numérico\",\"tooltip\":\"Código Numérico\",\"hintText\":\"Código Numérico - Gerado Automaticamente\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"B03 - cNF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NATUREZA_OPERACAO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Natureza da Operação\",\"labelText\":\"Natureza da Operação\",\"tooltip\":\"Natureza da Operação\",\"hintText\":\"Informe a Natureza da Operação\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"B04 - natOp\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CODIGO_MODELO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Modelo\",\"labelText\":\"Código Modelo\",\"tooltip\":\"Código Modelo\",\"hintText\":\"Informe o Código do Modelo\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":false,\"comentario\":\"B06 - mod\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `SERIE` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Série\",\"labelText\":\"Série\",\"tooltip\":\"Série\",\"hintText\":\"Informe a Série\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"B07 - serie\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO` VARCHAR(9) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Número - Gerado Automaticamente\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"B08 - nNF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_HORA_EMISSAO` TIMESTAMP NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data/Hora Emissão\",\"labelText\":\"Data/Hora Emissão\",\"tooltip\":\"Data/Hora Emissão\",\"hintText\":\"Informe a Data/Hora Emissão\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"B09 - dhEmi\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"dd-MM-yyyy hh:mm a\"}}',
  `DATA_HORA_ENTRADA_SAIDA` TIMESTAMP NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data/Hora Entrada/Saída\",\"labelText\":\"Data/Hora Entrada/Saída\",\"tooltip\":\"Data/Hora Entrada/Saída\",\"hintText\":\"Informe a Data/Hora Entrada/Saída\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"B10 - dhSaiEnt\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"dd-MM-yyyy hh:mm a\"}}',
  `TIPO_OPERACAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Operação\",\"labelText\":\"Tipo Operação\",\"tooltip\":\"Tipo Operação\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"B11 - tpNF\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"1=Saída\",\"itens\":[{\"dropDownButtonItem\":\"0=Entrada\"},{\"dropDownButtonItem\":\"1=Saída\"}]}}',
  `LOCAL_DESTINO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Local Destino\",\"labelText\":\"Local Destino\",\"tooltip\":\"Local Destino\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"B11a - idDest\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"1=Operação interna\",\"itens\":[{\"dropDownButtonItem\":\"1=Operação interna\"},{\"dropDownButtonItem\":\"2=Operação interestadual\"},{\"dropDownButtonItem\":\"3=Operação com exterior\"}]}}',
  `CODIGO_MUNICIPIO` INT UNSIGNED ZEROFILL NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município IBGE\",\"labelText\":\"Município IBGE\",\"tooltip\":\"Município IBGE\",\"hintText\":\"Importe o Município IBGE Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"municipio\",\"campoLookup\":\"codigoIbge\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"B12 - cMunFG\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `FORMATO_IMPRESSAO_DANFE` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Formato de Impressão do DANFE\",\"labelText\":\"Formato de Impressão do DANFE\",\"tooltip\":\"Formato de Impressão do DANFE\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"B21 - tpImp\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"1=DANFE normal, Retrato\",\"itens\":[{\"dropDownButtonItem\":\"0=Sem geração de DANFE\"},{\"dropDownButtonItem\":\"1=DANFE normal, Retrato\"},{\"dropDownButtonItem\":\"2=DANFE normal, Paisagem\"},{\"dropDownButtonItem\":\"3=DANFE Simplificado\"}]}}',
  `TIPO_EMISSAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo de Emissão\",\"labelText\":\"Tipo de Emissão\",\"tooltip\":\"Tipo de Emissão\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"B22 - tpEmis\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"1=Emissão normal\",\"itens\":[{\"dropDownButtonItem\":\"1=Emissão normal\"},{\"dropDownButtonItem\":\"2=Contingência FS-IA\"},{\"dropDownButtonItem\":\"4=Contingência EPEC\"},{\"dropDownButtonItem\":\"5=Contingência FS-DA\"},{\"dropDownButtonItem\":\"6=Contingência SVC-AN\"},{\"dropDownButtonItem\":\"7=Contingência SVC-RS\"}]}}',
  `CHAVE_ACESSO` VARCHAR(44) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Chave de Acesso\",\"labelText\":\"Chave de Acesso\",\"tooltip\":\"Chave de Acesso\",\"hintText\":\"Chave de Acesso - Gerado Automaticamente\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Chave de acesso da NF-e composta por  Código da UF + AAMM da emissão + CNPJ do  Emitente + Modelo, Série e Número da NFe +  Código Numérico + DV\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DIGITO_CHAVE_ACESSO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Dígito Chave de Acesso\",\"labelText\":\"Dígito Chave de Acesso\",\"tooltip\":\"Dígito Chave de Acesso\",\"hintText\":\"Dígito Chave de Acesso - Gerado Automaticamente\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"B23 - cDV\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `AMBIENTE` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Ambiente\",\"labelText\":\"Ambiente\",\"tooltip\":\"Ambiente\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":false,\"comentario\":\"B24 - tpAmb\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"1=Produção\",\"itens\":[{\"dropDownButtonItem\":\"1=Produção\"},{\"dropDownButtonItem\":\"2=Homologação\"}]}}',
  `FINALIDADE_EMISSAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Finalidade Emissão\",\"labelText\":\"Finalidade Emissão\",\"tooltip\":\"Finalidade Emissão\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"B25 - finNFe\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"1=NF-e normal\",\"itens\":[{\"dropDownButtonItem\":\"1=NF-e normal\"},{\"dropDownButtonItem\":\"2=NF-e complementar\"},{\"dropDownButtonItem\":\"3=NF-e de ajuste\"},{\"dropDownButtonItem\":\"4=Devolução de mercadoria\"}]}}',
  `CONSUMIDOR_OPERACAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Consumidor Final\",\"labelText\":\"Consumidor Final\",\"tooltip\":\"Consumidor Final\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"B25a - indFinal\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"0=Normal\",\"itens\":[{\"dropDownButtonItem\":\"0=Normal\"},{\"dropDownButtonItem\":\"1=Consumidor final\"}]}}',
  `CONSUMIDOR_PRESENCA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Indicador de Presença do Consumidor\",\"labelText\":\"Indicador de Presença do Consumidor\",\"tooltip\":\"Indicador de Presença do Consumidor\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"B25b - indPres\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"1=Operação presencial\",\"itens\":[{\"dropDownButtonItem\":\"0=Não se aplica\"},{\"dropDownButtonItem\":\"1=Operação presencial\"},{\"dropDownButtonItem\":\"2=Operação não presencial, pela Internet\"},{\"dropDownButtonItem\":\"3=Operação não presencial, Teleatendimento\"},{\"dropDownButtonItem\":\"4=NFC-e em operação com entrega a domicílio\"},{\"dropDownButtonItem\":\"5=Operação presencial, fora do estabelecimento\"},{\"dropDownButtonItem\":\"9=Operação não presencial, outros\"}]}}',
  `PROCESSO_EMISSAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Processo de Emissão\",\"labelText\":\"Processo de Emissão\",\"tooltip\":\"Processo de Emissão\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":false,\"comentario\":\"B26 - procEmi\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"0=Emissão de NF-e com aplicativo do contribuinte\",\"itens\":[{\"dropDownButtonItem\":\"0=Emissão de NF-e com aplicativo do contribuinte\"},{\"dropDownButtonItem\":\"1=Emissão de NF-e avulsa pelo Fisco\"},{\"dropDownButtonItem\":\"2=Emissão de NF-e avulsa, pelo contribuinte com seu certificado digital, através do site do Fisco\"},{\"dropDownButtonItem\":\"3=Emissão NF-e pelo contribuinte com aplicativo fornecido pelo Fisco\"}]}}',
  `VERSAO_PROCESSO_EMISSAO` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Versão Processo Emissão\",\"labelText\":\"Versão Processo Emissão\",\"tooltip\":\"Versão Processo Emissão\",\"hintText\":\"Versão Processo Emissão - Gerado Automaticamente\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":false,\"comentario\":\"B27 - verProc\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_ENTRADA_CONTINGENCIA` TIMESTAMP NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Contingência\",\"labelText\":\"Data Contingência\",\"tooltip\":\"Data Contingência\",\"hintText\":\"Informe a Data Contingência\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"B28 - dhCont\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"dd-MM-yyyy hh:mm a\"}}',
  `JUSTIFICATIVA_CONTINGENCIA` VARCHAR(255) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Justificativa Contingência\",\"labelText\":\"Justificativa Contingência\",\"tooltip\":\"Justificativa Contingência\",\"hintText\":\"Informe a Justificativa da Contingência\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"B29 - xJust\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `BASE_CALCULO_ICMS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Base Cálculo ICMS\",\"labelText\":\"Base Cálculo ICMS\",\"tooltip\":\"Base Cálculo ICMS\",\"hintText\":\"Informe a Base de Cálculo do ICMS\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W03 - vBC - informar o somatório da BC do ICMS (vBC) informado nos itens\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_ICMS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor do ICMS\",\"labelText\":\"Valor do ICMS\",\"tooltip\":\"Valor do ICMS\",\"hintText\":\"Informe o Valor do ICMS\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W04 - vICMS - informar o somatório de ICMS (vICMS) informado nos itens\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_ICMS_DESONERADO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor do ICMS Desonerado\",\"labelText\":\"Valor do ICMS Desonerado\",\"tooltip\":\"Valor do ICMS Desonerado\",\"hintText\":\"Informe o Valor do ICMS Desonerado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W04a - vICMSDeson - informar o somatório do Valor do ICMS desonerado (vICMSDeson) informado nos itens\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `TOTAL_ICMS_FCP_UF_DESTINO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Total ICMS FCP UF Destino\",\"labelText\":\"Total ICMS FCP UF Destino\",\"tooltip\":\"Total ICMS FCP UF Destino\",\"hintText\":\"Informe o Total ICMS FCP UF Destino\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W04c - vFCPUFDest - Corresponde ao total da soma dos campos vFCP informado nos itens\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `TOTAL_ICMS_INTERESTADUAL_UF_DESTINO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Total ICMS Interestadual UF Destino\",\"labelText\":\"Total ICMS Interestadual UF Destino\",\"tooltip\":\"Total ICMS Interestadual UF Destino\",\"hintText\":\"Informe o Total ICMS Interestadual UF Destino\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W04e - vICMSUFDest - informar o somatório do Valor do ICMS Interestadual para a UF de destino (vICMSUFDest) informado nos itens.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `TOTAL_ICMS_INTERESTADUAL_UF_REMENTE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Total ICMS Interestadual UF Remetente\",\"labelText\":\"Total ICMS Interestadual UF Remetente\",\"tooltip\":\"Total ICMS Interestadual UF Remetente\",\"hintText\":\"Informe o Total ICMS Interestadual UF Remetente\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":false,\"comentario\":\"W04g - vICMSUFRemet - informar o somatório do Valor total do ICMS Interestadual para a UF do remetente vICMSUFRemet) informado nos itens. Nota: A partir de 2019, este valor será zero.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_TOTAL_FCP` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Total FCP\",\"labelText\":\"Total FCP\",\"tooltip\":\"Total FCP\",\"hintText\":\"Informe o Total FCP - Fundo de Combate à Pobreza\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W04h - vFCP - informar o somatório do Valor do FCP (Fundo de Combate à Pobreza) (vFCP) informado nos itens.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `BASE_CALCULO_ICMS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Base Cálculo ICMS ST\",\"labelText\":\"Base Cálculo ICMS ST\",\"tooltip\":\"Base Cálculo ICMS ST\",\"hintText\":\"Informe a Base de Cálculo do ICMS ST\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W05 - vBCST - informar o somatório da BC ST (vBCST) informado nos itens\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_ICMS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor do ICMS ST\",\"labelText\":\"Valor do ICMS ST\",\"tooltip\":\"Valor do ICMS ST\",\"hintText\":\"Informe o Valor do ICMS ST\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W06 - vST - informar o somatório do ICMS ST (vICMSST) informado nos itens\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_TOTAL_FCP_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total FCP ST\",\"labelText\":\"Valor Total FCP ST\",\"tooltip\":\"Valor Total FCP ST\",\"hintText\":\"Informe o Valor Total FCP ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W06a - vFCPST - informar o somatório do Valor do FCP retido anteriormente por Substituição. Corresponde ao total da soma dos campos vFCPST informado nos itens.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_TOTAL_FCP_ST_RETIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total FCP ST Retido\",\"labelText\":\"Valor Total FCP ST Retido\",\"tooltip\":\"Valor Total FCP ST Retido\",\"hintText\":\"Informe o Valor Total FCP ST Retido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W06b - vFCPSTRet - informar o somatório do Valor do FCP retido anteriormente por Substituição. Corresponde ao total da soma dos campos vFCPSTRet informado nos itens.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_TOTAL_PRODUTOS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Produtos\",\"labelText\":\"Valor Total Produtos\",\"tooltip\":\"Valor Total Produtos\",\"hintText\":\"Informe o Valor Total Produtos\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W07 - vProd - informar o somatório de valor dos produtos (vProd) dos itens que tenham indicador de totalização = 1 (indTot). Os valores dos itens sujeitos ao ISSQN não devem ser acumulados neste campo.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_FRETE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Frete\",\"labelText\":\"Valor Frete\",\"tooltip\":\"Valor Frete\",\"hintText\":\"Informe o Valor Frete\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W08 - vFrete - informar o somatório de valor do Frete (vFrete) informado nos itens\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_SEGURO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Seguro\",\"labelText\":\"Valor Seguro\",\"tooltip\":\"Valor Seguro\",\"hintText\":\"Informe o Valor Seguro\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W09 - vSeg - informar o somatório valor do Seguro (vSeg) informado nos itens\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto\",\"labelText\":\"Valor Desconto\",\"tooltip\":\"Valor Desconto\",\"hintText\":\"Informe o Valor Desconto\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W10 - vDesc - informar o somatório do Desconto (vDesc) informado nos itens\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_IMPOSTO_IMPORTACAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Imposto Importação\",\"labelText\":\"Valor Imposto Importação\",\"tooltip\":\"Valor Imposto Importação\",\"hintText\":\"Informe o Valor Imposto Importação\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W11 - vII - informar o somatório de II (vII) informado nos itens\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_IPI` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor IPI\",\"labelText\":\"Valor IPI\",\"tooltip\":\"Valor IPI\",\"hintText\":\"Informe o Valor IPI\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W12 - vIPI - informar o somatório de IPI (vIPI) informado nos itens\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_IPI_DEVOLVIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor IPI Devolvido\",\"labelText\":\"Valor IPI Devolvido\",\"tooltip\":\"Valor IPI Devolvido\",\"hintText\":\"Informe o Valor IPI Devolvido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W12a - vIPIDevol - Corresponde ao total da soma dos campos vIIPIDevol do item.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_PIS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor PIS\",\"labelText\":\"Valor PIS\",\"tooltip\":\"Valor PIS\",\"hintText\":\"Informe o Valor PIS\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W13 - vPIS - informar o somatório de PIS (vPIS) informado nos itens sujeitos ao ICMS\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_COFINS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor COFINS\",\"labelText\":\"Valor COFINS\",\"tooltip\":\"Valor COFINS\",\"hintText\":\"Informe o Valor COFINS\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W14 - vCOFINS - informar o somatório de COFINS (vCOFINS) informado nos itens sujeitos ao ICMS\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_DESPESAS_ACESSORIAS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Despesas Acessórias\",\"labelText\":\"Valor Despesas Acessórias\",\"tooltip\":\"Valor Despesas Acessórias\",\"hintText\":\"Informe o Valor Despesas Acessórias\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W15 - vOutro - informar o somatório de vOutro (vOutro) informado nos itens\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_TOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total NF\",\"labelText\":\"Valor Total NF\",\"tooltip\":\"Valor Total NF\",\"hintText\":\"Informe o Valor Total NF\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W16 - vNF - Valor Total da NF-e [(+) vProd (id:W07)  (-) vDesc (id:W10)  (+) vICMSST (id:W06)  (+) vFrete (id:W09)  (+) vSeg (id:W10)  (+) vOutro (id:W15)  (+) vII (id:W11)  (+) vIPI (id:W12)  (+) vServ (id:W19) (NT 2011/004)]\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_TOTAL_TRIBUTOS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Tributos\",\"labelText\":\"Valor Total Tributos\",\"tooltip\":\"Valor Total Tributos\",\"hintText\":\"Informe o Valor Total Tributos\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W16a - vTotTrib - informar o somatório do valor total aproximado dos tributos (vTotTrib) informado nos itens, deve considerar valor de itens sujeitos ao ISSQN também.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_SERVICOS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Serviços\",\"labelText\":\"Valor Total Serviços\",\"tooltip\":\"Valor Total Serviços\",\"hintText\":\"Informe o Valor Total Serviços\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W18 - vServ - informar o valor total do Serviços Pretados, é o somatório dos valores informados em vProd dos itens sujeitos ao ISSQN. Os valores que sujeitos ao ISSQN deve ter o indTot informado com zero para evitar que o valor seja considerado na validação do somatório do vProd pela SEFAZ.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `BASE_CALCULO_ISSQN` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Base Cálculo ISSQN\",\"labelText\":\"Base Cálculo ISSQN\",\"tooltip\":\"Base Cálculo ISSQN\",\"hintText\":\"Informe o Base Cálculo ISSQN\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W19 - vBC - informar o somatório da BC do ISS informado nos itens de Serviços\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_ISSQN` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor ISSQN\",\"labelText\":\"Valor ISSQN\",\"tooltip\":\"Valor ISSQN\",\"hintText\":\"Informe o Valor ISSQN\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W20 - vISS - informar o somatório de ISS informado nos itens de Serviços\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_PIS_ISSQN` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor PIS ISSQN\",\"labelText\":\"Valor PIS ISSQN\",\"tooltip\":\"Valor PIS ISSQN\",\"hintText\":\"Informe o Valor PIS ISSQN\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W21 - vPIS - informar o somatório de PIS informado nos itens de Serviços\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_COFINS_ISSQN` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor COFINS ISSQN\",\"labelText\":\"Valor COFINS ISSQN\",\"tooltip\":\"Valor COFINS ISSQN\",\"hintText\":\"Informe o Valor COFINS ISSQN\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W22 - vCOFINS - informar o somatório de COFINS informado nos itens de Serviços\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `DATA_PRESTACAO_SERVICO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Prestação do Serviço\",\"labelText\":\"Data Prestação do Serviço\",\"tooltip\":\"Data Prestação do Serviço\",\"hintText\":\"Informe a Data de Prestação do Serviço\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W22a - dCompet\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `VALOR_DEDUCAO_ISSQN` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Dedução ISSQN\",\"labelText\":\"Valor Dedução ISSQN\",\"tooltip\":\"Valor Dedução ISSQN\",\"hintText\":\"Informe o Valor Dedução ISSQN\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W22b - vDeducao - informar o somatório do valor Valor total dedução para redução da Base de Cálculo (vDeducao) informado nos itens.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `OUTRAS_RETENCOES_ISSQN` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Outras Retenções ISSQN\",\"labelText\":\"Outras Retenções ISSQN\",\"tooltip\":\"Outras Retenções ISSQN\",\"hintText\":\"Informe o Outras Retenções ISSQN\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W22c - vOutro - informar o somatório do valor total Valor total outras retenções (vOutro) informado nos itens. Valor declaratório.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `DESCONTO_INCONDICIONADO_ISSQN` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto Incondicionado ISSQN\",\"labelText\":\"Valor Desconto Incondicionado ISSQN\",\"tooltip\":\"Valor Desconto Incondicionado ISSQN\",\"hintText\":\"Informe o Valor Desconto Incondicionado ISSQN\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W22d - vDescIncond - informar o somatório do Valor total desconto incondicionado (vDescIncond) informado nos itens.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `DESCONTO_CONDICIONADO_ISSQN` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto Condicionado ISSQN\",\"labelText\":\"Valor Desconto Condicionado ISSQN\",\"tooltip\":\"Valor Desconto Condicionado ISSQN\",\"hintText\":\"Informe o Valor Desconto Condicionado ISSQN\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W22e - vDescCond - informar o somatório do Valor total desconto condicionado (vDescCond) informado nos itens.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `TOTAL_RETENCAO_ISSQN` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Total Retenção ISSQN\",\"labelText\":\"Total Retenção ISSQN\",\"tooltip\":\"Total Retenção ISSQN\",\"hintText\":\"Informe o Total Retenção ISSQN\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W22f - vISSRet - informar o somatório do Valor total retenção ISS (vISSRet) informado nos itens.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `REGIME_ESPECIAL_TRIBUTACAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código do Regime Especial de Tributação\",\"labelText\":\"Código do Regime Especial de Tributação\",\"tooltip\":\"Código do Regime Especial de Tributação\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W22g - cRegTrib\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"0=Emissão de NF-e com aplicativo do contribuinte\"},{\"dropDownButtonItem\":\"1=Microempresa Municipal\"},{\"dropDownButtonItem\":\"2=Estimativa\"},{\"dropDownButtonItem\":\"3=Sociedade de Profissionais\"},{\"dropDownButtonItem\":\"4=Cooperativa\"},{\"dropDownButtonItem\":\"5=Microempresário Individual (MEI)\"},{\"dropDownButtonItem\":\"6=Microempresário e Empresa de Pequeno Porte\"}]}}',
  `VALOR_RETIDO_PIS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Retido PIS\",\"labelText\":\"Valor Retido PIS\",\"tooltip\":\"Valor Retido PIS\",\"hintText\":\"Informe o Valor Retido do PIS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W24 - vRetPIS\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_RETIDO_COFINS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Retido COFINS\",\"labelText\":\"Valor Retido COFINS\",\"tooltip\":\"Valor Retido COFINS\",\"hintText\":\"Informe o Valor Retido do COFINS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W25 - vRetCOFINS\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_RETIDO_CSLL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Retido CSLL\",\"labelText\":\"Valor Retido CSLL\",\"tooltip\":\"Valor Retido CSLL\",\"hintText\":\"Informe o Valor Retido do CSLL\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W26 - vRetCSLL\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `BASE_CALCULO_IRRF` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Base Cálculo IRRF\",\"labelText\":\"Valor Base Cálculo IRRF\",\"tooltip\":\"Valor Base Cálculo IRRF\",\"hintText\":\"Informe o Valor da Base de Cálculo do IRRF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W27 - vBCIRRF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_RETIDO_IRRF` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Retido IRRF\",\"labelText\":\"Valor Retido IRRF\",\"tooltip\":\"Valor Retido IRRF\",\"hintText\":\"Informe o Valor Retido IRRF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W28 - vIRRF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `BASE_CALCULO_PREVIDENCIA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Base Cálculo Retenção Previdência\",\"labelText\":\"Valor Base Cálculo Retenção Previdência\",\"tooltip\":\"Valor Base Cálculo Retenção Previdência\",\"hintText\":\"Informe o Valor da BC da Retenção da Previdência\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W29 - vBCRetPrev\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_RETIDO_PREVIDENCIA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Retido Previdência\",\"labelText\":\"Valor Retido Previdência\",\"tooltip\":\"Valor Retido Previdência\",\"hintText\":\"Informe o Valor Retido Previdência\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W30 - vRetPrev\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `INFORMACOES_ADD_FISCO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Informações Adicionais Fisco\",\"labelText\":\"Informações Adicionais Fisco\",\"tooltip\":\"Informações Adicionais Fisco\",\"hintText\":\"Informações Adicionais Fisco\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Z02 - infAdFisco\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `INFORMACOES_ADD_CONTRIBUINTE` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Informações Adicionais Contribuinte\",\"labelText\":\"Informações Adicionais Contribuinte\",\"tooltip\":\"Informações Adicionais Contribuinte\",\"hintText\":\"Informações Adicionais Contribuinte\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Z03 - infCpl\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `COMEX_UF_EMBARQUE` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF de Embarque\",\"labelText\":\"UF de Embarque\",\"tooltip\":\"UF de Embarque\",\"hintText\":\"Informe a UF de Embarque\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZA02 - UFSaidaPais\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF de Embarque\"}]}}',
  `COMEX_LOCAL_EMBARQUE` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Local de Embarque\",\"labelText\":\"Local de Embarque\",\"tooltip\":\"Local de Embarque\",\"hintText\":\"Local de Embarque\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZA03 - xLocExporta\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `COMEX_LOCAL_DESPACHO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Local de Despacho\",\"labelText\":\"Local de Despacho\",\"tooltip\":\"Local de Despacho\",\"hintText\":\"Local de Despacho\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZA04 - xLocDespacho\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `COMPRA_NOTA_EMPENHO` VARCHAR(22) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nota de Empenho\",\"labelText\":\"Nota de Empenho\",\"tooltip\":\"Nota de Empenho\",\"hintText\":\"Nota de Empenho\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZB02 - xNEmp\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `COMPRA_PEDIDO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Pedido Compra\",\"labelText\":\"Pedido Compra\",\"tooltip\":\"Pedido Compra\",\"hintText\":\"Pedido Compra\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZB03 - xPed\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `COMPRA_CONTRATO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Contrato Compra\",\"labelText\":\"Contrato Compra\",\"tooltip\":\"Contrato Compra\",\"hintText\":\"Contrato Compra\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZB 04 - xCont\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QRCODE` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"QRCode\",\"labelText\":\"QRCode\",\"tooltip\":\"QRCode\",\"hintText\":\"QRCode\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZX02 - qrCode\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `URL_CHAVE` VARCHAR(85) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"URL Chave\",\"labelText\":\"URL Chave\",\"tooltip\":\"URL Chave\",\"hintText\":\"URL Chave\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZX03 - urlChave\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `STATUS_NOTA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Status Nota\",\"labelText\":\"Status Nota\",\"tooltip\":\"Status Nota\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"W22g - cRegTrib\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"0-Em Edição\"},{\"dropDownButtonItem\":\"1-Salva\"},{\"dropDownButtonItem\":\"2-Validada\"},{\"dropDownButtonItem\":\"3-Assinada\"},{\"dropDownButtonItem\":\"4-Autorizada\"},{\"dropDownButtonItem\":\"5-Inutilizada\"},{\"dropDownButtonItem\":\"6-Cancelada\"}]}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_NFE_CABECALHO_VENDEDOR1_idx` (`ID_VENDEDOR` ASC),
  INDEX `fk_NFE_CABECALHO_FORNECEDOR1_idx` (`ID_FORNECEDOR` ASC),
  INDEX `fk_NFE_CABECALHO_CLIENTE1_idx` (`ID_CLIENTE` ASC),
  INDEX `fk_NFE_CABECALHO_TRIBUT_OPERACAO_FISCAL1_idx` (`ID_TRIBUT_OPERACAO_FISCAL` ASC),
  INDEX `fk_NFE_CABECALHO_VENDA_CABECALHO1_idx` (`ID_VENDA_CABECALHO` ASC),
  CONSTRAINT `fk_NFE_CABECALHO_VENDEDOR1`
    FOREIGN KEY (`ID_VENDEDOR`)
    REFERENCES `fenix`.`VENDEDOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_NFE_CABECALHO_FORNECEDOR1`
    FOREIGN KEY (`ID_FORNECEDOR`)
    REFERENCES `fenix`.`FORNECEDOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_NFE_CABECALHO_CLIENTE1`
    FOREIGN KEY (`ID_CLIENTE`)
    REFERENCES `fenix`.`CLIENTE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_NFE_CABECALHO_TRIBUT_OPERACAO_FISCAL1`
    FOREIGN KEY (`ID_TRIBUT_OPERACAO_FISCAL`)
    REFERENCES `fenix`.`TRIBUT_OPERACAO_FISCAL` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_NFE_CABECALHO_VENDA_CABECALHO1`
    FOREIGN KEY (`ID_VENDA_CABECALHO`)
    REFERENCES `fenix`.`VENDA_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[B. Identificação da Nota Fiscal eletrônica]\n[Grupo W. Total da NF-e]\n[Grupo W01. Total da NF-e / ISSQN]\n[Grupo W02. Total da NF-e / Retenção de Tributos]\n[Grupo Z. Informações Adicionais da NF-e]\n[Grupo ZA. Informações de Comércio Exterior]\n[Grupo ZB. Informações de Compras]\n[Grupo ZX. Informações Suplementares da Nota Fiscal]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DETALHE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DETALHE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_PRODUTO` INT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Produto\",\"labelText\":\"Produto\",\"tooltip\":\"Produto\",\"hintText\":\"Importe o Produto Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"produto\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO_ITEM` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número do Item\",\"labelText\":\"Número do Item\",\"tooltip\":\"Número do Item\",\"hintText\":\"Informe o Número do Item\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"H02 - nItem\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `CODIGO_PRODUTO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código do Produto\",\"labelText\":\"Código do Produto\",\"tooltip\":\"Código do Produto\",\"hintText\":\"Informe o Código do Produto\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I02 - cProd\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `GTIN` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"GTIN\",\"labelText\":\"GTIN\",\"tooltip\":\"GTIN\",\"hintText\":\"Informe o GTIN\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I03 - cEAN\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME_PRODUTO` VARCHAR(120) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I04 - xProd\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NCM` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"NCM\",\"labelText\":\"NCM\",\"tooltip\":\"NCM\",\"hintText\":\"Informe o NCM\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I05 - NCM\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NVE` VARCHAR(6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"NVE\",\"labelText\":\"NVE\",\"tooltip\":\"NVE\",\"hintText\":\"Informe o NVE\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I05a - NVE - Pode haver até 8 ocorrências, chances de virar uma tabela.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CEST` VARCHAR(7) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CEST\",\"labelText\":\"CEST\",\"tooltip\":\"CEST\",\"hintText\":\"Informe o CEST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I05c - CEST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `INDICADOR_ESCALA_RELEVANTE` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Indicador Escala Relevante\",\"labelText\":\"Indicador Escala Relevante\",\"tooltip\":\"Indicador Escala Relevante\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I05d - indEscala\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}',
  `CNPJ_FABRICANTE` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ do Fabricante\",\"labelText\":\"CNPJ do Fabricante\",\"tooltip\":\"CNPJ do Fabricante\",\"hintText\":\"Informe o CNPJ do Fabricante\",\"validacao\":\"CNPJ\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I05e - CNPJFab\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}',
  `CODIGO_BENEFICIO_FISCAL` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Benefício Fiscal\",\"labelText\":\"Código Benefício Fiscal\",\"tooltip\":\"Código Benefício Fiscal\",\"hintText\":\"Informe o Código Benefício Fiscal\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I05f - cBenef\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `EX_TIPI` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"EX TIPI\",\"labelText\":\"EX TIPI\",\"tooltip\":\"EX TIPI\",\"hintText\":\"Informe o EX TIPI\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I06 - EXTIPI\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `CFOP` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CFOP\",\"labelText\":\"CFOP\",\"tooltip\":\"CFOP\",\"hintText\":\"Informe o CFOP\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"cfop\",\"campoLookup\":\"cfop\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"%\",\"desenhaControle\":true,\"comentario\":\"I08 - CFOP\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `UNIDADE_COMERCIAL` VARCHAR(6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Unidade Comercial\",\"labelText\":\"Unidade Comercial\",\"tooltip\":\"Unidade Comercial\",\"hintText\":\"Informe o Unidade Comercial\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I09 - uCom\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE_COMERCIAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade Comercial\",\"labelText\":\"Quantidade Comercial\",\"tooltip\":\"Quantidade Comercial\",\"hintText\":\"Informe a Quantidade Comercial\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I10 - qCom\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `NUMERO_PEDIDO_COMPRA` VARCHAR(15) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número Pedido Compra\",\"labelText\":\"Número Pedido Compra\",\"tooltip\":\"Número Pedido Compra\",\"hintText\":\"Informe o Número Pedido Compra\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I60 - xPed\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ITEM_PEDIDO_COMPRA` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Item Pedido Compra\",\"labelText\":\"Item Pedido Compra\",\"tooltip\":\"Item Pedido Compra\",\"hintText\":\"Informe o Item Pedido Compra\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I61 - nItemPed\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `NUMERO_FCI` VARCHAR(36) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número FCI\",\"labelText\":\"Número FCI\",\"tooltip\":\"Número FCI\",\"hintText\":\"Informe o Número FCI\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I70 - nFCI\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO_RECOPI` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número RECOPI\",\"labelText\":\"Número RECOPI\",\"tooltip\":\"Número RECOPI\",\"hintText\":\"Informe o Número RECOPI\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LB01 - nRECOPI\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VALOR_UNITARIO_COMERCIAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Unitário Comercial\",\"labelText\":\"Valor Unitário Comercial\",\"tooltip\":\"Valor Unitário Comercial\",\"hintText\":\"Informe o Valor Unitário Comercial\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I10a - vUnCom\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_BRUTO_PRODUTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Bruto Produto\",\"labelText\":\"Valor Bruto Produto\",\"tooltip\":\"Valor Bruto Produto\",\"hintText\":\"Informe o Valor Bruto Produto\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I11 - vProd\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `GTIN_UNIDADE_TRIBUTAVEL` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"GTIN Unidade Tributável\",\"labelText\":\"GTIN Unidade Tributável\",\"tooltip\":\"GTIN Unidade Tributável\",\"hintText\":\"Informe o GTIN Unidade Tributável\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I12 - cEANTrib\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `UNIDADE_TRIBUTAVEL` VARCHAR(6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Unidade Tributável\",\"labelText\":\"Unidade Tributável\",\"tooltip\":\"Unidade Tributável\",\"hintText\":\"Informe o Unidade Tributável\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I13 - uTrib\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE_TRIBUTAVEL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade Tributável\",\"labelText\":\"Quantidade Tributável\",\"tooltip\":\"Quantidade Tributável\",\"hintText\":\"Informe a Quantidade Tributável\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I14 - qTrib\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `VALOR_UNITARIO_TRIBUTAVEL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Unitário Tributável\",\"labelText\":\"Valor Unitário Tributável\",\"tooltip\":\"Valor Unitário Tributável\",\"hintText\":\"Informe o Valor Unitário Tributável\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I14a - vUnTrib\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_FRETE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Frete\",\"labelText\":\"Valor Frete\",\"tooltip\":\"Valor Frete\",\"hintText\":\"Informe o Valor do Frete\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I15 - vFrete\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_SEGURO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Seguro\",\"labelText\":\"Valor Seguro\",\"tooltip\":\"Valor Seguro\",\"hintText\":\"Informe o Valor do Seguro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I16 - vSeg\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto\",\"labelText\":\"Valor Desconto\",\"tooltip\":\"Valor Desconto\",\"hintText\":\"Informe o Valor do Desconto\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I17 - vDesc\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_OUTRAS_DESPESAS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Outras Despesas\",\"labelText\":\"Valor Outras Despesas\",\"tooltip\":\"Valor Outras Despesas\",\"hintText\":\"Informe o Valor de Outras Despesas\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I17a - vOutro\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ENTRA_TOTAL` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Indicador Exibilidade\",\"labelText\":\"Indicador Exibilidade\",\"tooltip\":\"Indicador Exibilidade\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I17b - indTot\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[\n{\"dropDownButtonItem\":\"0=Valor do item (vProd) não compõe o valor total da NF-e\"},\n{\"dropDownButtonItem\":\"1=Valor do item (vProd) compõe o valor total da NF-e (vProd)\"}]}}',
  `VALOR_TOTAL_TRIBUTOS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Tributos\",\"labelText\":\"Valor Total Tributos\",\"tooltip\":\"Valor Total Tributos\",\"hintText\":\"Informe o Valor Total Tributos\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"M02 - vTotTrib\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `PERCENTUAL_DEVOLVIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Percentual Devolvido\",\"labelText\":\"Percentual Devolvido\",\"tooltip\":\"Percentual Devolvido\",\"hintText\":\"Informe o Percentual Devolvido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"UA02 - pDevol\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_IPI_DEVOLVIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor IPI Devolvido\",\"labelText\":\"Valor IPI Devolvido\",\"tooltip\":\"Valor IPI Devolvido\",\"hintText\":\"Informe o Valor IPI Devolvido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"UA04 - vIPIDevol\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `INFORMACOES_ADICIONAIS` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Informações Adicionais\",\"labelText\":\"Informações Adicionais\",\"tooltip\":\"Informações Adicionais\",\"hintText\":\"Informe o Informações Adicionais\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"V01 - infAdProd\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VALOR_SUBTOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Subtotal\",\"labelText\":\"Valor Subtotal\",\"tooltip\":\"Valor Subtotal\",\"hintText\":\"Informe o Valor Subtotal\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_TOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total\",\"labelText\":\"Valor Total\",\"tooltip\":\"Valor Total\",\"hintText\":\"Informe o Valor Total\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_CAB_DET` (`ID_NFE_CABECALHO` ASC),
  INDEX `fk_NFE_DETALHE_PRODUTO1_idx` (`ID_PRODUTO` ASC),
  CONSTRAINT `fk_{C0606BAB-4C4A-4C16-AC2D-F3605E698BF0}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_NFE_DETALHE_PRODUTO1`
    FOREIGN KEY (`ID_PRODUTO`)
    REFERENCES `fenix`.`PRODUTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo H. Detalhamento de Produtos e Serviços da NF-e] \n[Grupo I. Produtos e Serviços da NF-e]\n[Grupo I05. Produtos e Serviços / Pedido de Compra]\n[Grupo I07. Produtos e Serviços / Grupo Diversos]\n[Grupo LB. Detalhamento Específico para Operação com Papel Imune]\n[Grupo UA. Tributos Devolvidos (para o item da NF-e)]\n[Grupo V. Informações adicionais (para o item da NF-e)]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_REFERENCIADA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_REFERENCIADA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CHAVE_ACESSO` VARCHAR(44) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Chave de Acesso\",\"labelText\":\"Chave de Acesso\",\"tooltip\":\"Chave de Acesso\",\"hintText\":\"Chave de Acesso\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA02 - refNFe\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_REFERENCIADA` (`ID_NFE_CABECALHO` ASC),
  CONSTRAINT `fk_{AFFD7A57-423B-4616-90AA-D7C269FF9C5E}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo BA. Documento Fiscal Referenciado] BA01 - Informação de Documentos Fiscais\nreferenciados'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_EMITENTE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_EMITENTE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CNPJ` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ\",\"labelText\":\"CNPJ\",\"tooltip\":\"CNPJ\",\"hintText\":\"Informe o CNPJ\",\"validacao\":\"CNPJ\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C02 - CNPJ\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}',
  `CPF` VARCHAR(11) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CPF\",\"labelText\":\"CPF\",\"tooltip\":\"CPF\",\"hintText\":\"Informe o CPF\",\"validacao\":\"CPF\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C02a - CPF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CPF\"}}',
  `NOME` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C03 - xNome\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `FANTASIA` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Fantasia\",\"labelText\":\"Fantasia\",\"tooltip\":\"Fantasia\",\"hintText\":\"Informe o Fantasia\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C04 - xFant\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `LOGRADOURO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Logradouro\",\"labelText\":\"Logradouro\",\"tooltip\":\"Logradouro\",\"hintText\":\"Informe o Logradouro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C06 - xLgr\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C07 - nro\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `COMPLEMENTO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Complemento\",\"labelText\":\"Complemento\",\"tooltip\":\"Complemento\",\"hintText\":\"Informe o Complemento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C08 - xCpl\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `BAIRRO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Bairro\",\"labelText\":\"Bairro\",\"tooltip\":\"Bairro\",\"hintText\":\"Informe o Bairro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C09 - xBairro\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CODIGO_MUNICIPIO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município IBGE\",\"labelText\":\"Município IBGE\",\"tooltip\":\"Município IBGE\",\"hintText\":\"Importe o Município IBGE Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"municipio\",\"campoLookup\":\"codigoIbge\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C10 - cMun\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME_MUNICIPIO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município\",\"labelText\":\"Município\",\"tooltip\":\"Município\",\"hintText\":\"Informe o Município\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C11 - xMun\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `UF` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C12 - UF\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}',
  `CEP` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CEP\",\"labelText\":\"CEP\",\"tooltip\":\"CEP\",\"hintText\":\"Informe o CEP\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cep\",\"campoLookup\":\"numero\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C13 - CEP\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"CEP\"}}',
  `CODIGO_PAIS` INT NULL DEFAULT 1058 COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"País BACEN\",\"labelText\":\"País BACEN\",\"tooltip\":\"País BACEN\",\"hintText\":\"Importe o País BACEN Vinculado\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"pais\",\"campoLookup\":\"codigoBacen\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C14 - cPais\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME_PAIS` VARCHAR(60) NULL DEFAULT 'BRASIL' COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"País\",\"labelText\":\"País\",\"tooltip\":\"País\",\"hintText\":\"Informe o País\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C15 - xPais\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `TELEFONE` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Telefone\",\"labelText\":\"Telefone\",\"tooltip\":\"Telefone\",\"hintText\":\"Informe o Telefone\",\"validacao\":\"Telefone\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C16 - fone\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TELEFONE\"}}',
  `INSCRICAO_ESTADUAL` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Incrição Estadual\",\"labelText\":\"Incrição Estadual\",\"tooltip\":\"Incrição Estadual\",\"hintText\":\"Informe a Incrição Estadual\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C17 - IE\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `INSCRICAO_ESTADUAL_ST` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Incrição Estadual ST\",\"labelText\":\"Incrição Estadual ST\",\"tooltip\":\"Incrição Estadual ST\",\"hintText\":\"Informe a Incrição Estadual ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C18 - IEST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `INSCRICAO_MUNICIPAL` VARCHAR(15) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Incrição Municipal\",\"labelText\":\"Incrição Municipal\",\"tooltip\":\"Incrição Municipal\",\"hintText\":\"Informe a Incrição Municipal\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C19 - IM\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CNAE` VARCHAR(7) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNAE\",\"labelText\":\"CNAE\",\"tooltip\":\"CNAE\",\"hintText\":\"Informe o CNAE\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C20 - CNAE\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CRT` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CRT\",\"labelText\":\"CRT\",\"tooltip\":\"CRT\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"C21 - CRT\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[\n{\"dropDownButtonItem\":\"1-Simples Nacional\"},{\"dropDownButtonItem\":\"2-Simples Nacional - excesso de sublimite da receita bruta\"},{\"dropDownButtonItem\":\"3-Regime Normal\"}]}}\n',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_EMITENTE` (`ID_NFE_CABECALHO` ASC),
  CONSTRAINT `fk_{49365C97-491E-489C-9F44-C5B537E8913D}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo C. Identificação do Emitente da Nota Fiscal eletrônica]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DESTINATARIO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DESTINATARIO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CNPJ` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ\",\"labelText\":\"CNPJ\",\"tooltip\":\"CNPJ\",\"hintText\":\"Informe o CNPJ\",\"validacao\":\"CNPJ\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E02 - CNPJ\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}',
  `CPF` VARCHAR(11) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CPF\",\"labelText\":\"CPF\",\"tooltip\":\"CPF\",\"hintText\":\"Informe o CPF\",\"validacao\":\"CPF\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E03 - CPF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CPF\"}}',
  `ESTRANGEIRO_IDENTIFICACAO` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Idenfiticação Estrangeiro\",\"labelText\":\"Idenfiticação Estrangeiro\",\"tooltip\":\"Idenfiticação Estrangeiro\",\"hintText\":\"Informe a Idenfiticação Estrangeiro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E03a - idEstrangeiro\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E04 - xNome\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `LOGRADOURO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Logradouro\",\"labelText\":\"Logradouro\",\"tooltip\":\"Logradouro\",\"hintText\":\"Informe o Logradouro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E06 - xLgr\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E07 - nro\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `COMPLEMENTO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Complemento\",\"labelText\":\"Complemento\",\"tooltip\":\"Complemento\",\"hintText\":\"Informe o Complemento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E08 - xCpl\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `BAIRRO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Bairro\",\"labelText\":\"Bairro\",\"tooltip\":\"Bairro\",\"hintText\":\"Informe o Bairro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E09 - xBairro\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CODIGO_MUNICIPIO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município IBGE\",\"labelText\":\"Município IBGE\",\"tooltip\":\"Município IBGE\",\"hintText\":\"Importe o Município IBGE Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"municipio\",\"campoLookup\":\"codigoIbge\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E10 - cMun\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME_MUNICIPIO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município\",\"labelText\":\"Município\",\"tooltip\":\"Município\",\"hintText\":\"Informe o Município\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E11 - xMun\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `UF` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E12 - UF\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}',
  `CEP` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CEP\",\"labelText\":\"CEP\",\"tooltip\":\"CEP\",\"hintText\":\"Importe o CEP Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cep\",\"campoLookup\":\"numero\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E13 - CEP\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"CEP\"}}',
  `CODIGO_PAIS` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"País BACEN\",\"labelText\":\"País BACEN\",\"tooltip\":\"País BACEN\",\"hintText\":\"Importe o País BACEN Vinculado\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"pais\",\"campoLookup\":\"codigoBacen\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E14 - cPais\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME_PAIS` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"País\",\"labelText\":\"País\",\"tooltip\":\"País\",\"hintText\":\"Informe o País\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E15 - xPais\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `TELEFONE` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Telefone\",\"labelText\":\"Telefone\",\"tooltip\":\"Telefone\",\"hintText\":\"Informe o Telefone\",\"validacao\":\"Telefone\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E16 - fone\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TELEFONE\"}}',
  `INDICADOR_IE` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Indicador IE\",\"labelText\":\"Indicador IE\",\"tooltip\":\"Indicador IE\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E16a - indIEDest\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"1=Contribuinte ICMS\"},{\"dropDownButtonItem\":\"2=Contribuinte isento de Inscrição no cadastro de Contribuintes do ICMS\"},{\"dropDownButtonItem\":\"9=Não Contribuinte, que pode ou não possuir Inscrição Estadual no Cadastro de Contribuintes do ICMS\"}]}}\n',
  `INSCRICAO_ESTADUAL` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Inscrição Estadual\",\"labelText\":\"Inscrição Estadual\",\"tooltip\":\"Inscrição Estadual\",\"hintText\":\"Informe o Inscrição Estadual\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E17 - IE\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `SUFRAMA` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"SUFRAMA\",\"labelText\":\"SUFRAMA\",\"tooltip\":\"SUFRAMA\",\"hintText\":\"Informe a SUFRAMA\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E18 - ISUF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `INSCRICAO_MUNICIPAL` VARCHAR(15) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Inscrição Municipal\",\"labelText\":\"Inscrição Municipal\",\"tooltip\":\"Inscrição Municipal\",\"hintText\":\"Informe o Inscrição Municipal\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E18a - IM\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `EMAIL` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"EMail\",\"labelText\":\"EMail\",\"tooltip\":\"EMail\",\"hintText\":\"Informe o EMail\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"E19 - email\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_DESTINATARIO` (`ID_NFE_CABECALHO` ASC),
  CONSTRAINT `fk_{11F2D433-04D6-44FD-BAA8-D5B266160973}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo E. Identificação do Destinatário da Nota Fiscal eletrônica]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_LOCAL_RETIRADA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_LOCAL_RETIRADA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CNPJ` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ\",\"labelText\":\"CNPJ\",\"tooltip\":\"CNPJ\",\"hintText\":\"Informe o CNPJ\",\"validacao\":\"CNPJ\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F02 - CNPJ\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}',
  `CPF` VARCHAR(11) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CPF\",\"labelText\":\"CPF\",\"tooltip\":\"CPF\",\"hintText\":\"Informe o CPF\",\"validacao\":\"CPF\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F02a - CPF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CPF\"}}',
  `NOME_EXPEDIDOR` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome Expedidor\",\"labelText\":\"Nome Expedidor\",\"tooltip\":\"Nome Expedidor\",\"hintText\":\"Informe o Nome do Expedidor\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F02b - xNome\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `LOGRADOURO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Logradouro\",\"labelText\":\"Logradouro\",\"tooltip\":\"Logradouro\",\"hintText\":\"Informe o Logradouro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F03 - xLgr\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F04 - nro\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `COMPLEMENTO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Complemento\",\"labelText\":\"Complemento\",\"tooltip\":\"Complemento\",\"hintText\":\"Informe o Complemento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F05 - xCpl\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `BAIRRO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Bairro\",\"labelText\":\"Bairro\",\"tooltip\":\"Bairro\",\"hintText\":\"Informe o Bairro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F06 - xBairro\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CODIGO_MUNICIPIO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município IBGE\",\"labelText\":\"Município IBGE\",\"tooltip\":\"Município IBGE\",\"hintText\":\"Importe o Município IBGE Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"municipio\",\"campoLookup\":\"codigoIbge\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F07 - cMun\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME_MUNICIPIO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município\",\"labelText\":\"Município\",\"tooltip\":\"Município\",\"hintText\":\"Informe o Município\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F08 - xMun\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `UF` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F09 - UF\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}',
  `CEP` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CEP\",\"labelText\":\"CEP\",\"tooltip\":\"CEP\",\"hintText\":\"Informe o CEP\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cep\",\"campoLookup\":\"numero\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F10 - CEP\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"CEP\"}}',
  `CODIGO_PAIS` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"País BACEN\",\"labelText\":\"País BACEN\",\"tooltip\":\"País BACEN\",\"hintText\":\"Importe o País BACEN Vinculado\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"pais\",\"campoLookup\":\"codigoBacen\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F11 - cPais\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME_PAIS` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"País\",\"labelText\":\"País\",\"tooltip\":\"País\",\"hintText\":\"Informe o País\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F12 - xPais\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `TELEFONE` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Telefone\",\"labelText\":\"Telefone\",\"tooltip\":\"Telefone\",\"hintText\":\"Informe o Telefone\",\"validacao\":\"Telefone\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F14 - fone\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TELEFONE\"}}',
  `EMAIL` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"EMail\",\"labelText\":\"EMail\",\"tooltip\":\"EMail\",\"hintText\":\"Informe o EMail\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F14 - email\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `INSCRICAO_ESTADUAL` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Incrição Estadual\",\"labelText\":\"Incrição Estadual\",\"tooltip\":\"Incrição Estadual\",\"hintText\":\"Informe a Incrição Estadual\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"F15 - IE\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_LOCAL_RETIRADA` (`ID_NFE_CABECALHO` ASC),
  CONSTRAINT `fk_{8CDD5E8C-6254-41DE-A520-F318A3809796}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo F. Identificação do Local de Retirada]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_LOCAL_ENTREGA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_LOCAL_ENTREGA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CNPJ` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ\",\"labelText\":\"CNPJ\",\"tooltip\":\"CNPJ\",\"hintText\":\"Informe o CNPJ\",\"validacao\":\"CNPJ\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"G02 - CNPJ\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}',
  `CPF` VARCHAR(11) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CPF\",\"labelText\":\"CPF\",\"tooltip\":\"CPF\",\"hintText\":\"Informe o CPF\",\"validacao\":\"CPF\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"G02a - CPF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CPF\"}}',
  `NOME_RECEBEDOR` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"G02b - xNome\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `LOGRADOURO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Logradouro\",\"labelText\":\"Logradouro\",\"tooltip\":\"Logradouro\",\"hintText\":\"Informe o Logradouro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"G03 - xLgr\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"G04 - nro\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `COMPLEMENTO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Complemento\",\"labelText\":\"Complemento\",\"tooltip\":\"Complemento\",\"hintText\":\"Informe o Complemento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"G05 - xCpl\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `BAIRRO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Bairro\",\"labelText\":\"Bairro\",\"tooltip\":\"Bairro\",\"hintText\":\"Informe o Bairro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"G06 - xBairro\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CODIGO_MUNICIPIO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município IBGE\",\"labelText\":\"Município IBGE\",\"tooltip\":\"Município IBGE\",\"hintText\":\"Importe o Município IBGE Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"municipio\",\"campoLookup\":\"codigoIbge\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"G07 - cMun\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME_MUNICIPIO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município\",\"labelText\":\"Município\",\"tooltip\":\"Município\",\"hintText\":\"Informe o Município\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"G08 - xMun\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `UF` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"G09 - UF\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}',
  `CEP` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CEP\",\"labelText\":\"CEP\",\"tooltip\":\"CEP\",\"hintText\":\"Informe o CEP\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cep\",\"campoLookup\":\"numero\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"G10 - CEP\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"CEP\"}}',
  `CODIGO_PAIS` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"País BACEN\",\"labelText\":\"País BACEN\",\"tooltip\":\"País BACEN\",\"hintText\":\"Importe o País BACEN Vinculado\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"pais\",\"campoLookup\":\"codigoBacen\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME_PAIS` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"País\",\"labelText\":\"País\",\"tooltip\":\"País\",\"hintText\":\"Informe o País\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"G12 - xPais\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `TELEFONE` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Telefone\",\"labelText\":\"Telefone\",\"tooltip\":\"Telefone\",\"hintText\":\"Informe o Telefone\",\"validacao\":\"Telefone\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"G13 - fone\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TELEFONE\"}}',
  `EMAIL` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"EMail\",\"labelText\":\"EMail\",\"tooltip\":\"EMail\",\"hintText\":\"Informe o EMail\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"G14 - email\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `INSCRICAO_ESTADUAL` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Incrição Estadual\",\"labelText\":\"Incrição Estadual\",\"tooltip\":\"Incrição Estadual\",\"hintText\":\"Informe a Incrição Estadual\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"G15 - IE\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_LOCAL_ENTREGA` (`ID_NFE_CABECALHO` ASC),
  CONSTRAINT `fk_{BABA4BA0-DE05-4541-A1B6-03B53B66C5B1}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo G. Identificação do Local de Entrega]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DET_ESPECIFICO_VEICULO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DET_ESPECIFICO_VEICULO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `TIPO_OPERACAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Operação\",\"labelText\":\"Tipo Operação\",\"tooltip\":\"Tipo Operação\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J02 - tpOp\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"1=Venda concessionária\"},{\"dropDownButtonItem\":\"2=Faturamento direto para consumidor final\"},{\"dropDownButtonItem\":\"3=Venda direta para grandes consumidores\"},{\"dropDownButtonItem\":\"0=Outros\"}]}}',
  `CHASSI` VARCHAR(17) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Chassi\",\"labelText\":\"Chassi\",\"tooltip\":\"Chassi\",\"hintText\":\"Informe o Chassi\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J03 - chassi\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `COR` VARCHAR(4) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cor\",\"labelText\":\"Cor\",\"tooltip\":\"Cor\",\"hintText\":\"Informe o Cor\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J04 - cCor\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO_COR` VARCHAR(40) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição Cor\",\"labelText\":\"Descrição Cor\",\"tooltip\":\"Descrição Cor\",\"hintText\":\"Informe o Descrição Cor\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J05 - xCor\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `POTENCIA_MOTOR` VARCHAR(4) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Potência Motor\",\"labelText\":\"Potência Motor\",\"tooltip\":\"Potência Motor\",\"hintText\":\"Informe o Potência Motor\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J06 - pot\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CILINDRADAS` VARCHAR(4) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cilindradas\",\"labelText\":\"Cilindradas\",\"tooltip\":\"Cilindradas\",\"hintText\":\"Informe o Cilindradas\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J07 - cilin\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PESO_LIQUIDO` VARCHAR(9) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Peso Líquido\",\"labelText\":\"Peso Líquido\",\"tooltip\":\"Peso Líquido\",\"hintText\":\"Informe o Peso Líquido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J08 - pesoL\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PESO_BRUTO` VARCHAR(9) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Peso Bruto\",\"labelText\":\"Peso Bruto\",\"tooltip\":\"Peso Bruto\",\"hintText\":\"Informe o Peso Bruto\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J09 - pesoB\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO_SERIE` VARCHAR(9) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número de Série\",\"labelText\":\"Número de Série\",\"tooltip\":\"Número de Série\",\"hintText\":\"Informe o Número de Série\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J10 - nSerie\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `TIPO_COMBUSTIVEL` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Combustível\",\"labelText\":\"Tipo Combustível\",\"tooltip\":\"Tipo Combustível\",\"hintText\":\"Informe a Tipo Combustível\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J11 - tpComb\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaCombustivelRenavam\"}]}}',
  `NUMERO_MOTOR` VARCHAR(21) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número do Motor\",\"labelText\":\"Número do Motor\",\"tooltip\":\"Número do Motor\",\"hintText\":\"Informe o Número do Motor\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J12 - nMotor\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CAPACIDADE_MAXIMA_TRACAO` VARCHAR(9) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Capacidade Máxima Tração\",\"labelText\":\"Capacidade Máxima Tração\",\"tooltip\":\"Capacidade Máxima Tração\",\"hintText\":\"Informe a Capacidade Máxima Tração\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J13 - CMT\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DISTANCIA_EIXOS` VARCHAR(4) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Distância Entre Eixos\",\"labelText\":\"Distância Entre Eixos\",\"tooltip\":\"Distância Entre Eixos\",\"hintText\":\"Informe a Distância Entre Eixos\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J14 - dist\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ANO_MODELO` CHAR(4) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Ano Modelo\",\"labelText\":\"Ano Modelo\",\"tooltip\":\"Ano Modelo\",\"hintText\":\"Informe o Ano Modelo\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J16 - anoMod\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `ANO_FABRICACAO` CHAR(4) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Ano Fabricação\",\"labelText\":\"Ano Fabricação\",\"tooltip\":\"Ano Fabricação\",\"hintText\":\"Informe o Ano Fabricação\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J17 - anoFab\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `TIPO_PINTURA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Pintura\",\"labelText\":\"Tipo Pintura\",\"tooltip\":\"Tipo Pintura\",\"hintText\":\"Informe o Tipo Pintura\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J18 - tpPint\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `TIPO_VEICULO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Veículo\",\"labelText\":\"Tipo Veículo\",\"tooltip\":\"Tipo Veículo\",\"hintText\":\"Informe a Tipo Veículo\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J19 - tpVeic\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaVeiculoRenavam\"}]}}',
  `ESPECIE_VEICULO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Espécie Veículo\",\"labelText\":\"Espécie Veículo\",\"tooltip\":\"Espécie Veículo\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J20 - espVeic\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"1=PASSAGEIRO\"},{\"dropDownButtonItem\":\"2=CARGA\"},{\"dropDownButtonItem\":\"3=MISTO\"},{\"dropDownButtonItem\":\"4=CORRIDA\"},{\"dropDownButtonItem\":\"5=TRAÇÃO\"},{\"dropDownButtonItem\":\"6=ESPECIAL\"}]}}',
  `CONDICAO_VIN` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"VIN\",\"labelText\":\"VIN\",\"tooltip\":\"VIN\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J21 - VIN\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"R-Remarcado\"},{\"dropDownButtonItem\":\"N-Normal\"}]}}',
  `CONDICAO_VEICULO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Condição Veículo\",\"labelText\":\"Condição Veículo\",\"tooltip\":\"Condição Veículo\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J22 - condVeic\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"1=Acabado\"},{\"dropDownButtonItem\":\"2=Inacabado\"},{\"dropDownButtonItem\":\"3=Semiacabado\"}]}}',
  `CODIGO_MARCA_MODELO` VARCHAR(6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Marca Modelo\",\"labelText\":\"Código Marca Modelo\",\"tooltip\":\"Código Marca Modelo\",\"hintText\":\"Informe o Código Marca Modelo\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J23 - cMod\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CODIGO_COR_DENATRAN` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cor DENATRAN\",\"labelText\":\"Cor DENATRAN\",\"tooltip\":\"Cor DENATRAN\",\"hintText\":\"Informe a Cor DENATRAN\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J24 - cCorDENATRAN\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaCorDenatran\"}]}}',
  `LOTACAO_MAXIMA` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Lotação Máxima\",\"labelText\":\"Lotação Máxima\",\"tooltip\":\"Lotação Máxima\",\"hintText\":\"Informe a Lotação Máxima\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J25 - lota\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `RESTRICAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Restrição\",\"labelText\":\"Restrição\",\"tooltip\":\"Restrição\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"J26 - tpRest\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"0=Não há\"},{\"dropDownButtonItem\":\"1=Alienação Fiduciária\"},{\"dropDownButtonItem\":\"2=Arrendamento Mercantil\"},{\"dropDownButtonItem\":\"3=Reserva de Domínio\"},{\"dropDownButtonItem\":\"4=Penhor de Veículos\"},{\"dropDownButtonItem\":\"9=Outras\"}]}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_DET_ESPECIFICO_VEICULO` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_{02840D51-CFB3-4D99-AC7B-F82104B522C2}`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo JA. Detalhamento Específico de Veículos novos]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DET_ESPECIFICO_MEDICAMENTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DET_ESPECIFICO_MEDICAMENTO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CODIGO_ANVISA` VARCHAR(13) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Anvisa\",\"labelText\":\"Código Anvisa\",\"tooltip\":\"Código Anvisa\",\"hintText\":\"Informe o Código Anvisa\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"K01a - cProdANVISA\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MOTIVO_ISENCAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Motivo Isenção\",\"labelText\":\"Motivo Isenção\",\"tooltip\":\"Motivo Isenção\",\"hintText\":\"Informe o Motivo Isenção\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"K01b - xMotivoIsencao\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PRECO_MAXIMO_CONSUMIDOR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Preço Máximo Consumidor\",\"labelText\":\"Preço Máximo Consumidor\",\"tooltip\":\"Preço Máximo Consumidor\",\"hintText\":\"Informe o Preço Máximo Consumidor\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"K06 - vPMC\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_DET_ESP_MEDICAMENTO` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_{EE95E781-991D-48B7-815A-4BC87F24190B}`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo K. Detalhamento Específico de Medicamento e de matérias-primas farmacêuticas]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DET_ESPECIFICO_ARMAMENTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DET_ESPECIFICO_ARMAMENTO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `TIPO_ARMA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Indicador Tipo Arma\",\"labelText\":\"Indicador Tipo Arma\",\"tooltip\":\"Indicador Tipo Arma\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"L02 - tpArma\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"0=Uso permitido\"},{\"dropDownButtonItem\":\"1=Uso restrito\"}]}}\n',
  `NUMERO_SERIE_ARMA` VARCHAR(15) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número de Série Arma\",\"labelText\":\"Número de Série Arma\",\"tooltip\":\"Número de Série Arma\",\"hintText\":\"Informe o Número de Série Arma\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"L03 - nSerie\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO_SERIE_CANO` VARCHAR(15) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número de Série Cano\",\"labelText\":\"Número de Série Cano\",\"tooltip\":\"Número de Série Cano\",\"hintText\":\"Informe o Número de Série Cano\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"L04 - nCano\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe o Descrição\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"L05 - descr\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_DET_ESP_ARMAMENTO` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_{AA2001E7-A2C4-4E15-89D2-693E8939F1BD}`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo L. Detalhamento Específico de Armamentos]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DET_ESPECIFICO_COMBUSTIVEL`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DET_ESPECIFICO_COMBUSTIVEL` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CODIGO_ANP` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código ANP\",\"labelText\":\"Código ANP\",\"tooltip\":\"Código ANP\",\"hintText\":\"Informe a Código ANP\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA02 - cProdANP\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DESCRICAO_ANP` VARCHAR(95) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição ANP\",\"labelText\":\"Descrição ANP\",\"tooltip\":\"Descrição ANP\",\"hintText\":\"Informe o Descrição ANP\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA03 - descANP\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PERCENTUAL_GLP` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Percentual GLP\",\"labelText\":\"Percentual GLP\",\"tooltip\":\"Percentual GLP\",\"hintText\":\"Informe o Percentual GLP\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA03a - pGLP\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `PERCENTUAL_GAS_NACIONAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Percentual Gás Nacional\",\"labelText\":\"Percentual Gás Nacional\",\"tooltip\":\"Percentual Gás Nacional\",\"hintText\":\"Informe o Percentual Gás Nacional\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA03b - pGNn\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `PERCENTUAL_GAS_IMPORTADO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Percentual Gás Importado\",\"labelText\":\"Percentual Gás Importado\",\"tooltip\":\"Percentual Gás Importado\",\"hintText\":\"Informe o Percentual Gás Importado\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA03c - pGNi\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_PARTIDA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Partida\",\"labelText\":\"Valor Partida\",\"tooltip\":\"Valor Partida\",\"hintText\":\"Informe o Valor Partida\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA03d - vPart\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `CODIF` VARCHAR(21) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CODIF\",\"labelText\":\"CODIF\",\"tooltip\":\"CODIF\",\"hintText\":\"Informe o CODIF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA04 - CODIF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE_TEMP_AMBIENTE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade Temperatura Ambiente\",\"labelText\":\"Quantidade Temperatura Ambiente\",\"tooltip\":\"Quantidade Temperatura Ambiente\",\"hintText\":\"Informe a Quantidade Temperatura Ambiente\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA05 - qTemp\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `UF_CONSUMO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA06 - UFCons\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}',
  `CIDE_BASE_CALCULO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Base Cálculo CIDE\",\"labelText\":\"Base Cálculo CIDE\",\"tooltip\":\"Base Cálculo CIDE\",\"hintText\":\"Informe a Base de Cálculo do CIDE\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA08 - qBCProd\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `CIDE_ALIQUOTA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota CIDE\",\"labelText\":\"Alíquota CIDE\",\"tooltip\":\"Alíquota CIDE\",\"hintText\":\"Informe o Alíquota CIDE\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA09 - vAliqProd\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `CIDE_VALOR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor CIDE\",\"labelText\":\"Valor CIDE\",\"tooltip\":\"Valor CIDE\",\"hintText\":\"Informe o Valor CIDE\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA10 - vCIDE\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ENCERRANTE_BICO` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Encerrante - Número do Bico\",\"labelText\":\"Encerrante - Número do Bico\",\"tooltip\":\"Encerrante - Número do Bico\",\"hintText\":\"Informe a Encerrante - Número do Bico\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA12 - nBico\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `ENCERRANTE_BOMBA` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Encerrante - Número da Bomba\",\"labelText\":\"Encerrante - Número da Bomba\",\"tooltip\":\"Encerrante - Número da Bomba\",\"hintText\":\"Informe a Encerrante - Número da Bomba\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA13 - nBomba\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `ENCERRANTE_TANQUE` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Encerrante - Número do Tanque\",\"labelText\":\"Encerrante - Número do Tanque\",\"tooltip\":\"Encerrante - Número do Tanque\",\"hintText\":\"Informe a Encerrante - Número do Tanque\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA14 - nTanque\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `ENCERRANTE_VALOR_INICIO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Encerrante Início Abastecimento\",\"labelText\":\"Valor Encerrante Início Abastecimento\",\"tooltip\":\"Valor Encerrante Início Abastecimento\",\"hintText\":\"Informe o Valor Encerrante Início Abastecimento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA15 - vEncIni\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ENCERRANTE_VALOR_FIM` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Encerrante Fim Abastecimento\",\"labelText\":\"Valor Encerrante Fim Abastecimento\",\"tooltip\":\"Valor Encerrante Fim Abastecimento\",\"hintText\":\"Informe o Valor Encerrante Fim Abastecimento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"LA16 - vEncFin\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_DET_ESP_COMBUSTIVEL` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_{723B7CE2-E4EB-44C4-B2EA-C44402D5DCB4}`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo LA. Detalhamento Específico de Combustíveis]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_TRANSPORTE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_TRANSPORTE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_TRANSPORTADORA` INT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Transportadora\",\"labelText\":\"Transportadora\",\"tooltip\":\"Transportadora\",\"hintText\":\"Importe a Transportadora Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"transportadora\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MODALIDADE_FRETE` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modalidade Frete\",\"labelText\":\"Modalidade Frete\",\"tooltip\":\"Modalidade Frete\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X02 - modFrete\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[\n{\"dropDownButtonItem\":\"0=Contratação do Frete por conta do Remetente (CIF)\"},\n{\"dropDownButtonItem\":\"1=Contratação do Frete por conta do Destinatário (FOB)\"},\n{\"dropDownButtonItem\":\"2=Contratação do Frete por conta de Terceiros\"},\n{\"dropDownButtonItem\":\"3=Transporte Próprio por conta do Remetente\"},\n{\"dropDownButtonItem\":\"4=Transporte Próprio por conta do Destinatário\"},\n{\"dropDownButtonItem\":\"9=Sem Ocorrência de Transporte\"}]}}',
  `CNPJ` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ\",\"labelText\":\"CNPJ\",\"tooltip\":\"CNPJ\",\"hintText\":\"Informe o CNPJ\",\"validacao\":\"CNPJ\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X04 - CNPJ\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}',
  `CPF` VARCHAR(11) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CPF\",\"labelText\":\"CPF\",\"tooltip\":\"CPF\",\"hintText\":\"Informe o CPF\",\"validacao\":\"CPF\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X05 - CPF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CPF\"}}',
  `NOME` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X06 - xNome\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `INSCRICAO_ESTADUAL` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Incrição Estadual\",\"labelText\":\"Incrição Estadual\",\"tooltip\":\"Incrição Estadual\",\"hintText\":\"Informe a Incrição Estadual\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X07 - IE\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ENDERECO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Endereço\",\"labelText\":\"Endereço\",\"tooltip\":\"Endereço\",\"hintText\":\"Informe o Endereço\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X08 - xEnder\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME_MUNICIPIO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome Município\",\"labelText\":\"Nome Município\",\"tooltip\":\"Nome Município\",\"hintText\":\"Informe o Nome do Município\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X09 - xMun\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `UF` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X10 - UF\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}',
  `VALOR_SERVICO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Serviço\",\"labelText\":\"Valor Serviço\",\"tooltip\":\"Valor Serviço\",\"hintText\":\"Informe o Valor Serviço\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X12 - vServ\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_BC_RETENCAO_ICMS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC Retenção ICMS\",\"labelText\":\"Valor BC Retenção ICMS\",\"tooltip\":\"Valor BC Retenção ICMS\",\"hintText\":\"Informe o Valor BC Retenção ICMS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X13 - vBCRet\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ALIQUOTA_RETENCAO_ICMS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Retenção ICMS\",\"labelText\":\"Alíquota Retenção ICMS\",\"tooltip\":\"Alíquota Retenção ICMS\",\"hintText\":\"Informe o Alíquota Retenção ICMS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X14 - pICMSRet\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_ICMS_RETIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor ICMS Retido\",\"labelText\":\"Valor ICMS Retido\",\"tooltip\":\"Valor ICMS Retido\",\"hintText\":\"Informe o Valor ICMS Retido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X15 - vICMSRet\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `CFOP` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CFOP\",\"labelText\":\"CFOP\",\"tooltip\":\"CFOP\",\"hintText\":\"Informe o CFOP\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"cfop\",\"campoLookup\":\"cfop\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"%\",\"desenhaControle\":true,\"comentario\":\"X16 - CFOP\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MUNICIPIO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município IBGE\",\"labelText\":\"Município IBGE\",\"tooltip\":\"Município IBGE\",\"hintText\":\"Importe o Município IBGE Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"municipio\",\"campoLookup\":\"codigoIbge\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X17 - cMunFG\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PLACA_VEICULO` VARCHAR(7) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Placa Veículo\",\"labelText\":\"Placa Veículo\",\"tooltip\":\"Placa Veículo\",\"hintText\":\"Informe o Placa Veículo\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X19 - placa\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `UF_VEICULO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF do Veículo\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X20 - UF\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}',
  `RNTC_VEICULO` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"RNTC Veículo\",\"labelText\":\"RNTC Veículo\",\"tooltip\":\"RNTC Veículo\",\"hintText\":\"Informe o RNTC Veículo\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X21 - RNTC\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_TRANSPORTE` (`ID_NFE_CABECALHO` ASC),
  INDEX `fk_NFE_TRANSPORTE_TRANSPORTADORA1_idx` (`ID_TRANSPORTADORA` ASC),
  CONSTRAINT `fk_{BDFB69FA-15C2-4D43-ABDF-E39AB5044709}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_NFE_TRANSPORTE_TRANSPORTADORA1`
    FOREIGN KEY (`ID_TRANSPORTADORA`)
    REFERENCES `fenix`.`TRANSPORTADORA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo X. Informações do Transporte da NF-e]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_FATURA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_FATURA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `NUMERO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Y03 - nFat\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VALOR_ORIGINAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Original\",\"labelText\":\"Valor Original\",\"tooltip\":\"Valor Original\",\"hintText\":\"Informe o Valor Original\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Y04 - vOrig\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto\",\"labelText\":\"Valor Desconto\",\"tooltip\":\"Valor Desconto\",\"hintText\":\"Informe o Valor do Desconto\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Y05 - vDesc\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_LIQUIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Líquido\",\"labelText\":\"Valor Líquido\",\"tooltip\":\"Valor Líquido\",\"hintText\":\"Informe o Valor Líquido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Y06 - vLiq\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_CAB_FATURA` (`ID_NFE_CABECALHO` ASC),
  CONSTRAINT `fk_{FB825C01-BAC2-48B6-8B14-6D37BE3DAFF8}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo Y. Dados da Cobrança] Y02'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DUPLICATA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DUPLICATA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_FATURA` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `NUMERO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Y08 - nDup\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_VENCIMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Vencimento\",\"labelText\":\"Data de Vencimento\",\"tooltip\":\"Data de Vencimento\",\"hintText\":\"Informe a Data de Vencimento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Y09 - dVenc\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `VALOR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor\",\"labelText\":\"Valor\",\"tooltip\":\"Valor\",\"hintText\":\"Informe o Valor\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Y10 - vDup\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_NFE_DUPLICATA_NFE_FATURA1_idx` (`ID_NFE_FATURA` ASC),
  CONSTRAINT `fk_NFE_DUPLICATA_NFE_FATURA1`
    FOREIGN KEY (`ID_NFE_FATURA`)
    REFERENCES `fenix`.`NFE_FATURA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo Y. Dados da Cobrança] Y07'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DECLARACAO_IMPORTACAO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DECLARACAO_IMPORTACAO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `NUMERO_DOCUMENTO` VARCHAR(12) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número do Documento\",\"labelText\":\"Número do Documento\",\"tooltip\":\"Número do Documento\",\"hintText\":\"Informe o Número do Documento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I19 - nDI\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_REGISTRO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Registro\",\"labelText\":\"Data de Registro\",\"tooltip\":\"Data de Registro\",\"hintText\":\"Informe a Data de Registro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I20 - dDI\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `LOCAL_DESEMBARACO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Local Desembaraço\",\"labelText\":\"Local Desembaraço\",\"tooltip\":\"Local Desembaraço\",\"hintText\":\"Informe o Local Desembaraço\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I21 - xLocDesemb\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `UF_DESEMBARACO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF Desembaraço\",\"labelText\":\"UF Desembaraço\",\"tooltip\":\"UF Desembaraço\",\"hintText\":\"Informe a UF do Desembaraço\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I22 - UFDesemb\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}',
  `DATA_DESEMBARACO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Desembaraço\",\"labelText\":\"Data de Desembaraço\",\"tooltip\":\"Data de Desembaraço\",\"hintText\":\"Informe a Data de Desembaraço\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I23 - dDesemb\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `VIA_TRANSPORTE` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Via Transporte\",\"labelText\":\"Via Transporte\",\"tooltip\":\"Via Transporte\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I23a - tpViaTransp\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"1=Marítima\"},{\"dropDownButtonItem\":\"2=Fluvial\"},{\"dropDownButtonItem\":\"3=Lacustre\"},{\"dropDownButtonItem\":\"4=Aérea\"},{\"dropDownButtonItem\":\"5=Postal\"},{\"dropDownButtonItem\":\"6=Ferroviária\"},{\"dropDownButtonItem\":\"7=Rodoviária\"}]}}',
  `VALOR_AFRMM` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor AFRMM\",\"labelText\":\"Valor AFRMM\",\"tooltip\":\"Valor AFRMM\",\"hintText\":\"Informe o Valor Adicional ao Frete para Renovação da Marinha Mercante\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I23b - vAFRMM\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `FORMA_INTERMEDIACAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Forma Intermediação\",\"labelText\":\"Forma Intermediação\",\"tooltip\":\"Forma Intermediação\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I23c - tpIntermedio\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"1=Importação por conta própria\"},{\"dropDownButtonItem\":\"2=Importação por conta e ordem\"},{\"dropDownButtonItem\":\"3=Importação por encomenda\"}]}}',
  `CNPJ` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ\",\"labelText\":\"CNPJ\",\"tooltip\":\"CNPJ\",\"hintText\":\"Informe o CNPJ\",\"validacao\":\"CNPJ\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I23d - CNPJ\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}',
  `UF_TERCEIRO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF Terceiro\",\"labelText\":\"UF Terceiro\",\"tooltip\":\"UF Terceiro\",\"hintText\":\"Informe a UF do Terceiro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I23e - UFTerceiro\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}',
  `CODIGO_EXPORTADOR` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Exportador\",\"labelText\":\"Código Exportador\",\"tooltip\":\"Código Exportador\",\"hintText\":\"Informe o Código Exportador\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I24 - cExportador\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_DET_DEC_IMPORTACAO` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_{1771AE4C-E276-4F94-99EB-BDA61D415790}`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo I01. Produtos e Serviços / Declaração de Importação]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_IMPORTACAO_DETALHE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_IMPORTACAO_DETALHE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DECLARACAO_IMPORTACAO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `NUMERO_ADICAO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número Adição\",\"labelText\":\"Número Adição\",\"tooltip\":\"Número Adição\",\"hintText\":\"Informe o Número Adição\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I26 - nAdicao\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `NUMERO_SEQUENCIAL` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número Sequencial\",\"labelText\":\"Número Sequencial\",\"tooltip\":\"Número Sequencial\",\"hintText\":\"Informe o Número Sequencial\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I27 - nSeqAdicC\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `CODIGO_FABRICANTE_ESTRANGEIRO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Fabricante Estrangeiro\",\"labelText\":\"Código Fabricante Estrangeiro\",\"tooltip\":\"Código Fabricante Estrangeiro\",\"hintText\":\"Informe o Código Fabricante Estrangeiro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I28 - cFabricante\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto\",\"labelText\":\"Valor Desconto\",\"tooltip\":\"Valor Desconto\",\"hintText\":\"Informe o Valor do Desconto\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I29 - vDescDI\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `DRAWBACK` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Drawback\",\"labelText\":\"Drawback\",\"tooltip\":\"Drawback\",\"hintText\":\"Informe o Drawback\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I29a - nDraw\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_IMPORTACAO_DETALHE` (`ID_NFE_DECLARACAO_IMPORTACAO` ASC),
  CONSTRAINT `fk_{788F333A-DCB0-4371-AECF-18A42ABF93B6}`
    FOREIGN KEY (`ID_NFE_DECLARACAO_IMPORTACAO`)
    REFERENCES `fenix`.`NFE_DECLARACAO_IMPORTACAO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo I01. Produtos e Serviços / Declaração de Importação] Adições - I18'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_CANA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_CANA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `SAFRA` VARCHAR(9) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Safra\",\"labelText\":\"Safra\",\"tooltip\":\"Safra\",\"hintText\":\"Safra\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZC02 - safra - Identificação da safra - Informar a safra, AAAA ou AAAA/AAAA\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MES_ANO_REFERENCIA` VARCHAR(7) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Mês/Ano Referência\",\"labelText\":\"Mês/Ano Referência\",\"tooltip\":\"Mês/Ano Referência\",\"hintText\":\"Informe o Mês/Ano Referência\",\"validacao\":\"MES_ANO\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZC03 - ref\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"MES_ANO\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_CAB_CANA` (`ID_NFE_CABECALHO` ASC),
  CONSTRAINT `fk_{D05C4271-B5E5-47EB-BD5B-3885FEA3E899}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo ZC. Informações do Registro de Aquisição de Cana]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_CANA_FORNECIMENTO_DIARIO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_CANA_FORNECIMENTO_DIARIO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CANA` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `DIA` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Dia\",\"labelText\":\"Dia\",\"tooltip\":\"Dia\",\"hintText\":\"Informe o Dia\",\"validacao\":\"DIA\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZC04 - dia\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"DIA\"}}\n',
  `QUANTIDADE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade\",\"labelText\":\"Quantidade\",\"tooltip\":\"Quantidade\",\"hintText\":\"Informe a Quantidade\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZC06 - qtde\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `QUANTIDADE_TOTAL_MES` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade Total Mês\",\"labelText\":\"Quantidade Total Mês\",\"tooltip\":\"Quantidade Total Mês\",\"hintText\":\"Informe a Quantidade Total Mês\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZC07 - qTotMes\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `QUANTIDADE_TOTAL_ANTERIOR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade Total Anterior\",\"labelText\":\"Quantidade Total Anterior\",\"tooltip\":\"Quantidade Total Anterior\",\"hintText\":\"Informe a Quantidade Total Anterior\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZC08 - qTotAnt\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `QUANTIDADE_TOTAL_GERAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade Total Geral\",\"labelText\":\"Quantidade Total Geral\",\"tooltip\":\"Quantidade Total Geral\",\"hintText\":\"Informe a Quantidade Total Geral\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZC09 - qTotGer\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_CANA_FORNECIMENTO` (`ID_NFE_CANA` ASC),
  CONSTRAINT `fk_{71A66239-C538-4B83-AD23-EFC8F3FA0AB4}`
    FOREIGN KEY (`ID_NFE_CANA`)
    REFERENCES `fenix`.`NFE_CANA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo ZC. Informações do Registro de Aquisição de Cana] ZC04'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_CANA_DEDUCOES_SAFRA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_CANA_DEDUCOES_SAFRA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CANA` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `DECRICAO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZC11 - xDed\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VALOR_DEDUCAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Dedução\",\"labelText\":\"Valor Dedução\",\"tooltip\":\"Valor Dedução\",\"hintText\":\"Informe o Valor Dedução\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZC12 - vDed\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_FORNECIMENTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Fornecimento\",\"labelText\":\"Valor Fornecimento\",\"tooltip\":\"Valor Fornecimento\",\"hintText\":\"Informe o Valor Fornecimento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"XC13 - vFor\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_TOTAL_DEDUCAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Dedução\",\"labelText\":\"Valor Total Dedução\",\"tooltip\":\"Valor Total Dedução\",\"hintText\":\"Informe o Valor Total Dedução\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZC14 - vTotDed\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_LIQUIDO_FORNECIMENTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Líquido Fornecimento\",\"labelText\":\"Valor Líquido Fornecimento\",\"tooltip\":\"Valor Líquido Fornecimento\",\"hintText\":\"Informe o Valor Líquido Fornecimento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZC15 - vLiqFor\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_CANA_DEDUCOES` (`ID_NFE_CANA` ASC),
  CONSTRAINT `fk_{3C5FF51C-922A-47C7-B352-E32615CA2327}`
    FOREIGN KEY (`ID_NFE_CANA`)
    REFERENCES `fenix`.`NFE_CANA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo ZC. Informações do Registro de Aquisição de Cana] ZC10'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_CUPOM_FISCAL_REFERENCIADO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_CUPOM_FISCAL_REFERENCIADO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `MODELO_DOCUMENTO_FISCAL` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modelo Documento Fiscal\",\"labelText\":\"Modelo Documento Fiscal\",\"tooltip\":\"Modelo Documento Fiscal\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA21 - mod\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char(2)\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"2B=Cupom Fiscal emitido por máquina registradora\"},{\"dropDownButtonItem\":\"2C=Cupom Fiscal PDV\"},{\"dropDownButtonItem\":\"2D=Cupom Fiscal-emitido por ECF\"}]}}',
  `NUMERO_ORDEM_ECF` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número de Ordem ECF\",\"labelText\":\"Número de Ordem ECF\",\"tooltip\":\"Número de Ordem ECF\",\"hintText\":\"Informe o Número de Ordem ECF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA22 - nECF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `COO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"COO\",\"labelText\":\"COO\",\"tooltip\":\"COO\",\"hintText\":\"Informe o COO\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA23 - nCOO\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `DATA_EMISSAO_CUPOM` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Emissão\",\"labelText\":\"Data de Emissão\",\"tooltip\":\"Data de Emissão\",\"hintText\":\"Informe a Data de Emissão\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Utilizado na geração do SPED - REGISTRO C114\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `NUMERO_CAIXA` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número Caixa\",\"labelText\":\"Número Caixa\",\"tooltip\":\"Número Caixa\",\"hintText\":\"Informe o Número Caixa\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Utilizado na geração do SPED - REGISTRO C114\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `NUMERO_SERIE_ECF` VARCHAR(21) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número Série ECF\",\"labelText\":\"Número Série ECF\",\"tooltip\":\"Número Série ECF\",\"hintText\":\"Informe o Número Série do ECF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Utilizado na geração do SPED - REGISTRO C114\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_CF_REFERENCIADO` (`ID_NFE_CABECALHO` ASC),
  CONSTRAINT `fk_{0B096A6D-C97B-4DE5-B93D-254B25C37CC5}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo BA. Documento Fiscal Referenciado] Informações do Cupom Fiscal referenciado'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_NUMERO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_NUMERO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `SERIE` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Série\",\"labelText\":\"Série\",\"tooltip\":\"Série\",\"hintText\":\"Informe o Série\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número NF\",\"labelText\":\"Número NF\",\"tooltip\":\"Número NF\",\"hintText\":\"Informe o Número NF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`))
COMMENT = 'Guarda o número da última nota emitida'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_PROD_RURAL_REFERENCIADA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_PROD_RURAL_REFERENCIADA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CODIGO_UF` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código UF\",\"labelText\":\"Código UF\",\"tooltip\":\"Código UF\",\"hintText\":\"Importe o Código UF\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"uf\",\"campoLookup\":\"codigo_ibge\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"comentario\":\"BA11 - cUF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ANO_MES` VARCHAR(4) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Ano/Mês\",\"labelText\":\"Ano/Mês\",\"tooltip\":\"Ano/Mês\",\"hintText\":\"Informe o Ano/Mês\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA12 - AAMM\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CNPJ` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ\",\"labelText\":\"CNPJ\",\"tooltip\":\"CNPJ\",\"hintText\":\"Informe o CNPJ\",\"validacao\":\"CNPJ\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA13 - CNPJ\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}',
  `CPF` VARCHAR(11) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CPF\",\"labelText\":\"CPF\",\"tooltip\":\"CPF\",\"hintText\":\"Informe o CPF\",\"validacao\":\"CPF\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA14 - CPF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CPF\"}}',
  `INSCRICAO_ESTADUAL` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Incrição Estadual\",\"labelText\":\"Incrição Estadual\",\"tooltip\":\"Incrição Estadual\",\"hintText\":\"Informe a Incrição Estadual\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA15 - IE\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MODELO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modelo\",\"labelText\":\"Modelo\",\"tooltip\":\"Modelo\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA16 - mod\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char(2)\",\"valorPadrao\":\"\",\"itens\":[\n{\"dropDownButtonItem\":\"04=NF de Produtor\"},\n{\"dropDownButtonItem\":\"01=NF\"}]}}',
  `SERIE` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Série\",\"labelText\":\"Série\",\"tooltip\":\"Série\",\"hintText\":\"Informe o Série\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA17 - serie\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO_NF` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número NF\",\"labelText\":\"Número NF\",\"tooltip\":\"Número NF\",\"hintText\":\"Informe a Número NF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA18 - nNF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_RURAL_REFERENCIADO` (`ID_NFE_CABECALHO` ASC),
  CONSTRAINT `fk_{7C9760BE-F8ED-418D-AED9-1F65ACB76F67}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo BA. Documento Fiscal Referenciado] Grupo de informações da NF de produtor rural referenciada'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_NF_REFERENCIADA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_NF_REFERENCIADA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CODIGO_UF` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código UF\",\"labelText\":\"Código UF\",\"tooltip\":\"Código UF\",\"hintText\":\"Importe o Código UF\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"uf\",\"campoLookup\":\"codigo_ibge\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"comentario\":\"BA04 - cUF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ANO_MES` VARCHAR(4) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Ano/Mês\",\"labelText\":\"Ano/Mês\",\"tooltip\":\"Ano/Mês\",\"hintText\":\"Informe o Ano/Mês\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA05 - AAMM\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CNPJ` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ\",\"labelText\":\"CNPJ\",\"tooltip\":\"CNPJ\",\"hintText\":\"Informe o CNPJ\",\"validacao\":\"CNPJ\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA06 - CNPJ\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}',
  `MODELO` CHAR(2) NULL DEFAULT '01' COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modelo\",\"labelText\":\"Modelo\",\"tooltip\":\"Modelo\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA07 - mod\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char(2)\",\"valorPadrao\":\"\",\"itens\":[\n{\"dropDownButtonItem\":\"01=modelo 01\"},\n{\"dropDownButtonItem\":\"02=modelo 02\"}]}}',
  `SERIE` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Série\",\"labelText\":\"Série\",\"tooltip\":\"Série\",\"hintText\":\"Informe o Série\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA08 - serie\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO_NF` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número NF\",\"labelText\":\"Número NF\",\"tooltip\":\"Número NF\",\"hintText\":\"Informe o Número NF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA09 - nNF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_NF_REFERENCIADA` (`ID_NFE_CABECALHO` ASC),
  CONSTRAINT `fk_{172BA844-7467-4E30-BB11-F98E00D75E8F}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo BA. Documento Fiscal Referenciado] Grupo de informação da NF modelo 1/1A referenciada. '
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DETALHE_IMPOSTO_ICMS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DETALHE_IMPOSTO_ICMS` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ORIGEM_MERCADORIA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Origem da Mercadoria\",\"labelText\":\"Origem da Mercadoria\",\"tooltip\":\"Origem da Mercadoria\",\"hintText\":\"Informe a Origem da Mercadoria\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N11 - orig\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaIcmsOrigemMercadoria\"}]}}',
  `CST_ICMS` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CST\",\"labelText\":\"CST\",\"tooltip\":\"CST\",\"hintText\":\"Informe o CST\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cst_ipi\",\"campoLookup\":\"codigo\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"desenhaControle\":true,\"comentario\":\"N12 - CST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CSOSN` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CSOSN\",\"labelText\":\"CSOSN\",\"tooltip\":\"CSOSN\",\"hintText\":\"Informe o CSOSN\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"csosn\",\"campoLookup\":\"codigo\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"desenhaControle\":true,\"comentario\":\"N12a - CSOSN\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MODALIDADE_BC_ICMS` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modalidade Base Cálculo\",\"labelText\":\"Modalidade Base Cálculo\",\"tooltip\":\"Modalidade Base Cálculo\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N13 - modBC\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[\n{\"dropDownButtonItem\":\"0=Margem Valor Agregado (%)\"},\n{\"dropDownButtonItem\":\"1=Pauta (valor)\"},\n{\"dropDownButtonItem\":\"2=Preço Tabelado Máximo (valor)\"},\n{\"dropDownButtonItem\":\"3=Valor da Operação\"}]}}',
  `PERCENTUAL_REDUCAO_BC_ICMS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Percentual Redução BC ICMS\",\"labelText\":\"Percentual Redução BC ICMS\",\"tooltip\":\"Percentual Redução BC ICMS\",\"hintText\":\"Informe o Percentual Redução BC ICMS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N14 - pRedBC\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_BC_ICMS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC ICMS\",\"labelText\":\"Valor BC ICMS\",\"tooltip\":\"Valor BC ICMS\",\"hintText\":\"Informe o Valor BC ICMS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N15 - vBC\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ALIQUOTA_ICMS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota ICMS\",\"labelText\":\"Alíquota ICMS\",\"tooltip\":\"Alíquota ICMS\",\"hintText\":\"Informe o Alíquota ICMS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N16 - pICMS\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_ICMS_OPERACAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor ICMS Operação\",\"labelText\":\"Valor ICMS Operação\",\"tooltip\":\"Valor ICMS Operação\",\"hintText\":\"Informe o Valor ICMS Operação\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N16a - vICMSOp\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `PERCENTUAL_DIFERIMENTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Percentual Diferimento\",\"labelText\":\"Percentual Diferimento\",\"tooltip\":\"Percentual Diferimento\",\"hintText\":\"Informe o Percentual Diferimento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N16b - pDif\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_ICMS_DIFERIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor ICMS Diferido\",\"labelText\":\"Valor ICMS Diferido\",\"tooltip\":\"Valor ICMS Diferido\",\"hintText\":\"Informe o Valor ICMS Diferido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N16c - vICMSDif\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_ICMS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor ICMS\",\"labelText\":\"Valor ICMS\",\"tooltip\":\"Valor ICMS\",\"hintText\":\"Informe o Valor ICMS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N17 - vICMS\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `BASE_CALCULO_FCP` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC FCP\",\"labelText\":\"Valor BC FCP\",\"tooltip\":\"Valor BC FCP\",\"hintText\":\"Informe o Valor BC FCP\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N17a - vBCFCP\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `PERCENTUAL_FCP` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Percentual FCP\",\"labelText\":\"Percentual FCP\",\"tooltip\":\"Percentual FCP\",\"hintText\":\"Informe o Percentual FCP\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N17b - pFCP\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_FCP` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor FCP\",\"labelText\":\"Valor FCP\",\"tooltip\":\"Valor FCP\",\"hintText\":\"Informe o Valor FCP\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N17c - vFCP\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `MODALIDADE_BC_ICMS_ST` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Indicador Exibilidade\",\"labelText\":\"Indicador Exibilidade\",\"tooltip\":\"Indicador Exibilidade\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N18 - modBCST\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[\n{\"dropDownButtonItem\":\"0=Preço tabelado ou máximo sugerido\"},\n{\"dropDownButtonItem\":\"1=Lista Negativa (valor)\"},\n{\"dropDownButtonItem\":\"2=Lista Positiva (valor)\"},\n{\"dropDownButtonItem\":\"3=Lista Neutra (valor)\"},\n{\"dropDownButtonItem\":\"4=Margem Valor Agregado (%)\"},\n{\"dropDownButtonItem\":\"5=Pauta (valor)\"}]}}',
  `PERCENTUAL_MVA_ICMS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Percentual MVA ICMS ST\",\"labelText\":\"Percentual MVA ICMS ST\",\"tooltip\":\"Percentual MVA ICMS ST\",\"hintText\":\"Informe o Percentual MVA ICMS ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N19 - pMVAST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `PERCENTUAL_REDUCAO_BC_ICMS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Percentual Redução BC ICMS ST\",\"labelText\":\"Percentual Redução BC ICMS ST\",\"tooltip\":\"Percentual Redução BC ICMS ST\",\"hintText\":\"Informe o Percentual Redução BC ICMS ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N20 - pRedBCST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_BASE_CALCULO_ICMS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC ICMS ST\",\"labelText\":\"Valor BC ICMS ST\",\"tooltip\":\"Valor BC ICMS ST\",\"hintText\":\"Informe o Valor BC ICMS ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N21 - vBCST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ALIQUOTA_ICMS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota ICMS ST\",\"labelText\":\"Alíquota ICMS ST\",\"tooltip\":\"Alíquota ICMS ST\",\"hintText\":\"Informe o Alíquota ICMS ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N22 - pICMSST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_ICMS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor ICMS ST\",\"labelText\":\"Valor ICMS ST\",\"tooltip\":\"Valor ICMS ST\",\"hintText\":\"Informe o Valor ICMS ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N23 - vICMSST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `BASE_CALCULO_FCP_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC FCP ST\",\"labelText\":\"Valor BC FCP ST\",\"tooltip\":\"Valor BC FCP ST\",\"hintText\":\"Informe o Valor BC FCP ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N23a - vBCFCPST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `PERCENTUAL_FCP_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Percentual FCP ST\",\"labelText\":\"Percentual FCP ST\",\"tooltip\":\"Percentual FCP ST\",\"hintText\":\"Informe o Percentual FCP ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N23b - pFCPST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_FCP_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor FCP ST\",\"labelText\":\"Valor FCP ST\",\"tooltip\":\"Valor FCP ST\",\"hintText\":\"Informe o Valor FCP ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N23d - vFCPST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `UF_ST` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N24 - UFST\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}',
  `PERCENTUAL_BC_OPERACAO_PROPRIA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Percentual BC Operação Própria\",\"labelText\":\"Percentual BC Operação Própria\",\"tooltip\":\"Percentual BC Operação Própria\",\"hintText\":\"Informe o Percentual BC Operação Própria\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N25 - pBCOp\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_BC_ICMS_ST_RETIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC ICMS ST Retido\",\"labelText\":\"Valor BC ICMS ST Retido\",\"tooltip\":\"Valor BC ICMS ST Retido\",\"hintText\":\"Informe o Valor BC ICMS ST Retido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N26 - vBCSTRet\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ALIQUOTA_SUPORTADA_CONSUMIDOR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Suportada Consumidor\",\"labelText\":\"Alíquota Suportada Consumidor\",\"tooltip\":\"Alíquota Suportada Consumidor\",\"hintText\":\"Informe o Alíquota Suportada Consumidor\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N26a - pST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_ICMS_SUBSTITUTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor ICMS Substituto\",\"labelText\":\"Valor ICMS Substituto\",\"tooltip\":\"Valor ICMS Substituto\",\"hintText\":\"Informe o Valor ICMS Substituto\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N26b - vICMSSubstituto\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_ICMS_ST_RETIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor ICMS ST Retido\",\"labelText\":\"Valor ICMS ST Retido\",\"tooltip\":\"Valor ICMS ST Retido\",\"hintText\":\"Informe o Valor ICMS ST Retido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N27 - vICMSSTRet\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `BASE_CALCULO_FCP_ST_RETIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC FCP ST Retido\",\"labelText\":\"Valor BC FCP ST Retido\",\"tooltip\":\"Valor BC FCP ST Retido\",\"hintText\":\"Informe o Valor BC FCP ST Retido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N27a - vBCFCPSTRet\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `PERCENTUAL_FCP_ST_RETIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Percentual FCP ST Retido\",\"labelText\":\"Percentual FCP ST Retido\",\"tooltip\":\"Percentual FCP ST Retido\",\"hintText\":\"Informe o Percentual FCP ST Retido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N27b - pFCPSTRet\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_FCP_ST_RETIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor FCP ST Retido\",\"labelText\":\"Valor FCP ST Retido\",\"tooltip\":\"Valor FCP ST Retido\",\"hintText\":\"Informe o Valor FCP ST Retido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N27d - vFCPSTRet\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `MOTIVO_DESONERACAO_ICMS` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N28 - motDesICMS\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaIcmsMotivoDesoneracao\"}]}}',
  `VALOR_ICMS_DESONERADO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor ICMS Desonerado\",\"labelText\":\"Valor ICMS Desonerado\",\"tooltip\":\"Valor ICMS Desonerado\",\"hintText\":\"Informe o Valor ICMS Desonerado\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N28a - vICMSDeson\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ALIQUOTA_CREDITO_ICMS_SN` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Crédito ICMS SN\",\"labelText\":\"Alíquota Crédito ICMS SN\",\"tooltip\":\"Alíquota Crédito ICMS SN\",\"hintText\":\"Informe o Alíquota Crédito ICMS SN\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N29 - pCredSN\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_CREDITO_ICMS_SN` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Crédito ICMS SN\",\"labelText\":\"Valor Crédito ICMS SN\",\"tooltip\":\"Valor Crédito ICMS SN\",\"hintText\":\"Informe o Valor Crédito ICMS SN\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N30 - vCredICMSSN\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_BC_ICMS_ST_DESTINO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC ICMS ST Destino\",\"labelText\":\"Valor BC ICMS ST Destino\",\"tooltip\":\"Valor BC ICMS ST Destino\",\"hintText\":\"Informe o Valor BC ICMS ST Destino\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N31 - vBCSTDest\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_ICMS_ST_DESTINO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor ICMS ST Destino\",\"labelText\":\"Valor ICMS ST Destino\",\"tooltip\":\"Valor ICMS ST Destino\",\"hintText\":\"Informe o Valor ICMS ST Destino\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N32 - vICMSSTDest\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `PERCENTUAL_REDUCAO_BC_EFETIVO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Percentual Redução BC Efetivo\",\"labelText\":\"Percentual Redução BC Efetivo\",\"tooltip\":\"Percentual Redução BC Efetivo\",\"hintText\":\"Informe o Percentual Redução BC Efetivo\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N34 - pRedBCEfet\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_BC_EFETIVO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC Efetivo\",\"labelText\":\"Valor BC Efetivo\",\"tooltip\":\"Valor BC Efetivo\",\"hintText\":\"Informe o Valor BC Efetivo\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N35 - vBCEfet\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ALIQUOTA_ICMS_EFETIVO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota ICMS Efetivo\",\"labelText\":\"Alíquota ICMS Efetivo\",\"tooltip\":\"Alíquota ICMS Efetivo\",\"hintText\":\"Informe o Alíquota ICMS Efetivo\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N36 - pICMSEfet\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_ICMS_EFETIVO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor ICMS Efetivo\",\"labelText\":\"Valor ICMS Efetivo\",\"tooltip\":\"Valor ICMS Efetivo\",\"hintText\":\"Informe o Valor ICMS Efetivo\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"N37 - vICMSEfet\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_DET_ICMS` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_{2ECA610C-69B9-43E7-B65D-F30EDC701A59}`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo N. ICMS Normal e ST]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DETALHE_IMPOSTO_IPI`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DETALHE_IMPOSTO_IPI` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CNPJ_PRODUTOR` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ Produtor\",\"labelText\":\"CNPJ Produtor\",\"tooltip\":\"CNPJ Produtor\",\"hintText\":\"Informe o CNPJ Produtor\",\"validacao\":\"CNPJ\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"O03 - CNPJProd\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}',
  `CODIGO_SELO_IPI` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Selo IPI\",\"labelText\":\"Código Selo IPI\",\"tooltip\":\"Código Selo IPI\",\"hintText\":\"Informe o Código Selo IPI\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"O04 - cSelo\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE_SELO_IPI` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade Selos\",\"labelText\":\"Quantidade Selos\",\"tooltip\":\"Quantidade Selos\",\"hintText\":\"Informe a Quantidade de Selos\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"O05 - qSelo\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `ENQUADRAMENTO_LEGAL_IPI` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código de Enquadramento Legal\",\"labelText\":\"Código de Enquadramento Legal\",\"tooltip\":\"Código de Enquadramento Legal\",\"hintText\":\"Informe o Código de Enquadramento Legal\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"O06 - cEnq\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CST_IPI` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CST IPI\",\"labelText\":\"CST IPI\",\"tooltip\":\"CST IPI\",\"hintText\":\"Informe o CST IPI\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cst_ipi\",\"campoLookup\":\"codigo\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"desenhaControle\":true,\"comentario\":\"O09 - CST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VALOR_BASE_CALCULO_IPI` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC IPI\",\"labelText\":\"Valor BC IPI\",\"tooltip\":\"Valor BC IPI\",\"hintText\":\"Informe o Valor BC IPI\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"O10 - vBC\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `QUANTIDADE_UNIDADE_TRIBUTAVEL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade Unidade Tributável\",\"labelText\":\"Quantidade Unidade Tributável\",\"tooltip\":\"Quantidade Unidade Tributável\",\"hintText\":\"Informe a Quantidade Unidade Tributável\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"O11 - qUnid\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `VALOR_UNIDADE_TRIBUTAVEL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Unidade Tributável\",\"labelText\":\"Valor Unidade Tributável\",\"tooltip\":\"Valor Unidade Tributável\",\"hintText\":\"Informe o Valor Unidade Tributável\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"O12 - vUnid\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ALIQUOTA_IPI` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota IPI\",\"labelText\":\"Alíquota IPI\",\"tooltip\":\"Alíquota IPI\",\"hintText\":\"Informe o Alíquota IPI\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"O13 - pIPI\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_IPI` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor IPI\",\"labelText\":\"Valor IPI\",\"tooltip\":\"Valor IPI\",\"hintText\":\"Informe o Valor IPI\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"O14 - vIPI\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_DET_IPI` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_{848512E8-6818-4FF8-85B2-FD9773A5AAA8}`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo O. Imposto sobre Produtos Industrializados]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DETALHE_IMPOSTO_II`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DETALHE_IMPOSTO_II` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `VALOR_BC_II` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC II\",\"labelText\":\"Valor BC II\",\"tooltip\":\"Valor BC II\",\"hintText\":\"Informe o Valor BC II\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"P02 - vBC\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_DESPESAS_ADUANEIRAS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Despesas Aduaneiras\",\"labelText\":\"Valor Despesas Aduaneiras\",\"tooltip\":\"Valor Despesas Aduaneiras\",\"hintText\":\"Informe o Valor Despesas Aduaneiras\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"P03 - vDespAdu\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_IMPOSTO_IMPORTACAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Imposto Importação\",\"labelText\":\"Valor Imposto Importação\",\"tooltip\":\"Valor Imposto Importação\",\"hintText\":\"Informe o Valor Imposto Importação\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"P04 - vII\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_IOF` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor IOF\",\"labelText\":\"Valor IOF\",\"tooltip\":\"Valor IOF\",\"hintText\":\"Informe o Valor IOF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"P05 - vIOF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_DET_II` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_{DD990476-FEE7-4709-BD8D-A6D372781AE1}`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo P. Imposto de Importação]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DETALHE_IMPOSTO_PIS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DETALHE_IMPOSTO_PIS` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CST_PIS` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CST PIS\",\"labelText\":\"CST PIS\",\"tooltip\":\"CST PIS\",\"hintText\":\"Informe o CST PIS\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cst_ipi\",\"campoLookup\":\"codigo\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"desenhaControle\":true,\"comentario\":\"Q06 - CST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}\n',
  `VALOR_BASE_CALCULO_PIS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC PIS\",\"labelText\":\"Valor BC PIS\",\"tooltip\":\"Valor BC PIS\",\"hintText\":\"Informe o Valor BC PIS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Q07 - vBC\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ALIQUOTA_PIS_PERCENTUAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota PIS Percentual\",\"labelText\":\"Alíquota PIS Percentual\",\"tooltip\":\"Alíquota PIS Percentual\",\"hintText\":\"Informe o Alíquota PIS Percentual\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Q08 - pPIS\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_PIS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor PIS\",\"labelText\":\"Valor PIS\",\"tooltip\":\"Valor PIS\",\"hintText\":\"Informe o Valor PIS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Q09 - vPIS\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `QUANTIDADE_VENDIDA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade Vendida\",\"labelText\":\"Quantidade Vendida\",\"tooltip\":\"Quantidade Vendida\",\"hintText\":\"Informe a Quantidade Vendida\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Q10 - qBCProd\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `ALIQUOTA_PIS_REAIS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota PIS Reais\",\"labelText\":\"Alíquota PIS Reais\",\"tooltip\":\"Alíquota PIS Reais\",\"hintText\":\"Informe o Alíquota PIS Reais\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Q11 - vAliqProd\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_DET_PIS` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_{A901B4CB-EECC-4E0D-8163-E9C48092FBB3}`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo Q. PIS]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DETALHE_IMPOSTO_COFINS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DETALHE_IMPOSTO_COFINS` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CST_COFINS` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CST COFINS\",\"labelText\":\"CST COFINS\",\"tooltip\":\"CST COFINS\",\"hintText\":\"Informe o CST COFINS\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cst_cofins\",\"campoLookup\":\"codigo\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"desenhaControle\":true,\"comentario\":\"CST\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `BASE_CALCULO_COFINS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC COFINS\",\"labelText\":\"Valor BC COFINS\",\"tooltip\":\"Valor BC COFINS\",\"hintText\":\"Informe o Valor BC COFINS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"S07 - vBC\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ALIQUOTA_COFINS_PERCENTUAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota COFINS Percentual\",\"labelText\":\"Alíquota COFINS Percentual\",\"tooltip\":\"Alíquota COFINS Percentual\",\"hintText\":\"Informe o Alíquota COFINS Percentual\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"S08 - pCOFINS\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `QUANTIDADE_VENDIDA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade Vendida\",\"labelText\":\"Quantidade Vendida\",\"tooltip\":\"Quantidade Vendida\",\"hintText\":\"Informe a Quantidade Vendida\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"S09 - qBCProd\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `ALIQUOTA_COFINS_REAIS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota COFINS Reais\",\"labelText\":\"Alíquota COFINS Reais\",\"tooltip\":\"Alíquota COFINS Reais\",\"hintText\":\"Informe o Alíquota COFINS Reais\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"S10 - vAliqProd\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_COFINS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor COFINS\",\"labelText\":\"Valor COFINS\",\"tooltip\":\"Valor COFINS\",\"hintText\":\"Informe o Valor COFINS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"S11 - vCOFINS\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_DET_COFINS` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_{45F41988-B667-4183-8517-DD1800D74792}`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo S. COFINS]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DETALHE_IMPOSTO_ISSQN`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DETALHE_IMPOSTO_ISSQN` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `BASE_CALCULO_ISSQN` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC ISSQN\",\"labelText\":\"Valor BC ISSQN\",\"tooltip\":\"Valor BC ISSQN\",\"hintText\":\"Informe o Valor BC ISSQN\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"U02 - vBC\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ALIQUOTA_ISSQN` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota ISSQN\",\"labelText\":\"Alíquota ISSQN\",\"tooltip\":\"Alíquota ISSQN\",\"hintText\":\"Informe o Alíquota ISSQN\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"U03 - vAliq\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_ISSQN` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor ISSQN\",\"labelText\":\"Valor ISSQN\",\"tooltip\":\"Valor ISSQN\",\"hintText\":\"Informe o Valor ISSQN\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"U04 - vISSQN\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `MUNICIPIO_ISSQN` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município IBGE\",\"labelText\":\"Município IBGE\",\"tooltip\":\"Município IBGE\",\"hintText\":\"Importe o Município IBGE Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"municipio\",\"campoLookup\":\"codigoIbge\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"U05 - cMunFG\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ITEM_LISTA_SERVICOS` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Item Lista Serviços\",\"labelText\":\"Item Lista Serviços\",\"tooltip\":\"Item Lista Serviços\",\"hintText\":\"Informe o Item Lista Serviços\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"U06 - cListServ\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `VALOR_DEDUCAO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Dedução\",\"labelText\":\"Valor Dedução\",\"tooltip\":\"Valor Dedução\",\"hintText\":\"Informe o Valor Dedução\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"U07 - vDeducao\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_OUTRAS_RETENCOES` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Outras Deduções\",\"labelText\":\"Valor Outras Deduções\",\"tooltip\":\"Valor Outras Deduções\",\"hintText\":\"Informe o Valor Outras Deduções\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"U08 - vOutro\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_DESCONTO_INCONDICIONADO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto Incondicionado\",\"labelText\":\"Valor Desconto Incondicionado\",\"tooltip\":\"Valor Desconto Incondicionado\",\"hintText\":\"Informe o Valor Desconto Incondicionado\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"U09 - vDescIncondo\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_DESCONTO_CONDICIONADO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto Condicionado\",\"labelText\":\"Valor Desconto Condicionado\",\"tooltip\":\"Valor Desconto Condicionado\",\"hintText\":\"Informe o Valor Desconto Condicionado\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"U10 - vDescCond\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_RETENCAO_ISS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Retenção ISS\",\"labelText\":\"Valor Retenção ISS\",\"tooltip\":\"Valor Retenção ISS\",\"hintText\":\"Informe o Valor Retenção ISS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"U11 - vISSRet\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `INDICADOR_EXIGIBILIDADE_ISS` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Indicador Exibilidade\",\"labelText\":\"Indicador Exibilidade\",\"tooltip\":\"Indicador Exibilidade\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"U12 - indISS\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[\n{\"dropDownButtonItem\":\"1=Exigível\"},\n{\"dropDownButtonItem\":\"2=Não incidência\"},\n{\"dropDownButtonItem\":\"3=Isenção\"},\n{\"dropDownButtonItem\":\"4=Exportação\"},\n{\"dropDownButtonItem\":\"5=Imunidade\"},\n{\"dropDownButtonItem\":\"6=Exigibilidade Suspensa por Decisão Judicial\"},\n{\"dropDownButtonItem\":\"7=Exigibilidade Suspensa por Processo Administrativo\"}]}}',
  `CODIGO_SERVICO` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Serviço\",\"labelText\":\"Código Serviço\",\"tooltip\":\"Código Serviço\",\"hintText\":\"Informe o Código Serviço\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"U13 - cServico\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MUNICIPIO_INCIDENCIA` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município IBGE\",\"labelText\":\"Município IBGE\",\"tooltip\":\"Município IBGE\",\"hintText\":\"Importe o Município IBGE Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"municipio\",\"campoLookup\":\"codigoIbge\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"U14 - cMun\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PAIS_SEVICO_PRESTADO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"País BACEN\",\"labelText\":\"País BACEN\",\"tooltip\":\"País BACEN\",\"hintText\":\"Importe o País BACEN Vinculado\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":true,\"tabelaLookup\":\"pais\",\"campoLookup\":\"codigoBacen\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"U15 - cPais\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO_PROCESSO` VARCHAR(30) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número Processo\",\"labelText\":\"Número Processo\",\"tooltip\":\"Número Processo\",\"hintText\":\"Informe o Número Processo\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"U16 - nProcesso\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `INDICADOR_INCENTIVO_FISCAL` CHAR(1) NULL COMMENT 'U17 - indIncentivo',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_DET_ISSQN` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_{D3EADE39-4BA1-402D-9AE5-F55A6327DA3B}`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo U. ISSQN]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_TRANSPORTE_REBOQUE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_TRANSPORTE_REBOQUE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_TRANSPORTE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `PLACA` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Placa\",\"labelText\":\"Placa\",\"tooltip\":\"Placa\",\"hintText\":\"Informe a Placa\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X23 - placa\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `UF` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"UF\",\"labelText\":\"UF\",\"tooltip\":\"UF\",\"hintText\":\"Informe a UF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X24 - UF\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaUF\"}]}}',
  `RNTC` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"RNTC\",\"labelText\":\"RNTC\",\"tooltip\":\"RNTC\",\"hintText\":\"Informe o RNTC\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X25 - RNTC\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VAGAO` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Vagão\",\"labelText\":\"Vagão\",\"tooltip\":\"Vagão\",\"hintText\":\"Informe o Vagão\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X25a - vagao\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `BALSA` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Balsa\",\"labelText\":\"Balsa\",\"tooltip\":\"Balsa\",\"hintText\":\"Informe a Balsa\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X25b - balsa\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_TRANSP_REBOQUE` (`ID_NFE_TRANSPORTE` ASC),
  CONSTRAINT `fk_{33120FEA-84FA-45B1-9F95-183D64AC0F6F}`
    FOREIGN KEY (`ID_NFE_TRANSPORTE`)
    REFERENCES `fenix`.`NFE_TRANSPORTE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo X. Informações do Transporte da NF-e] X22'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_TRANSPORTE_VOLUME`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_TRANSPORTE_VOLUME` (
  `ID` INT UNSIGNED NOT NULL,
  `ID_NFE_TRANSPORTE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `QUANTIDADE` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade\",\"labelText\":\"Quantidade\",\"tooltip\":\"Quantidade\",\"hintText\":\"Informe a Quantidade\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X27 - qVol\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `ESPECIE` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Espécie\",\"labelText\":\"Espécie\",\"tooltip\":\"Espécie\",\"hintText\":\"Informe a Espécie\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X28 - esp\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MARCA` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Marca\",\"labelText\":\"Marca\",\"tooltip\":\"Marca\",\"hintText\":\"Informe a Marca\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X29 - marca\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERACAO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Numeração\",\"labelText\":\"Numeração\",\"tooltip\":\"Numeração\",\"hintText\":\"Informe a Numeração\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X30 - nVol\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PESO_LIQUIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Peso Líquido\",\"labelText\":\"Peso Líquido\",\"tooltip\":\"Peso Líquido\",\"hintText\":\"Informe o Peso Líquido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X31 - pesoL\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `PESO_BRUTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Peso Bruto\",\"labelText\":\"Peso Bruto\",\"tooltip\":\"Peso Bruto\",\"hintText\":\"Informe o Peso Bruto\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X32 - pesoB\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_TRANSP_VOLUME` (`ID_NFE_TRANSPORTE` ASC),
  CONSTRAINT `fk_{683237C6-9879-4C55-BBD3-8CD3F1AF00EF}`
    FOREIGN KEY (`ID_NFE_TRANSPORTE`)
    REFERENCES `fenix`.`NFE_TRANSPORTE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo X. Informações do Transporte da NF-e] X26'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_TRANSPORTE_VOLUME_LACRE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_TRANSPORTE_VOLUME_LACRE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_TRANSPORTE_VOLUME` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `NUMERO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"X34 - nLacre\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_TRANSP_VOL_LACRE` (`ID_NFE_TRANSPORTE_VOLUME` ASC),
  CONSTRAINT `fk_{C379B512-7C71-41D4-8D32-7A543923DC1E}`
    FOREIGN KEY (`ID_NFE_TRANSPORTE_VOLUME`)
    REFERENCES `fenix`.`NFE_TRANSPORTE_VOLUME` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo X. Informações do Transporte da NF-e] X33'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_PROCESSO_REFERENCIADO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_PROCESSO_REFERENCIADO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `IDENTIFICADOR` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Identificador\",\"labelText\":\"Identificador\",\"tooltip\":\"Identificador\",\"hintText\":\"Informe o Identificador\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Z11 - nProc\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ORIGEM` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Origem\",\"labelText\":\"Origem\",\"tooltip\":\"Origem\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Z12 - indProc\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[\n{\"dropDownButtonItem\":\"0=SEFAZ\"},\n{\"dropDownButtonItem\":\"1=Justiça Federal\"},\n{\"dropDownButtonItem\":\"2=Justiça Estadual\"},\n{\"dropDownButtonItem\":\"3=Secex/RFB\"},\n{\"dropDownButtonItem\":\"9=Outros\"}]}}',
  PRIMARY KEY (`ID`),
  INDEX `NFE_CAB_PROC_REF` (`ID_NFE_CABECALHO` ASC),
  CONSTRAINT `fk_{9C752FAA-B71F-425A-ACB6-A3E8972E6FF4}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo Z. Informações Adicionais da NF-e]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_CTE_REFERENCIADO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_CTE_REFERENCIADO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CHAVE_ACESSO` VARCHAR(44) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Chave de Acesso\",\"labelText\":\"Chave de Acesso\",\"tooltip\":\"Chave de Acesso\",\"hintText\":\"Chave de Acesso\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"BA19 - refCTe\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_CTE_REFERENCIADO` (`ID_NFE_CABECALHO` ASC),
  CONSTRAINT `fk_{43CDCD03-AB6A-4120-BB27-CBDB6C26B4A7}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo BA. Documento Fiscal Referenciado] refCTe'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_CONFIGURACAO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_CONFIGURACAO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CERTIFICADO_DIGITAL_SERIE` VARCHAR(100) NULL COMMENT 'Numero do Certificado Digital - CAPICOM',
  `CERTIFICADO_DIGITAL_CAMINHO` TEXT NULL COMMENT 'Local do Certificado - OpenSSL',
  `CERTIFICADO_DIGITAL_SENHA` VARCHAR(100) NULL COMMENT 'Senha do Certificado - OpenSSL',
  `TIPO_EMISSAO` INT UNSIGNED NULL COMMENT 'tpEmis - 1 - Normal - emissão normal;  2 - Contingência FS - emissão em contingência com impressão do DANFE em Formulário de Segurança;  3 - Contingência SCAN - emissão em contingência no Sistema de Contingência do Ambiente Nacional - SCAN;  4 - Contingência DPEC - emissão em contingência com envio da Declaração Prévia de Emissão em Contingência - DPEC;  5 - Contingência FS-DA - emissão em contingência com impressão do DANFE em Formulário de Segurança para Impressão de Documento Auxiliar de Documento Fiscal Eletrônico (FS-DA);  6 - Contingência SVC-AN, emissão em contingência na SEFAZ Virtual do Ambiente Nacional;  7 - Contingência SVC-RS, emissão em contingência na SEFAZ Virtual do RS.',
  `FORMATO_IMPRESSAO_DANFE` INT UNSIGNED NULL COMMENT 'tpImp - 1-Retrato/ 2-Paisagem',
  `PROCESSO_EMISSAO` INT UNSIGNED NULL COMMENT 'procEmi - Identificador do processo de  emissão da NF-e:  0 - emissão de NF-e com  aplicativo do contribuinte;  1 - emissão de NF-e avulsa pelo  Fisco;  2 - emissão de NF-e avulsa,  pelo contribuinte com seu  certificado digital, através do site  do Fisco;  3- emissão NF-e pelo  contribuinte com aplicativo  fornecido pelo Fisco.',
  `VERSAO_PROCESSO_EMISSAO` VARCHAR(20) NULL COMMENT 'verProc - Identificador da versão do  processo de emissão (informar  a versão do aplicativo emissor  de NF-e).',
  `CAMINHO_LOGOMARCA` TEXT NULL COMMENT 'Caminho para logo marca',
  `SALVAR_XML` CHAR(1) NULL COMMENT 'Salva Arquivo XML - [S=Sim | N=Não]',
  `CAMINHO_SALVAR_XML` TEXT NULL COMMENT 'Caminho onde vai salvar o arquivo',
  `CAMINHO_SCHEMAS` TEXT NULL COMMENT 'Caminho dos Schemas XML',
  `CAMINHO_ARQUIVO_DANFE` TEXT NULL,
  `CAMINHO_SALVAR_PDF` TEXT NULL,
  `WEBSERVICE_UF` CHAR(2) NULL COMMENT 'UF do Servidor',
  `WEBSERVICE_AMBIENTE` INT UNSIGNED NULL COMMENT 'tpAmb - 1-Produção/ 2-Homologação',
  `WEBSERVICE_PROXY_HOST` VARCHAR(100) NULL,
  `WEBSERVICE_PROXY_PORTA` INT UNSIGNED NULL,
  `WEBSERVICE_PROXY_USUARIO` VARCHAR(100) NULL,
  `WEBSERVICE_PROXY_SENHA` VARCHAR(100) NULL,
  `WEBSERVICE_VISUALIZAR` CHAR(1) NULL COMMENT '[S=Sim | N=Não]',
  `EMAIL_SERVIDOR_SMTP` VARCHAR(100) NULL COMMENT 'Host para envio de email',
  `EMAIL_PORTA` INT UNSIGNED NULL COMMENT 'Porta do Host',
  `EMAIL_USUARIO` VARCHAR(100) NULL COMMENT 'Usuario email',
  `EMAIL_SENHA` VARCHAR(100) NULL COMMENT 'Senha email',
  `EMAIL_ASSUNTO` VARCHAR(100) NULL COMMENT 'Assunto do Email',
  `EMAIL_AUTENTICA_SSL` CHAR(1) NULL COMMENT '[S=Sim | N=Não]',
  `EMAIL_TEXTO` TEXT NULL,
  PRIMARY KEY (`ID`))
COMMENT = 'Configurações para a nota fiscal eletrônica'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_ACESSO_XML`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_ACESSO_XML` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CNPJ` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ\",\"labelText\":\"CNPJ\",\"tooltip\":\"CNPJ\",\"hintText\":\"Informe o CNPJ\",\"validacao\":\"CNPJ\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"GA02 - CNPJ\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}\n',
  `CPF` VARCHAR(11) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CPF\",\"labelText\":\"CPF\",\"tooltip\":\"CPF\",\"hintText\":\"Informe o CPF\",\"validacao\":\"CPF\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"GA03 - CPF\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CPF\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_ACESSO_XML` (`ID_NFE_CABECALHO` ASC),
  CONSTRAINT `fk_{B466EFD3-8C2D-4EFD-B9AA-9DCEAAF57FA8}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo GA. Autorização para obter XML]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_EXPORTACAO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_EXPORTACAO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `DRAWBACK` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Drawback\",\"labelText\":\"Drawback\",\"tooltip\":\"Drawback\",\"hintText\":\"Informe o Drawback\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I51 - nDraw\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `NUMERO_REGISTRO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número Registro\",\"labelText\":\"Número Registro\",\"tooltip\":\"Número Registro\",\"hintText\":\"Informe o Número Registro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I53 - nRE\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `CHAVE_ACESSO` VARCHAR(44) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Chave de Acesso\",\"labelText\":\"Chave de Acesso\",\"tooltip\":\"Chave de Acesso\",\"hintText\":\"Chave de Acesso\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I54 - chNFe\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade\",\"labelText\":\"Quantidade\",\"tooltip\":\"Quantidade\",\"hintText\":\"Informe a Quantidade\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I55 - qExport\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_EXPORTACAO` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_{29DDD06F-DDD4-4255-94EE-1E32334EB138}`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo I03. Produtos e Serviços / Grupo de Exportação]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_INFORMACAO_PAGAMENTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_INFORMACAO_PAGAMENTO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `INDICADOR_PAGAMENTO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Indicador Pagamento\",\"labelText\":\"Indicador Pagamento\",\"tooltip\":\"Indicador Pagamento\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"YA01b - indPag\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[\n{\"dropDownButtonItem\":\"0=Pagamento à vista\"},\n{\"dropDownButtonItem\":\"1=Pagamento à prazo\"}]}}',
  `MEIO_PAGAMENTO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Meio de Pagamento\",\"labelText\":\"Meio de Pagamento\",\"tooltip\":\"Meio de Pagamento\",\"hintText\":\"Informe a Meio de Pagamento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"YA02 - tPag\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"valor\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"listaNfeMeioPagamento\"}]}}',
  `VALOR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor\",\"labelText\":\"Valor\",\"tooltip\":\"Valor\",\"hintText\":\"Informe o Valor\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"YA03 - vPag\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TIPO_INTEGRACAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Integração\",\"labelText\":\"Tipo Integração\",\"tooltip\":\"Tipo Integração\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"YA04a - tpIntegra\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[\n{\"dropDownButtonItem\":\"1=Pagamento integrado com o sistema de automação da empresa\"},\n{\"dropDownButtonItem\":\"2=Pagamento não integrado com o sistema de automação da empresa\"}]}}',
  `CNPJ_OPERADORA_CARTAO` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ\",\"labelText\":\"CNPJ\",\"tooltip\":\"CNPJ\",\"hintText\":\"Informe o CNPJ\",\"validacao\":\"CNPJ\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"YA05 - CNPJ\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}',
  `BANDEIRA` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Bandeira\",\"labelText\":\"Bandeira\",\"tooltip\":\"Bandeira\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"YA06 - tBand\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[\n{\"dropDownButtonItem\":\"01=Visa\"},\n{\"dropDownButtonItem\":\"02=Mastercard\"},\n{\"dropDownButtonItem\":\"03=American Express\"},\n{\"dropDownButtonItem\":\"04=Sorocred\"},\n{\"dropDownButtonItem\":\"99=Outros\"}]}}',
  `NUMERO_AUTORIZACAO` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número Autorização\",\"labelText\":\"Número Autorização\",\"tooltip\":\"Número Autorização\",\"hintText\":\"Informe o Número Autorização\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"YA07 - cAut\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `TROCO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Troco\",\"labelText\":\"Troco\",\"tooltip\":\"Troco\",\"hintText\":\"Informe o Troco\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"YA09 - vTroco\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_FORMA_PAGAMENTO` (`ID_NFE_CABECALHO` ASC),
  CONSTRAINT `fk_{BFB74DEB-FAC6-4F44-8FDA-F52BFEC321ED}`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo YA. Informações de Pagamento]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_NUMERO_INUTILIZADO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_NUMERO_INUTILIZADO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `SERIE` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Série\",\"labelText\":\"Série\",\"tooltip\":\"Série\",\"hintText\":\"Informe o Série\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número NF\",\"labelText\":\"Número NF\",\"tooltip\":\"Número NF\",\"hintText\":\"Informe a Número NF\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}\n',
  `DATA_INUTILIZACAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Inutilização\",\"labelText\":\"Data de Inutilização\",\"tooltip\":\"Data de Inutilização\",\"hintText\":\"Informe a Data de Inutilização\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `OBSERVACAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Guarda os números das notas inutilizadas'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_ITEM_RASTREADO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_ITEM_RASTREADO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `NUMERO_LOTE` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número do Lote\",\"labelText\":\"Número do Lote\",\"tooltip\":\"Número do Lote\",\"hintText\":\"Informe o Número do Lote\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I81 - nLote\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE_ITENS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade Itens\",\"labelText\":\"Quantidade Itens\",\"tooltip\":\"Quantidade Itens\",\"hintText\":\"Informe a Quantidade Itens\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I82 - \",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}qLote',
  `DATA_FABRICACAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Fabricação\",\"labelText\":\"Data de Fabricação\",\"tooltip\":\"Data de Fabricação\",\"hintText\":\"Informe a Data de Fabricação\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I83 - dFab\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `DATA_VALIDADE` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Validade\",\"labelText\":\"Data de Validade\",\"tooltip\":\"Data de Validade\",\"hintText\":\"Informe a Data de Validade\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I84 - dVal\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `CODIGO_AGREGACAO` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Agregação\",\"labelText\":\"Código Agregação\",\"tooltip\":\"Código Agregação\",\"hintText\":\"Informe o Código Agregação\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"I85 - cAgreg\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_NFE_ITEM_RASTREADO_NFE_DETALHE1_idx` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_NFE_ITEM_RASTREADO_NFE_DETALHE1`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = '[Grupo I80. Rastreabilidade de produto]';


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DETALHE_IMPOSTO_PIS_ST`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DETALHE_IMPOSTO_PIS_ST` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `VALOR_BASE_CALCULO_PIS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Base Cálculo PIS ST\",\"labelText\":\"Base Cálculo PIS ST\",\"tooltip\":\"Base Cálculo PIS ST\",\"hintText\":\"Informe a Base de Cálculo do PIS ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"R02 - vBC\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ALIQUOTA_PIS_ST_PERCENTUAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota PIS ST Percentual\",\"labelText\":\"Alíquota PIS ST Percentual\",\"tooltip\":\"Alíquota PIS ST Percentual\",\"hintText\":\"Informe o Alíquota PIS ST Percentual\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"R03 - pPIS\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `QUANTIDADE_VENDIDA_PIS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade Vendida PIS ST\",\"labelText\":\"Quantidade Vendida PIS ST\",\"tooltip\":\"Quantidade Vendida PIS ST\",\"hintText\":\"Informe a Quantidade Vendida PIS ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"R04 - qBCProd\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `ALIQUOTA_PIS_ST_REAIS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota PIS ST Reais\",\"labelText\":\"Alíquota PIS ST Reais\",\"tooltip\":\"Alíquota PIS ST Reais\",\"hintText\":\"Informe o Alíquota PIS ST Reais\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"R05 - vAliqProd\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_PIS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor PIS ST\",\"labelText\":\"Valor PIS ST\",\"tooltip\":\"Valor PIS ST\",\"hintText\":\"Informe o Valor PIS ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"R06 - vPIS\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_DET_PIS` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_{A901B4CB-EECC-4E0D-8163-E9C48092FBB3}0`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo R. PIS ST]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DETALHE_IMPOSTO_ICMS_UFDEST`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DETALHE_IMPOSTO_ICMS_UFDEST` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `VALOR_BC_ICMS_UF_DESTINO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC ICMS UF Destino\",\"labelText\":\"Valor BC ICMS UF Destino\",\"tooltip\":\"Valor BC ICMS UF Destino\",\"hintText\":\"Informe o Valor BC ICMS UF Destino\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"NA03 - vBCUFDest\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_BC_FCP_UF_DESTINO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC FCP UF Destino\",\"labelText\":\"Valor BC FCP UF Destino\",\"tooltip\":\"Valor BC FCP UF Destino\",\"hintText\":\"Informe o Valor BC FCP UF Destino\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"NA04 - vBCFCPUFDest\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `PERCENTUAL_FCP_UF_DESTINO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Percentual FCP UF Destino\",\"labelText\":\"Percentual FCP UF Destino\",\"tooltip\":\"Percentual FCP UF Destino\",\"hintText\":\"Informe o Percentual FCP UF Destino\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"NA05 - pFCPUFDest\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `ALIQUOTA_INTERNA_UF_DESTINO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Interna UF Destino\",\"labelText\":\"Alíquota Interna UF Destino\",\"tooltip\":\"Alíquota Interna UF Destino\",\"hintText\":\"Informe o Alíquota Interna UF Destino\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"NA07 - pICMSUFDest\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `ALIQUOTA_INTERESDATUAL_UF_ENVOLVIDAS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota Interestadual UF Envolvidas\",\"labelText\":\"Alíquota Interestadual UF Envolvidas\",\"tooltip\":\"Alíquota Interestadual UF Envolvidas\",\"hintText\":\"Informe o Alíquota Interestadual UF Envolvidas\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"NA09 - pICMSInter\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `PERCENTUAL_PROVISORIO_PARTILHA_ICMS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Percentual Provisório Partilha ICMS\",\"labelText\":\"Percentual Provisório Partilha ICMS\",\"tooltip\":\"Percentual Provisório Partilha ICMS\",\"hintText\":\"Informe o Percentual Provisório Partilha ICMS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"NA11 - pICMSInterPart\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_ICMS_FCP_UF_DESTINO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor ICMS FCP UF Destino\",\"labelText\":\"Valor ICMS FCP UF Destino\",\"tooltip\":\"Valor ICMS FCP UF Destino\",\"hintText\":\"Informe o Valor ICMS FCP UF Destino\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"NA13 - vFCPUFDest\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_INTERESTADUAL_UF_DESTINO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Interestadual UF Destino\",\"labelText\":\"Valor Interestadual UF Destino\",\"tooltip\":\"Valor Interestadual UF Destino\",\"hintText\":\"Informe o Valor Interestadual UF Destino\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"NA15 - vICMSUFDest\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_INTERESTADUAL_UF_REMETENTE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Interestadual UF Remetente\",\"labelText\":\"Valor Interestadual UF Remetente\",\"tooltip\":\"Valor Interestadual UF Remetente\",\"hintText\":\"Informe o Valor Interestadual UF Remetente\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"NA17 - vICMSUFRemet\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_DET_ICMS` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_{2ECA610C-69B9-43E7-B65D-F30EDC701A59}0`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo NA. ICMS para a UF de destino]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_DETALHE_IMPOSTO_COFINS_ST`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_DETALHE_IMPOSTO_COFINS_ST` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFE_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `BASE_CALCULO_COFINS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor BC COFINS ST\",\"labelText\":\"Valor BC COFINS ST\",\"tooltip\":\"Valor BC COFINS ST\",\"hintText\":\"Informe o Valor BC COFINS ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"T02 - vBC\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ALIQUOTA_COFINS_ST_PERCENTUAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota COFINS ST Percentual\",\"labelText\":\"Alíquota COFINS ST Percentual\",\"tooltip\":\"Alíquota COFINS ST Percentual\",\"hintText\":\"Informe o Alíquota COFINS ST Percentual\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"T03 - pCOFINS\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `QUANTIDADE_VENDIDA_COFINS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade Venda COFINS ST\",\"labelText\":\"Quantidade Venda COFINS ST\",\"tooltip\":\"Quantidade Venda COFINS ST\",\"hintText\":\"Informe a Quantidade Venda COFINS ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"T04 - qBCProd\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `ALIQUOTA_COFINS_ST_REAIS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota COFINS ST Reais\",\"labelText\":\"Alíquota COFINS ST Reais\",\"tooltip\":\"Alíquota COFINS ST Reais\",\"hintText\":\"Informe o Alíquota COFINS ST Reais\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"T05 - vAliqProd\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_COFINS_ST` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor COFINS ST\",\"labelText\":\"Valor COFINS ST\",\"tooltip\":\"Valor COFINS ST\",\"hintText\":\"Informe o Valor COFINS ST\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"T06 - vCOFINS\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFE_DET_COFINS` (`ID_NFE_DETALHE` ASC),
  CONSTRAINT `fk_{45F41988-B667-4183-8517-DD1800D74792}0`
    FOREIGN KEY (`ID_NFE_DETALHE`)
    REFERENCES `fenix`.`NFE_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '[Grupo T. COFINS ST]'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFE_RESPONSAVEL_TECNICO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFE_RESPONSAVEL_TECNICO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_NFE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CNPJ` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ\",\"labelText\":\"CNPJ\",\"tooltip\":\"CNPJ\",\"hintText\":\"Informe o CNPJ\",\"validacao\":\"CNPJ\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZD02 - CNPJ\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}',
  `CONTATO` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Contato\",\"labelText\":\"Contato\",\"tooltip\":\"Contato\",\"hintText\":\"Informe o Contato\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZD04 - xContato\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `EMAIL` VARCHAR(60) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"EMail\",\"labelText\":\"EMail\",\"tooltip\":\"EMail\",\"hintText\":\"Informe o EMail\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZD05 - email\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `TELEFONE` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Telefone\",\"labelText\":\"Telefone\",\"tooltip\":\"Telefone\",\"hintText\":\"Informe o Telefone\",\"validacao\":\"Telefone\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZD06 - fone\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TELEFONE\"}}',
  `IDENTIFICADOR_CSRT` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"ID CSRT\",\"labelText\":\"ID CSRT\",\"tooltip\":\"ID CSRT\",\"hintText\":\"Informe o ID CSRT\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZD08 - idCSRT\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `HASH_CSRT` VARCHAR(28) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"HASH CSRT\",\"labelText\":\"HASH CSRT\",\"tooltip\":\"HASH CSRT\",\"hintText\":\"Informe o HASH CSRT\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"ZD09 - hashCSRT\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_NFE_RESPONSAVEL_TECNICO_NFE_CABECALHO1_idx` (`ID_NFE_CABECALHO` ASC),
  CONSTRAINT `fk_NFE_RESPONSAVEL_TECNICO_NFE_CABECALHO1`
    FOREIGN KEY (`ID_NFE_CABECALHO`)
    REFERENCES `fenix`.`NFE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = '[Grupo ZD. Informações do Responsável Técnico]';


-- -----------------------------------------------------
-- Table `fenix`.`CBO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CBO` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `CODIGO` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código CBO 2002\",\"labelText\":\"Código CBO 2002\",\"tooltip\":\"Código CBO 2002\",\"hintText\":\"Informe o Código CBO 2002\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CODIGO_1994` VARCHAR(10) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código CBO 1994\",\"labelText\":\"Código CBO 1994\",\"tooltip\":\"Código CBO 1994\",\"hintText\":\"Informe o Código CBO 1994\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `OBSERVACAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`PAIS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`PAIS` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `CODIGO` INT NULL,
  `NOME_EN` VARCHAR(100) NULL,
  `NOME_PTBR` VARCHAR(100) NULL,
  `SIGLA2` CHAR(2) NULL,
  `SIGLA3` CHAR(3) NULL,
  `CODIGO_BACEN` INT NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`TABELA_PRECO_PRODUTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`TABELA_PRECO_PRODUTO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_TABELA_PRECO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_PRODUTO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Produto\",\"labelText\":\"Produto\",\"tooltip\":\"Produto\",\"hintText\":\"Importe o Produto Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"produto\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PRECO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Preço\",\"labelText\":\"Preço\",\"tooltip\":\"Preço\",\"hintText\":\"Informe o Preço\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_TABELA_PRECO` (`ID_TABELA_PRECO` ASC),
  INDEX `fk_TABELA_PRECO_PRODUTO_PRODUTO1_idx` (`ID_PRODUTO` ASC),
  CONSTRAINT `fk_{1C28A567-0398-406B-9243-4403C0E93099}`
    FOREIGN KEY (`ID_TABELA_PRECO`)
    REFERENCES `fenix`.`TABELA_PRECO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_TABELA_PRECO_PRODUTO_PRODUTO1`
    FOREIGN KEY (`ID_PRODUTO`)
    REFERENCES `fenix`.`PRODUTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Vincula a tabela de preço aos produtos que fazem parte dela.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`GED_TIPO_DOCUMENTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`GED_TIPO_DOCUMENTO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `TAMANHO_MAXIMO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tamanho Máximo\",\"labelText\":\"Tamanho Máximo\",\"tooltip\":\"Tamanho Máximo\",\"hintText\":\"Informe o Tamanho Máximo\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Indica o tamanho maximo do documento em MB\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  PRIMARY KEY (`ID`))
COMMENT = 'Armazena os tipos de documento que podem ser utilizados no GED.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`GED_DOCUMENTO_CABECALHO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`GED_DOCUMENTO_CABECALHO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Nome do documento ou grupo de documentos\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Documento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Descricao do documento ou grupo de documentos\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_INCLUSAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Inclusão\",\"labelText\":\"Data de Inclusão\",\"tooltip\":\"Data de Inclusão\",\"hintText\":\"Informe a Data de Inclusão\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Data de inclusão do documento ou grupo de documentos\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Nessa tabela devem ser incluídos os documentos que serão digitalizados. Essa tabela contém as informações de cabeçalho dos documentos. Os demais dados deverão ser armanzenados na tabela GED_DOCUMENTO_DETALHE.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`GED_DOCUMENTO_DETALHE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`GED_DOCUMENTO_DETALHE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_GED_DOCUMENTO_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_GED_TIPO_DOCUMENTO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Documento\",\"labelText\":\"Tipo Documento\",\"tooltip\":\"Tipo Documento\",\"hintText\":\"Importe o Tipo de Documento Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"GED_TIPO_DOCUMENTO\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição do Documento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PALAVRAS_CHAVE` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Palavras Chave\",\"labelText\":\"Palavras Chave\",\"tooltip\":\"Palavras Chave\",\"hintText\":\"Informe as Palavras Chave do Documento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Palavras que serao indexadas para facilitar a localizacao do documento\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PODE_EXCLUIR` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Pode Excluir\",\"labelText\":\"Pode Excluir\",\"tooltip\":\"Pode Excluir\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}',
  `PODE_ALTERAR` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Pode Alterar\",\"labelText\":\"Pode Alterar\",\"tooltip\":\"Pode Alterar\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}',
  `ASSINADO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Assinatura Digital\",\"labelText\":\"Assinatura Digital\",\"tooltip\":\"Assinatura Digital\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}',
  `DATA_FIM_VIGENCIA` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Fim Vigência\",\"labelText\":\"Data Fim Vigência\",\"tooltip\":\"Data Fim Vigência\",\"hintText\":\"Informe a Data Final da Vigência\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `DATA_EXCLUSAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Exclusão\",\"labelText\":\"Data Exclusão\",\"tooltip\":\"Data Exclusão\",\"hintText\":\"Informe a Data de Exclusão do Documento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_GED_TIPO_DOCUMENTO` (`ID_GED_TIPO_DOCUMENTO` ASC),
  INDEX `FK_GED_DOCUMENTO_CAB_DET` (`ID_GED_DOCUMENTO_CABECALHO` ASC),
  CONSTRAINT `fk_{57544F31-81AC-4880-AB10-168655128E85}`
    FOREIGN KEY (`ID_GED_TIPO_DOCUMENTO`)
    REFERENCES `fenix`.`GED_TIPO_DOCUMENTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_{CF2C5E46-A149-422F-B16C-A9731C76F40B}`
    FOREIGN KEY (`ID_GED_DOCUMENTO_CABECALHO`)
    REFERENCES `fenix`.`GED_DOCUMENTO_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena os dados do documento que foi digitalizado ou capturado pelo GED.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`GED_VERSAO_DOCUMENTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`GED_VERSAO_DOCUMENTO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_GED_DOCUMENTO_DETALHE` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_COLABORADOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Colaborador\",\"labelText\":\"Colaborador\",\"tooltip\":\"Colaborador\",\"hintText\":\"Importe o Colaborador Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"colaborador\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VERSAO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Versão\",\"labelText\":\"Versão\",\"tooltip\":\"Versão\",\"hintText\":\"Informe a Versão\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  `DATA_VERSAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data da Versão\",\"labelText\":\"Data da Versão\",\"tooltip\":\"Data da Versão\",\"hintText\":\"Informe a Data da Versão\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `HORA_VERSAO` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Hora da Versão\",\"labelText\":\"Hora da Versão\",\"tooltip\":\"Hora da Versão\",\"hintText\":\"Informe a Hora da Versão\",\"validacao\":\"HORA\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"HORA\"}}',
  `HASH_ARQUIVO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Hash Arquivo\",\"labelText\":\"Hash Arquivo\",\"tooltip\":\"Hash Arquivo\",\"hintText\":\"Informe o Hash do Arquivo\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CAMINHO` VARCHAR(250) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Caminho Arquivo\",\"labelText\":\"Caminho Arquivo\",\"tooltip\":\"Caminho Arquivo\",\"hintText\":\"Informe o Caminho do Arquivo\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ACAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Ação\",\"labelText\":\"Ação\",\"tooltip\":\"Ação\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Campo \'orig\' da NF-e\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Incluído\"},{\"dropDownButtonItem\":\"Alterado\"},{\"dropDownButtonItem\":\"Excluído\"}]}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_GED_DOC_VERSAO_DOC` (`ID_GED_DOCUMENTO_DETALHE` ASC),
  INDEX `fk_GED_VERSAO_DOCUMENTO_COLABORADOR1_idx` (`ID_COLABORADOR` ASC),
  CONSTRAINT `fk_{B1B3E93F-737D-4043-9164-AD1418B05011}`
    FOREIGN KEY (`ID_GED_DOCUMENTO_DETALHE`)
    REFERENCES `fenix`.`GED_DOCUMENTO_DETALHE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_GED_VERSAO_DOCUMENTO_COLABORADOR1`
    FOREIGN KEY (`ID_COLABORADOR`)
    REFERENCES `fenix`.`COLABORADOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena os dados de versionamento dos documentos gerenciados pelo GED.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`OS_STATUS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`OS_STATUS` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`OS_ABERTURA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`OS_ABERTURA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_OS_STATUS` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Status\",\"labelText\":\"Status\",\"tooltip\":\"Status\",\"hintText\":\"Importe o Status Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"os_status\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_CLIENTE` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cliente\",\"labelText\":\"Cliente\",\"tooltip\":\"Cliente\",\"hintText\":\"Importe o Cliente Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cliente\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_COLABORADOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Colaborador\",\"labelText\":\"Colaborador\",\"tooltip\":\"Colaborador\",\"hintText\":\"Importe o Colaborador Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"colaborador\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Pode seguir o ID ou ter um algoritmo próprio de geração a critério do desenvolvedor\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_INICIO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Inicial\",\"labelText\":\"Data Inicial\",\"tooltip\":\"Data Inicial\",\"hintText\":\"Informe a Data Inicial\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `HORA_INICIO` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Hora Inicial\",\"labelText\":\"Hora Inicial\",\"tooltip\":\"Hora Inicial\",\"hintText\":\"Informe a Hora Inicial\",\"validacao\":\"HORA\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"HORA\"}}',
  `DATA_PREVISAO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Prevista\",\"labelText\":\"Data Prevista\",\"tooltip\":\"Data Prevista\",\"hintText\":\"Informe a Data Prevista\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `HORA_PREVISAO` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Hora Prevista\",\"labelText\":\"Hora Prevista\",\"tooltip\":\"Hora Prevista\",\"hintText\":\"Informe a Hora Prevista\",\"validacao\":\"HORA\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"HORA\"}}',
  `DATA_FIM` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Final\",\"labelText\":\"Data Final\",\"tooltip\":\"Data Final\",\"hintText\":\"Informe a Data Final\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}\n',
  `HORA_FIM` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Hora Final\",\"labelText\":\"Hora Final\",\"tooltip\":\"Hora Final\",\"hintText\":\"Informe a Hora Final\",\"validacao\":\"HORA\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"HORA\"}}',
  `NOME_CONTATO` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome do Contato\",\"labelText\":\"Nome do Contato\",\"tooltip\":\"Nome do Contato\",\"hintText\":\"Informe o Nome do Contato\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `FONE_CONTATO` VARCHAR(15) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Telefone do Contato\",\"labelText\":\"Telefone do Contato\",\"tooltip\":\"Telefone do Contato\",\"hintText\":\"Informe o Telefone do Contato\",\"validacao\":\"Telefone do Contato\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TELEFONE\"}}',
  `OBSERVACAO_CLIENTE` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação Cliente\",\"labelText\":\"Observação Cliente\",\"tooltip\":\"Observação Cliente\",\"hintText\":\"Observações Gerais do Cliente\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Reclamação apresentada pelo cliente, defeito apresentado, etc\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `OBSERVACAO_ABERTURA` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação Abertura\",\"labelText\":\"Observação Abertura\",\"tooltip\":\"Observação Abertura\",\"hintText\":\"Observações Gerais do Colaborador\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Observação inserida pelo colaborador que abriu a OS, já pode ter realizado algum teste, etc\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_OS_STATUS_ABERTURA` (`ID_OS_STATUS` ASC),
  INDEX `fk_OS_ABERTURA_CLIENTE1_idx` (`ID_CLIENTE` ASC),
  INDEX `fk_OS_ABERTURA_COLABORADOR1_idx` (`ID_COLABORADOR` ASC),
  CONSTRAINT `fk_{6BCAE8D3-8EF5-45FD-8A45-38B20DFF9FB0}`
    FOREIGN KEY (`ID_OS_STATUS`)
    REFERENCES `fenix`.`OS_STATUS` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_OS_ABERTURA_CLIENTE1`
    FOREIGN KEY (`ID_CLIENTE`)
    REFERENCES `fenix`.`CLIENTE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_OS_ABERTURA_COLABORADOR1`
    FOREIGN KEY (`ID_COLABORADOR`)
    REFERENCES `fenix`.`COLABORADOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`OS_EQUIPAMENTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`OS_EQUIPAMENTO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Cadastro do equipamentos que são atendidos pela empresa.\n\nExemplo:\n\nNome: Notebook Dell\nDesrição: Intel i3, 8GB RAM, 1TB HD, Tela 15,6'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`OS_ABERTURA_EQUIPAMENTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`OS_ABERTURA_EQUIPAMENTO` (
  `ID` INT UNSIGNED NOT NULL,
  `ID_OS_EQUIPAMENTO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Equipamento\",\"labelText\":\"Equipamento\",\"tooltip\":\"Equipamento\",\"hintText\":\"Importe o Equipamento Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"os_equipamento\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_OS_ABERTURA` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `NUMERO_SERIE` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número de Série\",\"labelText\":\"Número de Série\",\"tooltip\":\"Número de Série\",\"hintText\":\"Informe o Número de Série\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `TIPO_COBERTURA` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Cobertura\",\"labelText\":\"Tipo Cobertura\",\"tooltip\":\"Tipo Cobertura\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"indice\",\"valorPadrao\":\"\",\n\"itens\":[\n{\"dropDownButtonItem\":\"Nenhum\"},\n{\"dropDownButtonItem\":\"Garantia\"},\n{\"dropDownButtonItem\":\"Seguro\"},\n{\"dropDownButtonItem\":\"Contrato\"}\n]}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_OS_ABERT_EQUIP` (`ID_OS_ABERTURA` ASC),
  INDEX `FK_OS_EQUIP_ABERT` (`ID_OS_EQUIPAMENTO` ASC),
  CONSTRAINT `fk_{810B527C-FC00-4178-9F83-0DCE34F553A7}`
    FOREIGN KEY (`ID_OS_ABERTURA`)
    REFERENCES `fenix`.`OS_ABERTURA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_{22CA1568-F45B-42E9-B840-4C2FDA5D9D90}`
    FOREIGN KEY (`ID_OS_EQUIPAMENTO`)
    REFERENCES `fenix`.`OS_EQUIPAMENTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Equipamentos que estão sendo atendidos na OS'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`OS_PRODUTO_SERVICO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`OS_PRODUTO_SERVICO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_OS_ABERTURA` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `ID_PRODUTO` INT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Produto\",\"labelText\":\"Produto\",\"tooltip\":\"Produto\",\"hintText\":\"Importe o Produto Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"produto\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `TIPO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo\",\"labelText\":\"Tipo\",\"tooltip\":\"Tipo\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Produto\"},{\"dropDownButtonItem\":\"Serviço\"}]}}',
  `COMPLEMENTO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Complemento\",\"labelText\":\"Complemento\",\"tooltip\":\"Complemento\",\"hintText\":\"Informe o Texto complementar ao produto ou serviço prestado\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `QUANTIDADE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade\",\"labelText\":\"Quantidade\",\"tooltip\":\"Quantidade\",\"hintText\":\"Informe a Quantidade\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  `VALOR_UNITARIO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Unitário\",\"labelText\":\"Valor Unitário\",\"tooltip\":\"Valor Unitário\",\"hintText\":\"Informe o Valor Unitário\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_SUBTOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Subtotal\",\"labelText\":\"Valor Subtotal\",\"tooltip\":\"Valor Subtotal\",\"hintText\":\"Informe o Valor Subtotal\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TAXA_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Desconto\",\"labelText\":\"Taxa Desconto\",\"tooltip\":\"Taxa Desconto\",\"hintText\":\"Informe a Taxa de Desconto\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto\",\"labelText\":\"Valor Desconto\",\"tooltip\":\"Valor Desconto\",\"hintText\":\"Informe o Valor do Desconto\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_TOTAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total\",\"labelText\":\"Valor Total\",\"tooltip\":\"Valor Total\",\"hintText\":\"Informe o Valor Total\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_OS_ABERT_PROD_SERV` (`ID_OS_ABERTURA` ASC),
  INDEX `fk_OS_PRODUTO_SERVICO_PRODUTO1_idx` (`ID_PRODUTO` ASC),
  CONSTRAINT `fk_{1076F74A-D810-46A7-BC95-BC173A02F8E1}`
    FOREIGN KEY (`ID_OS_ABERTURA`)
    REFERENCES `fenix`.`OS_ABERTURA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_OS_PRODUTO_SERVICO_PRODUTO1`
    FOREIGN KEY (`ID_PRODUTO`)
    REFERENCES `fenix`.`PRODUTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Serviços prestados durante a OS. Produtos comercializados por conta da OS.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`OS_EVOLUCAO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`OS_EVOLUCAO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_OS_ABERTURA` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `DATA_REGISTRO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data do Registro\",\"labelText\":\"Data do Registro\",\"tooltip\":\"Data do Registro\",\"hintText\":\"Informe a Data do Registro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `HORA_REGISTRO` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Hora do Registro\",\"labelText\":\"Hora do Registro\",\"tooltip\":\"Hora do Registro\",\"hintText\":\"Informe a Hora do Registro\",\"validacao\":\"HORA\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"HORA\"}}',
  `OBSERVACAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ENVIAR_EMAIL` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Enviar E-mail\",\"labelText\":\"Enviar E-mail\",\"tooltip\":\"Enviar E-mail\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}',
  PRIMARY KEY (`ID`),
  INDEX `OS_EVOLUCAO_FKIndex1` (`ID_OS_ABERTURA` ASC),
  CONSTRAINT `fk_{6EFF3578-C97B-4E12-8768-F5D4247D08A9}`
    FOREIGN KEY (`ID_OS_ABERTURA`)
    REFERENCES `fenix`.`OS_ABERTURA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`COMISSAO_OBJETIVO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`COMISSAO_OBJETIVO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_COMISSAO_PERFIL` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Pefil\",\"labelText\":\"Pefil\",\"tooltip\":\"Pefil\",\"hintText\":\"Importe o Pefil Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"pessoa\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_PRODUTO` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Produto\",\"labelText\":\"Produto\",\"tooltip\":\"Produto\",\"hintText\":\"Importe o Produto Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"produto\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CODIGO` CHAR(3) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NOME` VARCHAR(100) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"DESCRICAO COM EXPLICAÇÃO DETALHADA DO OBJETIVO\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `FORMA_PAGAMENTO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Forma de Pagamento\",\"labelText\":\"Forma de Pagamento\",\"tooltip\":\"Forma de Pagamento\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Valor Fixo\"},{\"dropDownButtonItem\":\"Percentual\"}]}}',
  `TAXA_PAGAMENTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Taxa Pagamento\",\"labelText\":\"Taxa Pagamento\",\"tooltip\":\"Taxa Pagamento\",\"hintText\":\"Informe a Taxa de Pagamento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"TAXA A SER UTILIZADA PARA O PAGAMENTO POR PERCENTUAL\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_PAGAMENTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Pagamento\",\"labelText\":\"Valor Pagamento\",\"tooltip\":\"Valor Pagamento\",\"hintText\":\"Informe o Valor Pagamento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"VALOR FIXO A SER PAGO CASO SE ATINJA A META\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_META` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Meta\",\"labelText\":\"Valor Meta\",\"tooltip\":\"Valor Meta\",\"hintText\":\"Informe o Valor Meta\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"VALOR MONETARIO DA META A SER ATINGIDA\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `QUANTIDADE` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Quantidade\",\"labelText\":\"Quantidade\",\"tooltip\":\"Quantidade\",\"hintText\":\"Informe a Quantidade\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"UTILIZADO QUANDO A META FOR A QUANTIDADE DE DETERMINADO PRODUTO\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"QUANTIDADE\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_COMISSAO_PERFIL_OBJETIVO` (`ID_COMISSAO_PERFIL` ASC),
  INDEX `fk_COMISSAO_OBJETIVO_PRODUTO1_idx` (`ID_PRODUTO` ASC),
  CONSTRAINT `fk_{351EF9CB-7F6D-4EDE-A2C3-E35B9EF1F796}`
    FOREIGN KEY (`ID_COMISSAO_PERFIL`)
    REFERENCES `fenix`.`COMISSAO_PERFIL` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_COMISSAO_OBJETIVO_PRODUTO1`
    FOREIGN KEY (`ID_PRODUTO`)
    REFERENCES `fenix`.`PRODUTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Os objetivos serão previamente cadastrados. Códigos:\n\n001 - Venda Bruta - valor bruto vendido dentro do mês\n002 - Recebimento Bruto - valor bruto recebido dentro do mês\n003 - Recebimento Liquido - valor liquido recebido dentro do mês\n004 - Valor Produto - valor bruto de determinado produto vendido dentro do mês\n005 - Quantidade Produto - Quantidade de determinado prodito vendido dentro do mês'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`VENDEDOR_ROTA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`VENDEDOR_ROTA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_VENDEDOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Vendedor\",\"labelText\":\"Vendedor\",\"tooltip\":\"Vendedor\",\"hintText\":\"Importe o Vendedor Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"vendedor\",\"campoLookup\":\"colaborador?.pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_CLIENTE` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cliente\",\"labelText\":\"Cliente\",\"tooltip\":\"Cliente\",\"hintText\":\"Importe o Cliente Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cliente\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `POSICAO` INT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Posição Rota\",\"labelText\":\"Posição Rota\",\"tooltip\":\"Posição Rota\",\"hintText\":\"Informe a Posição na Rota\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_VENDEDOR_ROTA_VENDEDOR1_idx` (`ID_VENDEDOR` ASC),
  INDEX `fk_VENDEDOR_ROTA_CLIENTE1_idx` (`ID_CLIENTE` ASC),
  CONSTRAINT `fk_VENDEDOR_ROTA_VENDEDOR1`
    FOREIGN KEY (`ID_VENDEDOR`)
    REFERENCES `fenix`.`VENDEDOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_VENDEDOR_ROTA_CLIENTE1`
    FOREIGN KEY (`ID_CLIENTE`)
    REFERENCES `fenix`.`CLIENTE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`VENDEDOR_META`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`VENDEDOR_META` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `ID_VENDEDOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Vendedor\",\"labelText\":\"Vendedor\",\"tooltip\":\"Vendedor\",\"hintText\":\"Importe o Vendedor Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"vendedor\",\"campoLookup\":\"colaborador?.pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_CLIENTE` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cliente\",\"labelText\":\"Cliente\",\"tooltip\":\"Cliente\",\"hintText\":\"Importe o Cliente Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cliente\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PERIODO_META` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Período\",\"labelText\":\"Período\",\"tooltip\":\"Período\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"indice\",\"valorPadrao\":\"\",\n\"itens\":[\n{\"dropDownButtonItem\":\"Semanal\"},\n{\"dropDownButtonItem\":\"Mensal\"},\n{\"dropDownButtonItem\":\"Bimestral\"},\n{\"dropDownButtonItem\":\"Trimestral\"},\n{\"dropDownButtonItem\":\"Semestral\"},\n{\"dropDownButtonItem\":\"Anual\"}\n]}}',
  `META_ORCADA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Meta Orçada\",\"labelText\":\"Meta Orçada\",\"tooltip\":\"Meta Orçada\",\"hintText\":\"Informe o Meta Orçada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `META_REALIZADA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Meta Realizada\",\"labelText\":\"Meta Realizada\",\"tooltip\":\"Meta Realizada\",\"hintText\":\"Informe o Meta Realizada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `DATA_INICIO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Inicial\",\"labelText\":\"Data Inicial\",\"tooltip\":\"Data Inicial\",\"hintText\":\"Informe a Data Inicial\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  `DATA_FIM` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data Final\",\"labelText\":\"Data Final\",\"tooltip\":\"Data Final\",\"hintText\":\"Informe a Data Final\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"EEE, d / MMM / yyyy\"}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_VENDEDOR_META_VENDEDOR1_idx` (`ID_VENDEDOR` ASC),
  INDEX `fk_VENDEDOR_META_CLIENTE1_idx` (`ID_CLIENTE` ASC),
  CONSTRAINT `fk_VENDEDOR_META_VENDEDOR1`
    FOREIGN KEY (`ID_VENDEDOR`)
    REFERENCES `fenix`.`VENDEDOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_VENDEDOR_META_CLIENTE1`
    FOREIGN KEY (`ID_CLIENTE`)
    REFERENCES `fenix`.`CLIENTE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fenix`.`NFCE_OPERADOR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFCE_OPERADOR` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_COLABORADOR` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Colaborador\",\"labelText\":\"Colaborador\",\"tooltip\":\"Colaborador\",\"hintText\":\"Importe o Colaborador Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"colaborador\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `LOGIN` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Login\",\"labelText\":\"Login\",\"tooltip\":\"Login\",\"hintText\":\"Informe o Login\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `SENHA` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Senha\",\"labelText\":\"Senha\",\"tooltip\":\"Senha\",\"hintText\":\"Informe a Senha\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NIVEL_AUTORIZACAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nível Autorização\",\"labelText\":\"Nível Autorização\",\"tooltip\":\"Nível Autorização\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"G=GERENTE\"},{\"dropDownButtonItem\":\"S=SUPERVISOR\"},{\"dropDownButtonItem\":\"O=OPERADOR\"}]}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_NFCE_OPERADOR_COLABORADOR1_idx` (`ID_COLABORADOR` ASC),
  CONSTRAINT `fk_NFCE_OPERADOR_COLABORADOR1`
    FOREIGN KEY (`ID_COLABORADOR`)
    REFERENCES `fenix`.`COLABORADOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena os operadores de caixa. Tem relacionamento com a tabela de funcionários, que vem do banco de dados principal.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFCE_CAIXA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFCE_CAIXA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `NOME` VARCHAR(50) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Nome\",\"labelText\":\"Nome\",\"tooltip\":\"Nome\",\"hintText\":\"Informe o Nome\",\"validacao\":\"Alfanumerico\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_CADASTRO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Cadastro\",\"labelText\":\"Data de Cadastro\",\"tooltip\":\"Data de Cadastro\",\"hintText\":\"Informe a Data de Cadastro\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"dd/MM/yyyy\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Armazena os dados dos terminais de caixa.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFCE_MOVIMENTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFCE_MOVIMENTO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFCE_CAIXA` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Caixa\",\"labelText\":\"Caixa\",\"tooltip\":\"Caixa\",\"hintText\":\"Importe a Caixa Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"nfce_caixa\",\"campoLookup\":\"nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_NFCE_OPERADOR` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Operador\",\"labelText\":\"Operador\",\"tooltip\":\"Operador\",\"hintText\":\"Importe o Operador Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"nfce_operador\",\"campoLookup\":\"login\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_GERENTE_SUPERVISOR` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Gerente\",\"labelText\":\"Gerente\",\"tooltip\":\"Gerente\",\"hintText\":\"Importe o Gerente Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"nfce_operador\",\"campoLookup\":\"login\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_ABERTURA` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Abertura\",\"labelText\":\"Data de Abertura\",\"tooltip\":\"Data de Abertura\",\"hintText\":\"Informe a Data de Abertura\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"dd/MM/yyyy\"}}',
  `HORA_ABERTURA` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Hora de Abertura\",\"labelText\":\"Hora de Abertura\",\"tooltip\":\"Hora de Abertura\",\"hintText\":\"Informe a Hora de Abertura\",\"validacao\":\"HORA\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"HORA\"}}\n',
  `DATA_FECHAMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Fechamento\",\"labelText\":\"Data de Fechamento\",\"tooltip\":\"Data de Fechamento\",\"hintText\":\"Informe a Data de Fechamento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"dd/MM/yyyy\"}}',
  `HORA_FECHAMENTO` VARCHAR(8) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Hora de Fechamento\",\"labelText\":\"Hora de Fechamento\",\"tooltip\":\"Hora de Fechamento\",\"hintText\":\"Informe a Hora de Fechamento\",\"validacao\":\"HORA\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"HORA\"}}\n',
  `TOTAL_SUPRIMENTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Suprimento\",\"labelText\":\"Valor Total Suprimento\",\"tooltip\":\"Valor Total Suprimento\",\"hintText\":\"Informe o Valor Total Suprimento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TOTAL_SANGRIA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Sangria\",\"labelText\":\"Valor Total Sangria\",\"tooltip\":\"Valor Total Sangria\",\"hintText\":\"Informe o Valor Total Sangria\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TOTAL_NAO_FISCAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Não Fiscal\",\"labelText\":\"Valor Total Não Fiscal\",\"tooltip\":\"Valor Total Não Fiscal\",\"hintText\":\"Informe o Valor Total Não Fiscal\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TOTAL_VENDA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Venda\",\"labelText\":\"Valor Total Venda\",\"tooltip\":\"Valor Total Venda\",\"hintText\":\"Informe o Valor Total Venda\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TOTAL_DESCONTO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Desconto\",\"labelText\":\"Valor Total Desconto\",\"tooltip\":\"Valor Total Desconto\",\"hintText\":\"Informe o Valor Total Desconto\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TOTAL_ACRESCIMO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Acréscimo\",\"labelText\":\"Valor Total Acréscimo\",\"tooltip\":\"Valor Total Acréscimo\",\"hintText\":\"Informe o Valor Total Acréscimo\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TOTAL_FINAL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Final\",\"labelText\":\"Valor Total Final\",\"tooltip\":\"Valor Total Final\",\"hintText\":\"Informe o Valor Total Final\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TOTAL_RECEBIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Recebido\",\"labelText\":\"Valor Total Recebido\",\"tooltip\":\"Valor Total Recebido\",\"hintText\":\"Informe o Valor Total Recebido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TOTAL_TROCO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Troco\",\"labelText\":\"Valor Total Troco\",\"tooltip\":\"Valor Total Troco\",\"hintText\":\"Informe o Valor Total Troco\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `TOTAL_CANCELADO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Total Cancelado\",\"labelText\":\"Valor Total Cancelado\",\"tooltip\":\"Valor Total Cancelado\",\"hintText\":\"Informe o Valor Total Cancelado\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `STATUS_MOVIMENTO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Modalidade Base Cálculo\",\"labelText\":\"Modalidade Base Cálculo\",\"tooltip\":\"Modalidade Base Cálculo\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"A=ABERTO\"},{\"dropDownButtonItem\":\"F=FECHADO\"},{\"dropDownButtonItem\":\"T=FECHADO TEMPORARIAMENTE\"}]}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFCE_OPERADOR_MOV` (`ID_NFCE_OPERADOR` ASC),
  INDEX `FK_NFCE_CAIXA_MOV` (`ID_NFCE_CAIXA` ASC),
  CONSTRAINT `fk_{B756D09A-C3D6-4012-A7D6-79F83A8EFBE7}`
    FOREIGN KEY (`ID_NFCE_OPERADOR`)
    REFERENCES `fenix`.`NFCE_OPERADOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_{C40AD05D-651F-4DE6-A93A-F0B3CF973D36}`
    FOREIGN KEY (`ID_NFCE_CAIXA`)
    REFERENCES `fenix`.`NFCE_CAIXA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena os movimentos para determinado caixa. Podem haver vários movimentos durante um dia. Um movimento deve ter obrigatoriamente:\n\n-Operador\n-Caixa (terminal)\n-Impressora\n-Turno\n-Status\n\nÉ através dessa tabela que o caixa deve funcionar. Sem um movimento aberto não pode haver movimentação no caixa.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFCE_SANGRIA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFCE_SANGRIA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFCE_MOVIMENTO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Movimento\",\"labelText\":\"Movimento\",\"tooltip\":\"Movimento\",\"hintText\":\"Importe o Movimento Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"nfce_movimento\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_SANGRIA` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data da Sangria\",\"labelText\":\"Data da Sangria\",\"tooltip\":\"Data da Sangria\",\"hintText\":\"Informe a Data da Sangria\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"dd/MM/yyyy\"}}',
  `VALOR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor\",\"labelText\":\"Valor\",\"tooltip\":\"Valor\",\"hintText\":\"Informe o Valor\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `OBSERVACAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFCE_MOV_SANGRIA` (`ID_NFCE_MOVIMENTO` ASC),
  CONSTRAINT `fk_{2777AC82-DFA3-4F0E-9EE3-1F4AE5C53CF3}`
    FOREIGN KEY (`ID_NFCE_MOVIMENTO`)
    REFERENCES `fenix`.`NFCE_MOVIMENTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena todas as sangrias que são feitas no caixa. Tem relacionamento direto com a tabela de movimento, já que uma sangria só pode ser feita se houver um \nmovimento aberto.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFCE_TIPO_PAGAMENTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFCE_TIPO_PAGAMENTO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(2) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `PERMITE_TROCO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Permite Troco\",\"labelText\":\"Permite Troco\",\"tooltip\":\"Permite Troco\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}',
  `GERA_PARCELAS` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Gera Parcelas\",\"labelText\":\"Gera Parcelas\",\"tooltip\":\"Gera Parcelas\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Armazena os possíveis tipos de pagamento:\n\nDinheiro\nCartão\nCheque\nEtc.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFCE_FECHAMENTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFCE_FECHAMENTO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFCE_MOVIMENTO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Movimento\",\"labelText\":\"Movimento\",\"tooltip\":\"Movimento\",\"hintText\":\"Importe o Movimento Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"nfce_movimento\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_NFCE_TIPO_PAGAMENTO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo Pagamento\",\"labelText\":\"Tipo Pagamento\",\"tooltip\":\"Tipo Pagamento\",\"hintText\":\"Importe o Tipo Pagamento Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"nfce_tipo_pagamento\",\"campoLookup\":\"descricao\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VALOR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor\",\"labelText\":\"Valor\",\"tooltip\":\"Valor\",\"hintText\":\"Informe o Valor\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFCE_MOV_FECHA` (`ID_NFCE_MOVIMENTO` ASC),
  INDEX `fk_NFCE_FECHAMENTO_NFCE_TIPO_PAGAMENTO1_idx` (`ID_NFCE_TIPO_PAGAMENTO` ASC),
  CONSTRAINT `fk_{7EE389FF-4DBB-42FE-8EC6-50DA93B8778E}`
    FOREIGN KEY (`ID_NFCE_MOVIMENTO`)
    REFERENCES `fenix`.`NFCE_MOVIMENTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_NFCE_FECHAMENTO_NFCE_TIPO_PAGAMENTO1`
    FOREIGN KEY (`ID_NFCE_TIPO_PAGAMENTO`)
    REFERENCES `fenix`.`NFCE_TIPO_PAGAMENTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFCE_SUPRIMENTO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFCE_SUPRIMENTO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFCE_MOVIMENTO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Movimento\",\"labelText\":\"Movimento\",\"tooltip\":\"Movimento\",\"hintText\":\"Importe o Movimento Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"nfce_movimento\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_SUPRIMENTO` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data do Suprimento\",\"labelText\":\"Data do Suprimento\",\"tooltip\":\"Data do Suprimento\",\"hintText\":\"Informe a Data do Suprimento\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"dd/MM/yyyy\"}}',
  `VALOR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor\",\"labelText\":\"Valor\",\"tooltip\":\"Valor\",\"hintText\":\"Informe o Valor\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `OBSERVACAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Observação\",\"labelText\":\"Observação\",\"tooltip\":\"Observação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFCE_MOV_SUPRIMENTO` (`ID_NFCE_MOVIMENTO` ASC),
  CONSTRAINT `fk_{2B61D0B4-58A2-49F5-A893-B0C551865C45}`
    FOREIGN KEY (`ID_NFCE_MOVIMENTO`)
    REFERENCES `fenix`.`NFCE_MOVIMENTO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Armazena todos os suprimentos que são feitos no caixa. Tem relacionamento direto com a tabela de movimento, já que um suprimento só pode ser feito se houver um movimento aberto.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFSE_LISTA_SERVICO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFSE_LISTA_SERVICO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CODIGO` CHAR(5) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código\",\"labelText\":\"Código\",\"tooltip\":\"Código\",\"hintText\":\"Informe o Código\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DESCRICAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Descrição\",\"labelText\":\"Descrição\",\"tooltip\":\"Descrição\",\"hintText\":\"Informe a Descrição\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`))
COMMENT = 'Armazena tabela conforme LEI COMPLEMENTAR Nº 116, DE 31 DE JULHO DE 2003'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFSE_CABECALHO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFSE_CABECALHO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CLIENTE` INT NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Cliente\",\"labelText\":\"Cliente\",\"tooltip\":\"Cliente\",\"hintText\":\"Importe o Cliente Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"cliente\",\"campoLookup\":\"pessoa?.nome\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_OS_ABERTURA` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Ordem de Serviço\",\"labelText\":\"Ordem de Serviço\",\"tooltip\":\"Ordem de Serviço\",\"hintText\":\"Importe o Ordem de Serviço Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"os_abertura\",\"campoLookup\":\"numero\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NUMERO` VARCHAR(15) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número\",\"labelText\":\"Número\",\"tooltip\":\"Número\",\"hintText\":\"Informe o Número\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Número da NFS-e, formado pelo ano com 04 (quatro) dígitos e um número seqüencial com 11 posições - Formato AAAANNNNNNNNNNN\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CODIGO_VERIFICACAO` VARCHAR(9) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Verificação\",\"labelText\":\"Código Verificação\",\"tooltip\":\"Código Verificação\",\"hintText\":\"Informe o Código Verificação\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Código da Verificação da NFS-e \",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `DATA_HORA_EMISSAO` TIMESTAMP NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data/Hora Emissão\",\"labelText\":\"Data/Hora Emissão\",\"tooltip\":\"Data/Hora Emissão\",\"hintText\":\"Informe a Data/Hora Emissão\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Data/Hora da emissão da NF  S-e (AAAA-MM-DDTHH:mm:ss). \",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"dd-MM-yyyy hh:mm a\"}}',
  `COMPETENCIA` VARCHAR(6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Mês/Ano Competência\",\"labelText\":\"Mês/Ano Competência\",\"tooltip\":\"Mês/Ano Competência\",\"hintText\":\"Informe o Mês/Ano Competência\",\"validacao\":\"MES_ANO\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Mês e ano da prestação de serviço. (AAAAMM).\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"MES_ANO\"}}',
  `NUMERO_SUBSTITUIDA` VARCHAR(15) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número Substituída\",\"labelText\":\"Número Substituída\",\"tooltip\":\"Número Substituída\",\"hintText\":\"Informe o Número\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Número da NFS-e substituída.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `NATUREZA_OPERACAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Natureza Operação\",\"labelText\":\"Natureza Operação\",\"tooltip\":\"Natureza Operação\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":\n[\n{\"dropDownButtonItem\":\"1=Tributação no município\"},\n{\"dropDownButtonItem\":\"2=Tributação fora do município\"},\n{\"dropDownButtonItem\":\"3=Isenção\"},\n{\"dropDownButtonItem\":\"4=Imune\"},\n{\"dropDownButtonItem\":\"5=Exigibilidade suspensa por decisão judicial\"},\n{\"dropDownButtonItem\":\"6=Exigibilidade suspensa por procedimento administrativo\"}\n]}}',
  `REGIME_ESPECIAL_TRIBUTACAO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Regime Especial Tributação\",\"labelText\":\"Regime Especial Tributação\",\"tooltip\":\"Regime Especial Tributação\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":\n[\n{\"dropDownButtonItem\":\"1=Microempresa Municipal\"},\n{\"dropDownButtonItem\":\"2=Estimativa\"},\n{\"dropDownButtonItem\":\"3=Sociedade de Profissionais\"},\n{\"dropDownButtonItem\":\"4=Cooperativa\"}\n]}}',
  `OPTANTE_SIMPLES_NACIONAL` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Optante Simples Nacional\",\"labelText\":\"Optante Simples Nacional\",\"tooltip\":\"Optante Simples Nacional\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}',
  `INCENTIVADOR_CULTURAL` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Incentivador Cultural\",\"labelText\":\"Incentivador Cultural\",\"tooltip\":\"Incentivador Cultural\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}',
  `NUMERO_RPS` VARCHAR(15) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Número RPS\",\"labelText\":\"Número RPS\",\"tooltip\":\"Número RPS\",\"hintText\":\"Informe o Número\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Número do RPS. Campo Obrigatório apenas para NFS-e geradas pela emissão de RPS. \",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `SERIE_RPS` VARCHAR(5) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Série RPS\",\"labelText\":\"Série RPS\",\"tooltip\":\"Série RPS\",\"hintText\":\"Informe a Série\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Número do equipamento emissor do RPS ou série do RPS.\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `TIPO_RPS` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Tipo RPS\",\"labelText\":\"Tipo RPS\",\"tooltip\":\"Tipo RPS\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":\n[\n{\"dropDownButtonItem\":\"1=Recibo Provisório de Serviços\"},\n{\"dropDownButtonItem\":\"2=RPS  Nota Fiscal Conjugada (Mista)\"},\n{\"dropDownButtonItem\":\"3=Cupom\"}\n]}}',
  `DATA_EMISSAO_RPS` DATE NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Data de Emissão do RPS\",\"labelText\":\"Data de Emissão do RPS\",\"tooltip\":\"Data de Emissão do RPS\",\"hintText\":\"Informe a Data de Emissão do RPS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Campo Obrigatório apenas para NFS-e geradas pela emissão de RPS. \",\"tipoControle\":{\"tipo\":\"datePickerItem\",\"firstDate\":\"1900-01-01\",\"lastDate\":\"now\",\"mascara\":\"dd/MM/yyyy\"}}',
  `OUTRAS_INFORMACOES` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Outras Informações\",\"labelText\":\"Outras Informações\",\"tooltip\":\"Outras Informações\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `fk_NFSE_CABECALHO_OS_ABERTURA1_idx` (`ID_OS_ABERTURA` ASC),
  INDEX `fk_NFSE_CABECALHO_CLIENTE1_idx` (`ID_CLIENTE` ASC),
  CONSTRAINT `fk_NFSE_CABECALHO_OS_ABERTURA1`
    FOREIGN KEY (`ID_OS_ABERTURA`)
    REFERENCES `fenix`.`OS_ABERTURA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_NFSE_CABECALHO_CLIENTE1`
    FOREIGN KEY (`ID_CLIENTE`)
    REFERENCES `fenix`.`CLIENTE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFSE_DETALHE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFSE_DETALHE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFSE_LISTA_SERVICO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"R\",\"side\":\"Local\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Lista Serviço\",\"labelText\":\"Lista Serviço\",\"tooltip\":\"Lista Serviço\",\"hintText\":\"Importe a Lista Serviço Vinculada\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"nfse_lista_servico\",\"campoLookup\":\"descricao\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `ID_NFSE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToMany\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `VALOR_SERVICOS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor dos Serviços\",\"labelText\":\"Valor dos Serviços\",\"tooltip\":\"Valor dos Serviços\",\"hintText\":\"Informe o Valor\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_DEDUCOES` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor das Deduções\",\"labelText\":\"Valor das Deduções\",\"tooltip\":\"Valor das Deduções\",\"hintText\":\"Informe o Valor\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_PIS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor do PIS\",\"labelText\":\"Valor do PIS\",\"tooltip\":\"Valor do PIS\",\"hintText\":\"Informe o Valor do PIS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_COFINS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor do COFINS\",\"labelText\":\"Valor do COFINS\",\"tooltip\":\"Valor do COFINS\",\"hintText\":\"Informe o Valor do COFINS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_INSS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor do INSS\",\"labelText\":\"Valor do INSS\",\"tooltip\":\"Valor do INSS\",\"hintText\":\"Informe o Valor do INSS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_IR` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor do IR\",\"labelText\":\"Valor do IR\",\"tooltip\":\"Valor do IR\",\"hintText\":\"Informe o Valor do IR\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_CSLL` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor do CSLL\",\"labelText\":\"Valor do CSLL\",\"tooltip\":\"Valor do CSLL\",\"hintText\":\"Informe o Valor do CSLL\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `CODIGO_CNAE` VARCHAR(7) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNAE\",\"labelText\":\"CNAE\",\"tooltip\":\"CNAE\",\"hintText\":\"Informe o CNAE\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"CNAE\",\"campoLookup\":\"codigo\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"%\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `CODIGO_TRIBUTACAO_MUNICIPIO` VARCHAR(20) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Código Tributação Município\",\"labelText\":\"Código Tributação Município\",\"tooltip\":\"Código Tributação Município\",\"hintText\":\"Informe o Código Tributação Município\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `VALOR_BASE_CALCULO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Base Cálculo\",\"labelText\":\"Valor Base Cálculo\",\"tooltip\":\"Valor Base Cálculo\",\"hintText\":\"Informe o Valor Base Cálculo\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"(Valor dos serviços - Valor das deduções - descontos incondicionados)\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `ALIQUOTA` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Alíquota\",\"labelText\":\"Alíquota\",\"tooltip\":\"Alíquota\",\"hintText\":\"Informe o Alíquota\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"Alíquota do serviço prestado\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"TAXA\"}}',
  `VALOR_ISS` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor do ISS\",\"labelText\":\"Valor do ISS\",\"tooltip\":\"Valor do ISS\",\"hintText\":\"Informe o Valor do ISS\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_LIQUIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Líquido\",\"labelText\":\"Valor Líquido\",\"tooltip\":\"Valor Líquido\",\"hintText\":\"Informe o Valor Líquido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"(ValorServicos - ValorPIS - ValorCOFINS - ValorINSS - ValorIR - ValorCSLL - OutrasRetençoes - ValorISSRetido -  DescontoIncondicionado - DescontoCondicionado)\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `OUTRAS_RETENCOES` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Outras Retenções\",\"labelText\":\"Valor Outras Retenções\",\"tooltip\":\"Valor Outras Retenções\",\"hintText\":\"Informe o Valor do Outras Retenções\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_CREDITO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Crédito\",\"labelText\":\"Valor Crédito\",\"tooltip\":\"Valor Crédito\",\"hintText\":\"Informe o Valor do Crédito\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `ISS_RETIDO` CHAR(1) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"ISS Retido\",\"labelText\":\"ISS Retido\",\"tooltip\":\"ISS Retido\",\"hintText\":\"Selecione a Opção Desejada\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"dropDownButton\",\"persiste\":\"char\",\"valorPadrao\":\"\",\"itens\":[{\"dropDownButtonItem\":\"Sim\"},{\"dropDownButtonItem\":\"Não\"}]}}',
  `VALOR_ISS_RETIDO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor ISS Retido\",\"labelText\":\"Valor ISS Retido\",\"tooltip\":\"Valor ISS Retido\",\"hintText\":\"Informe o Valor do ISS Retido\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}\n',
  `VALOR_DESCONTO_CONDICIONADO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto Condicionado\",\"labelText\":\"Valor Desconto Condicionado\",\"tooltip\":\"Valor Desconto Condicionado\",\"hintText\":\"Informe o Valor do Desconto Condicionado\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `VALOR_DESCONTO_INCONDICIONADO` DECIMAL(18,6) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Valor Desconto Incondicionado\",\"labelText\":\"Valor Desconto Incondicionado\",\"tooltip\":\"Valor Desconto Incondicionado\",\"hintText\":\"Informe o Valor do Desconto Incondicionado\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"VALOR\"}}',
  `DISCRIMINACAO` TEXT NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Discriminação\",\"labelText\":\"Discriminação\",\"tooltip\":\"Discriminação\",\"hintText\":\"Observações Gerais\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `MUNICIPIO_PRESTACAO` INT UNSIGNED NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Município IBGE\",\"labelText\":\"Município IBGE\",\"tooltip\":\"Município IBGE\",\"hintText\":\"Importe o Município IBGE Vinculado\",\"validacao\":\"\",\"obrigatorio\":true,\"readOnly\":true,\"tabelaLookup\":\"municipio\",\"campoLookup\":\"codigoIbge\",\"campoLookupTipoDado\":\"int\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFSE_CAB_DET` (`ID_NFSE_CABECALHO` ASC),
  INDEX `FK_LISTA_SER_DET` (`ID_NFSE_LISTA_SERVICO` ASC),
  CONSTRAINT `fk_{A4D96B77-6862-43DE-9D90-6CE7C219395A}`
    FOREIGN KEY (`ID_NFSE_CABECALHO`)
    REFERENCES `fenix`.`NFSE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_{250949FC-A04F-43DA-B79F-E473F538AA92}`
    FOREIGN KEY (`ID_NFSE_LISTA_SERVICO`)
    REFERENCES `fenix`.`NFSE_LISTA_SERVICO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`NFSE_INTERMEDIARIO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`NFSE_INTERMEDIARIO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_NFSE_CABECALHO` INT UNSIGNED NOT NULL COMMENT '{\"cardinalidade\":\"@OneToOne\",\"crud\":\"CRUD\",\"side\":\"Inverse\",\"cascade\":true,\"orphanRemoval\":true,\"label\":\"\",\"labelText\":\"\",\"tooltip\":\"\",\"hintText\":\"\",\"validacao\":\"\",\"campoLookup\":\"\",\"obrigatorio\":false,\"tipoControle\":null}',
  `CNPJ` VARCHAR(14) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"CNPJ\",\"labelText\":\"CNPJ\",\"tooltip\":\"CNPJ\",\"hintText\":\"Informe o CNPJ\",\"validacao\":\"CNPJ\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"number\",\"mascara\":\"CNPJ\"}}',
  `RAZAO` VARCHAR(150) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Razão\",\"labelText\":\"Razão\",\"tooltip\":\"Razão\",\"hintText\":\"Informe a Razão\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  `INSCRICAO_MUNICIPAL` VARCHAR(15) NULL COMMENT '{\"cardinalidade\":\"\",\"crud\":\"\",\"side\":\"\",\"cascade\":false,\"orphanRemoval\":false,\"label\":\"Incrição Municipal\",\"labelText\":\"Incrição Municipal\",\"tooltip\":\"Incrição Municipal\",\"hintText\":\"Informe a Incrição Municipal\",\"validacao\":\"\",\"obrigatorio\":false,\"readOnly\":false,\"tabelaLookup\":\"\",\"campoLookup\":\"\",\"campoLookupTipoDado\":\"\",\"valorPadraoLookup\":\"\",\"desenhaControle\":true,\"comentario\":\"\",\"tipoControle\":{\"tipo\":\"textFormField\",\"keyboardType\":\"\",\"mascara\":\"\"}}',
  PRIMARY KEY (`ID`),
  INDEX `FK_NFSE_INTERMEDIARIO` (`ID_NFSE_CABECALHO` ASC),
  CONSTRAINT `fk_{239E588F-852E-4B34-8B15-85253428F291}`
    FOREIGN KEY (`ID_NFSE_CABECALHO`)
    REFERENCES `fenix`.`NFSE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_CABECALHO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_CABECALHO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `UF_EMITENTE` INT UNSIGNED NULL COMMENT 'cUF - Código da UF do emitente do CT-e - Utilizar a Tabela do IBGE',
  `CODIGO_NUMERICO` VARCHAR(8) NULL COMMENT 'cCT - Código numérico que compõe a  Chave de Acesso. Número aleatório gerado pelo emitente  para cada CT-e, com o objetivo de evitar  acessos indevidos ao documento.',
  `CFOP` INT UNSIGNED NULL COMMENT 'CFOP - Código Fiscal de Operações e  Prestações',
  `NATUREZA_OPERACAO` VARCHAR(60) NULL COMMENT 'natOp - Natureza da Operação',
  `FORMA_PAGAMENTO` CHAR(1) NULL COMMENT 'forPag - 0 - Pago;  1 - A pagar;  2 - Outros',
  `MODELO` CHAR(2) NULL COMMENT 'mod - Utilizar o código 57 para identificação do  CT-e, emitido em substituição aos  modelos de conhecimentos em papel',
  `SERIE` CHAR(3) NULL COMMENT 'serie - Série do CT-e - Preencher com \'0\' no caso de série única',
  `NUMERO` VARCHAR(9) NULL COMMENT 'nCT - Número do CT-e',
  `DATA_HORA_EMISSAO` TIMESTAMP NULL COMMENT 'dhEmi - Data e hora de emissão do CT-e',
  `FORMATO_IMPRESSAO_DACTE` CHAR(1) NULL COMMENT 'tpImp - Formato de impressão do DACTE - 1 - Retrato; 2 -  Paisagem.',
  `TIPO_EMISSAO` CHAR(1) NULL COMMENT 'tpEmis - Forma de emissão do CT-e - 1 - Normal;  4-EPEC pela SVC; 5 - Contingência FSDA;  7 - Autorização pela SVC-RS;  8 - Autorização pela SVC-SP ',
  `CHAVE_ACESSO` VARCHAR(44) NULL COMMENT 'Chave de acesso ',
  `DIGITO_CHAVE_ACESSO` CHAR(1) NULL COMMENT 'cDV - Digito Verificador da chave de acesso do  CT-e - Informar o dígito de controle dachave de  acesso do CT-e, que deve ser calculado  com a aplicação do algoritmo módulo 11  (base 2,9) da chave de acesso',
  `AMBIENTE` CHAR(1) NULL COMMENT 'tpAmb - 1-Produção/ 2-Homologação',
  `TIPO_CTE` CHAR(1) NULL COMMENT 'tpCTe - Tipo do CT-e - 0 - CT-e Normal;  1 - CT-e de Complemento de Valores; 2 -  CT-e de Anulação;  3 - CT-e Substituto',
  `PROCESSO_EMISSAO` CHAR(1) NULL COMMENT 'procEmi - Identificador do processo de emissão do  CT-e - 0 - emissão de CT-e com aplicativo do  contribuinte;  1 - emissão de CT-e avulsa pelo Fisco;  2 - emissão de CT-e avulsa, pelo  contribuinte com seu certificado digital,  através do site do Fisco;  3- emissão CT-e pelo contribuinte com  aplicativo fornecido pelo Fisco.',
  `VERSAO_PROCESSO_EMISSAO` VARCHAR(20) NULL COMMENT 'verProc - Identificador da versão do  processo de emissão ',
  `CHAVE_REFERENCIADO` VARCHAR(44) NULL COMMENT 'refCTE - Chave de acesso do CT-e referenciado',
  `CODIGO_MUNICIPIO_ENVIO` INT UNSIGNED NULL COMMENT 'cMunEnv - Código do Município de envio do CT-e  (de onde o documento foi transmitido) - Utilizar a tabela do IBGE. Informar  9999999 para as operações com o  exterior',
  `NOME_MUNICIPIO_ENVIO` VARCHAR(60) NULL COMMENT 'xMunEnv - Nome do Município de envio do CT-e (de  onde o documento foi transmitido) - Informar PAIS/Municipio para as  operações com o exterior.',
  `UF_ENVIO` CHAR(2) NULL COMMENT 'UFEnv - Sigla da UF de envio do CT-e (de onde o  documento foi transmitido) - Informar \'EX\' para operações com o  exterior',
  `MODAL` CHAR(2) NULL COMMENT 'modal - Preencher com:01-Rodoviário;  02-Aéreo;03-Aquaviário;04-Ferroviário;05-Dutoviário;06-Multimodal;',
  `TIPO_SERVICO` CHAR(1) NULL COMMENT 'tpServ - Tipo do Serviço - 0 - Normal;1 - Subcontratação;  2 - Redespacho;3 - Redespacho  Intermediário; 4 - Serviço Vinculado a  Multimodal',
  `CODIGO_MUNICIPIO_INI_PRESTACAO` INT UNSIGNED NULL COMMENT 'cMunIni - Código do Município de início da  prestação',
  `NOME_MUNICIPIO_INI_PRESTACAO` VARCHAR(60) NULL COMMENT 'xMunIni - Nome do Município do início da  prestação',
  `UF_INI_PRESTACAO` CHAR(2) NULL COMMENT 'UFIni - UF do início da prestação',
  `CODIGO_MUNICIPIO_FIM_PRESTACAO` INT UNSIGNED NULL COMMENT 'cMunFim - Codigo do Município do término da  prestação',
  `NOME_MUNICIPIO_FIM_PRESTACAO` VARCHAR(60) NULL COMMENT 'xMunFim - Nome do Município de término da  prestação',
  `UF_FIM_PRESTACAO` CHAR(2) NULL COMMENT 'UFFim - UF do término da prestação',
  `RETIRA` CHAR(1) NULL COMMENT 'retira - Indicador se o Recebedor retira no  Aeroporto, Filial, Porto ou Estação de  Destino? - Preencher com: 0 - sim; 1 - não',
  `RETIRA_DETALHE` VARCHAR(160) NULL COMMENT 'xDetRetira - Detalhes do retira',
  `TOMADOR` CHAR(1) NULL COMMENT 'toma - Tomador do Serviço - 0-Remetente;  1-Expedidor;  2-Recebedor;  3-Destinatário; 4 - Outros',
  `DATA_ENTRADA_CONTINGENCIA` TIMESTAMP NULL COMMENT 'dhCont - Informar a data e hora de entrada em contingência no formato AAAA-MM-DDTHH:MM:SS',
  `JUSTIFICATIVA_CONTINGENCIA` VARCHAR(255) NULL COMMENT 'xJust - Justificativa da entrada em contingência',
  `CARAC_ADICIONAL_TRANSPORTE` VARCHAR(15) NULL COMMENT 'xCaracAd - Característica adicional do transporte - Texto livre:  REENTREGA; DEVOLUÇÃO;  REFATURAMENTO; etc',
  `CARAC_ADICIONAL_SERVICO` VARCHAR(30) NULL COMMENT 'xCaracSer - Característica adicional do serviço - Texto livre:  ENTREGA EXPRESSA; LOGÍSTICA  REVERSA; CONVENCIONAL;  EMERGENCIAL; etc',
  `FUNCIONARIO_EMISSOR` VARCHAR(20) NULL COMMENT 'xEmi - Funcionário emissor do CTe',
  `FLUXO_ORIGEM` VARCHAR(15) NULL COMMENT 'xOrig - Sigla ou código interno da  Filial/Porto/Estação/ Aeroporto de Origem - ',
  `ENTREGA_TIPO_PERIODO` CHAR(1) NULL COMMENT 'tpPer - Tipo de data/período programado para  entrega - 0- Sem data definida;1-Na data;  2-Até a data;  3-A partir da data;4-no período',
  `ENTREGA_DATA_PROGRAMADA` DATE NULL COMMENT 'dProg',
  `ENTREGA_DATA_INICIAL` DATE NULL COMMENT 'dIni',
  `ENTREGA_DATA_FINAL` DATE NULL COMMENT 'dFim',
  `ENTREGA_TIPO_HORA` CHAR(1) NULL COMMENT 'tpHor - 0- Sem hora definida;1--No horário;  2-Até o horário;  3-A partir do horário;4 - No intervalo de tempo',
  `ENTREGA_HORA_PROGRAMADA` VARCHAR(8) NULL COMMENT 'hProg',
  `ENTREGA_HORA_INICIAL` VARCHAR(8) NULL COMMENT 'hIni',
  `ENTREGA_HORA_FINAL` VARCHAR(8) NULL COMMENT 'hFim',
  `MUNICIPIO_ORIGEM_CALCULO` VARCHAR(40) NULL COMMENT 'origCalc - Município de origem para efeito de  cálculo do frete',
  `MUNICIPIO_DESTINO_CALCULO` VARCHAR(40) NULL COMMENT 'destCalc - Município de destino para efeito de  cálculo do frete',
  `OBSERVACOES_GERAIS` TEXT NULL COMMENT 'xObs',
  `VALOR_TOTAL_SERVICO` DECIMAL(18,6) NULL COMMENT 'vTPrest - Valor Total da Prestação do Serviço',
  `VALOR_RECEBER` DECIMAL(18,6) NULL COMMENT 'vRec - Valor a Receber',
  `CST` CHAR(2) NULL COMMENT 'CST - classificação Tributária do Serviço',
  `BASE_CALCULO_ICMS` DECIMAL(18,6) NULL COMMENT 'vBC - Base de Cálculo do ICMS',
  `ALIQUOTA_ICMS` DECIMAL(18,6) NULL COMMENT 'pICMS - Alíquota do ICMS',
  `VALOR_ICMS` DECIMAL(18,6) NULL COMMENT 'vICMS - Valor Total do ICMS',
  `PERCENTUAL_REDUCAO_BC_ICMS` DECIMAL(18,6) NULL COMMENT 'pRedBC-  Percentual de redução da BC',
  `VALOR_BC_ICMS_ST_RETIDO` DECIMAL(18,6) NULL COMMENT 'vBCSTRet',
  `VALOR_ICMS_ST_RETIDO` DECIMAL(18,6) NULL COMMENT 'vICMSSTRet',
  `ALIQUOTA_ICMS_ST_RETIDO` DECIMAL(18,6) NULL COMMENT 'pICMSSTRet',
  `VALOR_CREDITO_PRESUMIDO_ICMS` DECIMAL(18,6) NULL COMMENT 'vCred - Valor do Crédito outorgado/Presumido ',
  `PERCENTUAL_BC_ICMS_OUTRA_UF` DECIMAL(18,6) NULL COMMENT 'pRedBCOutraUF',
  `VALOR_BC_ICMS_OUTRA_UF` DECIMAL(18,6) NULL COMMENT 'vBCOutraUF',
  `ALIQUOTA_ICMS_OUTRA_UF` DECIMAL(18,6) NULL COMMENT 'pICMSOutraUF',
  `VALOR_ICMS_OUTRA_UF` DECIMAL(18,6) NULL COMMENT 'vICMSOutraUF',
  `SIMPLES_NACIONAL_INDICADOR` CHAR(1) NULL COMMENT 'indSN - Indica se o contribuinte é Simples  Nacional 1=Sim',
  `SIMPLES_NACIONAL_TOTAL` DECIMAL(18,6) NULL COMMENT 'vTotTrib - Valor de tributos federais, estaduais e  municipais',
  `INFORMACOES_ADD_FISCO` TEXT NULL COMMENT 'infAdFisco - Informações Adicionais de Interesse do Fisco',
  `VALOR_TOTAL_CARGA` DECIMAL(18,6) NULL COMMENT 'vCarga',
  `PRODUTO_PREDOMINANTE` VARCHAR(60) NULL COMMENT 'proPred - Informar a descrição do produto  predominante',
  `CARGA_OUTRAS_CARACTERISTICAS` VARCHAR(30) NULL COMMENT 'xOutCat - \'FRIA\', \'GRANEL\', \'REFRIGERADA\',  \'Medidas: 12X12X12\'',
  `MODAL_VERSAO_LAYOUT` INT UNSIGNED NULL COMMENT 'versaoModal - Versão do leiaute específico para o  Modal',
  `CHAVE_CTE_SUBSTITUIDO` VARCHAR(44) NULL COMMENT 'chCte',
  PRIMARY KEY (`ID`))
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_EMITENTE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_EMITENTE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `CNPJ` VARCHAR(14) NULL,
  `IE` VARCHAR(14) NULL,
  `NOME` VARCHAR(60) NULL COMMENT 'xNome',
  `FANTASIA` VARCHAR(60) NULL COMMENT 'xFant',
  `LOGRADOURO` VARCHAR(60) NULL COMMENT 'xLgr',
  `NUMERO` VARCHAR(60) NULL COMMENT 'nro',
  `COMPLEMENTO` VARCHAR(60) NULL COMMENT 'xCpl',
  `BAIRRO` VARCHAR(60) NULL COMMENT 'xBairro',
  `CODIGO_MUNICIPIO` INT UNSIGNED NULL COMMENT 'cMun - Utilizar a Tabela do IBGE',
  `NOME_MUNICIPIO` VARCHAR(60) NULL COMMENT 'xMun',
  `UF` CHAR(2) NULL COMMENT 'UF',
  `CEP` VARCHAR(8) NULL COMMENT 'CEP',
  `TELEFONE` VARCHAR(14) NULL COMMENT 'fone',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_EMITENTE` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{8E0EA66C-EA89-4C1F-8CF4-778365C7AE3B}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_LOCAL_COLETA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_LOCAL_COLETA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `CNPJ` VARCHAR(14) NULL,
  `CPF` VARCHAR(11) NULL,
  `NOME` VARCHAR(60) NULL COMMENT 'xNome',
  `LOGRADOURO` VARCHAR(250) NULL COMMENT 'xLgr',
  `NUMERO` VARCHAR(60) NULL COMMENT 'nro',
  `COMPLEMENTO` VARCHAR(60) NULL COMMENT 'xCpl',
  `BAIRRO` VARCHAR(60) NULL COMMENT 'xBairro',
  `CODIGO_MUNICIPIO` INT UNSIGNED NULL COMMENT 'cMun - Utilizar a Tabela do IBGE',
  `NOME_MUNICIPIO` VARCHAR(60) NULL COMMENT 'xMun - Informar \'EXTERIOR\' para operações com o exterior.',
  `UF` CHAR(2) NULL COMMENT 'UF - Informar \'EX\' para operações com o exterior.',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_COLETA` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{BB10E3A1-3572-4882-ADE2-B20451A6C58A}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_TOMADOR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_TOMADOR` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `CNPJ` VARCHAR(14) NULL COMMENT 'Em caso de empresa não estabelecida  no Brasil, será informado o CNPJ com  zeros.  Informar os zeros não significativos.',
  `CPF` VARCHAR(11) NULL COMMENT 'Informar os zeros não significativos',
  `IE` VARCHAR(14) NULL COMMENT 'Informar a IE do tomador ou ISENTO se  tomador é contribuinte do ICMS isento de  inscrição no cadastro de contribuintes do  ICMS. Caso o tomador não seja  contribuinte do ICMS não informar o  conteúdo.',
  `NOME` VARCHAR(60) NULL COMMENT 'xNome - Razão Social ou Nome',
  `FANTASIA` VARCHAR(60) NULL COMMENT 'xFant',
  `TELEFONE` VARCHAR(14) NULL COMMENT 'fone',
  `LOGRADOURO` VARCHAR(255) NULL COMMENT 'xLgr',
  `NUMERO` VARCHAR(60) NULL COMMENT 'nro',
  `COMPLEMENTO` VARCHAR(60) NULL COMMENT 'xCpl',
  `BAIRRO` VARCHAR(60) NULL COMMENT 'xBairro',
  `CODIGO_MUNICIPIO` INT UNSIGNED NULL COMMENT 'cMun - Utilizar a Tabela do IBGE',
  `NOME_MUNICIPIO` VARCHAR(60) NULL COMMENT 'xMun - Informar EXTERIOR para operações com  o exterior',
  `UF` CHAR(2) NULL COMMENT 'UF - Informar EX para operações com o  exterior.',
  `CEP` VARCHAR(8) NULL COMMENT 'CEP',
  `CODIGO_PAIS` INT NULL COMMENT 'cPais - Utilizar a Tabela do BACEN',
  `NOME_PAIS` VARCHAR(60) NULL COMMENT 'xPais',
  `EMAIL` VARCHAR(60) NULL COMMENT 'email',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_TOMADOR` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{FB54EDC5-F918-4617-A828-70539984BB46}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_PASSAGEM`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_PASSAGEM` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `SIGLA_PASSAGEM` VARCHAR(15) NULL COMMENT 'xPass - Sigla ou código interno da  Filial/Porto/Estação/Aeroporto de  Passagem',
  `SIGLA_DESTINO` VARCHAR(15) NULL COMMENT 'xDest - Sigla ou código interno da  Filial/Porto/Estação/Aeroporto de Destino',
  `ROTA` VARCHAR(10) NULL COMMENT 'xRota - Código da Rota de Entrega',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_PASSAGEM` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{08191DFB-E061-4102-A065-34C8E0503CDA}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_REMETENTE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_REMETENTE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `CNPJ` VARCHAR(14) NULL,
  `CPF` VARCHAR(11) NULL,
  `IE` VARCHAR(20) NULL COMMENT 'IE',
  `NOME` VARCHAR(60) NULL COMMENT 'xNome',
  `FANTASIA` VARCHAR(60) NULL COMMENT 'xFant',
  `TELEFONE` VARCHAR(14) NULL COMMENT 'fone',
  `LOGRADOURO` VARCHAR(250) NULL COMMENT 'xLgr',
  `NUMERO` VARCHAR(60) NULL COMMENT 'nro',
  `COMPLEMENTO` VARCHAR(60) NULL COMMENT 'xCpl',
  `BAIRRO` VARCHAR(60) NULL COMMENT 'xBairro',
  `CODIGO_MUNICIPIO` INT UNSIGNED NULL COMMENT 'cMun - Utilizar a Tabela do IBGE',
  `NOME_MUNICIPIO` VARCHAR(60) NULL COMMENT 'xMun',
  `UF` CHAR(2) NULL COMMENT 'UF',
  `CEP` VARCHAR(8) NULL COMMENT 'CEP',
  `CODIGO_PAIS` INT NULL COMMENT 'cPais',
  `NOME_PAIS` VARCHAR(60) NULL COMMENT 'xPais',
  `EMAIL` VARCHAR(60) NULL COMMENT 'email',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_REMETENTE` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{44C5AE01-875B-414A-B141-210DE88D5716}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_EXPEDIDOR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_EXPEDIDOR` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `CNPJ` VARCHAR(14) NULL,
  `CPF` VARCHAR(11) NULL,
  `IE` VARCHAR(20) NULL COMMENT 'IE',
  `NOME` VARCHAR(60) NULL COMMENT 'xNome',
  `FANTASIA` VARCHAR(60) NULL COMMENT 'xFant',
  `TELEFONE` VARCHAR(14) NULL COMMENT 'fone',
  `LOGRADOURO` VARCHAR(250) NULL COMMENT 'xLgr',
  `NUMERO` VARCHAR(60) NULL COMMENT 'nro',
  `COMPLEMENTO` VARCHAR(60) NULL COMMENT 'xCpl',
  `BAIRRO` VARCHAR(60) NULL COMMENT 'xBairro',
  `CODIGO_MUNICIPIO` INT UNSIGNED NULL COMMENT 'cMun - Utilizar a Tabela do IBGE',
  `NOME_MUNICIPIO` VARCHAR(60) NULL COMMENT 'xMun',
  `UF` CHAR(2) NULL COMMENT 'UF',
  `CEP` VARCHAR(8) NULL COMMENT 'CEP',
  `CODIGO_PAIS` INT NULL COMMENT 'cPais',
  `NOME_PAIS` VARCHAR(60) NULL COMMENT 'xPais',
  `EMAIL` VARCHAR(60) NULL COMMENT 'email',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_EXPEDIDOR` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{B49F680D-20F5-4D89-945B-ED7A99C5EBFF}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_RECEBEDOR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_RECEBEDOR` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `CNPJ` VARCHAR(14) NULL,
  `CPF` VARCHAR(11) NULL,
  `IE` VARCHAR(20) NULL COMMENT 'IE',
  `NOME` VARCHAR(60) NULL COMMENT 'xNome',
  `FANTASIA` VARCHAR(60) NULL COMMENT 'xFant',
  `TELEFONE` VARCHAR(14) NULL COMMENT 'fone',
  `LOGRADOURO` VARCHAR(250) NULL COMMENT 'xLgr',
  `NUMERO` VARCHAR(60) NULL COMMENT 'nro',
  `COMPLEMENTO` VARCHAR(60) NULL COMMENT 'xCpl',
  `BAIRRO` VARCHAR(60) NULL COMMENT 'xBairro',
  `CODIGO_MUNICIPIO` INT UNSIGNED NULL COMMENT 'cMun - Utilizar a Tabela do IBGE',
  `NOME_MUNICIPIO` VARCHAR(60) NULL COMMENT 'xMun',
  `UF` CHAR(2) NULL COMMENT 'UF',
  `CEP` VARCHAR(8) NULL COMMENT 'CEP',
  `CODIGO_PAIS` INT NULL COMMENT 'cPais',
  `NOME_PAIS` VARCHAR(60) NULL COMMENT 'xPais',
  `EMAIL` VARCHAR(60) NULL COMMENT 'email',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_RECEBEDOR` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{73407675-AEAC-4EF4-80E5-3EF4401CF4D3}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_DESTINATARIO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_DESTINATARIO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `CNPJ` VARCHAR(14) NULL,
  `CPF` VARCHAR(11) NULL,
  `IE` VARCHAR(20) NULL COMMENT 'IE',
  `NOME` VARCHAR(60) NULL COMMENT 'xNome',
  `FANTASIA` VARCHAR(60) NULL COMMENT 'xFant',
  `TELEFONE` VARCHAR(14) NULL COMMENT 'fone',
  `LOGRADOURO` VARCHAR(250) NULL COMMENT 'xLgr',
  `NUMERO` VARCHAR(60) NULL COMMENT 'nro',
  `COMPLEMENTO` VARCHAR(60) NULL COMMENT 'xCpl',
  `BAIRRO` VARCHAR(60) NULL COMMENT 'xBairro',
  `CODIGO_MUNICIPIO` INT UNSIGNED NULL COMMENT 'cMun - Utilizar a Tabela do IBGE',
  `NOME_MUNICIPIO` VARCHAR(60) NULL COMMENT 'xMun',
  `UF` CHAR(2) NULL COMMENT 'UF',
  `CEP` VARCHAR(8) NULL COMMENT 'CEP',
  `CODIGO_PAIS` INT NULL COMMENT 'cPais',
  `NOME_PAIS` VARCHAR(60) NULL COMMENT 'xPais',
  `EMAIL` VARCHAR(60) NULL COMMENT 'email',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_DESTINATARIO` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{BE7E7FC1-4115-40C2-AB9E-A9B2B1B52D98}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_LOCAL_ENTREGA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_LOCAL_ENTREGA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `CNPJ` VARCHAR(14) NULL,
  `CPF` VARCHAR(11) NULL,
  `NOME` VARCHAR(60) NULL COMMENT 'xNome',
  `LOGRADOURO` VARCHAR(250) NULL COMMENT 'xLgr',
  `NUMERO` VARCHAR(60) NULL COMMENT 'nro',
  `COMPLEMENTO` VARCHAR(60) NULL COMMENT 'xCpl',
  `BAIRRO` VARCHAR(60) NULL COMMENT 'xBairro',
  `CODIGO_MUNICIPIO` INT UNSIGNED NULL COMMENT 'cMun - Utilizar a Tabela do IBGE',
  `NOME_MUNICIPIO` VARCHAR(60) NULL COMMENT 'xMun - Informar \'EXTERIOR\' para operações com o exterior.',
  `UF` CHAR(2) NULL COMMENT 'UF - Informar \'EX\' para operações com o exterior.',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_ENTREGA` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{754026B8-770A-45E5-B53C-385E92689A57}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_COMPONENTE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_COMPONENTE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `NOME` VARCHAR(15) NULL COMMENT 'xNome - Nome do componente - Exemplos: FRETE PESO, FRETE  VALOR, SEC/CAT, ADEME,  AGENDAMENTO, etc',
  `VALOR` DECIMAL(18,6) NULL COMMENT 'vComp - Valor do componente',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_COMPONENTE` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{6A4B28DA-0868-47B1-B135-35B9AB1D1598}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_CARGA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_CARGA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `CODIGO_UNIDADE_MEDIDA` CHAR(2) NULL COMMENT 'cUnid - 00-M3;  01-KG;  02-TON;  03-UNIDADE;  04-LITROS;  05-MMBTU',
  `TIPO_MEDIDA` VARCHAR(20) NULL COMMENT 'tpMed - Exemplos:  PESO BRUTO, PESO DECLARADO,  PESO CUBADO, PESO AFORADO,  PESO AFERIDO, PESO BASE DE  CÁLCULO, LITRAGEM, CAIXAS e etc',
  `QUANTIDADE` DECIMAL(18,6) NULL COMMENT 'qCarga',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_CARGA` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{9B8A42CB-E13A-475F-81FE-5E7C48ED6E2F}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_INFORMACAO_NF_OUTROS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_INFORMACAO_NF_OUTROS` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `NUMERO_ROMANEIO` VARCHAR(20) NULL COMMENT 'nRoma - Número do Romaneio da NF',
  `NUMERO_PEDIDO` VARCHAR(20) NULL COMMENT 'nPed - Número do Pedido da NF',
  `CHAVE_ACESSO_NFE` VARCHAR(44) NULL COMMENT 'chave',
  `CODIGO_MODELO` CHAR(2) NULL COMMENT 'mod',
  `SERIE` CHAR(3) NULL COMMENT 'serie',
  `NUMERO` VARCHAR(20) NULL COMMENT 'nDoc',
  `DATA_EMISSAO` DATE NULL COMMENT 'dEmi',
  `UF_EMITENTE` INT UNSIGNED NULL COMMENT 'cUF - Código da UF do emitente do Documento Fiscal. Utilizar a Tabela do IBGE de código de unidades da federação (Anexo IX - Tabela de UF, Município e País)',
  `BASE_CALCULO_ICMS` DECIMAL(18,6) NULL COMMENT 'vBC - Base de Cálculo do ICMS',
  `VALOR_ICMS` DECIMAL(18,6) NULL COMMENT 'vICMS - Valor Total do ICMS',
  `BASE_CALCULO_ICMS_ST` DECIMAL(18,6) NULL COMMENT 'vBCST - Base de Cálculo do ICMS ST',
  `VALOR_ICMS_ST` DECIMAL(18,6) NULL COMMENT 'vST - Valor Total do ICMS ST',
  `VALOR_TOTAL_PRODUTOS` DECIMAL(18,6) NULL COMMENT 'vProd - Valor Total dos produtos e  serviços',
  `VALOR_TOTAL` DECIMAL(18,6) NULL COMMENT 'vNF',
  `CFOP_PREDOMINANTE` INT UNSIGNED NULL COMMENT 'nCFOP',
  `PESO_TOTAL_KG` DECIMAL(18,6) NULL COMMENT 'nPeso',
  `PIN_SUFRAMA` INT UNSIGNED NULL COMMENT 'PIN',
  `DATA_PREVISTA_ENTREGA` DATE NULL COMMENT 'dPrev',
  `OUTRO_TIPO_DOC_ORIG` CHAR(2) NULL COMMENT 'tpDoc - Tipo de documento originário - Preencher com:  00 - Declaração;10 - Dutoviário;  99 - Outros',
  `OUTRO_DESCRICAO` VARCHAR(100) NULL COMMENT 'descOutros - Descrição quando se tratar de 99-Outros',
  `OUTRO_VALOR_DOCUMENTO` DECIMAL(18,6) NULL COMMENT 'vDocFisc - Valor do documento',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_INFORMACAO_NF` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{9E6856B9-7569-4D16-BE24-71BF8F64DC1E}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Tabela que armazena o cabeçalho das notas fiscais eletrônicas.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_INFORMACAO_NF_TRANSPORTE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_INFORMACAO_NF_TRANSPORTE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_INFORMACAO_NF` INT UNSIGNED NOT NULL,
  `TIPO_UNIDADE_TRANSPORTE` CHAR(1) NULL COMMENT 'tpUnidTransp - 1 - Rodoviário Tração  2 - Rodoviário Reboque  3 - Navio  4 - Balsa  5 - Aeronave  6 - Vagão  7 - Outros',
  `ID_UNIDADE_TRANSPORTE` VARCHAR(20) NULL COMMENT 'idUnidTransp - Informar a identificaçãoconforme o tipo  de unidade de transporte.  Por exemplo: para rodoviário tração ou  reboque deverá preencher com a placa  do veículo.',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_INF_NF_TRANSP` (`ID_CTE_INFORMACAO_NF` ASC),
  CONSTRAINT `fk_{A8CC2C9B-5112-4914-AA89-D8E0D2B6E646}`
    FOREIGN KEY (`ID_CTE_INFORMACAO_NF`)
    REFERENCES `fenix`.`CTE_INFORMACAO_NF_OUTROS` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_INF_NF_TRANSPORTE_LACRE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_INF_NF_TRANSPORTE_LACRE` (
  `ID` INT UNSIGNED NOT NULL,
  `ID_CTE_INFORMACAO_NF_TRANSPORTE` INT UNSIGNED NOT NULL,
  `NUMERO` VARCHAR(20) NULL COMMENT 'nLacre',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_INF_NF_TRANS_LACRE` (`ID_CTE_INFORMACAO_NF_TRANSPORTE` ASC),
  CONSTRAINT `fk_{95A36096-9355-464E-A5DB-22B8F81E9908}`
    FOREIGN KEY (`ID_CTE_INFORMACAO_NF_TRANSPORTE`)
    REFERENCES `fenix`.`CTE_INFORMACAO_NF_TRANSPORTE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_INFORMACAO_NF_CARGA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_INFORMACAO_NF_CARGA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_INFORMACAO_NF` INT UNSIGNED NOT NULL,
  `TIPO_UNIDADE_CARGA` CHAR(1) NULL COMMENT 'tpUnidCarga - 1 - Container  2 - ULD  3 - Pallet  4 - Outros',
  `ID_UNIDADE_CARGA` VARCHAR(20) NULL COMMENT 'idUnidCarga - Informar a identificação da unidade de  carga, por exemplo: número do container.',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_INF_NF_CARGA` (`ID_CTE_INFORMACAO_NF` ASC),
  CONSTRAINT `fk_{5DB05F41-B797-49A1-B003-D7B4DF8BF69C}`
    FOREIGN KEY (`ID_CTE_INFORMACAO_NF`)
    REFERENCES `fenix`.`CTE_INFORMACAO_NF_OUTROS` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_INF_NF_CARGA_LACRE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_INF_NF_CARGA_LACRE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_INFORMACAO_NF_CARGA` INT UNSIGNED NOT NULL,
  `NUMERO` VARCHAR(20) NULL COMMENT 'nLacre',
  `QUANTIDADE_RATEADA` DECIMAL(18,6) NULL COMMENT 'qtdRat',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_INF_CARGA_LACRE` (`ID_CTE_INFORMACAO_NF_CARGA` ASC),
  CONSTRAINT `fk_{54ED8F7F-282E-4133-B2EF-771071F016AC}`
    FOREIGN KEY (`ID_CTE_INFORMACAO_NF_CARGA`)
    REFERENCES `fenix`.`CTE_INFORMACAO_NF_CARGA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_DOCUMENTO_ANTERIOR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_DOCUMENTO_ANTERIOR` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `CNPJ` VARCHAR(14) NULL,
  `CPF` VARCHAR(11) NULL,
  `IE` VARCHAR(20) NULL COMMENT 'IE',
  `UF` CHAR(2) NULL COMMENT 'UF',
  `NOME` VARCHAR(60) NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_DOC_ANTERIOR` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{A53759A7-D844-4D7F-A5EF-B72EACB5ED21}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_DOCUMENTO_ANTERIOR_ID`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_DOCUMENTO_ANTERIOR_ID` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_DOCUMENTO_ANTERIOR` INT UNSIGNED NOT NULL,
  `TIPO` CHAR(2) NULL COMMENT 'tpDoc - Preencher com:  00-CTRC;  01-CTAC;  02-ACT;  03 - NF Modelo 7;  04 - NF Modelo 27;  05-Conhecimento Aéreo Nacional;  06-CTMC;  07-ATRE;  08-DTA (Despacho de Transito  Aduaneiro);  09-Conhecimento Aéreo Internacional;  10  Conhecimento - Carta de Porte  Internacional;  11  Conhecimento Avulso;  12-TIF (Transporte Internacional  Ferroviário);  99 - outros',
  `SERIE` CHAR(3) NULL COMMENT 'serie',
  `SUBSERIE` CHAR(2) NULL COMMENT 'subser',
  `NUMERO` VARCHAR(20) NULL COMMENT 'nDoc',
  `DATA_EMISSAO` DATE NULL COMMENT 'dEmi',
  `CHAVE_CTE` VARCHAR(44) NULL COMMENT 'chave',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_DOC_ANTERIOR_ID` (`ID_CTE_DOCUMENTO_ANTERIOR` ASC),
  CONSTRAINT `fk_{9A75406F-0C01-483F-8130-100DDC289E23}`
    FOREIGN KEY (`ID_CTE_DOCUMENTO_ANTERIOR`)
    REFERENCES `fenix`.`CTE_DOCUMENTO_ANTERIOR` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_SEGURO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_SEGURO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `RESPONSAVEL` CHAR(1) NULL COMMENT 'respSeg - Preencher com:  0- Remetente;  1- Expedidor;  2 - Recebedor;3 - Destinatário;  4 - Emitente do CT-e;  5 - Tomador de Serviço.  Dados obrigatórios apenas no modal  Rodoviário, depois da lei 11.442/07. Para  os demais modais esta informação é  opcional',
  `SEGURADORA` VARCHAR(30) NULL COMMENT 'xSeg',
  `APOLICE` VARCHAR(20) NULL COMMENT 'nApol',
  `AVERBACAO` VARCHAR(20) NULL COMMENT 'nAver',
  `VALOR_CARGA` DECIMAL(18,6) NULL COMMENT 'vCarga',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_SEGURO` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{C775F390-C782-4417-A741-5F64688574C5}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_PERIGOSO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_PERIGOSO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `NUMERO_ONU` VARCHAR(4) NULL COMMENT 'nONU',
  `NOME_APROPRIADO` VARCHAR(150) NULL COMMENT 'xNomeAE',
  `CLASSE_RISCO` VARCHAR(40) NULL COMMENT 'xClaRisco',
  `GRUPO_EMBALAGEM` VARCHAR(6) NULL COMMENT 'grEmb',
  `QUANTIDADE_TOTAL_PRODUTO` VARCHAR(20) NULL COMMENT 'qTotProd',
  `QUANTIDADE_TIPO_VOLUME` VARCHAR(60) NULL COMMENT 'qVolTipo',
  `PONTO_FULGOR` VARCHAR(6) NULL COMMENT 'pontoFulgor',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_PERIGO` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{B945A292-6A0E-4DF9-83F2-A595019696D1}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_VEICULO_NOVO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_VEICULO_NOVO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `CHASSI` VARCHAR(17) NULL COMMENT 'chassi - VIN (código-identificaçãoveículo)',
  `COR` VARCHAR(4) NULL COMMENT 'cCor - Código de cada montadora',
  `DESCRICAO_COR` VARCHAR(40) NULL COMMENT 'xCor',
  `CODIGO_MARCA_MODELO` VARCHAR(6) NULL COMMENT 'cMod - Utilizar Tabela RENAVAM',
  `VALOR_UNITARIO` DECIMAL(18,6) NULL COMMENT 'vUnit',
  `VALOR_FRETE` DECIMAL(18,6) NULL COMMENT 'vFrete',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_VEICULO_NOVO` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{7276AE5B-E77E-49F1-B481-FA55D8680C6D}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Grupo do detalhamento de Veículos novos. Informar apenas quando se tratar de veículos novos'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_FATURA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_FATURA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `NUMERO` VARCHAR(60) NULL COMMENT 'nFat - Número da Fatura',
  `VALOR_ORIGINAL` DECIMAL(18,6) NULL COMMENT 'vOrig - Valor Original da Fatura',
  `VALOR_DESCONTO` DECIMAL(18,6) NULL COMMENT 'vDesc - Valor do desconto',
  `VALOR_LIQUIDO` DECIMAL(18,6) NULL COMMENT 'vLiq - Valor Líquido da Fatura',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_FATURA` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{6438723E-81DB-4412-ABEC-40020AAE5DF6}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Grupo da Fatura'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_DUPLICATA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_DUPLICATA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `NUMERO` VARCHAR(60) NULL COMMENT 'nDup - Número da Duplicata',
  `DATA_VENCIMENTO` DATE NULL COMMENT 'dVenc - Data de vencimento',
  `VALOR` DECIMAL(18,6) NULL COMMENT 'vDup - Valor da duplicata',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_DUPLICATA` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{59427324-F43F-494C-B707-2C3544786351}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Informações das duplicatas.'
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_RODOVIARIO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_RODOVIARIO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `RNTRC` VARCHAR(8) NULL,
  `DATA_PREVISTA_ENTREGA` DATE NULL COMMENT 'dPrev',
  `INDICADOR_LOTACAO` CHAR(1) NULL COMMENT 'lota - Preencher com: 0 - Não; 1 - Sim  Será lotação quando houver um único  conhecimento de transporte por veículo,  ou combinação veicular, e por viagem',
  `CIOT` INT UNSIGNED NULL COMMENT 'Código Identificador da Operação de  Transporte',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_RODOVIARIO` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{F5250044-0CC7-4629-AFFF-D28B8FDA5EA1}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_RODOVIARIO_OCC`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_RODOVIARIO_OCC` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_RODOVIARIO` INT UNSIGNED NOT NULL,
  `SERIE` CHAR(3) NULL COMMENT 'serie',
  `NUMERO` INT UNSIGNED NULL COMMENT 'nOcc',
  `DATA_EMISSAO` DATE NULL COMMENT 'dEmi',
  `CNPJ` VARCHAR(14) NULL,
  `CODIGO_INTERNO` VARCHAR(10) NULL COMMENT 'cInt',
  `IE` VARCHAR(14) NULL,
  `UF` CHAR(2) NULL,
  `TELEFONE` VARCHAR(14) NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_ROD_OCC` (`ID_CTE_RODOVIARIO` ASC),
  CONSTRAINT `fk_{DD4F9E16-BFDD-4934-A6A0-AFAD56955E2A}`
    FOREIGN KEY (`ID_CTE_RODOVIARIO`)
    REFERENCES `fenix`.`CTE_RODOVIARIO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_RODOVIARIO_PEDAGIO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_RODOVIARIO_PEDAGIO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_RODOVIARIO` INT UNSIGNED NOT NULL,
  `CNPJ_FORNECEDOR` VARCHAR(14) NULL COMMENT 'CNPJForn',
  `COMPROVANTE_COMPRA` VARCHAR(20) NULL COMMENT 'nCompra',
  `CNPJ_RESPONSAVEL` VARCHAR(14) NULL COMMENT 'CNPJPg',
  `VALOR` DECIMAL(18,6) NULL COMMENT 'vValePed',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_ROD_PEDAGIO` (`ID_CTE_RODOVIARIO` ASC),
  CONSTRAINT `fk_{1DD17407-7E79-4DC1-B8EC-40D707A49A50}`
    FOREIGN KEY (`ID_CTE_RODOVIARIO`)
    REFERENCES `fenix`.`CTE_RODOVIARIO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_RODOVIARIO_VEICULO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_RODOVIARIO_VEICULO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_RODOVIARIO` INT UNSIGNED NOT NULL,
  `CODIGO_INTERNO` VARCHAR(10) NULL COMMENT 'cInt',
  `RENAVAM` VARCHAR(11) NULL,
  `PLACA` VARCHAR(7) NULL COMMENT 'placa',
  `TARA` INT UNSIGNED NULL COMMENT 'tara',
  `CAPACIDADE_KG` INT UNSIGNED NULL COMMENT 'capKG',
  `CAPACIDADE_M3` INT UNSIGNED NULL COMMENT 'capM3',
  `TIPO_PROPRIEDADE` CHAR(1) NULL COMMENT 'tpProp - Preencher com:  P- Próprio;  T- terceiro.  Será próprio quando o proprietário, coproprietário ou arrendatário do veículo for  o Emitente do CT-e, caso contrário será  caracterizado como de propriedade de  Terceiro',
  `TIPO_VEICULO` CHAR(1) NULL COMMENT 'tpVeic - 0-Tração; 1-Reboque',
  `TIPO_RODADO` CHAR(2) NULL COMMENT 'tpRod - 00 - não aplicável;  01 - Truck;  02 - Toco;  03 - Cavalo Mecânico;  04 - VAN;  05 - Utilitário;  06 - Outros',
  `TIPO_CARROCERIA` CHAR(2) NULL COMMENT 'tpCar - 00 - não aplicável;  01 - Aberta;  02 - Fechada/Baú;  03 - Granelera;  04 - Porta Container;  05 - Sider',
  `UF` CHAR(2) NULL,
  `PROPRIETARIO_CPF` VARCHAR(11) NULL,
  `PROPRIETARIO_CNPJ` VARCHAR(14) NULL,
  `PROPRIETARIO_RNTRC` VARCHAR(8) NULL,
  `PROPRIETARIO_NOME` VARCHAR(60) NULL,
  `PROPRIETARIO_IE` VARCHAR(14) NULL,
  `PROPRIETARIO_UF` CHAR(2) NULL,
  `PROPRIETARIO_TIPO` CHAR(1) NULL COMMENT 'tpProp - Preencher com:  0-TAC  Agregado;  1-TAC Independente; ou  2  Outros',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_ROD_VEICULO` (`ID_CTE_RODOVIARIO` ASC),
  CONSTRAINT `fk_{8F53C9C0-2DBD-4833-9A6F-65464DB09C72}`
    FOREIGN KEY (`ID_CTE_RODOVIARIO`)
    REFERENCES `fenix`.`CTE_RODOVIARIO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_RODOVIARIO_LACRE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_RODOVIARIO_LACRE` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_RODOVIARIO` INT UNSIGNED NOT NULL,
  `NUMERO` VARCHAR(20) NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_ROD_LACRE` (`ID_CTE_RODOVIARIO` ASC),
  CONSTRAINT `fk_{004EE9FA-6941-4146-834D-227A24BAD2A6}`
    FOREIGN KEY (`ID_CTE_RODOVIARIO`)
    REFERENCES `fenix`.`CTE_RODOVIARIO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_RODOVIARIO_MOTORISTA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_RODOVIARIO_MOTORISTA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_RODOVIARIO` INT UNSIGNED NOT NULL,
  `NOME` VARCHAR(60) NULL,
  `CPF` VARCHAR(11) NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_ROD_MOTORISTA` (`ID_CTE_RODOVIARIO` ASC),
  CONSTRAINT `fk_{C8EBA555-FAA4-4B1E-BBD6-6544E2BC65B6}`
    FOREIGN KEY (`ID_CTE_RODOVIARIO`)
    REFERENCES `fenix`.`CTE_RODOVIARIO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_AEREO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_AEREO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `NUMERO_MINUTA` INT UNSIGNED NULL COMMENT 'nMinu',
  `NUMERO_CONHECIMENTO` INT UNSIGNED NULL COMMENT 'nOCA',
  `DATA_PREVISTA_ENTREGA` DATE NULL COMMENT 'dPrevAereo',
  `ID_EMISSOR` VARCHAR(20) NULL COMMENT 'xLAgEmi',
  `ID_INTERNA_TOMADOR` VARCHAR(14) NULL COMMENT 'IdT',
  `TARIFA_CLASSE` CHAR(1) NULL COMMENT 'CL - Preencher com:  M - Tarifa Mínima;  G - Tarifa Geral;  E - Tarifa Específica',
  `TARIFA_CODIGO` VARCHAR(4) NULL COMMENT 'cTar',
  `TARIFA_VALOR` DECIMAL(18,6) NULL COMMENT 'vTar',
  `CARGA_DIMENSAO` VARCHAR(14) NULL COMMENT 'xDime',
  `CARGA_INFORMACAO_MANUSEIO` CHAR(1) NULL COMMENT 'cInfManu - 1 - certificado do expedidor para  embarque de animal vivo;  2 - artigo perigoso conforme Declaração  do Expedidor anexa;  3 - somente em aeronave cargueira;  4 - artigo perigoso - declaração do  expedidor não requerida;  5 - artigo perigoso em quantidade isenta;  6 - gelo seco para refrigeração  (especificar no campo observações a  quantidade)  7 - não restrito (especificar a Disposição  Especial no campo observações)  8 - artigo perigoso em carga consolidada  (especificar a quantidade no campo  observações)  9 - autorização da autoridade  governamental anexa (especificar no  campo observações)  99 - outro (especificar no campo  observações)',
  `CARGA_ESPECIAL` CHAR(3) NULL COMMENT 'cIMP',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_AEREO` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{582ED563-2B8F-4348-9777-94FA1657E73D}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_AQUAVIARIO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_AQUAVIARIO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `VALOR_PRESTACAO` DECIMAL(18,6) NULL COMMENT 'vPrest',
  `AFRMM` DECIMAL(18,6) NULL COMMENT 'vAFRMM',
  `NUMERO_BOOKING` VARCHAR(10) NULL COMMENT 'nBooking',
  `NUMERO_CONTROLE` VARCHAR(10) NULL COMMENT 'nCtrl',
  `ID_NAVIO` VARCHAR(60) NULL COMMENT 'xNavio',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_AQUAVIARIO` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{EDB7A037-01B1-41C0-935C-32998709A4CA}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_AQUAVIARIO_BALSA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_AQUAVIARIO_BALSA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_AQUAVIARIO` INT UNSIGNED NOT NULL,
  `ID_BALSA` VARCHAR(60) NULL COMMENT 'xBalsa',
  `NUMERO_VIAGEM` INT UNSIGNED NULL COMMENT 'nViag',
  `DIRECAO` CHAR(1) NULL COMMENT 'direc - Preencher com:N-Norte, L-Leste, S-Sul,  O-Oeste',
  `PORTO_EMBARQUE` VARCHAR(60) NULL COMMENT 'prtEmb',
  `PORTO_TRANSBORDO` VARCHAR(60) NULL COMMENT 'prtTrans',
  `PORTO_DESTINO` VARCHAR(60) NULL COMMENT 'prtDest',
  `TIPO_NAVEGACAO` CHAR(1) NULL COMMENT 'tpNav - Preencher com:  0 - Interior;  1 - Cabotagem',
  `IRIN` VARCHAR(10) NULL COMMENT 'irin',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_AQUA_BALSA` (`ID_CTE_AQUAVIARIO` ASC),
  CONSTRAINT `fk_{C55A2FC7-979F-4114-A228-699EFFDFF750}`
    FOREIGN KEY (`ID_CTE_AQUAVIARIO`)
    REFERENCES `fenix`.`CTE_AQUAVIARIO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_FERROVIARIO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_FERROVIARIO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `TIPO_TRAFEGO` CHAR(1) NULL COMMENT 'tpTraf - Preencher com:  0-Próprio;  1-Mútuo;  2-Rodoferroviário;  3-Rodoviário.',
  `RESPONSAVEL_FATURAMENTO` CHAR(1) NULL COMMENT 'respFat - Preencher com:  1-Ferrovia de origem;  2-Ferrovia de destino',
  `FERROVIA_EMITENTE_CTE` CHAR(1) NULL COMMENT 'ferrEmi - Preencher com:  1-Ferrovia de origem;  2-Ferrovia de destino',
  `FLUXO` VARCHAR(10) NULL COMMENT 'fluxo',
  `ID_TREM` VARCHAR(7) NULL COMMENT 'idTrem',
  `VALOR_FRETE` DECIMAL(18,6) NULL COMMENT 'vFrete',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_FERROVIARIO` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{9633509A-D7F9-476B-B37E-8210A6404789}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_FERROVIARIO_FERROVIA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_FERROVIARIO_FERROVIA` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_FERROVIARIO` INT UNSIGNED NOT NULL,
  `CNPJ` VARCHAR(14) NULL,
  `CODIGO_INTERNO` VARCHAR(10) NULL,
  `IE` VARCHAR(20) NULL COMMENT 'IE',
  `NOME` VARCHAR(60) NULL COMMENT 'xNome',
  `LOGRADOURO` VARCHAR(250) NULL COMMENT 'xLgr',
  `NUMERO` VARCHAR(60) NULL COMMENT 'nro',
  `COMPLEMENTO` VARCHAR(60) NULL COMMENT 'xCpl',
  `BAIRRO` VARCHAR(60) NULL COMMENT 'xBairro',
  `CODIGO_MUNICIPIO` INT UNSIGNED NULL COMMENT 'cMun - Utilizar a Tabela do IBGE',
  `NOME_MUNICIPIO` VARCHAR(60) NULL COMMENT 'xMun',
  `UF` CHAR(2) NULL COMMENT 'UF',
  `CEP` VARCHAR(8) NULL COMMENT 'CEP',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_FERRO_VIA` (`ID_CTE_FERROVIARIO` ASC),
  CONSTRAINT `fk_{AB21B268-A4C7-48C0-AB57-87A4561FC4F2}`
    FOREIGN KEY (`ID_CTE_FERROVIARIO`)
    REFERENCES `fenix`.`CTE_FERROVIARIO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_FERROVIARIO_VAGAO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_FERROVIARIO_VAGAO` (
  `ID` INT UNSIGNED NOT NULL,
  `ID_CTE_FERROVIARIO` INT UNSIGNED NOT NULL,
  `NUMERO_VAGAO` INT UNSIGNED NULL COMMENT 'nVag',
  `CAPACIDADE` DECIMAL(18,6) NULL COMMENT 'cap',
  `TIPO_VAGAO` CHAR(3) NULL COMMENT 'tpVag',
  `PESO_REAL` DECIMAL(18,6) NULL COMMENT 'pesoR',
  `PESO_BC` DECIMAL(18,6) NULL COMMENT 'pesoBC',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_FERRO_VAGAO` (`ID_CTE_FERROVIARIO` ASC),
  CONSTRAINT `fk_{3913F060-202C-40F7-8696-5C45BD7ED67A}`
    FOREIGN KEY (`ID_CTE_FERROVIARIO`)
    REFERENCES `fenix`.`CTE_FERROVIARIO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_DUTOVIARIO`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_DUTOVIARIO` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `VALOR_TARIFA` DECIMAL(18,6) NULL COMMENT 'vTar',
  `DATA_INICIO` DATE NULL,
  `DATA_FIM` DATE NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_DUTO` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{9FEE0E93-DDB5-4B3F-ADA6-8301E12F3CD9}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `fenix`.`CTE_MULTIMODAL`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`CTE_MULTIMODAL` (
  `ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `ID_CTE_CABECALHO` INT UNSIGNED NOT NULL,
  `COTM` VARCHAR(20) NULL,
  `INDICADOR_NEGOCIAVEL` CHAR(1) NULL COMMENT 'Indicador Negociável  Preencher com: 0 - Não Negociável; 1 -  Negociável',
  PRIMARY KEY (`ID`),
  INDEX `FK_CTE_CAB_MULTI` (`ID_CTE_CABECALHO` ASC),
  CONSTRAINT `fk_{D4D31624-133A-4E99-9505-EFF8AA9EE66C}`
    FOREIGN KEY (`ID_CTE_CABECALHO`)
    REFERENCES `fenix`.`CTE_CABECALHO` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;

USE `fenix` ;

-- -----------------------------------------------------
-- Placeholder table for view `fenix`.`view_fin_cheque_nao_compensado`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`view_fin_cheque_nao_compensado` (`ID` INT, `ID_CONTA_CAIXA` INT, `NOME_CONTA_CAIXA` INT, `TALAO` INT, `NUMERO_TALAO` INT, `NUMERO_CHEQUE` INT, `STATUS_CHEQUE` INT, `DATA_STATUS` INT, `VALOR` INT);

-- -----------------------------------------------------
-- Placeholder table for view `fenix`.`view_fin_fluxo_caixa`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`view_fin_fluxo_caixa` (`ID` INT, `ID_CONTA_CAIXA` INT, `NOME_CONTA_CAIXA` INT, `NOME_PESSOA` INT, `DATA_LANCAMENTO` INT, `DATA_VENCIMENTO` INT, `VALOR` INT, `CODIGO_SITUACAO` INT, `DESCRICAO_SITUACAO` INT, `OPERACAO` INT);

-- -----------------------------------------------------
-- Placeholder table for view `fenix`.`view_fin_lancamento_pagar`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`view_fin_lancamento_pagar` (`ID` INT, `ID_FIN_LANCAMENTO_PAGAR` INT, `QUANTIDADE_PARCELA` INT, `VALOR_LANCAMENTO` INT, `DATA_LANCAMENTO` INT, `NUMERO_DOCUMENTO` INT, `ID_FIN_PARCELA_PAGAR` INT, `NUMERO_PARCELA` INT, `DATA_VENCIMENTO` INT, `VALOR_PARCELA` INT, `TAXA_JURO` INT, `VALOR_JURO` INT, `TAXA_MULTA` INT, `VALOR_MULTA` INT, `TAXA_DESCONTO` INT, `VALOR_DESCONTO` INT, `SIGLA_DOCUMENTO` INT, `NOME_FORNECEDOR` INT, `ID_FIN_STATUS_PARCELA` INT, `SITUACAO_PARCELA` INT, `DESCRICAO_SITUACAO_PARCELA` INT, `ID_BANCO_CONTA_CAIXA` INT, `NOME_CONTA_CAIXA` INT);

-- -----------------------------------------------------
-- Placeholder table for view `fenix`.`view_fin_movimento_caixa_banco`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`view_fin_movimento_caixa_banco` (`ID` INT, `ID_CONTA_CAIXA` INT, `NOME_CONTA_CAIXA` INT, `NOME_PESSOA` INT, `DATA_LANCAMENTO` INT, `DATA_PAGO_RECEBIDO` INT, `MES_ANO` INT, `HISTORICO` INT, `VALOR` INT, `DESCRICAO_DOCUMENTO_ORIGEM` INT, `OPERACAO` INT);

-- -----------------------------------------------------
-- Placeholder table for view `fenix`.`view_pessoa_cliente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`view_pessoa_cliente` (`ID` INT, `NOME` INT, `TIPO` INT, `EMAIL` INT, `SITE` INT, `CPF_CNPJ` INT, `RG_IE` INT, `DESDE` INT, `TAXA_DESCONTO` INT, `LIMITE_CREDITO` INT, `DATA_CADASTRO` INT, `OBSERVACAO` INT, `ID_PESSOA` INT);

-- -----------------------------------------------------
-- Placeholder table for view `fenix`.`view_pessoa_colaborador`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`view_pessoa_colaborador` (`ID` INT, `NOME` INT, `TIPO` INT, `EMAIL` INT, `SITE` INT, `CPF_CNPJ` INT, `RG_IE` INT, `MATRICULA` INT, `DATA_CADASTRO` INT, `DATA_ADMISSAO` INT, `DATA_DEMISSAO` INT, `CTPS_NUMERO` INT, `CTPS_SERIE` INT, `CTPS_DATA_EXPEDICAO` INT, `CTPS_UF` INT, `OBSERVACAO` INT, `LOGRADOURO` INT, `NUMERO` INT, `COMPLEMENTO` INT, `BAIRRO` INT, `CIDADE` INT, `CEP` INT, `MUNICIPIO_IBGE` INT, `UF` INT, `ID_PESSOA` INT, `ID_CARGO` INT, `ID_SETOR` INT);

-- -----------------------------------------------------
-- Placeholder table for view `fenix`.`view_pessoa_fornecedor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`view_pessoa_fornecedor` (`ID` INT, `NOME` INT, `TIPO` INT, `EMAIL` INT, `SITE` INT, `CPF_CNPJ` INT, `RG_IE` INT, `DESDE` INT, `DATA_CADASTRO` INT, `OBSERVACAO` INT, `ID_PESSOA` INT);

-- -----------------------------------------------------
-- Placeholder table for view `fenix`.`view_pessoa_transportadora`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`view_pessoa_transportadora` (`ID` INT, `NOME` INT, `TIPO` INT, `EMAIL` INT, `SITE` INT, `CPF_CNPJ` INT, `RG_IE` INT, `DATA_CADASTRO` INT, `OBSERVACAO` INT, `ID_PESSOA` INT);

-- -----------------------------------------------------
-- Placeholder table for view `fenix`.`view_pessoa_vendedor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`view_pessoa_vendedor` (`ID` INT, `NOME` INT, `TIPO` INT, `EMAIL` INT, `SITE` INT, `CPF_CNPJ` INT, `RG_IE` INT, `MATRICULA` INT, `DATA_CADASTRO` INT, `DATA_ADMISSAO` INT, `DATA_DEMISSAO` INT, `CTPS_NUMERO` INT, `CTPS_SERIE` INT, `CTPS_DATA_EXPEDICAO` INT, `CTPS_UF` INT, `OBSERVACAO` INT, `LOGRADOURO` INT, `NUMERO` INT, `COMPLEMENTO` INT, `BAIRRO` INT, `CIDADE` INT, `CEP` INT, `MUNICIPIO_IBGE` INT, `UF` INT, `ID_PESSOA` INT, `ID_CARGO` INT, `ID_SETOR` INT, `COMISSAO` INT, `META_VENDA` INT);

-- -----------------------------------------------------
-- Placeholder table for view `fenix`.`view_sped_c190`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`view_sped_c190` (`ID` INT, `CST_ICMS` INT, `CFOP` INT, `ALIQUOTA_ICMS` INT, `DATA_HORA_EMISSAO` INT, `SOMA_VALOR_OPERACAO` INT, `SOMA_BASE_CALCULO_ICMS` INT, `SOMA_VALOR_ICMS` INT, `SOMA_BASE_CALCULO_ICMS_ST` INT, `SOMA_VALOR_ICMS_ST` INT, `SOMA_VL_RED_BC` INT, `SOMA_VALOR_IPI` INT);

-- -----------------------------------------------------
-- Placeholder table for view `fenix`.`view_sped_nfe_destinatario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`view_sped_nfe_destinatario` (`ID` INT, `NOME` INT, `CPF` INT, `CNPJ` INT, `INSCRICAO_ESTADUAL` INT, `CODIGO_MUNICIPIO` INT, `SUFRAMA` INT, `LOGRADOURO` INT, `NUMERO` INT, `COMPLEMENTO` INT, `BAIRRO` INT);

-- -----------------------------------------------------
-- Placeholder table for view `fenix`.`view_sped_nfe_detalhe`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`view_sped_nfe_detalhe` (`ID` INT, `ID_NFE_CABECALHO` INT, `ID_PRODUTO` INT, `NUMERO_ITEM` INT, `CODIGO_PRODUTO` INT, `GTIN` INT, `NOME_PRODUTO` INT, `NCM` INT, `NVE` INT, `CEST` INT, `INDICADOR_ESCALA_RELEVANTE` INT, `CNPJ_FABRICANTE` INT, `CODIGO_BENEFICIO_FISCAL` INT, `EX_TIPI` INT, `CFOP` INT, `UNIDADE_COMERCIAL` INT, `QUANTIDADE_COMERCIAL` INT, `NUMERO_PEDIDO_COMPRA` INT, `ITEM_PEDIDO_COMPRA` INT, `NUMERO_FCI` INT, `NUMERO_RECOPI` INT, `VALOR_UNITARIO_COMERCIAL` INT, `VALOR_BRUTO_PRODUTO` INT, `GTIN_UNIDADE_TRIBUTAVEL` INT, `UNIDADE_TRIBUTAVEL` INT, `QUANTIDADE_TRIBUTAVEL` INT, `VALOR_UNITARIO_TRIBUTAVEL` INT, `VALOR_FRETE` INT, `VALOR_SEGURO` INT, `VALOR_DESCONTO` INT, `VALOR_OUTRAS_DESPESAS` INT, `ENTRA_TOTAL` INT, `VALOR_TOTAL_TRIBUTOS` INT, `PERCENTUAL_DEVOLVIDO` INT, `VALOR_IPI_DEVOLVIDO` INT, `INFORMACOES_ADICIONAIS` INT, `VALOR_SUBTOTAL` INT, `VALOR_TOTAL` INT, `ID_TRIBUT_OPERACAO_FISCAL` INT, `ID_PRODUTO_UNIDADE` INT, `CST_COFINS` INT, `QUANTIDADE_VENDIDA_COFINS` INT, `BASE_CALCULO_COFINS` INT, `ALIQUOTA_COFINS_PERCENTUAL` INT, `ALIQUOTA_COFINS_REAIS` INT, `VALOR_COFINS` INT, `ORIGEM_MERCADORIA` INT, `CST_ICMS` INT, `CSOSN` INT, `MODALIDADE_BC_ICMS` INT, `PERCENTUAL_REDUCAO_BC_ICMS` INT, `VALOR_BC_ICMS` INT, `ALIQUOTA_ICMS` INT, `VALOR_ICMS` INT, `MOTIVO_DESONERACAO_ICMS` INT, `MODALIDADE_BC_ICMS_ST` INT, `PERCENTUAL_MVA_ICMS_ST` INT, `PERCENTUAL_REDUCAO_BC_ICMS_ST` INT, `VALOR_BASE_CALCULO_ICMS_ST` INT, `ALIQUOTA_ICMS_ST` INT, `VALOR_ICMS_ST` INT, `VALOR_BC_ICMS_ST_RETIDO` INT, `VALOR_ICMS_ST_RETIDO` INT, `VALOR_BC_ICMS_ST_DESTINO` INT, `VALOR_ICMS_ST_DESTINO` INT, `ALIQUOTA_CREDITO_ICMS_SN` INT, `VALOR_CREDITO_ICMS_SN` INT, `PERCENTUAL_BC_OPERACAO_PROPRIA` INT, `UF_ST` INT, `VALOR_BC_II` INT, `VALOR_DESPESAS_ADUANEIRAS` INT, `VALOR_IMPOSTO_IMPORTACAO` INT, `VALOR_IOF` INT, `CNPJ_PRODUTOR` INT, `CODIGO_SELO_IPI` INT, `QUANTIDADE_SELO_IPI` INT, `ENQUADRAMENTO_LEGAL_IPI` INT, `CST_IPI` INT, `VALOR_BASE_CALCULO_IPI` INT, `ALIQUOTA_IPI` INT, `QUANTIDADE_UNIDADE_TRIBUTAVEL` INT, `VALOR_UNIDADE_TRIBUTAVEL` INT, `VALOR_IPI` INT, `BASE_CALCULO_ISSQN` INT, `ALIQUOTA_ISSQN` INT, `VALOR_ISSQN` INT, `MUNICIPIO_ISSQN` INT, `ITEM_LISTA_SERVICOS` INT, `CST_PIS` INT, `QUANTIDADE_VENDIDA_PIS` INT, `VALOR_BASE_CALCULO_PIS` INT, `ALIQUOTA_PIS_PERCENTUAL` INT, `ALIQUOTA_PIS_REAIS` INT, `VALOR_PIS` INT);

-- -----------------------------------------------------
-- Placeholder table for view `fenix`.`view_sped_nfe_emitente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`view_sped_nfe_emitente` (`ID` INT, `NOME` INT, `CPF` INT, `CNPJ` INT, `INSCRICAO_ESTADUAL` INT, `CODIGO_MUNICIPIO` INT, `LOGRADOURO` INT, `NUMERO` INT, `COMPLEMENTO` INT, `BAIRRO` INT);

-- -----------------------------------------------------
-- Placeholder table for view `fenix`.`view_sped_nfe_item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fenix`.`view_sped_nfe_item` (`ID` INT, `NOME` INT, `GTIN` INT, `ID_PRODUTO_UNIDADE` INT, `SIGLA` INT, `CODIGO_NCM` INT);

-- -----------------------------------------------------
-- View `fenix`.`view_fin_cheque_nao_compensado`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fenix`.`view_fin_cheque_nao_compensado`;
USE `fenix`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `fenix`.`view_fin_cheque_nao_compensado` AS select 0 AS `ID`,`cc`.`ID` AS `ID_CONTA_CAIXA`,`cc`.`NOME` AS `NOME_CONTA_CAIXA`,`t`.`TALAO` AS `TALAO`,`t`.`NUMERO` AS `NUMERO_TALAO`,`c`.`NUMERO` AS `NUMERO_CHEQUE`,`c`.`STATUS_CHEQUE` AS `STATUS_CHEQUE`,`c`.`DATA_STATUS` AS `DATA_STATUS`,`ce`.`VALOR` AS `VALOR` from (((`fenix`.`cheque` `c` join `fenix`.`talonario_cheque` `t` on((`c`.`ID_TALONARIO_CHEQUE` = `t`.`ID`))) join `fenix`.`banco_conta_caixa` `cc` on((`t`.`ID_BANCO_CONTA_CAIXA` = `cc`.`ID`))) join `fenix`.`fin_cheque_emitido` `ce` on((`ce`.`ID_CHEQUE` = `c`.`ID`))) where (`c`.`STATUS_CHEQUE` <> '3');

-- -----------------------------------------------------
-- View `fenix`.`view_fin_fluxo_caixa`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fenix`.`view_fin_fluxo_caixa`;
USE `fenix`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `fenix`.`view_fin_fluxo_caixa` AS select cast(concat(`lp`.`ID`,`pp`.`ID`) as unsigned) AS `ID`,`cc`.`ID` AS `ID_CONTA_CAIXA`,`cc`.`NOME` AS `NOME_CONTA_CAIXA`,`p`.`NOME` AS `NOME_PESSOA`,`lp`.`DATA_LANCAMENTO` AS `DATA_LANCAMENTO`,`pp`.`DATA_VENCIMENTO` AS `DATA_VENCIMENTO`,`pp`.`VALOR` AS `VALOR`,`sp`.`SITUACAO` AS `CODIGO_SITUACAO`,`sp`.`DESCRICAO` AS `DESCRICAO_SITUACAO`,'S' AS `OPERACAO` from (((((`fenix`.`fin_parcela_pagar` `pp` join `fenix`.`fin_lancamento_pagar` `lp` on((`pp`.`ID_FIN_LANCAMENTO_PAGAR` = `lp`.`ID`))) join `fenix`.`banco_conta_caixa` `cc` on((`lp`.`ID_BANCO_CONTA_CAIXA` = `cc`.`ID`))) join `fenix`.`fornecedor` `f` on((`lp`.`ID_FORNECEDOR` = `f`.`ID`))) join `fenix`.`pessoa` `p` on((`f`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`fin_status_parcela` `sp` on((`pp`.`ID_FIN_STATUS_PARCELA` = `sp`.`ID`))) where ((`sp`.`SITUACAO` = '01') or (`sp`.`SITUACAO` = '04')) union select cast(concat(`lr`.`ID`,`pr`.`ID`) as unsigned) AS `ID`,`cc`.`ID` AS `ID_CONTA_CAIXA`,`cc`.`NOME` AS `NOME_CONTA_CAIXA`,`p`.`NOME` AS `NOME`,`lr`.`DATA_LANCAMENTO` AS `DATA_LANCAMENTO`,`pr`.`DATA_VENCIMENTO` AS `DATA_VENCIMENTO`,`pr`.`VALOR` AS `VALOR`,`sp`.`SITUACAO` AS `SITUACAO`,`sp`.`DESCRICAO` AS `DESCRICAO`,'E' AS `OPERACAO` from (((((`fenix`.`fin_parcela_receber` `pr` join `fenix`.`fin_lancamento_receber` `lr` on((`pr`.`ID_FIN_LANCAMENTO_RECEBER` = `lr`.`ID`))) join `fenix`.`banco_conta_caixa` `cc` on((`lr`.`ID_BANCO_CONTA_CAIXA` = `cc`.`ID`))) join `fenix`.`cliente` `c` on((`lr`.`ID_CLIENTE` = `c`.`ID`))) join `fenix`.`pessoa` `p` on((`c`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`fin_status_parcela` `sp` on((`pr`.`ID_FIN_STATUS_PARCELA` = `sp`.`ID`))) where ((`sp`.`SITUACAO` = '01') or (`sp`.`SITUACAO` = '04')) order by `ID`;

-- -----------------------------------------------------
-- View `fenix`.`view_fin_lancamento_pagar`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fenix`.`view_fin_lancamento_pagar`;
USE `fenix`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `fenix`.`view_fin_lancamento_pagar` AS select cast(concat(`lp`.`ID`,`pp`.`ID`) as unsigned) AS `ID`,`lp`.`ID` AS `ID_FIN_LANCAMENTO_PAGAR`,`lp`.`QUANTIDADE_PARCELA` AS `QUANTIDADE_PARCELA`,`lp`.`VALOR_A_PAGAR` AS `VALOR_LANCAMENTO`,`lp`.`DATA_LANCAMENTO` AS `DATA_LANCAMENTO`,`lp`.`NUMERO_DOCUMENTO` AS `NUMERO_DOCUMENTO`,`pp`.`ID` AS `ID_FIN_PARCELA_PAGAR`,`pp`.`NUMERO_PARCELA` AS `NUMERO_PARCELA`,`pp`.`DATA_VENCIMENTO` AS `DATA_VENCIMENTO`,`pp`.`VALOR` AS `VALOR_PARCELA`,`pp`.`TAXA_JURO` AS `TAXA_JURO`,`pp`.`VALOR_JURO` AS `VALOR_JURO`,`pp`.`TAXA_MULTA` AS `TAXA_MULTA`,`pp`.`VALOR_MULTA` AS `VALOR_MULTA`,`pp`.`TAXA_DESCONTO` AS `TAXA_DESCONTO`,`pp`.`VALOR_DESCONTO` AS `VALOR_DESCONTO`,`doc`.`SIGLA` AS `SIGLA_DOCUMENTO`,`p`.`NOME` AS `NOME_FORNECEDOR`,`s`.`ID` AS `ID_FIN_STATUS_PARCELA`,`s`.`SITUACAO` AS `SITUACAO_PARCELA`,`s`.`DESCRICAO` AS `DESCRICAO_SITUACAO_PARCELA`,`cc`.`ID` AS `ID_BANCO_CONTA_CAIXA`,`cc`.`NOME` AS `NOME_CONTA_CAIXA` from ((((((`fenix`.`fin_lancamento_pagar` `lp` join `fenix`.`fin_parcela_pagar` `pp`) join `fenix`.`fin_documento_origem` `doc`) join `fenix`.`pessoa` `p`) join `fenix`.`fornecedor` `f`) join `fenix`.`fin_status_parcela` `s`) join `fenix`.`banco_conta_caixa` `cc`) where ((`pp`.`ID_FIN_LANCAMENTO_PAGAR` = `lp`.`ID`) and (`pp`.`ID_FIN_STATUS_PARCELA` = `s`.`ID`) and (`lp`.`ID_FIN_DOCUMENTO_ORIGEM` = `doc`.`ID`) and (`lp`.`ID_FORNECEDOR` = `f`.`ID`) and (`f`.`ID_PESSOA` = `p`.`ID`) and (`lp`.`ID_BANCO_CONTA_CAIXA` = `cc`.`ID`));

-- -----------------------------------------------------
-- View `fenix`.`view_fin_movimento_caixa_banco`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fenix`.`view_fin_movimento_caixa_banco`;
USE `fenix`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `fenix`.`view_fin_movimento_caixa_banco` AS select cast(concat(`cc`.`ID`,`lp`.`ID`,`pp`.`ID`) as unsigned) AS `ID`,`cc`.`ID` AS `ID_CONTA_CAIXA`,`cc`.`NOME` AS `NOME_CONTA_CAIXA`,`p`.`NOME` AS `NOME_PESSOA`,`lp`.`DATA_LANCAMENTO` AS `DATA_LANCAMENTO`,`pp`.`DATA_PAGAMENTO` AS `DATA_PAGO_RECEBIDO`,date_format(`pp`.`DATA_PAGAMENTO`,'%m/%Y') AS `MES_ANO`,`pp`.`HISTORICO` AS `HISTORICO`,`pp`.`VALOR_PAGO` AS `VALOR`,`doc`.`DESCRICAO` AS `DESCRICAO_DOCUMENTO_ORIGEM`,'S' AS `OPERACAO` from (((((`fenix`.`fin_parcela_pagar` `pp` join `fenix`.`fin_lancamento_pagar` `lp` on((`pp`.`ID_FIN_LANCAMENTO_PAGAR` = `lp`.`ID`))) join `fenix`.`banco_conta_caixa` `cc` on((`lp`.`ID_BANCO_CONTA_CAIXA` = `cc`.`ID`))) join `fenix`.`fornecedor` `f` on((`lp`.`ID_FORNECEDOR` = `f`.`ID`))) join `fenix`.`pessoa` `p` on((`f`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`fin_documento_origem` `doc` on((`lp`.`ID_FIN_DOCUMENTO_ORIGEM` = `doc`.`ID`))) union select cast(concat(`cc`.`ID`,`lr`.`ID`,`pr`.`ID`) as unsigned) AS `ID`,`cc`.`ID` AS `ID_CONTA_CAIXA`,`cc`.`NOME` AS `NOME_CONTA_CAIXA`,`p`.`NOME` AS `NOME`,`lr`.`DATA_LANCAMENTO` AS `DATA_LANCAMENTO`,`pr`.`DATA_RECEBIMENTO` AS `DATA_PAGO_RECEBIDO`,date_format(`pr`.`DATA_RECEBIMENTO`,'%m/%Y') AS `MES_ANO`,`pr`.`HISTORICO` AS `HISTORICO`,`pr`.`VALOR_RECEBIDO` AS `VALOR_RECEBIDO`,`doc`.`DESCRICAO` AS `DESCRICAO`,'E' AS `OPERACAO` from (((((`fenix`.`fin_parcela_receber` `pr` join `fenix`.`fin_lancamento_receber` `lr` on((`pr`.`ID_FIN_LANCAMENTO_RECEBER` = `lr`.`ID`))) join `fenix`.`banco_conta_caixa` `cc` on((`lr`.`ID_BANCO_CONTA_CAIXA` = `cc`.`ID`))) join `fenix`.`cliente` `c` on((`lr`.`ID_CLIENTE` = `c`.`ID`))) join `fenix`.`pessoa` `p` on((`c`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`fin_documento_origem` `doc` on((`lr`.`ID_FIN_DOCUMENTO_ORIGEM` = `doc`.`ID`))) order by `ID_CONTA_CAIXA`,`DATA_LANCAMENTO`;

-- -----------------------------------------------------
-- View `fenix`.`view_pessoa_cliente`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fenix`.`view_pessoa_cliente`;
USE `fenix`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `fenix`.`view_pessoa_cliente` AS select `c`.`ID` AS `ID`,`p`.`NOME` AS `NOME`,`p`.`TIPO` AS `TIPO`,`p`.`EMAIL` AS `EMAIL`,`p`.`SITE` AS `SITE`,`pf`.`CPF` AS `CPF_CNPJ`,`pf`.`RG` AS `RG_IE`,`c`.`DESDE` AS `DESDE`,`c`.`TAXA_DESCONTO` AS `TAXA_DESCONTO`,`c`.`LIMITE_CREDITO` AS `LIMITE_CREDITO`,`c`.`DATA_CADASTRO` AS `DATA_CADASTRO`,`c`.`OBSERVACAO` AS `OBSERVACAO`,`p`.`ID` AS `ID_PESSOA` from ((`fenix`.`pessoa` `p` join `fenix`.`pessoa_fisica` `pf` on((`pf`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`cliente` `c` on((`c`.`ID_PESSOA` = `p`.`ID`))) where (`p`.`CLIENTE` = 'S') union select `c`.`ID` AS `ID`,`p`.`NOME` AS `NOME`,`p`.`TIPO` AS `TIPO`,`p`.`EMAIL` AS `EMAIL`,`p`.`SITE` AS `SITE`,`pj`.`CNPJ` AS `CPF_CNPJ`,`pj`.`INSCRICAO_ESTADUAL` AS `RG_IE`,`c`.`DESDE` AS `DESDE`,`c`.`TAXA_DESCONTO` AS `TAXA_DESCONTO`,`c`.`LIMITE_CREDITO` AS `LIMITE_CREDITO`,`c`.`DATA_CADASTRO` AS `DATA_CADASTRO`,`c`.`OBSERVACAO` AS `OBSERVACAO`,`p`.`ID` AS `ID_PESSOA` from ((`fenix`.`pessoa` `p` join `fenix`.`pessoa_juridica` `pj` on((`pj`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`cliente` `c` on((`c`.`ID_PESSOA` = `p`.`ID`))) where (`p`.`CLIENTE` = 'S');

-- -----------------------------------------------------
-- View `fenix`.`view_pessoa_colaborador`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fenix`.`view_pessoa_colaborador`;
USE `fenix`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `fenix`.`view_pessoa_colaborador` AS select `c`.`ID` AS `ID`,`p`.`NOME` AS `NOME`,`p`.`TIPO` AS `TIPO`,`p`.`EMAIL` AS `EMAIL`,`p`.`SITE` AS `SITE`,`pf`.`CPF` AS `CPF_CNPJ`,`pf`.`RG` AS `RG_IE`,`c`.`MATRICULA` AS `MATRICULA`,`c`.`DATA_CADASTRO` AS `DATA_CADASTRO`,`c`.`DATA_ADMISSAO` AS `DATA_ADMISSAO`,`c`.`DATA_DEMISSAO` AS `DATA_DEMISSAO`,`c`.`CTPS_NUMERO` AS `CTPS_NUMERO`,`c`.`CTPS_SERIE` AS `CTPS_SERIE`,`c`.`CTPS_DATA_EXPEDICAO` AS `CTPS_DATA_EXPEDICAO`,`c`.`CTPS_UF` AS `CTPS_UF`,`c`.`OBSERVACAO` AS `OBSERVACAO`,`e`.`LOGRADOURO` AS `LOGRADOURO`,`e`.`NUMERO` AS `NUMERO`,`e`.`COMPLEMENTO` AS `COMPLEMENTO`,`e`.`BAIRRO` AS `BAIRRO`,`e`.`CIDADE` AS `CIDADE`,`e`.`CEP` AS `CEP`,`e`.`MUNICIPIO_IBGE` AS `MUNICIPIO_IBGE`,`e`.`UF` AS `UF`,`p`.`ID` AS `ID_PESSOA`,`c`.`ID_CARGO` AS `ID_CARGO`,`c`.`ID_SETOR` AS `ID_SETOR` from (((`fenix`.`pessoa` `p` join `fenix`.`pessoa_fisica` `pf` on((`pf`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`colaborador` `c` on((`c`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`pessoa_endereco` `e` on((`e`.`ID_PESSOA` = `p`.`ID`))) where ((`p`.`COLABORADOR` = 'S') and (`e`.`PRINCIPAL` = 'S')) union select `c`.`ID` AS `ID`,`p`.`NOME` AS `NOME`,`p`.`TIPO` AS `TIPO`,`p`.`EMAIL` AS `EMAIL`,`p`.`SITE` AS `SITE`,`pj`.`CNPJ` AS `CPF_CNPJ`,`pj`.`INSCRICAO_ESTADUAL` AS `RG_IE`,`c`.`MATRICULA` AS `MATRICULA`,`c`.`DATA_CADASTRO` AS `DATA_CADASTRO`,`c`.`DATA_ADMISSAO` AS `DATA_ADMISSAO`,`c`.`DATA_DEMISSAO` AS `DATA_DEMISSAO`,`c`.`CTPS_NUMERO` AS `CTPS_NUMERO`,`c`.`CTPS_SERIE` AS `CTPS_SERIE`,`c`.`CTPS_DATA_EXPEDICAO` AS `CTPS_DATA_EXPEDICAO`,`c`.`CTPS_UF` AS `CTPS_UF`,`c`.`OBSERVACAO` AS `OBSERVACAO`,`e`.`LOGRADOURO` AS `LOGRADOURO`,`e`.`NUMERO` AS `NUMERO`,`e`.`COMPLEMENTO` AS `COMPLEMENTO`,`e`.`BAIRRO` AS `BAIRRO`,`e`.`CIDADE` AS `CIDADE`,`e`.`CEP` AS `CEP`,`e`.`MUNICIPIO_IBGE` AS `MUNICIPIO_IBGE`,`e`.`UF` AS `UF`,`p`.`ID` AS `ID_PESSOA`,`c`.`ID_CARGO` AS `ID_CARGO`,`c`.`ID_SETOR` AS `ID_SETOR` from (((`fenix`.`pessoa` `p` join `fenix`.`pessoa_juridica` `pj` on((`pj`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`colaborador` `c` on((`c`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`pessoa_endereco` `e` on((`e`.`ID_PESSOA` = `p`.`ID`))) where ((`p`.`COLABORADOR` = 'S') and (`e`.`PRINCIPAL` = 'S'));

-- -----------------------------------------------------
-- View `fenix`.`view_pessoa_fornecedor`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fenix`.`view_pessoa_fornecedor`;
USE `fenix`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `fenix`.`view_pessoa_fornecedor` AS select `f`.`ID` AS `ID`,`p`.`NOME` AS `NOME`,`p`.`TIPO` AS `TIPO`,`p`.`EMAIL` AS `EMAIL`,`p`.`SITE` AS `SITE`,`pf`.`CPF` AS `CPF_CNPJ`,`pf`.`RG` AS `RG_IE`,`f`.`DESDE` AS `DESDE`,`f`.`DATA_CADASTRO` AS `DATA_CADASTRO`,`f`.`OBSERVACAO` AS `OBSERVACAO`,`p`.`ID` AS `ID_PESSOA` from ((`fenix`.`pessoa` `p` join `fenix`.`pessoa_fisica` `pf` on((`pf`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`fornecedor` `f` on((`f`.`ID_PESSOA` = `p`.`ID`))) where (`p`.`FORNECEDOR` = 'S') union select `f`.`ID` AS `ID`,`p`.`NOME` AS `NOME`,`p`.`TIPO` AS `TIPO`,`p`.`EMAIL` AS `EMAIL`,`p`.`SITE` AS `SITE`,`pj`.`CNPJ` AS `CPF_CNPJ`,`pj`.`INSCRICAO_ESTADUAL` AS `RG_IE`,`f`.`DESDE` AS `DESDE`,`f`.`DATA_CADASTRO` AS `DATA_CADASTRO`,`f`.`OBSERVACAO` AS `OBSERVACAO`,`p`.`ID` AS `ID_PESSOA` from ((`fenix`.`pessoa` `p` join `fenix`.`pessoa_juridica` `pj` on((`pj`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`fornecedor` `f` on((`f`.`ID_PESSOA` = `p`.`ID`))) where (`p`.`FORNECEDOR` = 'S');

-- -----------------------------------------------------
-- View `fenix`.`view_pessoa_transportadora`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fenix`.`view_pessoa_transportadora`;
USE `fenix`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `fenix`.`view_pessoa_transportadora` AS select `t`.`ID` AS `ID`,`p`.`NOME` AS `NOME`,`p`.`TIPO` AS `TIPO`,`p`.`EMAIL` AS `EMAIL`,`p`.`SITE` AS `SITE`,`pf`.`CPF` AS `CPF_CNPJ`,`pf`.`RG` AS `RG_IE`,`t`.`DATA_CADASTRO` AS `DATA_CADASTRO`,`t`.`OBSERVACAO` AS `OBSERVACAO`,`p`.`ID` AS `ID_PESSOA` from ((`fenix`.`pessoa` `p` join `fenix`.`pessoa_fisica` `pf` on((`pf`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`transportadora` `t` on((`t`.`ID_PESSOA` = `p`.`ID`))) where (`p`.`TRANSPORTADORA` = 'S') union select `t`.`ID` AS `ID`,`p`.`NOME` AS `NOME`,`p`.`TIPO` AS `TIPO`,`p`.`EMAIL` AS `EMAIL`,`p`.`SITE` AS `SITE`,`pj`.`CNPJ` AS `CPF_CNPJ`,`pj`.`INSCRICAO_ESTADUAL` AS `RG_IE`,`t`.`DATA_CADASTRO` AS `DATA_CADASTRO`,`t`.`OBSERVACAO` AS `OBSERVACAO`,`p`.`ID` AS `ID_PESSOA` from ((`fenix`.`pessoa` `p` join `fenix`.`pessoa_juridica` `pj` on((`pj`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`transportadora` `t` on((`t`.`ID_PESSOA` = `p`.`ID`))) where (`p`.`TRANSPORTADORA` = 'S');

-- -----------------------------------------------------
-- View `fenix`.`view_pessoa_vendedor`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fenix`.`view_pessoa_vendedor`;
USE `fenix`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `fenix`.`view_pessoa_vendedor` AS select `c`.`ID` AS `ID`,`p`.`NOME` AS `NOME`,`p`.`TIPO` AS `TIPO`,`p`.`EMAIL` AS `EMAIL`,`p`.`SITE` AS `SITE`,`pf`.`CPF` AS `CPF_CNPJ`,`pf`.`RG` AS `RG_IE`,`c`.`MATRICULA` AS `MATRICULA`,`c`.`DATA_CADASTRO` AS `DATA_CADASTRO`,`c`.`DATA_ADMISSAO` AS `DATA_ADMISSAO`,`c`.`DATA_DEMISSAO` AS `DATA_DEMISSAO`,`c`.`CTPS_NUMERO` AS `CTPS_NUMERO`,`c`.`CTPS_SERIE` AS `CTPS_SERIE`,`c`.`CTPS_DATA_EXPEDICAO` AS `CTPS_DATA_EXPEDICAO`,`c`.`CTPS_UF` AS `CTPS_UF`,`c`.`OBSERVACAO` AS `OBSERVACAO`,`e`.`LOGRADOURO` AS `LOGRADOURO`,`e`.`NUMERO` AS `NUMERO`,`e`.`COMPLEMENTO` AS `COMPLEMENTO`,`e`.`BAIRRO` AS `BAIRRO`,`e`.`CIDADE` AS `CIDADE`,`e`.`CEP` AS `CEP`,`e`.`MUNICIPIO_IBGE` AS `MUNICIPIO_IBGE`,`e`.`UF` AS `UF`,`p`.`ID` AS `ID_PESSOA`,`c`.`ID_CARGO` AS `ID_CARGO`,`c`.`ID_SETOR` AS `ID_SETOR`,`v`.`COMISSAO` AS `COMISSAO`,`v`.`META_VENDA` AS `META_VENDA` from ((((`fenix`.`pessoa` `p` join `fenix`.`pessoa_fisica` `pf` on((`pf`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`colaborador` `c` on((`c`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`pessoa_endereco` `e` on((`e`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`vendedor` `v` on((`v`.`ID_COLABORADOR` = `c`.`ID`))) where ((`p`.`COLABORADOR` = 'S') and (`e`.`PRINCIPAL` = 'S')) union select `c`.`ID` AS `ID`,`p`.`NOME` AS `NOME`,`p`.`TIPO` AS `TIPO`,`p`.`EMAIL` AS `EMAIL`,`p`.`SITE` AS `SITE`,`pj`.`CNPJ` AS `CPF_CNPJ`,`pj`.`INSCRICAO_ESTADUAL` AS `RG_IE`,`c`.`MATRICULA` AS `MATRICULA`,`c`.`DATA_CADASTRO` AS `DATA_CADASTRO`,`c`.`DATA_ADMISSAO` AS `DATA_ADMISSAO`,`c`.`DATA_DEMISSAO` AS `DATA_DEMISSAO`,`c`.`CTPS_NUMERO` AS `CTPS_NUMERO`,`c`.`CTPS_SERIE` AS `CTPS_SERIE`,`c`.`CTPS_DATA_EXPEDICAO` AS `CTPS_DATA_EXPEDICAO`,`c`.`CTPS_UF` AS `CTPS_UF`,`c`.`OBSERVACAO` AS `OBSERVACAO`,`e`.`LOGRADOURO` AS `LOGRADOURO`,`e`.`NUMERO` AS `NUMERO`,`e`.`COMPLEMENTO` AS `COMPLEMENTO`,`e`.`BAIRRO` AS `BAIRRO`,`e`.`CIDADE` AS `CIDADE`,`e`.`CEP` AS `CEP`,`e`.`MUNICIPIO_IBGE` AS `MUNICIPIO_IBGE`,`e`.`UF` AS `UF`,`p`.`ID` AS `ID_PESSOA`,`c`.`ID_CARGO` AS `ID_CARGO`,`c`.`ID_SETOR` AS `ID_SETOR`,`v`.`COMISSAO` AS `COMISSAO`,`v`.`META_VENDA` AS `META_VENDA` from ((((`fenix`.`pessoa` `p` join `fenix`.`pessoa_juridica` `pj` on((`pj`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`colaborador` `c` on((`c`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`pessoa_endereco` `e` on((`e`.`ID_PESSOA` = `p`.`ID`))) join `fenix`.`vendedor` `v` on((`v`.`ID_COLABORADOR` = `c`.`ID`))) where ((`p`.`COLABORADOR` = 'S') and (`e`.`PRINCIPAL` = 'S'));

-- -----------------------------------------------------
-- View `fenix`.`view_sped_c190`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fenix`.`view_sped_c190`;
USE `fenix`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `fenix`.`view_sped_c190` AS select `nfec`.`ID` AS `ID`,`nfed`.`CST_ICMS` AS `CST_ICMS`,`nfed`.`CFOP` AS `CFOP`,`nfed`.`ALIQUOTA_ICMS` AS `ALIQUOTA_ICMS`,`nfec`.`DATA_HORA_EMISSAO` AS `DATA_HORA_EMISSAO`,sum(`nfed`.`VALOR_TOTAL`) AS `SOMA_VALOR_OPERACAO`,sum(`nfed`.`VALOR_BC_ICMS`) AS `SOMA_BASE_CALCULO_ICMS`,sum(`nfed`.`VALOR_ICMS`) AS `SOMA_VALOR_ICMS`,sum(`nfed`.`VALOR_BASE_CALCULO_ICMS_ST`) AS `SOMA_BASE_CALCULO_ICMS_ST`,sum(`nfed`.`VALOR_ICMS_ST`) AS `SOMA_VALOR_ICMS_ST`,sum(`nfed`.`VALOR_OUTRAS_DESPESAS`) AS `SOMA_VL_RED_BC`,sum(`nfed`.`VALOR_IPI`) AS `SOMA_VALOR_IPI` from (`fenix`.`view_sped_nfe_detalhe` `nfed` join `fenix`.`nfe_cabecalho` `nfec` on((`nfed`.`ID_NFE_CABECALHO` = `nfec`.`ID`))) group by `nfec`.`ID`,`nfed`.`CST_ICMS`,`nfed`.`CFOP`,`nfed`.`ALIQUOTA_ICMS`,`nfec`.`DATA_HORA_EMISSAO`;

-- -----------------------------------------------------
-- View `fenix`.`view_sped_nfe_destinatario`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fenix`.`view_sped_nfe_destinatario`;
USE `fenix`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `fenix`.`view_sped_nfe_destinatario` AS select distinct `nfe`.`ID_CLIENTE` AS `ID`,`d`.`NOME` AS `NOME`,`d`.`CPF` AS `CPF`,`d`.`CNPJ` AS `CNPJ`,`d`.`INSCRICAO_ESTADUAL` AS `INSCRICAO_ESTADUAL`,`d`.`CODIGO_MUNICIPIO` AS `CODIGO_MUNICIPIO`,`d`.`SUFRAMA` AS `SUFRAMA`,`d`.`LOGRADOURO` AS `LOGRADOURO`,`d`.`NUMERO` AS `NUMERO`,`d`.`COMPLEMENTO` AS `COMPLEMENTO`,`d`.`BAIRRO` AS `BAIRRO` from (`fenix`.`nfe_cabecalho` `nfe` join `fenix`.`nfe_destinatario` `d` on((`d`.`ID_NFE_CABECALHO` = `nfe`.`ID`))) where (`nfe`.`ID_CLIENTE` is not null);

-- -----------------------------------------------------
-- View `fenix`.`view_sped_nfe_detalhe`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fenix`.`view_sped_nfe_detalhe`;
USE `fenix`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `fenix`.`view_sped_nfe_detalhe` AS select `nfed`.`ID` AS `ID`,`nfed`.`ID_NFE_CABECALHO` AS `ID_NFE_CABECALHO`,`nfed`.`ID_PRODUTO` AS `ID_PRODUTO`,`nfed`.`NUMERO_ITEM` AS `NUMERO_ITEM`,`nfed`.`CODIGO_PRODUTO` AS `CODIGO_PRODUTO`,`nfed`.`GTIN` AS `GTIN`,`nfed`.`NOME_PRODUTO` AS `NOME_PRODUTO`,`nfed`.`NCM` AS `NCM`,`nfed`.`NVE` AS `NVE`,`nfed`.`CEST` AS `CEST`,`nfed`.`INDICADOR_ESCALA_RELEVANTE` AS `INDICADOR_ESCALA_RELEVANTE`,`nfed`.`CNPJ_FABRICANTE` AS `CNPJ_FABRICANTE`,`nfed`.`CODIGO_BENEFICIO_FISCAL` AS `CODIGO_BENEFICIO_FISCAL`,`nfed`.`EX_TIPI` AS `EX_TIPI`,`nfed`.`CFOP` AS `CFOP`,`nfed`.`UNIDADE_COMERCIAL` AS `UNIDADE_COMERCIAL`,`nfed`.`QUANTIDADE_COMERCIAL` AS `QUANTIDADE_COMERCIAL`,`nfed`.`NUMERO_PEDIDO_COMPRA` AS `NUMERO_PEDIDO_COMPRA`,`nfed`.`ITEM_PEDIDO_COMPRA` AS `ITEM_PEDIDO_COMPRA`,`nfed`.`NUMERO_FCI` AS `NUMERO_FCI`,`nfed`.`NUMERO_RECOPI` AS `NUMERO_RECOPI`,`nfed`.`VALOR_UNITARIO_COMERCIAL` AS `VALOR_UNITARIO_COMERCIAL`,`nfed`.`VALOR_BRUTO_PRODUTO` AS `VALOR_BRUTO_PRODUTO`,`nfed`.`GTIN_UNIDADE_TRIBUTAVEL` AS `GTIN_UNIDADE_TRIBUTAVEL`,`nfed`.`UNIDADE_TRIBUTAVEL` AS `UNIDADE_TRIBUTAVEL`,`nfed`.`QUANTIDADE_TRIBUTAVEL` AS `QUANTIDADE_TRIBUTAVEL`,`nfed`.`VALOR_UNITARIO_TRIBUTAVEL` AS `VALOR_UNITARIO_TRIBUTAVEL`,`nfed`.`VALOR_FRETE` AS `VALOR_FRETE`,`nfed`.`VALOR_SEGURO` AS `VALOR_SEGURO`,`nfed`.`VALOR_DESCONTO` AS `VALOR_DESCONTO`,`nfed`.`VALOR_OUTRAS_DESPESAS` AS `VALOR_OUTRAS_DESPESAS`,`nfed`.`ENTRA_TOTAL` AS `ENTRA_TOTAL`,`nfed`.`VALOR_TOTAL_TRIBUTOS` AS `VALOR_TOTAL_TRIBUTOS`,`nfed`.`PERCENTUAL_DEVOLVIDO` AS `PERCENTUAL_DEVOLVIDO`,`nfed`.`VALOR_IPI_DEVOLVIDO` AS `VALOR_IPI_DEVOLVIDO`,`nfed`.`INFORMACOES_ADICIONAIS` AS `INFORMACOES_ADICIONAIS`,`nfed`.`VALOR_SUBTOTAL` AS `VALOR_SUBTOTAL`,`nfed`.`VALOR_TOTAL` AS `VALOR_TOTAL`,`nfec`.`ID_TRIBUT_OPERACAO_FISCAL` AS `ID_TRIBUT_OPERACAO_FISCAL`,`p`.`ID_PRODUTO_UNIDADE` AS `ID_PRODUTO_UNIDADE`,`cofins`.`CST_COFINS` AS `CST_COFINS`,`cofins`.`QUANTIDADE_VENDIDA` AS `QUANTIDADE_VENDIDA_COFINS`,`cofins`.`BASE_CALCULO_COFINS` AS `BASE_CALCULO_COFINS`,`cofins`.`ALIQUOTA_COFINS_PERCENTUAL` AS `ALIQUOTA_COFINS_PERCENTUAL`,`cofins`.`ALIQUOTA_COFINS_REAIS` AS `ALIQUOTA_COFINS_REAIS`,`cofins`.`VALOR_COFINS` AS `VALOR_COFINS`,`icms`.`ORIGEM_MERCADORIA` AS `ORIGEM_MERCADORIA`,`icms`.`CST_ICMS` AS `CST_ICMS`,`icms`.`CSOSN` AS `CSOSN`,`icms`.`MODALIDADE_BC_ICMS` AS `MODALIDADE_BC_ICMS`,`icms`.`PERCENTUAL_REDUCAO_BC_ICMS` AS `PERCENTUAL_REDUCAO_BC_ICMS`,`icms`.`VALOR_BC_ICMS` AS `VALOR_BC_ICMS`,`icms`.`ALIQUOTA_ICMS` AS `ALIQUOTA_ICMS`,`icms`.`VALOR_ICMS` AS `VALOR_ICMS`,`icms`.`MOTIVO_DESONERACAO_ICMS` AS `MOTIVO_DESONERACAO_ICMS`,`icms`.`MODALIDADE_BC_ICMS_ST` AS `MODALIDADE_BC_ICMS_ST`,`icms`.`PERCENTUAL_MVA_ICMS_ST` AS `PERCENTUAL_MVA_ICMS_ST`,`icms`.`PERCENTUAL_REDUCAO_BC_ICMS_ST` AS `PERCENTUAL_REDUCAO_BC_ICMS_ST`,`icms`.`VALOR_BASE_CALCULO_ICMS_ST` AS `VALOR_BASE_CALCULO_ICMS_ST`,`icms`.`ALIQUOTA_ICMS_ST` AS `ALIQUOTA_ICMS_ST`,`icms`.`VALOR_ICMS_ST` AS `VALOR_ICMS_ST`,`icms`.`VALOR_BC_ICMS_ST_RETIDO` AS `VALOR_BC_ICMS_ST_RETIDO`,`icms`.`VALOR_ICMS_ST_RETIDO` AS `VALOR_ICMS_ST_RETIDO`,`icms`.`VALOR_BC_ICMS_ST_DESTINO` AS `VALOR_BC_ICMS_ST_DESTINO`,`icms`.`VALOR_ICMS_ST_DESTINO` AS `VALOR_ICMS_ST_DESTINO`,`icms`.`ALIQUOTA_CREDITO_ICMS_SN` AS `ALIQUOTA_CREDITO_ICMS_SN`,`icms`.`VALOR_CREDITO_ICMS_SN` AS `VALOR_CREDITO_ICMS_SN`,`icms`.`PERCENTUAL_BC_OPERACAO_PROPRIA` AS `PERCENTUAL_BC_OPERACAO_PROPRIA`,`icms`.`UF_ST` AS `UF_ST`,`ii`.`VALOR_BC_II` AS `VALOR_BC_II`,`ii`.`VALOR_DESPESAS_ADUANEIRAS` AS `VALOR_DESPESAS_ADUANEIRAS`,`ii`.`VALOR_IMPOSTO_IMPORTACAO` AS `VALOR_IMPOSTO_IMPORTACAO`,`ii`.`VALOR_IOF` AS `VALOR_IOF`,`ipi`.`CNPJ_PRODUTOR` AS `CNPJ_PRODUTOR`,`ipi`.`CODIGO_SELO_IPI` AS `CODIGO_SELO_IPI`,`ipi`.`QUANTIDADE_SELO_IPI` AS `QUANTIDADE_SELO_IPI`,`ipi`.`ENQUADRAMENTO_LEGAL_IPI` AS `ENQUADRAMENTO_LEGAL_IPI`,`ipi`.`CST_IPI` AS `CST_IPI`,`ipi`.`VALOR_BASE_CALCULO_IPI` AS `VALOR_BASE_CALCULO_IPI`,`ipi`.`ALIQUOTA_IPI` AS `ALIQUOTA_IPI`,`ipi`.`QUANTIDADE_UNIDADE_TRIBUTAVEL` AS `QUANTIDADE_UNIDADE_TRIBUTAVEL`,`ipi`.`VALOR_UNIDADE_TRIBUTAVEL` AS `VALOR_UNIDADE_TRIBUTAVEL`,`ipi`.`VALOR_IPI` AS `VALOR_IPI`,`issqn`.`BASE_CALCULO_ISSQN` AS `BASE_CALCULO_ISSQN`,`issqn`.`ALIQUOTA_ISSQN` AS `ALIQUOTA_ISSQN`,`issqn`.`VALOR_ISSQN` AS `VALOR_ISSQN`,`issqn`.`MUNICIPIO_ISSQN` AS `MUNICIPIO_ISSQN`,`issqn`.`ITEM_LISTA_SERVICOS` AS `ITEM_LISTA_SERVICOS`,`pis`.`CST_PIS` AS `CST_PIS`,`pis`.`QUANTIDADE_VENDIDA` AS `QUANTIDADE_VENDIDA_PIS`,`pis`.`VALOR_BASE_CALCULO_PIS` AS `VALOR_BASE_CALCULO_PIS`,`pis`.`ALIQUOTA_PIS_PERCENTUAL` AS `ALIQUOTA_PIS_PERCENTUAL`,`pis`.`ALIQUOTA_PIS_REAIS` AS `ALIQUOTA_PIS_REAIS`,`pis`.`VALOR_PIS` AS `VALOR_PIS` from ((((((((`fenix`.`nfe_detalhe` `nfed` left join `fenix`.`nfe_detalhe_imposto_cofins` `cofins` on((`cofins`.`ID_NFE_DETALHE` = `nfed`.`ID`))) left join `fenix`.`nfe_detalhe_imposto_icms` `icms` on((`icms`.`ID_NFE_DETALHE` = `nfed`.`ID`))) left join `fenix`.`nfe_detalhe_imposto_ii` `ii` on((`ii`.`ID_NFE_DETALHE` = `nfed`.`ID`))) left join `fenix`.`nfe_detalhe_imposto_ipi` `ipi` on((`ipi`.`ID_NFE_DETALHE` = `nfed`.`ID`))) left join `fenix`.`nfe_detalhe_imposto_issqn` `issqn` on((`issqn`.`ID_NFE_DETALHE` = `nfed`.`ID`))) left join `fenix`.`nfe_detalhe_imposto_pis` `pis` on((`pis`.`ID_NFE_DETALHE` = `nfed`.`ID`))) left join `fenix`.`produto` `p` on((`nfed`.`ID_PRODUTO` = `p`.`ID`))) left join `fenix`.`nfe_cabecalho` `nfec` on((`nfed`.`ID_NFE_CABECALHO` = `nfec`.`ID`)));

-- -----------------------------------------------------
-- View `fenix`.`view_sped_nfe_emitente`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fenix`.`view_sped_nfe_emitente`;
USE `fenix`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `fenix`.`view_sped_nfe_emitente` AS select distinct `nfe`.`ID_FORNECEDOR` AS `ID`,`e`.`NOME` AS `NOME`,`e`.`CPF` AS `CPF`,`e`.`CNPJ` AS `CNPJ`,`e`.`INSCRICAO_ESTADUAL` AS `INSCRICAO_ESTADUAL`,`e`.`CODIGO_MUNICIPIO` AS `CODIGO_MUNICIPIO`,`e`.`LOGRADOURO` AS `LOGRADOURO`,`e`.`NUMERO` AS `NUMERO`,`e`.`COMPLEMENTO` AS `COMPLEMENTO`,`e`.`BAIRRO` AS `BAIRRO` from (`fenix`.`nfe_cabecalho` `nfe` join `fenix`.`nfe_emitente` `e` on((`e`.`ID_NFE_CABECALHO` = `nfe`.`ID`))) where (`nfe`.`ID_FORNECEDOR` is not null);

-- -----------------------------------------------------
-- View `fenix`.`view_sped_nfe_item`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fenix`.`view_sped_nfe_item`;
USE `fenix`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `fenix`.`view_sped_nfe_item` AS select distinct `p`.`ID` AS `ID`,`p`.`NOME` AS `NOME`,`p`.`GTIN` AS `GTIN`,`p`.`ID_PRODUTO_UNIDADE` AS `ID_PRODUTO_UNIDADE`,`u`.`SIGLA` AS `SIGLA`,`p`.`CODIGO_NCM` AS `CODIGO_NCM` from ((`fenix`.`produto` `p` join `fenix`.`nfe_detalhe` `nfed` on((`nfed`.`ID_PRODUTO` = `p`.`ID`))) join `fenix`.`produto_unidade` `u` on((`p`.`ID_PRODUTO_UNIDADE` = `u`.`ID`)));

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
