package com.ninano.karaoke.aiflow.aiflowremote;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ninano.karaoke.aiflow.aiflowremote.base.BluetoothPref;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    // Intent request code
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    public static final int GOOGLE_STT = 999;
    // Layout
    private Button btn_Connect;
    private TextView txt_Result;
//    private EditText tvContent;

    public BluetoothPref bluetoothPref;
    private BluetoothAdapter btAdapter;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        bluetoothPref = new BluetoothPref(MainActivity.this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        tvContent = (EditText)findViewById(R.id.tvContent);
//
//        tvContent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try{
//                    Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);            //intent 생성
//                    i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());    //호출한 패키지
//                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");                            //음성인식 언어 설정
//                    i.putExtra(RecognizerIntent.EXTRA_PROMPT, "말을 하세요.");                     //사용자에게 보여 줄 글자
//
//                    startActivityForResult(i, GOOGLE_STT);
//                } catch (ActivityNotFoundException ex) {
//                    Toast.makeText(MainActivity.this, "Activity Not Found", Toast.LENGTH_LONG).show();
//                }
//            }
//        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//

            }
        });


        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
//        if(data != null)
//            Log.i(TAG, "onActivityResult " + data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).toString());
//        switch (requestCode) {
//            case REQUEST_CONNECT_DEVICE:
//                // When DeviceListActivity returns with a device to connect
//                if (resultCode == Activity.RESULT_OK) {
//                    btService.getDeviceInfo(data);
//                }
//                break;
//            case REQUEST_ENABLE_BT:
//                // When the request to enable Bluetooth returns
//                if (resultCode == Activity.RESULT_OK) {
//                } else {
//                    Log.d(TAG, "Bluetooth is not enabled");
//                }
//                break;
//            case RESULT_OK:
//            case GOOGLE_STT: //음성키보드 결과값 받는 영역
//                String str = "";
//                ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
////                tvContent.setText(results.get(0));
//                connectDevice(results.get(0));
//                break;
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActionGoActivity(View v){
//        Intent intent = new Intent(MainActivity.this, CustomUIActivity.class);
//        startActivity(intent);
    }
}
