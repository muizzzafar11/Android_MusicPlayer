package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

// layout manager: arranges items into rows and columns, image and text.
// view holder: different components to hold
// adapter: place view holder into proper position

public class MainActivity extends AppCompatActivity implements SongRecyclerAdapter.OnSongClickListener {
    // RecyclerView
    RecyclerView songListView;
    SongRecyclerAdapter songListViewAdapter;
    RecyclerView.LayoutManager layoutManager;

    Intent intent;
    Permissions permissions;
    SongPlayScreen songPlayScreen;

    TextView bottomTV, mainTV;
    Button play, next, prev;
    Button normalPlay, normalNext, normalPrev;
    ConstraintLayout bottomBar;

    public static MediaPlayer mediaPlayer;
    MediaPlayerControl MPControl;
    public static int songCurrentPosition;
    String songName, songUri;

    // Storing the names
//    static ArrayList<HashMap<String, String>> songList = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        songListView = findViewById(R.id.SongList);
        layoutManager = new LinearLayoutManager(this);
        songListView.setLayoutManager(layoutManager);

        bottomTV = findViewById(R.id.bottomTextView);
        play = findViewById(R.id.bottomPlayButton);
        next = findViewById(R.id.bottomNextButton);
        prev = findViewById(R.id.bottomPrevButton);
        bottomBar = findViewById(R.id.bottomSongDispLayout);

        permissions = new Permissions(this);
        if (permissions.readExternalStoragePermission()) {
            MediaPlayerControl.ReadSongs(this);
//            songList = MediaPlayerControl.allSongs;

            songListViewAdapter = new SongRecyclerAdapter(MediaPlayerControl.allSongs, "Title:", this);
            songListView.setHasFixedSize(true);
            songListView.setAdapter(songListViewAdapter);

            mediaPlayer = MediaPlayerControl.InitializePlayer(mediaPlayer, this);

            playNextSong(next);
            playPrevSong(prev);
            bottomBarClicked(this);
        } else {
            Toast.makeText(this, "Permission Not Granted, Please restart the app", Toast.LENGTH_LONG).show();
        }
    }

    void bottomBarClicked(final Activity activity) {
//        if(intent == null)
//            intent = new Intent(this, songPlayScreen.getClass());
        bottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//        songPlayScreen = new SongPlayScreen(songList.get(position).get("Title:"), songList.get(position).get("URI:"), position, songList);
//        intent = new Intent(this, songPlayScreen.getClass());
//        startActivity(intent);
            }
        });
    }

    @Override
    public void onClickSong(int position) {
//        MPControl = new MediaPlayerControl(position+1, mediaPlayer, this);
//        MediaPlayerControl.songControls(MPControl, play, bottomTV, this);
        songChange(position);
    }

    void songChange(int position){
        MPControl = new MediaPlayerControl(position+1, mediaPlayer, this);
        MediaPlayerControl.songControls(MPControl, play, bottomTV, this);
    }

    void playNextSong(Button Next) {
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songCurrentPosition == MediaPlayerControl.songsSize - 1)
                    songCurrentPosition = -1;
                songChange(songCurrentPosition + 1);
            }
        });
    }

    void playPrevSong(Button Prev) {
        Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songCurrentPosition == 0)
                    songCurrentPosition = MediaPlayerControl.songsSize;
                songChange(songCurrentPosition - 1);
            }
        });
    }
}
