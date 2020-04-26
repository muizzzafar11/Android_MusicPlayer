package com.example.musicplayer;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import static com.example.musicplayer.MainActivity.songList;
import static com.example.musicplayer.MainActivity.songCurrentPosition;

class MediaPlayerHandler {
    private String uri;
    private static String title;
    private static MediaPlayer mediaPlayer;
    private Activity activity;
    private boolean flag = false;

    MediaPlayerHandler(String _uri, String _title, MediaPlayer _mp, Activity _activity) {
        this.uri = _uri;
        title = _title;
        if(mediaPlayer != null)
            stopPlayer();
        mediaPlayer = _mp;
        activity = _activity;
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
        if(mediaPlayer.isPlaying()) {
           mediaPlayer.stop();
           mediaPlayer.reset();
           mediaPlayer.release();
        }
    }

    static void songControls(int position, MediaPlayerHandler MPH, Button play, TextView TV, Activity activity) {
        mediaPlayer = InitializePlayer(mediaPlayer, activity);
        songCurrentPosition = position;
        DispText(title, TV);
        MPH.playPause(play, position);
    }

    private static void DispText(String songTitle, TextView TV) {
        TV.setText(songTitle);
        TV.setSelected(true);
    }

    private void playPause(Button playPause, int pos) {
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
