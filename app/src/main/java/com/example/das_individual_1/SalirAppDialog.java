package com.example.das_individual_1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SalirAppDialog extends DialogFragment {

    ListenerDialogoSalirApp miListener;

    //Interfaz para poder acceder a eventos del diálogo desde la actividad
    public interface ListenerDialogoSalirApp {
        void onClickSi();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        miListener = (ListenerDialogoSalirApp) getActivity();

        //Configurar el diálogo usando la clase builder (Título y mensaje)
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Salir de la aplicación");
        builder.setMessage("¿Deseas salir de la aplicación?");

        //Definir botones y Listeners de los botones
        builder.setPositiveButton("Sí", (dialogInterface, i) -> {
            miListener.onClickSi(); //Al pulsar "Sí" salir de la aplicación
        });

        //No hacer nada al pulsar "No" (se cierra el diálogo)
        builder.setNegativeButton("No", (dialogInterface, i) -> {});

        return builder.create();
    }
}
