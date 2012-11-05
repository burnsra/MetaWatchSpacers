package me.robertburns.android.mwm.spacer;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import me.robertburns.android.mwm.spacer.R;

public class Spacer extends Activity {
    
    public static final String TAG = "Spacer-MWM";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this;
        
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);
        TextView txtContent = (TextView) findViewById(R.id.txtWelcome);

        AssetManager assetManager = getAssets();
        InputStream input;
        try {
            input = assetManager.open("welcome.txt");

            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            String text = new String(buffer);

            txtContent.setText(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Widget.update(context);
    }

}
