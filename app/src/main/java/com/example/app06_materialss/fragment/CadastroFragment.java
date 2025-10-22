package com.example.app06_materialss.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app06_materialss.R;
import com.example.app06_materialss.activity.AppAutopecaActivity;
import com.example.app06_materialss.fragment.interfaces.NavegacaoAutenticacaoListener;
import com.google.android.material.textfield.TextInputEditText;

public class CadastroFragment extends Fragment {

    private TextInputEditText etNome, etEmail, etSenha;
    private Button btnCadastrar;
    private TextView tvFazerLogin;
    private AppAutopecaActivity baseActivity;
    private NavegacaoAutenticacaoListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppAutopecaActivity) {
            baseActivity = (AppAutopecaActivity) context;
        } else {
            throw new RuntimeException(context.toString() + " must be an instance of AppAutopecaActivity");
        }

        if (context instanceof NavegacaoAutenticacaoListener) {
            listener = (NavegacaoAutenticacaoListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement NavegacaoAutenticacaoListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cadastro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vinculaComponentes(view);
        configuraCliqueLogin();
        configuraBotaoCadastrar();
    }

    private void vinculaComponentes(View view) {
        etNome = view.findViewById(R.id.cadastro_textInputEditText_nome);
        etEmail = view.findViewById(R.id.cadastro_textInputEditText_email);
        etSenha = view.findViewById(R.id.cadastro_textInputEditText_senha);
        btnCadastrar = view.findViewById(R.id.cadastro_button_cadastrar);
        tvFazerLogin = view.findViewById(R.id.cadastro_textView_fazerLogin);
    }

    private void configuraCliqueLogin() {
        tvFazerLogin.setOnClickListener(v -> {
            if (listener != null) {
                listener.irParaLogin();
            }
        });
    }

    private void configuraBotaoCadastrar() {
        btnCadastrar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "TODO: Implementar lógica de cadastro", Toast.LENGTH_SHORT).show();
        });
    }
}
