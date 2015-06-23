package com.example.yuplayer;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.yuplayer.service.PlayListService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class PlayListActivity extends ActionBarActivity {

    public static ArrayList<HashMap<String, String>> songsList = new ArrayList<>();

    ListView mPlayList ;

    public static int songIndex;

    public static Uri audioFileUri = null;

    public static Bitmap audioSongImage;

    public static PlayListService mp3Service;

    public static boolean appstatus= false;

    public ServiceConnection mp3PlayerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            mp3Service = ((PlayListService.LocalBinder) binder).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_list);

        startService(new Intent(this, PlayListService.class));
        Intent connectionIntent = new Intent(this, PlayListService.class);
        bindService(connectionIntent, mp3PlayerServiceConnection,
                Context.BIND_AUTO_CREATE);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlayerFragment())
                    .commit();

            mPlayList = (ListView) findViewById(R.id.activity_play_list_lv_playList);


            new SongAsync(this).execute();


            // listening to single listitem click
            mPlayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    if(PlayerFragment.frgParent.getVisibility() == View.INVISIBLE ){
                        PlayerFragment.frgParent.setVisibility(View.VISIBLE);
                    }
                    // getting listitem index
                    songIndex = position;
                    playSong(songIndex, getBaseContext());

                }
            });


        }
    }


    public static void playSong(int position, Context cn){
        audioFileUri = Uri.parse(songsList.get(position).get("songPath"));
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(songsList.get(songIndex).get("songPath"));
        byte[] artBytes =  mmr.getEmbeddedPicture();
        if(artBytes != null)
        {
            InputStream is = new ByteArrayInputStream(mmr.getEmbeddedPicture());
            audioSongImage = BitmapFactory.decodeStream(is);
        }
        else
        {
            audioSongImage = null;
        }
        mp3Service.playSong(cn);
        PlayerFragment.songName.setText(songsList.get(songIndex).get("songTitle"));
        PlayerFragment.songTimer.setText(songsList.get(songIndex).get("songArtist"));
        if(PlayerActivity.MediaPlayerFragment.songName != null){
            PlayerActivity.MediaPlayerFragment.songName.setText(songsList.get(songIndex).get("songTitle"));
            PlayerActivity.MediaPlayerFragment.songArtist.setText(songsList.get(songIndex).get("songArtist"));
        }
        // check app status
        if(!appstatus && !PlayerActivity.appstatus){
            mp3Service.createNoti();
        }

    }

    public static void playNext(Context cn){
        if(songIndex == songsList.size()-1){
            songIndex = 0;
        }
        else{
            songIndex += 1;
        }
        playSong(songIndex, cn);
    }

    public static void playPrevious(Context cn){


        if(songIndex == 0){
            songIndex = songsList.size() - 1;
        }
        else{
            songIndex -= 1;
        }
        playSong(songIndex, cn);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
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
    public static class PlayerFragment extends Fragment {

        public static ImageView mPlayerstatus;
        public static TextView songTimer, songName;
        public static RelativeLayout listText, frgParent;

        public PlayerFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_play_list, container, false);
            frgParent = (RelativeLayout) rootView.findViewById(R.id.fragment_play_list_rl_parent);
            frgParent.setVisibility(View.INVISIBLE);
            songTimer = (TextView) rootView.findViewById(R.id.fragment_play_list_txt_artist);
            songName = (TextView) rootView.findViewById(R.id.fragment_play_list_txt_song);
            listText = (RelativeLayout) rootView.findViewById(R.id.fragment_play_list_ll_text);
            listText.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    getActivity().startActivity(intent);
                }
            });

            mPlayerstatus = (ImageView) rootView.findViewById(R.id.fragment_play_list_imgV_status);
            mPlayerstatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(audioFileUri != null) {
                        mp3Service.pauseSong(getActivity());
                    }
                }
            });

            return rootView;
        }
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    @Override
    public void onDestroy(){
        unbindService(this.mp3PlayerServiceConnection);
        super.onDestroy();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }
    @Override
    public void onPause() {
        appstatus = false;
        super.onPause();
        if(mp3Service !=null){
            mp3Service.createNoti();
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

    public class SongAsync extends AsyncTask<Void, Void, String> {
        Context mContext;
        ProgressDialog progress=null;
        // Get Cursor
        public SongAsync(Context context){
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
//            this.progress.show();

        }


            @Override
            protected String doInBackground(Void... params) {
            Looper.prepare();

            // Do work
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION
            };

            Uri queryUri = MediaStore.Files.getContentUri("external");

            CursorLoader cursorLoader = new CursorLoader(
                    mContext,
                    queryUri,
                    projection,
                    selection,
                    null, // Selection args (none).
                    MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
            );
            Cursor cursor = cursorLoader.loadInBackground();

            while(cursor.moveToNext()) {
                HashMap<String, String> songMap = new HashMap<>();
                songMap.put("songTitle",
                        cursor.getString(2));
                songMap.put("songPath", cursor.getString(3));
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                Uri uri = Uri.parse(cursor.getString(3));
                mediaMetadataRetriever.setDataSource(mContext, uri);
                String artist =  mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
                if(artist == null){
                    songMap.put("songArtist", "---");
                }
                else{
                    songMap.put("songArtist", artist);
                }
                songsList.add(songMap);

            }

            return "done";

        }

        @Override protected void onPostExecute(String result) {
           // Log.e("MyAsyncTask", "Received result: " + result);
            ListAdapter adapter = new SimpleAdapter(mContext, songsList,
                    R.layout.playlist_item, new String[] { "songTitle" }, new int[] {
                    R.id.songTitle });

            mPlayList.setAdapter(adapter);
            //this.progress.dismiss();
        }


    }

}
