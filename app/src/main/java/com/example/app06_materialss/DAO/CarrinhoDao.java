package com.example.app06_materialss.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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

    @Query("SELECT * FROM tabela_carrinho WHERE pecaId = :pecaId LIMIT 1")
    PecaCarrinho getItemPorId(int pecaId);

    @Update
    void atualizar(PecaCarrinho item);
}