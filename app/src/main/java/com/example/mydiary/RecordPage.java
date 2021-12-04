package com.example.mydiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.List;

public class RecordPage extends AppCompatActivity {

    Button imageLoadBtn;
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

    boolean isHappy=false;
    boolean isSad=false;
    boolean isBoring=false;
    boolean isSurprised=false;
    boolean isLoved=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recod_page);

        imageLoadBtn=findViewById(R.id.imgLoadBtn);
        saveBtn=findViewById(R.id.saveBtn);
        recordText=findViewById(R.id.recoedText);
        emtHappy=findViewById(R.id.btnSmile);
        emtSad=findViewById(R.id.btnSad);
        emtBoring=findViewById(R.id.btnBoring);
        emtSurprised=findViewById(R.id.btnSurprised);
        emtLoved=findViewById(R.id.btnLoved);

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

        emtHappy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isHappy){
                    emtHappy.setBackgroundResource(R.drawable.img_happy);
                    isHappy=false;
                }
                else{
                    emtHappy.setBackgroundResource(R.drawable.img_happy_selected);
                    isHappy=true;
                }
            }
        });

        emtSad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSad){
                    emtSad.setBackgroundResource(R.drawable.img_sad);
                    isSad=false;
                }
                else{
                    emtSad.setBackgroundResource(R.drawable.img_sad_selected);
                    isSad=true;
                }
            }
        });

        emtBoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBoring){
                    emtBoring.setBackgroundResource(R.drawable.img_boring);
                    isBoring=false;
                }
                else{
                    emtBoring.setBackgroundResource(R.drawable.img_boring_selected);
                    isBoring=true;
                }
            }
        });

        emtSurprised.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSurprised){
                    emtSurprised.setBackgroundResource(R.drawable.img_surprised);
                    isSurprised=false;
                }
                else{
                    emtSurprised.setBackgroundResource(R.drawable.img_surprised_selected);
                    isSurprised=true;
                }
            }
        });

        emtLoved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLoved){
                    emtLoved.setBackgroundResource(R.drawable.img_loved);
                    isLoved=false;
                }
                else{
                    emtLoved.setBackgroundResource(R.drawable.img_loved_selected);
                    isLoved=true;
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
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
        boolean flagHappy=isHappy;
        boolean flagSad=isSad;
        boolean flagSurprised=isSurprised;
        boolean flagBoring=isBoring;
        boolean flagLoved=isLoved;
    }
}

