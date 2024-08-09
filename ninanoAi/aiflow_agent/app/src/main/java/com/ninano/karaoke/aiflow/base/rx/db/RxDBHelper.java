package com.ninano.karaoke.aiflow.base.rx.db;

import android.util.Log;

import com.google.gson.JsonElement;
import com.ninano.karaoke.aiflow.base.dao.DBOpenHelper;
import com.ninano.karaoke.aiflow.base.data.Song;

import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class RxDBHelper {
    private DBOpenHelper dbOpenHelper = null;

    public RxDBHelper(DBOpenHelper _dbOpenHelper){
        this.dbOpenHelper = _dbOpenHelper;
    }

    public Observable<ArrayList<Song>> getSongs(HashMap<String, JsonElement> keywords) {
        return makeObservable(dbOpenHelper.getSongs(keywords))
                .subscribeOn(Schedulers.computation());
    }

    private static <T> Observable<T> makeObservable(final Callable<T> func) {
        return Observable.fromCallable(() -> func.call());
    }

}
