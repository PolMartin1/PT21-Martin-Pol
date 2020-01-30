package org.escoladeltreball.pt21_martin_pol;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ContentProv extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "test";
    Button contactes, gravacions;
    Button trucades;
    TextView textView;
    CustomAdapterE mAdapter;

    private static final int MY_PERMISSIONS_REQUESTS = 70;
    Boolean bGranted = false;
    RecyclerView recyclerView;
    List<String> llista = new ArrayList<String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentprov);

        //passo de manera estàtica les dades al recycler
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        textView=(TextView) findViewById(R.id.texto);
        bGranted = false;
        try {

            textView.setText("");
            //  Log.d(TAG, "test: " + "gravacions:" + getResources().getString(R.string.example));
            contactes = (Button) findViewById(R.id.contactes);
            trucades = (Button) findViewById(R.id.trucades);
            textView = (TextView) findViewById(R.id.texto);
            gravacions = (Button) findViewById(R.id.gravacions);

            contactes.setOnClickListener(this);
            trucades.setOnClickListener(this);
            gravacions.setOnClickListener(this);



        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "test: " + e.getMessage() + e.getCause());
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contactes:

                checkPermissions();
                Log.d(TAG, "onClick: " + bGranted);
                if (bGranted) ObtenirDadesContactes();
                //contactes

                break;
            case R.id.trucades:

                checkPermissions();
                Log.d(TAG, "onClick: " + bGranted);
                if (bGranted) ObtenirDadesTrucades();


                break;
            case R.id.gravacions:

                checkPermissions();
                //Log.d(TAG, "onClick:gravacions "+bGranted);
                if (bGranted) {
                    ObtenirDadesGravacions();
                }
                break;
            default:
                break;
        }

        if (bGranted && llista.size()>0) {
            //llista actualitzada, creo nou adapter, i events...
                    mAdapter = new CustomAdapterE(llista);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(new CustomAdapterE.OnItemClickListener(){
                        @Override
                        public void onItemClick(int position) {

                            try {
                                Toast.makeText(ContentProv.this, "Clicat a posició" + position, Toast.LENGTH_SHORT).show();
                             //   PlaySound(position);
                                //   Intent intent = new Intent(ContentProv.this, ActivityDisplayBloc.class);
                             //   intent.putExtra("Bloc", llista.get(position));
                             //   startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("test", "onItemClick: "+e.getMessage()+e.getCause());

                            }
                        }

                        @Override
                        public void onLongItemClick(int position) {
                            // TODO: 30/01/20 expandable ...
                            llista.remove(position);
                            mAdapter.notifyItemRemoved(position);
                            Toast.makeText(ContentProv.this, "fila esborrada:" + position, Toast.LENGTH_SHORT).show();

                        }

                    });


        }
    }

    public void PlaySound (int position) throws IOException {
        final int[] loaded = {0};


        final MediaPlayer myMediaPlayer = new MediaPlayer();
        myMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        myMediaPlayer.setDataSource(llista.get(position));

        if (!myMediaPlayer.isPlaying()) {
            myMediaPlayer.prepareAsync();
            myMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "Starting music");
                    // mpReady = true;
                    myMediaPlayer.start();
                }
            });
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUESTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED ) {
                    // permission was granted, do your work....
                    Log.d("test", "tot concedit");
                    bGranted = true;
                    //bGranted, endavant per a fer coses


                } else {
                    bGranted = false;
                    //ha de parar l'accés a les crides ..

                    // permission denied
                    // Disable the functionality that depends on this permission.

                }
                return;
            }

            // other 'case' statements for other permssions
        }
    }


    public void checkPermissions() {

        int permCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int permCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permCheck3 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);


        if (!(permCheck1 == PackageManager.PERMISSION_GRANTED) |
                !(permCheck2 == PackageManager.PERMISSION_GRANTED) |
                !(permCheck3== PackageManager.PERMISSION_GRANTED)) {

            //ara cal demanar permissos...o el que falta
            // si l'ha negat una vegada, li dona explicació, i torna a demanar-lo.
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) |
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALL_LOG))
                    | (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)))) {


                //menu dialeg
                new AlertDialog.Builder(this)
                        .setTitle("Es necessita permís d'accés a lectura d'agenda de contactes, SD i trucades fetes")
                        .setMessage("Necessitem accedir al disc i tenir permís de lectura de ")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(ContentProv.this
                                        ,  new String[]{Manifest.permission.READ_CALL_LOG,
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.READ_CONTACTS
                                        },
                                        MY_PERMISSIONS_REQUESTS);
                            }
                        })
                        .setNegativeButton("cancel.lar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();



            } else {
                // request the permission.
                // CALLBACK_NUMBER is a integer constants
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.READ_CALL_LOG}, MY_PERMISSIONS_REQUESTS);

            }


        } else bGranted = true;


    }

    public void ObtenirDadesGravacions() {

        Uri uri;
        /*
        content://media/internal/images
        content://media/external/video
        content://media/internal/audio     */
        //content://media/*/images
        //content://settings/system/ringtones
        try {


            //mecanisme d emmagatzemament com les BBDD
            //per compartir info entre apps
            //no creem, els consumim CRUD, contacts, trucades enviades o rebudes

            uri = Uri.parse("content://media/external/audio/media");
         //   uri = Uri.parse("content://media/external/video/media");

            String[] projeccion = new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media._ID
                    };
        //    https://developer.android.com/guide/topics/providers/content-providers?hl=en
            Cursor c = getContentResolver().query(
                    uri,
                    projeccion,
                    null,
                    null,
                    null);

            llista.clear();
            while (c.moveToNext()) {

                    llista.add(" Name: " + c.getString(0) +
                            " \n Id: " + c.getString(1) +" ");
                    //+  "\n Extra: "  + "\n \n");
                }
            c.close();

            Toast.makeText(this, "size:" + String.valueOf(llista.size()), Toast.LENGTH_SHORT).show();
            if (llista.size() == 0)
                    Toast.makeText(this, "No hi ha cap gravació a mostrar", Toast.LENGTH_SHORT).show();



        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "ObtenirDadesGravacions: " + e.getCause() + e.getMessage());
        }


    }


    public void ObtenirDadesTrucades() {
    //    List<String> llista2 = new ArrayList<String>();

        Uri uri;
        /*
        content://media/internal/images
        content://media/external/video
        content://media/internal/audio*/
        //         content://media/*/images
        //         content://settings/system/ringtones
        try {

            //android.permission.READ_CALL_LOG
            //or android.permission.WRITE_CALL_LOG

            uri = Uri.parse("content://call_log/calls");

            String[] projeccion = new String[]{CallLog.Calls.TYPE, CallLog.Calls.NUMBER,
                    CallLog.Calls.DURATION};

            Cursor c = getContentResolver().query(
                    uri,
                    projeccion,
                    null,
                    null,
                    null);

            textView.setText("");
            // Log.d(TAG, "ObtenirDadesTrucades: " + c.getCount());


            Toast.makeText(this, "Trucades: " + String.valueOf(c.getCount()), Toast.LENGTH_LONG).show();
            llista.clear();

            while (c.moveToNext()) {
                llista.add("Tipus: " + c.getString(0) + " Número: " + c.getString(1) +
                        " Duració: " + c.getString(2) + "\n");
            }
            c.close();

            if (llista.size() == 0)
                        Toast.makeText(this, "No hi ha cap trucada a mostrar", Toast.LENGTH_LONG).show();



            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "ObtenerDatosLlamadas: " + e.getCause() + e.getMessage());
            }



    }

        public void ObtenirDadesContactes() {
// contactes

            try {
                checkPermissions();
                String[] projeccion = new String[]{ContactsContract.Data._ID,
                        ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE,ContactsContract.Data.TIMES_USED};

                String selectionClause = ContactsContract.Data.MIMETYPE + "='" +
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND "
                        + ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";
                String sortOrder = ContactsContract.Data.DISPLAY_NAME + " ASC";

                Cursor c = getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI,
                        projeccion,
                        selectionClause,
                        null,
                        sortOrder);

                textView.setText("");
                Toast.makeText(this, String.valueOf(c.getCount()), Toast.LENGTH_LONG).show();
                Log.d(TAG, "ObtenirDadesContactes: " + c.getCount());

                llista.clear();
                while (c.moveToNext()) {
                    llista.add(" Nom: " + c.getString(1) +
                            " Núm.: " + c.getString(2) + " Tipo: " + c.getString(3) + "\n");
                }
                c.close();

                //Toast.makeText(this, "size:" + String.valueOf(llista.size()), Toast.LENGTH_LONG).show();
                if (llista.size() == 0)
                        Toast.makeText(this, "No hi ha cap contacte a mostrar", Toast.LENGTH_LONG).show();


            } catch (IllegalStateException e) {
                e.printStackTrace();
                Log.d(TAG, "stopRecording: " + e.getMessage() + e.getCause());
                Toast.makeText(this, "IllegalStateException" + e.getMessage() + e.getCause(), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                //        Toast.makeText(this, "Exception"+e.getMessage() + e.getCause(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Log.d(TAG, "ObtenerDatos: " + e.getMessage() + e.getCause());
            }


        /*contactsCursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,   // URI de contenido para los contactos
                projection,                        // Columnas a seleccionar
                selectionClause                    // Condición del WHERE
                selectionArgs,                     // Valores de la condición
                sortOrder);                        // ORDER BY columna [ASC|DESC]*/


        }

}