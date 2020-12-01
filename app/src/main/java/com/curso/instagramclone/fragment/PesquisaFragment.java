package com.curso.instagramclone.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.curso.instagramclone.R;
import com.curso.instagramclone.activity.PerfilAmigoActivity;
import com.curso.instagramclone.adapter.AdapterPesquisa;
import com.curso.instagramclone.helper.ConfiguracaoFirebase;
import com.curso.instagramclone.helper.RecyclerItemClickListener;
import com.curso.instagramclone.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PesquisaFragment extends Fragment {
    private SearchView searchViewPesquisa;
    private RecyclerView recyclerViewPesquisa;
    private List<Usuario> listaUsuarios;
    private DatabaseReference usuariosRef;
    private AdapterPesquisa adapterPesquisa;

    public PesquisaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        // configuracoes iniciais
        searchViewPesquisa = view.findViewById( R.id.searchViewPesquisa );
        recyclerViewPesquisa = view.findViewById( R.id.recyclerViewPesquisa );
        listaUsuarios = new ArrayList<>();
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child( "usuarios" );
        adapterPesquisa = new AdapterPesquisa( listaUsuarios, getActivity() );

        // configura o reciclerview
        recyclerViewPesquisa.setHasFixedSize( true );
        recyclerViewPesquisa.setLayoutManager( new LinearLayoutManager( getActivity()) );
        recyclerViewPesquisa.setAdapter( adapterPesquisa );

        // configura evento de clique
        recyclerViewPesquisa.addOnItemTouchListener( new RecyclerItemClickListener(
                getActivity(),
                recyclerViewPesquisa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick( View view, int position ) {
                        Usuario usuarioSelecionado = listaUsuarios.get( position );
                        Intent i = new Intent( getActivity(), PerfilAmigoActivity.class );
                        /* ocorre um erro ao declarar a linha abaixo devido a necessidade de uma
                         * interface para passar dados de uma Activity para outra Activity.
                         * Para solucionar, basta implementar a Interface Serializable na classe
                         * Usuario.
                         */
                        i.putExtra("usuarioSelecionado", usuarioSelecionado );
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick( View view, int position ) {

                    }

                    @Override
                    public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {

                    }
                }
        ));

        // configura a searchview
        searchViewPesquisa.setQueryHint( "Buscar usuários" );
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toUpperCase();

                pesquisarUsuarios( textoDigitado );
                return true;
            }
        });
        return view;
    }

    public void pesquisarUsuarios( String texto ) {
        // limpa a lista
        listaUsuarios.clear();

        // executa a pesquisa caso haja texto digitado
        if( texto.length() >= 2 ) {
            Query query = usuariosRef.orderByChild( "nome" )
                    .startAt( texto )
                    .endAt( texto + "\uf8ff" );
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // limpa a lista
                    listaUsuarios.clear();

                    for( DataSnapshot ds : dataSnapshot.getChildren() ) {
                        listaUsuarios.add( ds.getValue( Usuario.class ) );
                    }

                    adapterPesquisa.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}