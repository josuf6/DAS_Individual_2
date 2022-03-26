package com.example.das_individual_1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.sql.ResultSet;
import java.util.ArrayList;

public class PaisesBD extends SQLiteAssetHelper {

    private SQLiteDatabase db;
    public static final String DBNAME = "paises.sqlite";
    public static final int DBVERSION = 1;

    public PaisesBD(Context context) {
        super(context , DBNAME, null, DBVERSION);
        db = getWritableDatabase();
    }

    public ArrayList<String> getSiguienteBandera(String continente, ArrayList<String> acertadosList) {
        String acertados = "";
        ArrayList<String> candidatosList = new ArrayList<>();
        ArrayList<String> siguienteBandera = new ArrayList<>();
        int candidatosKop;

        //Preparar un string con los países ya acertados, para excluirlos en la query
        for (int i = 0; i < acertadosList.size(); i++) {
            acertados += "'" + acertadosList.get(i) + "'";
            if (i + 1 < acertadosList.size()) {
                acertados += ", ";
            }
        }

        //Query para conseguir los candidatos para la siguiente bandera (los que todavia no hayan sido acertados)
        String query1;
        if (continente == "Global") {
            query1 = "SELECT Nombre FROM Pais WHERE Nombre NOT IN (" + acertados + ");";
        } else {
            query1 = "SELECT Nombre FROM Pais WHERE Continente = '" + continente + "' AND Nombre NOT IN (" + acertados + ");";
        }
        Cursor c1 = db.rawQuery(query1, null);

        //Obtener siguiente Bandera de manera aleatoria
        while (c1.moveToNext()) {
            candidatosList.add(c1.getString(0));
        }
        candidatosKop = candidatosList.size();
        if (candidatosKop == 0) { //Si no hay candidatos se ha terminado la partida;
            return  null;
        }
        int numSigBandera = (int)(Math.random() * candidatosKop);
        siguienteBandera.add(0, candidatosList.get(numSigBandera));

        //Query para obtener nombre del archivo que contiene la imagen de la bandera
        String query2 = "SELECT Archivo FROM Pais WHERE Nombre = '" + siguienteBandera.get(0) + "';";
        Cursor c2 = db.rawQuery(query2, null);
        c2.moveToNext();
        siguienteBandera.add(1, c2.getString(0));

        return siguienteBandera;
    }

    public ArrayList<String> getPaisesAleatorios(String continente, String siguientePais) {
        ArrayList<String> candidatosList = new ArrayList<>();
        ArrayList<String> aleatoriosList = new ArrayList<>();

        //Query para conseguir los candidatos para las opciones incorrectas (excluyendo el país de la opción correcta)
        String query1;
        if (continente == "Global") {
            query1 = "SELECT Nombre FROM Pais WHERE Nombre NOT IN ('" + siguientePais + "');";
        } else {
            query1 = "SELECT Nombre FROM Pais WHERE Continente = '" + continente + "' AND Nombre NOT IN ('" + siguientePais + "');";
        }
        Cursor c1 = db.rawQuery(query1, null);

        //Obtener opciones incorrectas de manera aleatoria
        while (c1.moveToNext()) {
            candidatosList.add(c1.getString(0));
        }
        int numAleatorio = (int)(Math.random() * candidatosList.size());
        aleatoriosList.add(candidatosList.get(numAleatorio));
        candidatosList.remove(numAleatorio); //Eliminar de la lista de candidatos el primer aleatorio conseguido
        numAleatorio = (int)(Math.random() * candidatosList.size());
        aleatoriosList.add(candidatosList.get(numAleatorio));

        return aleatoriosList;
    }
}
