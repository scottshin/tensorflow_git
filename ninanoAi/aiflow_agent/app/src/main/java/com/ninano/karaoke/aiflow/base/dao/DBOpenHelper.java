package com.ninano.karaoke.aiflow.base.dao;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.JsonElement;
import com.ninano.karaoke.aiflow.base.data.Song;

import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Subscriber;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.ninano.karaoke.aiflow.base.NinanoStatus.ALBUM_ENT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.ARTIST_ENT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.GENRE_ENT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.SING_CONTRACT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.SONG_ENT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.ACTION_ENT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.TIEUP_CONTRACT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.TIEUP_ENT;
import static com.ninano.karaoke.aiflow.base.NinanoStatus.YEAR_ENT;
import static com.ninano.karaoke.aiflow.base.dao.DBHelper.*;
import static io.reactivex.schedulers.Schedulers.io;

public class DBOpenHelper {

    private Context mContext;
    private SQLiteDatabase mDB;

    private DBHelper mHelper;


    private final static String PACKAGE_NAME = "com.ninano.karaoke.aiflow";
    private final static String TAG = "DBOpenHelper";

    public DBOpenHelper(Context _context) {
        mContext = _context;
        mHelper = new DBHelper(mContext);
    }

    public Callable<ArrayList<Song>> getSongs(HashMap<String, JsonElement> params) {
        return new Callable<ArrayList<Song>>() {
            @Override
            public ArrayList<Song> call() {
                return selectSongList(params);
            }
        };
    }

