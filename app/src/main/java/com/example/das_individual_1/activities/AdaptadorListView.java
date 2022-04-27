package com.example.das_individual_1.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.das_individual_1.R;

public class AdaptadorListView extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private String[] continentes;
    private String[] records;
    private int[] imagenes;

    public AdaptadorListView(Context pContext, String[] pContinentes, String[] pRecords, int[] pImagenes) {
        this.context = pContext;
        this.continentes = pContinentes;
        this.records = pRecords;
        this.imagenes = pImagenes;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return continentes.length;
    }

    @Override
    public Object getItem(int i) {
        return continentes[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = this.inflater.inflate(R.layout.fila,null);
        TextView nombre = (TextView) view.findViewById(R.id.nombre_continente);
        TextView record = (TextView) view.findViewById(R.id.record_continente);
        ImageView img = (ImageView) view.findViewById(R.id.imagen_continente);

        nombre.setText(continentes[i]);
        record.setText("Récord: " + records[i]);
        img.setImageResource(imagenes[i]);

        return view;
    }
}
