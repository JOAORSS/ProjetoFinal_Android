package com.example.app06_materialss.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.app06_materialss.entity.PecaCarrinho;

import java.util.List;

@Dao
public interface CarrinhoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(PecaCarrinho item);

    /**
     * Este é o método que o seu LocalController.getTodosItens() vai chamar.
     * Note que ele retorna a Lista diretamente.
     */
    @Query("SELECT * FROM tabela_carrinho") // Use o nome da sua tabela
    List<PecaCarrinho> getTodosItens();

    /**
     * Este é o método que o seu LocalController.limparCarrinho() pode chamar.
     */
    @Query("DELETE FROM tabela_carrinho")
    void limparCarrinho();

    @Delete
    void deletarItem(PecaCarrinho item);

    // Você pode adicionar outros métodos que precisar
    // Ex: @Query("SELECT COUNT(pecaId) FROM tabela_carrinho")
    // int getContagemDeItens();
}