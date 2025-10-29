package com.example.app06_materialss.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app06_materialss.R;
import com.example.app06_materialss.adapter.CarrinhoAdapter;
import com.example.app06_materialss.controller.LocalController;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class CompraConcluidaActivity extends AppAutopecaActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private Button btnVoltar;
    private CarrinhoAdapter carrinhoAdapter;
    private LocalController lcont;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_compra_concluida);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.compraConcluida_main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lcont = LocalController.getInstance(this);

        vinculaComponentes();
        configuraBotoesVoltar();
        configuraRecyclerResumo();
        carregarItensDoCarrinho();
    }

    private void vinculaComponentes() {
        toolbar = findViewById(R.id.compraConcluida_toolbar);
        recyclerView = findViewById(R.id.compraConcluida_recyclerView_resumo);
        btnVoltar = findViewById(R.id.compraConcluida_button_voltar);
    }

    private void configuraBotoesVoltar() {
        toolbar.setNavigationOnClickListener(v -> voltarParaHome());
        btnVoltar.setOnClickListener(v -> voltarParaHome());
    }

    private void voltarParaHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void configuraRecyclerResumo() {
        carrinhoAdapter = new CarrinhoAdapter(new ArrayList<>(), null);
        recyclerView.setAdapter(carrinhoAdapter);
    }

    private void carregarItensDoCarrinho() {
        executarLocal(
                () -> lcont.listaItemCarrinho(),
                listaRecebida -> {
                    if (listaRecebida != null) {
                        carrinhoAdapter.atualizarLista(listaRecebida);
                        limparCarrinhoLocal();
                    }
                }
        );
    }

    private void limparCarrinhoLocal() {
        executarLocal(
                () -> lcont.limparCarrinho(),
                resultado -> {
                    Log.d("CompraConcluidaActivity", "Carrinho local limpo com sucesso.");
                }
        );
    }
}