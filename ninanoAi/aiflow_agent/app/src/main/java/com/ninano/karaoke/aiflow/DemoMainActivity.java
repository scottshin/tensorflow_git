package com.ninano.karaoke.aiflow;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ninano.karaoke.aiflow.base.BaseActivity;
import com.ninano.karaoke.aiflow.base.Config;
import com.ninano.karaoke.aiflow.base.LanguageConfig;
import com.ninano.karaoke.aiflow.base.dao.DBHelper;
import com.ninano.karaoke.aiflow.base.dao.DBOpenHelper;
import com.ninano.karaoke.aiflow.base.data.Song;
import com.ninano.karaoke.aiflow.base.rx.api.RxAPI;
import com.ninano.karaoke.aiflow.utils.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.GsonFactory;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIEvent;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.ninano.karaoke.aiflow.base.NinanoStatus.*;

public class DemoMainActivity extends BaseActivity implements AIListener, View.OnClickListener{


    public boolean isReserve = false;
    public boolean isStart = false;
    public boolean isSearch = false;
    public boolean isSingerContract = false;

    public static final String TAG = DemoMainActivity.class.getName();

    private Gson gson = GsonFactory.getGson();
    //Text
    private AIDataService aiDataService;
//    private ImageButton ibTopBtn = null;
//    private Button ibTopSingBtn = null;
    private RelativeLayout rlSearchArea;
    private TextView etQuery;
    private VideoView mVideoView = null;


    private TextView tvCloud;


    private int stopPosition = 0;

    Handler mKeywordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_SHORT).show();
            String readMessage = (String)msg.obj;  // 아이유의 너의의미
//           String readMessage = "最後の雨";
            etQuery.setText(readMessage);
            //onChageSearchView(true);
            sendRequest( readMessage );
        }
    };

    private int video_inx = 0;
    private String[] video_array = new String[] {
                "xing_demo_cm3.mp4",
                "xing_demo_cm0.mp4",
                "xing_demo_cm1.mp4",
                "xing_demo_cm2.mp4"
    };
    private CheckBox cbToggle = null;

    private void setStatus(int resId) {
        if (null == DemoMainActivity.this) {
            return;
        }
        Log.i(TAG, getResources().getString(resId).toString());
    }

    private void setStatus(CharSequence subTitle) {
        if (null == DemoMainActivity.this) {
            return;
        }
        Log.i(TAG, subTitle.toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAudioRecordPermission();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

        if ( null != mVideoView ) {
        //    mVideoView.start();

            mVideoView.seekTo( stopPosition );
            mVideoView.start();

        }
    }

    private void initService(final LanguageConfig selectedLanguage)
    {
        final AIConfiguration.SupportedLanguages lang = AIConfiguration.SupportedLanguages.fromLanguageTag(selectedLanguage.getLanguageCode());
        Log.i(TAG, "LANG[" + lang + "]");
        final AIConfiguration config = new AIConfiguration(selectedLanguage.getAccessToken(),
                lang,
                AIConfiguration.RecognitionEngine.System);

        if (aiDataService != null) {
            aiDataService = null;
        }
        //
        // Text
        //
        aiDataService = new AIDataService(this, config);
    }

    public static Activity ROOT_ACTIVITY = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
       // setContentView(R.layout.demo_main_act);
        setContentView(R.layout.demo_main_video);

//
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if ( permissionCheck == -1)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, permissionCheck);
        }


        tvCloud = (TextView) findViewById(R.id.tvCloud);
        tvCloud.setVisibility( View.INVISIBLE );


/*ppp

        Button btn = (Button) findViewById(R.id.button4);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AISingleton.getInstance().mContext = getApplicationContext();
                AISingleton.getInstance().mKeywordHandler = mKeywordHandler;
                AISingleton.getInstance().doRecStart(AISingleton.getInstance().mConfig, DemoMainActivity.this);
            }
        });

*/

/* ppp   for demo_layout_vert
        ibTopBtn = (ImageButton) findViewById(R.id.ibTopBtn);
        ibTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btn click ");
            }
        });

*/

