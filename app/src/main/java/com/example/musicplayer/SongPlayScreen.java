package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

// TODO: notification bar player, widget and lockscreen controls and display + permissions for those

public class SongPlayScreen extends AppCompatActivity {

    private float currentVolume;
    public static int currentSongPos;
    private static AudioManager audioManager;

    // mediaplayer is being used
    private MediaPlayer mediaPlayer;
    private Button play;
    private SeekBar seekBar;
    private TextView songDisp;
    // This is in milliseconds
    private long maxTime = 500;
    private long btnPressedTime;

    public SongPlayScreen() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_song_play_screen);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        play = findViewById(R.id.play_button);
        songDisp = findViewById(R.id.songNameDisplay);
        Button next = findViewById(R.id.next_button);
        Button previous = findViewById(R.id.previous_button);

        seekBar = findViewById(R.id.seekBarChange);


        songStateChange(currentSongPos);
        playNextSong(next);
        playPrevSong(previous);
    }

    private void songStateChange(int position) {
        MediaPlayerControl mpControlClass =
                new MediaPlayerControl(position + 1, mediaPlayer, this, seekBar);
        MediaPlayerControl.songControls(mpControlClass, play, songDisp, this);
        Thread seekBarThread = new Thread(mpControlClass);
        seekBarThread.start();
    }

        void playNextSong(Button Next) {

                Next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        new CountDownTimer(500,250) {
//                            @Override
//                            public void onTick(long millisUntilFinished) {
//
//                            }
//
//                            @Override
//                            public void onFinish() {
                                if (currentSongPos == MediaPlayerControl.songsSize - 1)
                                    currentSongPos = -1;
                                songStateChange(currentSongPos + 1);
//                            }
//                        };

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

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
}