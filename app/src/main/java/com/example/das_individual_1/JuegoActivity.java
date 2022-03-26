package com.example.das_individual_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class JuegoActivity extends AppCompatActivity implements SalirJuegoDialog.ListenerDialogoSalirJuego {

    private String continente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        //guardamos la información pasada desde el menu
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            continente = extras.getString("continente");
        }
    }

    public void onClickSalir(View v) { //Mostrar diálogo al pulsar botón "Salir"
        DialogFragment dialogSalirJuego = new SalirJuegoDialog();
        dialogSalirJuego.show(getSupportFragmentManager(), "dialogSalirJuego");
    }

    @Override
    public void onBackPressed() { //Mostrar diálogo al pulsar botón back
        DialogFragment dialogSalirJuego = new SalirJuegoDialog();
        dialogSalirJuego.show(getSupportFragmentManager(), "dialogSalirJuego");
    }

    @Override
    public void onClickSi() { //Al pulsar "Sí" matar la actividad (vuelve al menú principal porque es la última actividad en la pila)
        finish();
    }
}