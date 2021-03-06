package com.example.bbbb.healthmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.net.Uri;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Environment;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ImageButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AddBloodPressureFragment.OnApplySelectedListener {
    private static final int MY_PERMISSION_STORAGE = 1111;
    private static final int MY_PERMISSION_CAMERA = 2222;
    private static final int REQUEST_TAKE_PHOTO = 3333;
    private static final int REQUEST_TAKE_ALBUM = 4444;
    private static final int REQUEST_IMAGE_CROP = 5555;

    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat mFormat2 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat mFormatHome = new SimpleDateFormat("MMM dd일 EEE요일", Locale.KOREAN);

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;

    private String userID = "";
    private String userEmail = "";
    private String userName = "";
    private String userBirth = "";

    private TabHost host;

    private ImageButton btnPhoto;
    private String mCurrentPhotoPath;
    private Uri imageUri;
    private Uri photoURI, albumURI;

    private TextView userNameTV;
    private TextView tvUserName, tvDateOfBirth, tvStatus, tvPredDate;
    private TextView tvHomeSys1, tvHomeSys2, tvHomeSys3;
    private TextView tvHomeDia1, tvHomeDia2, tvHomeDia3;

    private LineChart lineChart;

    private TextView textViewDate, datetextView;
    private TextView BPTextView1, BPTextView2, BPTextView3;
    private TextView BPTimeTextView1, BPTimeTextView2, BPTimeTextView3;

    private TextView tvAverSys, tvAverDia;
    private float totalSys, totalDia;

    private Fragment addBPFragment = null;
    private Bundle bundle;

    private List<Entry> SysEntries, DiaEntries;
    private LineDataSet lineDataSet1, lineDataSet2;
    private LineData lineData, lineData2;

    private String currDate;

    //사용자정보
    private int mYear, mMonth, mDay = 0;
    private boolean manPressed, womanPressed, smokeTF, familyTF;
    private int mKey, mWeight, mExer, mHighbp, mLowbp, mAge, gender;
    private float mPressure;

    private int sysCount = 1;
    private int diaCount = 1;


    /*------- 주연 변수 -------*/
    private BackPressCloseHandler backPressCloseHandler;
    //-- crawling --
    private String htmlPageUrl_1 = "http://hqcenter.snu.ac.kr/archives/jiphyunjeon_type/h-disease";
    private String htmlPageUrl_2 = "http://hqcenter.snu.ac.kr/archives/jiphyunjeon_type/h-disease/page/2?issub=yes";
    private String htmlPageUrl_3 = "http://hqcenter.snu.ac.kr/archives/jiphyunjeon_type/h-disease/page/3?issub=yes";

    static List EMPTY = new ArrayList();
    static List LIST_MENU = new ArrayList();
    private Elements titles1, titles2, titles3, body1, body2, body3;
    private Bitmap[] bm1 = new Bitmap[6];
    private Bitmap[] bm2 = new Bitmap[6];
    private Bitmap[] bm3 = new Bitmap[6];

    // Window, Chrome의 User Agent.안드로이드 삼성 브라우저도 Chromium 기반이어서 UserAgent =  Chrome
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";

    //custome listView를 위함
    private ListView listView;
    private ListViewAdapter listViewAdapter;


    private String htmlPageUrl119 = "http://www.dang119.com/shop/board/list.php?id=datac";
    private String ntmlLogin = "https://www.dang119.com:14027/shop/member/login.php?&";
    private ArrayAdapter adapter;
    static List LIST_FAQ = new ArrayList();
    private Elements titleList;
    private ListView listFAQ;
    private Map<String, String> loginCookie;

    //bluetooth
    private ImageButton button_connect_bluetooth;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            backPressCloseHandler.onBackPressed();
        }
    }

    //----------------------


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent loadingIntent = new Intent(this, LoadingActivity.class);
        startActivity(loadingIntent);

        lineChart = (LineChart) findViewById(R.id.chart);
        backPressCloseHandler = new BackPressCloseHandler(this);


        //크롤링 클래스 실행
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
        JsoupAsyncTaskFAQ jsoupAsyncTaskFAQ = new JsoupAsyncTaskFAQ();
        jsoupAsyncTaskFAQ.execute();

        lineChart = (LineChart) findViewById(R.id.chart);
        datetextView = findViewById(R.id.dateButton);
        tvAverSys = findViewById(R.id.tv_aver_sys);
        tvAverDia = findViewById(R.id.tv_aver_dia);

        // 탭 아이콘 지정
        host = findViewById(R.id.host);
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
        userNameTV = nav_header_view.findViewById(R.id.userName);
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

        DatabaseReference userDatabase = mDatabase.child("users").child(userID);

        updateProfile(userDatabase);
