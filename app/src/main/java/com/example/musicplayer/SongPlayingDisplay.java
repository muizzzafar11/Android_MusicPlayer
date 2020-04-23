package com.example.musicplayer;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

class SongPlayingDisplay {
    private int pos;
    private String title, uri;
    private MediaPlayer mediaPlayer;
    private Activity activity;

    SongPlayingDisplay(int _pos, String _title, String _uri, MediaPlayer _mp, Activity _activity) {
        this.pos = _pos;
        this.title = _title;
        this.uri = _uri;
        this.mediaPlayer = _mp;
        this.activity = _activity;
    }

     static MediaPlayer InitializePlayer(MediaPlayer mp, Activity activity) {
        mp = new MediaPlayer();
        // To set the Volume to that of mediaPlayer when the song starts

         mp.setAudioAttributes(new AudioAttributes.Builder()
                 .setLegacyStreamType(activity.getVolumeControlStream())
                 .setUsage(AudioAttributes.USAGE_MEDIA)
                 .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                 .build());
        return mp;
    }

     void playPause() {
         if (!mediaPlayer.isPlaying()) {
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
         } else {
             mediaPlayer.pause();
         }
     }
}
