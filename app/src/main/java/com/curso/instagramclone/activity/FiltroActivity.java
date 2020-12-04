package com.curso.instagramclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.curso.instagramclone.R;
import com.curso.instagramclone.adapter.AdapterMiniaturas;
import com.curso.instagramclone.helper.RecyclerItemClickListener;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

// https://github.com/ravi8x/AndroidPhotoFilters
public class FiltroActivity extends AppCompatActivity {
    // carrega a biblioteca de filtros
    static
    {

        System.loadLibrary("NativeImageProcessor" );
    }

    private ImageView imageFotoEscolhida;
    private Bitmap imagem;
    private Bitmap imagemFiltro;
    private List<ThumbnailItem> listaFiltros;
    private RecyclerView recyclerFiltros;
    private AdapterMiniaturas adapterMiniaturas;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_filtro );

        // inicializar componentes
        imageFotoEscolhida = findViewById( R.id.imageFotoEscolhida );
        recyclerFiltros = findViewById( R.id.recyclerFiltros );

        // configuracoes iniciais
        listaFiltros = new ArrayList<>();

        // configura a toolbar
        Toolbar toolbar = findViewById( R.id.toolbarPrincipal );
        toolbar.setTitle( "Filtros" );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_close_black_24dp );

        // recupera a imagem escolhida
        Bundle bundle = getIntent().getExtras();
        if( bundle != null ) {
            byte[] dadosImagem = bundle.getByteArray("fotoEscolhida" );
            imagem = BitmapFactory.decodeByteArray( dadosImagem,0, dadosImagem.length );
            imageFotoEscolhida.setImageBitmap( imagem );

            // configura recyclerview de filtros
            adapterMiniaturas = new AdapterMiniaturas( listaFiltros, getApplicationContext() );
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false );
            recyclerFiltros.setLayoutManager( layoutManager );
            recyclerFiltros.setAdapter( adapterMiniaturas );

            // adiciona evento de clique no recyclerview
            recyclerFiltros.addOnItemTouchListener( new RecyclerItemClickListener(
                    getApplicationContext(),
                    recyclerFiltros,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick( View view, int position ) {
                            ThumbnailItem item = listaFiltros.get( position );

                            imagemFiltro = imagem.copy( imagem.getConfig(),true );
                            Filter filtro = item.filter;
                            imageFotoEscolhida.setImageBitmap( filtro.processFilter( imagemFiltro ) );
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {  }

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  }
                    }
            ));

            // recupera filtros
            recuperarFiltros();
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate( R.menu.menu_filtro, menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {
        switch( item.getItemId() ) {
            case R.id.ic_salvar_postagem :

                break;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void recuperarFiltros() {
        // limpar itens
        ThumbnailsManager.clearThumbs();
        listaFiltros.clear();

        // configura filtro normal
        ThumbnailItem item = new ThumbnailItem();
        item.image = imagem;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb( item );

        // lista todos os filtros
        List<Filter> filtros = FilterPack.getFilterPack( getApplicationContext() );

        for( Filter filtro: filtros ) {
            ThumbnailItem itemFiltro = new ThumbnailItem();

            itemFiltro.image = imagem;
            itemFiltro.filter = filtro;
            itemFiltro.filterName = filtro.getName();

            ThumbnailsManager.addThumb( itemFiltro );
        }

        listaFiltros.addAll( ThumbnailsManager.processThumbs( getApplicationContext() ) );
        adapterMiniaturas.notifyDataSetChanged();
    }
}