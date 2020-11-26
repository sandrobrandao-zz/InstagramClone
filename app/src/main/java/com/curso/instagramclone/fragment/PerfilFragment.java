package com.curso.instagramclone.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.curso.instagramclone.R;
import com.curso.instagramclone.activity.EditarPerfilActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {
    private ProgressBar progressBar;
    private CircleImageView imagePerfil;
    private TextView textPublicacoes;
    private TextView textSeguidores;
    private TextView textSeguindo;
    private Button buttonEditarPerfil;
    private GridView gridViewPerfil;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // configuracoes dos componentes
        progressBar = view.findViewById( R.id.progressBarPerfil );
        imagePerfil = view.findViewById( R.id.imagePerfil );
        textPublicacoes = view.findViewById( R.id.textPublicacoes );
        textSeguidores = view.findViewById( R.id.textSeguidores );
        textSeguindo = view.findViewById( R.id.textSeguindo );
        buttonEditarPerfil = view.findViewById( R.id.buttonEditarPerfil );
        gridViewPerfil = view.findViewById( R.id.gridViewPerfil );

        // abre a edicao do perfil
        buttonEditarPerfil.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent i = new Intent( getActivity(), EditarPerfilActivity.class );
                startActivity(i);
            }
        });

        return view;
    }
}