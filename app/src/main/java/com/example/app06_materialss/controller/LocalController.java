package com.example.app06_materialss.controller;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.app06_materialss.DAO.CarrinhoDao;
import com.example.app06_materialss.DAO.UsuarioDao;
import com.example.app06_materialss.entity.PecaCarrinho;
import com.example.app06_materialss.entity.UsuarioLogado;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import br.com.autopeca360.dominio.Usuario;

@Database(entities = {PecaCarrinho.class, UsuarioLogado.class}, version = 2)
public abstract class LocalController extends RoomDatabase {

    private static volatile LocalController INSTANCE;
    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    public abstract CarrinhoDao carrinhoDao();
    public abstract UsuarioDao   usuarioDao();

    public static LocalController getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (LocalController.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    LocalController.class, "autopeca360-db")
                                    .build();
                }
            }
        }
        return INSTANCE;
    }

    public Future<List<PecaCarrinho>> listaItemCarrinho() {
        Callable<List<PecaCarrinho>> tarefaLeitura = () ->
                carrinhoDao().getTodosItens();
        return dbExecutor.submit(tarefaLeitura);
    }

    public Future<Void> inserirItemCarrinho(PecaCarrinho item) {
        Callable<Void> tarefaEscrita = () -> {
            carrinhoDao().inserir(item);
            return null;
        };
        return dbExecutor.submit(tarefaEscrita);
    }

    public Future<PecaCarrinho> buscaItemCarrinhoPorId(int pecaId) {
        return dbExecutor.submit(() -> carrinhoDao().getItemPorId(pecaId));
    }

    public Future<Void> atualizarItemCarrinho(PecaCarrinho item) {
        return dbExecutor.submit(() -> {
            carrinhoDao().atualizar(item);
            return null;
        });
    }

    public Future<Void> deletarItemCarrinho(PecaCarrinho item) {
        Callable<Void> tarefaDelecao = () -> {
            carrinhoDao().deletarItem(item);
            return null;
        };
        return dbExecutor.submit(tarefaDelecao);
    }

    public Future<UsuarioLogado> getUsuarioLogado() {
        return dbExecutor.submit(() -> usuarioDao().getUsuario());
    }

    public Future<Void> inserirUsuarioLogado(UsuarioLogado usuario) {
        return dbExecutor.submit(() -> {
            usuarioDao().inserir(usuario);
            return null;
        });
    }

    public Future<Void> deletarUsuarioLogado() {
        return dbExecutor.submit(() -> {
            usuarioDao().limparUsuario();
            return null;
        });
    }

}