package com.example.bbbb.healthmanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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

import org.tensorflow.lite.Interpreter;

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

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;

    private String userID = "";
    private String userEmail = "";
    private String userName = "";

    private LineChart lineChart;

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


        // 네비케이션바
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
        mDatabase = database.getReference().child("users").child(userID).child("userName");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userNameTV.setText(dataSnapshot.getValue().toString());
                userName = (String) userNameTV.getText();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
