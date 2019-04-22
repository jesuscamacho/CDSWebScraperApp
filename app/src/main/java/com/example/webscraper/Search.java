package com.example.webscraper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Search extends AppCompatActivity {
    protected SQLiteDatabase db =null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Intent t = new Intent(Search.this, MainActivity.class);
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
        setContentView(R.layout.activity_search);
        overridePendingTransition(0, 0);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(1).setChecked(true);

        db  =  openOrCreateDatabase("any",MODE_PRIVATE, null);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_search){
            Intent t = new Intent(Search.this, MainActivity.class);
            startActivity(t);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void searchFood(View v){
        EditText ed = findViewById(R.id.query);
        String query = ed.getText().toString();
        Cursor c = null;
        String s = "";
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.container);
        if (!query.equals("")) {
            linearLayout.removeAllViews();
            c = db.rawQuery("select name from Food where name like '%" + query + "%'", null);
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                for (int j = 0; j < c.getColumnCount(); j++) {
                    /// Log.v("MYTAG","********** "+c.getString(j));
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
                    btn.setText("track");
                    btn.setLayoutParams(lp);
                    btn.setOnClickListener(new View.OnClickListener(){
                       @Override
                       public void onClick(View v) {
                           /*-----------------------TODO: ADD FOOD TO USER DB-------------------------------------*/
                           //db.execSQL("insert into User values('Chase','"+item.text().replace("'","")+"');");
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
}