package com.example.bbbb.healthmanager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.HorizontalViewPortHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;
import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int MY_PERMISSION_STORAGE = 1111;
    private static final int MY_PERMISSION_CAMERA = 2222;
    private static final int REQUEST_TAKE_PHOTO = 3333;
    private static final int REQUEST_TAKE_ALBUM = 4444;
    private static final int REQUEST_IMAGE_CROP = 5555;

    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat mFormatHome = new SimpleDateFormat("MMM dd일 EEE요일", Locale.KOREAN);

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;

    private String userID = "";
    private String userEmail = "";
    private String userName = "";

    private ImageButton btnPhoto;
    private String mCurrentPhotoPath;
    private Uri imageUri;
    private Uri photoURI, albumURI;

    private TextView tvUserName, tvDateOfBirth, tvStatus, tvPredDate;

    private LineChart lineChart;

    private TextView textViewDate;
    private TextView BPTextView1, BPTextView2, BPTextView3;
    private TextView BPTimeTextView1, BPTimeTextView2, BPTimeTextView3;

    private Fragment addBPFragment = null;
    private Bundle bundle;

    //사용자정보
    private int mYear, mMonth, mDay = 0;
    private boolean manPressed, womanPressed, smokeTF, familyTF;
    private int mKey, mWeight, mExer;
    private float mPressure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent loadingIntent = new Intent(this, LoadingActivity.class);
        startActivity(loadingIntent);

        lineChart = (LineChart) findViewById(R.id.chart);



        // 탭 아이콘 지정
        TabHost host = findViewById(R.id.host);
        host.setup();

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("tab1")) {
                }

            }
        });

        TabHost.TabSpec spec = host.newTabSpec("tab1");
        spec.setContent(R.id.tab_content1);
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.tab_home, null));
        host.addTab(spec);

        spec = host.newTabSpec("tab2");
        spec.setContent(R.id.tab_content2);
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.tab_calendar, null));
        host.addTab(spec);

        spec = host.newTabSpec("tab3");
        spec.setContent(R.id.tab_content3);
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.tab_predict, null));
        host.addTab(spec);

        spec = host.newTabSpec("tab4");
        spec.setContent(R.id.tab_content4);
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.tab_news, null));
        host.addTab(spec);


        // 툴바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        // 네비게이션바
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final Intent myIntent = getIntent();

        View nav_header_view = navigationView.getHeaderView(0);

        // User information setting
        final TextView userNameTV = nav_header_view.findViewById(R.id.userName);
        TextView userEmailTV = nav_header_view.findViewById(R.id.userEmail);

        userEmailTV.setText(myIntent.getStringExtra("userEmail"));
        userEmail = (String) userEmailTV.getText();

        String split[] = userEmail.split("@");
        userID = split[0];

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();


        // tab1 - set profile
        btnPhoto = findViewById(R.id.button_user_photo);
        tvUserName = findViewById(R.id.tv_profile_name);
        tvDateOfBirth = findViewById(R.id.tv_profile_dob);
        tvStatus = findViewById(R.id.tv_profile_status);
        tvPredDate = findViewById(R.id.tv_profile_pred_date);

        DatabaseReference userNameDatabase = mDatabase.child("users").child(userID);

        userNameDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals("userBirth")) {
                        tvDateOfBirth.setText(snapshot.getValue().toString());
                    } else if (snapshot.getKey().equals("userName")) {
                        tvUserName.setText(snapshot.getValue().toString());
                        userNameTV.setText(snapshot.getValue().toString());
                    }
                }
                userNameTV.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        // tab2 - graph
        TextView textViewHomeDate = findViewById(R.id.tv_home_date);
        textViewHomeDate.setText(getTime1());



        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 1));
        entries.add(new Entry(2, 2));
        entries.add(new Entry(3, 0));
        entries.add(new Entry(4, 4));
        entries.add(new Entry(5 ,1));
        entries.add(new Entry(6, 2));
        entries.add(new Entry(7, 4));
        entries.add(new Entry(8, 8));
        entries.add(new Entry(9, 1));
        entries.add(new Entry(10, 2));
        entries.add(new Entry(11, 0));
        entries.add(new Entry(12, 4));
        entries.add(new Entry(13 ,1));
        entries.add(new Entry(14, 2));
        entries.add(new Entry(15, 4));
        entries.add(new Entry(16, 8));

        LineDataSet lineDataSet = new LineDataSet(entries, "속성명1");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setCircleHoleColor(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        lineChart.setDragEnabled(true);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        lineChart.animateY(2000, Easing.EaseInCubic);
        lineChart.invalidate();



        // tab2 - setBP
        textViewDate = findViewById(R.id.curr_date);
        textViewDate.setText(getTime2());

        BPTextView1 = findViewById(R.id.tv_m_bp);
        BPTextView2 = findViewById(R.id.tv_a_bp);
        BPTextView3 = findViewById(R.id.tv_e_bp);

        BPTimeTextView1 = findViewById(R.id.tv_m_time);
        BPTimeTextView2 = findViewById(R.id.tv_a_time);
        BPTimeTextView3 = findViewById(R.id.tv_e_time);

        DatabaseReference bpDataBase = mDatabase.child("users").child(userID).child("bloodPressure");
        setBPDataBase(bpDataBase);

    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseReference bpDataBase = database.getReference().child("users").child(userID).child("bloodPressure");
        setBPDataBase(bpDataBase);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.my_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("signOut", true);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String getTime1() {
        long mNow;
        Date mDate;

        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);

        return mFormatHome.format(mDate);
    }

    private String getTime2() {
        long mNow;
        Date mDate;

        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);

        return mFormat.format(mDate);
    }

    private void fragmentSetting() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.tab_content2, addBPFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void captureCamera() {
        String state = Environment.getExternalStorageState();
        // 외장 메모리 검사
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.e("captureCamera Error", ex.toString());
                }
                if (photoFile != null) {
                    Uri providerURI = FileProvider.getUriForFile(this, getPackageName()+".fileprovider", photoFile);
                    imageUri = providerURI;

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "hamer");

        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void getAlbum() {
        Log.i("getAlbum", "Call");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    private void galleryAddPic() {
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    public void cropImage() {
        Log.i("cropImage", "Call");
        Log.i("cropImage", "photoURI : " +photoURI + " / albumURI : "+albumURI);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", albumURI);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if(resultCode == Activity.RESULT_OK) {
                    try{
                        Log.i("REQUEST_TAKE_PHOTO", "OK");
                        galleryAddPic();

                        btnPhoto.setImageURI(imageUri);
                    } catch (Exception e){
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText((MainActivity.this), "사진찍기를 취소하였습니다", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_TAKE_ALBUM:
                if(resultCode == Activity.RESULT_OK) {
                    if(data.getData() != null){
                        try{
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                            cropImage();
                        } catch(Exception e){
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;
            case REQUEST_IMAGE_CROP:
                if(resultCode == Activity.RESULT_OK){
                    galleryAddPic();
                    btnPhoto.setImageURI(albumURI);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_STORAGE:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] < 0) {
                        Toast.makeText(MainActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 허용했다면 이 부분에서..

                break;
        }
    }

    public void onImageButtonClicekd(View view) {
        switch (view.getId()) {
            case R.id.button_user_photo:
                CameraDialog cameraDialog = new CameraDialog(MainActivity.this);
                cameraDialog.setDialogListener(new CameraDialog.CustomDialogListener() {
                    @Override
                    public void onCaptureClicked() {
                        captureCamera();
                    }

                    @Override
                    public void onAlbumClicked() {
                        getAlbum();
                    }
                });
                cameraDialog.show();

                checkPermission();
                break;
            case R.id.button_refesh:
                textViewDate.setText(getTime2());
                break;
            case R.id.button_m_bp:
                addBPFragment = new AddBloodPressureFragment();
                bundle = new Bundle();
                bundle.putString("userID", userID);
                bundle.putString("buttonStatus", "mbp");
                addBPFragment.setArguments(bundle);
                fragmentSetting();
                break;
            case R.id.button_a_bp:
                addBPFragment = new AddBloodPressureFragment();
                bundle = new Bundle();
                bundle.putString("userID", userID);
                bundle.putString("buttonStatus", "abp");
                addBPFragment.setArguments(bundle);
                fragmentSetting();
                break;
            case R.id.button_e_bp:
                addBPFragment = new AddBloodPressureFragment();
                bundle = new Bundle();
                bundle.putString("userID", userID);
                bundle.putString("buttonStatus", "ebp");
                addBPFragment.setArguments(bundle);
                fragmentSetting();
                break;
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final AlertDialog permissionDialog = builder.create();
                permissionDialog.setTitle("알림");
                permissionDialog.setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.");
                permissionDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                });
                permissionDialog.setButton(DialogInterface.BUTTON_POSITIVE, "확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permissionDialog.dismiss();
                    }
                });
                permissionDialog.setCancelable(false);

                permissionDialog.show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}
                        , MY_PERMISSION_STORAGE);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final AlertDialog permissionDialog = builder.create();
                permissionDialog.setTitle("알림");
                permissionDialog.setMessage("카메라 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.");
                permissionDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                });
                permissionDialog.setButton(DialogInterface.BUTTON_POSITIVE, "확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permissionDialog.dismiss();
                    }
                });
                permissionDialog.setCancelable(false);

                permissionDialog.show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }

    private void setBPDataBase(DatabaseReference bpDataBase) {
        bpDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String split[] = getTime2().split(" ");
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    if (snapshot1.getKey().equals(split[0])) {
                        for (DataSnapshot snapshot2 : dataSnapshot.child(split[0]).getChildren()) {
                            String when = snapshot2.getKey();
                            String bp[] = new String[3];
                            int bpIdx = 0;

                            for (DataSnapshot snapshot3 : dataSnapshot.child(split[0]).child(when).getChildren()) {
                                bp[bpIdx++] = snapshot3.getValue().toString();
                            }

                            if (when.equals("morning")) {
                                BPTimeTextView1.setText(" (" + bp[2] + ") ");
                                BPTextView1.setText(bp[1] + " / " + bp[0]);
                            } else if (when.equals("afternoon")) {
                                BPTimeTextView2.setText(" (" + bp[2] + ") ");
                                BPTextView2.setText(bp[1] + " / " + bp[0]);
                            } else if (when.equals("evening")) {
                                BPTimeTextView3.setText(" (" + bp[2] + ") ");
                                BPTextView3.setText(bp[1] + " / " + bp[0]);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //생년월일 timepicker
    public boolean onclickeddatebutton(View view) {
        // Handle navigation view item clicks here.
        int id = view.getId();

        if (id == R.id.dateButton) {
            Calendar c = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    try {
                        mYear = year;
                        mMonth = monthOfYear + 1;
                        mDay = dayOfMonth;
                        updateEditText();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            datePickerDialog.show();
        }
        return true;
    }

    protected void updateEditText() {
        StringBuffer sb = new StringBuffer();
        TextView textView = findViewById(R.id.dateButton);
        textView.setText(sb.append(mYear + "/").append(mMonth + "/").append(mDay));

    }

    //유무 선택버튼
    public boolean onClickbutton(View view) {
        int id = view.getId();
        TextView manView = findViewById(R.id.manButton);
        TextView womanView = findViewById(R.id.womanButton);
        TextView smokeT = findViewById(R.id.smokeTButton);
        TextView smokeF = findViewById(R.id.smokeFButton);
        TextView familyT = findViewById(R.id.familyTButton);
        TextView familyF = findViewById(R.id.familyFButton);

        if (id == R.id.manButton) {
            womanPressed=false;
            manView.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_selected_background));
            womanView.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_background));
            manPressed = true;
        }
        else if (id == R.id.womanButton){
            manPressed = false;
            womanView.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_selected_background));
            manView.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_background));
            womanPressed = true;
        }
        else if (id == R.id.smokeTButton){
            smokeTF = true;
            smokeT.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_selected_background));
            smokeF.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_background));
        }
        else if (id == R.id.smokeFButton){
            smokeTF = false;
            smokeF.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_selected_background));
            smokeT.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_background));
        }else if (id == R.id.familyTButton){
            familyTF = true;
            familyT.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_selected_background));
            familyF.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_background));
        }
        else if (id == R.id.familyFButton){
            familyTF = true;
            familyF.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_selected_background));
            familyT.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_background));
        }
        return true;
    }

    //예측하기 버튼
    public boolean onclickedPredbutton(View view){
        int id = view.getId();
        if (id == R.id.predButton){
            EditText keyView = (EditText)findViewById(R.id.statureButton);
            EditText weightView = (EditText)findViewById(R.id.weightButton);
            EditText exerView = (EditText)findViewById(R.id.walkButton);
            EditText pressView = (EditText)findViewById(R.id.pressureButton);

            mKey = Integer.parseInt(keyView.getText().toString());
            mWeight = Integer.parseInt(weightView.getText().toString());
            mExer = Integer.parseInt(exerView.getText().toString());
            mPressure = Integer.parseInt(pressView.getText().toString());

            //이 입력은 예시로 넣은거
            float[] inputs = new float[]{80, 71, 98};
            //사용자 data 입력
            //float[] inputs = new float[]{mPressure, (float)mWeight/(float)mKey, mExer};
            //예측값 변수 초기화
            float[] output = new float[]{0};

            Interpreter tflite = getTfliteInterpreter("regression_model.tflite");
            tflite.run(inputs, output);

            TextView pred = findViewById(R.id.bigButton);
            pred.setText("6개월 후 혈압수치\n"+String.valueOf(output[0]));
            //사용자 입력값 잘 받아오는지 확인하는 용도
            //pred.setText(String.valueOf(mPressure)+" "+String.valueOf(mKey)+" "+String.valueOf(mKey)+" "+String.valueOf(mExer));

            final ScrollView scrollView = findViewById(R.id.tab_content3);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            });
        }
        return true;
    }

    //tensorflow 사용에 필요한 것
    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(MainActivity.this, modelPath));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

}
