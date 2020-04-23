package com.example.music_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    Button btnpause,btnprev,btnnext,btnff,btnfr,btnloop;
    TextView txtsn,txtsstart,txtsstop;
    SeekBar seekmusic;
    BarVisualizer mVisualizer;
    String sname;
    ImageView imageView;
    public static final String EXTRA_NAME = "song_name";

    static MediaPlayer mediaPlayer;
    int position;

    ArrayList<File> mySongs;
    Thread updateSeekbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==android.R.id.home)
        {
            Intent mIntent=new Intent(PlayerActivity.this, MainActivity.class);
            sname = mySongs.get(position).getName().toString();
            mIntent.putExtra(EXTRA_NAME, sname);
            startActivity(mIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnpause = findViewById(R.id.playbtn);
        btnnext = findViewById(R.id.btnnext);
        btnprev = findViewById(R.id.btnprev);
        txtsn = findViewById(R.id.txtsn);
        seekmusic = findViewById(R.id.seekbars);
        txtsstart  =findViewById(R.id.txtsstart);
        txtsstop = findViewById(R.id.txtsstop);
        btnff = findViewById(R.id.btnff);
        btnfr = findViewById(R.id.btnfr);
        btnloop = findViewById(R.id.btnloop);

        mVisualizer = findViewById(R.id.bar);
        imageView = findViewById(R.id.imageview);



        updateSeekbar = new Thread()
        {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition<totalDuration)
                {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekmusic.setProgress(currentPosition);
                    }
                    catch (InterruptedException | IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };

        if (mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }


        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");

        sname = mySongs.get(position).getName().toString();
        String songName = i.getStringExtra("songname");
        txtsn.setSelected(true);

        position = bundle.getInt("pos",0);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sname = mySongs.get(position).getName().toString();
        txtsn.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnnext.performClick();
            }
        });

        //visualizer
        int audioSessionId = mediaPlayer.getAudioSessionId();
        if (audioSessionId != -1)
            mVisualizer.setAudioSessionId(audioSessionId);



        String endtime = createtime(mediaPlayer.getDuration());
        txtsstop.setText(endtime);

        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                String currenttime = createtime(mediaPlayer.getCurrentPosition());
                txtsstart.setText(currenttime);
                handler.postDelayed(this, delay);
            }
        }, delay);

        seekmusic.setMax(mediaPlayer.getDuration());

        updateSeekbar.start();
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);

        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });

        btnpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seekmusic.setMax(mediaPlayer.getDuration());
                if (mediaPlayer.isPlaying())
                {
                    btnpause.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();

                }
                else
                {
                    btnpause.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mySongs.size());

                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sname = mySongs.get(position).getName().toString();
                txtsn.setText(sname);
                mediaPlayer.start();
                btnpause.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);
                int audioSessionId = mediaPlayer.getAudioSessionId();
                if (audioSessionId != -1)
                    mVisualizer.setAudioSessionId(audioSessionId);
            }
        });

        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0)?(mySongs.size()-1):(position-1);

                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sname = mySongs.get(position).getName().toString();
                txtsn.setText(sname);
                mediaPlayer.start();
                btnpause.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);
                int audioSessionId = mediaPlayer.getAudioSessionId();
                if (audioSessionId != -1)
                    mVisualizer.setAudioSessionId(audioSessionId);

            }
        });

        btnff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });
        btnfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });
        btnloop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer!=null)
                {
                    if (mediaPlayer.isLooping())
                    {
                        mediaPlayer.setLooping(false);
                        btnloop.setBackgroundResource(R.drawable.ic_repeat);
                    }
                    else
                    {
                        mediaPlayer.setLooping(true);
                        btnloop.setBackgroundResource(R.drawable.ic_repeat_one);
                    }
                }
            }
        });



    }

    public String createtime(int duration)
    {
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;
        time+= min + ":";

        if (sec<10)
        {
            time+= "0";
        }
        time+=sec;

        return time;
    }
    public void startAnimation(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f , 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    protected void onDestroy() {
        if (mVisualizer != null)
            mVisualizer.release();
        super.onDestroy();
    }
}
