package ca.uqac.sylvain.p2pplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    //media player
    private MediaPlayer player;
    //song list
    private List<CustomFile> songs;
    //current position
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    private LocalBroadcastManager broadcaster;

    static final public String MUSIC_SERVICE_RESULT = "ca.uqac.sylvain.p2pplayer.MusicService.REQUEST_PROCESSED";
    static final public String MUSIC_SERVICE_MSG = "ca.uqac.sylvain.p2pplayer.MusicService.MSG";

    public void onCreate(){
        super.onCreate();
        songPosn = 0;
        songs = new ArrayList<>();
        player = new MediaPlayer();
        broadcaster = LocalBroadcastManager.getInstance(this);

        initMusicPlayer();
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();

        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(songPosn + 1 < songs.size()) {
            playNextSong();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
        sendResult("new song");
    }

    public void setList(List<CustomFile> songs){
        this.songs = songs;
    }

    public void addSong(CustomFile file) {
        songs.add(file);
    }

    public void removeSong(File file) {
        songs.remove(file);
    }

    public File getSong() {
        if(songPosn > -1 && songPosn < songs.size()) {
            return songs.get(songPosn);
        }
        else {
            return  null;
        }
    }

    public int getSongDuration() {
        return player.getDuration() / 1000;
    }

    public int getSongProgress() {
        return player.getCurrentPosition() / 1000;
    }

    public void setSongProgress(int progress) {
        player.seekTo(progress * 1000);
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong() {
        playSong(songPosn);
    }

    public void playSong(CustomFile song) {
        player.reset();

        songs.add(0, song);
        songPosn = 0;
        Uri trackUri = Uri.fromFile(song);

        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
    }

    public void playSong(int songPosn) {
        player.reset();

        this.songPosn = songPosn;
        CustomFile song = songs.get(songPosn);
        Uri trackUri = Uri.fromFile(song);

        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
    }

    public void pause() {
        if(player.isPlaying()) {
            player.pause();
        }
    }

    public void resume() {
        if(!player.isPlaying()) {
            player.start();
        }
    }

    public void playPreviousSong() {
        if(songs != null && songs.size() > 0) {
            songPosn--;
            if (songPosn < 0) {
                songPosn = songs.size() - 1;
            }
            playSong(songPosn);
        }
    }

    public void playNextSong() {
        if(songs != null && songs.size() > 0) {
            songPosn++;
            if (songPosn > songs.size() - 1) {
                songPosn = 0;
            }
            playSong(songPosn);
        }
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    public void sendResult(String message) {
        Intent intent = new Intent(MUSIC_SERVICE_RESULT);
        if(message != null)
            intent.putExtra(MUSIC_SERVICE_MSG, message);
        broadcaster.sendBroadcast(intent);
    }
}
