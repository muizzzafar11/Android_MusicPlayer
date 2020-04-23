package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashMap;

// *** ----> working on it rn, ******-----> done the task
// TODO: -----------------------------------DOCUMENTATION -------------------------------------------
// ******TODO: Work on making it so that when a new song starts(clicked in the recycler view), the previous one stops automatically
// TODO: Work on the previous and next song button stuff
// ******TODO: make the code DRY and less redundant, rn it's all over the place
// TODO: make it so that if the play button is pressed after the song ends, it doesn't play the song
// TODO: work on automatically playing the next song after the previous one ends
// TODO: Volume controls when it is running in the background
// TODO: Make one activity for song play screen as well
// TODO: Headphones plugged in
// The task above will be able to work if we have a notification activity
// ******TODO: Seekbar
// ******TODO: arranging songs in alphabetical order
// ******TODO: when there is no song display and empty list, catch that exception as well
// ******TODO: control the song volume with the song buttons
// TODO: research on the galaxy buds touch control for app and implement it
// ******TODO: Headphones plugged in
// TODO: notification bar player, widget and lockscreen controls and display + permissions for those
// ******TODO: when previous button is pressed, keep the activity open so that the song doesn't restart
public class SongPlayScreen extends AppCompatActivity {

    static String displayName;
    static String uri;
    static String prevUri = "";
    float currentVolume;

    private boolean flag = false;
    static AudioManager audioManager;
    static Handler SeekbarUpdateHandler;
    static Runnable UpdateSeekbar;

    static MediaPlayer mediaPlayer;
    public Button playPause, next, previous;
    private SeekBar seekBar;
    TextView songDisp;
    static int position;
    static ArrayList<HashMap<String, String>> songList;

    public SongPlayScreen() {
    }

    public SongPlayScreen(String _displayName, String _uri, int _position,
                          ArrayList<HashMap<String, String>> _songList) {
        displayName = _displayName;
        uri = _uri;
        position = _position;
        songList = _songList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_play_screen);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        playPause = findViewById(R.id.play_button);
        songDisp = findViewById(R.id.songNameDisplay);
        songDisp.setText(displayName);
        songDisp.setSelected(true);
        seekBar = findViewById(R.id.seekBar);
        next = findViewById(R.id.next_button);

        if (!prevUri.equals(uri) || prevUri.isEmpty()) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }
            mediaPlayer = InitializePlayer(mediaPlayer);
            SeekbarUpdateHandler = new Handler();

        }

        UpdateSeekbar = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                SeekbarUpdateHandler.postDelayed(this, 0);

                float volumeUp = (float) (6.666666666666667 * (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))) / 100;
                mediaPlayer.setVolume(volumeUp, volumeUp);
            }
        };

        if (mediaPlayer != null) {
            SongControls(mediaPlayer, playPause, uri, seekBar, next, position);
        }

    }

    private MediaPlayer InitializePlayer
            (MediaPlayer mp) {

        // Initializing MediaPlayer again, after the previous song has been stopped
        mp = new MediaPlayer();

        // To set the Volume to that of mediaPlayer when the song starts
        mp.setAudioAttributes(new AudioAttributes.Builder()
                .setLegacyStreamType(getVolumeControlStream())
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        currentVolume = (float) (6.666666666666667 * (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))) / 100;
        mp.setVolume(currentVolume, currentVolume);

        return mp;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    void SongControls(MediaPlayer mediaPlayer, Button play, String uri, SeekBar progressBar, Button nextButton, int pos) {
        songPlayPause(mediaPlayer, play, uri);
        seekBarUserProgress(progressBar, mediaPlayer);
        songNext(nextButton, pos);
    }


    void songPlayPause(final MediaPlayer mediaPlayer, Button play, String uri) {
        if (!prevUri.equals(uri) || prevUri.isEmpty()) {
            try {
                mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(uri));
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                Toast.makeText(this, "No Song Found. Please check the file path.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    seekBar.setMax(mp.getDuration());
                    SeekbarUpdateHandler.postDelayed(UpdateSeekbar, 0);
                }
            });
            // Setting it equal here so that it doesn't mess up with the .start() if statement
            // This will be triggered every time. It can be thought of as an extension of previous if statement
            prevUri = uri;
        }
//        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayClick(mediaPlayer);
            }
        });
    }

    void onPlayClick(final MediaPlayer mediaPlayer) {
        if (!flag && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
//            SeekbarUpdateHandler.removeCallbacks(UpdateSeekbar);
            flag = false;
        } else if (!flag) {
            mediaPlayer.start();
//            SeekbarUpdateHandler.postDelayed(UpdateSeekbar, 0);
            flag = true;
        } else if (mediaPlayer.isPlaying() && flag) {
            mediaPlayer.pause();
//            SeekbarUpdateHandler.removeCallbacks(UpdateSeekbar);
            flag = false;
        }
    }

    void songNext(Button nextButton, final int pos) {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void seekBarUserProgress(SeekBar progressBar, final MediaPlayer mp) {

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mp.seekTo(progress);
//                if(progress == mp.getDuration())
//                    mp.setNextMediaPlayer(mp);// Pass in the next mediaPlayer
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}