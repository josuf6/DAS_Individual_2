package com.example.das_individual_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class JuegoActivity extends AppCompatActivity {

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
}