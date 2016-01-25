package com.example.edson.lembretes;

import android.app.Dialog;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class LembreteActivity extends AppCompatActivity {
    private ListView fListView;
    private LembreteDbAdapter fDbAdapter;
    private LembreteSimpleCursorAdapter fCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lembrete);
        fListView = (ListView) findViewById(R.id.lembrete_listView);
        fListView.setDivider(null);
        fDbAdapter = new LembreteDbAdapter(this);
        try {
            fDbAdapter.open();
            if(savedInstanceState == null){
                fDbAdapter.deleteTodosLembretes();
                fDbAdapter.createLembrete("prova de matematica dia 10/01", true);
                fDbAdapter.createLembrete("prova de ingles dia 12/01", false);
                fDbAdapter.createLembrete("teste de edicao dia 15/01", true);
                fDbAdapter.createLembrete("teste de exclusao dia 20/01", false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int masterListPosition, long id){
                //Toast.makeText(MainActivity.this, "clicked" + position, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(LembreteActivity.this);
                ListView modeListView = new ListView(LembreteActivity.this);
                String[] modes = new String[]{"Edit Lembrete", "Delete Lembrete"};
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(LembreteActivity.this,
                        android.R.layout.simple_list_item_1,android.R.id.text1, modes);
                modeListView.setAdapter(modeAdapter);
                builder.setView(modeListView);
                final Dialog dialog = builder.create();
                dialog.show();
                modeListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        //edit lembrete
                        if (position == 0) {
                            int nId = getIdFromPosition(masterListPosition);
                            Lembrete lembrete = fDbAdapter.fetchLembreteById(nId);
                            fireCustomDialog(lembrete);
                            Toast.makeText(LembreteActivity.this, "editar posicao " + position, Toast.LENGTH_SHORT).show();
                            //delete reminder
                        } else {
                            Toast.makeText(LembreteActivity.this,"delete " + position, Toast.LENGTH_SHORT).show();
                            fDbAdapter.deleteLembreteById(getIdFromPosition(masterListPosition));
                            fCursorAdapter.changeCursor(fDbAdapter.fetchAllLembretes());
                        }
                        dialog.dismiss();
                    }
                });

            }

        });

        Cursor cursor = fDbAdapter.fetchAllLembretes();
        //from colunas definidas no db
        String[] from = new String[]{LembreteDbAdapter.COL_CONTENT};
        //to ids das views no layout
        int[] to = new int[]{R.id.linha_texto};

        //O cursorAdapter faz o papel de controlador seguindo o
        //modelo model-view-control
        fCursorAdapter = new LembreteSimpleCursorAdapter(
                //contexto
                LembreteActivity.this,
                //o layout da linha
                R.layout.lembrete_linha,
                //cursor
                cursor,
                //from colunas definidas no db
                from,
                //to os ids das views no layout
                to,
                //flag - nao usado
                0
        );
        fListView.setAdapter(fCursorAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    private int getIdFromPosition(int nc){
        return (int)fCursorAdapter.getItemId(nc);
    }
    private void fireCustomDialog(final Lembrete lembrete) {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        TextView titleView = (TextView) dialog.findViewById(R.id.custom_title);
        final EditText editCustom = (EditText) dialog.findViewById(R.id.custom_edit_reminder);
        Button commitButton = (Button) dialog.findViewById(R.id.custom_button_commit);
        final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.custom_check_box);
        LinearLayout rootLayout = (LinearLayout) dialog.findViewById(R.id.custom_root_layout);
        final boolean isEditOperation = (lembrete != null);
        //this is for an edit
        if (isEditOperation) {
            titleView.setText("Edit Lembrete");
            checkBox.setChecked(lembrete.getImportancia() == 1);
            editCustom.setText(lembrete.getContent());
            rootLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        }
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reminderText = editCustom.getText().toString();
                if (isEditOperation) {
                    Lembrete reminderEdited = new Lembrete(lembrete.getFid(),
                            reminderText, checkBox.isChecked() ? 1 : 0);
                    fDbAdapter.updateLembrete(reminderEdited);
                    //this is for new reminder
                } else {
                    fDbAdapter.createLembrete(reminderText, checkBox.isChecked());
                }
                fCursorAdapter.changeCursor(fDbAdapter.fetchAllLembretes());
                dialog.dismiss();
            }
        });
        Button buttonCancel = (Button) dialog.findViewById(R.id.custom_button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lembrete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Manipula as acoes da barra de acao Overflow Menu (pontinhos na primeira linha da tela)
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_new:
                fireCustomDialog(null);// null abre a tela de adicção de lembrete
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return false;
        }




    }
}
