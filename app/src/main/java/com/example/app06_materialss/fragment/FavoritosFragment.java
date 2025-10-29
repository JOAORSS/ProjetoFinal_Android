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
import com.example.app06_materialss.activity.CarrinhoActivity;
import com.example.app06_materialss.activity.PecaActivity;
import com.example.app06_materialss.adapter.PecaFavoritaAdapter;
import com.example.app06_materialss.controller.LocalController;
import com.example.app06_materialss.entity.PecaFavorita;
import com.example.app06_materialss.fragment.interfaces.OnSearchBarClickListener;
import com.google.android.material.search.SearchBar;

import java.util.ArrayList;
import java.util.List;

public class FavoritosFragment extends Fragment {

    private RecyclerView recyclerViewFavoritos;
    private PecaFavoritaAdapter pecaAdapter;
    private List<PecaFavorita> listaDePecas;
    private LocalController lcont;
    private AppAutopecaActivity baseActivity;
    private SearchBar searchBar;
    private TextView tvEmpty;
    private OnSearchBarClickListener listener;

    public static FavoritosFragment newInstance() {
        return new FavoritosFragment();
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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = (AppAutopecaActivity) getActivity();
        if (baseActivity != null) {
            lcont = baseActivity.lcont;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favoritos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vinculaViews(view);
        configuraRecyclerView();
        configuraSearch();
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarPecasFavoritas();
    }

    private void vinculaViews(View view) {
        recyclerViewFavoritos = view.findViewById(R.id.favoritos_recyclerView);
        searchBar = view.findViewById(R.id.favoritos_search_bar);
        tvEmpty = view.findViewById(R.id.favoritos_textView_empty);
    }

    private void configuraSearch() {
        searchBar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSearchBarClicked(searchBar);
            }
        });

        searchBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_carrinho) {
                Intent intent = new Intent(getActivity(), CarrinhoActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void configuraRecyclerView() {
        listaDePecas = new ArrayList<>();
        pecaAdapter = new PecaFavoritaAdapter(listaDePecas, peca -> {
            Intent intent = new Intent(getActivity(), PecaActivity.class);
            intent.putExtra(HomeFragment.PECA_ID, peca.getPecaId());
            startActivity(intent);
        });
        recyclerViewFavoritos.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewFavoritos.setAdapter(pecaAdapter);
    }

    private void carregarPecasFavoritas() {
        if (baseActivity == null || lcont == null) return;

        baseActivity.executarLocal(
                () -> lcont.listaItensFavoritos(),
                listaRecebida -> {
                    if (listaRecebida != null && !listaRecebida.isEmpty()) {
                        pecaAdapter.atualizarLista(listaRecebida);
                        recyclerViewFavoritos.setVisibility(View.VISIBLE);
                        tvEmpty.setVisibility(View.GONE);
                    } else {
                        pecaAdapter.atualizarLista(new ArrayList<>());
                        recyclerViewFavoritos.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                }
        );
    }
}

