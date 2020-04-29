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

    TextView bottomTV, mainTV;
    Button play, next, prev;
    Button normalPlay, normalNext, normalPrev;
    ConstraintLayout bottomBar;

    public static MediaPlayer mediaPlayer;
    MediaPlayerControl MPControl;
    public static int songCurrentPosition;
    String songName, songUri;

    // Storing the names
    static ArrayList<HashMap<String, String>> songList = new ArrayList<>();

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

        if(permissions.readExternalStoragePermission()){
            contentResolver = this.getContentResolver();
            externalStoreUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            cursor = contentResolver.query(externalStoreUri, null, null, null, null);

            songList = readAudioStorage(cursor);

            songListViewAdapter = new SongRecyclerAdapter(songList, "Title:", this);
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
//                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//                    setContentView(R.layout.song_playing_display_screen);
//                    normalPlay = findViewById(R.id.play_button);
//                    mainTV = findViewById(R.id.songNameDisplay);
//                    onClickSong(songCurrentPosition);
//                    if(MPControl != null)
//                        MediaPlayerControl.songControls(songCurrentPosition, MPControl, play, bottomTV, activity);

//        songPlayScreen = new SongPlayScreen(songList.get(position).get("Title:"), songList.get(position).get("URI:"), position, songList);
//        intent = new Intent(this, songPlayScreen.getClass());
//        startActivity(intent);
                }
        });
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
        songName = songList.get(position).get("Title:");
        songUri = songList.get(position).get("URI:");
//        songInfoStart(MPControl, position, bottomTV, this, play);
        MPControl = new MediaPlayerControl(songUri, songName, mediaPlayer, this);
        MediaPlayerControl.songControls(position, MPControl, play, bottomTV, this);
    }

    static void songInfoStart(MediaPlayerControl mpControl, int position, TextView tv, Activity activity, Button Play) {
        mpControl = new MediaPlayerControl(songList.get(position).get("URI:"), songList.get(position).get("Title:"), mediaPlayer, activity);
        MediaPlayerControl.songControls(position, mpControl, Play, tv, activity);
    }
    void playNextSong(Button Next){
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(songCurrentPosition == songList.size()-1)
                    songCurrentPosition = -1;
                onClickSong(songCurrentPosition+1);
            }
        });
    }

    void playPrevSong(Button Prev){
        Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(songCurrentPosition == 0)
                    songCurrentPosition = songList.size();
                onClickSong(songCurrentPosition-1);
            }
        });
    }
}
