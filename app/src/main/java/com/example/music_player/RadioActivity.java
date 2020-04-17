package com.example.music_player;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class RadioActivity extends AppCompatActivity {
    Button btnradio;
    boolean prepared = false;
    boolean started = false;

    String stream = "http://radio.net.bd/embed/foorti/";
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        btnradio = findViewById(R.id.btnradio);
        btnradio.setEnabled(false);
        btnradio.setText("Loading...");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        new PlayerTask().execute(stream);
        btnradio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (started)
                {
                    started = false;
                    mediaPlayer.pause();
                    btnradio.setText("Play");
                }
                else
                {
                    started = true;
                    mediaPlayer.start();
                    btnradio.setText("Pause");
                }

            }
        });
    }
    class PlayerTask extends AsyncTask<String, Void, Boolean>
    {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                prepared = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            btnradio.setEnabled(true);
            btnradio.setText("Play");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (started)
        {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (started)
        {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (prepared)
        {
            mediaPlayer.release();
        }
    }
}
