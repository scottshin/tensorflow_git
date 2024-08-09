package com.ninano.karaoke.aiflow.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ninano.karaoke.aiflow.R;
import com.ninano.karaoke.aiflow.RsvSingleton;
import com.ninano.karaoke.aiflow.base.data.Song;

public class CustomDialog {

    private Context mContext;
    private Song mItem;
    public CustomDialog(Context mContext) {
        this.mContext = mContext;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction( Song _item) {
        this.mItem = _item;
        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(mContext);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.custom_dialog);

        // 커스텀 다이얼로그를 노출한다.


        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final TextView tvSongName = (TextView) dlg.findViewById(R.id.tvSongName);
        final TextView tvArtistName = (TextView) dlg.findViewById(R.id.tvArtistName);
        final TextView tvDateName = (TextView) dlg.findViewById(R.id.tvDateName);

        if(mItem != null){
            tvSongName.setText(mItem.getTitle());
            tvArtistName.setText(mItem.getSinger());
            tvDateName.setText(
            String.format(
                    mContext.getResources().getString(R.string.adapter_item_song_content_date_format),
                    mItem.getCreated()));
        }



        final ImageButton okButton = (ImageButton) dlg.findViewById(R.id.okButton);
        final ImageButton cancelButton = (ImageButton) dlg.findViewById(R.id.cancelButton);

        dlg.show();
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
                // 커스텀 다이얼로그에서 입력한 메시지를 대입한다.

                ViewUtils.showToast(mContext, "予約しました。", R.layout.activity_ninano_main);

                RsvSingleton.getInstance().Reserv( mItem );


                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ViewUtils.showToast(mContext, "취소 했습니다. ", R.layout.activity_ninano_main);
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
    }
}