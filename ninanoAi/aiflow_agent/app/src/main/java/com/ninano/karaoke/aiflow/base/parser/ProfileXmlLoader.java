package com.ninano.karaoke.aiflow.base.parser;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ninano.karaoke.aiflow.R;
import com.ninano.karaoke.aiflow.base.data.Song;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;


public class ProfileXmlLoader {

    private Context mContext = null;
    private static final String TAG = ProfileXmlLoader.class.getName();

    public ProfileXmlLoader(Context _context){
        mContext = _context;
    }

    public Callable<String> getArtistImgUrl(String keyword, String type) {
        return new Callable<String>() {
            @Override
            public String call() {
                return loader(keyword, type);
            }
        };
    }

    public String loader(String keyword, String type){
      String result = "";
      boolean isProfile = false;
      String repaceKeyword = keyword;
      try {
          if(keyword.indexOf("(") > -1) {
              repaceKeyword = keyword.substring(1, keyword.indexOf("("));
          }else{
              repaceKeyword = keyword;
          }
      }catch (Exception e){
          e.printStackTrace();
          repaceKeyword = keyword;
      }

      Log.e(TAG, "XML KEYWORD [" + repaceKeyword + "]");
      try{
            String xmlUrl = String.format(mContext.getString(R.string.search_xml_parser_url),
                    URLEncoder.encode(repaceKeyword, "utf-8"), type);

            Log.e(TAG, "XML URL ["+xmlUrl+"]");
            //검색 URL부분
            URL url = new URL(xmlUrl);

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();
            Log.d(TAG, "파싱시작합니다.");

            while (parserEvent != XmlPullParser.END_DOCUMENT){
                switch(parserEvent){
                    case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
//                        Log.d(TAG, "============= START_TAG");
                        if(parser.getName().equals("image")){ //image 만나면 내용을 받을수 있게 하자
                            Log.d(TAG, "==== IMG");
                            isProfile = true;
                        }
                        break;

                    case XmlPullParser.TEXT://parser가 내용에 접근했을때
//                        Log.d(TAG, "============= TEXT");
                        if(isProfile){ //isProfile true일 때 태그의 내용을 저장.
                            result = parser.getText();
                            isProfile=false;
                        }
                        break;

                    case XmlPullParser.END_TAG:
//                        Log.d(TAG, "============= END_TAG");
                        break;
                }

                if(!TextUtils.isEmpty(result)){
                    break;
                }
                parserEvent = parser.next();
            }
        } catch(Exception e){
            Log.e(TAG,"XML PARSER :: 에러가..났습니다...");
        }
        Log.d(TAG, "ARTIST URL : ["+result+"]");
        return result;
    }
}
