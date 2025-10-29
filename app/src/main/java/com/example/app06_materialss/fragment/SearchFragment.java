package com.example.app06_materialss.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app06_materialss.R;
import com.example.app06_materialss.activity.AppAutopecaActivity;
import com.example.app06_materialss.activity.AutenticacaoActivity;
import com.example.app06_materialss.activity.CarrinhoActivity;
import com.example.app06_materialss.activity.PecaActivity;
import com.example.app06_materialss.adapter.PecaAdapter; // Mudança para PecaAdapter
import com.example.app06_materialss.controller.ConexaoController; // Necessário para ccont
import com.example.app06_materialss.fragment.interfaces.OnSearchBarClickListener;
import com.example.app06_materialss.utils.SessaoManager;
import com.google.android.material.search.SearchBar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.autopeca360.dominio.Peca;

public class SearchFragment extends Fragment {

    private SearchBar searchBar;
    private RecyclerView recyclerViewResultados;
    private TextView tvNenhumResultado;
    private PecaAdapter resultadoAdapter;
    private List<Peca> listaResultados;
    private List<Peca> todasAsPecas;
    private AppAutopecaActivity baseActivity;
    private ConexaoController ccont;

    private OnSearchBarClickListener listener;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchBarClickListener) {
            listener = (OnSearchBarClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchBarClickListener");
        }
        if (context instanceof AppAutopecaActivity) {
            baseActivity = (AppAutopecaActivity) context;
            ccont = baseActivity.ccont;
        } else {
            throw new RuntimeException("Activity must extend AppAutopecaActivity");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vinculaViews(view);
        configuraRecyclerView();
        configuraSearch();
        carregarCatalogoCompleto();
    }

    private void vinculaViews(View view) {
        searchBar = view.findViewById(R.id.search_search_bar);
        recyclerViewResultados = view.findViewById(R.id.search_recyclerView_resultados);
        tvNenhumResultado = view.findViewById(R.id.search_textView_nenhumResultado);
    }

    private void configuraSearch() {
        // Configura clique no ícone do carrinho
        searchBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_carrinho) {
                if (SessaoManager.getInstance().isLogado()) {
                    Intent intent = new Intent(getActivity(), CarrinhoActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Faça login para acessar o carrinho.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), AutenticacaoActivity.class);
                    startActivity(intent);
                }
                return true;
            }
            return false;
        });

        // Trigger para mostrar a SearchView imediatamente
        searchBar.post(() -> {
            if (listener != null) {
                listener.onSearchBarClicked(searchBar);
            }
        });
    }

    private void configuraRecyclerView() {
        listaResultados = new ArrayList<>();
        todasAsPecas = new ArrayList<>();
        resultadoAdapter = new PecaAdapter(listaResultados, peca -> {
            Intent intent = new Intent(getActivity(), PecaActivity.class);
            intent.putExtra(HomeFragment.PECA_ID, peca.getCodpeca()); // Usa o ID da Peca
            startActivity(intent);
        });
        recyclerViewResultados.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewResultados.setAdapter(resultadoAdapter);
    }

    private void carregarCatalogoCompleto() {
        if (baseActivity == null || ccont == null) {
            Toast.makeText(getContext(), "Erro ao inicializar.", Toast.LENGTH_SHORT).show();
            return;
        }

        baseActivity.executarComConexao(
                () -> ccont.pecaLista(),
                listaRecebida -> {
                    if (listaRecebida != null) {
                        todasAsPecas = listaRecebida;
                    } else {
                        Toast.makeText(getContext(), "Erro ao carregar catálogo", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void realizarBusca(String query) {
        if (todasAsPecas == null) {
            return;
        }

        if (query == null || query.trim().isEmpty()) {
            atualizarResultados(new ArrayList<>());
            return;
        }

        String lowerCaseQuery = query.trim().toLowerCase();

        List<Peca> resultadosFiltrados = todasAsPecas.stream()
                .filter(peca -> peca.getNome().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());

        atualizarResultados(resultadosFiltrados);
    }

    // Método atualizado para receber List<Peca>
    private void atualizarResultados(List<Peca> resultados) {
        if (resultados != null && !resultados.isEmpty()) {
            resultadoAdapter.atualizarLista(resultados);
            recyclerViewResultados.setVisibility(View.VISIBLE);
            tvNenhumResultado.setVisibility(View.GONE);
        } else {
            resultadoAdapter.atualizarLista(new ArrayList<>()); // Limpa o adapter
            recyclerViewResultados.setVisibility(View.GONE);
            tvNenhumResultado.setVisibility(View.VISIBLE); // Mostra "Nenhum resultado"
        }
    }
}

