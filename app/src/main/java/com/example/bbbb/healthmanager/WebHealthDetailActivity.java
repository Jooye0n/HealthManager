package com.example.bbbb.healthmanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebHealthDetailActivity extends AppCompatActivity {

    private int position;
    private String titleIntent;
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
    private Map<String, String > loginCookie;
    private String detailPage1 = "http://hqcenter.snu.ac.kr/archives/jiphyunjeon/%EC%95%88%EC%A0%84%ED%95%98%EA%B3%A0-%EA%B1%B4%EA%B0%95%ED%95%9C-%EC%82%B0%ED%96%89%EC%9D%84-%EC%9C%84%ED%95%9C-%EB%93%B1%EC%83%81-%EA%B0%80%EC%9D%B4%EB%93%9C?pnum=0&cat=95";
    private String detailPage2 = "http://hqcenter.snu.ac.kr/archives/jiphyunjeon/%EC%8B%9C%EA%B0%81%EC%9E%A5%EC%95%A0%EC%9D%B8%EC%9D%84-%EC%9C%84%ED%95%9C-%EC%9A%B4%EB%8F%99-%EA%B8%B8%EB%9D%BC%EC%9E%A1%EC%9D%B4-2?pnum=0&cat=95";
    static List LIST_MENU = new ArrayList();
    static List EMPTY = new ArrayList();
    private TextView sub1, sub2, sub3;
    private Boolean chk = false;
    private TextView title;
    private ImageView imageView;
    private Bitmap bm;
    private ArrayList<Element> imgList = new ArrayList<>();
    private TextView contextTitle1, contextTitle2, p0;
    private TextView contextBody1, contextBody2, contextBody3;
    private ListView listViewDetail;
    private ListViewDetailAdapter listViewAdapterDetail;
    private Elements PSET, HSET;
    private ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        position = Integer.parseInt(getIntent().getExtras().getString("POSITION"));
        titleIntent = String.valueOf(getIntent().getExtras().getString("TITLE"));
        Log.i("log", titleIntent);

        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();

        title = (TextView)findViewById(R.id.title_detail);
        sub1 = (TextView)findViewById(R.id.subtitle1);
        sub2 = (TextView)findViewById(R.id.subtitle2);
        sub3 = (TextView)findViewById(R.id.subtitle3);

        p0 = (TextView)findViewById(R.id.p0);
        imageView = (ImageView)findViewById(R.id.imageView_detail);
        contextTitle1 = (TextView)findViewById(R.id.context1);
        contextTitle2 = (TextView)findViewById(R.id.context3);
        contextBody1 = (TextView)findViewById(R.id.context2);
        contextBody2 = (TextView)findViewById(R.id.context4);
        contextBody3 = (TextView)findViewById(R.id.context5);

        title.setText(titleIntent);
        title.setSelected(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);


        listViewDetail = (ListView)findViewById(R.id.list_detail);
        listViewAdapterDetail = new ListViewDetailAdapter();

        //리스트뷰에 어뎁터 set
        listViewDetail.setAdapter(listViewAdapterDetail);


        WebHealthDetailActivity.this.findViewById(R.id.scroll).scrollTo(0, 0);



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Document doc;
            //thread 2번째
            try {

                switch(position){
                    case 1:
                        doc = Jsoup.connect(detailPage2)
                                .userAgent(userAgent)
                                .get();

                        ///LIST_MENU.add(e.text().trim());
                        LIST_MENU.add(doc.select("div.entry-meta time.entry-date").get(0).text().trim());//작성일
                        LIST_MENU.add(doc.select("div.entry-meta span.modified-date").get(0).text().trim());//최종수정일
                        LIST_MENU.add(doc.select("div.entry-meta span.hits").get(0).text().trim());//조회수


                        PSET = doc.select("div.entry-content p");


                        for(Element e:PSET){
                            Log.i("PSET", e.text().trim()+'\n');
                        }

                        Log.i("time1", String.valueOf(LIST_MENU.get(0)));
                        Log.i("time2", String.valueOf(LIST_MENU.get(1)));
                        Log.i("clicknum", String.valueOf(LIST_MENU.get(2)));
//                        Log.i("h2", String.valueOf(LIST_MENU.get(3)));
//                        Log.i("h2", String.valueOf(LIST_MENU.get(4)));
//                        Log.i("p0", String.valueOf(LIST_MENU.get(5)));
//                        Log.i("p3", String.valueOf(LIST_MENU.get(6)));
//                        Log.i("p4", String.valueOf(LIST_MENU.get(7)));

                        String imgUrl2 = "http://hqcenter.snu.ac.kr/hp/wp-content/uploads/filebox/board/MTQyMDUyMTA0NzA0MTMzODAw.jpg";
                        System.out.println(imgUrl2);
                        URL url2 = new URL(imgUrl2);
                        URLConnection conn2 = url2.openConnection();
                        conn2.connect();
                        BufferedInputStream bis2 = new BufferedInputStream(conn2.getInputStream());
                        bm = BitmapFactory.decodeStream(bis2);
                        bis2.close();
                        break;

                        default:
                            doc = Jsoup.connect(detailPage1)
                                    .userAgent(userAgent)
                                    .get();

                            ///LIST_MENU.add(e.text().trim());
                            LIST_MENU.add(doc.select("div.entry-meta time.entry-date").get(0).text().trim());
                            LIST_MENU.add(doc.select("div.entry-meta span.modified-date").get(0).text().trim());
                            LIST_MENU.add(doc.select("div.entry-meta span.hits").get(0).text().trim());
                            LIST_MENU.add(doc.select("div.entry-content h2").get(0).text().trim());
                            LIST_MENU.add(doc.select("div.entry-content h2").get(1).text().trim());
                            LIST_MENU.add(doc.select("div.entry-content p").get(0).text().trim());
                            LIST_MENU.add(doc.select("div.entry-content p").get(3).text().trim());//body1
                            LIST_MENU.add(doc.select("div.entry-content p").get(4).text().trim());//body2

                            PSET = doc.select("div.entry-content p");
                            HSET = doc.select("div.entry-content h3");


                            for(Element e:PSET){
                                Log.i("PSET", e.text().trim()+'\n');
                            }

                            for(Element e:HSET){
                                Log.i("HSET", e.text().trim()+'\n');
                            }


                            Log.i("time1", String.valueOf(LIST_MENU.get(0)));
                            Log.i("time2", String.valueOf(LIST_MENU.get(1)));
                            Log.i("clicknum", String.valueOf(LIST_MENU.get(2)));
                            Log.i("h2", String.valueOf(LIST_MENU.get(3)));
                            Log.i("h2", String.valueOf(LIST_MENU.get(4)));
                            Log.i("p0", String.valueOf(LIST_MENU.get(5)));
                            Log.i("p3", String.valueOf(LIST_MENU.get(6)));
                            Log.i("p4", String.valueOf(LIST_MENU.get(7)));

                            String imgUrl = ""+doc.body().getElementsByClass("aligncenter size-medium wp-image-30385").eq(0).select("img").attr("src");
                            System.out.println(imgUrl);
                            URL url = new URL(imgUrl);
                            URLConnection conn = url.openConnection();
                            conn.connect();
                            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                            bm = BitmapFactory.decodeStream(bis);
                            bis.close();

                            break;

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            //background thread가 종료된 이후의 UI

            //e.text().trim()
            switch (position){

                case 1:
                    sub1.setText(String.valueOf(LIST_MENU.get(0)));
                    sub2.setText(String.valueOf(LIST_MENU.get(1)));
                    sub3.setText(String.valueOf(LIST_MENU.get(2)));
                    imageView.setImageBitmap(bm);
                    p0.setVisibility(View.GONE);//setText(String.valueOf(LIST_MENU.get(5)));
                    contextTitle1.setVisibility(View.GONE);//setText(String.valueOf(LIST_MENU.get(3)));
                    contextTitle2.setVisibility(View.GONE);//setText(String.valueOf(LIST_MENU.get(4)));

                    contextBody1.setText(PSET.get(2).text().trim());
                    contextBody2.setText(PSET.get(4).text().trim());
                    contextBody3.setText(PSET.get(6).text().trim());

                    /*
                    listViewAdapterDetail.addItem(HSET.get(0).text().trim(),PSET.get(3).text().trim());
                    listViewAdapterDetail.addItem(HSET.get(1).text().trim(),PSET.get(4).text().trim());
                    listViewAdapterDetail.addItem(HSET.get(2).text().trim(),PSET.get(5).text().trim()+'\n'+'\n'+PSET.get(6).text().trim());
                    listViewAdapterDetail.addItem(HSET.get(3).text().trim(),PSET.get(7).text().trim()+'\n'+'\n'+PSET.get(8).text().trim());
                     */
                    LIST_MENU.clear();
                    PSET.clear();
                    break;


                default:
                    sub1.setText(String.valueOf(LIST_MENU.get(0)));
                    sub2.setText(String.valueOf(LIST_MENU.get(1)));
                    sub3.setText(String.valueOf(LIST_MENU.get(2)));
                    imageView.setImageBitmap(bm);
                    p0.setText(String.valueOf(LIST_MENU.get(5)));
                    contextTitle1.setText(String.valueOf(LIST_MENU.get(3)));
                    contextTitle2.setText(String.valueOf(LIST_MENU.get(4)));
                    contextBody1.setText(String.valueOf(LIST_MENU.get(6)));
                    contextBody2.setText(String.valueOf(LIST_MENU.get(7)));


                    listViewAdapterDetail.addItem(HSET.get(0).text().trim(),PSET.get(3).text().trim());
                    listViewAdapterDetail.addItem(HSET.get(1).text().trim(),PSET.get(4).text().trim());
                    listViewAdapterDetail.addItem(HSET.get(2).text().trim(),PSET.get(5).text().trim()+'\n'+'\n'+PSET.get(6).text().trim());
                    listViewAdapterDetail.addItem(HSET.get(3).text().trim(),PSET.get(7).text().trim()+'\n'+'\n'+PSET.get(8).text().trim());
                    LIST_MENU.clear();
                    PSET.clear();
                    HSET.clear();

                    //리스트뷰에 어뎁터 set
                    //listViewDetail.setAdapter(listViewAdapterDetail);
                    break;


            }
            WebHealthDetailActivity.this.findViewById(R.id.scroll).scrollTo(0, 0);
        }


    }
}


