package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

    TextView bottomTV;
    Button play, next, prev;
    ConstraintLayout bottomBar;

    // Changed it to non-static, if problem
    public MediaPlayer mediaPlayer;
    MediaPlayerControl MPControl;
    public static int songCurrentPosition;

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if(MPControl != null)
            MediaPlayerControl.songControls(MPControl, play, bottomTV, this);
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
        bottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        songPlayScreen = new SongPlayScreen();
        intent = new Intent(activity, songPlayScreen.getClass());
        startActivity(intent);
            }
        });
    }

    @Override
    public void onClickSong(int position) {
        changeSongState(position);
    }

    private void changeSongState(int position){
        MPControl = new MediaPlayerControl(position+1, mediaPlayer, this, null);
        MediaPlayerControl.songControls(MPControl, play, bottomTV, this);
    }

    void playNextSong(Button Next) {
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new CountDownTimer(500,1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        if (songCurrentPosition == MediaPlayerControl.songsSize - 1)
//                            songCurrentPosition = -1;
//                        changeSongState(songCurrentPosition + 1);
//                    }
//                };
                if (songCurrentPosition == MediaPlayerControl.songsSize - 1)
                    songCurrentPosition = -1;
                changeSongState(songCurrentPosition + 1);
            }
        });
    }

    void playPrevSong(Button Prev) {
        Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songCurrentPosition == 0)
                    songCurrentPosition = MediaPlayerControl.songsSize;
                changeSongState(songCurrentPosition - 1);
            }
        });
    }
}
