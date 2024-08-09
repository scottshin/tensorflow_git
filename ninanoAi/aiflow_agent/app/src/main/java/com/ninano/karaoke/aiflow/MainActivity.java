package com.ninano.karaoke.aiflow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ninano.karaoke.aiflow.adapter.SongsAdapter;
import com.ninano.karaoke.aiflow.base.BaseActivity;
import com.ninano.karaoke.aiflow.base.BluetoothPref;
import com.ninano.karaoke.aiflow.base.Config;
import com.ninano.karaoke.aiflow.base.LanguageConfig;
import com.ninano.karaoke.aiflow.base.dao.DBHelper;
import com.ninano.karaoke.aiflow.base.dao.DBOpenHelper;
import com.ninano.karaoke.aiflow.base.data.Song;
import com.ninano.karaoke.aiflow.base.rx.api.RxAPI;
import com.ninano.karaoke.aiflow.utils.CustomDialog;
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
import ai.api.android.AIService;
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

import static com.ninano.karaoke.aiflow.base.NinanoStatus.ALBUM_ENT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.ARTIST_ENT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.GENRE_ENT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.SONG_ENT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.YEAR_ENT;

public class MainActivity extends BaseActivity implements AIListener, AdapterView.OnItemSelectedListener{


    public static final String TAG = MainActivity.class.getName();


    private Gson gson = GsonFactory.getGson();

    //Voices
    private AIService aiService;

    //Text
    private AIDataService aiDataService;

    private EditText etQuery;
    private ProgressBar progressBar;
    private ImageButton ibVoiceSearch;
    private ImageButton ibTextSearch;
    private LinearLayout llListEmpty;
    private ListView lvList;
    private Spinner eventSpinner;

    private ArrayList<Song> mSongList;
    private SongsAdapter songsAdapter;

    public BluetoothPref bluetoothPref;
    private BluetoothAdapter btAdapter;

//    public BluetoothService btService = null;


    /**
     * Google Sample Start
     * */
    private String mConnectedDeviceName = null;
//    private BluetoothChatService mChatService = null;
    //String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private  boolean isRead = false;

    private void setStatus(int resId) {
        if (null == MainActivity.this) {
            return;
        }
        Toast.makeText(MainActivity.this, getResources().getString(resId).toString(), Toast.LENGTH_SHORT).show();
    }

    private void setStatus(CharSequence subTitle) {
        if (null == MainActivity.this) {
            return;
        }
        Toast.makeText(MainActivity.this, subTitle.toString(), Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
           /*
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(MainActivity.this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
               */
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkAudioRecordPermission();

        if (!btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
//        else if (mChatService == null) {
//            setupChat();
//        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
//        mChatService = new BluetoothChatService(MainActivity.this, blHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }


    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (btAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
/*
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(MainActivity.this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
//			tvResultValue.setText(mOutStringBuffer);
            Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>         sendMessage : " +mOutStringBuffer.toString());
        }
    }
*/
    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mChatService != null) {
//            mChatService.stop();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
/*
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
*/
        //TODO-aiDataSearch는 Text 전용으로 라이프사이클 컨트롤해주지 않아도 된다.
        if (aiService != null) {
            aiService.resume();
        }
    }
    /**
     * Google Sample End
     * */

    private void initService(final LanguageConfig selectedLanguage) {


        final AIConfiguration.SupportedLanguages lang = AIConfiguration.SupportedLanguages.fromLanguageTag(selectedLanguage.getLanguageCode());
        Log.i(TAG, "LANG["+lang+"]");
        final AIConfiguration config = new AIConfiguration(selectedLanguage.getAccessToken(),
                lang,
                AIConfiguration.RecognitionEngine.System);

        if (aiService != null) {
            aiService.pause();
            aiService = null;
        }

        if(aiDataService != null){
            aiDataService = null;
        }


        //Voice
        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        //Text
        aiDataService = new AIDataService(this, config);

    }


    public void onActiionBtn(View v){
        switch (v.getId()) {
            case R.id.ibSearchVoice:
                mLogValue = "";
                Toast.makeText(getApplicationContext(), "Voices Start", Toast.LENGTH_SHORT).show();

                aiService.stopListening();
                aiService.cancel();

                etQuery.setText("");
                startRecognition();
                break;
            case R.id.ibSearchBtn:
                mLogValue = "";
                sendRequest();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ninano_main);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        ibVoiceSearch = (ImageButton) findViewById(R.id.ibSearchVoice);
        ibTextSearch = (ImageButton) findViewById(R.id.ibSearchBtn);
        etQuery = (EditText)findViewById(R.id.etQuery);
        lvList = (ListView)findViewById(R.id.lvSongList);
        llListEmpty = (LinearLayout)findViewById(R.id.llSongEmpty);
        llListEmpty.setVisibility(View.VISIBLE);
        lvList.setVisibility(View.GONE);
        songsAdapter = new SongsAdapter(MainActivity.this, null);
        lvList.setAdapter(songsAdapter);





        //Language Ko Setting
        initService(Config.languages[ Constants.mSpokenLanguage] );

        rxAPI = new RxAPI(getApplicationContext());

        etQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        sendRequest();
                        InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etQuery.getWindowToken(), 0);
                        break;
                }
                return true;
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.selectLanguageSpinner);
        final ArrayAdapter<LanguageConfig> languagesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Config.languages);
        spinner.setAdapter(languagesAdapter);
        spinner.setOnItemSelectedListener(this);



