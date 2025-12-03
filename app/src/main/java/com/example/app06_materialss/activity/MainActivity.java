package com.example.app06_materialss.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;
import android.widget.TextView; // Import para EditorActionListener

import androidx.activity.OnBackPressedCallback;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;

import com.example.app06_materialss.R;
import com.example.app06_materialss.controller.ConexaoController;
import com.example.app06_materialss.controller.LocalController; // Import LocalController
import com.example.app06_materialss.fragment.FavoritosFragment;
import com.example.app06_materialss.fragment.HomeFragment;
import com.example.app06_materialss.fragment.PerfilFragment;
import com.example.app06_materialss.fragment.SearchFragment;
import com.example.app06_materialss.fragment.interfaces.OnSearchBarClickListener;
import com.example.app06_materialss.utils.SessaoManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppAutopecaActivity implements OnSearchBarClickListener {

    public ConexaoController ccont;

    private boolean isLoginCheckFinished = false;
    private boolean isConnectionFinished = false;

    private BottomNavigationView navBar;
    private SearchView searchView;

    private OnBackPressedCallback searchViewBackPressedCallback;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        splashScreen.setKeepOnScreenCondition(() -> !isConnectionFinished || !isLoginCheckFinished );
        conectaServidor();
        verificarLoginLocal();

        setContentView(R.layout.activity_main);
        vinculaComponetes();
        ConfiguraNavBar();
        ConfiguraSearchView();

        if (savedInstanceState == null) {
            loadFragment(HomeFragment.newInstance());
        }
    }


    private void vinculaComponetes() {
        navBar = findViewById(R.id.main_nav_bar);
        searchView = findViewById(R.id.main_search_view);
    }

    private void ConfiguraNavBar() {
        navBar.setOnItemSelectedListener(item -> {
            int id  = item.getItemId();
            Fragment selectedFragment = null;

            if (id == R.id.nav_home) {
                selectedFragment = HomeFragment.newInstance();
            } else if (id == R.id.nav_search) {
                selectedFragment = SearchFragment.newInstance();
            } else if (id == R.id.nav_favorite) {
                selectedFragment = FavoritosFragment.newInstance();
            } else if (id == R.id.nav_user) {
                if (SessaoManager.getInstance().isLogado()) {
                    selectedFragment = PerfilFragment.newInstance();
                } else {
                    Toast.makeText(this, "Faça login para acessar o perfil.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, AutenticacaoActivity.class);
                    startActivity(intent);
                    return false;
                }
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void ConfiguraSearchView() {
        searchView.getToolbar().setNavigationOnClickListener(v -> {
            searchView.hide();
            if (searchViewBackPressedCallback != null && searchViewBackPressedCallback.isEnabled()) {
                searchViewBackPressedCallback.handleOnBackPressed();
            } else {
                loadFragment(HomeFragment.newInstance());
                navBar.setSelectedItemId(R.id.nav_home);
            }
        });

        searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchView.getText().toString();
                searchView.hide();

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof SearchFragment) {
                    ((SearchFragment) currentFragment).realizarBusca(query);
                }
                return true;
            }
            return false;
        });

        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (currentFragment instanceof SearchFragment) {
                        ((SearchFragment) currentFragment).realizarBusca("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        if (searchViewBackPressedCallback != null) {
            searchViewBackPressedCallback.setEnabled(false);
        }

        if (searchView.isShowing()) {
            searchView.hide();
        }
    }

    private void conectaServidor() {
        if (ccont == null) {
            ccont = ConexaoController.getInstance();
        }
        ccont.executar( /* faz uma chamada ao servidor */
                () -> runOnUiThread(() -> isConnectionFinished = true), /*  */
                () -> runOnUiThread(() -> {
                    Toast.makeText(this, "Falha na conexão com o servidor!", Toast.LENGTH_LONG).show();
                    isConnectionFinished = true;
                })
        );
    }

    private void verificarLoginLocal() {
        if (lcont == null) {
            lcont = LocalController.getInstance(this);
        }
        executarLocal(
                () -> lcont.getUsuarioLogado(),
                usuarioDoBanco -> {
                    if (usuarioDoBanco != null) {
                        SessaoManager.getInstance().iniciarSessao(usuarioDoBanco);
                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Bem vindo, " + usuarioDoBanco.getNome() + "!", Snackbar.LENGTH_SHORT);
                        snack.setAnchorView(navBar);
                        snack.show();
                    } else {
                        SessaoManager.getInstance().encerrarSessao();
                    }
                    isLoginCheckFinished = true;
                }
        );
    }

    @Override
    public void onHomeSearchBarClicked() {
        loadFragment(SearchFragment.newInstance());
        if (navBar != null) {
            navBar.setSelectedItemId(R.id.nav_search);
        }
    }


    @Override
    public void onSearchBarClicked(SearchBar searchBarDoFragment) {
        searchView.setupWithSearchBar(searchBarDoFragment);
        searchView.show();

        if (searchViewBackPressedCallback != null) {
            searchViewBackPressedCallback.remove();
        }

        if (searchBarDoFragment.getId() == R.id.favoritos_search_bar) {
            searchViewBackPressedCallback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (searchView.isShowing()) {
                        searchView.hide();
                    }
                    loadFragment(FavoritosFragment.newInstance());
                    setEnabled(false);
                }
            };
        } else if (searchBarDoFragment.getId() == R.id.search_search_bar) {
            searchViewBackPressedCallback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (searchView.isShowing()) {
                        searchView.hide();
                    } else {
                        finish();
                    }
                }
            };
        }
        else {
            searchViewBackPressedCallback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (searchView.isShowing()) {
                        searchView.hide();
                    }
                    loadFragment(HomeFragment.newInstance());
                    setEnabled(false);
                }
            };
        }

        // Adiciona o novo callback ao dispatcher
        getOnBackPressedDispatcher().addCallback(this, searchViewBackPressedCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Cancela buscas pendentes se a activity for parada
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}

