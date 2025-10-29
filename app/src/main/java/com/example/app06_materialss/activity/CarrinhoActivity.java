package com.example.app06_materialss.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app06_materialss.R;
import com.example.app06_materialss.adapter.CarrinhoAdapter;
import com.example.app06_materialss.controller.LocalController;
import com.example.app06_materialss.entity.PecaCarrinho;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CarrinhoActivity extends AppAutopecaActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private TextView valorTotalTextView;
    private Button finalizarButton;
    private CarrinhoAdapter carrinhoAdapter;
    private List<PecaCarrinho> listaItens;
    private LocalController lcont;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_carrinho);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.carrinho_main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lcont = LocalController.getInstance(this);

        vinculaComponentes();
        configuraVoltarTopBar();
        configuraRecyclerCarrinho();
        carregarItensDoCarrinho();
        configuraBotaoFinalizar();
        atualizaEstadoBotaoFinalizar(new ArrayList<>());
    }

    private void vinculaComponentes() {
        toolbar = findViewById(R.id.carrinho_toolbar);
        recyclerView = findViewById(R.id.carrinho_recyclerView);
        valorTotalTextView = findViewById(R.id.carrinho_textView_valor);
        finalizarButton = findViewById(R.id.carrinho_button_finalizar);
    }

    private void configuraBotaoFinalizar() {
        finalizarButton.setOnClickListener(v -> {
            Intent intent = new Intent(CarrinhoActivity.this, FinalizarActivity.class);
            startActivity(intent);
        });
    }

    private void configuraVoltarTopBar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void configuraRecyclerCarrinho() {
        listaItens = new ArrayList<>();
        carrinhoAdapter = new CarrinhoAdapter(listaItens, this::configuraExclusaoPecaDoCarrinho);
        recyclerView.setAdapter(carrinhoAdapter);
    }

    private void configuraValorTotal(List<PecaCarrinho> itens) {
        double total = 0.0;
        for (PecaCarrinho item : itens) {
            total += item.getPreco() * item.getQuantidade();
        }
        String precoFormatado = String.format(new Locale("pt", "BR"), "R$ %.2f", total);
        valorTotalTextView.setText(precoFormatado);
    }

    private void carregarItensDoCarrinho() {
        executarLocal(
                () -> lcont.listaItemCarrinho(),
                listaRecebida -> {
                    if (listaRecebida != null) {
                        this.listaItens = listaRecebida;
                        carrinhoAdapter.atualizarLista(listaItens);
                        configuraValorTotal(listaItens);
                        atualizaEstadoBotaoFinalizar(listaItens);
                    }
                }
        );
    }

    private void atualizaEstadoBotaoFinalizar(List<PecaCarrinho> itens) {
        if (itens == null || itens.isEmpty()) {
            finalizarButton.setEnabled(false);
        } else {
            finalizarButton.setEnabled(true);
        }
    }

    private void configuraExclusaoPecaDoCarrinho(PecaCarrinho itemParaExcluir) {
        executarLocal(
                () -> lcont.deletarItemCarrinho(itemParaExcluir),
                resultado -> {
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                              "Removido do carrinho",
                                                    Snackbar.LENGTH_SHORT);
                    snack.setAnchorView(finalizarButton);
                    snack.show();
                    carregarItensDoCarrinho();
                }
        );
    }
}