/*
        ibTopSingBtn = (Button) findViewById(R.id.ibTopSingBtn);
        ibTopSingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String readMessage = "김건모";  // 아이유의 너의의미
                String readMessage = "真夏の果実";
                etQuery.setText(readMessage);
                onChageSearchView(true);
                sendRequest( readMessage );

                //Intent intent = new Intent( getApplicationContext(), SpeachActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                //intent.putExtra("language", mSelectedLanguage);
                //startActivityForResult(intent, 1000);

//                AISingleton.getInstance().mContext = getApplicationContext();
//                AISingleton.getInstance().mKeywordHandler = mKeywordHandler;
//                AISingleton.doRecStart( AISingleton.getInstance().mConfig );
            }
        });
        Button btnBySong = (Button) findViewById(R.id.btnBySong);
        btnBySong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v )
            {
                Intent i = new Intent(DemoMainActivity.this, DemoResultListActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("query", "전체곡");

                startActivity(i);
            }
        });
*/
        rlSearchArea = (RelativeLayout) findViewById(R.id.rlSearchArea);
        rlSearchArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChageSearchView(false);
            }
        });
        etQuery = (TextView) findViewById(R.id.etQuery);
//        try {
//            etQuery.setTypeface(CustomTypeFace.setFont(DemoMainActivity.this, MEDIUM));
//        }catch (Exception e){e.printStackTrace();}
        //Language Ko Setting

        initService(Config.languages[  Constants.mSpokenLanguage  ]);
        rxAPI = new RxAPI(getApplicationContext());


