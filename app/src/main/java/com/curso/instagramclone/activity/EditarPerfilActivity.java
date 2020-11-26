package com.curso.instagramclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.curso.instagramclone.R;

public class EditarPerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_editar_perfil );

        // configura a toolbar
        Toolbar toolbar = findViewById( R.id.toolbarPrincipal );
        toolbar.setTitle( "Editar perfil" );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_close_black_24dp );
    }
}