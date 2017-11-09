package com.willy.maziwa;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.DateUtil;

/**
 * Created by Alberto on 1/21/2016.
 */
public class MonitorActivity extends AppCompatActivity {

    private static final int REQUEST_DISCOVERY = 0x1;
    private static final String CollectionDB = null;
    private final String TAG = "MonitorActivity";
    private Handler _handler = new Handler();
    private final int maxlength = 28;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device = null;
    private BluetoothSocket socket = null;

    private EditText TextView, sTextView, sno, pin;

    private OutputStream outputStream;
    private InputStream inputStream;

    ProgressDialog dialog = null;
    private Spinner spinner11, spinner21;
    String[] iplTeam= {"AM", "PM1", "PM2" };

    Button btnCalendar, btnTimePicker,send;
    SQLiteDatabase db, db1;
    private Object obj1 = new Object();
    private Object obj2 = new Object();
    public static boolean canRead = true;

    AppStatus appstatus = new AppStatus();

    public static StringBuffer hexString = new StringBuffer();
    ScrollView mScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.monitor);
        sTextView = (EditText) findViewById(R.id.sTextView);
        TextView = (EditText) findViewById(R.id.TextView);
        sno = (EditText) findViewById(R.id.sno);

        spinner11=(Spinner) findViewById(R.id.spinner1);
       // spinner11.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
       // ArrayAdapter adapter=new ArrayAdapter<String>(MonitorActivity.this, android.R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

       MilkRecords db=new MilkRecords(MonitorActivity.this);
       db.open();
        Button back =(Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), MainIntake.class);
                startActivity(i);
                MonitorActivity.this.finish();
            }
        });

        Button connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to Register screen
                connect(device);
            }
        });
        mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice finalDevice = this.getIntent().getParcelableExtra(
                BluetoothDevice.EXTRA_DEVICE);
       Controller app = new Controller();
        device = app.getDevice();
        Log.d(TAG, "test1");
        if (finalDevice == null) {
            if (device == null) {
                Log.d(TAG, "test2");
                Intent intent = new Intent(this, SearchDeviceActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            Log.d(TAG, "test4");
        } else if (finalDevice != null) {
            Log.d(TAG, "test3");
            app.setDevice(finalDevice);
            device = app.getDevice();
        }
        new Thread() {
            public void run() {
                connect(device);
            };
        }.start();


    }
    public void onButtonClickclear(View view) throws IOException {
        hexString = new StringBuffer();
        sTextView.setText(hexString.toString());
    }

    /* after select, connect to device */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_DISCOVERY) {
            finish();
            return;
        }
        if (resultCode != RESULT_OK) {
            finish();
            return;
        }
        final BluetoothDevice device = data
                .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        new Thread() {
            public void run() {
                connect(device);
            };
        }.start();
    }
    protected void connect(BluetoothDevice device) {
        try {
            Log.d(TAG, "Searching");
            // Create a Socket connection: need the server's UUID number of
            // registered
            Method m = device.getClass().getMethod("createRfcommSocket",
                    new Class[] { int.class });
            socket = (BluetoothSocket) m.invoke(device, 1);
            socket.connect();
            Log.d(TAG, ">>Client connectted");
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            int read = -1;
            final byte[] bytes = new byte[2048];
            while (true) {
                synchronized (obj1) {
                    read = inputStream.read(bytes);
                    Log.d(TAG, "read:" + read);
                    if (read > 0) {
                        final int count = read;
                        String str = SamplesUtils.byteToHex(bytes, count);
//						Log.d(TAG, "test1:" + str);

                        String hex = hexString.toString();
                        if (hex == "") {
                            hexString.append(",");
                        } else {
                            if (hex.lastIndexOf("GS,") < hex.lastIndexOf(" ")) {
                                hexString.append("\n, ");
                            }
                        }
                        hexString.append(str);
                        hex = hexString.toString();
//						Log.d(TAG, "test2:" + hex);
                        if (hex.length() > maxlength) {
                            try {
                                hex = hex.substring(hex.length() - maxlength,
                                        hex.length());
                                hex = hex.substring(hex.indexOf(" "));
                                hex = "GS," + hex;
                                hexString = new StringBuffer();
                                hexString.append(hex);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "e", e);
                            }
                        }
                        _handler.post(new Runnable() {
                            public void run() {

									/*sTextView
											.setText(bufferStrToHex(
													hexString.toString(), false)
													.trim());*/
                                String txt=sTextView.toString();
                                String value = txt;
                                //String TextView1 = value.substring(value.lastIndexOf('g')+1);
                                String bb = bufferStrToHex(hexString.toString(), false).trim().replaceAll("[^0-9.]", "");
                                String tx=bb.toString();
                                //String TextView1 = tx.substring(tx.lastIndexOf(',')+1);
                                TextView.setText(bb.toString());
                            }

                        });

                    }
                    //TextView.setText(sTextView.getText().toString().replaceAll("[^0-9.]", ""));
                    //value.SubString(value.LastIndexOf('g')+1)
                }
                send = (Button) findViewById(R.id.save);
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //String text=TextView.getText().toString();
                        //String snno=sno.getText().toString();
                        if(TextView.getText().toString().trim().length()==0||
                                sno.getText().toString().trim().length()==0)
                        {
                            showMessage("Error", "Please enter all values");
                            return;
                        }else {
                            dialog();
                        }
                    }
                });
               /* btnRegister.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // Switching to Register screen
                        if(TextView.getText().toString().trim().length()==0||
                                sno.getText().toString().trim().length()==0)
                        {
                            showMessage("Error", "Please enter all values");
                            return;
                        }
                        dialog();
		            		/*db.execSQL("INSERT INTO CollectionDB VALUES('"+sno.getText()+"','"+TextView.getText()+"','"+pin.getText()+
		            				   "','0');");*/
		            		//showMessage("Success", "Record added");
		            		//Intent i = new Intent(getApplicationContext(), MainPrintActivity.class);
			                //i.putExtra("qty", TextView.getText().toString());
			               // i.putExtra("sno", sno.getText().toString());
			               // i.putExtra("pin", pin.getText().toString());
			                //startActivity(i);

                    //}

               // });*/
            }

        } catch (Exception e) {
            Log.e(TAG, ">>", e);
            Toast.makeText(getBaseContext(),
                    getResources().getString(R.string.ioexception),
                    Toast.LENGTH_SHORT).show();
            return;
        } finally {
            if (socket != null) {
                try {
                    Log.d(TAG, ">>Client Socket Close");
                    socket.close();
                    socket = null;
                    // this.finish();
                    return;
                } catch (IOException e) {
                    Log.e(TAG, ">>", e);
                }
            }
        }
    }

    public void showMessage(String title,String message)
    {
        Builder builder=new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public String bufferStrToHex(String buffer, boolean flag) {
        String all = buffer;
        StringBuffer sb = new StringBuffer();
        String[] ones = all.split("GS, ");
        for (int i = 0; i < ones.length; i++) {
            if (ones[i] != "") {
                String[] twos = ones[i].split("  ");
                for (int j = 0; j < twos.length; j++) {
                    if (twos[j] != "") {
                        if (flag) {
                            sb.append(SamplesUtils.stringToHex(twos[j]));
                        } else {
                            sb.append(SamplesUtils.hexToString(twos[j]));
                        }
                        if (j != twos.length - 1) {
                            if (sb.toString() != "") {
                                sb.append("\n");
                            }
                            sb.append("GS, ");
                        }
                    }
                }
                if (i != ones.length - 1) {
                    if (sb.toString() != "") {
                        sb.append("\n");
                    }
                    sb.append(" ");
                }
            }
        }
        return sb.toString();
    }
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, ">>", e);
        }
    }
    protected void dialog() {

        AlertDialog.Builder build = new AlertDialog.Builder(MonitorActivity.this);
        build.setTitle("Confirmation :SNo="+sno.getText()+" and Quantity ="+TextView.getText().toString()+"");
        //final String cont= String.valueOf(spinner11.getSelectedItem());
        //final String sess= String.valueOf(spinner21.getSelectedItem());
        build.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String user=null;

                        StringBuffer buffer=new StringBuffer();

                        long milis1		= System.currentTimeMillis();

                        String date1		= DateUtil.timeMilisToString(milis1, "dd-MM-yyyy");

                         Editable sno1=sno.getText();
                        String quantity=TextView.getText().toString();
                        DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
                        //get current date time with Date()
                        Date date = new Date ();
                        MilkRecords dbUser = new MilkRecords(MonitorActivity.this);
                       dbUser.open();

                        dbUser.AddRecords(sno1, quantity,0);

                        showMessage("Success", "Record added");
                        Intent i = new Intent(getApplicationContext(), MainPrintActivity.class);
                        i.putExtra("qty", TextView.getText().toString());
                        i.putExtra("sno", sno.getText().toString());
                        startActivity(i);sno.setText("");
                        //MonitorActivity.this.finish();
                    }
                });



        build.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        build.create().show();
    }


}
