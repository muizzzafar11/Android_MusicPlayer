package com.example.musicplayer;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import static com.example.musicplayer.MainActivity.songCurrentPosition;

class MediaPlayerControl {
    private static String uri;
    private static String checkUri;
    private static String title;
    private static MediaPlayer controlClassMP;
    private Activity activity;
    private boolean flag = false;

    MediaPlayerControl(String _uri, String _title, MediaPlayer _mp, Activity _activity) {
        uri = _uri;
        title = _title;
        if(controlClassMP != null && checkUri != null && !checkUri.equals(_uri))
            stopPlayer();
        if(checkUri == null || !checkUri.equals(_uri))
            controlClassMP = _mp;
        activity = _activity;
    }

    MediaPlayerControl(Activity _activity) {
        this.activity = _activity;
    }

    static MediaPlayer InitializePlayer(MediaPlayer mp, Activity activity) {
        mp = new MediaPlayer();
         mp.setAudioAttributes(new AudioAttributes.Builder()
                 .setLegacyStreamType(activity.getVolumeControlStream())
                 .setUsage(AudioAttributes.USAGE_MEDIA)
                 .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                 .build());
        return mp;
    }

    private void stopPlayer() {
        if(controlClassMP.isPlaying()) {
            controlClassMP.stop();
            controlClassMP.reset();
            controlClassMP.release();
        }
    }

    static void songControls
            (int position, MediaPlayerControl MpControl, Button play, TextView TV, Activity activity) {
        if(checkUri == null || !checkUri.equals(uri))
            controlClassMP = InitializePlayer(controlClassMP, activity);
        songCurrentPosition = position;
        DispText(title, TV);
        MpControl.playPause(play);
    }

    private static void DispText(String songTitle, TextView TV) {
        TV.setText(songTitle);
        TV.setSelected(true);
    }

    private void playPause(Button playPause) {
        if (checkUri == null || !checkUri.equals(uri)) {
            try {
                controlClassMP.setDataSource(activity.getApplicationContext(), Uri.parse(uri));
                controlClassMP.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        controlClassMP.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        }
        checkUri = uri;
         playPause.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 buttonPlayPause();
             }
         });
     }

    private void buttonPlayPause() {
         if (!flag && controlClassMP.isPlaying()) {
             controlClassMP.pause();
             flag = false;
         } else if (!flag) {
             controlClassMP.start();
             flag = true;
         } else if (controlClassMP.isPlaying() && flag) {
             controlClassMP.pause();
             flag = false;
         }
     }

}
