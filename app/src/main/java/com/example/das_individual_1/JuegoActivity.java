package com.example.das_individual_1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class JuegoActivity extends AppCompatActivity implements SalirJuegoDialog.ListenerDialogoSalirJuego {

    private PaisesBD gestorPaisesDB;
    private String continente;
    private ArrayList<String> acertados = new ArrayList<>();
    private ArrayList<String> siguientePais;
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private int btn_1Estado;
    private int btn_2Estado;
    private int btn_3Estado;
    private int btnJugarEstado;
    private int btnSiguienteEstado;
    private Button btnJugar;
    private Button btnSiguiente;
    private Boolean opcion;
    private Boolean restored;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        btn_1 = (Button) findViewById(R.id.opcion_1_btn);
        btn_2 = (Button) findViewById(R.id.opcion_2_btn);
        btn_3 = (Button) findViewById(R.id.opcion_3_btn);

        //Se utilizan estados de los botones para saber en qué estado se encontraban cuando se interrumpe la actividad
        btn_1Estado = 0;
        btn_2Estado = 0;
        btn_3Estado = 0;

        btnJugar = (Button) findViewById(R.id.jugar_btn);
        btnSiguiente = (Button) findViewById(R.id.siguiente_btn);
        opcion = false; //este flag se utiliza para anular el funcionamiento cuando se ha seleccionado alguna opción (se vuelve a habilitar al pulsar "Siguiente")

        //guardamos la información pasada desde el menú
        if (continente == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                continente = extras.getString("continente");
            }
        }

        gestorPaisesDB = new PaisesBD(this); //base de datos de países, continentes de los países, y nombres de archivos de las imágenes de las banderas

        if (this.restored == null) { //flag utilizado cuando se interrumpe la actividad (por una llamada, al salir de ella, ...)
            this.restored = false;
        }
        if (!this.restored) { //se ejecuta si no se ha interrumpido la actividad
            this.siguienteBandera();
        }
    }

    public void onClickOpcion(View v) { //Mostrar diálogo al pulsar botón "Salir"
        Button btnOpcion = (Button) findViewById(v.getId());

        if (opcion) return; //se anula el resto del método si hay una selección hecha
        opcion = true; //se deshabilita el funcionamiento de los botones

        if (btnOpcion.getText().toString().equals(siguientePais.get(0))) { //en caso de acertar
            if (btnOpcion == btn_1) { //cambiar el color de la opcion seleccionada a verde (acierto)
                btn_1Estado = 1; //este estado indica acierto en el botón (verde)
                btn_1.setBackgroundColor(Color.GREEN);
            } else if (btnOpcion == btn_2) {
                btn_2Estado = 1;
                btn_2.setBackgroundColor(Color.GREEN);
            } else if (btnOpcion == btn_3) {
                btn_3Estado = 1;
                btn_3.setBackgroundColor(Color.GREEN);
            }
            acertados.add(btnOpcion.getText().toString());

            Boolean ultimo = gestorPaisesDB.comprobarUltimo(continente, acertados);

            if (ultimo) {
                this.terminarPartida();
            } else {
                btnSiguienteEstado = 1;
                btnSiguiente.setVisibility(View.VISIBLE);
            }
        } else { //cambiar el color de la opcion seleccionada a rojo (fallo) y a verde el de la opción correcta
            if (btnOpcion == btn_1) {
                btn_1Estado = 2; //este estado indica fallo en el botón (rojo)
                btn_1.setBackgroundColor(Color.RED);
                if (btn_2.getText().toString().equals(siguientePais.get(0))) {
                    btn_2Estado = 1;
                    btn_2.setBackgroundColor(Color.GREEN);
                } else if (btn_3.getText().toString().equals(siguientePais.get(0))) {
                    btn_3Estado = 1;
                    btn_3.setBackgroundColor(Color.GREEN);
                }
            } else if (btnOpcion == btn_2) {
                btn_2Estado = 2;
                btn_2.setBackgroundColor(Color.RED);
                if (btn_1.getText().toString().equals(siguientePais.get(0))) {
                    btn_1Estado = 1;
                    btn_1.setBackgroundColor(Color.GREEN);
                } else if (btn_3.getText().toString().equals(siguientePais.get(0))) {
                    btn_3Estado = 1;
                    btn_3.setBackgroundColor(Color.GREEN);
                }
            } else if (btnOpcion == btn_3) {
                btn_3Estado = 2;
                btn_3.setBackgroundColor(Color.RED);
                if (btn_1.getText().toString().equals(siguientePais.get(0))) {
                    btn_1Estado = 1;
                    btn_1.setBackgroundColor(Color.GREEN);
                } else if (btn_2.getText().toString().equals(siguientePais.get(0))) {
                    btn_2Estado = 1;
                    btn_2.setBackgroundColor(Color.GREEN);
                }
            }
            this.terminarPartida(); //al haber fallado ejecutar el método de fin de partida
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) { //para guardar el estado de la actividad (valores de variables y estados de objetos)
        super.onSaveInstanceState(savedInstanceState);

        //Guardar info
        savedInstanceState.putBoolean("restored", true);
        savedInstanceState.putString("continente", continente);
        savedInstanceState.putStringArrayList("acertados", acertados);
        savedInstanceState.putStringArrayList("siguientePais", siguientePais);
        savedInstanceState.putInt("btn_1Estado", btn_1Estado);
        savedInstanceState.putInt("btn_2Estado", btn_2Estado);
        savedInstanceState.putInt("btn_3Estado", btn_3Estado);
        savedInstanceState.putInt("btnJugarEstado", btnJugarEstado);
        savedInstanceState.putInt("btnSiguienteEstado", btnSiguienteEstado);
        savedInstanceState.putString("btn_1Text", btn_1.getText().toString());
        savedInstanceState.putString("btn_2Text", btn_2.getText().toString());
        savedInstanceState.putString("btn_3Text", btn_3.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) { //para recuperar el estado de la actividad (valores de variables y estados de objetos)
        super.onRestoreInstanceState(savedInstanceState);

        //Restaurar info
        restored = savedInstanceState.getBoolean("restored");
        continente = savedInstanceState.getString("continente");
        acertados = savedInstanceState.getStringArrayList("acertados");
        siguientePais = savedInstanceState.getStringArrayList("siguientePais");

        //restaurar imagen
        int resId = this.getResources().getIdentifier(siguientePais.get(1), "drawable", this.getPackageName());
        ImageView img = (ImageView) findViewById(R.id.bandera);
        img.setImageResource(resId);

        //Estados de los botones (Texto y colores)
        btn_1Estado = savedInstanceState.getInt("btn_1Estado");
        btn_1.setText(savedInstanceState.getString("btn_1Text"));
        if (btn_1Estado == 0) {
            btn_1.setBackgroundColor(Color.parseColor("#6200EE"));
        } else if (btn_1Estado == 1) {
            btn_1.setBackgroundColor(Color.GREEN);
        } else if (btn_1Estado == 2) {
            btn_1.setBackgroundColor(Color.RED);
        }

        btn_2Estado = savedInstanceState.getInt("btn_2Estado");
        btn_2.setText(savedInstanceState.getString("btn_2Text"));
        if (btn_2Estado == 0) {
            btn_2.setBackgroundColor(Color.parseColor("#6200EE"));
        } else if (btn_2Estado == 1) {
            btn_2.setBackgroundColor(Color.GREEN);
        } else if (btn_2Estado == 2) {
            btn_2.setBackgroundColor(Color.RED);
        }

        btn_3Estado = savedInstanceState.getInt("btn_3Estado");
        btn_3.setText(savedInstanceState.getString("btn_3Text"));
        if (btn_3Estado == 0) {
            btn_3.setBackgroundColor(Color.parseColor("#6200EE"));
        } else if (btn_3Estado == 1) {
            btn_3.setBackgroundColor(Color.GREEN);
        } else if (btn_3Estado == 2) {
            btn_3.setBackgroundColor(Color.RED);
        }

        //indicar si se había seleccionado alguna opción antes de la interrupción
        opcion = btn_1Estado == 1 || btn_2Estado == 1 || btn_3Estado == 1;

        //recuperar el estado (visible o invisible) del botón "Siguiente"
        btnSiguienteEstado = savedInstanceState.getInt("btnSiguienteEstado");
        if (btnSiguienteEstado == 1) {
            btnSiguiente.setVisibility(View.VISIBLE);
        } else {
            btnSiguiente.setVisibility(View.INVISIBLE);
        }

        //recuperar el estado (visible o invisible) del botón "Volver a jugar"
        btnJugarEstado = savedInstanceState.getInt("btnJugarEstado");
        if (btnJugarEstado == 1) {
            btnJugar.setVisibility(View.VISIBLE);
        } else {
            btnJugar.setVisibility(View.INVISIBLE);
        }
    }

    public void onClickJugar(View v) { //ocClick del botón "Volver a jugar
        btnJugarEstado = 0; //estado que indica invisibilidad
        acertados = new ArrayList<>(); //se vacía la lista de acertados (porque se empieza una nueva partida)
        btnJugar.setVisibility(View.INVISIBLE);
        opcion = false; //se anula el flag que indica la selección de alguna opción (para cambiar el color de los botones)
        this.siguienteBandera(); //mostrar siguiente bandera y opciones
    }

    public void onClickSiguiente(View v) { //ocClick del botón "Siguiente"
        btnSiguienteEstado = 0; //estado que indica invisibilidad
        btnSiguiente.setVisibility(View.INVISIBLE);
        this.siguienteBandera(); //mostrar siguiente bandera y opciones
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

            //estados por defecto de los botones y del flag de opción seleccionada
            btn_1Estado = 0;
            btn_2Estado = 0;
            btn_3Estado = 0;
            btnSiguienteEstado = 0;
            btnJugarEstado = 0;
            opcion = false;

            //pintar los botones de las opciones del color estándar
            btn_1.setBackgroundColor(Color.parseColor("#6200EE"));
            btn_2.setBackgroundColor(Color.parseColor("#6200EE"));
            btn_3.setBackgroundColor(Color.parseColor("#6200EE"));

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
            btnJugarEstado = 0;
            this.terminarPartida();
        }
    }

    private void terminarPartida() {
        btnJugarEstado = 1;
        btnJugar.setVisibility(View.VISIBLE);
        this.notificarResultado();
    }

    private void notificarResultado() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //Versiones Android 8.0 y superiores
            NotificationChannel channel = new NotificationChannel("canal", "nombre", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

        }

        //editar apariencia y configuración de la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "canal")
                .setSmallIcon(R.drawable.icono)
                .setContentTitle("Resultado de la última partida (" + this.continente + "):")
                .setContentText("Aciertos: " + acertados.size())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        managerCompat.notify(1, builder.build());
    }
}