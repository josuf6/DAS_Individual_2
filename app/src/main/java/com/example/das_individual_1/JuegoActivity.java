package com.example.das_individual_1;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.common.util.ArrayUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class JuegoActivity extends AppCompatActivity implements SalirJuegoDialog.ListenerDialogoSalirJuego {

    private PaisesBD gestorPaisesDB;
    private String continente;
    private ArrayList<String> acertados = new ArrayList<>();
    private ArrayList<String> siguientePais;
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private Button btnJugar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        btn_1 = (Button) findViewById(R.id.opcion_1_btn);
        btn_2 = (Button) findViewById(R.id.opcion_2_btn);
        btn_3 = (Button) findViewById(R.id.opcion_3_btn);
        btnJugar = (Button) findViewById(R.id.jugar_btn);

        //guardamos la información pasada desde el menu
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            continente = extras.getString("continente");
        }

        gestorPaisesDB = new PaisesBD(this);
        this.siguienteBandera();
    }

    public void onClickSalir(View v) { //Mostrar diálogo al pulsar botón "Salir"
        DialogFragment dialogSalirJuego = new SalirJuegoDialog();
        dialogSalirJuego.show(getSupportFragmentManager(), "dialogSalirJuego");
    }

    public void onClickOpcion(View v) throws InterruptedException { //Mostrar diálogo al pulsar botón "Salir"
        Button btnOpcion = (Button) findViewById(v.getId());

        if (btnOpcion.getText().toString() == siguientePais.get(0)) {
            if (btnOpcion == btn_1) {
                btn_1.setBackgroundColor(Color.GREEN);
            } else if (btnOpcion == btn_2) {
                btn_2.setBackgroundColor(Color.GREEN);
            } else if (btnOpcion == btn_3) {
                btn_3.setBackgroundColor(Color.GREEN);
            }
            acertados.add(btnOpcion.getText().toString());
            btn_1.setEnabled(false);
            btn_2.setEnabled(false);
            btn_3.setEnabled(false);

            //TODO poner timer 1s y que funcione lo de cambiar de color

            this.siguienteBandera();
        } else {
            if (btnOpcion == btn_1) {
                btn_1.setBackgroundColor(Color.RED);
                if (btn_2.getText().toString() == siguientePais.get(0)) {
                    btn_2.setBackgroundColor(Color.GREEN);
                } else if (btn_3.getText().toString() == siguientePais.get(0)) {
                    btn_3.setBackgroundColor(Color.GREEN);
                }
            } else if (btnOpcion == btn_2) {
                btn_2.setBackgroundColor(Color.RED);
                if (btn_1.getText().toString() == siguientePais.get(0)) {
                    btn_1.setBackgroundColor(Color.GREEN);
                } else if (btn_3.getText().toString() == siguientePais.get(0)) {
                    btn_3.setBackgroundColor(Color.GREEN);
                }
            } else if (btnOpcion == btn_3) {
                btn_3.setBackgroundColor(Color.RED);
                if (btn_1.getText().toString() == siguientePais.get(0)) {
                    btn_1.setBackgroundColor(Color.GREEN);
                } else if (btn_2.getText().toString() == siguientePais.get(0)) {
                    btn_2.setBackgroundColor(Color.GREEN);
                }
            }
            btnJugar.setVisibility(View.VISIBLE);
        }
    }

    public void onClickJugar(View v) throws InterruptedException {
        acertados = new ArrayList<>();
        btnJugar.setVisibility(View.INVISIBLE);
        this.siguienteBandera();
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

    private void siguienteBandera() { //Pone en la pantalla la siguiente bandera que hay que acertar (y las opciones en los botones)
        siguientePais = gestorPaisesDB.getSiguienteBandera(continente, acertados); //Obtener siguiente bandera
        if (siguientePais != null) { //Si no es null seguir adelante
            btn_1.setBackgroundColor(Color.parseColor("#6200EE"));
            btn_2.setBackgroundColor(Color.parseColor("#6200EE"));
            btn_3.setBackgroundColor(Color.parseColor("#6200EE"));
            btn_1.setEnabled(true);
            btn_2.setEnabled(true);
            btn_3.setEnabled(true);

            //Poner imagen de la bandera en la pantalla
            int resId = this.getResources().getIdentifier(siguientePais.get(1), "drawable", this.getPackageName());
            ImageView img = (ImageView) findViewById(R.id.bandera);
            img.setImageResource(resId);

            //Obtener países de las respuestas erróneas
            ArrayList<String> paisesAleatorios = gestorPaisesDB.getPaisesAleatorios(continente, siguientePais.get(0));

            //Proceso para ordenar opciones de manera aleatoria
            ArrayList<Integer> numeros = new ArrayList<>();
            while (numeros.size() < 3) {
                int numero = (int)(Math.random() * 3 + 1);
                if (!numeros.contains(numero)) {
                    numeros.add(numero);
                }
            }

            //Poner texto de las respuestas en los botones
            if (numeros.get(0) == 1) {
                btn_1.setText(siguientePais.get(0));
            } else if (numeros.get(0) == 2) {
                btn_2.setText(siguientePais.get(0));
            } else if (numeros.get(0) == 3) {
                btn_3.setText(siguientePais.get(0));
            }

            if (numeros.get(1) == 1) {
                btn_1.setText(paisesAleatorios.get(0));
            } else if (numeros.get(1) == 2) {
                btn_2.setText(paisesAleatorios.get(0));
            } else if (numeros.get(1) == 3) {
                btn_3.setText(paisesAleatorios.get(0));
            }

            if (numeros.get(2) == 1) {
                btn_1.setText(paisesAleatorios.get(1));
            } else if (numeros.get(2) == 2) {
                btn_2.setText(paisesAleatorios.get(1));
            } else if (numeros.get(2) == 3) {
                btn_3.setText(paisesAleatorios.get(1));
            }
        } else { //Si es null se ha terminado la partida
            this.terminarPartida();
        }
    }

    private void terminarPartida() {
        //TODO
        //TODO
        //TODO
        //TODO
        //TODO
        //TODO
        //TODO
    }
}