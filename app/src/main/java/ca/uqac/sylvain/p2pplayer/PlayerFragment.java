package ca.uqac.sylvain.p2pplayer;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

public class PlayerFragment extends Fragment {
    private MusicService musicSrv;
    private ImageButton playButton;
    private ImageButton previousButton;
    private ImageButton nextButton;
    private SeekBar seekBar;
    private ImageView cover;
    private TextView artistText;
    private TextView albumText;
    private TextView songText;
    private boolean running = true;

    public PlayerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        view.setBackgroundColor(Color.WHITE);

        playButton = (ImageButton)view.findViewById(R.id.playButton);
        previousButton = (ImageButton)view.findViewById(R.id.previousButton);
        nextButton = (ImageButton)view.findViewById(R.id.nextButton);
        seekBar = (SeekBar)view.findViewById(R.id.seekBar);
        cover = (ImageView)view.findViewById(R.id.cover);
        artistText = (TextView)view.findViewById(R.id.artist);
        albumText = (TextView)view.findViewById(R.id.album);
        songText = (TextView)view.findViewById(R.id.song);
        MainActivity activity = (MainActivity)getActivity();
        musicSrv = activity.getMusicSrv();

        initPlayerView();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicSrv.isPlaying()) {
                    musicSrv.pause();
                    playButton.setImageResource(R.mipmap.ic_media_play);
                    running = false;
                }
                else {
                    musicSrv.resume();
                    playButton.setImageResource(R.mipmap.ic_media_pause);
                    running = true;
                }
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.playPreviousSong();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.playNextSong();
            }
        });

        return view;
    }

    public void initPlayerView() {
        seekBar.setOnClickListener(null);

        if(musicSrv != null && musicSrv.getSong() != null) {
            playButton.setImageResource(R.mipmap.ic_media_pause);
            seekBar.setMax(musicSrv.getSongDuration());
            seekBar.setProgress(musicSrv.getSongProgress());
            seekBar.setEnabled(true);
            seekBar.postDelayed(onEverySecond, 1000);
            running = true;
        }
        else {
            playButton.setImageResource(R.mipmap.ic_media_play);
            running = false;
            seekBar.setEnabled(false);
            seekBar.setProgress(0);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser && musicSrv != null) {
                    musicSrv.setSongProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        updateMetadatas();
    }

    public void updateMetadatas() {
        if(musicSrv != null) {
            try {
                File song = musicSrv.getSong();
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(song.getPath());
                byte[] coverBytes = retriever.getEmbeddedPicture();
                if(coverBytes != null) {
                    Bitmap bMap = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.length);
                    cover.setImageBitmap(bMap);
                }
                artistText.setText(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                albumText.setText(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                songText.setText(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));

            }
            catch(Exception e) {}
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        running = false;
    }

    private Runnable onEverySecond = new Runnable() {
        @Override
        public void run(){
            if(true == running){
                if(musicSrv.isPlaying()) {
                    seekBar.postDelayed(onEverySecond, 1000);
                    updateTime();
                }
            }
        }
    };

    private void updateTime() {
        if(musicSrv.getSongProgress() > musicSrv.getSongDuration()) {
            seekBar.setProgress(musicSrv.getSongDuration());
            running = false;
        }
        else {
            seekBar.setProgress(musicSrv.getSongProgress());
            if(musicSrv.getSongDuration() == musicSrv.getSongProgress()) {
                running = false;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initPlayerView();
    }
}
