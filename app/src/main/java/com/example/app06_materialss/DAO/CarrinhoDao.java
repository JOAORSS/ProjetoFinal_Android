package com.example.app06_materialss.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.app06_materialss.entity.PecaCarrinho;

import java.util.List;
import java.util.concurrent.Future;

@Dao
public interface CarrinhoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(PecaCarrinho item);

    @Delete
    void deletar(PecaCarrinho item);

    @Query("SELECT * FROM tabela_carrinho")
    List<PecaCarrinho> getTodosItens();

    @Query("DELETE FROM tabela_carrinho")
    void limparCarrinho();

    @Query("SELECT COUNT(pecaId) FROM tabela_carrinho")
    int getContagemDeItens(); // SÍNCRONA
}