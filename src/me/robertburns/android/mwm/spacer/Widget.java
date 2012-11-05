package me.robertburns.android.mwm.spacer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class Widget extends BroadcastReceiver {

    public static final String TAG = "Spacer-MWM";
    final static String widget_description_prefix = "Spacer ";

    public static ArrayList<String> widgets_desired = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive() " + action);

        if (action != null && action.equals("org.metawatch.manager.REFRESH_WIDGET_REQUEST")) {
            Bundle bundle = intent.getExtras();

            boolean getPreviews = bundle.containsKey("org.metawatch.manager.get_previews");
            if (getPreviews)
                Log.d(TAG, "get_previews");

            ArrayList<String> widgets_desired = null;

            if (bundle.containsKey("org.metawatch.manager.widgets_desired")) {
                Log.d(TAG, "widgets_desired");
                widgets_desired = new ArrayList<String>(Arrays.asList(bundle.getStringArray("org.metawatch.manager.widgets_desired")));
            }
            update(context);
        }
    }

    private static void genSpace(Context context, int[] n, int k) {
        if (widgets_desired != null && !widgets_desired.contains("spacer_" + n[0] + "_" + n[1])) {
            if (k == n.length) {
                genWidget(context, n[0], n[1]);
            } else {
                for (int i = k; i < n.length; i++) {
                    int temp = n[k];
                    n[k]=n[i];
                    n[i]=temp;
                    genSpace(context,n,k+1);
                    temp=n[k];
                    n[k]=n[i];
                    n[i]=temp;
                }
            }
        }
    }

    private synchronized static void genWidget(Context context, int width, int height) {

        Log.d(TAG, "genWidget: spacer_" + width + "_" + height);
        widgets_desired.add("spacer_" + width + "_" + height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        Intent i = createUpdateIntent(bitmap, "spacer_" + width + "_" + height, widget_description_prefix + "(" + width + "x" + height + ")", 1);
        context.sendBroadcast(i);
    }
    
    public static void update(Context context) {
        Log.d(TAG, "update");
        widgets_desired = new ArrayList<String>(Arrays.asList(""));
        int n[] = {8,16,24,32,48,96};
        for (int i = 0; i < n.length; i++) {
            genWidget(context, n[i], n[i]);
        }
        genSpace(context, n, 0);
    }

    private static Intent createUpdateIntent(Bitmap bitmap, String id, String description, int priority) {
        int pixelArray[] = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixelArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        Intent intent = new Intent("org.metawatch.manager.WIDGET_UPDATE");
        Bundle b = new Bundle();
        b.putString("id", id);
        b.putString("desc", description);
        b.putInt("width", bitmap.getWidth());
        b.putInt("height", bitmap.getHeight());
        b.putInt("priority", priority);
        b.putIntArray("array", pixelArray);
        intent.putExtras(b);

        return intent;
    }

}
