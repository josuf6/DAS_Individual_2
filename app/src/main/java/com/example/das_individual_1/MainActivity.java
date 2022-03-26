package com.example.das_individual_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ListView continentes;
    String[] nombres = {"África", "América", "Asia", "Europa", "Oceanía"};
    int[] imagenes = {R.drawable.africa, R.drawable.america, R.drawable.asia, R.drawable.europa, R.drawable.oceania};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        continentes = (ListView) findViewById(R.id.continentes); //crea el ListView en el que se mostrarán continentes con iconos y nombres
        AdaptadorListView adaptadorLV = new AdaptadorListView(getApplicationContext(), nombres, imagenes); //Al ser un ListView personalizado se utiliza un objeto adaptador
        continentes.setAdapter(adaptadorLV); //se relaciona el adaptador con el ListView
    }
}