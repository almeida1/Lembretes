package com.example.edson.lembretes;


import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

/**
 * Created by edson on 14/01/2016.
 * Demostra a logica necessaria para conectar os valores obtidos do banco de dados
 * para um objeto linha na aplicação.
 */

public class LembreteSimpleCursorAdapter extends SimpleCursorAdapter {
    public LembreteSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags){
        super(context, layout, c, from, to, flags);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return super.newView(context, cursor, parent);
    }
    @Override
    public void bindView (View view, Context context, Cursor cursor){
        super.bindView(view, context, cursor);
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null){
            holder = new ViewHolder() ;
            holder.colImp = cursor.getColumnIndexOrThrow(LembreteDbAdapter.COL_IMPORTANCIA);
            holder.listTab = view.findViewById(R.id.linha_texto);
            view.setTag(holder);
        }
        if(cursor.getInt(holder.colImp)>0){
            holder.listTab.setBackgroundColor(context.getResources().getColor(R.color.laranja));
        }else{
            holder.listTab.setBackgroundColor(context.getResources().getColor(R.color.verde));
        }

    }
    static class ViewHolder{
        int colImp;//armazena o indice da coluna
        View listTab;//armazena a view
    }
}
