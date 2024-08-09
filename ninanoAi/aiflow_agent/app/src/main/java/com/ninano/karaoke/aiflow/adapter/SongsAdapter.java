package com.ninano.karaoke.aiflow.adapter;


import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ninano.karaoke.aiflow.R;
import com.ninano.karaoke.aiflow.base.NinanoStatus;
import com.ninano.karaoke.aiflow.base.data.Song;
import com.ninano.karaoke.aiflow.base.rx.api.RxAPI;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;


public class SongsAdapter extends BaseAdapter {

    private ArrayList<Song> mData = null;
    private Context mContext = null;
    private LayoutInflater inflater = null;
    private static ViewHolder viewHolder = null;

    private RxAPI rxAPI = null;
    class ViewHolder{
//        public ImageView iconImageView = null;
        public TextView titleTextView = null;
        public TextView artistTextView = null;
        public TextView albumeTextView = null;
        public TextView dateTextView = null;
        public TextView genreTextView = null;
    }
    public static final String TAG = SongsAdapter.class.getName();
    public SongsAdapter(Context context, ArrayList<Song> _data) {
        this.mContext = context;
        this.mData = _data;
        this.inflater = LayoutInflater.from(mContext);
        this.rxAPI = new RxAPI(mContext);
    }

    @Override
    public int getCount() {
        return mData!=null?mData.size():0;
    }

    @Override
    public Song getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_song, parent, false);
//            viewHolder.iconImageView = (ImageView) convertView.findViewById(R.id.ivFace) ;
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.tvContentSong) ;
            viewHolder.artistTextView = (TextView) convertView.findViewById(R.id.tvContentArtist) ;
            viewHolder.albumeTextView = (TextView) convertView.findViewById(R.id.tvContentAlbum) ;
            viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.tvContentDate) ;
            viewHolder.genreTextView = (TextView)convertView.findViewById(R.id.tvGenre);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        try {
            Song song = getItem(position);
            //이미지 받아오는곳~!
            //http://www.maniadb.com/api/search/%EC%95%84%EC%9D%B4%EC%9C%A0/?sr=artist&display=10&key=example&v=0.5
//            if(!TextUtils.isEmpty(song.getImgUrl())) {
//                Observable.just(song.getSinger())
//                        .doOnNext(item -> Log.d(TAG, "ANSER doOnNext::"+item))
//                        .subscribeOn(Schedulers.io())
//                        .subscribe(
//                                item -> {
//                                    Log.d(TAG, "OnNext:: START");
//                                    rxAPI
//                                            .getArtistImgUrl(item, NinanoStatus.XML_STATUS_ARTIST)
//                                            .subscribe(
//                                                    url->{item.setImgUrl(url);
//                                                    });
//                                    Log.d(TAG, "OnNext:: URL >"+item.getImgUrl());
//                                },
//                                throwable -> {
//                                    Log.d(TAG,"onError");
//                                },
//                                () -> {
//                                    Log.d(TAG,"onComplete");
//                                });
//                Glide.with(mContext).load(song.getImgUrl()).into(viewHolder.iconImageView);
//            }else{
//                viewHolder.iconImageView.setBackgroundColor(Color.parseColor("#c8c8c8"));
//            }
            viewHolder.titleTextView.setText(convertText(song, 0));
            viewHolder.artistTextView.setText(convertText(song, 1));
            viewHolder.albumeTextView.setText(convertText(song, 2));
            viewHolder.dateTextView.setText(convertText(song, 3));

            if(!TextUtils.isEmpty(song.getGenre())) {
                viewHolder.genreTextView.setVisibility(View.VISIBLE);
                viewHolder.genreTextView.setText(song.getGenre());
            }else{
                viewHolder.genreTextView.setVisibility(View.INVISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }

    private String convertText(Song song , int _type){
        String result = "";
        switch (_type){
            case 0:
                result = String.format(
                        mContext.getResources().getString(R.string.adapter_item_song_content_title_format),
                        song.getTitle());
                break;
            case 1:
                result = String.format(
                        mContext.getResources().getString(R.string.adapter_item_song_content_artist_format),
                        song.getSinger());
                break;
            case 2:
                result = String.format(
                        mContext.getResources().getString(R.string.adapter_item_song_content_albume_format),
                        song.getAlbum());

                break;
            case 3:
                result = String.format(
                        mContext.getResources().getString(R.string.adapter_item_song_content_date_format),
                        song.getCreated());

                break;
        }
        return result;
    }

    public void setData(ArrayList<Song> _data){
        mData = _data;
        notifyDataSetChanged();
    }
}
