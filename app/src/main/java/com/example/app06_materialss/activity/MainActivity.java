package com.example.app06_materialss.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;

import com.example.app06_materialss.R;
import com.example.app06_materialss.controller.ConexaoController;
import com.example.app06_materialss.entity.PecaCarrinho;
import com.example.app06_materialss.fragment.HomeFragment;
import com.example.app06_materialss.fragment.SearchFragment;
import com.example.app06_materialss.fragment.interfaces.OnSearchBarClickListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

public class MainActivity extends AppAutopecaActivity implements OnSearchBarClickListener {

    public ConexaoController ccont;
    private boolean isConnectionFinished = false;

    private BottomNavigationView navBar;
    private SearchView searchView;
    private OnBackPressedCallback searchViewBackPressedCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        splashScreen.setKeepOnScreenCondition(() -> !isConnectionFinished);
        conectaServidor();

        setContentView(R.layout.activity_main);
        VinculaComponetes();
        ConfiguraNavBar();
        ConfiguraSearchView();

        if (savedInstanceState == null) {
            loadFragment(HomeFragment.newInstance());
        }

    }

    private void ConfiguraSearchView() {
        searchViewBackPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                setEnabled(false);
                searchView.hide();
                loadFragment(HomeFragment.newInstance());
                navBar.setSelectedItemId(R.id.nav_home);
            }
        };

        getOnBackPressedDispatcher().addCallback(this, searchViewBackPressedCallback);

        searchView.getToolbar().setNavigationOnClickListener(v -> {
            if (searchViewBackPressedCallback.isEnabled()) {
                searchViewBackPressedCallback.handleOnBackPressed();
            } else {
                searchView.hide();
            }
        });
    }

    @Override
    public void onSearchBarClicked(SearchBar searchBarDoFragment) {
        searchView.setupWithSearchBar(searchBarDoFragment);
        searchView.show();
        searchViewBackPressedCallback.setEnabled(true);
    }

    private void ConfiguraNavBar() {

        navBar.setOnItemSelectedListener(item -> {
            int id  = item.getItemId();

            if (id == R.id.nav_home) {
                loadFragment(HomeFragment.newInstance());
                return true;
            } else if (id == R.id.nav_search) {
                loadFragment(SearchFragment.newInstance());
                return true;
            } else if (id == R.id.nav_favorite) {
                //loadFragment();
                return true;
            } else if (id == R.id.nav_user) {
                //loadFragment();
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        searchViewBackPressedCallback.setEnabled(false);

        if (searchView.isShowing()) {
            searchView.hide();
        }
    }
    private void VinculaComponetes() {
        navBar = findViewById(R.id.main_nav_bar);
        searchView = findViewById(R.id.main_search_view);
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

    private void carregaCarrinho() {
        executarLocal(
                () -> lcont.listaItemCarrinho(),
                listaItemCarrinho -> {
                    if (listaItemCarrinho != null) {
                        Toast.makeText(this, "A lista de tamanho" + listaItemCarrinho.size(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "A lista de peças veio vazia ou nula.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void InserirCarrinho(PecaCarrinho peca) {
        executarLocal(
                () -> lcont.inserirItemCarrinho(peca),
                inserirItemCarrinho -> {
                    Toast.makeText(this, "Peca adicionada com sucesso.", Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}