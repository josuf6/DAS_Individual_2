package com.example.das_individual_1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.das_individual_1.R;
import com.example.das_individual_1.workers.ObtenerRecordsDB;

import java.util.Arrays;

public class MenuActivity extends AppCompatActivity  implements SalirAppDialog.ListenerDialogoSalirApp {

    private String usuario;
    private ListView continentes;
    private String[] nombres = {"África", "América", "Asia", "Europa", "Oceanía", "Global"};
    private String[] records = new String[6];
    private int[] imagenes = {R.drawable.africa, R.drawable.america, R.drawable.asia, R.drawable.europa, R.drawable.oceania, R.drawable.global};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //guardamos el usuario con el que se ha iniciado sesión
        if (this.usuario == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                this.usuario = extras.getString("usuario");
            }
        }

        this.obtenerRecords(); //método usado para almacenar la información de los records en la variable "records"
    }

    private void obtenerRecords() {

        //Se obtiene la info de los records del usuario para añadir al ListView
        Data datos = new Data.Builder()
                .putString("usuario", this.usuario)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ObtenerRecordsDB.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            if (workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {
                                if (workInfo.getOutputData().getString("datos").equals("error")) {
                                    Arrays.fill(records, "null");
                                } else {
                                    String resultado = workInfo.getOutputData().getString("datos");
                                    records = resultado.split(",");

                                    continentes = (ListView) findViewById(R.id.continentes); //Crea el ListView en el que se mostrarán continentes con iconos y nombres
                                    AdaptadorListView adaptadorLV = new AdaptadorListView(getApplicationContext(), nombres, records, imagenes); //Al ser un ListView personalizado se utiliza un objeto adaptador
                                    continentes.setAdapter(adaptadorLV); //Se relaciona el adaptador con el ListView

                                    continentesLVListener(); //Configurar listener del ListView "continentes"
                                }
                            } else {
                                Arrays.fill(records, "null");
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void continentesLVListener() {
        //Listener del ListView "continentes"
        //Dependiendo de la fila que se pulse llamará a la actividad del juego con la información del continente seleccionado
        this.continentes.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(MenuActivity.this, JuegoActivity.class);
            intent.putExtra("continente", ((TextView) view.findViewById(R.id.nombre_continente)).getText().toString());
            intent.putExtra("usuario", this.usuario);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() { //Mostrar diálogo al pulsar botón back
        DialogFragment dialogSalirApp = new SalirAppDialog();
        dialogSalirApp.show(getSupportFragmentManager(), "dialogSalirApp");
    }

    @Override
    public void onClickSalirApp() { //Al pulsar "Sí" matar la actividad (Salir de la aplicación, porque no hay ninguna actividad más en la pila)
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) { //para guardar el estado de la actividad (valores de variables y estados de objetos)
        super.onSaveInstanceState(savedInstanceState);

        //Guardar info
        savedInstanceState.putString("usuario", this.usuario);
        savedInstanceState.putStringArray("records", this.records);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) { //para recuperar el estado de la actividad (valores de variables y estados de objetos)
        super.onRestoreInstanceState(savedInstanceState);

        //Restaurar info
        this.usuario = savedInstanceState.getString("usuario");
        this.records = savedInstanceState.getStringArray("records");
    }
}