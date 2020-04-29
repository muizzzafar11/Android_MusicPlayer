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

    private String displayName;
    private String uri;

    private float currentVolume;
    public static int currentSongPos;
    private static AudioManager audioManager;

    private static Handler seekBarUpdateHandler;
    private static Runnable updateRunSeekBar;

    private MediaPlayer mediaPlayer;
    private MediaPlayerControl MpControlClass;
    private Button play, next, previous;
    private SeekBar seekBar;
    private TextView songDisp;

    public SongPlayScreen() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_song_play_screen);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

//        currentSongPos = MainActivity.songCurrentPosition;

        play = findViewById(R.id.play_button);
        songDisp = findViewById(R.id.songNameDisplay);
        next = findViewById(R.id.next_button);
        previous = findViewById(R.id.previous_button);

        seekBar = findViewById(R.id.seekBar);

        songStateChange(currentSongPos);
        playNextSong(next);
        playPrevSong(previous);

        seekBarUpdateHandler = new Handler();
        updateRunSeekBar = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekBarUpdateHandler.postDelayed(this, 0);
            }
        };
    }

    private void songStateChange(int position) {
        MpControlClass = new MediaPlayerControl(position + 1, mediaPlayer, this);
        MediaPlayerControl.songControls(MpControlClass, play, songDisp, this);
    }

    void playNextSong(Button Next) {
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSongPos == MediaPlayerControl.songsSize - 1)
                    currentSongPos = -1;
                songStateChange(currentSongPos + 1);
            }
        });
    }

    void playPrevSong(Button Prev) {
        Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSongPos == 0)
                    currentSongPos = MediaPlayerControl.songsSize;
                songStateChange(currentSongPos - 1);
            }
        });
    }

    //    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_VOLUME_UP:
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                // MIN = 0;
//                // MAX = 15;// TODO: CHECK WHETHER THIS HOLDS TRUE IN OTHER PHONES OR NOT.
//                        AudioManager.ADJUST_LOWER,
//                audioManager.adjustStreamVolume(
//                        AudioManager.STREAM_MUSIC,
//                        AudioManager.ADJUST_RAISE,
//                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
//                float volumeUp = (float) (6.666666666666667 * (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))) / 100;
//                mediaPlayer.setVolume(volumeUp, volumeUp);
//                break;
//            default:
//                break;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

//    private MediaPlayer InitializePlayer
//            (MediaPlayer mp) {
//
//        // Initializing MediaPlayer again, after the previous song has been stopped
//        mp = new MediaPlayer();
//
//        // To set the Volume to that of mediaPlayer when the song starts
//        mp.setAudioAttributes(new AudioAttributes.Builder()
//                .setLegacyStreamType(getVolumeControlStream())
//                .setUsage(AudioAttributes.USAGE_MEDIA)
//                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                .build());
//        return mp;
//    }

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    private void seekBarUserProgress(SeekBar progressBar, final MediaPlayer mp) {

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mp.seekTo(progress);
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