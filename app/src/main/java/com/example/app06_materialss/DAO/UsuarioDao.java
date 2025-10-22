package com.example.app06_materialss.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.app06_materialss.entity.UsuarioLogado;

@Dao
public interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(UsuarioLogado usuario);

    @Query("SELECT * FROM tabela_usuario_logado LIMIT 1")
    UsuarioLogado getUsuario();

    @Query("DELETE FROM tabela_usuario_logado")
    void limparUsuario();
}
