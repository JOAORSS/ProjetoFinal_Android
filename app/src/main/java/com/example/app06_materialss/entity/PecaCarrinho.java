package com.example.app06_materialss.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabela_carrinho")
public class PecaCarrinho {

    @PrimaryKey
    private int pecaId;

    private String nome;
    private double preco;
    private byte[] imagem;
    private int quantidade;

    public PecaCarrinho(int pecaId, String nome, double preco, byte[] imagem, int quantidade) {
        this.pecaId = pecaId;
        this.nome = nome;
        this.preco = preco;
        this.imagem = imagem;
        this.quantidade = quantidade;
    }

    public int getPecaId() {
        return pecaId;
    }

    public void setPecaId(int pecaId) {
        this.pecaId = pecaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}


