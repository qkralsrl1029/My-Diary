package com.example.mydiary;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import android.content.ContentResolver;
import java.net.URI;
import java.util.Date;


public class ViewFragment extends Fragment {

    MaterialCalendarView calendarView;
    ImageView diaryImage;

    DatabaseHelper dbHelper;
    SQLiteDatabase sqLiteDb;
    Cursor cursor;

    GoogleMap mMap;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermission();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.fragment_view, container, false);

        calendarView=rootView.findViewById(R.id.calendar);
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                String targetDate = null;
                if((date.getMonth()+1)/10==0){
                    targetDate = date.getYear()+"-0"+(date.getMonth()+1)+"-"+date.getDay();}
                else
                    targetDate = date.getYear()+"-0"+(date.getMonth())+"-"+date.getDay();
                Log.d("getDay",targetDate);
                String queryDistanceSum="SELECT * FROM "+DatabaseHelper.TABLE_NAME+" WHERE "+DatabaseHelper.PRIMARY_KEY+" = '"+targetDate+"';";
                cursor = sqLiteDb.rawQuery(queryDistanceSum,null);

                if(cursor.getCount()==0){
                    FancyToast.makeText(getActivity(), "이 날에 기록된 일기가 없네요 !",FancyToast.LENGTH_LONG,FancyToast.DEFAULT,false).show();
                    return;
                }

                cursor.moveToFirst();
                String title=cursor.getString(0);
                Double longitude= cursor.getDouble(1);
                Double latitude= cursor.getDouble(2);
                int checkHappy=cursor.getInt(3);
                int checkSad=cursor.getInt(4);
                int checkBoring=cursor.getInt(5);
                int checkSurprised=cursor.getInt(6);
                int checkLoved=cursor.getInt(7);
                String path=cursor.getString(8);
                String body=cursor.getString(9);

                Dialog dailyDiary=new Dialog(getActivity());
                dailyDiary.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dailyDiary.setContentView(R.layout.dialog_diary);
                dailyDiary.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView diaryTitle=dailyDiary.findViewById(R.id.diaryTitle);
                diaryImage=dailyDiary.findViewById(R.id.dialogImage);
                ImageView diaryHappy=dailyDiary.findViewById(R.id.img1);
                ImageView diarySad=dailyDiary.findViewById(R.id.img2);
                ImageView diaryBoring=dailyDiary.findViewById(R.id.img3);
                ImageView diarySurprised=dailyDiary.findViewById(R.id.img4);
                ImageView diaryLoved=dailyDiary.findViewById(R.id.img5);
                TextView diaryBody=dailyDiary.findViewById(R.id.diaryBody);

                diaryTitle.setText(title);

                //setImage(Uri.parse(path));
                Log.d("image path",Uri.parse(path).toString());
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
                                             mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 17));
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

    private void setImage(Uri uri) {
        try{
            InputStream in =getActivity().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            diaryImage.setImageBitmap(bitmap);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission(){
        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
    }
}