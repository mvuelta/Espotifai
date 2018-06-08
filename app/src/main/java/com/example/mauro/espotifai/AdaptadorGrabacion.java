package com.example.mauro.espotifai;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;

public class AdaptadorGrabacion extends RecyclerView.Adapter<AdaptadorGrabacion.GrabacionViewHolder> implements View.OnClickListener {

    private ArrayList<Grabacion> datos;
    private View.OnClickListener listener;
    public static Activity activity;

    public static MediaRecorder mediaRecorder;
    public static MediaPlayer mediaPlayer;

    //Path del archivo de audio
    public static String fichero;
    public static String ficheroPath = Environment.
            getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/";

    private static int quality;

    //Constructor
    public AdaptadorGrabacion(ArrayList<Grabacion> datos, Activity c) {
        this.activity = c;
        this.datos = datos;
    }

    @Override
    public GrabacionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recitem_grabacion, parent, false);

        itemView.setOnClickListener(this);

        GrabacionViewHolder tvh = new GrabacionViewHolder(itemView, this.activity);
        return tvh;
    }

    @Override
    public void onBindViewHolder(GrabacionViewHolder holder, int position) {
        Grabacion item = datos.get(position);

        holder.bindTitular(item);
    }


    @Override
    public int getItemCount() {

        return datos.size();
    }

    /*************Holder q extiende de RecyclerView.ViewHolder***************/
    public static class GrabacionViewHolder extends RecyclerView.ViewHolder{

        private EditText nombre;
        private TextView tiempo;
        private Button rec;
        private Button play;
        private Button stop;

        private static final String LOG_TAG = "Grabadora";

        @TargetApi(Build.VERSION_CODES.M)
        public GrabacionViewHolder(final View itemView, final Activity activity) {
            super(itemView);

            nombre = (EditText)itemView.findViewById(R.id.eNombre);
            tiempo = (TextView)itemView.findViewById(R.id.eTiempo);

            rec = (Button)itemView.findViewById(R.id.btnRec);
            play = (Button)itemView.findViewById(R.id.btnPlay);
            stop = (Button)itemView.findViewById(R.id.btnStop);

            rec.setEnabled(true);
            play.setEnabled(true);
            stop.setEnabled(false);

            /** -------------Inicio que funcion realiza cada boton --------------**/
            rec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nombre.setTextColor(Color.MAGENTA);

                    SharedPreferences pref =
                            PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());

                    //  - Calidad Normal: AMR_NB
                    //  - Mejor Calidad: AMR_WB
                    //  - Alta Calidad: AAC_LC
                    String cal = pref.getString("calidad", "");
                    if (cal.equals("NORMAL")) {
                        quality = MediaRecorder.AudioEncoder.AMR_NB;
                    }
                    if (cal.equals("MEJOR")) {
                        quality = MediaRecorder.AudioEncoder.AMR_WB;
                    }
                    if (cal.equals("ALTA")) {
                        quality = MediaRecorder.AudioEncoder.AAC;
                    }
                    SharedPreferences.Editor editor = pref.edit();
                    editor.apply();

                    fichero = ficheroPath + nombre.getText().toString() + ".3gp";
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setAudioEncoder(quality);
                    mediaRecorder.setOutputFile(fichero);
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        Toast.makeText(v.getContext(), "OK", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Fallo en grabación");
                    } catch (IllegalStateException ise) {
                        //Hacer algo.
                    }

                    rec.setEnabled(false);
                    stop.setEnabled(true);
                    play.setEnabled(false);
                }
            });

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer = new MediaPlayer();
                    fichero = ficheroPath + nombre.getText() + ".3gp";
                    try {
                        mediaPlayer.setDataSource(fichero);
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                    try {
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Toast.makeText(v.getContext(), "Reproduciendo...", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        //Toast.makeText(v.getContext(), "No hay archivo de audio", Toast.LENGTH_SHORT).show();
                        //e.printStackTrace();
                    } catch (IllegalStateException e) {
                        Toast.makeText(v.getContext(), "No hay archivo de audio", Toast.LENGTH_SHORT).show();
                        //e.printStackTrace();
                    }

                    rec.setEnabled(true);
                    play.setEnabled(true);
                    stop.setEnabled(false);
                }
            });

            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mediaRecorder.stop();
                    mediaRecorder.release();

                    rec.setEnabled(true);
                    stop.setEnabled(false);
                    play.setEnabled(true);

                    Toast.makeText(v.getContext(), "Grabacion EXITOSA!", Toast.LENGTH_SHORT).show();

                }
            });

            /**---------------- Fin de los botones ---------------------**/

        //Falta pedir los permisos


        }

        public void bindTitular(Grabacion t) {
            nombre.setText(t.getNombre());
            tiempo.setText(String.valueOf(t.getTiempo()));

        }

    }
    /********************************* Fin del Holder**********************************************/



    //*********** Click del item ********************//

    @Override
    public void onClick(View v) {
        if (listener != null)
            listener.onClick(v);

    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
    //************** Fin click del item *********************//

    /******************** Clase para los permisos. Dentro del Adaptador *************/
/*

    public static class RequesterPermissions extends AppCompatActivity {
        private final static String TAG = "RequesterPermissions";
        private Activity activity;

        RequesterPermissions(Activity activity) {
            this.activity = activity;
        }

        boolean checkIfPermissionIsGranted(String permission) {
            boolean Return = false;
            int permissionChecked = ContextCompat.checkSelfPermission(activity, permission);
            if (permissionChecked == PackageManager.PERMISSION_GRANTED) {
                // Permission already granted
                Return = true;
            } else if (permissionChecked == PackageManager.PERMISSION_DENIED) {
                // Permission denied
                Return = false;
            }
            return Return;
        }

        void requestForPermission(String[] permission) {
            // Open dialog to grant or denied permission
            ActivityCompat.requestPermissions(this.activity, permission, REQUEST_CODE_MIC);
            ActivityCompat.requestPermissions(this.activity, permission, REQUEST_CODE_SD);
        }

        //Opcion 1
        /////////// Cuando pido permisos (requestForPermission) vengo aca, pregunto pregunto cual permiso "interrumpio"
        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            switch (requestCode) {
                case REQUEST_CODE_MIC:
                    //RequesterPermissions per = new RequesterPermissions(activity);
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //Permiso de MICROFONO concedido
                //        boolean sdPermission = per.checkIfPermissionIsGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        //int sdPermission = ContextCompat.checkSelfPermission(activity Manifest.permission.WRITE_EXTERNAL_STORAGE);
                 //       if (sdPermission != true) {    //Si no consulte, pido permiso para grabar SD
                            //ActivityCompat.requestPermissions(activity,
                                    // String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_SD);
                //            per.requestForPermission(permissions);
                            Toast.makeText(activity, "Permiso MIC Concedido!", Toast.LENGTH_SHORT).show();
                        return;
                        //}
                    } else {
                        //Permiso denegado
                        Toast.makeText(activity, "Permiso MIC Denegado!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case REQUEST_CODE_SD:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Si el permiso de la SD fue concedido comienzo a grabar...
                        Toast.makeText(activity, "Permiso otorgado", Toast.LENGTH_SHORT).show();
                        */
/*                      mediaRecorder = new MediaRecorder();
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

                        //-Calidad Normal: AMR_NB - Mejor Calidad: AMR_WB - Alta Calidad: AAC_LC
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
                        mediaRecorder.setOutputFile(fichero);

                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IOException e) {
                            //Log.e(LOG_TAG, "Fallo en grabación");
                        } catch (IllegalStateException ise) {
                            //Hacer algo.
                        }
*//*

                    }
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }

        }


    }
*/

    /****************************** Fin de la clase permisos ************************************/


}