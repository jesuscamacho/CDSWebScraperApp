package com.example.webscraper;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button getBtn;
    private TextView result;
    protected SQLiteDatabase db =null;



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

        db  =  openOrCreateDatabase("any",MODE_PRIVATE, null);
       dbsetup();
        //load() db into view

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
                    ScrollView views = findViewById(R.id.scroll);

                    for (Element item : food_items) {
                        //Log.v("yaaa","--- ------ "+ item.text());
                       // db.execSQL("insert into Food values('Chase','"+item.text().replace("'","")+"');");
                        builder.append('\n').append('\n').append(item.text());
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

    private void dbsetup(){
       // db.execSQL("Drop table if exists Food");
        db.execSQL(" create table if not exists Food(hall text,name text);");
        db.execSQL("Drop table if exists User");
        db.execSQL(" create table if not exists User(food text);");
    }
//    private void insertFood(String hall,String name){
//        //db.execSQL("insert into Food("+hall+","+name+");");
//        Log.v("yaaa","--- ------ "+hall+"  "+ name);
//        //db.execSQL(" create table if not exists user(id int,food text);");
//    }

}
