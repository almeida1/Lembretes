package com.example.edson.lembretes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by edson on 24/01/2016.
 */
public class LembreteDbAdapter {

    //defincao das colunas
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_IMPORTANCIA = "importancia";

    //definicao dos indices
    public static final int INDEX_ID = 0;
    public static final int INDEX_CONTENT = INDEX_ID +1;
    public static final int INDEX_IMPORTANCIA = INDEX_ID + 2;

    //registro de log
    private static final String TAG = "LembreteDbAdapter";

    private DatabaseHelper fDbHelper;
    private SQLiteDatabase fDb;

    private static final String DATABASE_NAME = "dba_lembretes";
    private static final String TABLE_NAME = "tbl_lembretes";
    private static final int DATABASE_VERSION = 1;

    private Context fCtx = null;

    //declaracoes SQL usadas para criar o banco de dados
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY, " +
                    COL_CONTENT + " TEXT, " +
                    COL_IMPORTANCIA + " INTEGER );";

    public LembreteDbAdapter(Context ctx){
        this.fCtx = ctx;
    }

    public void open() throws SQLException {
        fDbHelper = new DatabaseHelper(fCtx);
        fDb = fDbHelper.getWritableDatabase();
    }

    public void close(){
        if (fDbHelper != null){
            fDbHelper.close();
        }
    }

    //Operacoes CRUD de acesso a dados (DAO)
    //CREATE
    public void createLembrete(String nome, boolean importancia){
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, nome);
        values.put(COL_IMPORTANCIA, importancia ? 1: 0);
        fDb.insert(TABLE_NAME,null, values);
    }
    public long createLembrete(Lembrete lembrete){
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, lembrete.getContent());//nome do contato
        values.put(COL_IMPORTANCIA, lembrete.getImportancia()); //
        //insere a linha
        return fDb.insert(TABLE_NAME,null, values);
    }
    //READ
    public Lembrete fetchLembreteById(int id){
        Cursor cursor = fDb.query(TABLE_NAME, new String[] {COL_ID,
                        COL_CONTENT, COL_IMPORTANCIA}, COL_ID + "=?",
                new String[] {String.valueOf(id)}, null,null, null,null);
        if (cursor !=null)
            cursor.moveToFirst();
        return new Lembrete(cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_CONTENT),
                cursor.getInt(INDEX_IMPORTANCIA)
        );
    }
    public Cursor fetchAllLembretes(){
        Cursor fCursor = fDb.query(TABLE_NAME, new String[]{COL_ID, COL_CONTENT, COL_IMPORTANCIA},
                null, null, null, null,null);
        if (fCursor != null){
            fCursor.moveToFirst();
        }
        return fCursor;
    }

    public void updateLembrete (Lembrete lembrete){
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, lembrete.getContent());
        values.put(COL_IMPORTANCIA, lembrete.getImportancia());
        fDb.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(lembrete.getFid())});
    }
    public void deleteLembreteById(int id){
        fDb.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
    }
    public void deleteTodosLembretes(){
        fDb.delete(TABLE_NAME, null, null);
    }
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper (Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.w(TAG, "atualizado a versao do banco de dados ");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
