package com.y.bigpic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.y.lib.bigpic.BigPic;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TestProguard tp = new TestProguard();
        tp.show();

        BigPic bigPicView = findViewById(R.id.bigpic);
        InputStream is = null;
        try {
            is = getAssets().open("map.jpg");
            bigPicView.setPic(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
