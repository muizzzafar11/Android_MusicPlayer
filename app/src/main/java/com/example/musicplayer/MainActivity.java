package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

// layout manager: arranges items into rows and columns, image and text.
// view holder: different components to hold
// adapter: place view holder into proper position

// TODO: make a separate void for reading the files from storage using cursor
// TODO: in the future make a separate class for reading files off of the phone storage and if that doesnt work then go to the cursor reading off of the phone audio storage.

public class MainActivity extends AppCompatActivity implements SongRecyclerAdapter.OnSongClickListener {
    // Accessing files
    Cursor cursor;
    Uri externalStoreUri;
    ContentResolver contentResolver;

    // RecyclerView
    RecyclerView songListView;
    SongRecyclerAdapter songListViewAdapter;
    RecyclerView.LayoutManager layoutManager;

    Intent intent;
    Permissions permissions;
    SongPlayScreen songPlayScreen;

    TextView bottomTV;
    Button play, next, prev;
    ConstraintLayout bottomBar;

    MediaPlayer mediaPlayer;
    private boolean flag = false;

    MediaPlayerHandler songPlayDisp;

    // Storing the names
    static ArrayList<HashMap<String, String>> songList = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
//        BottomBarControls();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        songListView = findViewById(R.id.SongList);
        layoutManager = new LinearLayoutManager(this);
        songListView.setLayoutManager(layoutManager);

        bottomTV = findViewById(R.id.bottomTextView);
        play = findViewById(R.id.bottomPlayButton);
        bottomBar = findViewById(R.id.bottomSongDispLayout);

        permissions = new Permissions(this);

        if(permissions.readExternalStoragePermission()){
            contentResolver = this.getContentResolver();
            externalStoreUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            cursor = contentResolver.query(externalStoreUri, null, null, null, null);

            songList = readAudioStorage(cursor);

            songListViewAdapter = new SongRecyclerAdapter(songList, "Title:", this);
            songListView.setHasFixedSize(true);
            songListView.setAdapter(songListViewAdapter);

            mediaPlayer = MediaPlayerHandler.InitializePlayer(mediaPlayer, this);
        } else {
            Toast.makeText(this, "Permission Not Granted, Please restart the app", Toast.LENGTH_LONG).show();
        }
    }

    void BottomBarControls(String title, MediaPlayer bottomMediaPlayer) {
        if(mediaPlayer != null){
            DispBottomText(title);
            PlayPauseBottom(bottomMediaPlayer);
        }
    }

    void bottomBarClicked() {
        if(intent != null){
            bottomBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    songPlayScreen = new SongPlayScreen(songList.get(position).get("Title:"), songList.get(position).get("URI:"), position, songList);
//                    intent = new Intent(this, songPlayScreen.getClass());
//                    startActivity(intent);
                }
            });
        }
    }

    void PlayPauseBottom(MediaPlayer bottomM_P) {
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    void DispBottomText(String songTitle) {
            bottomTV.setText(songTitle);
            bottomTV.setSelected(true);
    }

    ArrayList<HashMap<String, String>> readAudioStorage(Cursor _cursor) {
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
        return allFiles;
    }

    @Override
    public void onClickSong(int position) {
        stopInitializePlayer(true);
        song(position);
    }

    void stopInitializePlayer(boolean initialize) {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            if(initialize)
                mediaPlayer = MediaPlayerHandler.InitializePlayer(mediaPlayer, this);
        }
    }

    public void song(int position) {
        BottomBarControls(songList.get(position).get("Title:"), mediaPlayer);
        MediaPlayerHandler MPH =
                new MediaPlayerHandler(songList.get(position).get("URI:"), mediaPlayer, this);
        MPH.playPause(play, position);
//        songPlayScreen = new SongPlayScreen(songList.get(position).get("Title:"), songList.get(position).get("URI:"), position, songList);
//        intent = new Intent(this, songPlayScreen.getClass());
//        startActivity(intent);
    }
}
