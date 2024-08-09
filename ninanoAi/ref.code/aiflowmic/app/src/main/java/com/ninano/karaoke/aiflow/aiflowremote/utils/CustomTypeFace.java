package com.ninano.karaoke.aiflow.aiflowremote.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public class CustomTypeFace {
//    private Typeface typeface = null;

    public static final String DEMILIGTH = "DEMILIGTH";
    public static final String MEDIUM = "MEDIUM";
    public static final Map<String, String> FONT_STYLE_LIST =  new HashMap<String, String>(){
        {
            put("DEMILIGTH","fonts/NotoSansCJKjp-DemiLight.otf");
            put("MEDIUM","fonts/NotoSansCJKjp-Medium.otf");
        }
    };

    public static Typeface setFont(Context context, String fontType){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),
                FONT_STYLE_LIST.get(fontType));

        return typeface;
    }
}
