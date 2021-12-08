package com.example.mydiary;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Date;


public class ViewFragment extends Fragment {

    MaterialCalendarView calendarView;

    DatabaseHelper dbHelper;
    SQLiteDatabase sqLiteDb;
    Cursor cursor;

    GoogleMap mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.fragment_view, container, false);

        calendarView=rootView.findViewById(R.id.calendar);
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {


                String targetDate = date.getYear()+"-"+(date.getMonth()+1)+"-"+date.getDay();
                Log.d("getDay",targetDate);
                String queryDistanceSum="SELECT * FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.PRIMARY_KEY+" = '"+targetDate+"';";
                cursor = sqLiteDb.rawQuery(queryDistanceSum,null);
                cursor.moveToFirst();

                String title=cursor.getString(0);
                Double longitude= cursor.getDouble(1);
                Double latitude= cursor.getDouble(2);
                int checkHappy=cursor.getInt(3);
                int checkSad=cursor.getInt(4);
                int checkBoring=cursor.getInt(5);
                int checkSurprised=cursor.getInt(6);
                int checkLoved=cursor.getInt(7);
                String body=cursor.getString(8);

                Dialog dailyDiary=new Dialog(getActivity());
                dailyDiary.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dailyDiary.setContentView(R.layout.dialog_diary);
                dailyDiary.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView diaryTitle=dailyDiary.findViewById(R.id.diaryTitle);
                ImageView diaryHappy=dailyDiary.findViewById(R.id.img1);
                ImageView diarySad=dailyDiary.findViewById(R.id.img2);
                ImageView diaryBoring=dailyDiary.findViewById(R.id.img3);
                ImageView diarySurprised=dailyDiary.findViewById(R.id.img4);
                ImageView diaryLoved=dailyDiary.findViewById(R.id.img5);
                TextView diaryBody=dailyDiary.findViewById(R.id.diaryBody);

                diaryTitle.setText(title);
                diaryBody.setText(body);
                if(checkHappy==0)
                    diaryHappy.setVisibility(View.GONE);
                if(checkSad==0)
                    diarySad.setVisibility(View.GONE);
                if(checkBoring==0)
                    diaryBoring.setVisibility(View.GONE);
                if(checkSurprised==0)
                    diarySurprised.setVisibility(View.GONE);
                if(checkLoved==0)
                    diaryLoved.setVisibility(View.GONE);

                SupportMapFragment diaryMap=(SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.diaryMap);
                diaryMap.getMapAsync(new OnMapReadyCallback() {
                                         @Override
                                         public void onMapReady(@NonNull GoogleMap googleMap) {
                                             mMap = googleMap;


                                             MarkerOptions markerOptions = new MarkerOptions();
                                             markerOptions.position(new LatLng(latitude,longitude));
                                             markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                                             markerOptions.title("멋진 하루였네요!");
                                             mMap.addMarker(markerOptions);
                                             mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 15));
                                         }
                                     });



                dailyDiary.show();

                TextView close=dailyDiary.findViewById(R.id.closeBtn);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mMap.clear();
                        dailyDiary.dismiss();
                    }
                });
            }
        });

        dbHelper=new DatabaseHelper(getContext());
        sqLiteDb=dbHelper.getReadableDatabase();

        return rootView;
    }
}