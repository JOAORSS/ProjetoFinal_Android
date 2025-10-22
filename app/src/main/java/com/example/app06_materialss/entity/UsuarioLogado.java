package com.example.app06_materialss.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabela_usuario_logado")
public class UsuarioLogado {

    @PrimaryKey
    private int id;
    private String nome;
    private String email;
    private String endereco;
    private String dataCadastro;

    public UsuarioLogado(int id, String nome, String email, String endereco, String dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.endereco = endereco;
        this.dataCadastro = dataCadastro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
