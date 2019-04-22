package com.example.webscraper;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
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
import android.widget.RelativeLayout;
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
import java.util.Timer;
import java.util.TimerTask;

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
       Cursor c = null;

      /*--------------button that calls get website and result text view commented out------------*/
//        result = findViewById(R.id.result);
//        result.setText("No foods are being tracked");
//        getBtn = findViewById(R.id.getBtn);
//        getBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getWebsite();
//            }
//        });
       BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
       navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        LinearLayout linearLayout = findViewById(R.id.userfood);
       c = db.rawQuery("select food from User", null);
        if (c.getCount()!=0) {
            c.moveToFirst();
            linearLayout.removeAllViews();
            for (int i = 0; i < c.getCount(); i++) {
                for (int j = 0; j < c.getColumnCount(); j++) {
                    // set up linear layout
                    RelativeLayout newlayout = new RelativeLayout(this);
                    newlayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

                    // set up textview
                    TextView item = new TextView(this);
                    item.setText(c.getString(j));
                    item.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    newlayout.addView(item);
                    //set up button
                    Button btn = new Button(this);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    btn.setText("untrack");
                    btn.setLayoutParams(lp);
                    btn.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            //db.execSQL("insert into User values('Chase','"+item.text().replace("'","")+"');");
                            /*-----------------------TODO: DELETE TRACKED FOOD TO USER DB-------------------------------------*/

                        }
                    });
                    newlayout.addView(btn);
                    //add border
                    GradientDrawable border = new GradientDrawable();
                    border.setColor(0xFFFFFFFF);
                    border.setStroke(1, 0xFF000000);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        newlayout.setBackground(border);
                    }
                    linearLayout.addView(newlayout);
                }
                c.moveToNext();
            }
            c.close();
        }else{
            linearLayout.removeAllViews();
        }




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
        //db.execSQL("Drop table if exists User");
        db.execSQL(" create table if not exists User(food text);");
        //db.execSQL("insert into User values('Turkey Burger Patty');");
        //db.execSQL("insert into User values('Scrambled Eggs');");

    }

}
