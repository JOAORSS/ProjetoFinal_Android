package com.example.app06_materialss.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.app06_materialss.R;
import com.example.app06_materialss.controller.ConexaoController;
import com.example.app06_materialss.controller.LocalController;
import com.example.app06_materialss.entity.PecaCarrinho;
import com.example.app06_materialss.entity.PecaFavorita;
import com.example.app06_materialss.fragment.HomeFragment;
import com.example.app06_materialss.utils.SessaoManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import br.com.autopeca360.dominio.Peca;
import io.getstream.photoview.PhotoView;

public class PecaActivity extends AppAutopecaActivity {

    private TextView TvTitulo, TvPreco, TvFornecedor, TvCompativel, TvDescricao, Quantidade;
    private ImageButton BtnMenosQuantidade, BtnMaisQuantidade, BtnFavoritar;
    private Button BtnAdicionarAoCarrinho;
    private MaterialToolbar toolbar;
    private PhotoView photoView;

    private Peca peca;
    private boolean favoritado;
    private int quantidadeInteiro = 1;
    private LocalController lcont;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_peca);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ccont = ConexaoController.getInstance();
        lcont = LocalController.getInstance(this);

        vinculaComponentes();
        carregaConteudoPeca();
        configuraSpinnerQuantidade();
        configuraBotaoAdicionarAoCarrinho();
        configuraBotaoFavoritar();
        configuraBotaoVoltarToolbar();

    }

    private void configuraBotaoVoltarToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void configuraBotaoFavoritar() {
        BtnFavoritar.setOnClickListener(v -> {
            if (peca == null) return;

            if (favoritado) {
                PecaFavorita pecaParaDeletar = new PecaFavorita(peca.getCodpeca(), peca.getNome(), peca.getPreco(), peca.getImagem());
                executarLocal(
                        () -> lcont.deletarFavorito(pecaParaDeletar),
                        resultado -> {
                            Snackbar snack = Snackbar.make(v, "Removido dos favoritos", Snackbar.LENGTH_SHORT);
                            snack.setAnchorView(BtnAdicionarAoCarrinho);
                            snack.show();
                            atualizarIconeFavorito(false);
                        }
                );
            } else {
                PecaFavorita pecaParaSalvar = new PecaFavorita(peca.getCodpeca(), peca.getNome(), peca.getPreco(), peca.getImagem());
                executarLocal(
                        () -> lcont.inserirFavorito(pecaParaSalvar),
                        resultado -> {
                            Snackbar snack = Snackbar.make(v, "Adicionado aos favoritos", Snackbar.LENGTH_SHORT);
                            snack.setAnchorView(BtnAdicionarAoCarrinho);
                            snack.show();
                            atualizarIconeFavorito(true);
                        }
                );
            }
        });
    }

    private void verificarStatusFavorito(int pecaId) {
        executarLocal(
                () -> lcont.buscarFavoritoPorId(pecaId),
                pecaFavorita -> {
                    if (pecaFavorita != null) {
                        atualizarIconeFavorito(true);
                    } else {
                        atualizarIconeFavorito(false);
                    }
                }
        );
    }

    private void atualizarIconeFavorito(boolean isFavorito) {
        this.favoritado = isFavorito;
        if (isFavorito) {
            BtnFavoritar.setColorFilter(ContextCompat.getColor(this, R.color.md_theme_tertiaryFixedDim));
        } else {
            BtnFavoritar.setColorFilter(ContextCompat.getColor(this, R.color.md_theme_secondaryFixedDim));
        }
    }

    private void configuraBotaoAdicionarAoCarrinho() {
        BtnAdicionarAoCarrinho.setOnClickListener(v -> {
            if (!SessaoManager.getInstance().isLogado()) {
                Toast.makeText(this, "Faça login para adicionar itens ao carrinho.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, AutenticacaoActivity.class);
                startActivity(intent);
                return;
            }

            if (peca == null) {
                Toast.makeText(this, "Aguarde o carregamento da peça.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (lcont == null) {
                Toast.makeText(this, "Erro no banco de dados. Tente novamente.", Toast.LENGTH_SHORT).show();
                return;
            }

            executarLocal(
                    () -> lcont.buscaItemCarrinhoPorId(peca.getCodpeca()),
                    itemExistente -> {
                        if (itemExistente != null) {
                            int novaQuantidade = itemExistente.getQuantidade() + quantidadeInteiro;
                            itemExistente.setQuantidade(novaQuantidade);
                            executarLocal(
                                    () -> lcont.atualizarItemCarrinho(itemExistente),
                                    resultadoUpdate -> {
                                        Snackbar snack = Snackbar.make(v, "Quantidade atualizada no carrinho", Snackbar.LENGTH_SHORT);
                                        snack.setAnchorView(BtnAdicionarAoCarrinho);
                                        snack.show();
                                    }
                            );
                        } else {
                            PecaCarrinho itemNovo = new PecaCarrinho(
                                    peca.getCodpeca(),
                                    peca.getNome(),
                                    peca.getPreco(),
                                    peca.getImagem(),
                                    quantidadeInteiro
                            );
                            executarLocal(
                                    () -> lcont.inserirItemCarrinho(itemNovo),
                                    resultadoInsert -> {
                                        Snackbar snack = Snackbar.make(v, "Adicionado ao carrinho", Snackbar.LENGTH_SHORT);
                                        snack.setAnchorView(BtnAdicionarAoCarrinho);
                                        snack.show();
                                    }
                            );
                        }
                    }
            );
        });
    }

    private void configuraSpinnerQuantidade() {
        BtnMenosQuantidade.setOnClickListener(v -> {
            quantidadeInteiro = Integer.parseInt(Quantidade.getText().toString());
            if (quantidadeInteiro > 1) {
                quantidadeInteiro--;
                Quantidade.setText(String.valueOf(quantidadeInteiro));
            }
        });

        BtnMaisQuantidade.setOnClickListener(v -> {
            quantidadeInteiro = Integer.parseInt(Quantidade.getText().toString());
            if (peca != null && quantidadeInteiro < peca.getQuantidadeEstoque()){
                quantidadeInteiro++;
                Quantidade.setText(String.valueOf(quantidadeInteiro));
            }
        });
    }

    private void carregaConteudoPeca() {
        int pecaId = getIntent().getIntExtra(HomeFragment.PECA_ID, -1);
        if (pecaId != -1) {
            carregarDadosPeca(pecaId);
        } else {
            Toast.makeText(this, "Erro ao carregar peça", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void vinculaComponentes() {
        TvTitulo = findViewById(R.id.peca_textView_title);
        photoView = findViewById(R.id.peca_photoView);
        TvPreco = findViewById(R.id.peca_textView_preco);
        toolbar = findViewById(R.id.peca_toolbar);
        BtnMenosQuantidade = findViewById(R.id.peca_button_decrease);
        BtnMaisQuantidade = findViewById(R.id.peca_button_increment);
        Quantidade = findViewById(R.id.peca_textView_quantity);
        BtnAdicionarAoCarrinho = findViewById(R.id.peca_button_adicionarAoCarrinho);
        BtnFavoritar = findViewById(R.id.peca_favoritar);
        TvFornecedor = findViewById(R.id.peca_textView_fornecedor);
        TvCompativel = findViewById(R.id.peca_textView_compativel);
        TvDescricao = findViewById(R.id.peca_textView_descricao);
    }

    private void carregarDadosPeca(int pecaId) {
        executarComConexao(
                () -> ccont.pecaBusca(pecaId),
                pecaResult -> {
                    if (pecaResult != null) {
                        this.peca = pecaResult;
                        TvTitulo.setText(peca.getNome());
                        toolbar.setTitle(peca.getNome());
                        String precoFormatado = String.format(new Locale("pt", "BR"), "R$ %.2f", peca.getPreco());
                        TvPreco.setText(precoFormatado);
                        TvFornecedor.setText(peca.getFornecedor().getNome());
                        TvCompativel.setText(peca.getCarroCompativel());
                        TvDescricao.setText(peca.getDescricao());
                        Glide.with(this)
                                .load(peca.getImagem())
                                .into(photoView);

                        verificarStatusFavorito(peca.getCodpeca());
                    } else {
                        Toast.makeText(this, "Erro ao carregar peças", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}

