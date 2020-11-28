package com.curso.instagramclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.curso.instagramclone.R;
import com.curso.instagramclone.helper.ConfiguracaoFirebase;
import com.curso.instagramclone.helper.UsuarioFirebase;
import com.curso.instagramclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {
    private EditText campoNome;
    private EditText campoEmail;
    private EditText campoSenha;
    private Button botaoCadastrar;
    private ProgressBar progressBar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_cadastro );

        inicializarComponentes();

        // cadastrar usuario
        progressBar.setVisibility( View.GONE );
        botaoCadastrar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                if( !textoNome.isEmpty() ) {
                    if( !textoEmail.isEmpty() ) {
                        if( !textoSenha.isEmpty() ) {
                            usuario = new Usuario();
                            usuario.setNome( textoNome );
                            usuario.setEmail( textoEmail );
                            usuario.setSenha( textoSenha );
                            cadastrar( usuario );
                        } else {
                            Toast.makeText( CadastroActivity.this,
                                    "Preencha a senha!",
                                    Toast.LENGTH_SHORT ).show();
                        }
                    } else {
                        Toast.makeText( CadastroActivity.this,
                                "Preencha o email!",
                                Toast.LENGTH_SHORT ).show();
                    }
                } else {
                    Toast.makeText( CadastroActivity.this,
                            "Preencha o nome!",
                            Toast.LENGTH_SHORT ).show();
                }
            }
        });
    }

    public void inicializarComponentes() {
        campoNome = findViewById( R.id.editCadastroNome );
        campoEmail = findViewById( R.id.editCadastroEmail );
        campoSenha = findViewById( R.id.editCadastroSenha );
        botaoCadastrar = findViewById( R.id.buttonCadastrar );
        progressBar = findViewById( R.id.progressCadastro );
        campoNome.requestFocus();
    }

    public void cadastrar(final Usuario usuario ) {
        progressBar.setVisibility( View.VISIBLE );
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( @NonNull Task<AuthResult> task ) {
                        if( task.isSuccessful() ) {
                            try {
                                progressBar.setVisibility( View.GONE );

                                // salva dados no firebase
                                String idUsuario = task.getResult().getUser().getUid();
                                usuario.setId( idUsuario );
                                usuario.salvar();

                                // salvar dados no profile do firebase
                                UsuarioFirebase.atualizarNomeUsuario( usuario.getNome() );

                                Toast.makeText(CadastroActivity.this,
                                        "Cadastro realizado com sucesso!",
                                        Toast.LENGTH_SHORT ).show();
                                startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
                                finish();
                            } catch( Exception e ) {
                                e.printStackTrace();
                            }
                        } else {
                            progressBar.setVisibility( View.GONE );

                            String excecao = "";
                            try {
                                throw task.getException();
                            } catch( FirebaseAuthWeakPasswordException e ) {
                                excecao = "Digite uma senha mais forte!";
                            } catch ( FirebaseAuthInvalidCredentialsException e ) {
                                excecao = "Digite um email válido!";
                            } catch( FirebaseAuthUserCollisionException e ) {
                                excecao = "Esta conta já existe!";
                            } catch( Exception e ) {
                                excecao = "Erro ao logar usuário: " + e.getMessage();
                                e.printStackTrace(); // printa no Logcat
                            }
                            Toast.makeText(CadastroActivity.this,
                                    excecao,
                                    Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
        );
    }
}