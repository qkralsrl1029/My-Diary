package com.example.mydiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.animation.ObjectAnimator;
import android.content.Intent;
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

        View imageView=findViewById(R.id.background);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intro.this,MainActivity.class);
                startActivity(intent);
            }
        });

        for(int i=0;i<viewGroup.getChildCount();i++)
        {
            ImageView effect=(ImageView) viewGroup.getChildAt(i);
            ObjectAnimator animation = ObjectAnimator.ofFloat(effect, "translationY", 20f*i);
            animation.setDuration(2000);
            animation.start();
        }

    }
}