/* ppp main_layout_vert
        cbToggle = (CheckBox) findViewById(R.id.cbToggle);
        cbToggle.setOnClickListener(this);

        cbToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                onActionLangToggle(b);
                Toast.makeText(DemoMainActivity.this, "cbToggle Change ["+b+"]", Toast.LENGTH_SHORT).show();
            }
        });
*/


        String sdcardPath = null;
        String sdcardStat = Environment.getExternalStorageState();
        if(sdcardStat.equals(Environment.MEDIA_MOUNTED))
        {
            sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        mVideoView = (VideoView) findViewById(R.id.videoView);
/*
//        mVideoView.setVideoPath( sdcardPath + "/iu.mp4");
//        mVideoView.setVideoPath( sdcardPath + "/xing.mp4");
        mVideoView.setVideoPath( sdcardPath + "/xing_demo_cm3.mp4");
        mVideoView.start();

        mVideoView.setOnClickListener(new VideoView.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                mp.setVolume(0,0);
                return false;
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
//                mVideoView.start(); //need to make transition seamless.

                String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                mVideoView.setVideoPath( sdcardPath + '/'+ video_array[video_inx]);
                video_inx = (++video_inx % video_array.length);
                mVideoView.start();
                //mp.start();
            }
        });
*/
    }


    @Override
    protected void onPause() {
        super.onPause();

        if ( null != mVideoView ) {
            //    mVideoView.start();

               stopPosition = mVideoView.getCurrentPosition();
               mVideoView.pause();
        }
    }

    // --------------------------- Text Query Area --------------------------------------- //
    private void sendRequest(String strKey ) {
        final String queryString = !TextUtils.isEmpty(strKey) ? String.valueOf(strKey) : null;
        if (TextUtils.isEmpty(queryString)) {
            onError(new AIError(getString(R.string.non_empty_query)));
            return;
        }

        final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {

            private AIError aiError;

            @Override
            protected AIResponse doInBackground(final String... params) {
                final AIRequest request = new AIRequest();
                String query = params[0];
                String event = params[1];

                if (!TextUtils.isEmpty(query))
                    request.setQuery(query);
                if (!TextUtils.isEmpty(event))
                    request.setEvent(new AIEvent(event));
                final String contextString = params[2];
                RequestExtras requestExtras = null;
                if (!TextUtils.isEmpty(contextString)) {
                    final List<AIContext> contexts = Collections.singletonList(new AIContext(contextString));
                    requestExtras = new RequestExtras(contexts, null);
                }

                try {
                    return aiDataService.request(request, requestExtras);
                } catch (final AIServiceException e) {
                    aiError = new AIError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final AIResponse response) {
                if (response != null) {
                    onResult(response);
                } else {
                    onError(aiError);
                }
            }
        };

        task.execute(queryString, "", "");
    }
    // --------------------------- Text Query Area --------------------------------------- //

    //    ----------------------- Service Listener;
    private String mLogValue = "";
    ArrayList<Song> anser = null;

    RxAPI rxAPI = null;

    @Override
    public void onResult(final AIResponse response) {



//
        final Status status = response.getStatus();
//        Log.i(TAG, "==================== NinanoStatus code: " + status.getCode());
//        Log.i(TAG, "==================== NinanoStatus type: " + status.getErrorType());
//
        final Result result = response.getResult();
        Log.d(TAG, "==================== onResult START Query ["+ result.getResolvedQuery()+"]");
        Log.i(TAG, "==================== ALL response : [" + gson.toJson(response).toString() + "]");
        Log.i(TAG, "==================== Received success response");

        io.reactivex.Observable.just("", result.getResolvedQuery())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        d -> etQuery.setText(d),
                        e -> {
                            e.printStackTrace();
                        },
                        () -> Log.d(TAG, "etQuery Setting!!!"
                        ));
//
//        Log.i(TAG, "==================== Action: " + result.getAction());
//
//        final String speech = result.getFulfillment().getSpeech();
//        Log.i(TAG, "==================== Speech: " + speech);

        final Metadata metadata = result.getMetadata();
        if (metadata != null) {
            Log.i(TAG, "Intent id: [" + metadata.getIntentId() + "] Intent name: [" + metadata.getIntentName() + "]");
        }

        final HashMap<String, JsonElement> params = result.getParameters();
//        final HashMap<String, String> keywords = new HashMap<String, String>();
        mLogValue = "";
        if (params != null && !params.isEmpty()) {
            Log.i(TAG, "\nParameters: >>>>>>>>>"+ result.getParameters().toString()+"\n");
            Log.d(TAG, "==================== onResult END");
                new AsyncTask<HashMap<String, JsonElement>, HashMap<String, JsonElement>, ArrayList<Song>>() {
                    @Override
                    protected ArrayList<Song> doInBackground(HashMap<String, JsonElement>... params) {
                        DBOpenHelper dbOpenHelper =  new DBOpenHelper(DemoMainActivity.this);
                        anser = dbOpenHelper.selectSongList(params[0]);

//                        for ( int i = 0; i < params[0].size(); i++)
                        {

                            HashMap<String, JsonElement> par = params[0];
                            ArrayList<Song> rows = new ArrayList<Song>();
                            HashMap<String, String> keywords = new HashMap<String, String>();
                            boolean isArtsit = false;
                            isReserve = false;
                            isStart = false;
                            isSearch= false;
                            isSingerContract = false;

                            for (final Map.Entry<String, JsonElement> entry : par.entrySet()) {
                                if (ARTIST_ENT.equals(entry.getKey())) {
                                    keywords.put(DBHelper.COLUMS_SINGER, entry.getValue().toString());
                                    if (entry.getValue() != null)
                                        isArtsit = true;
                                } else if (SONG_ENT.equals(entry.getKey())) {
                                    keywords.put(DBHelper.COLUMS_TITLE, entry.getValue().toString());
                                } else if (ACTION_ENT.equals(entry.getKey())) {

                                    String s =  entry.getValue().toString();
                                    isReserve = "\"RESERVE\"".equals( s );
                                    isStart = "\"START\"".equals( s );
                                    isSearch = "\"SEARCH\"".equals( s );


                                } else if (SING_CONTRACT.equals(entry.getKey())) {

                                    isSingerContract = true;

                                    //HashMap<String, JsonElement> par2 = <String,JsonElement> entry.getValue();

                                }


                            }
                        }
                        return anser;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<Song> _list) {
                        super.onPostExecute(_list);
                        if(_list != null && _list.size() > 0) {
                            Log.d(TAG, "onPostExecute >>>>>>>  "+_list.toString());
                            Log.i(TAG, "데이터가 있다 >>>  리스트 액티비티를 노출 시켜줘야 한다. ");
                            onChageSearchView(false);
                            onActionChangeActivity(true);


                        }else{
//                            hideListView();
                            onChageSearchView(false);

                            ViewUtils.showToast(DemoMainActivity.this, "検索したデータがありません。",Toast.LENGTH_SHORT);
                            Log.i(TAG, "데이터가 없다. ");
                        }
                    }
                }.execute(params);
        } else {
            onChageSearchView(false);

            ViewUtils.showToast(DemoMainActivity.this, "検索したデータがありません。",Toast.LENGTH_SHORT);
            Log.i(TAG, "데이터가 없다. ");
        }

    }

    @Override
    public void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, error.toString());
            }
        });
    }

    @Override
    public void onAudioLevel(final float level) {
    }

    @Override
    public void onListeningStarted() {
    }

    @Override
    public void onListeningCanceled() {
    }

    @Override
    public void onListeningFinished() {
    }

