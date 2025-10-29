package com.example.app06_materialss.fragment;

import android.content.Context;
import android.content.Intent;
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
import com.example.app06_materialss.activity.MainActivity;
import com.example.app06_materialss.entity.UsuarioLogado;
import com.example.app06_materialss.fragment.interfaces.NavegacaoAutenticacaoListener;
import com.example.app06_materialss.utils.SessaoManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.autopeca360.dominio.Usuario;

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
            String nome = etNome.getText().toString();
            String email = etEmail.getText().toString();
            String senha = etSenha.getText().toString();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Snackbar.make(baseActivity.findViewById(R.id.cadastro_constraintLayout_main),
                        "Preencha todos os campos",
                            Toast.LENGTH_SHORT)
                            .show();
                return;
            }

            Usuario usuarioDeCadastro = new Usuario(0, nome, email, senha);

            baseActivity.executarComConexao(
                    () -> baseActivity.ccont.usuarioCadastro(usuarioDeCadastro),
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
                                        Snackbar.make(baseActivity.findViewById(R.id.cadastro_constraintLayout_main),
                                                 "Cadastro realizado com sucesso!",
                                                      Snackbar.LENGTH_LONG)
                                                      .show();
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                            );
                        } else {
                            Snackbar.make(baseActivity.findViewById(R.id.cadastro_constraintLayout_main),
                                     "E-mail ou senha inválidos.",
                                          Snackbar.LENGTH_SHORT)
                                          .show();
                        }
                    }
            );

        });
    }
}
