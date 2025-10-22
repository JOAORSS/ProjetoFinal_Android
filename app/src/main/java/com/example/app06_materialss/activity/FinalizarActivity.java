package com.example.app06_materialss.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

public class FinalizarActivity extends AppAutopecaActivity {

    private MaterialToolbar toolbar;
    private TextView emailTextView, nomeTextView, dataCriacaoTextView;
    private EditText enderecoEditText;
    private TextView valorPagarTextView, pixKeyTextView;
    private Button copiarButton;
    private RecyclerView resumoRecyclerView;
    private CarrinhoAdapter carrinhoAdapter;
    private LocalController lcont;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_finalizar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.finalizar_main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lcont = LocalController.getInstance(this);

        vinculaComponentes();
        configuraVoltarTopBar();
        configuraBotaoConfirmar();
        configuraBotaoCopiar();
        configuraRecyclerResumo();
        carregarDados();
    }

    private void vinculaComponentes() {
        toolbar = findViewById(R.id.finalizar_toolbar);
        emailTextView = findViewById(R.id.finalizar_textView_email);
        nomeTextView = findViewById(R.id.finalizar_textView_nome);
        dataCriacaoTextView = findViewById(R.id.finalizar_textView_dataCriacao);
        enderecoEditText = findViewById(R.id.finalizar_editText_endereco);
        valorPagarTextView = findViewById(R.id.finalizar_textView_valorPagar);
        pixKeyTextView = findViewById(R.id.finalizar_textView_pixKey);
        copiarButton = findViewById(R.id.finalizar_button_copiar);
        resumoRecyclerView = findViewById(R.id.finalizar_recyclerView_resumo);
    }

    private void configuraVoltarTopBar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void configuraBotaoConfirmar() {
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.finalizar_menu_confirmar) {
                // Intent intent = new Intent(FinalizarActivity.this, ProximaTelaActivity.class);
                // startActivity(intent);
                Snackbar.make(findViewById(android.R.id.content), "Pedido finalizado!", Snackbar.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void configuraBotaoCopiar() {
        copiarButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Chave Pix", pixKeyTextView.getText().toString());
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Snackbar.make(findViewById(android.R.id.content), "Chave Pix copiada!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void configuraRecyclerResumo() {
        carrinhoAdapter = new CarrinhoAdapter(new ArrayList<>(), null);
        resumoRecyclerView.setAdapter(carrinhoAdapter);
    }

    private void carregarDados() {
        carregarDadosUsuario();
        carregarItensDoCarrinho();
    }

    private void carregarDadosUsuario() {
        // TODO: Substituir pela lógica de busca do usuário logado
        emailTextView.setText("Email: usuario@exemplo.com");
        nomeTextView.setText("Nome: Usuário de Teste");
        dataCriacaoTextView.setText("Cliente desde: 22/10/2025");
    }

    private void carregarItensDoCarrinho() {
        executarLocal(
                () -> lcont.listaItemCarrinho(),
                listaRecebida -> {
                    if (listaRecebida != null) {
                        carrinhoAdapter.atualizarLista(listaRecebida);
                        configuraValorTotal(listaRecebida);
                    }
                }
        );
    }

    private void configuraValorTotal(List<PecaCarrinho> itens) {
        double total = 0.0;
        for (PecaCarrinho item : itens) {
            total += item.getPreco() * item.getQuantidade();
        }
        String precoFormatado = String.format(new Locale("pt", "BR"), "R$ %.2f", total);
        valorPagarTextView.setText("Valor a pagar: " + precoFormatado);
    }
}