//    ----------------------- Service Listener;



    //
    // Finish
    //
    private void onActionChangeActivity(boolean type){

//        if(!ViewUtils.onTopActivity(DemoMainActivity.this).equals(TAG)) {
//            ViewUtils.returnRootActivity();
//        }

//        if(ACTIVITY_QUEUE.size() > 0){
//            ViewUtils.returnRootActivity();
//        }



        Log.e("ERR", "isReserve" + isReserve );

        if ( isReserve | isStart )
        {
/*

 //           tvClound.setVisi

            this.tvCloud.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){

                    tvCloud.setVisibility(View.INVISIBLE);
                }
            }, 5000);    //3초 뒤에

*/

            if ( anser.size( ) > 0 ) {


                if ( isReserve  )
                    ViewUtils.cloudToast(DemoMainActivity.this, "予約しました。", anser.get(0).getTitle(), Toast.LENGTH_SHORT);
                else
                    ViewUtils.cloudToast(DemoMainActivity.this, "曲スタート", anser.get(0).getTitle(), Toast.LENGTH_SHORT);

                RsvSingleton.getInstance().Reserv(anser.get(0));
            }


        }
        else {
            Intent i = new Intent(DemoMainActivity.this, DemoResultListActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("query", etQuery.getText());
            startActivity(i);
        }
    }

    private void onChageSearchView(boolean type) {

        rlSearchArea.setVisibility( type==true ?View.VISIBLE:View.GONE);
        rlSearchArea.setFocusable( type );
        rlSearchArea.setClickable( type );
/*
        if ( ibTopBtn != null) {
            ibTopBtn.setFocusable( type ? true : false);
            ibTopBtn.setClickable( type ? true : false);
        }
*/
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {

        Log.d("abc", "key up" + Integer.toString( keyCode ) );
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.d("abc", "key down" + Integer.toString( keyCode ) );

        if ( keyCode == 126 || keyCode == 127 || keyCode == 85  || keyCode == 84/*음성키*/)
        {

            System.gc();
            {
                Intent intent = new Intent(this, SpeachActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                intent.putExtra("language", Constants.mSpokenLanguage);
                //startActivityForResult(intent, 1000);
                startActivity( intent );
            }
            {
                        AISingleton s = AISingleton.getInstance();
                        s.mContext = getApplicationContext();
                        s.mKeywordHandler = mKeywordHandler;
                        s.doRecStart(AISingleton.getInstance().mConfig, this);
            }
            return true;
        }
        else
        if ( keyCode == 82 )        // reserved list
        {

                Intent i = new Intent(DemoMainActivity.this, ReservedListActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("query", "         予約リスト");

                startActivity(i);
                return true;
        }
        if ( keyCode == 4) {
            System.exit(1);
        }
        else
        {
            //onRecStop();
        }
        return (super.onKeyDown(keyCode, event));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK){
//            String readMessage = data.getStringExtra("return");
//                etQuery.setText(readMessage);
//                onChageSearchView(true);
//                sendRequest( readMessage );
        }
    }

    public void onActionLangToggle(boolean type){
        //         true :  kr
        // default false :  jp
        Constants.mSpokenLanguage = type==true ? 0 : 1;
        cbToggle.setChecked( type );
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onBackPressed() {

        //
        System.exit(1);
        super.onBackPressed();
    }
}




