package com.example.u5_z_a16alejandrobp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class U5_Z_A16AlejandroBP extends AppCompatActivity {
    boolean sdDisponhible = false;
    boolean sdAccesoEscritura = false;
    File filesDir;
    File dirFicheiroSD;
    File rutaCompleta;
    EditText directory_et;
    EditText file_et;
    RadioButton internal_rb;
    RadioButton sd_rb;
    RadioButton raw_rb;
    CheckBox cb;
    EditText sentences_et;
    Button write_add_btn;
    Button read_btn;
    Button delete_btn;
    Button list_bn;
    TextView tv;
    public static String nomeFicheiro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        directory_et = (EditText)findViewById(R.id.directory_et);
        file_et = (EditText)findViewById(R.id.file_et);
        internal_rb = (RadioButton)findViewById(R.id.internal_rb);
        sd_rb = (RadioButton)findViewById(R.id.sd_rb);
        raw_rb = (RadioButton)findViewById(R.id.raw_rb);
        cb = (CheckBox)findViewById(R.id.cb);
        sentences_et = (EditText)findViewById(R.id.sentences_et);
        write_add_btn = (Button)findViewById(R.id.write_add_btn);
        read_btn = (Button)findViewById(R.id.read_btn);
        delete_btn = (Button)findViewById(R.id.delete_btn);
        list_bn = (Button)findViewById(R.id.list_btn);
        tv = (TextView)findViewById(R.id.tv);
        comprobarEstadoSD();
    }
    public void comprobarEstadoSD() {
        if(Build.VERSION.SDK_INT>=23){
            File[] storages = ContextCompat.getExternalFilesDirs(this, null);
            if (storages.length > 1 && storages[0] != null && storages[1] != null) {
                sdDisponhible = true;
                sdAccesoEscritura = true;
            }
            else {
                sdDisponhible = true;
            }
        }else{
            String estado = Environment.getExternalStorageState();
            Log.e("SD", estado);

            if (estado.equals(Environment.MEDIA_MOUNTED)) {
                sdDisponhible = true;
                sdAccesoEscritura = true;
            } else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
                sdDisponhible = true;
            }
        }
    }

    public void establecerDirectorioFicheiro() {

        if (sdDisponhible) {
            // dirFicheiroSD = Environment.getExternalStorageDirectory();
            if(Build.VERSION.SDK_INT>=23) {
                File[] storages = ContextCompat.getExternalFilesDirs(this, null);
                filesDir = storages[1];
                dirFicheiroSD = storages[1];
                if(directory_et.getText().toString().equals("")) {
                    rutaCompleta = new File(dirFicheiroSD, nomeFicheiro);
                }else{
                    dirFicheiroSD = new File(dirFicheiroSD.toString()+"/"+directory_et.getText().toString());
                    rutaCompleta = new File(dirFicheiroSD,nomeFicheiro);
                }
            }else{
                filesDir = getExternalFilesDir(null);
                dirFicheiroSD = getExternalFilesDir(null);
                if(directory_et.getText().toString().equals("")) {
                    rutaCompleta = new File(dirFicheiroSD.getAbsolutePath(), nomeFicheiro);
                }else{
                    dirFicheiroSD = new File(dirFicheiroSD.getAbsolutePath()+"/"+directory_et.getText().toString());
                    rutaCompleta = new File(dirFicheiroSD,nomeFicheiro);
                }

            }

        }
    }

    public void onWriteAddClick(View view) {
        if(raw_rb.isChecked()){
            Toast.makeText(this,"You can't write in RAW memory",Toast.LENGTH_LONG).show();
        }else if(internal_rb.isChecked()) {
            int contexto;
            if (file_et.getText().toString().equals("")) {
                Toast.makeText(this, "You should type a file", Toast.LENGTH_LONG).show();
            } else {
                nomeFicheiro = file_et.getText().toString();
                tv.setText("");
                if(directory_et.getText().toString().equals("")){

                    if (cb.isChecked())
                        contexto = Context.MODE_PRIVATE;
                    else
                        contexto = Context.MODE_APPEND;

                    try {

                        OutputStreamWriter osw = new OutputStreamWriter(openFileOutput(nomeFicheiro, contexto));

                        osw.write(sentences_et.getText() + "\n");
                        osw.close();

                    } catch (Exception ex) {
                        Log.e("INTERNA", "Error escribindo no ficheiro");
                    }
                }else{
                    try {
                        File subdir = new File(getFilesDir()+"/"+directory_et.getText().toString());
                        subdir.mkdirs();
                        File ruta = new File(subdir,nomeFicheiro);
                        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(ruta,!(cb.isChecked())));

                        osw.write(sentences_et.getText() + "\n");
                        osw.close();

                    } catch (Exception ex) {
                        Log.e("INTERNA", "Error escribindo no ficheiro");
                    }
                }
            }
        }else if(sd_rb.isChecked()) {
            nomeFicheiro = file_et.getText().toString();
            establecerDirectorioFicheiro();
                dirFicheiroSD.mkdirs();
            boolean sobrescribir = false;

            sobrescribir = !(cb.isChecked());
            if (file_et.getText().toString().equals("")) {
                Toast.makeText(this, "You should type a file", Toast.LENGTH_LONG).show();
            } else {
                tv.setText("");

                if (sdAccesoEscritura) {

                    try {

                        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(rutaCompleta, sobrescribir));

                        osw.write(sentences_et.getText() + "\n");
                        osw.close();

                    } catch (Exception ex) {
                        Log.e("SD", "Error escribindo no ficheiro");
                        ex.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "A tarxeta SD non está en modo acceso escritura", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void onReadClick(View view) {
        tv.setText("");
        if(raw_rb.isChecked()){
            String linea;
            tv.setText("");
            try {
                InputStream is = null;
                String fileText = file_et.getText().toString();
                if(fileText.equals("ola")) {
                    is = getResources().openRawResource(R.raw.ola);
                }else if(fileText.equals("adeus")) {
                    is = getResources().openRawResource(R.raw.adeus);
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                while ((linea = br.readLine()) != null)
                    tv.append(linea + "\n");

                br.close();
                is.close();
            } catch (Exception ex) {
                CharSequence msg = "Error reading file: "+file_et.getText();
                Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
                Log.e("Error","Erro lendo o ficheiro");
                ex.printStackTrace();
            }
        }else if(internal_rb.isChecked()){
            String linha = "";
            tv.setText(linha);
            nomeFicheiro = file_et.getText().toString();
            if(directory_et.getText().toString().equals("")) {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(nomeFicheiro)));

                    while ((linha = br.readLine()) != null)
                        tv.append(linha + "\n");

                    br.close();

                } catch (Exception ex) {
                    if (nomeFicheiro.equals("")) {
                        Toast.makeText(this, "Error reading file: " + getFilesDir(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error reading file: " + getFilesDir() + "/" + nomeFicheiro, Toast.LENGTH_SHORT).show();
                    }

                    Log.e("INTERNA", "Erro lendo o ficheiro. ");

                }
            }else{
                File subdir = new File(getFilesDir()+"/"+directory_et.getText().toString());
                File ruta = new File(subdir,nomeFicheiro);
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ruta)));

                    while ((linha = br.readLine()) != null)
                        tv.append(linha + "\n");

                    br.close();

                } catch (Exception ex) {
                    if (nomeFicheiro.equals("")) {
                        Toast.makeText(this, "Error reading file: " + subdir.toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error reading file: " + subdir.toString() + "/" + nomeFicheiro, Toast.LENGTH_SHORT).show();
                    }

                    Log.e("INTERNA", "Erro lendo o ficheiro. ");

                }
            }
        }else if(sd_rb.isChecked()){
            nomeFicheiro = file_et.getText().toString();
            establecerDirectorioFicheiro();
            String linha = "";
            tv.setText(linha);

            if (sdDisponhible) {
                try {

                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(rutaCompleta)));

                    while ((linha = br.readLine()) != null)
                        tv.append(linha + "\n");

                    br.close();

                } catch (Exception ex) {
                    Toast.makeText(this, "Error reading file: "+rutaCompleta, Toast.LENGTH_SHORT).show();
                    Log.e("SD", "Erro lendo o ficheiro. ");

                }
            } else
                Toast.makeText(this, "A tarxeta SD non está dispoñible", Toast.LENGTH_SHORT).show();

        }
    }

    public void onDeleteClick(View view) {
        if(raw_rb.isChecked()){
            Toast.makeText(this,"You can't delete in RAW memory",Toast.LENGTH_LONG).show();
        }else if(internal_rb.isChecked()){
            nomeFicheiro = file_et.getText().toString();
            File directorio_app;
            if(directory_et.getText().toString().equals("")) {
                directorio_app = getFilesDir();
            }else{
                directorio_app = new File(getFilesDir()+"/"+directory_et.getText().toString());
            }
            File ruta_completa = new File(directorio_app, "/" + nomeFicheiro);

            if (ruta_completa.delete()) {
                Toast.makeText(this,ruta_completa.toString()+" has been deleted",Toast.LENGTH_LONG).show();
                Log.i("INTERNA", "Ficheiro borrado");
            }else {
                Log.e("INTERNA", "Problemas borrando o ficheiro");
                Toast.makeText(this, "There are problems deleting "+ruta_completa.toString()+". Maybe is not an empty dir", Toast.LENGTH_SHORT).show();

            }
        }else if(sd_rb.isChecked()){
            nomeFicheiro = file_et.getText().toString();
            establecerDirectorioFicheiro();
            if (sdAccesoEscritura) {
                if(nomeFicheiro.equals("")){
                    if (dirFicheiroSD.delete()) {
                        if(dirFicheiroSD == filesDir){
                            Toast.makeText(this, "There are problems deleting "+dirFicheiroSD.toString()+". Maybe is not an empty dir", Toast.LENGTH_SHORT).show();
                        }else {
                            Log.i("SD", "Ficheiro borrado");
                            Toast.makeText(this, dirFicheiroSD.toString() + " has been deleted", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Log.e("SD", "Problemas borrando o ficheiro");
                        Toast.makeText(this, "There are problems deleting "+dirFicheiroSD.toString()+". Maybe is not an empty dir", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if (rutaCompleta.delete()) {
                        Toast.makeText(this,rutaCompleta.toString()+" has been deleted",Toast.LENGTH_LONG).show();
                        Log.i("SD", "Ficheiro borrado");
                    }else {
                        Log.e("SD", "Problemas borrando o ficheiro");
                        Toast.makeText(this, "There are problems deleting "+rutaCompleta.toString()+". Maybe is not an empty dir", Toast.LENGTH_SHORT).show();
                    }
                }
            } else{
                Toast.makeText(this, "A tarxeta SD non está en modo acceso escritura", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onListClick(View view) {
        tv.setText("");
        if(raw_rb.isChecked()){
            tv.setText("RAW Content: \n");
            tv.append("  File: adeus\n");
            tv.append("  File: ola\n");
        }else if(internal_rb.isChecked()){
            nomeFicheiro = file_et.getText().toString();
            File directorio_app;
            if(directory_et.getText().toString().equals("")) {
                directorio_app = getFilesDir();
            }else {
                directorio_app = new File(getFilesDir()+"/"+directory_et.getText().toString());
            }
            if(!directorio_app.exists()){
                Toast.makeText(this, "Error listing file: "+directorio_app.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }
            tv.append(directorio_app.getAbsolutePath() + " Content:");
            try {
                String[] files = directorio_app.list();

                for (int i = 0; i < files.length; i++) {
                    File subdir = new File(directorio_app, "/" + files[i]);
                    if (subdir.isDirectory())
                        tv.append("\n  Subdirectory: " + files[i]);
                    else
                        tv.append("\n  File: " + files[i]);
                }
                Log.i("INTERNA", "Listado realizado");

            } catch (Exception ex) {
                Log.e("INTERNA", "Erro listando o directorio");
            }
        }else if(sd_rb.isChecked()){
            nomeFicheiro = file_et.getText().toString();
            establecerDirectorioFicheiro();
            if (sdDisponhible) {
                tv.append(dirFicheiroSD.getAbsolutePath() + "\nContido:");
                if(!dirFicheiroSD.exists()){
                    Toast.makeText(this, "Error listing file: "+dirFicheiroSD.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }


                try {
                    String[] files = dirFicheiroSD.list();

                    for (int i = 0; i < files.length; i++) {
                        File subdir = new File(dirFicheiroSD, "/" + files[i]);
                        if (subdir.isDirectory())
                            tv.append("\n  Subdirectorio: " + files[i]);
                        else
                            tv.append("\n  Ficheiro: " + files[i]);
                    }
                    Log.i("SD", "Listado realizado");

                } catch (Exception ex) {
                    Log.e("SD", "Erro listando o directorio");
                }

            } else{
                Toast.makeText(this, "A tarxeta SD non está dispoñible", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
