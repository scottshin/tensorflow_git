package com.ninano.karaoke.aiflow.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.ninano.karaoke.aiflow.R;
import com.ninano.karaoke.aiflow.base.data.Song;

public class ReserveDialog {

    private Context mContext;
    private Song mItem;
    public ReserveDialog(Context mContext) {
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
        dlg.setContentView(R.layout.dialog_reserv);
        dlg.show();

        dlg.setCancelable(true);

    }
}