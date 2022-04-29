package com.example.das_individual_1.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MensajeFCMDB extends Worker {
    public MensajeFCMDB(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String direccion = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/jferreras001/WEB/mensaje-fcm.php";
        HttpURLConnection urlConnection;

        //Parametros que se van a enviar en la conexion
        String token = getInputData().getString("token");
        String usuario = getInputData().getString("usuario");
        String parametros = "token=" + token + "&usuario=" + usuario;
        try {
            //Preparar datos de la conexion
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            //LLamada al servicio web
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) { //Si va bien
                return Result.success();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.failure();
    }
}
