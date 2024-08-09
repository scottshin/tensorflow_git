package com.ninano.karaoke.aiflow;

import android.app.ActivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ninano.karaoke.aiflow.adapter.DemoSongsAdapter;
import com.ninano.karaoke.aiflow.base.BaseActivity;
import com.ninano.karaoke.aiflow.base.Config;
import com.ninano.karaoke.aiflow.base.LanguageConfig;
import com.ninano.karaoke.aiflow.base.dao.DBHelper;
import com.ninano.karaoke.aiflow.base.dao.DBOpenHelper;
import com.ninano.karaoke.aiflow.base.data.Song;
import com.ninano.karaoke.aiflow.base.rx.api.RxAPI;
import com.ninano.karaoke.aiflow.utils.CustomDialog;
import com.ninano.karaoke.aiflow.utils.CustomTypeFace;
import com.ninano.karaoke.aiflow.utils.ReserveDialog;
import com.ninano.karaoke.aiflow.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import static com.ninano.karaoke.aiflow.DemoMainActivity.ROOT_ACTIVITY;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.ALBUM_ENT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.ARTIST_ENT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.GENRE_ENT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.SING_CONTRACT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.SONG_ENT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.YEAR_ENT;
import static com.ninano.karaoke.aiflow.utils.CustomTypeFace.MEDIUM;
import static com.ninano.karaoke.aiflow.utils.ViewUtils.returnRootActivity;

public class DemoResultListActivity extends BaseActivity implements AIListener{


    public static final String TAG = DemoResultListActivity.class.getName();

    private Gson gson = GsonFactory.getGson();

    //Text
    private AIDataService aiDataService;

//    private ImageButton ibTopBtn, ibBackBtn;
//    private LinearLayout llDummyScrollBar;
//    private ImageView ivListPrev, ivListNext;

    private TextView tvResultText;
//    private LinearLayout llListEmpty;


    private ListView lvList;

    private ArrayList<Song> mSongList;
    private DemoSongsAdapter songsAdapter;

    private int VISIBLE_VIEW_MAX_COUNT = 7;
    private int mNextPos = 0;

//    public BluetoothService btService = null;
    private  boolean isRead = false;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");


    }

    private void initService(final LanguageConfig selectedLanguage)
    {
        final AIConfiguration.SupportedLanguages lang = AIConfiguration.SupportedLanguages.fromLanguageTag(selectedLanguage.getLanguageCode());
        Log.i(TAG, "LANG["+lang+"]");
        final AIConfiguration config = new AIConfiguration(selectedLanguage.getAccessToken(),
                lang,
                AIConfiguration.RecognitionEngine.System);

        if(aiDataService != null){
            aiDataService = null;
        }
        //Text
        aiDataService = new AIDataService(this, config);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_main_list_result);
/*
        ibTopBtn = (ImageButton)findViewById(R.id.ibTopBtn);
        ibTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnRootActivity();
            }
        });
        ibBackBtn = (ImageButton)findViewById(R.id.ibBackBtn);
        ibBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
*/
        tvResultText = (TextView)findViewById(R.id.tvResultText);
//        try {
//            tvResultText.setTypeface(CustomTypeFace.setFont(DemoResultListActivity.this, MEDIUM));
//        }catch (Exception e){e.printStackTrace();}

//ppp        llDummyScrollBar = (LinearLayout)findViewById(R.id.llDummyScrollBar);
        lvList = (ListView)findViewById(R.id.lvSongList);

//        llListEmpty = (LinearLayout)findViewById(R.id.llSongEmpty);
//        llListEmpty.setVisibility(View.GONE);

        lvList.setVisibility(View.GONE);
//ppp        llDummyScrollBar.setVisibility(View.GONE);
        songsAdapter = new DemoSongsAdapter(DemoResultListActivity.this, null);
        lvList.setAdapter(songsAdapter);
