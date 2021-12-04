package com.example.mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class RecordPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recod_page);
    }
    
    //frame layout으로 먼저 구글맵 띄우고 설정 위치로 카메라 고정 후 그 위에 이미지 뷰를 겹쳐서 지도 위에 이미지가 있는것처럼 구성
    //이미지를 버튼으로 만들어서(디폴트는 그냥 구글맵 이미지 캡쳐본) 선택 완료하면 마커캡쳐이미지로 변경, 그 위에 사용자 업로드 이미지 setvisible true


    
}

