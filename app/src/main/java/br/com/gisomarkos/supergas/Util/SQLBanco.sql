create database supergas;

CREATE TABLE `supergas`.`endereco` (
  `idendereco` INT NOT NULL COMMENT '',
  `latitude` VARCHAR(15) NOT NULL COMMENT '',
  `longitude` VARCHAR(15) NOT NULL COMMENT '',
  `rua` VARCHAR(45) NOT NULL COMMENT '',
  `numero` VARCHAR(8) NOT NULL COMMENT '',
  `complemento` VARCHAR(45) NULL COMMENT '',
  `bairro` VARCHAR(45) NOT NULL COMMENT '',
  `cidade` VARCHAR(45) NOT NULL COMMENT '',
  `estado` VARCHAR(45) NOT NULL COMMENT '',
  `cep` VARCHAR(10) NULL COMMENT '',
  PRIMARY KEY (`idendereco`)  COMMENT '');

ALTER TABLE `supergas`.`endereco`
CHANGE COLUMN `idendereco` `idendereco` INT(11) NOT NULL AUTO_INCREMENT COMMENT '' ;

CREATE TABLE `supergas`.`cliente` (
  `email` VARCHAR(45) NOT NULL COMMENT '',
  `nome` VARCHAR(45) NOT NULL COMMENT '',
  `telefone` VARCHAR(45) NOT NULL COMMENT '',
  `idendereco` INT NOT NULL COMMENT '',
  PRIMARY KEY (`email`)  COMMENT '',
  INDEX `fkclienteendereceo_idx` (`idendereco` ASC)  COMMENT '',
  CONSTRAINT `fkclienteendereceo`
    FOREIGN KEY (`idendereco`)
    REFERENCES `supergas`.`endereco` (`idendereco`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE `supergas`.`fornecedor` (
  `idfornecedor` INT NOT NULL COMMENT '',
  `cnpj` VARCHAR(20) NOT NULL COMMENT '',
  `nomefornecedor` VARCHAR(45) NOT NULL COMMENT '',
  `cpfproprietario` VARCHAR(15) NOT NULL COMMENT '',
  `nomeproprietario` VARCHAR(45) NULL COMMENT '',
  `idendereco` INT NULL COMMENT '',
  PRIMARY KEY (`idfornecedor`)  COMMENT '',
  INDEX `fkfornecedorendereco_idx` (`idendereco` ASC)  COMMENT '',
  CONSTRAINT `fkfornecedorendereco`
    FOREIGN KEY (`idendereco`)
    REFERENCES `supergas`.`endereco` (`idendereco`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE `supergas`.`pedido` (
  `idpedido` INT NOT NULL COMMENT '',
  `clienteemail` VARCHAR(45) NOT NULL COMMENT '',
  `idfornecedor` INT NOT NULL COMMENT '',
  `quantidade` INT NULL COMMENT '',
  `statuspedido` VARCHAR(45) NULL COMMENT '',
  PRIMARY KEY (`idpedido`)  COMMENT '',
  INDEX `fkpedidocliente_idx` (`clienteemail` ASC)  COMMENT '',
  INDEX `fkpedidofornecedor_idx` (`idfornecedor` ASC)  COMMENT '',
  CONSTRAINT `fkpedidocliente`
    FOREIGN KEY (`clienteemail`)
    REFERENCES `supergas`.`cliente` (`email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fkpedidofornecedor`
    FOREIGN KEY (`idfornecedor`)
    REFERENCES `supergas`.`fornecedor` (`idfornecedor`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);