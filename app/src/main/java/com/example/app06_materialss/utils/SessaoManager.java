package com.example.app06_materialss.utils;

import com.example.app06_materialss.entity.UsuarioLogado;

public class SessaoManager {

    private static volatile SessaoManager instance;
    private UsuarioLogado usuarioLogado;

    private SessaoManager() {}

    public static SessaoManager getInstance() {
        if (instance == null) {
            synchronized (SessaoManager.class) {
                if (instance == null) {
                    instance = new SessaoManager();
                }
            }
        }
        return instance;
    }

    public void iniciarSessao(UsuarioLogado usuario) {
        this.usuarioLogado = usuario;
    }

    public void encerrarSessao() {
        this.usuarioLogado = null;
    }

    public boolean isLogado() {
        return this.usuarioLogado != null;
    }

    public UsuarioLogado getUsuario() {
        return this.usuarioLogado;
    }
}
