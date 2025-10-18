package com.example.app06_materialss.activity;

import android.app.Application;

import com.example.app06_materialss.controller.ConexaoController;

public class Principal extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ConexaoController.getInstance();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ConexaoController.getInstance().desconectar();
    }
}
