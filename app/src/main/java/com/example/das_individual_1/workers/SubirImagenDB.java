package com.example.das_individual_1.workers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SubirImagenDB extends Worker {
    public SubirImagenDB(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String direccion = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/jferreras001/WEB/subir-imagen.php";
        HttpURLConnection urlConnection;

        //Parametros que se van a enviar en la conexion
        String nombre = getInputData().getString("nombrefich");
        String imagen = getInputData().getString("imagen");
        Uri uriImagen = Uri.parse(imagen);

        Bitmap bitmapImagen = null;
        try {
            bitmapImagen = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uriImagen);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int anchoDestino = 150;
        int altoDestino = 250;
        int anchoImagen = bitmapImagen.getWidth();
        int altoImagen = bitmapImagen.getHeight();
        float ratioImagen = (float) anchoImagen / (float) altoImagen;
        float ratioDestino = (float) anchoDestino / (float) altoDestino;
        int anchoFinal = anchoDestino;
        int altoFinal = altoDestino;
        if (ratioDestino > ratioImagen) {
            anchoFinal = (int) ((float)altoDestino * ratioImagen);
        } else {
            altoFinal = (int) ((float)anchoDestino / ratioImagen);
        }
        Bitmap bitmapredimensionado = Bitmap.createScaledBitmap(bitmapImagen, anchoFinal, altoFinal, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapredimensionado.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imagenTransformada = stream.toByteArray();
        String fotoen64 = Base64.encodeToString(imagenTransformada, Base64.DEFAULT);

        try {
            JSONObject parametrosJSON = new JSONObject();
            parametrosJSON.put("nombre", nombre);
            parametrosJSON.put("imagen", fotoen64);

            //Preparar datos de la conexion
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametrosJSON.toString());
            out.close();

            //LLamada al servicio web
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) { //Si va bien
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                inputStream.close();

                Data resultados = new Data.Builder()
                        .putString("datos",result)
                        .build();
                return Result.success(resultados);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return Result.failure();
    }
}
