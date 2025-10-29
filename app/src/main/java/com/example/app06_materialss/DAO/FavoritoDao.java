package com.example.app06_materialss.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.app06_materialss.entity.PecaFavorita;

import java.util.List;

@Dao
public interface FavoritoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(PecaFavorita peca);

    @Delete
    void deletar(PecaFavorita peca);

    @Query("SELECT * FROM tabela_favoritos")
    List<PecaFavorita> getTodosFavoritos();

    @Query("SELECT * FROM tabela_favoritos WHERE pecaId = :pecaId LIMIT 1")
    PecaFavorita buscarPorId(int pecaId);

    @Query("SELECT * FROM tabela_favoritos WHERE nome LIKE :query")
    List<PecaFavorita> buscarPorNome(String query);
}
