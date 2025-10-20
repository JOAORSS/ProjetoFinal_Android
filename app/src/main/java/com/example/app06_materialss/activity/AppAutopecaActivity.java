package com.example.app06_materialss.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app06_materialss.controller.ConexaoController;
import com.example.app06_materialss.controller.LocalController;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class AppAutopecaActivity extends AppCompatActivity {

    public ConexaoController ccont;
    public LocalController lcont;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ccont = ConexaoController.getInstance();
        //lcont = LocalController.getInstance(this);
    }

    /**
     * Executa uma operação de rede de forma segura, genérica e assíncrona.
     *
     * @param operacaoDeRede A função que executa a chamada de rede e retorna um resultado. (Ex: () -> ccont.pecaLista().get())
     * @param noSucesso A função que será executada na UI Thread com o resultado da operação. (Ex: lista -> meuAdapter.atualizar(lista))
     * @param <T> O tipo de dado que a operação de rede retornará (ex: List<Peca>, Usuario).
     */
    public <T> void executarComConexao(Callable<Future<T>> operacaoDeRede, Consumer<T> noSucesso) {
        ccont.executar(
                () -> new Thread(() -> {
                        try {
                            final T resultado = operacaoDeRede.call().get();
                            runOnUiThread(() -> noSucesso.accept(resultado));
                        } catch (Exception e) {
                            Log.e("BaseActivity", "Erro executando operação de rede", e);
                            runOnUiThread(() -> Toast.makeText(this, "Erro na operação.", Toast.LENGTH_SHORT).show());
                        }
                    }).start(),
                () -> runOnUiThread(() -> Toast.makeText(this, "Falha na conexão.", Toast.LENGTH_LONG).show())
        );
    }

    protected <T> void executarLoacal(Callable<Future<T>> operacaoDeBanco, Consumer<T> noSucesso) {
        new Thread(() -> {
            try {
                final T resultado = operacaoDeBanco.call().get();
                runOnUiThread(() -> noSucesso.accept(resultado));
            } catch (Exception e) {
                Log.e("BaseActivity", "Erro ao executar no banco", e);
                runOnUiThread(() -> Toast.makeText(this, "Erro no banco de dados.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