        bluetoothPref = new BluetoothPref(MainActivity.this);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    @Override
    protected void onPause() {
        super.onPause();

        // use this method to disconnect from speech recognition service
        // Not destroying the SpeechRecognition object in onPause method would block other apps from using SpeechRecognition service
        //TODO-aiDataSearch는 Text 전용으로 라이프사이클 컨트롤해주지 않아도 된다.
        if (aiService != null) {
            aiService.pause();
        }
    }

    // --------------------------- Text Query Area --------------------------------------- //
    private void sendRequest() {

        final String queryString = !TextUtils.isEmpty(etQuery.getText())? String.valueOf(etQuery.getText()) : null;

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

    // --------------------------- Voice Query Area --------------------------------------- //
    public void startRecognition() {
        final String contextString = String.valueOf("");
        if (TextUtils.isEmpty(contextString)) {
            aiService.startListening();
        } else {
            final List<AIContext> contexts = Collections.singletonList(new AIContext(contextString));
            final RequestExtras requestExtras = new RequestExtras(contexts, null);
            aiService.startListening(requestExtras);
        }

    }
    // --------------------------- Voice Query Area --------------------------------------- //

    //    ----------------------- Service Listener;
    private String mArtistName = "";
    private String mSongName = "";
    private String mAlbumName = "";
    private String mArtistUrl = "";
    private String mLogValue = "";
    ArrayList<Song> anser = null;

    RxAPI rxAPI = null;
    @Override
    public void onResult(final AIResponse response) {


        Log.d(TAG, "==================== onResult");
        Log.i(TAG, "==================== ALL response : ["+gson.toJson(response).toString()+"]");
        Log.i(TAG, "==================== Received success response");

        final Status status = response.getStatus();
        Log.i(TAG, "==================== NinanoStatus code: " + status.getCode());
        Log.i(TAG, "==================== NinanoStatus type: " + status.getErrorType());

        final Result result = response.getResult();
        Log.i(TAG, "==================== Resolved query: " + result.getResolvedQuery());

        io.reactivex.Observable.just("",result.getResolvedQuery())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        d -> etQuery.setText(d),
                        e->{e.printStackTrace();},
                        ()->Log.d(TAG,"etQuery Setting!!!"
                ));

        Log.i(TAG, "==================== Action: " + result.getAction());

        final String speech = result.getFulfillment().getSpeech();
        Log.i(TAG, "==================== Speech: " + speech);

        //        지정한 Speech - "노래를 찾았습니다" 단어를 듣고싶으면 아래 TTS.speak를 사용해서 Client에서 들을수있다.
        //        TTS.speak(speech);

        final Metadata metadata = result.getMetadata();
        if (metadata != null) {
            Log.i(TAG, "Intent id: " + metadata.getIntentId());
            Log.i(TAG, "Intent name: " + metadata.getIntentName());
        }

        final HashMap<String, JsonElement> params = result.getParameters();
        final HashMap<String, String> keywords = new HashMap<String, String>();
        mLogValue = "";
        if (params != null && !params.isEmpty()) {
            Log.i(TAG, "Parameters: ");
            for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {

                if(GENRE_ENT.equals(entry.getKey())){
                    keywords.put(DBHelper.COLUMS_GENRE, entry.getValue().toString());
                }
                else if(YEAR_ENT.equals(entry.getKey())){
                    keywords.put(DBHelper.COLUMS_CREATEDATE, entry.getValue().toString().replaceAll("[^0-9]", ""));
                }
//                        else if(MONTH_ENT.equals(entry.getKey())){
//                            keywords.put(DBHelper.COLUMS_GENRE, entry.getValue().toString());
//                        }
                else if(ARTIST_ENT.equals(entry.getKey())){
                    keywords.put(DBHelper.COLUMS_SINGER, entry.getValue().toString());
                    mArtistName = entry.getValue().toString();
                }
                else if(ALBUM_ENT.equals(entry.getKey())){
                    keywords.put(DBHelper.COLUMS_ALBUM, entry.getValue().toString());
                    mAlbumName = entry.getValue().toString();
                }
                else if(SONG_ENT.equals(entry.getKey())){
                    keywords.put(DBHelper.COLUMS_TITLE, entry.getValue().toString());
                    mSongName = entry.getValue().toString();
                }
                else{
                    //...
                }

                mLogValue += String.format("%s: %s", entry.getKey(), entry.getValue().toString());
                Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));

            }

