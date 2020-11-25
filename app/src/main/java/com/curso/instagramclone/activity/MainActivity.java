package com.curso.instagramclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.curso.instagramclone.R;
import com.curso.instagramclone.fragment.FeedFragment;
import com.curso.instagramclone.fragment.PerfilFragment;
import com.curso.instagramclone.fragment.PesquisaFragment;
import com.curso.instagramclone.fragment.PostagemFragment;
import com.curso.instagramclone.helper.ConfiguracaoFirebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // configura a toolbar
        Toolbar toolbar = findViewById( R.id.toolbarPrincipal );
        toolbar.setTitle( "Instagram" );
        setSupportActionBar( toolbar );

        // configuracao inicial de objetos
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        // configura bottom navigation view
        configuraBottomNavigationView();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace( R.id.viewPager, new FeedFragment()).commit();
    }

    public void configuraBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById( R.id.bottomNavigation );

        // configuracoes iniciais
        bottomNavigationViewEx.enableAnimation( true );
        bottomNavigationViewEx.enableItemShiftingMode( false );
        bottomNavigationViewEx.enableShiftingMode( false );
        bottomNavigationViewEx.setTextVisibility( false );

        // habilitar a navegacao
        habilitarNavegacao( bottomNavigationViewEx );

        // configura item selecionado inicialmente
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0 ); // define Home como padrao
        menuItem.setChecked( true );
    }

    public void habilitarNavegacao( BottomNavigationViewEx viewEx ) {
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( @NonNull MenuItem item ) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch( item.getItemId()) {
                    case R.id.ic_home :
                        fragmentTransaction.replace( R.id.viewPager, new FeedFragment()).commit();
                        return true;
                    case R.id.ic_pesquisa :
                        fragmentTransaction.replace( R.id.viewPager, new PesquisaFragment()).commit();
                        return true;
                    case R.id.ic_postagem :
                        fragmentTransaction.replace( R.id.viewPager, new PostagemFragment()).commit();
                        return true;
                    case R.id.ic_perfil :
                        fragmentTransaction.replace( R.id.viewPager, new PerfilFragment()).commit();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.menu_main, menu );

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch( item.getItemId() ) {
            case R.id.menu_sair :
                deslogarUsuario();
                startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deslogarUsuario() {
        try {
            autenticacao.signOut();
        } catch( Exception e) {
            e.printStackTrace();
        }
    }
}