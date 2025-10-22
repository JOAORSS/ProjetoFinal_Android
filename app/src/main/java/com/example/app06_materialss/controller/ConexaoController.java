
package com.example.app06_materialss.controller;
import com.example.app06_materialss.BuildConfig;

import br.com.autopeca360.dominio.Fornecedor;
import br.com.autopeca360.dominio.Peca;
import br.com.autopeca360.dominio.Usuario;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConexaoController {

    public enum EstadoConexao {
        DESCONECTADO,
        CONECTANDO,
        CONECTADO,
        ERRO
    }

    private static ConexaoController instance;

    private ConexaoController() {}

    public static synchronized ConexaoController getInstance() {
        if (instance == null) {
            instance = new ConexaoController();
        }
        return instance;
    }

    private Socket cliente;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Usuario userLogado;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private volatile EstadoConexao estado = EstadoConexao.DESCONECTADO;

    public Usuario getUserLogado() {
        return userLogado;
    }

    public void setUserLogado(Usuario userLogado) {
        this.userLogado = userLogado;
    }

    public EstadoConexao getEstado() {
        return estado;
    }

    public void executar(Runnable onSuccess, Runnable onError) {
        if (estado == EstadoConexao.CONECTADO) {
            if (onSuccess != null) onSuccess.run();
            return;
        }

        if (estado == EstadoConexao.CONECTANDO) {
            return;
        }

        estado = EstadoConexao.CONECTANDO;

        executor.execute(() -> {
            try {
                String ipServidor = BuildConfig.SERVER_HOST_IP;

                cliente = new Socket(ipServidor, 12345);
                out = new ObjectOutputStream(cliente.getOutputStream());
                in = new ObjectInputStream(cliente.getInputStream());

                estado = EstadoConexao.CONECTADO;
                if (onSuccess != null) onSuccess.run();

            } catch (IOException e) {
                e.printStackTrace();
                estado = EstadoConexao.ERRO;
                if (onError != null) onError.run();
            }
        });
    }


    public void desconectar() {
        estado = EstadoConexao.DESCONECTADO;
        executor.execute(() -> {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (cliente != null) cliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Future<List<Peca>> pecaLista() {
        Callable<List<Peca>> pecaListar = () -> {
            if (estado != EstadoConexao.CONECTADO) {
                throw new IllegalStateException("Cliente não está conectado.");
            }
            out.writeObject("PecaLista");
            out.flush();
            in.readObject();
            return (List<Peca>) in.readObject();
        };
        return executor.submit(pecaListar);
    }

    public Future<Usuario> usuarioLogin(Usuario user){
        Callable<Usuario> usuarioLogar = () -> {
            if (estado != EstadoConexao.CONECTADO) {
                throw new IllegalStateException("Cliente não está conectado.");
            }
            out.writeObject("UsuarioLogin");
            out.flush();
            in.readObject();
            out.writeObject(user);
            out.flush();
            return (Usuario) in.readObject();
        };
        return executor.submit(usuarioLogar);
    }

    public Future<Peca> pecaBusca(int id){
        Callable<Peca> pecaBuscar = () -> {
            if (estado != EstadoConexao.CONECTADO) {
                throw new IllegalStateException("Cliente não está conectado.");
            }
            out.writeObject("PecaBusca");
            out.flush();
            in.readObject();
            out.writeObject(id);
            out.flush();
            return (Peca) in.readObject();
        };
        return executor.submit(pecaBuscar);
    }

    public boolean pecaInserir(Peca p){
        try {
            out.writeObject("PecaInserir");
            in.readObject();
            out.writeObject(p);
            return (boolean) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean pecaEditar(Peca p){
        try {
            out.writeObject("PecaEditar");
            in.readObject();
            out.writeObject(p);
            return (boolean) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean pecaExcluir(Peca p){
        try {
            out.writeObject("PecaExcluir");
            in.readObject();
            out.writeObject(p);
            return (boolean) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    

    
    public List<Fornecedor> fornecedorLista(){
        try {
            out.writeObject("FornecedorLista");
            in.readObject();
            return (List<Fornecedor>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean fornecedorInserir(Fornecedor f){
        try {
            out.writeObject("FornecedorInserir");
            in.readObject();
            out.writeObject(f);
            return (boolean) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean fornecedorEditar(Fornecedor f){
        try {
            out.writeObject("FornecedorEditar");
            in.readObject();
            out.writeObject(f);
            return (boolean) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean fornecedorExcluir(Fornecedor f){
        try {
            out.writeObject("FornecedorExcluir");
            in.readObject();
            out.writeObject(f);
            return (boolean) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }        
       
    public List<Usuario> usuarioLista() {
            try {
                out.writeObject("usuarioLista");
                System.out.println(in.readObject());
                List<Usuario> lista = (List<Usuario>) in.readObject();
                return lista;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
    }
    
    public boolean usuarioExcluir(Usuario u){
        try {
            out.writeObject("UsuarioExcluir");
            in.readObject();
            out.writeObject(u);
            return (boolean) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }   

    private void trataErro(IOException e) {
        
    }
    
}
