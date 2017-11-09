package com.willy.maziwa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Alberto on 1/15/2016.
 */
public class Collection extends AppCompatActivity {
Button current,daily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        current=(Button)findViewById(R.id.current);
        daily=(Button)findViewById(R.id.daily);

        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent current=new Intent(Collection.this,MainIntake.class);
                startActivity(current);

            }
        });

       daily.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent daily =new Intent(Collection.this,DailyReport.class);
               startActivity(daily);
           }
       });

    }
}
