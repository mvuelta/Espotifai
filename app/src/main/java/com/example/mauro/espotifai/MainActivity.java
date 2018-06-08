package com.example.mauro.espotifai;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recView;
    private ArrayList<Grabacion> datos;
    private Toolbar toolbar;

    //Codigos de permisos
    final int REQUEST_CODE_MIC = 123;
    final int REQUEST_CODE_SD = 321;

    //Path del archivo de audio
    public static String fichero;
    public static String ficheroPath = Environment.
            getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            //Appbar
            toolbar = (Toolbar) findViewById(R.id.appbar);
            setSupportActionBar(toolbar);

            //inicialización de la lista de datos de ejemplo
            datos = new ArrayList<>();
            for(int i=0; i<20; i++)
                datos.add(new Grabacion("Título " + i,  i));

            //Inicialización RecyclerView
            recView = (RecyclerView) findViewById(R.id.RecView);
            recView.setHasFixedSize(true);

            final AdaptadorGrabacion adaptador = new AdaptadorGrabacion(datos,MainActivity.this);

            adaptador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this,
                            "Elemento PRESIONADO -> " + recView.getChildAdapterPosition(v),Toast.LENGTH_SHORT).show();
                }
            });

            recView.setAdapter(adaptador);

            recView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            recView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /***********************Menu Toolbar*****************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_nuevo:
                int micPermission = checkSelfPermission(Manifest.permission.RECORD_AUDIO);  //Recibo el estado del permiso
                if(micPermission != PackageManager.PERMISSION_GRANTED) {    //Pregunto por el estado
                    requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_MIC); //Pido al usuario permiso
                    //return;
                } else {

                    Grabacion aux = new Grabacion("Archivo", 5);
                    datos.add(aux);

                    final AdaptadorGrabacion adaptador = new AdaptadorGrabacion(datos, MainActivity.this);
                    recView.setAdapter(adaptador);
                    Toast.makeText(MainActivity.this, "Item INGRESADO", Toast.LENGTH_SHORT);
                    return true;
                }
                break;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, PreferenciasActivity.class));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /*********************Fin Menu Toolbar********************************/


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_MIC:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permiso de MICROFONO concedido
                    int sdPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (sdPermission != PackageManager.PERMISSION_GRANTED) {    //Si no consulte, pido permiso para grabar SD
                        requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_SD);
                        return;
                    }
                } else {
                    //Permiso denegado
                    Toast.makeText(MainActivity.this, "Permiso MIC Denegado!", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_SD:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Si el permiso de la SD fue concedido comienzo a grabar...
            /*        mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
                    mediaRecorder.setOutputFile(fichero);

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        //Log.e(LOG_TAG, "Fallo en grabación");
                    } catch (IllegalStateException ise) {
                        //Hacer algo.
                    }*/
                    Toast.makeText(MainActivity.this, "Permisos CONCEDIDOS!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
