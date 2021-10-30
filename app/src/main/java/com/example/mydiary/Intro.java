package com.example.mydiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Intro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ViewGroup viewGroup=(ViewGroup) findViewById(R.id.group);



        for(int i=0;i<viewGroup.getChildCount();i++)
        {
            Log.d("effect",Integer.toString(i));
            ImageView effect=(ImageView) viewGroup.getChildAt(i);
            ObjectAnimator animation = ObjectAnimator.ofFloat(effect, "translationY", 20f*i);
            animation.setDuration(2000);
            animation.start();
        }

    }
}