    /**
     *
     * */
    public ArrayList<Song> selectSongList(HashMap<String, JsonElement> params) {


        ArrayList<Song> rows = new ArrayList<Song>();
        HashMap<String, String> keywords = new HashMap<String, String>();
        boolean isArtsit = false;

        for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
            if (GENRE_ENT.equals(entry.getKey())) {
                keywords.put(DBHelper.COLUMS_GENRE, entry.getValue().toString());
            } else if (YEAR_ENT.equals(entry.getKey())) {
                keywords.put(DBHelper.COLUMS_CREATEDATE, entry.getValue().toString().replaceAll("[^0-9]", ""));
            }
//        else if(MONTH_ENT.equals(entry.getKey())){
//            keywords.put(DBHelper.COLUMS_GENRE, entry.getValue().toString());
//        }
            else if (ARTIST_ENT.equals(entry.getKey())) {
                keywords.put(DBHelper.COLUMS_SINGER, entry.getValue().toString());
                if(entry.getValue() != null ) isArtsit = true;
            } else if (ALBUM_ENT.equals(entry.getKey())) {
                keywords.put(DBHelper.COLUMS_ALBUM, entry.getValue().toString());
            } else if (SONG_ENT.equals(entry.getKey())) {
                keywords.put(DBHelper.COLUMS_TITLE, entry.getValue().toString());
            } else if (SING_CONTRACT.equals(entry.getKey())) {
                try {
                    JSONObject singContract = new JSONObject(entry.getValue().toString());
                    keywords.put(DBHelper.COLUMS_SINGER, singContract.getString(ARTIST_ENT));
                    keywords.put(DBHelper.COLUMS_TITLE, singContract.getString(SONG_ENT));

                    if(singContract.getString(ARTIST_ENT) != null ) isArtsit = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if (TIEUP_ENT.equals(entry.getKey())) {
                //{TIEUP_ENT="TBS金曜ドラマ『アンナチュラル』主題歌"}
                keywords.put(DBHelper.COLUMS_TIEUP, entry.getValue().toString());
            } else if (TIEUP_CONTRACT.equals(entry.getKey())) {
                try {
                    //{TIEUP_CONTRACT={"TIEUP_ENT":"TBS系ドラマ「聖者の行進」主題歌","SONG_ENT":"糸《生演奏》"}}
                    JSONObject tieupContract = new JSONObject(entry.getValue().toString());
                    keywords.put(DBHelper.COLUMS_TIEUP, tieupContract.getString(TIEUP_ENT));
                    keywords.put(DBHelper.COLUMS_TITLE, tieupContract.getString(SONG_ENT));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }




            else {
                //...
            }
        }

        mDB = mHelper.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT * FROM " + TABLE_NAME + " ";
        String whereSearch = "WHERE ";

        String options = " COLLATE NOCASE ORDER BY createdate DESC"; //대소문자 구문 없이 검색
        int i = 0;
        for( Map.Entry<String, String> elem : keywords.entrySet() ){

            if(keywords.size() > 1) {
                if(i > 0){
// ppp                   if (isArtsit) {
// ppp                      whereSearch += " OR ";
// ppp                   } else
                    {
                        whereSearch += " AND ";
                    }
                }
            }

            if(COLUMS_CREATEDATE.equals(elem.getKey())){

                int created = Integer.parseInt(elem.getValue());

                if(1900 > created && created < 2000){
                    created = created + 1900;
                }
                whereSearch += " cast(substr(createdate,0,5) as integer)  >= " + created +
                                " AND  cast(substr(createdate,0,5) as integer)  <= " + (created + 9);
            }else{
                whereSearch += elem.getKey() + " Like '%" + elem.getValue().replaceAll("\"", "") + "%'";
            }

            i++;
        }

        query += whereSearch + options;

        Log.d(TAG, " >>>>>>>>>>>>>>>>>>>                 SELECT Query :" + query);
        try {
            cursor = mDB.rawQuery(query, null);
            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                do {
                    Song row = new Song();
                    row.setGenre(cursor.getString(cursor.getColumnIndex(COLUMS_GENRE)));
                    row.setCreated(cursor.getString(cursor.getColumnIndex(COLUMS_CREATEDATE)));
                    row.setSinger(cursor.getString(cursor.getColumnIndex(COLUMS_SINGER)));
                    row.setAlbum(cursor.getString(cursor.getColumnIndex(COLUMS_ALBUM)));
                    row.setTitle(cursor.getString(cursor.getColumnIndex(COLUMS_TITLE)));
                    row.setTieup(cursor.getString(cursor.getColumnIndex(COLUMS_TIEUP)));

                    rows.add(row);
                } while (cursor.moveToNext());
            } else {
                rows = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null) {
                cursor.close();
            }
            mHelper.close();
        }



        return rows;
    }

    /**
     * DB 파일을 복사하자
     * 내장되어 있는 DB를 외장메모리로 복사하는 기능 *
     * <p>
     **/
    private void transferFile(String toPath, String toFilename, String srcName) {
        String dstName = toPath + "/" + toFilename;
        try {
            File targetPath = new File(toPath);
            Log.d(TAG, "Transfer Src = " + srcName);
            Log.d(TAG, "Transfer Dst = " + dstName);
            targetPath.mkdirs();
            File fi = new File(srcName);
            File fo = new File(dstName);
            FileInputStream fis = new FileInputStream(fi);
            BufferedInputStream bis = new BufferedInputStream(fis);
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            if (fo.exists()) {
                fo.delete();
                fo.createNewFile();
            }
            fos = new FileOutputStream(fo);
            bos = new BufferedOutputStream(fos);
            int read = -1;
            byte[] buffer = new byte[1024];
            while ((read = bis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, read);
            }
            bos.flush();
            fos.close();
            bos.close();
            fis.close();
            bis.close();
        } catch (FileNotFoundException e) {
            Log.w(TAG, "FILE not found(" + srcName + " or " + dstName + ")");
        } catch (IOException e) {
            Log.w(TAG, "io exception(" + srcName + "to" + dstName + ")");
        }
    }


    //DB가 있나 체크하기
    public boolean isCheckDB() {
        String filePath = "/data/data/" + PACKAGE_NAME + "/databases/" + DB_NAME;
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    // DB를 복사하기 // assets의 /db/xxxx.db 파일을 설치된 프로그램의 내부 DB공간으로 복사하기
    public void copyDB() {
        Log.d(TAG, "copyDB");
        AssetManager manager = mContext.getAssets();
        String folderPath = "/data/data/" + PACKAGE_NAME + "/databases";
        String filePath = "/data/data/" + PACKAGE_NAME + "/databases/" + DB_NAME;
        File folder = new File(folderPath);
        File file = new File(filePath);
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            InputStream is = manager.open("db/" + DB_NAME);
            BufferedInputStream bis = new BufferedInputStream(is);
            if (folder.exists()) {
            } else {
                folder.mkdirs();
            }
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            int read = -1;
            byte[] buffer = new byte[1024];
            while ((read = bis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, read);
            }
            bos.flush();
            bos.close();
            fos.close();
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("ErrorMessage : ", e.getMessage());
        }
    }


}
