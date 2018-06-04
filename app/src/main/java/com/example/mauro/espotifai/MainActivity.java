package com.example.mauro.espotifai;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public Toolbar toolbar;

    private RecyclerView recView;
    private ArrayList<Grabacion> datos;
    private AdaptadorGrabacion.GrabacionViewHolder pedo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            //Appbar
            //toolbar = (Toolbar) findViewById(R.id.appbar);
            //setSupportActionBar(toolbar);

            //inicialización de la lista de datos de ejemplo
            datos = new ArrayList<>();
            for(int i=0; i<50; i++)
                datos.add(new Grabacion("Título " + i,  i));

            //Inicialización RecyclerView
            recView = (RecyclerView) findViewById(R.id.RecView);
            recView.setHasFixedSize(true);

            final AdaptadorGrabacion adaptador = new AdaptadorGrabacion(datos,MainActivity.this);

            adaptador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this,"Elemento PRESIONADO -> " + recView.getChildAdapterPosition(v),Toast.LENGTH_LONG).show();
                }
            });

            recView.setAdapter(adaptador);

            recView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            recView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

}
