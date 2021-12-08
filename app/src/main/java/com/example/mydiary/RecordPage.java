package com.example.mydiary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class RecordPage extends AppCompatActivity {

    Button imageLoadBtn;
    TextView imgName;
    Button saveBtn;
    EditText recordText;
    Button emtHappy;
    Button emtSad;
    Button emtBoring;
    Button emtSurprised;
    Button emtLoved;

    GoogleMap mMap;
    SupportMapFragment mapFragment;
    boolean check=false;
    boolean initMap=false;
    MarkerOptions markerOptions;

    int isHappy=0;
    int isSad=0;
    int isBoring=0;
    int isSurprised=0;
    int isLoved=0;

    DatabaseHelper dbHelper;
    SQLiteDatabase sqLiteDb;

    private final int GET_GALLERY_IMAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recod_page);

        imageLoadBtn=findViewById(R.id.imgLoadBtn);
        imgName=findViewById(R.id.imgName);
        saveBtn=findViewById(R.id.saveBtn);
        recordText=findViewById(R.id.recoedText);
        emtHappy=findViewById(R.id.btnSmile);
        emtSad=findViewById(R.id.btnSad);
        emtBoring=findViewById(R.id.btnBoring);
        emtSurprised=findViewById(R.id.btnSurprised);
        emtLoved=findViewById(R.id.btnLoved);
        markerOptions=new MarkerOptions();

        dbHelper=new DatabaseHelper(getApplicationContext());
        sqLiteDb=dbHelper.getWritableDatabase();

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                showCurrentPosition(imageLoadBtn);

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        mMap.clear();

                        //when map clicked
                        markerOptions=new MarkerOptions();
                        //set marker position
                        markerOptions.position(latLng);
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                        //set marker title
                        markerOptions.title("이 장소에서 있었던 일을 오늘 일기에 추가할까요?");
                        mMap.addMarker(markerOptions);
                    }
                });
            }
        });

        imageLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent,GET_GALLERY_IMAGE);
            }
        });

        emtHappy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isHappy==1){
                    emtHappy.setBackgroundResource(R.drawable.img_happy);
                    isHappy=0;
                }
                else{
                    emtHappy.setBackgroundResource(R.drawable.img_happy_selected);
                    isHappy=1;
                }
            }
        });

        emtSad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSad==1){
                    emtSad.setBackgroundResource(R.drawable.img_sad);
                    isSad=0;
                }
                else{
                    emtSad.setBackgroundResource(R.drawable.img_sad_selected);
                    isSad=1;
                }
            }
        });

        emtBoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBoring==1){
                    emtBoring.setBackgroundResource(R.drawable.img_boring);
                    isBoring=0;
                }
                else{
                    emtBoring.setBackgroundResource(R.drawable.img_boring_selected);
                    isBoring=1;
                }
            }
        });

        emtSurprised.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSurprised==1){
                    emtSurprised.setBackgroundResource(R.drawable.img_surprised);
                    isSurprised=0;
                }
                else{
                    emtSurprised.setBackgroundResource(R.drawable.img_surprised_selected);
                    isSurprised=1;
                }
            }
        });

        emtLoved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLoved==1){
                    emtLoved.setBackgroundResource(R.drawable.img_loved);
                    isLoved=0;
                }
                else{
                    emtLoved.setBackgroundResource(R.drawable.img_loved_selected);
                    isLoved=1;
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FancyToast.makeText(getApplicationContext(),"저장 완료!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false);
                saveData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            //imageview.setImageURI(selectedImageUri);
            imgName.setText(selectedImageUri.toString());
        }
    }


    //frame layout으로 먼저 구글맵 띄우고 설정 위치로 카메라 고정 후 그 위에 이미지 뷰를 겹쳐서 지도 위에 이미지가 있는것처럼 구성
    //이미지를 버튼으로 만들어서(디폴트는 그냥 구글맵 이미지 캡쳐본) 선택 완료하면 마커캡쳐이미지로 변경, 그 위에 사용자 업로드 이미지 setvisible true

    public void setDefaultLocation() {
        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);
    }

    public void showCurrentPosition(View view){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try{

            GPSListener gpsListener = new GPSListener();
            long minTime = 1000;
            float minDistance = 0;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            check=true;

        }catch(SecurityException e){
            e.printStackTrace();
        }
    }


    class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            showCurrentLocation(latitude, longitude);

        }
    }

    private void showCurrentLocation(double latitude, double longitude){
        LatLng curPoint = new LatLng(latitude, longitude);
        if(!initMap){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
            initMap=true;
        }
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            FancyToast.makeText(getApplicationContext(), "Permission Granted",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            FancyToast.makeText(getApplicationContext(), "Permission Denied",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
        }
    };

    private void saveData(){
        LatLng position=markerOptions.getPosition();
        String text=recordText.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(Calendar.getInstance().getTime());

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PRIMARY_KEY, date);
        values.put(DatabaseHelper.POSITION_X, position.longitude);
        values.put(DatabaseHelper.POSITION_Y, position.latitude);
        values.put(DatabaseHelper.IS_HAPPY, isHappy);
        values.put(DatabaseHelper.IS_BORING, isBoring);
        values.put(DatabaseHelper.IS_LOVED, isLoved);
        values.put(DatabaseHelper.IS_SURPRISED, isSurprised);
        values.put(DatabaseHelper.IS_SAD, isSad);
        values.put(DatabaseHelper.BODY, text);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = sqLiteDb.insert(DatabaseHelper.TABLE_NAME, null, values);
        if(newRowId==-1)
            Log.e("DB Error","data insertion error");
        else
            Log.d("getDay",date);

        FancyToast.makeText(getApplicationContext(), "작성 완료 !",FancyToast.LENGTH_LONG,FancyToast.DEFAULT,false).show();
        finish();
    }
}

