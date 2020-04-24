package com.example.musicplayer;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

class MediaPlayerHandler {
    private String uri;
    private MediaPlayer mediaPlayer;
    private Activity activity;
    private boolean flag = false;
    private int checkPos;

    MediaPlayerHandler(String _uri, MediaPlayer _mp, Activity _activity) {
        this.uri = _uri;
        this.mediaPlayer = _mp;
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


     void playPause(Button playPause, int pos) {
             try {
                 mediaPlayer.setDataSource(activity.getApplicationContext(), Uri.parse(uri));
                 mediaPlayer.prepareAsync();
             } catch (IOException e) {
                 e.printStackTrace();
             }
             mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                 @Override
                 public void onPrepared(MediaPlayer mp) {
                     mp.start();
                 }
             });
             checkPos = pos;
         playPause.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 buttonPlayPause();
             }
         });
     }

     private void buttonPlayPause() {
         if (!flag && mediaPlayer.isPlaying()) {
             mediaPlayer.pause();
             flag = false;
         } else if (!flag) {
             mediaPlayer.start();
             flag = true;
         } else if (mediaPlayer.isPlaying() && flag) {
             mediaPlayer.pause();
             flag = false;
         }
     }
}
