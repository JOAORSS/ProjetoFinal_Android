package com.example.app06_materialss.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.app06_materialss.activity.MainActivity;
import com.example.app06_materialss.entity.UsuarioLogado;
import com.example.app06_materialss.fragment.interfaces.NavegacaoAutenticacaoListener;
import com.example.app06_materialss.utils.SessaoManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.autopeca360.dominio.Usuario;

public class LoginFragment extends Fragment {

    private TextInputEditText etEmail;
    private TextInputEditText etSenha;
    private Button btnLogin;
    private TextView tvCadastrar;
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vinculaComponentes(view);
        configuraBotaoLogin();
        configuraCliqueCadastro();
    }

    private void vinculaComponentes(View view) {
        etEmail = view.findViewById(R.id.login_textInputEditText_email);
        etSenha = view.findViewById(R.id.login_textInputEditText_senha);
        btnLogin = view.findViewById(R.id.login_button_login);
        tvCadastrar = view.findViewById(R.id.login_textView_cadastrar);
    }

    private void configuraCliqueCadastro() {
        tvCadastrar.setOnClickListener(v -> {
            if (listener != null) {
                listener.irParaCadastro();
            }
        });
    }

    private void configuraBotaoLogin() {
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String senha = etSenha.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
                Snackbar.make(baseActivity.findViewById(R.id.login_constraintLayout_main),
                         "Por favor, preencha todos os campos.",
                              Snackbar.LENGTH_SHORT)
                              .show();
                return;
            }

            baseActivity.executarComConexao(
                    () -> baseActivity.ccont.usuarioLogin(new Usuario(email, senha)),
                    usuarioRetornado -> {
                        if (usuarioRetornado != null) {
                            UsuarioLogado usuarioParaSalvar = new UsuarioLogado(
                                    usuarioRetornado.getCodusuario(),
                                    usuarioRetornado.getNome(),
                                    usuarioRetornado.getEmail(),
                                    usuarioRetornado.getEndereco(),
                                    new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"))
                                            .format(usuarioRetornado.getDataCadastro())
                            );

                            baseActivity.executarLocal(
                                    () -> baseActivity.lcont.inserirUsuarioLogado(usuarioParaSalvar),
                                    resultado -> {
                                        SessaoManager.getInstance().iniciarSessao(usuarioParaSalvar);
                                        Snackbar.make(baseActivity.findViewById(R.id.login_constraintLayout_main),
                                                 "Login realizado com sucesso!",
                                                      Snackbar.LENGTH_LONG)
                                                      .show();
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                            );
                        } else {
                            Snackbar.make(baseActivity.findViewById(R.id.login_constraintLayout_main),
                                     "E-mail ou senha inválidos.",
                                          Snackbar.LENGTH_LONG)
                                          .show();
                        }
                    }
            );
        });
    }
}
