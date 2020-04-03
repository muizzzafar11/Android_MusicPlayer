package com.example.musicplayer;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class SongRecyclerAdapter extends RecyclerView.Adapter <SongRecyclerAdapter.ViewHolder> {

    private ArrayList<HashMap<String,String>> _songs;
    private String _displayString;
    private OnSongClickListener _songClickListener;

    SongRecyclerAdapter(ArrayList<HashMap<String, String>> songs, String displayString, OnSongClickListener onSongClickListener) {
        _displayString = displayString;
        _songs = songs;
        _songClickListener = onSongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        TextView songNameText = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.song_display_view, parent, false);
        return new ViewHolder(songNameText, _songClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.songName.setText(_songs.get(position).get(_displayString));
    }

    @Override
    public int getItemCount() {
        // Simple statement just saying that if the size is less than zero then return 0
        try {
            return Math.max(_songs.size(), 0);
        }catch (IndexOutOfBoundsException e){
            e.getMessage();
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView songName;
        OnSongClickListener songClickListener;

        ViewHolder(@NonNull TextView itemView, OnSongClickListener songClickListener) {
            super(itemView);
            songName = itemView;
            itemView.setOnClickListener(this);
            this.songClickListener = songClickListener;
        }

        @Override
        public void onClick(View v) {
            songClickListener.onClickSong(getAdapterPosition());
        }
    }

    public interface OnSongClickListener{
        void onClickSong(int position);
    }
}
