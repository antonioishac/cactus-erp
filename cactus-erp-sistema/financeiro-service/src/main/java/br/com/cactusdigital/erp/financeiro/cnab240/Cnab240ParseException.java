package br.com.cactusdigital.erp.financeiro.cnab240;

/** Arquivo não é um CNAB240 reconhecível (nenhum segmento T encontrado) ou uma linha tem
 * menos de 240 caracteres. */
public class Cnab240ParseException extends RuntimeException {

    public Cnab240ParseException(String message) {
        super(message);
    }
}
