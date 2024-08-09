package com.ninano.karaoke.aiflow.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ninano.karaoke.aiflow.DemoMainActivity;
import com.ninano.karaoke.aiflow.R;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.ninano.karaoke.aiflow.base.BaseActivity.ACTIVITY_QUEUE;

public class ViewUtils {

    public static void showToast(Context context, String content, int rootId){
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) ((Activity) context).findViewById(rootId));
        TextView tv = (TextView) layout.findViewById(R.id.txtvw);
        tv.setText(content);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }


    public static void cloudToast(Context context,  String title, String content,  int rootId){
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.cloud_toast, (ViewGroup) ((Activity) context).findViewById(rootId));

        TextView tvTitle = (TextView) layout.findViewById(R.id.txtCmd);
        tvTitle.setText( title );

        TextView tv = (TextView) layout.findViewById(R.id.txtvw);
        tv.setText(content);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.TOP, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }



    public static void onGotoRootActivity(Activity mainActivity, Activity targetActivity){
        ActivityManager mngr = (ActivityManager) mainActivity.getSystemService( ACTIVITY_SERVICE );
        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(50);

        if(taskList.size() > 1) {
            Log.i("ViewUtils", "This is last activity in the stack");

            for(int i = 0; i< taskList.size(); i++){

            }
        }else{
            if(taskList.size() == 1){
                targetActivity.finish();
            }
        }
    }

    public static void returnRootActivity(){
        try {
            if (ACTIVITY_QUEUE.size() > 0) {
                for (int i = 0; i < ACTIVITY_QUEUE.size(); i++) {
                    ACTIVITY_QUEUE.get(i).finish();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String onTopActivity(Activity activity){
        ActivityManager am = (ActivityManager) activity.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        return taskInfo.get(0).topActivity.getClassName();
        //        ComponentName componentInfo = taskInfo.get(0).topActivity;
        //        componentInfo.getPackageName();
    }
}
