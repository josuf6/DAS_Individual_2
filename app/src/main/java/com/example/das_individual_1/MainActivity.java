package com.example.das_individual_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity  implements SalirAppDialog.ListenerDialogoSalirApp {

    ListView continentes;
    String[] nombres = {"África", "América", "Asia", "Europa", "Oceanía"};
    int[] imagenes = {R.drawable.africa, R.drawable.america, R.drawable.asia, R.drawable.europa, R.drawable.oceania};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        continentes = (ListView) findViewById(R.id.continentes); //Crea el ListView en el que se mostrarán continentes con iconos y nombres
        AdaptadorListView adaptadorLV = new AdaptadorListView(getApplicationContext(), nombres, imagenes); //Al ser un ListView personalizado se utiliza un objeto adaptador
        continentes.setAdapter(adaptadorLV); //Se relaciona el adaptador con el ListView

        this.continentesLVListener(); //Configurar listener del ListView "continentes"
    }

    private void continentesLVListener() {
        //Listener del ListView "continentes"
        //Dependiendo de la fila que se pulse llamará a la actividad del juego con la información del continente seleccionado
        this.continentes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, JuegoActivity.class);
                intent.putExtra("continente", ((TextView)view.findViewById(R.id.nombre_continente)).getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() { //Mostrar diálogo al pulsar botón back
        DialogFragment dialogSalirApp = new SalirAppDialog();
        dialogSalirApp.show(getSupportFragmentManager(), "dialogSalirApp");
    }

    @Override
    public void onClickSi() { //Al pulsar "Sí" matar la actividad (Salir de la aplicación, porque no hay ninguna actividad más en la pila)
        finish();
    }
}