package com.example.app06_materialss.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.app06_materialss.R;
import com.example.app06_materialss.activity.AppAutopecaActivity;
import com.example.app06_materialss.adapter.PecaAdapter;
import com.example.app06_materialss.fragment.interfaces.OnSearchBarClickListener;
import com.google.android.material.search.SearchBar;

import br.com.autopeca360.dominio.Peca;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewPeca;
    private SearchBar searchBar;
    private PecaAdapter pecaAdapter;
    private List<Peca> listaDePecas;

    private AppAutopecaActivity baseActivity;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private OnSearchBarClickListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchBarClickListener) {
            listener = (OnSearchBarClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchBarClickListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = (AppAutopecaActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VinculaViews(view);
        configuraRecyclerViewPeca(view);
        configuraSearch();
        carregarPecas();
    }

    private void configuraSearch() {
        searchBar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSearchBarClicked(searchBar);
            }
        });
    }

    private void VinculaViews(View view) {
        recyclerViewPeca = view.findViewById(R.id.home_recyclerView_peca);
        searchBar = view.findViewById(R.id.home_search_bar);
    }

    private void configuraRecyclerViewPeca(View view) {
        listaDePecas = new ArrayList<>();
        pecaAdapter = new PecaAdapter(listaDePecas);
        recyclerViewPeca.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewPeca.setAdapter(pecaAdapter);
    }

    private void carregarPecas() {
        if (baseActivity == null) return;

        baseActivity.executarComConexao(
                () -> baseActivity.ccont.pecaLista(),
                listaRecebida -> {
                    if (listaRecebida != null) {
                        pecaAdapter.atualizarLista(listaRecebida);
                    } else {
                        Toast.makeText(getContext(), "Erro ao carregar peças", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}