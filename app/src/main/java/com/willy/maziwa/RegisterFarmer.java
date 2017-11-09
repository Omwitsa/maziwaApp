package com.willy.maziwa;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Alberto on 1/15/2016.
 */
public class RegisterFarmer extends AppCompatActivity{
    private DBUserAdapter dbUser;
    private SQLiteDatabase database;
    public static final String ROOT_URL = "http://amtechafrica.com/";

    EditText fullname,id_no, phone_no,bbranch,bank,account, branch,location;
    Button register,sync;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
        fullname=(EditText)findViewById(R.id.fname);
        id_no=(EditText)findViewById(R.id.id_no);
        phone_no=(EditText)findViewById(R.id.phone_no);
        bbranch=(EditText)findViewById(R.id.bbranch);
        bank=(EditText)findViewById(R.id.bank);
        account=(EditText)findViewById(R.id.account);
        branch=(EditText)findViewById(R.id.branch);
        location=(EditText)findViewById(R.id.location);
        sync=(Button)findViewById(R.id.sync);

        register=(Button)findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (
                            fullname.getText().toString().length() > 0
                                    && id_no.getText().toString().length() > 0
                                    && phone_no.getText().toString().length() > 0
                                    && bbranch.getText().toString().length() > 0
                                    && bank.getText().toString().length() > 0
                                    && account.getText().toString().length() > 0
                                    && branch.getText().toString().length() > 0
                                    && location.getText().toString().length() > 0
                            ) {
                        String fname = fullname.getText().toString();
                        String id = id_no.getText().toString();
                        String phone = phone_no.getText().toString();
                        String bbranchv = bbranch.getText().toString();
                        String bankv = bank.getText().toString();
                        String accountv = account.getText().toString();
                        String branchv = branch.getText().toString();
                        String locationv = location.getText().toString();

                        DBFarmers dbUser=new DBFarmers(RegisterFarmer.this);
                        dbUser.open();
                        dbUser.AddFarmers(fname,id,phone,bbranchv,bankv,accountv,branchv,locationv);

                           Toast.makeText(RegisterFarmer.this, "Farmer successfully added", Toast.LENGTH_LONG).show();

                        dbUser.close();
                    } else {

                        Toast.makeText(RegisterFarmer.this, "Fill all the required details", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(RegisterFarmer.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DBUserAdapter dbUser = new DBUserAdapter(RegisterFarmer.this);


                    DBFarmers dbUser = new DBFarmers(RegisterFarmer.this);
                    dbUser.open();


                try {
                    Cursor myContent = dbUser.getMembers();
                        if(myContent != null)
                        {


                            while(myContent.moveToNext()){

                                String fname =myContent.getString(1) ;
                                String id_no =myContent.getString(2) ;
                                String phone_no =myContent.getString(3) ;
                                String bbranch =myContent.getString(4) ;
                                String bank = myContent.getString(5);
                                String account =myContent.getString(6) ;
                                String branch = myContent.getString(7);
                                String location =myContent.getString(8) ;
                                register(fname, id_no, phone_no, bbranch, bank, account, branch, location);

                            }

                        }
                    else {
                            Toast.makeText(RegisterFarmer.this, "No records", Toast.LENGTH_LONG).show();
                        }

                    dbUser.close();
                } catch (Exception e) {
                    Toast.makeText(RegisterFarmer.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });



    }



    private void register(String fname, String id_no, String phone_no, String bbranch, String bank, String account, String branch, String location) {
        //Here we will handle the http request to insert user to mysql db
        //Creating a RestAdapter

        final ProgressDialog loading = ProgressDialog.show(this,"Submitting Data","Please wait...",false,false);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ROOT_URL) //Setting the Root URL
                .build(); //Finally building the adapter

        //Creating object for our interface
        RegisterAPI api = adapter.create(RegisterAPI.class);

        //Defining the method insertuser of our interface
        api.loginUser(

                //Passing the values by getting it from editTexts
                fname,
                id_no,
                phone_no,
                bbranch,
                bank,
                account,
                branch,
                location,

                //Creating an anonymous callback
                new Callback<ArrayList<User>>() {
                    @Override
                    public void success(ArrayList<User> result, Response response) {
                        loading.dismiss();

                        //String name=result.get(0).getName().toString();

                        String myString1 =result.get(0).getFullname();
                        String myString2 = new String("Failed");
                        if(myString1.equals(myString2)) {


                            Toast.makeText(RegisterFarmer.this, "Failed to register farmer ", Toast.LENGTH_LONG).show();

                        }
                        else {

                            Toast.makeText(RegisterFarmer.this, "Registration  successful", Toast.LENGTH_LONG).show();



                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        loading.dismiss();

                        //If any error occured displaying the error as toast
                        Toast.makeText(RegisterFarmer.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
