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

    private static ConexaoController instance;

    private Socket cliente;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /* O executador é usado para realizar operações em threads separadas */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    /* Estado da conexão. Inicia como desconectado */
    private volatile EstadoConexao estado = EstadoConexao.DESCONECTADO;

    private ConexaoController() {}

    /** Retorna a instância única da classe ConexaoController.
     * @static é possivel chamar mesmo sem o objeto criado.
     * @synchronized só uma thread pode acessar ao mesmo tempo.*/
    public static synchronized ConexaoController getInstance() {
        if (instance == null) {
            instance = new ConexaoController();
        }
        return instance;
    }

    /* Estados possíveis da conexão */
    public enum EstadoConexao {
        DESCONECTADO,
        CONECTANDO,
        CONECTADO,
        ERRO
    }

    /** @Runnable é uma função que será recebida pelo méthodo  */
    public void executar(Runnable onSuccess, Runnable onError) {

        /* Se ja estiver conectado ele apenas irá rodar o sucesso*/
        if (estado == EstadoConexao.CONECTADO) {
            if (onSuccess != null) onSuccess.run();
            return;
        }
        if (estado == EstadoConexao.CONECTANDO) { return; }
        estado = EstadoConexao.CONECTANDO;

        /* Aqui é onde a conexão é feita, se conectar o estado vira CONECTADO
           e roda o sucesso, se não roda o erro. */
        executor.execute(() -> {
            try {
                cliente = new Socket(BuildConfig.SERVER_HOST_IP, 12345); // ip e porta do servidor
                out = new ObjectOutputStream(cliente.getOutputStream());
                in = new ObjectInputStream(cliente.getInputStream());

                estado = EstadoConexao.CONECTADO;
                if (onSuccess != null) onSuccess.run(); // roda o sucesso
            } catch (IOException e) {
                e.printStackTrace();
                estado = EstadoConexao.ERRO;
                if (onError != null) onError.run(); // roda erro
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

    /* Nome do méthodo chamado nas telas*/
    public Future<Usuario> usuarioLogin(Usuario user){
        /* Nome da função chamada dentro do executor
           normalmente o nome méthodo com o verbo no infinitivo*/
        Callable<Usuario> usuarioLogar = () -> {
            if (estado != EstadoConexao.CONECTADO) {
                throw new IllegalStateException("Cliente não está conectado.");
            }
            out.writeObject("UsuarioLogin");
            out.flush(); // limpa e envia o writeObject
            in.readObject();
            out.writeObject(user);
            out.flush(); // limpa e envia o writeObject
            return (Usuario) in.readObject();
        };
        /* Onde a operação vai ser excutada */
        return executor.submit(usuarioLogar);
    }

    public Future<Usuario> usuarioCadastro(Usuario user){
        Callable<Usuario> usuarioLogar = () -> {
            if (estado != EstadoConexao.CONECTADO) {
                throw new IllegalStateException("Cliente não está conectado.");
            }
            out.writeObject("UsuarioCadastro");
            out.flush();
            in.readObject();
            out.writeObject(user);
            out.flush();
            return (Usuario) in.readObject();
        };
        return executor.submit(usuarioLogar);
    }

    public Future<Boolean> usuarioExcluir(Usuario user) {
        Callable<Boolean> excluirConta = () -> {
            if (estado != EstadoConexao.CONECTADO) {
                throw new IllegalStateException("Cliente não está conectado.");
            }
            out.writeObject("UsuarioExcluir");
            out.flush();
            in.readObject();
            out.writeObject(user);
            out.flush();
            return (boolean) in.readObject();
        };
        return executor.submit(excluirConta);
    }

    public Future<Boolean> usuarioEditar(Usuario user) {
        Callable<Boolean> usaurioEdit = () -> {
            if (estado != EstadoConexao.CONECTADO) {
                throw new IllegalStateException("Cliente não está conectado.");
            }
            out.writeObject("UsuarioEditar");
            out.flush();
            in.readObject();
            out.writeObject(user);
            out.flush();
            return (boolean) in.readObject();
        };
        return executor.submit(usaurioEdit);
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

    private void trataErro(IOException e) {
        
    }
    
}
