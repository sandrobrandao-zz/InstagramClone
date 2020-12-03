package com.curso.instagramclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.curso.instagramclone.R;
import com.curso.instagramclone.helper.ConfiguracaoFirebase;
import com.curso.instagramclone.helper.UsuarioFirebase;
import com.curso.instagramclone.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {
    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference firebaseRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference usuarioLogadoRef;
    private ValueEventListener valueEventListenerPerfilAmigo;
    private TextView textPublicacoes;
    private TextView textSeguidores;
    private TextView textSeguindo;
    private String idUsuarioLogado;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_perfil_amigo );

        // configuracoes iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        usuariosRef = firebaseRef.child( "usuarios");
        seguidoresRef = firebaseRef.child( "seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        inicializarComponentes();

        // configura a toolbar
        Toolbar toolbar = findViewById( R.id.toolbarPrincipal );
        toolbar.setTitle( "Perfil" );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_close_black_24dp );

        // recupera usuario selecionado
        Bundle bundle = getIntent().getExtras();
        if( bundle != null ) {
            usuarioSelecionado = ( Usuario ) bundle.getSerializable("usuarioSelecionado" );

            // configura o nome do usuario na toolbar
            getSupportActionBar().setTitle( usuarioSelecionado.getNome() );

            // recupera a foto do usuario
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();
            if( caminhoFoto != null ) {
                Uri uri = Uri.parse( caminhoFoto );
                Glide.with(PerfilAmigoActivity.this )
                        .load( uri )
                        .into( imagePerfil );
            }
        }
    }

    private void recuperarDadosUsuarioLogado(){
        usuarioLogadoRef = usuariosRef.child( idUsuarioLogado );
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot dataSnapshot ) {
                        //Recupera dados de usuário logado
                        usuarioLogado = dataSnapshot.getValue( Usuario.class );

                        // Verifica se usuário segue o amigo selecionado
                        verificaSegueUsuarioAmigo();
                    }

                    @Override
                    public void onCancelled( DatabaseError databaseError ) {  }
                }
        );
    }

    private void verificaSegueUsuarioAmigo(){
        DatabaseReference seguidorRef = seguidoresRef
                .child( idUsuarioLogado )
                .child( usuarioSelecionado.getId() );
        seguidorRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot dataSnapshot ) {
                        if( dataSnapshot.exists() ){
                            // seguindo
                            habilitarBotaoSeguir(true );
                        }else {
                            // nao segue
                            habilitarBotaoSeguir(false );
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {  }
                }
        );
    }

    private void habilitarBotaoSeguir( boolean segueUsuario ) {
        if( segueUsuario ) {
            buttonAcaoPerfil.setText("Seguindo");
        } else {
            buttonAcaoPerfil.setText("Seguir");

            //Adiciona evento para seguir usuário
            buttonAcaoPerfil.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    //Salva seguidor
                    salvarSeguidor(usuarioLogado, usuarioSelecionado);
                }
            });
        }
    }

    private void salvarSeguidor( Usuario uLogado, Usuario uAmigo ) {
        /* seguidores
         *   id usuario logado
         *      id amigo (segue)
         *         dados seguindo
         */
        HashMap<String, Object> dadosAmigo = new HashMap<>();
        dadosAmigo.put( "nome", uAmigo.getNome() );
        dadosAmigo.put( "caminhoFoto", uAmigo.getCaminhoFoto() );
        DatabaseReference seguidorRef = seguidoresRef
                .child( uLogado.getId() )
                .child( uAmigo.getId() );
        seguidorRef.setValue( dadosAmigo );

        // altera botao para seguindo
        buttonAcaoPerfil.setText( "Seguindo" );

        // desabilita evento de clique do botao
        buttonAcaoPerfil.setOnClickListener( null );

        // incrementa noh seguindo do usuario logado
        int seguindo = uLogado.getSeguindo() + 1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put( "seguindo", seguindo );
        DatabaseReference usuarioSeguindo = usuariosRef
                .child( uLogado.getId() );
        usuarioSeguindo.updateChildren( dadosSeguindo );

        // incrementa noh seguidores do amigo
        int seguidores = uAmigo.getSeguidores() + 1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put( "seguidores", seguidores );
        DatabaseReference usuarioSeguidores = usuariosRef
                .child( uAmigo.getId() );
        usuarioSeguidores.updateChildren( dadosSeguidores );
    }

    @Override
    protected void onStart() {
        super.onStart();

        // recupera dados do amigo selecionado
        recuperarDadosPerfilAmigo();

        // recupera dados usuario logado
        recuperarDadosUsuarioLogado();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioAmigoRef.removeEventListener( valueEventListenerPerfilAmigo );
    }

    private void recuperarDadosPerfilAmigo() {
        usuarioAmigoRef = usuariosRef.child( usuarioSelecionado.getId() );
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot dataSnapshot ) {
                        Usuario usuario = dataSnapshot.getValue( Usuario.class );
                        String postagens = String.valueOf( usuario.getPostagens() );
                        String seguindo = String.valueOf( usuario.getSeguindo() );
                        String seguidores = String.valueOf( usuario.getSeguidores() );

                        //Configura valores recuperados
                        textPublicacoes.setText( postagens );
                        textSeguidores.setText( seguidores );
                        textSeguindo.setText( seguindo );
                    }

                    @Override
                    public void onCancelled( DatabaseError databaseError ) {  }
                }
        );
    }

    private void inicializarComponentes() {
        imagePerfil = findViewById(R.id.imagePerfil);
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        textPublicacoes = findViewById(R.id.textPublicacoes);
        textSeguidores = findViewById(R.id.textSeguidores);
        textSeguindo = findViewById(R.id.textSeguindo);
        buttonAcaoPerfil.setText("Carregando");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}