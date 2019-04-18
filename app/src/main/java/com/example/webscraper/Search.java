package com.example.webscraper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
        TextView txt = findViewById(R.id.searchResult);
        EditText ed = findViewById(R.id.query);
        String query = ed.getText().toString();
        Cursor c = null;
        String s = "";
        c = db.rawQuery("select name from Food where name like '%"+query+"%'", null);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            for (int j = 0; j < c.getColumnCount(); j++) {
                s += "  \n" + c.getString(j);
                Log.v("MYTAG","********** "+c.getString(j));
            }
            c.moveToNext();
        }
        c.close();

        txt.setText(s);

    }
}