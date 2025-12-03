package com.example.app06_materialss.activity;

import android.app.Application;
import com.example.app06_materialss.controller.ConexaoController;

public class Principal extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Criando a instancia da conexão nas base */
        ConexaoController.getInstance();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        /* Fehcando a instancia da conexão */
        ConexaoController.getInstance().desconectar();
    }
}


