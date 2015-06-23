package com.example.yuplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.yuplayer.PlayListActivity;
import com.example.yuplayer.PlayerActivity;
import com.example.yuplayer.R;
import com.example.yuplayer.receiver.PlayerReceiver;
import com.example.yuplayer.utilities.Utilities;

import java.net.URI;

/**
 * Created by indianic on 06/04/15.
 */
public class PlayListService extends Service implements AudioManager.OnAudioFocusChangeListener{
    public final IBinder localBinder = new LocalBinder();
    public MediaPlayer mPlayer;
    public static RemoteViews remoteViews;
    private boolean created = false;
    public Handler mHandler = new Handler();;
    public Utilities utils;
    public int seekForwardTime = 5000; // 5000 milliseconds
    public int seekBackwardTime = 5000; // 5000 milliseconds

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    @Override
    public void onCreate() {
        AudioManager mAudioManager = (AudioManager) PlayListService.this.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

    }
   @Override
    public void onAudioFocusChange(int focusChange)
    {
        switch (focusChange)
        {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if(PlayListActivity.audioFileUri != null && mPlayer.isPlaying()) {
                    pauseSong(this);
                }
            break;
        }
    }


    public class LocalBinder extends Binder {
        public PlayListService getService() {
            return PlayListService.this;
        }
    }

    public void playSong(Context c) {
        if(!created){
            this.mPlayer = MediaPlayer.create(c, PlayListActivity.audioFileUri);
            created = true;
            this.mPlayer.start();
            if(PlayerActivity.MediaPlayerFragment.songStatus !=null) {
                if (PlayListActivity.audioSongImage != null) {
                    PlayerActivity.MediaPlayerFragment.songImage.setImageBitmap(PlayListActivity.audioSongImage);
                } else {
                    PlayerActivity.MediaPlayerFragment.songImage.setImageResource(R.drawable.adele);
                }

                if(PlayerActivity.MediaPlayerFragment.songSeek !=null){
                    // set Progress bar values
                    PlayerActivity.MediaPlayerFragment.songSeek.setProgress(0);
                    PlayerActivity.MediaPlayerFragment.songSeek.setMax(100);
                    // Updating progress bar
                    updateProgressBar();

                }
                PlayerActivity.MediaPlayerFragment.songStatus.setImageResource(android.R.drawable.ic_media_pause);
            }

            PlayListActivity.PlayerFragment.mPlayerstatus.setImageResource(R.drawable.pause);
        }else{
            try{
                if(this.mPlayer.isPlaying() || !this.mPlayer.isPlaying()){
                    this.mPlayer.stop();
                    this.mPlayer.release();
                    created = false;
                    playSong(c);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        this.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                PlayListActivity.playNext(getBaseContext());

            }

        });



    }

    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    public Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mPlayer.getDuration();
            long currentDuration = mPlayer.getCurrentPosition();

            //Log.e("utilTime", String.valueOf(Utilities.getProgressPercentage(currentDuration, totalDuration)));

            if(PlayerActivity.MediaPlayerFragment.songCurrentTime !=null && PlayerActivity.MediaPlayerFragment.songTotalTime !=null){
                // Displaying Total Duration time
                PlayerActivity.MediaPlayerFragment.songTotalTime.setText(""+Utilities.milliSecondsToTimer(totalDuration));
                // Displaying time completed playing
                PlayerActivity.MediaPlayerFragment.songCurrentTime.setText(""+Utilities.milliSecondsToTimer(currentDuration));
            }


            // Updating progress bar
           int progress = (int)(Utilities.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            PlayerActivity.MediaPlayerFragment.songSeek.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };




    public void createNoti(){
        try{
            if(this.mPlayer != null && this.mPlayer.isPlaying()){


                RemoteViews remoteViews = new RemoteViews(getPackageName(),
                        R.layout.player_notification);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        this).setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(PlayListActivity.audioSongImage).setContent(
                        remoteViews);
                // Creates an explicit intent for an Activity in your app
                Intent moveIntent = new Intent(this, PlayerReceiver.class);
                Bundle moveIntentBundle = new Bundle();
                moveIntentBundle.putString("notificationId", "1");
                moveIntent.putExtras(moveIntentBundle);
                PendingIntent movePendingIntent = PendingIntent.getBroadcast(this, 1, moveIntent, 0);

                Intent forwardIntent = new Intent(this, PlayerReceiver.class);
                Bundle forwardIntentBundle = new Bundle();
                forwardIntentBundle.putString("notificationId", "0");
                forwardIntent.putExtras(forwardIntentBundle);
                PendingIntent forwardPendingIntent = PendingIntent.getBroadcast(this, 3, forwardIntent, 0);


                Intent playIntent = new Intent(this, PlayerReceiver.class);
                Bundle playIntentBundle = new Bundle();
                playIntentBundle.putString("notificationId", "2");
                playIntent.putExtras(playIntentBundle);
                PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 4, playIntent, 0);


                Intent previousIntent = new Intent(this, PlayerReceiver.class);
                Bundle previousIntentBundle = new Bundle();
                previousIntentBundle.putString("notificationId", "3");
                previousIntent.putExtras(previousIntentBundle);
                PendingIntent previousPendingIntent = PendingIntent.getBroadcast(this, 5, previousIntent, 0);

                remoteViews.setOnClickPendingIntent(R.id.player_notification_imgV_previous, previousPendingIntent);
                remoteViews.setOnClickPendingIntent(R.id.player_notification_imgV_forward, forwardPendingIntent);
                remoteViews.setOnClickPendingIntent(R.id.player_notification_imgV_status, playPendingIntent);
                remoteViews.setOnClickPendingIntent(R.id.player_notification_ll_text, movePendingIntent);
                remoteViews.setTextViewText(R.id.player_notification_txt_song, PlayListActivity.PlayerFragment.songName.getText());
                remoteViews.setTextViewText(R.id.player_notification_txt_artist, PlayListActivity.PlayerFragment.songTimer.getText());
                remoteViews.setImageViewBitmap(R.id.player_notification_imgV_songImage, PlayListActivity.audioSongImage);
                remoteViews.setImageViewResource(R.id.player_notification_imgV_status, android.R.drawable.ic_media_pause);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


                Notification notif = mBuilder.build();
                notif.flags |= Notification.FLAG_NO_CLEAR;
                // mId allows you to update the notification later on.
                mNotificationManager.notify(0, notif);

            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    public void pauseSong(Context c) {
        if(this.mPlayer.isPlaying()){
            this.mPlayer.pause();
            PlayListActivity.PlayerFragment.mPlayerstatus.setImageResource(R.drawable.play);
            if(PlayerActivity.MediaPlayerFragment.songStatus !=null){
                PlayerActivity.MediaPlayerFragment.songStatus.setImageResource(android.R.drawable.ic_media_play);
            }
        }
        else{
            this.mPlayer.start();
            PlayListActivity.PlayerFragment.mPlayerstatus.setImageResource(R.drawable.pause);
            if(PlayerActivity.MediaPlayerFragment.songStatus !=null){
                PlayerActivity.MediaPlayerFragment.songStatus.setImageResource(android.R.drawable.ic_media_pause);
            }

        }
    }


    @Override
    public void onDestroy(){
        this.mPlayer.stop();
        this.mPlayer.reset();
        this.mPlayer.release();
    }

}