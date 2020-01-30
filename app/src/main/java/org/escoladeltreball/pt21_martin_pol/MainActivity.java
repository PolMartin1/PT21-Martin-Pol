package org.escoladeltreball.pt21_martin_pol;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.media.VolumeAutomation;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private SoundPool pool;
    private static final String TAG = "test";
    private List<String> mDataSet;
    private int soundId1;
    private int soundId2;
    private int soundId3;
    private int soundId4;
    private BroadcastReceiver receiver;
    private boolean mpReady;
    private boolean bjustPassed;
    private RecyclerView recyclerView;
    private View startButton;
    private View stopButton;
    MediaRecorder recorder;
    File audiofile = null;

    int loaded;

    ImageButton imgbtpingpong;
    ImageButton imgbtreloj;
    ImageButton imgbtcerdo;
    ImageButton imgbtgrito;
    Button btnStart_recording, btnStop_Recorging, btnShow_Recorgings;

    String songURL;
    MediaPlayer myMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        imgbtpingpong = (ImageButton) findViewById(R.id.imbtpingpong);
        imgbtreloj = (ImageButton) findViewById(R.id.imbtrellotge);
        imgbtcerdo = (ImageButton) findViewById(R.id.imbtporc);
        imgbtgrito = (ImageButton) findViewById(R.id.imbtcrit);
        btnShow_Recorgings = (Button) findViewById(R.id.btnShow_Recordings);
        startButton = findViewById(R.id.btnStart_Recording);
        stopButton = findViewById(R.id.btnStop_Recording);

        imgbtpingpong.setOnClickListener(this);

        imgbtpingpong.setOnClickListener(this);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        imgbtreloj.setOnClickListener(this);
        imgbtcerdo.setOnClickListener(this);
        imgbtgrito.setOnClickListener(this);

        pool = new SoundPool(1,AudioManager.STREAM_MUSIC,0);
        pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded++;
            }
        });

        soundId1 = pool.load(this,R.raw.pingpong,1);
        soundId2 = pool.load(this,R.raw.alarm,1);
        soundId3 = pool.load(this,R.raw.cerdo,1);
        soundId4 = pool.load(this,R.raw.grito,1);
    }

    public void startRecording(View view) throws IOException{
        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        File sampleDir = Environment.getExternalStorageDirectory();
        try {
            audiofile = File.createTempFile("sound", ".3gp", sampleDir);
        } catch (IOException e) {
            Log.e(TAG, "sdcard access error");
            return;
        }
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        recorder.prepare();
        recorder.start();
    }

    public void stopRecording(View view) {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        recorder.stop();
        recorder.release();
        addRecordingToMediaLibrary();
    }

    protected void addRecordingToMediaLibrary() {
        ContentValues values = new ContentValues(4);
        long current = System.currentTimeMillis();
        values.put(MediaStore.Audio.Media.TITLE, "audio" + audiofile.getName());
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
        values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());
        ContentResolver contentResolver = getContentResolver();

        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
        Toast.makeText(this, "Added File " + newUri, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.musica) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
            if(id==R.id.imbtpingpong){
                pool.play(soundId1,1,1,0,0,1);
            }else if(id==R.id.imbtrellotge){
                pool.play(soundId2,1,1,0,0,1);
            }else if(id==R.id.imbtporc){
                pool.play(soundId3,1,1,0,0,1);
            }else if(id==R.id.imbtcrit){
                pool.play(soundId4,1,1,0,0,1);
            }else if(id==R.id.musica){
                bootstrapMediaPlayer();
            }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public void bootstrapMediaPlayer(){
        myMediaPlayer = MediaPlayer.create(this,R.raw.musica);
        myMediaPlayer.start();
    }

    //getExternarFilesDir

    @Override
    protected void onResume() {
        super.onResume();
        bootstrapMediaPlayer();
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        IntentFilter filter2 = new IntentFilter(AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(receiver, filter);
        registerReceiver(receiver, filter2);
    }

    public class MusicLoudReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)){
                if(myMediaPlayer.isPlaying()){
                    Toast.makeText(context, "Music stoped.", Toast.LENGTH_SHORT).show();
                    mpReady = false;
                    myMediaPlayer.stop();
                    bjustPassed=true;
                }
            }
        }
    }
}