/*
        ivListNext = (ImageView) findViewById(R.id.ivListNext);
        ivListNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(songsAdapter != null && (songsAdapter.getCount() > 8)) {
                    if(lvList != null ) {
//                        int gotoPosition = 0;
//                        if(lvList.getLastVisiblePosition() == VISIBLE_VIEW_MAX_COUNT ) {
//                            gotoPosition = (lvList.getLastVisiblePosition() == songsAdapter.getCount() ?
//                                    songsAdapter.getCount() :
//                                    lvList.getLastVisiblePosition() + 1);
//                            mNextPos = gotoPosition;
//                        }else{
//                            gotoPosition = mNextPos + 1;
////                            if(lvList.getFirstVisiblePosition() > VISIBLE_VIEW_MAX_COUNT){
////                                gotoPosition = lvList.getFirstVisiblePosition()+1;
////                            }else{
////                            }
//                        }
                        int gotoPosition = (lvList.getLastVisiblePosition() == songsAdapter.getCount() ?
                                songsAdapter.getCount() :
                                lvList.getLastVisiblePosition() + 1);
                        lvList.setSelection( gotoPosition );
                        Log.i(TAG, "lISTVIEW FIRST [" + lvList.getFirstVisiblePosition() + "] >>> LAST ["
                                + lvList.getLastVisiblePosition() + "] >> gotoPosition ["+gotoPosition+"]");
                    }
                }

            }
        });
        ivListPrev = (ImageView) findViewById(R.id.ivListPrev);
        ivListPrev.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(songsAdapter != null && (songsAdapter.getCount() > 8)) {
                    if(lvList != null && lvList.getFirstVisiblePosition() > 0) {
                        int gotoPosition = (lvList.getFirstVisiblePosition() == 0 ? 0: lvList.getFirstVisiblePosition()-1);
                        lvList.setSelection( gotoPosition );
                        Log.i(TAG, "lISTVIEW FIRST [" + lvList.getFirstVisiblePosition() + "] >>> LAST ["
                                + lvList.getLastVisiblePosition() + "] >> gotoPosition ["+gotoPosition+"]");
                    }
                }
            }
        });


*/

        //Language Ko Setting
        initService(Config.languages[ Constants.mSpokenLanguage ]);

        rxAPI = new RxAPI(getApplicationContext());

        if(getIntent() != null){
            tvResultText.setText(getIntent().getStringExtra("query"));
            if(tvResultText.getText() != null) {
                sendRequest();
            }else{
                onFinish();
            }
        }else{
            Log.i(TAG, "데이터 오류가 있습니다. 종료 됩니다. ");
            onFinish();
        }
        try {
            if (this != null)
                ACTIVITY_QUEUE.add((BaseActivity) this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void onFinish(){
        Log.i(TAG, "데이터 오류가 있습니다. 종료 됩니다. ");
        finish();
    }







    @Override
    protected void onPause() {
        super.onPause();
    }

    // --------------------------- Text Query Area --------------------------------------- //
    private void sendRequest() {

        final String queryString = !TextUtils.isEmpty(tvResultText.getText())? String.valueOf(tvResultText.getText()) : null;

        if (TextUtils.isEmpty(queryString) ) {
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


        Log.d(TAG, "==================== onResult");
        Log.i(TAG, "==================== ALL response : ["+gson.toJson(response).toString()+"]");
        Log.i(TAG, "==================== Received success response");

        final Status status = response.getStatus();
//        Log.i(TAG, "==================== NinanoStatus code: " + status.getCode());
//        Log.i(TAG, "==================== NinanoStatus type: " + status.getErrorType());

        final Result result = response.getResult();
        Log.i(TAG, "==================== Resolved query: " + result.getResolvedQuery());
//        Log.i(TAG, "==================== Action: " + result.getAction());

        final String speech = result.getFulfillment().getSpeech();
//        Log.i(TAG, "==================== Speech: " + speech);

        final Metadata metadata = result.getMetadata();
        if (metadata != null) {
            Log.i(TAG, "Intent id: [" + metadata.getIntentId() + "] Intent name: [" + metadata.getIntentName() + "]");
        }

        final HashMap<String, JsonElement> params = result.getParameters();
//        final HashMap<String, String> keywords = new HashMap<String, String>();
        mLogValue = "";
        if (params != null && !params.isEmpty()) {
            Log.i(TAG, "Parameters: >>>>>>>>>"+ result.getParameters().toString());
            Log.d(TAG, "==================== onResult END");

//            ViewUtils.showToast(DemoResultListActivity.this, mLogValue, R.layout.activity_ninano_main);
            Log.i(TAG, mLogValue);
            new AsyncTask<HashMap<String, JsonElement>, HashMap<String, JsonElement>, ArrayList<Song>>() {

                @Override
                protected ArrayList<Song> doInBackground(HashMap<String, JsonElement>... params)
                {

                    DBOpenHelper dbOpenHelper =  new DBOpenHelper(DemoResultListActivity.this);
                    anser = dbOpenHelper.selectSongList(params[0]);
                    return anser;
                }

                @Override
                protected void onPostExecute(ArrayList<Song> _list) {
                    super.onPostExecute(_list);
                    if(_list != null && _list.size() > 0) {
                        mSongList = _list;
                        songsAdapter = new DemoSongsAdapter(DemoResultListActivity.this, _list);
                        lvList.setAdapter(songsAdapter);
                        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Log.i(TAG,"POS["+i+"]" + mSongList.get(i).getSinger());


//                                ReserveDialog dlg = new ReserveDialog(DemoResultListActivity.this);
 //                               dlg.callFunction(mSongList.get(i));

                                CustomDialog customDialog = new CustomDialog(DemoResultListActivity.this);

                                // 커스텀 다이얼로그를 호출한다.
                                // 커스텀 다이얼로그의 결과를 출력할 TextView를 매개변수로 같이 넘겨준다.
                                customDialog.callFunction(mSongList.get(i));

                            }
                        });
                        showListView();
                    }else{
                        hideListView();
                    }
                }
            }.execute(params);

        } else {
            hideListView();
        }
    }

    private void hideListView(){
        lvList.setVisibility(View.GONE);
//ppp        llDummyScrollBar.setVisibility(View.GONE);
//        llListEmpty.setVisibility(View.VISIBLE);
    }

    private void showListView(){
//ppp        llDummyScrollBar.setVisibility(View.VISIBLE);
        lvList.setVisibility(View.VISIBLE);
//        llListEmpty.setVisibility(View.GONE);

        lvList.requestFocus();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


     @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if ( keyCode == 126 || keyCode == 127 )
        {
            super.onBackPressed();
        }
        else
        {
            //onRecStop();
        }

        if ( keyCode == KeyEvent.KEYCODE_BACK)
            super.onBackPressed();

        //return super.onKeyDown(keyCode, event);
        return (true);
    }




}

