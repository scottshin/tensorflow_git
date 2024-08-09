package com.ninano.karaoke.aiflow.base.rx.api;

import android.content.Context;

import com.ninano.karaoke.aiflow.base.parser.ProfileXmlLoader;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class RxAPI {
    private ProfileXmlLoader profileXmlLoader = null;
    private Context mContext = null;

    public RxAPI(Context context){
        this.mContext = context;
        this.profileXmlLoader = new ProfileXmlLoader(mContext);
    }

    public Observable<String> getArtistImgUrl(String keyword, String type) {
        return makeObservable(profileXmlLoader.getArtistImgUrl(keyword, type))
                .subscribeOn(Schedulers.newThread());
    }

    private static <T> Observable<T> makeObservable(final Callable<T> func) {
        return Observable.fromCallable(() -> func.call());
    }

}
