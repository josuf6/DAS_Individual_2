package com.example.das_individual_1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class MenuActivity extends AppCompatActivity  implements SalirAppDialog.ListenerDialogoSalirApp {

    ListView continentes;
    String[] nombres = {"África", "América", "Asia", "Europa", "Oceanía", "Global"};
    int[] imagenes = {R.drawable.africa, R.drawable.america, R.drawable.asia, R.drawable.europa, R.drawable.oceania, R.drawable.global};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        continentes = (ListView) findViewById(R.id.continentes); //Crea el ListView en el que se mostrarán continentes con iconos y nombres
        AdaptadorListView adaptadorLV = new AdaptadorListView(getApplicationContext(), nombres, imagenes); //Al ser un ListView personalizado se utiliza un objeto adaptador
        continentes.setAdapter(adaptadorLV); //Se relaciona el adaptador con el ListView

        this.continentesLVListener(); //Configurar listener del ListView "continentes"
    }

    private void continentesLVListener() {
        //Listener del ListView "continentes"
        //Dependiendo de la fila que se pulse llamará a la actividad del juego con la información del continente seleccionado
        this.continentes.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(MenuActivity.this, JuegoActivity.class);
            intent.putExtra("continente", ((TextView)view.findViewById(R.id.nombre_continente)).getText().toString());
            startActivity(intent);
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