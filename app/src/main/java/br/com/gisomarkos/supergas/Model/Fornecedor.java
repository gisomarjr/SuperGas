package br.com.gisomarkos.supergas.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by User on 02/08/2015.
 */
public class Fornecedor implements Serializable {

    public int id;
    @SerializedName("nome")
    public String nome;
    public String cnpj;
    public String nomeProprietario;
    public String cpf;
    public Endereco endereco;

}
