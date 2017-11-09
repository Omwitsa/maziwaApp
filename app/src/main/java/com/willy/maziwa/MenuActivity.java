package com.willy.maziwa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Alberto on 1/15/2016.
 */
public class MenuActivity  extends AppCompatActivity {

    Button register,collection,settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register=(Button)findViewById(R.id.register);
        collection=(Button)findViewById(R.id.collection);
        settings=(Button)findViewById(R.id.settings);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register=new Intent(MenuActivity.this,RegisterFarmer.class);
                startActivity(register);
            }
        });

        collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent collection = new Intent(MenuActivity.this, Collection.class);
                startActivity(collection);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(MenuActivity.this, Settings.class);
                startActivity(settings);
            }
        });


    }
}
