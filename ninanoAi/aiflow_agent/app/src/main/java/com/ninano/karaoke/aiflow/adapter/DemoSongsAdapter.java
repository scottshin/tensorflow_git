package com.ninano.karaoke.aiflow.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninano.karaoke.aiflow.R;
import com.ninano.karaoke.aiflow.base.data.Song;
import com.ninano.karaoke.aiflow.base.rx.api.RxAPI;

import java.util.ArrayList;


public class DemoSongsAdapter extends BaseAdapter {

    private ArrayList<Song> mData = null;
    private Context mContext = null;
    private LayoutInflater inflater = null;
    private static ViewHolder viewHolder = null;

    private RxAPI rxAPI = null;
    class ViewHolder{
//        public ImageView iconImageView = null;
        public TextView titleTextView = null;
        public TextView singerTextView = null;
    }
    public static final String TAG = DemoSongsAdapter.class.getName();
    public DemoSongsAdapter(Context context, ArrayList<Song> _data) {
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
            convertView = inflater.inflate(R.layout.item_result_song, parent, false);
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.tvContentSong) ;
            viewHolder.singerTextView = (TextView) convertView.findViewById(R.id.tvContentSinger) ;
//            viewHolder.titleTextView.setTypeface(CustomTypeFace.setFont(mContext, REGULAR));
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        try {
            Song song = getItem(position);
               viewHolder.titleTextView.setText(song.getTitle());
               viewHolder.singerTextView.setText(song.getSinger() );

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
