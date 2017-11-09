package com.willy.maziwa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Alberto on 1/15/2016.
 */
public class Settings extends AppCompatActivity {
    Button add;
    EditText username1,password1,pass1,key1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        add=(Button)findViewById(R.id.add);
        username1=(EditText)findViewById(R.id.username);
        pass1=(EditText)findViewById(R.id.pass2);
        password1=(EditText)findViewById(R.id.password);
        key1=(EditText)findViewById(R.id.key);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                String adminkey="Admin.123.albert";
                String username =username1.getText().toString();
                String password = password1.getText().toString();
                String pass2=pass1.getText().toString();
                String key=key1.getText().toString();

                try{
                    if(pass2.equals(password)) {
                        if(adminkey.equals(key)) {
                            if (username.length() > 0 && password.length() > 0 && pass2.length() > 0 && key.length() > 0) {
                                DBUserAdapter dbUser = new DBUserAdapter(Settings.this);
                                dbUser.open();

                                dbUser.AddUserAdmin(username, password);
                                dbUser.close();
                                Toast.makeText(Settings.this, "User successfully added", Toast.LENGTH_LONG).show();
                                Intent login=new Intent(Settings.this,MainActivity.class);
                                startActivity(login);
                            } else {

                                Toast.makeText(Settings.this, "Fill all the required details", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(Settings.this, "Wrong admin key", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {

                        Toast.makeText(Settings.this, "Password does not match", Toast.LENGTH_LONG).show();
                    }
                }catch(Exception e)
                {
                    Toast.makeText(Settings.this,e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}
