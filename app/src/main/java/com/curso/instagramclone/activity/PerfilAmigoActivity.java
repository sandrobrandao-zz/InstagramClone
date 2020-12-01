package com.curso.instagramclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.curso.instagramclone.R;
import com.curso.instagramclone.helper.ConfiguracaoFirebase;
import com.curso.instagramclone.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {
    private Usuario usuarioSelecionado;
    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private ValueEventListener valueEventListenerPerfilAmigo;
    private TextView textPublicacoes;
    private TextView textSeguidores;
    private TextView textSeguindo;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_perfil_amigo );

        // configuracoes iniciais
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child( "usuarios");
        inicializaComponentes();

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

    @Override
    protected void onStart() {
        super.onStart();

        // recursos disponibilizados
        recuperarDadosPerfilAmigo();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // libera recursos
        usuarioAmigoRef.removeEventListener( valueEventListenerPerfilAmigo );
    }

    public void inicializaComponentes() {
        buttonAcaoPerfil = findViewById( R.id.buttonAcaoPerfil );
        buttonAcaoPerfil.setText( "Seguir" );
        imagePerfil = findViewById( R.id.imagePerfil );
        textPublicacoes = findViewById( R.id.textPublicacoes );
        textSeguidores = findViewById( R.id.textSeguidores );
        textSeguindo = findViewById( R.id.textSeguindo );
    }

    public void recuperarDadosPerfilAmigo() {
        usuarioAmigoRef = usuariosRef.child( usuarioSelecionado.getId() );
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Usuario usuario = dataSnapshot.getValue( Usuario.class );
                        String seguidores = String.valueOf( usuario.getSeguidores() );
                        String seguindo = String.valueOf( usuario.getSeguindo() );
                        String postagens = String.valueOf( usuario.getPostagens() );

                        // configura os valores recuperados
                        textPublicacoes.setText( postagens );
                        textSeguidores.setText( seguidores );
                        textSeguindo.setText( seguindo );

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}