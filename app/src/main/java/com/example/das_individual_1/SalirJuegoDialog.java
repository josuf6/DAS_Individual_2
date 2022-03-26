package com.example.das_individual_1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SalirJuegoDialog extends DialogFragment {

    ListenerDialogoSalirJuego miListener;

    //Interfaz para poder acceder a eventos del diálogo desde la actividad
    public interface ListenerDialogoSalirJuego {
        void onClickSi();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        miListener = (ListenerDialogoSalirJuego) getActivity();

        //Configurar el diálogo usando la clase builder (Título y mensaje)
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Salir al menú principal");
        builder.setMessage("¿Deseas salir al menú principal? Se perderá el progreso de la partida actual.");

        //Definir botones y Listeners de los botones
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                miListener.onClickSi(); //Al pulsar "Sí" volver al menú principal
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {} //No hacer nada al pulsar "No" (se cierra el diálogo)
        });

        return builder.create();
    }
}
