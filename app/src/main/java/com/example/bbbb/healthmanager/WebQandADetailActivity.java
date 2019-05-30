package com.example.bbbb.healthmanager;
//Q&A눌렀을 떄 상세 페이지

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WebQandADetailActivity extends AppCompatActivity {

    int position;
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
    private Map<String, String >loginCookie;
    private String page1 = "http://www.dang119.com/shop/board/view.php?id=datac&no=14455";
    private String ntmlLogin = "https://www.dang119.com:14027/shop/member/login.php?&";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = Integer.parseInt(getIntent().getExtras().getString("POSITION"));
        //getIntent().getExtras().getString("POST_CATEGORY")

        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();


    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Connection.Response loginPageResponse = Jsoup.connect(ntmlLogin)
                        .userAgent(userAgent)
                        .timeout(3000)
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Upgrade-Insecure-Requests","1")
                        .method(Connection.Method.GET)
                        .execute();


                //로그인페이지 쿠키 (로그인 전 쿠키)
                Map<String, String> loginTryCookie = loginPageResponse.cookies();

                //로그인페이지로 전송할 data
                Map<String, String> data = new HashMap<>();
                data.put("m_id", "wndus6165");
                data.put("password", "Wkwmdsk0920");

                Connection.Response response = Jsoup.connect(ntmlLogin)
                        .userAgent(userAgent)
                        .timeout(3000)
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Upgrade-Insecure-Requests","1")
                        .cookies(loginTryCookie)//쿠키
                        .data(data)//데이터
                        .method(Connection.Method.POST)//보내기
                        .execute();//실행

                loginCookie = response.cookies();//로그인 후의 쿠키

                Log.i("로그인이후", "login fin");

                switch (position) {
                    case 0:
                        Document adminPageDocument = Jsoup.connect(page1)
                                .userAgent(userAgent)
                                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                                .header("Upgrade-Insecure-Requests","1")
                                .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
                                .get();

                        Elements table = adminPageDocument.select("div.sub_page tr");

                        //Log.i("chk","cjk");
                        int count = 0;

                        for (Element e : table) {
                            count++;
                            //System.out.println("detailtitle: " + e.text());
                            //LIST_FAQ.add(e.text().trim());
                            //adapter.add(e.text().trim());
                            //String humi = e.text();
                        }
                        Log.i("count",String.valueOf(count));


                        break;

                    case 2:
                    case 3:
                    case 4:
                    case 5://...19까지
                }
            }catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }



}
