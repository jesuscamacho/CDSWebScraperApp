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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Search extends AppCompatActivity {
    private FirebaseFirestore flame;
    private MainActivity main;


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

    private View v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        overridePendingTransition(0, 0);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(1).setChecked(true);

        flame = FirebaseFirestore.getInstance();
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
    public void here(View view) {
        EditText ed = findViewById(R.id.query);
        final String query = ed.getText().toString();
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.container);
        if (!query.equals("")) {
            linearLayout.removeAllViews();

            flame.collection("Chase").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> e) {
                    if (e != null) {
                        Log.v("TAG", "ERROR");
                    }
                    for (DocumentSnapshot doc : e.getResult()) {
                        Log.v("KOKOKOKO11", "-----------" + doc.getString("item"));

                        if (doc.getString("item").contains(query)) {
                            Log.v("kokook", "" + query);
                            final String fS = doc.getId();
                            final String fN = doc.getString("item");
                            RelativeLayout newlayout = new RelativeLayout(getApplicationContext());
                            newlayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                            TextView item = new TextView(getApplicationContext());
                            item.setText(doc.getString("item"));
                            item.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            newlayout.addView(item);
                            final Button btn = new Button(getApplicationContext());
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                btn.setText("track");

                            final boolean x = doc.getBoolean("available");
                            final String l = doc.getString("location");
                            btn.setLayoutParams(lp);
                            btn.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("item", fN);
                                    data.put("available", x);
                                    data.put("location",l);
                                    //data.put("available", false);
                                    String foodS = fS;
                                    if (btn.getText().equals("track")) {
                                        btn.setText("untrack");
                                       flame.collection("user").document(foodS).set(data);

                                    } else {
                                        btn.setText("track");
                                       flame.collection("user").document(foodS).delete();

                                    }
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
                    }
                }
            });
        } else {
            linearLayout.removeAllViews();
        }
    }

}