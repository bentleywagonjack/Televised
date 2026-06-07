package com.bentleywagonjack.televised;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.*;
import android.provider.MediaStore;
import android.widget.*;
import java.io.IOException;
import java.util.*;

public class MainActivity extends Activity {

    MediaPlayer mediaPlayer;
    ListView listView;
    Button btnPlayPause, btnPrev, btnNext;
    TextView tvNowPlaying, tvElapsed, tvDuration;
    SeekBar seekBar;

    List<String> songNames = new ArrayList<>();
    List<String> songPaths = new ArrayList<>();
    int currentIndex = -1;
    boolean isPlaying = false;

    Handler handler = new Handler(Looper.getMainLooper());
    Runnable seekUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView     = findViewById(R.id.listView);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnPrev      = findViewById(R.id.btnPrev);
        btnNext      = findViewById(R.id.btnNext);
        tvNowPlaying = findViewById(R.id.tvNowPlaying);
        tvElapsed    = findViewById(R.id.tvElapsed);
        tvDuration   = findViewById(R.id.tvDuration);
        seekBar      = findViewById(R.id.seekBar);

        requestPermissions();

        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnNext.setOnClickListener(v -> playNext());
        btnPrev.setOnClickListener(v -> playPrev());

        listView.setOnItemClickListener((p, v, pos, id) -> {
            currentIndex = pos;
            playSong(currentIndex);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) mediaPlayer.seekTo(progress);
            }
            public void onStartTrackingTouch(SeekBar sb) {}
            public void onStopTrackingTouch(SeekBar sb) {}
        });
    }

    void requestPermissions() {
        String perm = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? Manifest.permission.READ_MEDIA_AUDIO
            : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{perm}, 1);
        else
            loadSongs();
    }

    @Override
    public void onRequestPermissionsResult(int req, String[] perms, int[] results) {
        if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED)
            loadSongs();
        else
            Toast.makeText(this, "Storage permission denied", Toast.LENGTH_LONG).show();
    }

    void loadSongs() {
        Cursor cursor = getContentResolver().query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA},
            MediaStore.Audio.Media.IS_MUSIC + "=1", null,
            MediaStore.Audio.Media.DISPLAY_NAME + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                songNames.add(cursor.getString(0));
                songPaths.add(cursor.getString(1));
            }
            cursor.close();
        }

        listView.setAdapter(new ArrayAdapter<>(this,
            android.R.layout.simple_list_item_1, songNames));

        if (songNames.isEmpty())
            Toast.makeText(this, "No music found on device", Toast.LENGTH_LONG).show();
    }

    void playSong(int index) {
        if (index < 0 || index >= songPaths.size()) return;
        if (mediaPlayer != null) { mediaPlayer.release(); mediaPlayer = null; }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(songPaths.get(index));
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            tvNowPlaying.setText("♪  " + songNames.get(index));
            btnPlayPause.setText("⏸");
            seekBar.setMax(mediaPlayer.getDuration());
            startSeekBarUpdate();
            mediaPlayer.setOnCompletionListener(mp -> playNext());
        } catch (IOException e) {
            Toast.makeText(this, "Cannot play this file", Toast.LENGTH_SHORT).show();
        }
    }

    void togglePlayPause() {
        if (mediaPlayer == null) {
            if (!songPaths.isEmpty()) { currentIndex = 0; playSong(0); }
            return;
        }
        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlayPause.setText("▶");
        } else {
            mediaPlayer.start();
            isPlaying = true;
            btnPlayPause.setText("⏸");
            startSeekBarUpdate();
        }
    }

    void playNext() {
        if (songPaths.isEmpty()) return;
        currentIndex = (currentIndex + 1) % songPaths.size();
        playSong(currentIndex);
    }

    void playPrev() {
        if (songPaths.isEmpty()) return;
        currentIndex = (currentIndex - 1 + songPaths.size()) % songPaths.size();
        playSong(currentIndex);
    }

    void startSeekBarUpdate() {
        if (seekUpdater != null) handler.removeCallbacks(seekUpdater);
        seekUpdater = new Runnable() {
            public void run() {
                if (mediaPlayer != null && isPlaying) {
                    int pos = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(pos);
                    tvElapsed.setText(formatTime(pos));
                    tvDuration.setText(formatTime(mediaPlayer.getDuration()));
                    handler.postDelayed(this, 500);
                }
            }
        };
        handler.post(seekUpdater);
    }

    String formatTime(int ms) {
        int s = ms / 1000;
        return String.format("%d:%02d", s / 60, s % 60);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (seekUpdater != null) handler.removeCallbacks(seekUpdater);
        if (mediaPlayer != null) { mediaPlayer.release(); mediaPlayer = null; }
    }
}
