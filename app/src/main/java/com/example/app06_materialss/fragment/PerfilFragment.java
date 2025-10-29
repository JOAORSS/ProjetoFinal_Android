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
import com.example.app06_materialss.activity.AutenticacaoActivity;
import com.example.app06_materialss.activity.MainActivity;
import com.example.app06_materialss.controller.ConexaoController;
import com.example.app06_materialss.controller.LocalController;
import com.example.app06_materialss.entity.UsuarioLogado;
import com.example.app06_materialss.utils.SessaoManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import br.com.autopeca360.dominio.Usuario;

public class PerfilFragment extends Fragment {

    private MaterialToolbar toolbar;
    private TextView tvNome, tvEmail, tvDataCadastro, tvExcluirConta;
    private Button btnDesconectar;

    private AppAutopecaActivity baseActivity;
    private LocalController lcont;
    private ConexaoController ccont;

    public static PerfilFragment newInstance() {
        return new PerfilFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() instanceof AppAutopecaActivity) {
            baseActivity = (AppAutopecaActivity) getActivity();
            lcont = baseActivity.lcont;
            ccont = baseActivity.ccont;
        } else {
            throw new RuntimeException("A Activity hospedeira deve herdar de AppAutopecaActivity");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vinculaComponentes(view);
        configuraToolbar();
        carregarDadosUsuario();
        configuraBotaoDesconectar();
        configuraBotaoExcluirConta();
    }

    private void vinculaComponentes(View view) {
        toolbar = view.findViewById(R.id.perfil_toolbar);
        tvNome = view.findViewById(R.id.perfil_textView_nome);
        tvEmail = view.findViewById(R.id.perfil_textView_email);
        tvDataCadastro = view.findViewById(R.id.perfil_textView_dataCadastro);
        btnDesconectar = view.findViewById(R.id.perfil_button_desconectar);
        tvExcluirConta = view.findViewById(R.id.perfil_textView_excluirConta);
    }

    private void configuraToolbar() {
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    private void carregarDadosUsuario() {
        UsuarioLogado usuario = SessaoManager.getInstance().getUsuario();
        if (usuario != null) {
            tvNome.setText(usuario.getNome());
            tvEmail.setText(usuario.getEmail());
            tvDataCadastro.setText(usuario.getDataCadastro());
        } else {
            Toast.makeText(getContext(), "Faça o login novamente.", Toast.LENGTH_SHORT).show();
            if (getContext() != null) {
                Intent intent = new Intent(getContext(), AutenticacaoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        }
    }

    private void configuraBotaoDesconectar() {
        btnDesconectar.setOnClickListener(v -> {
            desconectarUsuario();
        });
    }

    private void configuraBotaoExcluirConta() {
        tvExcluirConta.setOnClickListener(v -> {
            exibirConfirmacaoExclusao();
        });
    }

    private void exibirConfirmacaoExclusao() {
        if (getContext() == null) return;
        UsuarioLogado usuario = SessaoManager.getInstance().getUsuario();

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja excluir sua conta? Esta ação não pode ser desfeita.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Excluir", (dialog, which) -> {
                    if (baseActivity == null) return;
                    baseActivity.executarComConexao(
                            () -> ccont.usuarioExcluir(new Usuario(usuario.getId())),
                            sucesso -> {
                                if (sucesso) {
                                    Toast.makeText(getContext(), "Conta excluída com sucesso", Toast.LENGTH_SHORT).show();
                                    desconectarUsuario();
                                } else {
                                    Toast.makeText(getContext(), "Erro ao excluir conta no servidor", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                })
                .show();
    }

    private void desconectarUsuario() {
        if (baseActivity == null || getContext() == null) return;

        baseActivity.executarLocal(
                () -> lcont.deletarUsuarioLogado(),
                resultado -> {
                    SessaoManager.getInstance().encerrarSessao();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
        );
    }
}

