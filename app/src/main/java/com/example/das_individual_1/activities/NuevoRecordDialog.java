package com.example.das_individual_1.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class NuevoRecordDialog extends DialogFragment {

    ListenerDialogoNuevoRecord miListener;

    //Interfaz para poder acceder a eventos del diálogo desde la actividad
    public interface ListenerDialogoNuevoRecord {
        void onClickNuevoRecord();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        miListener = (ListenerDialogoNuevoRecord) getActivity();

        //Configurar el diálogo usando la clase builder (Título y mensaje)
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("¡Enhorabuena!");
        builder.setMessage("Has superado tu anterior record. ¿Te gustaría sacarte una foto?");

        //Definir botones y Listeners de los botones
        builder.setPositiveButton("Sí", (dialogInterface, i) -> {
            miListener.onClickNuevoRecord(); //Al pulsar "Sí" abrir la cámara
        });

        //No hacer nada al pulsar "No" (se cierra el diálogo)
        builder.setNegativeButton("No", (dialogInterface, i) -> {});

        return builder.create();
    }
}
