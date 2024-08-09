package com.ninano.karaoke.aiflow.base.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public final static String TAG = "DBHelper";

    public final static String DB_NAME = "songlist.db";
    public final static String TABLE_NAME = "songlist";

    public final static String COLUMS_GENRE = "genre";
    public final static String COLUMS_CREATEDATE = "createdate";
    public final static String COLUMS_SINGER = "singer";
    public final static String COLUMS_ALBUM = "album";
    public final static String COLUMS_TITLE = "title";
    public final static String COLUMS_TIEUP = "tieup";

    public final static String COLUMS_COMPANY = "company";
    public final static String COLUMS_WRITE = "write";
    public final static String COLUMS_COMPOSER = "composer";
    public final static String COLUMS_GENDER = "gender";

    Context mContext;

    private int prevVersion = 1;
    private  int currentVersion  = 1;

    public DBHelper(Context context) {
        // Database이름은 실제 단말상에서 생성될 파일이름입니다.
        // data/data/package명/databases/DATABASE_NAME식으로 저장
        super(context, DB_NAME, null, 1); // 제일 마지막 인자 : 버젼, 만약 버젼이 높아지면 onUpgrade를 수행한다.
        mContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate");
        // TODO Auto-generated method stub // 원래 여기에 create 문이 들어가야하나 기존에 있는 DB를 사용하므로 생략
//        db.execSQL(
//                "CREATE TABLE " + TABLE_NAME + "(" +
//                        " seq INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
//                        " songnum INTEGER NOT NULL, " +
//                        " company INTEGER, " +
//                        " title TEXT, " +
//                        " writer TEXT, " +
//                        " composer TEXT, " +
//                        " singer TEXT, " +
//                        " album INTEGER, " +
//                        " createdate TEXT, " +
//                        " genre TEXT, " +
//                        " gender TEXT );"
//        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);


        //Create SongList
//        db.execSQL(
//        "CREATE TABLE " + TABLE_NAME + "(" +
//                " seq INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
//                " songnum INTEGER NOT NULL, " +
//                " company INTEGER, " +
//                " title TEXT, " +
//                " writer TEXT, " +
//                " composer TEXT, " +
//                " singer TEXT, " +
//                " album INTEGER, " +
//                " createdate TEXT, " +
//                " genre TEXT, " +
//                " gender TEXT );"
//        );
    }
}


