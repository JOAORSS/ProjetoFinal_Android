package com.example.app06_materialss.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app06_materialss.R;
import br.com.autopeca360.dominio.Usuario;

public class LoginActivity extends AppCompatActivity {

    private
        Button BtnLogin;
        EditText TFemail;
        EditText TFsenha;
        TextView TVrecuperarSenha;
        TextView TVcadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        VinculaBotoes();

        BtnLogin.setOnClickListener(v -> {
            String email = TFemail.getText().toString();
            String senha = TFsenha.getText().toString();

            Usuario usuario = new Usuario(email, senha);
            //Toast.makeText(usuario.toString(), Toast.LENGTH_SHORT);
        });

    }

    private void VinculaBotoes() {
        BtnLogin = findViewById(R.id.jBtnLogin);
        TFemail = findViewById(R.id.jTFemail);
        TFsenha = findViewById(R.id.jTFsenha);
        TVrecuperarSenha = findViewById(R.id.jTVrecuperarSenha);
        TVcadastrar = findViewById(R.id.jTVcadastrar);
    }
}