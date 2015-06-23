package com.example.yuplayer.receiver;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.yuplayer.PlayListActivity;
import com.example.yuplayer.PlayerActivity;
import com.example.yuplayer.R;
import com.example.yuplayer.service.PlayListService;

/**
 * Created by indianic on 07/04/15.
 */
public class PlayerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle getIntentData = new Bundle();
        getIntentData = intent.getExtras();
        Intent moveIntent, forwardIntent, playIntent, previousIntent;
        Bundle moveIntentBundle, forwardIntentBundle, playIntentBundle, previousIntentBundle;
        PendingIntent movePendingIntent, forwardPendingIntent, playPendingIntent, previousPendingIntent;



        int notificationId = Integer.parseInt(getIntentData.getString("notificationId"));
        //Log.e("notificationId", String.valueOf(notificationId));
        switch(notificationId){
            case 0:
                PlayListActivity.playNext(context);
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.player_notification);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        context).setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(PlayListActivity.audioSongImage).setContent(
                        remoteViews);
                // Creates an explicit intent for an Activity in your app
                moveIntent = new Intent(context, PlayerReceiver.class);
                moveIntentBundle = new Bundle();
                moveIntentBundle.putString("notificationId", "1");
                moveIntent.putExtras(moveIntentBundle);
                movePendingIntent = PendingIntent.getBroadcast(context, 6, moveIntent, 0);

                forwardIntent = new Intent(context, PlayerReceiver.class);
                forwardIntentBundle = new Bundle();
                forwardIntentBundle.putString("notificationId", "0");
                forwardIntent.putExtras(forwardIntentBundle);
                forwardPendingIntent = PendingIntent.getBroadcast(context, 7, forwardIntent, 0);


                playIntent = new Intent(context, PlayerReceiver.class);
                playIntentBundle = new Bundle();
                playIntentBundle.putString("notificationId", "2");
                playIntent.putExtras(playIntentBundle);
                playPendingIntent = PendingIntent.getBroadcast(context, 8, playIntent, 0);

                previousIntent = new Intent(context, PlayerReceiver.class);
                previousIntentBundle = new Bundle();
                previousIntentBundle.putString("notificationId", "3");
                previousIntent.putExtras(previousIntentBundle);
                previousPendingIntent = PendingIntent.getBroadcast(context, 9, previousIntent, 0);

                remoteViews.setOnClickPendingIntent(R.id.player_notification_imgV_previous, previousPendingIntent);
                remoteViews.setOnClickPendingIntent(R.id.player_notification_imgV_forward, forwardPendingIntent);
                remoteViews.setOnClickPendingIntent(R.id.player_notification_imgV_status, playPendingIntent);
                remoteViews.setOnClickPendingIntent(R.id.player_notification_ll_text, movePendingIntent);
                remoteViews.setTextViewText(R.id.player_notification_txt_song, PlayListActivity.PlayerFragment.songName.getText());
                remoteViews.setTextViewText(R.id.player_notification_txt_artist, PlayListActivity.PlayerFragment.songTimer.getText());
                remoteViews.setImageViewBitmap(R.id.player_notification_imgV_songImage, PlayListActivity.audioSongImage);
                remoteViews.setImageViewResource(R.id.player_notification_imgV_status, android.R.drawable.ic_media_pause);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


                Notification notif = mBuilder.build();
                notif.flags |= Notification.FLAG_NO_CLEAR;
                // mId allows you to update the notification later on.
                mNotificationManager.notify(0, notif);
                break;
            case 1:
                intent = new Intent(context, PlayerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);

                Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                context.sendBroadcast(it);
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(0);
                break;
            case 2:
                if(PlayListActivity.audioFileUri != null) {
                    PlayListActivity.mp3Service.pauseSong(context);
                    if(PlayListActivity.mp3Service.mPlayer.isPlaying()){
                        remoteViews = new RemoteViews(context.getPackageName(),
                                R.layout.player_notification);
                        mBuilder = new NotificationCompat.Builder(
                                context).setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(PlayListActivity.audioSongImage).setContent(
                                remoteViews);
                        // Creates an explicit intent for an Activity in your app
                        moveIntent = new Intent(context, PlayerReceiver.class);
                        moveIntentBundle = new Bundle();
                        moveIntentBundle.putString("notificationId", "1");
                        moveIntent.putExtras(moveIntentBundle);
                        movePendingIntent = PendingIntent.getBroadcast(context, 10, moveIntent, 0);

                        forwardIntent = new Intent(context, PlayerReceiver.class);
                        forwardIntentBundle = new Bundle();
                        forwardIntentBundle.putString("notificationId", "0");
                        forwardIntent.putExtras(forwardIntentBundle);
                        forwardPendingIntent = PendingIntent.getBroadcast(context, 11, forwardIntent, 0);


                        playIntent = new Intent(context, PlayerReceiver.class);
                        playIntentBundle = new Bundle();
                        playIntentBundle.putString("notificationId", "2");
                        playIntent.putExtras(playIntentBundle);
                        playPendingIntent = PendingIntent.getBroadcast(context, 12, playIntent, 0);

                        previousIntent = new Intent(context, PlayerReceiver.class);
                        previousIntentBundle = new Bundle();
                        previousIntentBundle.putString("notificationId", "3");
                        previousIntent.putExtras(previousIntentBundle);
                        previousPendingIntent = PendingIntent.getBroadcast(context, 13, previousIntent, 0);

                        remoteViews.setOnClickPendingIntent(R.id.player_notification_imgV_previous, previousPendingIntent);
                        remoteViews.setOnClickPendingIntent(R.id.player_notification_imgV_forward, forwardPendingIntent);
                        remoteViews.setOnClickPendingIntent(R.id.player_notification_imgV_status, playPendingIntent);
                        remoteViews.setOnClickPendingIntent(R.id.player_notification_ll_text, movePendingIntent);
                        remoteViews.setTextViewText(R.id.player_notification_txt_song, PlayListActivity.PlayerFragment.songName.getText());
                        remoteViews.setTextViewText(R.id.player_notification_txt_artist, PlayListActivity.PlayerFragment.songTimer.getText());
                        remoteViews.setImageViewBitmap(R.id.player_notification_imgV_songImage, PlayListActivity.audioSongImage);
                        remoteViews.setImageViewResource(R.id.player_notification_imgV_status, android.R.drawable.ic_media_pause);
                        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notif = mBuilder.build();
                        notif.flags |= Notification.FLAG_NO_CLEAR;
                        // mId allows you to update the notification later on.
                        mNotificationManager.notify(0, notif);

                    }
                    else{
                        remoteViews = new RemoteViews(context.getPackageName(),
                                R.layout.player_notification);
                        mBuilder = new NotificationCompat.Builder(
                                context).setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(PlayListActivity.audioSongImage).setContent(
                                remoteViews);
                        // Creates an explicit intent for an Activity in your app
                        moveIntent = new Intent(context, PlayerReceiver.class);
                        moveIntentBundle = new Bundle();
                        moveIntentBundle.putString("notificationId", "1");
                        moveIntent.putExtras(moveIntentBundle);
                        movePendingIntent = PendingIntent.getBroadcast(context, 14, moveIntent, 0);

                        forwardIntent = new Intent(context, PlayerReceiver.class);
                        forwardIntentBundle = new Bundle();
                        forwardIntentBundle.putString("notificationId", "0");
                        forwardIntent.putExtras(forwardIntentBundle);
                        forwardPendingIntent = PendingIntent.getBroadcast(context, 15, forwardIntent, 0);


                        playIntent = new Intent(context, PlayerReceiver.class);
                        playIntentBundle = new Bundle();
                        playIntentBundle.putString("notificationId", "2");
                        playIntent.putExtras(playIntentBundle);
                        playPendingIntent = PendingIntent.getBroadcast(context, 16, playIntent, 0);

                        previousIntent = new Intent(context, PlayerReceiver.class);
                        previousIntentBundle = new Bundle();
                        previousIntentBundle.putString("notificationId", "3");
                        previousIntent.putExtras(previousIntentBundle);
                        previousPendingIntent = PendingIntent.getBroadcast(context, 17, previousIntent, 0);

                        remoteViews.setOnClickPendingIntent(R.id.player_notification_imgV_previous, previousPendingIntent);
                        remoteViews.setOnClickPendingIntent(R.id.player_notification_imgV_forward, forwardPendingIntent);
                        remoteViews.setOnClickPendingIntent(R.id.player_notification_imgV_status, playPendingIntent);
                        remoteViews.setOnClickPendingIntent(R.id.player_notification_ll_text, movePendingIntent);
                        remoteViews.setTextViewText(R.id.player_notification_txt_song, PlayListActivity.PlayerFragment.songName.getText());
                        remoteViews.setTextViewText(R.id.player_notification_txt_artist, PlayListActivity.PlayerFragment.songTimer.getText());
                        remoteViews.setImageViewBitmap(R.id.player_notification_imgV_songImage, PlayListActivity.audioSongImage);
                        remoteViews.setImageViewResource(R.id.player_notification_imgV_status, android.R.drawable.ic_media_play);
                        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notif = mBuilder.build();
                        notif.flags |= Notification.FLAG_NO_CLEAR;
                        // mId allows you to update the notification later on.
                        mNotificationManager.notify(0, notif);

                    }
                }
                break;

            case 3:
                PlayListActivity.playPrevious(context);
                remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.player_notification);
                mBuilder = new NotificationCompat.Builder(
                        context).setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(PlayListActivity.audioSongImage).setContent(
                        remoteViews);
                // Creates an explicit intent for an Activity in your app
                moveIntent = new Intent(context, PlayerReceiver.class);
                moveIntentBundle = new Bundle();
                moveIntentBundle.putString("notificationId", "1");
                moveIntent.putExtras(moveIntentBundle);
                movePendingIntent = PendingIntent.getBroadcast(context, 18, moveIntent, 0);

                forwardIntent = new Intent(context, PlayerReceiver.class);
                forwardIntentBundle = new Bundle();
                forwardIntentBundle.putString("notificationId", "0");
                forwardIntent.putExtras(forwardIntentBundle);
                forwardPendingIntent = PendingIntent.getBroadcast(context, 19, forwardIntent, 0);


                playIntent = new Intent(context, PlayerReceiver.class);
                playIntentBundle = new Bundle();
                playIntentBundle.putString("notificationId", "2");
                playIntent.putExtras(playIntentBundle);
                playPendingIntent = PendingIntent.getBroadcast(context, 20, playIntent, 0);

                previousIntent = new Intent(context, PlayerReceiver.class);
                previousIntentBundle = new Bundle();
                previousIntentBundle.putString("notificationId", "3");
                previousIntent.putExtras(previousIntentBundle);
                previousPendingIntent = PendingIntent.getBroadcast(context, 21, previousIntent, 0);

                remoteViews.setOnClickPendingIntent(R.id.player_notification_imgV_previous, previousPendingIntent);
                remoteViews.setOnClickPendingIntent(R.id.player_notification_imgV_forward, forwardPendingIntent);
                remoteViews.setOnClickPendingIntent(R.id.player_notification_imgV_status, playPendingIntent);
                remoteViews.setOnClickPendingIntent(R.id.player_notification_ll_text, movePendingIntent);
                remoteViews.setTextViewText(R.id.player_notification_txt_song, PlayListActivity.PlayerFragment.songName.getText());
                remoteViews.setTextViewText(R.id.player_notification_txt_artist, PlayListActivity.PlayerFragment.songTimer.getText());
                remoteViews.setImageViewBitmap(R.id.player_notification_imgV_songImage, PlayListActivity.audioSongImage);
                remoteViews.setImageViewResource(R.id.player_notification_imgV_status, android.R.drawable.ic_media_pause);
                mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notif = mBuilder.build();
                notif.flags |= Notification.FLAG_NO_CLEAR;
                // mId allows you to update the notification later on.
                mNotificationManager.notify(0, notif);
                break;
            default:
                break;
        }


    }
}