//        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    if (snapshot.getKey().equals("userBirth")) {
//                        tvDateOfBirth.setText(snapshot.getValue().toString());
//                        userBirth = tvDateOfBirth.getText().toString();
//                        mYear = Integer.parseInt(userBirth.substring(0, 4));
//                        mMonth = Integer.parseInt(userBirth.substring(4, 6));
//                        mDay = Integer.parseInt(userBirth.substring(6, 8));
//                        Calendar c = Calendar.getInstance();
//                        mAge = c.get(Calendar.YEAR) - mYear + 1;
//                        updateEditText();
//                    } else if (snapshot.getKey().equals("userName")) {
//                        tvUserName.setText(snapshot.getValue().toString());
//                        userNameTV.setText(snapshot.getValue().toString());
//                    } else if (snapshot.getKey().equals("userStatus")) {
//                        tvStatus.setText(snapshot.getValue().toString());
//                    } else if (snapshot.getKey().equals("userPredDate")) {
//                        tvPredDate.setText(snapshot.getValue().toString());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


        // tab2 - graph
        TextView textViewHomeDate = findViewById(R.id.tv_home_date);
        textViewHomeDate.setText(getTime1());

        currDate = getTime3();


        SysEntries = new ArrayList<>();
        SysEntries.add(new Entry());

        lineDataSet1 = new LineDataSet(SysEntries, "SYS");
        lineDataSet1.setLineWidth(2);
        lineDataSet1.setCircleRadius(4);
        lineDataSet1.setCircleColor(Color.parseColor("#FBD5DA"));
        lineDataSet1.setCircleHoleColor(Color.parseColor("#F8A7B6"));
        lineDataSet1.setColor(Color.parseColor("#FBD5DA"));
        lineDataSet1.setDrawCircleHole(true);
        lineDataSet1.setDrawCircles(true);
        lineDataSet1.setDrawHorizontalHighlightIndicator(false);
        lineDataSet1.setDrawHighlightIndicators(false);
        lineDataSet1.setDrawValues(false);
        lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);

        DiaEntries = new ArrayList<>();
        DiaEntries.add(new Entry());

        lineDataSet2 = new LineDataSet(DiaEntries, "DIA");
        lineDataSet2.setLineWidth(2);
        lineDataSet2.setCircleRadius(4);
        lineDataSet2.setCircleColor(Color.parseColor("#B1E6E0"));
        lineDataSet2.setCircleHoleColor(Color.parseColor("#48B9AC"));
        lineDataSet2.setColor(Color.parseColor("#B1E6E0"));
        lineDataSet2.setDrawCircleHole(true);
        lineDataSet2.setDrawCircles(true);
        lineDataSet2.setDrawHorizontalHighlightIndicator(false);
        lineDataSet2.setDrawHighlightIndicators(false);
        lineDataSet2.setDrawValues(false);
        lineDataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);

        lineData = new LineData(lineDataSet1);
        lineData.addDataSet(lineDataSet2);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return "-";
            }
        });

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        MyMarkerView marker = new MyMarkerView(this, R.layout.marker);
        marker.setChartView(lineChart);
        lineChart.setMarker(marker);
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        lineChart.animateY(2000, Easing.EaseInCubic);
        lineChart.setVisibleXRangeMaximum(9);
        lineChart.moveViewToX(lineData.getEntryCount());
        lineChart.invalidate();


        // tab1, tab2 - setBP

        tvHomeSys1 = findViewById(R.id.tv_home_m_sys);
        tvHomeSys2 = findViewById(R.id.tv_home_a_sys);
        tvHomeSys3 = findViewById(R.id.tv_home_e_sys);
        tvHomeDia1 = findViewById(R.id.tv_home_m_dia);
        tvHomeDia2 = findViewById(R.id.tv_home_a_dia);
        tvHomeDia3 = findViewById(R.id.tv_home_e_dia);

        textViewDate = findViewById(R.id.curr_date);
        textViewDate.setText(getTime2());

        BPTextView1 = findViewById(R.id.tv_m_bp);
        BPTextView2 = findViewById(R.id.tv_a_bp);
        BPTextView3 = findViewById(R.id.tv_e_bp);

        BPTimeTextView1 = findViewById(R.id.tv_m_time);
        BPTimeTextView2 = findViewById(R.id.tv_a_time);
        BPTimeTextView3 = findViewById(R.id.tv_e_time);

        DatabaseReference bpDataBase = mDatabase.child("users").child(userID).child("bloodPressure");
        setBPDataBase(bpDataBase); // tab2
        drawBPGraph(bpDataBase);
    }

    @Override
    public void onCatagoryApplySelected(float sys, float dia) {
        SysEntries.add(new Entry(sysCount++, sys));
        DiaEntries.add(new Entry(diaCount++, dia));
        lineChart.setVisibleXRangeMaximum(9);
        lineChart.moveViewToX(lineData.getEntryCount());
        lineDataSet1.notifyDataSetChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();

        totalSys += sys;
        totalDia += dia;
        float averSys = totalSys / (sysCount - 1);
        float averDia = totalDia / (diaCount - 1);
        tvAverSys.setText(Float.toString(averSys));
        tvAverDia.setText(Float.toString(averDia));

        if (averSys >= 120) {
            if (averSys >= 140) {
                tvAverSys.setTextColor(getResources().getColor(R.color.colorRed));
            } else {
                tvAverSys.setTextColor(getResources().getColor(R.color.colorOrange));
            }
        }

        if (averDia >= 80) {
            if (averDia >= 90) {
                tvAverDia.setTextColor(getResources().getColor(R.color.colorRed));
            } else {
                tvAverDia.setTextColor(getResources().getColor(R.color.colorOrange));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseReference bpDataBase = database.getReference().child("users").child(userID).child("bloodPressure");
        DatabaseReference userDatabase = database.getReference().child("users").child(userID);
        setBPDataBase(bpDataBase);
        updateProfile(userDatabase);



        /*-- 주연 --*/
        button_connect_bluetooth = (ImageButton) findViewById(R.id.button_connect_bluetooth);


        //블루투스 연결 버튼
        button_connect_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 새 창에서 BluetoothConnectActivity 실행
                Intent intent = new Intent(MainActivity.this, BluetoothConnectActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up_anim, R.anim.no_change);
            }
        });

        listView = (ListView) findViewById(R.id.listHealth);
        listViewAdapter = new ListViewAdapter();

        adapter = new ArrayAdapter(this, R.layout.web_list_layout, EMPTY);
        listFAQ = (ListView) findViewById(R.id.listFAQ);
        listFAQ.setAdapter(adapter);


    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                Document doc1 = Jsoup.connect(htmlPageUrl_1)
                        .userAgent(userAgent)
                        .get();
                //테스트
                //Elements titles= doc.select("div.news-con h1.tit-news");
                //배열
                Document doc2 = Jsoup.connect(htmlPageUrl_2).userAgent(userAgent).get();
                Document doc3 = Jsoup.connect(htmlPageUrl_3).userAgent(userAgent).get();


                //이미지 가져와서 비트맵으로 변환
                titles1 = doc1.select("div.post-content h1.entry-title");
                body1 = doc1.select("div.entry-summary p");
                titles2 = doc2.select("div.post-content h1.entry-title");
                body2 = doc1.select("div.entry-summary p");
                titles3 = doc3.select("div.post-content h1.entry-title");
                body3 = doc1.select("div.entry-summary p");

                bm1 = getImage(doc1, titles1.size());
                bm2 = getImage(doc2, titles2.size());
                bm3 = getImage(doc3, titles3.size());


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            for (int i = 0; i < titles1.size(); i++) {
                listViewAdapter.addItem(titles1.get(i).text().trim(), body1.get(i).text().trim(), bm1[i]);
                LIST_MENU.add(titles1.get(i).text().trim());
            }
            for (int i = 0; i < titles2.size(); i++) {
                listViewAdapter.addItem(titles2.get(i).text().trim(), body2.get(i).text().trim(), bm2[i]);
                LIST_MENU.add(titles2.get(i).text().trim());
            }
            for (int i = 0; i < titles3.size(); i++) {
                listViewAdapter.addItem(titles3.get(i).text().trim(), body3.get(i).text().trim(), bm3[i]);
                LIST_MENU.add(titles3.get(i).text().trim());
            }


            //리스트뷰에 어뎁터 set
            listView.setAdapter(listViewAdapter);

            //total 14
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent intent = new Intent(WebQandA.this, MainActivity.class);
//                    startActivity(intent);

                    //intent로 pos(0-13)전달
                    Intent intent = new Intent(MainActivity.this, WebHealthDetailActivity.class);
                    Log.i("POSITION", String.valueOf(position));
                    intent.putExtra("POSITION", String.valueOf(position));
                    intent.putExtra("TITLE", String.valueOf(LIST_MENU.get(position)));
                    Log.i("log", String.valueOf(LIST_MENU.get(position)));

                    //startActivity(new Intent(현재Activity.this, 불러올Activity.class));
                    //overridePendingTransition(R.anim.현재(사라질)Activity애니메이션, R.anim.현재(사라질)Activity애니메이션);

                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_up_anim, R.anim.no_change);
                }
            });

        }
    }

    private class JsoupAsyncTaskFAQ extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                Connection.Response loginPageResponse = Jsoup.connect(ntmlLogin)
                        .method(Connection.Method.GET)
                        .execute();

                //로그인페이지 쿠키 (로그인 전 쿠키)
                Map<String, String> loginTryCookie = loginPageResponse.cookies();

                //로그인페이지로 전송할 토큰
                //String ofp = loginPageDocument.select("input.m_id").val();
                //String nfp = loginPageDocument.select("input.password").val();

                //로그인페이지로 전송할 data
                Map<String, String> data = new HashMap<>();
                data.put("m_id", "wndus6165");
                data.put("password", "Wkwmdsk0920");

                Connection.Response response = Jsoup.connect(ntmlLogin)
                        .userAgent(userAgent)
                        .timeout(3000)
                        .cookies(loginTryCookie)//쿠키
                        .data(data)//데이터
                        .method(Connection.Method.POST)//보내기
                        .execute();//실행

                loginCookie = response.cookies();//로그인 후의 쿠키

                Document doc = Jsoup.connect(htmlPageUrl119)
                        .userAgent(userAgent)
                        .get();

                Elements titles = doc.select("div.sub_page tr");
                Log.v("sdgsadg", "parsingDiv");

                titleList = titles;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.v("onPostExecute", "check");

            //titleList에서 짝수 element만 가져옴
            for (int i = 4; i < 43; i += 2) {
                Element e = titleList.get(i);
                System.out.println("title: " + e.text());

                LIST_FAQ.add(e.text().trim());
                adapter.add(e.text().trim());
            }

            listFAQ.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //position :0-19
                    Intent intent = new Intent(MainActivity.this, WebQandADetailActivity.class);
                    Log.i("POSITION", String.valueOf(position));
                    intent.putExtra("POSITION", String.valueOf(position));
                    //startActivity(intent);


                    //imageview.setImageResource(R.drawable.mountain);

                    //FAQimg.setImageResource(R.drawable.s1250);

                    final Dialog dialog = new Dialog(MainActivity.this);

                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.fragment_faq_dialog);
                    ImageView FAQimg = dialog.findViewById(R.id.FAQimg);

                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    };

                    switch (position) {
                        //dafault = (1258, 5)
                        case 0:
                            FAQimg.setImageResource(R.drawable.s1263);
                            break;
                        case 1:
                            FAQimg.setImageResource(R.drawable.s1262);
                            break;
                        case 2:
                            FAQimg.setImageResource(R.drawable.s1261);
                            break;
                        case 3:
                            FAQimg.setImageResource(R.drawable.s1260);
                            break;
                        case 4:
                            FAQimg.setImageResource(R.drawable.s1259);
                            break;
                        case 6:
                            FAQimg.setImageResource(R.drawable.s1257);
                            break;
                        case 7:
                            FAQimg.setImageResource(R.drawable.s1256);
                            break;
                        case 8:
                            FAQimg.setImageResource(R.drawable.s1255);
                            break;
                        case 9:
                            FAQimg.setImageResource(R.drawable.s1254);
                            break;
                        case 10:
                            FAQimg.setImageResource(R.drawable.s1253);
                            break;
                        case 11:
                            FAQimg.setImageResource(R.drawable.s1252);
                            break;
                        case 12:
                            FAQimg.setImageResource(R.drawable.s1251);
                            break;
                        case 13:
                            FAQimg.setImageResource(R.drawable.s1250);
                            break;
                        case 14:
                            FAQimg.setImageResource(R.drawable.s1249);
                            break;
                        case 15:
                            FAQimg.setImageResource(R.drawable.s1248);
                            break;
                        case 16:
                            FAQimg.setImageResource(R.drawable.s1247);
                            break;
                        case 17:
                            FAQimg.setImageResource(R.drawable.s1246);
                            break;
                        case 18:
                            FAQimg.setImageResource(R.drawable.s1245);
                            break;
                        case 19:
                            FAQimg.setImageResource(R.drawable.s1244);
                            break;
                    }


                    Button ok = dialog.findViewById(R.id.filter_ok);

                    switch (position) {
                        case 0:

                    }

                    ok.setOnClickListener(listener);
                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();


                    params.width = 1400;
                    dialog.getWindow().setAttributes(params);
                    dialog.show();
                }
            });

        }
    }

    //이미지 가져오기
    public Bitmap[] getImage(Document doc, int size) {
        Bitmap[] bm = new Bitmap[6];
        for (int i = 0; i < size; i++) {

            String imgUrl = "" + doc.body()
                    .getElementsByClass("attachment-medium wp-post-image").eq(i).select("img").attr("src");
            System.out.println(imgUrl);

            try {
                URL url = new URL(imgUrl);
                URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                bm[i] = BitmapFactory.decodeStream(bis);
                bis.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm;
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
            case R.id.button_input:
                host.setCurrentTab(1);
                break;
        }
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

    private String getTime3() {
        long mNow;
        Date mDate;

        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);

        return mFormat2.format(mDate);
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
                    Uri providerURI = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
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
        Log.i("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);

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
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Log.i("REQUEST_TAKE_PHOTO", "OK");
                        galleryAddPic();

                        btnPhoto.setImageURI(imageUri);
                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText((MainActivity.this), "사진찍기를 취소하였습니다", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_TAKE_ALBUM:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getData() != null) {
                        try {
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                            cropImage();
                        } catch (Exception e) {
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;
            case REQUEST_IMAGE_CROP:
                if (resultCode == Activity.RESULT_OK) {
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

    private void updateProfile(DatabaseReference userDatabase) {
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals("userBirth")) {
                        tvDateOfBirth.setText(snapshot.getValue().toString());
                        userBirth = tvDateOfBirth.getText().toString();
                        mYear = Integer.parseInt(userBirth.substring(0, 4));
                        mMonth = Integer.parseInt(userBirth.substring(4, 6));
                        mDay = Integer.parseInt(userBirth.substring(6, 8));
                        Calendar c = Calendar.getInstance();
                        mAge = c.get(Calendar.YEAR) - mYear + 1;
                        updateEditText();
                    } else if (snapshot.getKey().equals("userName")) {
                        tvUserName.setText(snapshot.getValue().toString());
                        userNameTV.setText(snapshot.getValue().toString());
                    } else if (snapshot.getKey().equals("userStatus")) {
                        tvStatus.setText(snapshot.getValue().toString());
                    } else if (snapshot.getKey().equals("userPredDate")) {
                        tvPredDate.setText(snapshot.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void drawBPGraph(DatabaseReference bpDataBase) {
        bpDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                        for (DataSnapshot snapshot3 : snapshot2.getChildren()) {
                            if (snapshot3.getKey().equals("sys")) {
                                SysEntries.add(new Entry(sysCount++, Float.parseFloat(snapshot3.getValue().toString())));
                                lineChart.setVisibleXRangeMaximum(9);
                                lineChart.moveViewToX(lineData.getEntryCount());
                                lineDataSet1.notifyDataSetChanged();
                                lineChart.notifyDataSetChanged();
                                lineChart.invalidate();

                                totalSys += Float.parseFloat(snapshot3.getValue().toString());
                            } else if (snapshot3.getKey().equals("dia")) {
                                DiaEntries.add(new Entry(diaCount++, Float.parseFloat(snapshot3.getValue().toString())));
                                lineChart.setVisibleXRangeMaximum(9);
                                lineChart.moveViewToX(lineData.getEntryCount());
                                lineDataSet1.notifyDataSetChanged();
                                lineChart.notifyDataSetChanged();
                                lineChart.invalidate();

                                totalDia += Float.parseFloat(snapshot3.getValue().toString());
                            }
                        }
                    }
                }

                float averSys = totalSys / (sysCount - 1);
                float averDia = totalDia / (diaCount - 1);
                tvAverSys.setText(Float.toString(averSys));
                tvAverDia.setText(Float.toString(averDia));

                if (averSys >= 120) {
                    if (averSys >= 140) {
                        tvAverSys.setTextColor(getResources().getColor(R.color.colorRed));
                    } else {
                        tvAverSys.setTextColor(getResources().getColor(R.color.colorOrange));
                    }
                }

                if (averDia >= 80) {
                    if (averDia >= 90) {
                        tvAverDia.setTextColor(getResources().getColor(R.color.colorRed));
                    } else {
                        tvAverDia.setTextColor(getResources().getColor(R.color.colorOrange));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

                            if (when.equals("1morning")) {
                                BPTimeTextView1.setText(" (" + bp[2] + ") ");
                                BPTextView1.setText(bp[1] + " / " + bp[0]);
                                tvHomeSys1.setText(bp[1]);
                                tvHomeDia1.setText(bp[0]);

                                if (Float.parseFloat(bp[1]) >= 120) {
                                    if (Float.parseFloat(bp[1]) >= 140) {
                                        tvHomeSys1.setTextColor(getResources().getColor(R.color.colorRed));
                                    } else {
                                        tvHomeSys1.setTextColor(getResources().getColor(R.color.colorOrange));
                                    }
                                }

                                if (Float.parseFloat(bp[0]) >= 80) {
                                    if (Float.parseFloat(bp[0]) >= 90) {
                                        tvHomeDia1.setTextColor(getResources().getColor(R.color.colorRed));
                                    } else {
                                        tvHomeDia1.setTextColor(getResources().getColor(R.color.colorOrange));
                                    }
                                }
                            } else if (when.equals("2afternoon")) {
                                BPTimeTextView2.setText(" (" + bp[2] + ") ");
                                BPTextView2.setText(bp[1] + " / " + bp[0]);
                                tvHomeSys2.setText(bp[1]);
                                tvHomeDia2.setText(bp[0]);

                                if (Float.parseFloat(bp[1]) >= 120) {
                                    if (Float.parseFloat(bp[1]) >= 140) {
                                        tvHomeSys2.setTextColor(getResources().getColor(R.color.colorRed));
                                    } else {
                                        tvHomeSys2.setTextColor(getResources().getColor(R.color.colorOrange));
                                    }
                                }

                                if (Float.parseFloat(bp[0]) >= 80) {
                                    if (Float.parseFloat(bp[0]) >= 90) {
                                        tvHomeDia2.setTextColor(getResources().getColor(R.color.colorRed));
                                    } else {
                                        tvHomeDia2.setTextColor(getResources().getColor(R.color.colorOrange));
                                    }
                                }
                            } else if (when.equals("3evening")) {
                                BPTimeTextView3.setText(" (" + bp[2] + ") ");
                                BPTextView3.setText(bp[1] + " / " + bp[0]);
                                tvHomeSys3.setText(bp[1]);
                                tvHomeDia3.setText(bp[0]);

                                if (Float.parseFloat(bp[1]) >= 120) {
                                    if (Float.parseFloat(bp[1]) >= 140) {
                                        tvHomeSys3.setTextColor(getResources().getColor(R.color.colorRed));
                                    } else {
                                        tvHomeSys3.setTextColor(getResources().getColor(R.color.colorOrange));
                                    }
                                }

                                if (Float.parseFloat(bp[0]) >= 80) {
                                    if (Float.parseFloat(bp[0]) >= 90) {
                                        tvHomeDia3.setTextColor(getResources().getColor(R.color.colorRed));
                                    } else {
                                        tvHomeDia3.setTextColor(getResources().getColor(R.color.colorOrange));
                                    }
                                }
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
            }, mYear, mMonth, mDay);

            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            datePickerDialog.show();
        }
        return true;
    }

    protected void updateEditText() {
        StringBuffer sb = new StringBuffer();
        datetextView.setText(sb.append(mYear + "/").append(mMonth + "/").append(mDay));
        mMonth--;
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
            gender = 1;
            manView.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_selected_background));
            womanView.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_background));
        } else if (id == R.id.womanButton) {
            gender = 2;
            womanView.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_selected_background));
            manView.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_background));
        } else if (id == R.id.smokeTButton) {
            smokeTF = true;
            smokeT.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_selected_background));
            smokeF.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_background));
        } else if (id == R.id.smokeFButton) {
            smokeTF = false;
            smokeF.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_selected_background));
            smokeT.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_background));
        } else if (id == R.id.familyTButton) {
            familyTF = true;
            familyT.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_selected_background));
            familyF.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_background));
        } else if (id == R.id.familyFButton) {
            familyTF = true;
            familyF.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_selected_background));
            familyT.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_background));
        }
        return true;
    }

    //예측하기 버튼
    public boolean onclickedPredbutton(View view) {
        int id = view.getId();
        if (id == R.id.predButton) {
            EditText keyView = (EditText) findViewById(R.id.statureButton);
            EditText weightView = (EditText) findViewById(R.id.weightButton);
            EditText exerView = (EditText) findViewById(R.id.walkButton);
            EditText highbpView = (EditText) findViewById(R.id.highbpButton);
            EditText lowbpView = (EditText) findViewById(R.id.lowbpButton);

            mKey = Integer.parseInt(keyView.getText().toString());
            mWeight = Integer.parseInt(weightView.getText().toString());
            mExer = Integer.parseInt(exerView.getText().toString());
            mHighbp = Integer.parseInt(highbpView.getText().toString());
            mLowbp = Integer.parseInt(lowbpView.getText().toString());
            int mAgegroup = (mAge - 10) / 3;
            float mBMI = (float) mWeight / (float) mKey;

            //사용자 data 입력
            float[] inputs = new float[]{gender, mAgegroup, mHighbp, mLowbp, mBMI};
            //예측값 변수 초기화
            float[] output = new float[]{0};

            Interpreter tflite = getTfliteInterpreter("regression_model.tflite");
            tflite.run(inputs, output);

            TextView pred = findViewById(R.id.bigButton);
            if (output[0] > 120) {
                pred.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_red_background));
            } else
                pred.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_background));

            pred.setText("6개월 후 수축기혈압수치\n" + String.valueOf(Math.round(output[0]) + "mmHg"));

            updateBPstatus(Math.round(output[0]));

            mDatabase.child("users").child(userID).child("userPredDate").setValue(getTime3());

            final ScrollView scrollView = findViewById(R.id.tab_content3);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            });

            onResume();
        }


        return true;
    }

    private void updateBPstatus(int BP) {
        if (BP >= 140) {
            mDatabase.child("users").child(userID).child("userStatus").setValue("매우 위험 (" + BP + "mmHg)");
        } else if (BP >= 120) {
            mDatabase.child("users").child(userID).child("userStatus").setValue("위험 (" + BP + "mmHg)");
        } else {
            mDatabase.child("users").child(userID).child("userStatus").setValue("양호 (" + BP + "mmHg)");
        }
    }

    //tensorflow 사용에 필요한 것
    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(MainActivity.this, modelPath));
        } catch (Exception e) {
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
