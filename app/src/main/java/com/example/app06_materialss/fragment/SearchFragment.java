package com.example.app06_materialss.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.example.app06_materialss.R;
import com.example.app06_materialss.fragment.interfaces.OnSearchBarClickListener;
import com.google.android.material.search.SearchBar;

public class SearchFragment extends Fragment {

    // TODO: aqui vai precisar de mais um db local pra guardar as ultimas pesquisas

    private SearchBar searchBar;

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

        searchBar = view.findViewById(R.id.search_search_bar);
        configuraSearch();
    }

    private void configuraSearch() {
        searchBar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSearchBarClicked(searchBar);
            }
        });
        searchBar.post(() -> {
            if (listener != null) {
                listener.onSearchBarClicked(searchBar);
            }
        });
    }

}