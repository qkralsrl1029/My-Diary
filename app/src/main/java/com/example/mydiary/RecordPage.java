package com.example.mydiary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.File;
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
    Uri imageUri;

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
            int startIndex = selectedImageUri.toString().indexOf("jpeg");
            imgName.setText(selectedImageUri.toString().substring(startIndex));
            imageUri=selectedImageUri;
        }
    }


    //frame layout으로 먼저 구글맵 띄우고 설정 위치로 카메라 고정 후 그 위에 이미지 뷰를 겹쳐서 지도 위에 이미지가 있는것처럼 구성
    //이미지를 버튼으로 만들어서(디폴트는 그냥 구글맵 이미지 캡쳐본) 선택 완료하면 마커캡쳐이미지로 변경, 그 위에 사용자 업로드 이미지 setvisible true


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
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 17));
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
        String imagePath=getRealPathFromURI(getApplicationContext(),imageUri);  //이미지 절대 경로 저장

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PRIMARY_KEY, date);
        values.put(DatabaseHelper.POSITION_X, position.longitude);
        values.put(DatabaseHelper.POSITION_Y, position.latitude);
        values.put(DatabaseHelper.IS_HAPPY, isHappy);
        values.put(DatabaseHelper.IS_BORING, isBoring);
        values.put(DatabaseHelper.IS_LOVED, isLoved);
        values.put(DatabaseHelper.IS_SURPRISED, isSurprised);
        values.put(DatabaseHelper.IS_SAD, isSad);
        values.put(DatabaseHelper.IMAGE,imagePath);
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

    //to get real path of image
    //used in diary view dialog
    public static String getRealPathFromURI(final Context context, final Uri uri) {

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                } else {
                    String SDcardpath = getRemovableSDCardPath(context).split("/Android")[0];
                    return SDcardpath +"/"+ split[1];
                }
            }

            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }

            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    public static String getRemovableSDCardPath(Context context) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);
        if (storages.length > 1 && storages[0] != null && storages[1] != null)
            return storages[1].toString();
        else
            return "";
    }


    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }


    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }
}

