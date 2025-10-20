package com.example.app06_materialss.controller;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.app06_materialss.DAO.CarrinhoDao;
import com.example.app06_materialss.entity.PecaCarrinho;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Database(entities = {PecaCarrinho.class}, version = 1)
public abstract class LocalController extends RoomDatabase {

    private static volatile LocalController INSTANCE;
    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    public abstract CarrinhoDao carrinhoDao();

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

}