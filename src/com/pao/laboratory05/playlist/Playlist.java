package com.pao.laboratory05.playlist;
import java.util.Arrays;

public class Playlist {
    private String name;
    private Song[] songs;

    public Playlist(String name){
        this.name = name;
        this.songs = new Song[0];
    }
    public String getName() {
        return name;
    }
    public void addSong(Song song) {

        Song[] newSongs = new Song[this.songs.length + 1];
        System.arraycopy(this.songs, 0, newSongs, 0, this.songs.length);
        newSongs[newSongs.length - 1] = song;
        this.songs = newSongs;
    }
    public void printSortedByTitle() {
        Song[] copy = this.songs.clone();
        Arrays.sort(copy);
        for (Song song : copy) {
            System.out.println(song);
        }
    }

    public void printSortedByDuration() {
        Song[] copy = this.songs.clone();
        Arrays.sort(copy, new SongDurationComparator());
        for (Song song : copy) {
            System.out.println(song);
        }
    }
    public int getTotalDuration() {
        int total = 0;
        for (Song song : this.songs) {
            total += song.durationSeconds();
        }
        return total;
    }

}


