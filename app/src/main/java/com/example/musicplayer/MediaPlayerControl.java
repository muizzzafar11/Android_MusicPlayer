package com.example.musicplayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

import static com.example.musicplayer.MainActivity.songCurrentPosition;

class MediaPlayerControl {
    private static String uri;
    private static String checkUri;
    private static String title;
    private static int increasedPosition;
    private static int checkPos;
    private static int normalPos;
    static int songsSize;

    private static MediaPlayer controlClassMP;
    private Activity activity;

    private boolean flag = false;

    static ArrayList<HashMap<String, String>> allSongs = new ArrayList<>();

    MediaPlayerControl(int _increasedPosition, MediaPlayer _mp, Activity _activity) {
        increasedPosition = _increasedPosition;
        normalPos = increasedPosition-1;
        increasedPosition = _increasedPosition;
        if (controlClassMP != null && checkPos != 0 && checkPos != increasedPosition) {
            stopPlayer();
            controlClassMP = _mp;
        }
        activity = _activity;
    }

    MediaPlayerControl( Activity _activity) {
        this.activity = _activity;
    }

    static void ReadSongs(Activity activity) {
        ContentResolver contentResolver = activity.getContentResolver();
        Uri externalStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(externalStoreUri, null, null, null, null);
        allSongs = readAudioStorage(cursor);
    }

    private static ArrayList<HashMap<String, String>> readAudioStorage(Cursor _cursor) {
        ArrayList<HashMap<String, String>> allFiles = new ArrayList<>();
        if (_cursor == null) {
            System.out.println("query has failed, wrong cursor??");
        } else if (!_cursor.moveToFirst()) {
            System.out.println("No songs on the device");
        } else {
            int titleColumn = _cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = _cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            do {
                HashMap<String, String> song = new HashMap<>();
                long thisId = _cursor.getLong(idColumn);
                Uri songURI = ContentUris.withAppendedId(//sth. to this
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId);
                String thisTitle = _cursor.getString(titleColumn);
                song.put("Title:", thisTitle);
                song.put("URI:", songURI.toString());
                allFiles.add(song);
            } while (_cursor.moveToNext());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            allFiles.sort(new Comparator<HashMap<String, String>>() {
                @Override
                public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {

                    return (Objects.requireNonNull(Objects.requireNonNull(o1.get("Title:")).toLowerCase())).compareTo(Objects.requireNonNull(Objects.requireNonNull(o2.get("Title:")).toLowerCase()));
                }
            });
        }
        songsSize = allFiles.size();
        return allFiles;
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
        if (controlClassMP.isPlaying()) {
            controlClassMP.stop();
            controlClassMP.reset();
            controlClassMP.release();
        }
    }

    static void songControls (MediaPlayerControl MpControl, Button play, TextView TV, Activity activity) {
        if(allSongs != null) {
            title = allSongs.get(normalPos).get("Title:");
            uri = allSongs.get(normalPos).get("URI:");
        }
        if (checkPos == 0 || checkPos != increasedPosition)
            controlClassMP = InitializePlayer(controlClassMP, activity);
        songCurrentPosition = normalPos;
        DispText(title, TV);
        MpControl.playPause(play);
    }

    private static void DispText(String songTitle, TextView TV) {
        TV.setText(songTitle);
        TV.setSelected(true);
    }

    private void playPause(Button playPause) {
        if (checkPos == 0 || checkPos != increasedPosition) {
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
        checkPos = increasedPosition;
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