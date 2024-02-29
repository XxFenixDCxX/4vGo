package es.cuatrovientos.a4vgo.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import es.cuatrovientos.a4vgo.R;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    public CustomSpinnerAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        applyCustomStyle((TextView) view);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        applyCustomStyle((TextView) view);
        return view;
    }

    private void applyCustomStyle(TextView textView) {
        textView.setTextAppearance(getContext(), R.style.SpinnerTextStyle);
        // Puedes ajustar otros atributos según tus preferencias aquí
    }
}

