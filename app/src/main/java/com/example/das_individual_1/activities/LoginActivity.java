package com.example.das_individual_1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.das_individual_1.R;
import com.example.das_individual_1.workers.ComprobarUsuarioDB;
import com.example.das_individual_1.workers.CrearUsuarioDB;
import com.example.das_individual_1.workers.HacerLoginDB;
import com.example.das_individual_1.workers.MensajeFCMDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private String token;
    private String usuario;
    private String password;
    private EditText usuario_txt;
    private EditText password_txt;
    private Button login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (this.usuario == null) {
            this.usuario = "";
        }
        if (this.password == null) {
            this.password = "";
        }
        this.usuario_txt = (EditText) findViewById(R.id.usuario_txt);
        this.password_txt = (EditText) findViewById(R.id.password_txt);
        this.login_btn = (Button) findViewById(R.id.login_btn);

        //Proceso para guardar token de FCM
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            token = task.getResult();
                        }
                    }
                });
    }

    public void onClickLogin(View v) {
        this.login_btn.setEnabled(false);
        this.usuario_txt.setEnabled(false);
        this.password_txt.setEnabled(false);

        //Proceso de login (al pulsar botón de login)
        //1. comprobar que campos de usuario y contraseña no están vacios
        //2. Comprobar si el usuario existe en la BD de usuarios remota
        //3. Dependiendo del resultado:
            //Si no existe, crear nuevo usuario con los datos proporcionados
            //Si existe, comprobar si la contraseña es correcta, y hacer login o no dependiendo de si lo es
        this.usuario = this.usuario_txt.getText().toString();
        this.password = this.password_txt.getText().toString();
        if (!this.usuario.equals("") && !this.password.equals("")) {
            Data datos = new Data.Builder()
                    .putString("usuario", this.usuario)
                    .build();

            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ComprobarUsuarioDB.class).setInputData(datos).build();
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState().isFinished()) {
                                if (workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {
                                    if (workInfo.getOutputData().getString("datos").equals("error")) {
                                        Toast toast= Toast.makeText(getApplicationContext(),"Algo ha ido mal :(",Toast.LENGTH_SHORT);
                                        toast.show();
                                        login_btn.setEnabled(true);
                                        usuario_txt.setEnabled(true);
                                        password_txt.setEnabled(true);
                                    } else if (workInfo.getOutputData().getString("datos").equals("0")) {
                                        crearUsuario(usuario, password);
                                    } else {
                                        procesoLogin(usuario, password);
                                    }
                                } else {
                                    Toast toast= Toast.makeText(getApplicationContext(),"Algo ha ido mal :(",Toast.LENGTH_SHORT);
                                    toast.show();
                                    login_btn.setEnabled(true);
                                    usuario_txt.setEnabled(true);
                                    password_txt.setEnabled(true);
                                }
                            }
                        }
                    });
            WorkManager.getInstance(this).enqueue(otwr);
        } else {
            Toast toast= Toast.makeText(getApplicationContext(),"Introduce usuario y contraseña",Toast.LENGTH_SHORT);
            toast.show();
            login_btn.setEnabled(true);
            usuario_txt.setEnabled(true);
            password_txt.setEnabled(true);
        }
    }

    private void crearUsuario(String pUsuario, String pPassword) {

        //Proceso para crear nuevo usuario en la BD de usuarios con los datos proporcionados
        Data datos = new Data.Builder()
                .putString("usuario", pUsuario)
                .putString("password", pPassword)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(CrearUsuarioDB.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            if (workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {
                                if (workInfo.getOutputData().getString("datos").equals("error")) {
                                    Toast toast= Toast.makeText(getApplicationContext(),"No se ha podido crear un nuevo usuario, vuelve a intentarlo más tarde",Toast.LENGTH_SHORT);
                                    toast.show();
                                    login_btn.setEnabled(true);
                                    usuario_txt.setEnabled(true);
                                    password_txt.setEnabled(true);
                                } else if (workInfo.getOutputData().getString("datos").equals("creado")) {
                                    Toast toast= Toast.makeText(getApplicationContext(),"Usuario " + pUsuario + " creado correctamente",Toast.LENGTH_SHORT);
                                    toast.show();
                                    procesoLogin(pUsuario, pPassword);
                                } else {
                                    Toast toast= Toast.makeText(getApplicationContext(),"Algo ha ido mal :(",Toast.LENGTH_SHORT);
                                    toast.show();
                                    login_btn.setEnabled(true);
                                    usuario_txt.setEnabled(true);
                                    password_txt.setEnabled(true);
                                }
                            } else {
                                Toast toast= Toast.makeText(getApplicationContext(),"Algo ha ido mal :(",Toast.LENGTH_SHORT);
                                toast.show();
                                login_btn.setEnabled(true);
                                usuario_txt.setEnabled(true);
                                password_txt.setEnabled(true);
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void procesoLogin(String pUsuario, String pPassword) {

        //Proceso para comprobar si el usuario introducido es correcto y, si lo es, hacer login
        Data datos = new Data.Builder()
                .putString("usuario", pUsuario)
                .putString("password", pPassword)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(HacerLoginDB.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            if (workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {
                                if (workInfo.getOutputData().getString("datos").equals("error")) {
                                    Toast toast= Toast.makeText(getApplicationContext(),"No se ha podido iniciar sesión, vuelve a intentarlo más tarde",Toast.LENGTH_SHORT);
                                    toast.show();
                                    login_btn.setEnabled(true);
                                    usuario_txt.setEnabled(true);
                                    password_txt.setEnabled(true);
                                } else if (workInfo.getOutputData().getString("datos").equals("loggeado")) { //Si los datos son correctos hacer login
                                    Toast toast= Toast.makeText(getApplicationContext(),"¡Hola, " + pUsuario + "!",Toast.LENGTH_SHORT);
                                    toast.show();
                                    
                                    mensajeFCM();

                                    //Abrir la actividad del menú y cerrar esta
                                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                    intent.putExtra("token", token);
                                    intent.putExtra("usuario", usuario);
                                    startActivity(intent);
                                    finish();
                                } else if (workInfo.getOutputData().getString("datos").equals("incorrecto")) { //Si los datos son incorrectos no hacer nada
                                    Toast toast= Toast.makeText(getApplicationContext(),"Usuario o contraseña incorrectos",Toast.LENGTH_SHORT);
                                    toast.show();
                                    login_btn.setEnabled(true);
                                    usuario_txt.setEnabled(true);
                                    password_txt.setEnabled(true);
                                } else {
                                    Toast toast= Toast.makeText(getApplicationContext(),"Algo ha ido mal :(",Toast.LENGTH_SHORT);
                                    toast.show();
                                    login_btn.setEnabled(true);
                                    usuario_txt.setEnabled(true);
                                    password_txt.setEnabled(true);
                                }
                            } else {
                                Toast toast= Toast.makeText(getApplicationContext(),"Algo ha ido mal :(",Toast.LENGTH_SHORT);
                                toast.show();
                                login_btn.setEnabled(true);
                                usuario_txt.setEnabled(true);
                                password_txt.setEnabled(true);
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void mensajeFCM() {
        Data datos = new Data.Builder()
                .putString("token", this.token)
                .putString("usuario", this.usuario)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(MensajeFCMDB.class).setInputData(datos).build();
        WorkManager.getInstance(this).enqueue(otwr);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) { //para guardar el estado de la actividad (valores de variables y estados de objetos)
        super.onSaveInstanceState(savedInstanceState);

        //Guardar info
        savedInstanceState.putString("token", this.token);
        savedInstanceState.putString("usuario_txt", this.usuario_txt.getText().toString());
        savedInstanceState.putString("password_txt", this.password_txt.getText().toString());
        savedInstanceState.putString("usuario", this.usuario);
        savedInstanceState.putString("password", this.password);
        savedInstanceState.putBoolean("login_btnEnabled", this.login_btn.isEnabled());
        savedInstanceState.putBoolean("usuario_txtEnabled", this.usuario_txt.isEnabled());
        savedInstanceState.putBoolean("password_txtEnabled", this.password_txt.isEnabled());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) { //para recuperar el estado de la actividad (valores de variables y estados de objetos)
        super.onRestoreInstanceState(savedInstanceState);

        //Restaurar info
        this.token = savedInstanceState.getString("token");
        this.usuario_txt.setText(savedInstanceState.getString("usuario_txt"));
        this.password_txt.setText(savedInstanceState.getString("password_txt"));
        this.usuario = savedInstanceState.getString("usuario");
        this.password = savedInstanceState.getString("password");

        //Restaurar estado botón login y EditText
        this.login_btn.setEnabled(savedInstanceState.getBoolean("login_btnEnabled"));
        this.usuario_txt.setEnabled(savedInstanceState.getBoolean("usuario_txtEnabled"));
        this.password_txt.setEnabled(savedInstanceState.getBoolean("password_txtEnabled"));
    }
}