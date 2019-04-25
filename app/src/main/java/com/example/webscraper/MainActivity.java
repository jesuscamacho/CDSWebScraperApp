package com.example.webscraper;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.BoringLayout;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private Button getBtn;
    private TextView result;
    protected SQLiteDatabase db =null;
    private FirebaseFirestore blaze;
    private HashMap<String,Boolean> chase = new HashMap<String,Boolean>();
    private HashMap<String,Boolean> lenior = new HashMap<String,Boolean>();


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



        blaze = FirebaseFirestore.getInstance();
        look();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    public void look() {

        final LinearLayout linearLayout = findViewById(R.id.userfood);

        blaze.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> e) {
                if (e.getResult().isEmpty()) {
                    Log.v("TAG", "ERROR");
                    RelativeLayout newlayout = new RelativeLayout(getApplicationContext());
                    newlayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                    TextView item = new TextView(getApplicationContext());
                    item.setText("Search to add food");
                    newlayout.addView(item);
                    linearLayout.addView(newlayout);

                }

                for (DocumentSnapshot doc : e.getResult()) {
                        final String fS = doc.getId();
                        RelativeLayout newlayout = new RelativeLayout(getApplicationContext());
                        newlayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                        TextView item = new TextView(getApplicationContext());
                        if (doc.getBoolean("available")){
                            item.setText(doc.getString("item")+'\n'+"available today @ "+ doc.getString("location"));
                        }else{
                            item.setText(doc.getString("item")+'\n'+"Not available today :(");
                        }

                        item.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        newlayout.addView(item);
                        final Button btn = new Button(getApplicationContext());
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                        btn.setText("untrack");

                        btn.setLayoutParams(lp);
                    final String fN = doc.getString("item");
                    final boolean x = doc.getBoolean("available");
                    final String l = doc.getString("location");
                    btn.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                String foodS = fS;
                                Map<String, Object> data = new HashMap<>();
                                data.put("item", fN);
                                data.put("available", x);
                                data.put("location",l);
                                if (btn.getText().equals("untrack")) {
                                    btn.setText("undo");
                                    blaze.collection("user").document(foodS).delete();
                                } else {
                                    btn.setText("untrack");
                                    blaze.collection("user").document(foodS).set(data);
                                }
                                //look();

                            }
                        });
                        newlayout.addView(btn);
//                        //add border
                        GradientDrawable border = new GradientDrawable();
                        border.setColor(0xFFFFFFFF);
                        border.setStroke(1, 0xFF000000);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            newlayout.setBackground(border);
                        }
                        linearLayout.addView(newlayout);
                   // }
                }
            }
        });
    }




}
