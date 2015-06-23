package com.example.yuplayer;

import android.app.NotificationManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.yuplayer.utilities.Utilities;


public class PlayerActivity extends ActionBarActivity {
    public PlayListActivity mPlayList;
    public static boolean appstatus = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MediaPlayerFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MediaPlayerFragment extends Fragment {

        public static TextView songName, songCurrentTime, songTotalTime, songArtist;
        public static ImageView songImage;
        public static ImageButton songNext, songPrevious, songStatus;
        public static SeekBar songSeek;
        public MediaPlayerFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_player, container, false);

            songName = (TextView) rootView.findViewById(R.id.fragment_player_txt_songName);
            songArtist = (TextView) rootView.findViewById(R.id.fragment_player_txt_songArtist);
            songName.setText(PlayListActivity.PlayerFragment.songName.getText());
            songArtist.setText(PlayListActivity.PlayerFragment.songTimer.getText());

            songImage = (ImageView) rootView.findViewById(R.id.fragment_player_imgV_songImage);
            if(PlayListActivity.audioSongImage != null){
                songImage.setImageBitmap(PlayListActivity.audioSongImage);
            }
            else{
                songImage.setImageResource(R.drawable.adele);
            }

            songImage.setScaleType(ImageView.ScaleType.FIT_XY);
            songImage.setAdjustViewBounds(true);

            songNext = (ImageButton) rootView.findViewById(R.id.fragment_player_imBtn_btnNext);
            songNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayListActivity.playNext(getActivity());
                }
            });

            songPrevious = (ImageButton) rootView.findViewById(R.id.fragment_player_imBtn_btnPrevious);
            songPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayListActivity.playPrevious(getActivity());
                }
            });

            songStatus = (ImageButton) rootView.findViewById(R.id.fragment_player_imBtn_btnPlay);
            songStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayListActivity.mp3Service.pauseSong(getActivity());
                }
            });

            songSeek = (SeekBar) rootView.findViewById(R.id.fragment_player_sb_songSeek);

            songTotalTime = (TextView) rootView.findViewById(R.id.fragment_player_txt_totalTime);
            songCurrentTime = (TextView) rootView.findViewById(R.id.fragment_player_txt_currentTime);

            if(PlayListActivity.mp3Service.mPlayer !=null && PlayListActivity.mp3Service.mPlayer.isPlaying()){
                PlayerActivity.MediaPlayerFragment.songStatus.setImageResource(android.R.drawable.ic_media_pause);
            }
            else{
                PlayerActivity.MediaPlayerFragment.songStatus.setImageResource(android.R.drawable.ic_media_play);
            }

            PlayerActivity.MediaPlayerFragment.songSeek.setProgress(0);
            PlayerActivity.MediaPlayerFragment.songSeek.setMax(100);
            // Updating progress bar
            PlayListActivity.mp3Service.updateProgressBar();

            songSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //Toast.makeText(getActivity(), String.valueOf(progress), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //Toast.makeText(getActivity(), String.valueOf("here"), Toast.LENGTH_LONG).show();
                    PlayListActivity.mp3Service.mHandler.removeCallbacks(PlayListActivity.mp3Service.mUpdateTimeTask);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    PlayListActivity.mp3Service.mHandler.removeCallbacks(PlayListActivity.mp3Service.mUpdateTimeTask);
                    int totalDuration = PlayListActivity.mp3Service.mPlayer.getDuration();
                    int currentPosition = Utilities.progressToTimer(seekBar.getProgress(), totalDuration);

                    // forward or backward to certain seconds
                    PlayListActivity.mp3Service.mPlayer.seekTo(currentPosition);

                    // update timer progress again
                    PlayListActivity.mp3Service.updateProgressBar();
                }
            });


            return rootView;
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }


    @Override
    public void onDestroy(){

        super.onDestroy();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }
    @Override
    public void onPause() {
        appstatus = false;
        super.onPause();
        if(mPlayList.mp3Service !=null){
            mPlayList.mp3Service.createNoti();
        }

    }

    @Override
    public void onResume() {
        appstatus = true;
        super.onResume();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }
}
