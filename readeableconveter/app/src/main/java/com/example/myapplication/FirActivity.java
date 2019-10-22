package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class FirActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fir);

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep(5000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    Intent mainIntent =new Intent(FirActivity.this,SecActivity.class);
                    startActivity(mainIntent);
                }
            }
        };

        thread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }
}
