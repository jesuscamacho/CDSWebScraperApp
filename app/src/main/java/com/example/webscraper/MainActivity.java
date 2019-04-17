package com.example.webscraper;

import android.content.ClipData;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button getBtn;
    private TextView result;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_search:
                    Intent t = new Intent(MainActivity.this, Search.class);
                    startActivity(t);
                    finish();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_main);

        //setContentView(R.layout.activity_main);
        result = findViewById(R.id.result);
        result.setText("No foods are being tracked");
        getBtn = findViewById(R.id.getBtn);
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWebsite();
            }
        });
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    private void getWebsite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();
                DateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
                Date today = new Date();

                try {
                    Document doc = Jsoup.connect("https://dining.unc.edu/locations/chase/?date="+datef.format(today)).get();
                    String title = doc.title();
                    Elements food_items = doc.select(".menu-item-li a");
                    //Elements food_items = doc.select(".c-tabs-nav__link .is-active .c-tabs-nav__link-inner");
                    //Elements food_items = doc.select(".c-tabs-nav__link .is-active .c-tabs-nav__link-inner");
                    builder.append(title).append("\n");
                    for (Element item : food_items) {
//                        Log.v("idk","**********************");
//                        Log.v("Food:","             text: "+item.text());
//                        Log.v("Food:","             baseurl: "+item.html());
//                        Log.v("Food:","             href: "+item.cssSelector());
//                        Log.v("Food:","             tabindex: "+item.attributes());
//
//                        Log.v("idk","**********************");

                        builder.append("\n").append("\n").append("Item : ").append(item.text());
                    }
                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        result.setText(builder.toString());
                    }
                });
            }
        }).start();
    }




}
