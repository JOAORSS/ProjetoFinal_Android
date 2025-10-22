package com.example.app06_materialss.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app06_materialss.R;
import com.example.app06_materialss.fragment.CadastroFragment;
import com.example.app06_materialss.fragment.LoginFragment;
import com.example.app06_materialss.fragment.interfaces.NavegacaoAutenticacaoListener;
import com.google.android.material.appbar.MaterialToolbar;

public class AutenticacaoActivity extends AppAutopecaActivity implements NavegacaoAutenticacaoListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_autenticacao);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.autenticacao_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.autenticacao_toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.autenticacao_fragment_container, new LoginFragment())
                    .commit();
        }
    }

    @Override
    public void irParaCadastro() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.autenticacao_fragment_container, new CadastroFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void irParaLogin() {
        getSupportFragmentManager().popBackStack();
    }
}

