package com.example.das_individual_1.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.das_individual_1.R;
import com.example.das_individual_1.workers.SubirImagenDB;

import java.io.File;
import java.io.IOException;

public class NuevoRecordActivity extends AppCompatActivity {

    private String usuario;
    private String continente;
    private int puntuacion;
    private ImageView imagen;
    private Button subir_btn;
    private Button sacar_foto_btn;
    private String nombrefich;
    private Uri uriimagen;
    private boolean fotoHecha;
    private int subir_btn_enabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_record);

        //guardar los datos pasados desde la actividad del juego
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
            continente = extras.getString("continente");
            puntuacion = extras.getInt("puntuacion");
        }

        imagen = (ImageView) findViewById(R.id.imagen);
        subir_btn = (Button) findViewById(R.id.subir_btn);
        sacar_foto_btn = (Button) findViewById(R.id.sacar_foto_btn);

        //estado para guardar info de atributo enabled de subir_btn
        subir_btn_enabled = 1;

        if (!fotoHecha) {
            imagen.setImageResource(R.drawable.camara);
        }
    }

    private void ponerImagen() {
        nombrefich = usuario + "_" + continente + "_" + puntuacion;
        File directorio = this.getFilesDir();
        File fichImg = null;
        uriimagen = null;
        try {
            fichImg = File.createTempFile(nombrefich, ".jpg", directorio);
            uriimagen = FileProvider.getUriForFile(this, "com.example.das_individual_1.provider", fichImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent elIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        elIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriimagen);
        startActivityForResult(elIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            imagen.setImageURI(uriimagen);
            fotoHecha = true;
            sacar_foto_btn.setVisibility(View.INVISIBLE);
            subir_btn.setVisibility(View.VISIBLE);
        }
    }

    public void onClickSacarFoto(View v) {
        this.ponerImagen();
    }

    public void onClickSubir(View v) { //ocClick del botón "Volver a jugar"
        subir_btn.setEnabled(false);
        subir_btn_enabled = 0;

        Data datos = new Data.Builder()
                .putString("nombrefich",nombrefich)
                .putString("imagen",uriimagen.toString())
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(SubirImagenDB.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            if (workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {
                                if (workInfo.getOutputData().getString("datos").equals("subida")) {
                                    Toast toast= Toast.makeText(getApplicationContext(),"Imagen subida correctamente",Toast.LENGTH_SHORT);
                                    toast.show();
                                } else {
                                    Toast toast= Toast.makeText(getApplicationContext(),"No se ha podido subir la imagen",Toast.LENGTH_SHORT);
                                    toast.show();
                                    subir_btn.setEnabled(true);
                                    subir_btn_enabled = 1;
                                }
                            } else {
                                Toast toast= Toast.makeText(getApplicationContext(),"Algo ha ido mal :(",Toast.LENGTH_SHORT);
                                toast.show();
                                subir_btn.setEnabled(true);
                                subir_btn_enabled = 1;
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) { //para guardar el estado de la actividad (valores de variables y estados de objetos)
        super.onSaveInstanceState(savedInstanceState);

        //Guardar
        savedInstanceState.putString("usuario", usuario);
        savedInstanceState.putString("continente", continente);
        savedInstanceState.putInt("puntuacion", puntuacion);
        savedInstanceState.putString("nombrefich", nombrefich);
        if (uriimagen == null) {
            savedInstanceState.putString("uriimagen", null);
        } else {
            savedInstanceState.putString("uriimagen", uriimagen.toString());
        }
        savedInstanceState.putInt("subir_btn_enabled", subir_btn_enabled);
        savedInstanceState.putBoolean("fotoHecha", fotoHecha);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) { //para recuperar el estado de la actividad (valores de variables y estados de objetos)
        super.onRestoreInstanceState(savedInstanceState);

        //Restaurar info
        usuario = savedInstanceState.getString("usuario");
        continente = savedInstanceState.getString("continente");
        puntuacion = savedInstanceState.getInt("puntuacion");
        nombrefich = savedInstanceState.getString("nombrefich");
        String uriString = savedInstanceState.getString("uriimagen");
        if (uriString != null) {
            uriimagen = Uri.parse(uriString);
        }
        subir_btn_enabled = savedInstanceState.getInt("subir_btn_enabled");
        fotoHecha = savedInstanceState.getBoolean("fotoHecha");
        imagen = (ImageView) findViewById(R.id.imagen);

        if (fotoHecha) {
            imagen.setImageURI(uriimagen);
            sacar_foto_btn.setVisibility(View.INVISIBLE);
            subir_btn.setVisibility(View.VISIBLE);

            if (subir_btn_enabled == 0) {
                subir_btn.setEnabled(false);
            } else if (subir_btn_enabled == 1) {
                subir_btn.setEnabled(true);
            }
        } else {
            sacar_foto_btn.setVisibility(View.VISIBLE);
            subir_btn.setVisibility(View.INVISIBLE);
        }
    }
}