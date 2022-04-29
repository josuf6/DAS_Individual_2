package com.example.das_individual_1.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.das_individual_1.R;
import com.example.das_individual_1.workers.ComprobarRecordDB;
import com.example.das_individual_1.workers.ObtenerRecordContinenteDB;
import com.example.das_individual_1.workers.PaisesBD;

import java.util.ArrayList;

public class JuegoActivity extends AppCompatActivity implements SalirJuegoDialog.ListenerDialogoSalirJuego, NuevoRecordDialog.ListenerDialogoNuevoRecord {

    private PaisesBD gestorPaisesDB;
    private String continente;
    private String usuario;
    private ArrayList<String> acertados = new ArrayList<>();
    private ArrayList<String> siguientePais;
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private int puntuacion;
    private int btn_1Estado;
    private int btn_2Estado;
    private int btn_3Estado;
    private int btnJugarEstado;
    private int btnSiguienteEstado;
    private Button btnJugar;
    private Button btnSiguiente;
    private Boolean opcion;
    private Boolean restored;
    private TextView points;
    private TextView record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        btn_1 = (Button) findViewById(R.id.opcion_1_btn);
        btn_2 = (Button) findViewById(R.id.opcion_2_btn);
        btn_3 = (Button) findViewById(R.id.opcion_3_btn);

        points = (TextView) findViewById(R.id.points);
        record = (TextView) findViewById(R.id.record);

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

        if (usuario == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                usuario = extras.getString("usuario");
            }
        }

        this.setRecordText();

        gestorPaisesDB = new PaisesBD(this); //base de datos de países, continentes de los países, y nombres de archivos de las imágenes de las banderas

        if (this.restored == null) { //flag utilizado cuando se interrumpe la actividad (por una llamada, al salir de ella, ...)
            this.restored = false;
        }
        if (!this.restored) { //se ejecuta si no se ha interrumpido la actividad
            this.puntuacion = 0;
            points.setText(Integer.toString(this.puntuacion));
            this.siguienteBandera();
        }
    }

    private void setRecordText() {

        //se obtiene la información del récord del usuario y se muestra en la parte superior de la pantalla
        Data datos = new Data.Builder()
                .putString("usuario", this.usuario)
                .putString("continente", this.continente)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ObtenerRecordContinenteDB.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            if (workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {
                                if (workInfo.getOutputData().getString("datos").equals("error")) {
                                    record.setText("null");
                                } else {
                                    record.setText(workInfo.getOutputData().getString("datos"));
                                }
                            } else {
                                record.setText("null");
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    public void onClickOpcion(View v) { //Al clickar una de las opciones de respuesta
        Button btnOpcion = (Button) findViewById(v.getId());

        if (opcion) return; //se anula el resto del método si hay una selección hecha
        opcion = true; //se deshabilita el funcionamiento de los botones

        if (btnOpcion.getText().toString().equals(siguientePais.get(0))) { //en caso de acertar
            puntuacion++; //incrementar numero de aciertos
            points.setText(Integer.toString(puntuacion)); //Poner numero de aciertos en pantalla
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
        savedInstanceState.putString("usuario", usuario);
        savedInstanceState.putInt("puntuacion", puntuacion);
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
        savedInstanceState.putString("pointsText", points.getText().toString());
        savedInstanceState.putString("recordText", record.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) { //para recuperar el estado de la actividad (valores de variables y estados de objetos)
        super.onRestoreInstanceState(savedInstanceState);

        //Restaurar info
        restored = savedInstanceState.getBoolean("restored");
        continente = savedInstanceState.getString("continente");
        usuario = savedInstanceState.getString("usuario");
        puntuacion = savedInstanceState.getInt("puntuacion");
        acertados = savedInstanceState.getStringArrayList("acertados");
        siguientePais = savedInstanceState.getStringArrayList("siguientePais");
        points.setText(savedInstanceState.getString("pointsText"));
        record.setText(savedInstanceState.getString("recordText"));

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

    public void onClickJugar(View v) { //ocClick del botón "Volver a jugar"
        puntuacion = 0; //resetear puntuacion
        points.setText(Integer.toString(this.puntuacion)); //resetear marcador
        btnJugarEstado = 0; //estado que indica invisibilidad
        acertados = new ArrayList<>(); //se vacía la lista de acertados (porque se empieza una nueva partida)
        btnJugar.setVisibility(View.INVISIBLE);
        opcion = false; //se anula el flag que indica la selección de alguna opción (para cambiar el color de los botones)
        this.setRecordText();
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
    public void onClickSalirJuego() { //Al pulsar "Sí" matar la actividad (vuelve al menú principal porque es la última actividad en la pila)
        Intent intent = new Intent(JuegoActivity.this, MenuActivity.class);
        intent.putExtra("usuario", usuario);
        startActivity(intent);
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
        this.comprobarNuevoRecord();
    }

    private void comprobarNuevoRecord() {
        Data datos = new Data.Builder()
                .putString("usuario", this.usuario)
                .putString("continente", this.continente)
                .putInt("puntuacion", this.puntuacion)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ComprobarRecordDB.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            if (workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {
                                if (workInfo.getOutputData().getString("datos").equals("record")) {
                                    nuevoRecord();
                                }
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void nuevoRecord() {
        //Mostrar diálogo al superar el record
        DialogFragment dialogNuecoRecord = new NuevoRecordDialog();
        dialogNuecoRecord.show(getSupportFragmentManager(), "dialogNuecoRecord");
    }

    @Override
    public void onClickNuevoRecord() {
        Intent intent = new Intent(JuegoActivity.this, NuevoRecordActivity.class);
        intent.putExtra("usuario", this.usuario);
        intent.putExtra("continente", this.continente);
        intent.putExtra("puntuacion", this.puntuacion);
        startActivity(intent);
    }

    private void notificarResultado() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //Versiones Android 8.0 y superiores
            NotificationChannel channel = new NotificationChannel("canal", "nombre", NotificationManager.IMPORTANCE_LOW);
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