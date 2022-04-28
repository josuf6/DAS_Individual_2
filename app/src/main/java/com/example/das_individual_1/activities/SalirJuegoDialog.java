package com.example.das_individual_1.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SalirJuegoDialog extends DialogFragment {

    ListenerDialogoSalirJuego miListener;

    //Interfaz para poder acceder a eventos del diálogo desde la actividad
    public interface ListenerDialogoSalirJuego {
        void onClickSalirJuego();
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
        builder.setPositiveButton("Sí", (dialogInterface, i) -> {
            miListener.onClickSalirJuego(); //Al pulsar "Sí" volver al menú principal
        });

        //No hacer nada al pulsar "No" (se cierra el diálogo)
        builder.setNegativeButton("No", (dialogInterface, i) -> {});

        return builder.create();
    }
}
