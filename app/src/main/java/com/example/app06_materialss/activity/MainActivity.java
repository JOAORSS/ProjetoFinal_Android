package com.example.app06_materialss.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app06_materialss.R;
import com.example.app06_materialss.controller.ConexaoController;
import com.example.app06_materialss.recycler.PecaAdapter;

import java.util.ArrayList;
import java.util.List;

import br.com.autopeca360.dominio.Peca;

public class MainActivity extends AppAutopecaActivity {

    public ConexaoController ccont;
    private boolean isConnectionFinished = false;

    private RecyclerView recyclerPeca;
    private PecaAdapter pecaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        splashScreen.setKeepOnScreenCondition(() -> !isConnectionFinished);
        conectaServidor();

        setContentView(R.layout.activity_main);

        VinculaComponetes();
        ConfiguraRecyclerPeca();
        CarregaListaPecas();

    }

    private void VinculaComponetes() {
        recyclerPeca = findViewById(R.id.recyclerView_peca);
    }

    private void conectaServidor() {
        ccont = ConexaoController.getInstance();
        ccont.executar(
                () -> runOnUiThread(() -> isConnectionFinished = true),
                () -> runOnUiThread(() -> {
                    Toast.makeText(this, "Falha na conexão com o servidor!", Toast.LENGTH_LONG).show();
                    isConnectionFinished = true;
                })
        );
    }

    private void CarregaListaPecas() {
        executarComConexao(
                () -> ccont.pecaLista().get(),
                listaDePecas -> {
                    if (listaDePecas != null) {
                        pecaAdapter.atualizarLista(listaDePecas);
                    } else {
                        Toast.makeText(this, "A lista de peças veio vazia ou nula.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void ConfiguraRecyclerPeca() {
        List<Peca> lista = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerPeca.setLayoutManager(gridLayoutManager);
        pecaAdapter = new PecaAdapter(lista);
        recyclerPeca.setAdapter(pecaAdapter);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}