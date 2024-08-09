package com.ninano.karaoke.aiflow;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ninano.karaoke.aiflow.adapter.DemoSongsAdapter;
import com.ninano.karaoke.aiflow.adapter.RsvSongsAdapter;
import com.ninano.karaoke.aiflow.base.BaseActivity;
import com.ninano.karaoke.aiflow.base.Config;
import com.ninano.karaoke.aiflow.base.LanguageConfig;
import com.ninano.karaoke.aiflow.base.dao.DBOpenHelper;
import com.ninano.karaoke.aiflow.base.data.Song;
import com.ninano.karaoke.aiflow.base.rx.api.RxAPI;
import com.ninano.karaoke.aiflow.utils.ReserveDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;



public class ReservedListActivity extends BaseActivity {


    public static final String TAG = ReservedListActivity.class.getName();

    private TextView tvResultText;
    private LinearLayout llListEmpty;

    private ListView lvList;

    private RsvSongsAdapter songsAdapter;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_main_list_result);
        tvResultText = (TextView)findViewById(R.id.tvResultText);
//        try {
//            tvResultText.setTypeface(CustomTypeFace.setFont(DemoResultListActivity.this, MEDIUM));
//        }catch (Exception e){e.printStackTrace();}

//ppp        llDummyScrollBar = (LinearLayout)findViewById(R.id.llDummyScrollBar);
        lvList = (ListView)findViewById(R.id.lvSongList);

  //      llListEmpty = (LinearLayout)findViewById(R.id.llSongEmpty);
 //       llListEmpty.setVisibility(View.GONE);
//        lvList.setVisibility(View.GONE);
//ppp        llDummyScrollBar.setVisibility(View.GONE);
        songsAdapter = new RsvSongsAdapter(ReservedListActivity.this, null);
        lvList.setAdapter(songsAdapter);


        songsAdapter.setData( RsvSingleton.getInstance().getRsvList() );
        songsAdapter.notifyDataSetChanged();//변경된 데이터 확인 후, 화면 갱신 

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
//    private void sendRequest() {
//
//        final String queryString = !TextUtils.isEmpty(tvResultText.getText()) ? String.valueOf(tvResultText.getText()) : null;
//
//        if (TextUtils.isEmpty(queryString)) {
//            onError(new AIError(getString(R.string.non_empty_query)));
//            return;
//        }
//    }



    private void hideListView(){
        lvList.setVisibility(View.GONE);
//ppp        llDummyScrollBar.setVisibility(View.GONE);
        llListEmpty.setVisibility(View.VISIBLE);
    }

    private void showListView(){
//ppp        llDummyScrollBar.setVisibility(View.VISIBLE);
        lvList.setVisibility(View.VISIBLE);
        llListEmpty.setVisibility(View.GONE);
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