            ViewUtils.showToast(MainActivity.this, mLogValue, R.layout.activity_ninano_main);

            if(keywords.size() > 0) {
//                RxDBHelper rxDBHelper = new RxDBHelper(dbHelper);
//
//                Observable source = rxDBHelper.getSongs(keywords);
//                source.subscribe(lists -> {anser = (ArrayList<Song>)lists; });
//
//                if (anser != null) {
//                    Log.i(TAG, "############################ SQL ANSER ::" + anser.toString());

//                    Observable.fromIterable(anser)
//                        .doOnNext(item -> Log.d(TAG, "ANSER doOnNext::"+item.getSinger()))
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                item -> {
//                                    Log.d(TAG, "OnNext:: START");
//                                    rxAPI
//                                        .getArtistImgUrl(item.getSinger(), NinanoStatus.XML_STATUS_ARTIST)
//                                        .subscribe(
//                                                url->{
//                                                    item.setImgUrl(url);
//                                                    Log.d(TAG, "rxAPI.OnNext:: URL >"+url);
//                                                },
//                                                throwable -> {
//                                                    Log.d(TAG,"rxAPI.onError");
//                                                },
//                                                () -> {
//                                                    Log.d(TAG,"rxAPI.onComplete");
//                                                    songsAdapter.setData(anser);
//                                                }
//                                        );
//                                    Log.d(TAG, "OnNext:: URL >"+item.getImgUrl());
//                                },
//                                throwable -> {
//                                    Log.d(TAG,"onError");
//                                },
//                                () -> {
//                                    Log.d(TAG,"onComplete");
//                        });

//
//                    songsAdapter = new SongsAdapter(MainActivity.this, anser);
//                    lvList.setAdapter(songsAdapter);


                    new AsyncTask<HashMap<String, JsonElement>, HashMap<String, JsonElement>, ArrayList<Song>>() {

                        @Override
                        protected ArrayList<Song> doInBackground(HashMap<String, JsonElement>... params) {

                            DBOpenHelper dbOpenHelper =  new DBOpenHelper(MainActivity.this);
                            anser = dbOpenHelper.selectSongList(params[0]);

                            //리스트 아티스트 하나씩 뽑아서 xml뽑는 영역
//                            ProfileXmlLoader profileXmlLoader = new ProfileXmlLoader(MainActivity.this);
//                            for(Song item : anser) {
//                                item.setImgUrl(profileXmlLoader.loader(item.getSinger(), XML_STATUS_ARTIST));
//                            }
                            return anser;
                        }

                        @Override
                        protected void onPostExecute(ArrayList<Song> _list) {
                            super.onPostExecute(_list);
                            if(_list != null && _list.size() > 0) {
                                mSongList = _list;
                                songsAdapter = new SongsAdapter(MainActivity.this, _list);
                                lvList.setAdapter(songsAdapter);
                                lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        Log.i(TAG,"POS["+i+"]" + mSongList.get(i).getSinger());
                                        CustomDialog customDialog = new CustomDialog(MainActivity.this);

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
            }else{
                hideListView();
            }

    }

    private void hideListView(){
        lvList.setVisibility(View.GONE);
        llListEmpty.setVisibility(View.VISIBLE);
    }

    private void showListView(){
        lvList.setVisibility(View.VISIBLE);
        llListEmpty.setVisibility(View.GONE);
    }

    @Override
    public void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, error.toString());
            }
        });
    }

    @Override
    public void onAudioLevel(final float level) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float positiveLevel = Math.abs(level);

                if (positiveLevel > 100) {
                    positiveLevel = 100;
                }
                progressBar.setProgress((int) positiveLevel);
            }
        });
    }

    @Override
    public void onListeningStarted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                recIndicator.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onListeningCanceled() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                recIndicator.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onListeningFinished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                recIndicator.setVisibility(View.INVISIBLE);
            }
        });
    }

//    ----------------------- Service Listener;

    @Override
    public void onBackPressed() {


       //
       System.exit(1);
        super.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        final LanguageConfig selectedLanguage = (LanguageConfig) parent.getItemAtPosition(position);
        initService(selectedLanguage);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //


}
