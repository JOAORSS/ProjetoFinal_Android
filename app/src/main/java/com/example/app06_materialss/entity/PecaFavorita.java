package com.example.app06_materialss.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabela_favoritos")
public class PecaFavorita {

    @PrimaryKey
    private int pecaId;
    private String nome;
    private double preco;
    private byte[] imagem;

    public PecaFavorita(int pecaId, String nome, double preco, byte[] imagem) {
        this.pecaId = pecaId;
        this.nome = nome;
        this.preco = preco;
        this.imagem = imagem;
    }

    // Getters
    public int getPecaId() { return pecaId; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public byte[] getImagem() { return imagem; }
}
