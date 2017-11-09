package com.willy.maziwa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    SQLiteDatabase db;
    Button login;
    DBUserAdapter DB;

    ProgressDialog dialog = null;
    EditText username1,password1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username1=(EditText)findViewById(R.id.username);
        password1=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.buttonLogin);



       login.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String user="Wilson";
               String pass="admin123";




               String username =username1.getText().toString();
               String password = password1.getText().toString();
               try{
                   if(username.length() > 0 && password.length() >0)
                   {
                       DBUserAdapter dbUser = new DBUserAdapter(MainActivity.this);
                       dbUser.open();

                       dbUser.AddUserAdmin(user,pass);

                       if(dbUser.Login(username, password))
                       {
                           Toast.makeText(MainActivity.this,"Successfully Logged In", Toast.LENGTH_LONG).show();
                           Intent menu=new Intent(MainActivity.this,MenuActivity.class);
                           startActivity(menu);
                       }else{
                           Toast.makeText(MainActivity.this, "Invalid Username/Password", Toast.LENGTH_LONG).show();
                       }
                       dbUser.close();
                   }
                   else {

                       Toast.makeText(MainActivity.this, "Fill all the required details", Toast.LENGTH_LONG).show();
                   }

               }catch(Exception e)
               {
                   Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
               }

           }
       });



    }

    private void login() {

}

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onClick(View v) {

    }
}
