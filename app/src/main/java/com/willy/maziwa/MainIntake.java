package com.willy.maziwa;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Alberto on 1/21/2016.
 */
public class MainIntake extends AppCompatActivity {

    ImageView logo;
    Button btnSetting;
    Button btnSearchDevice;
    Button btnMonitor;
    Button btnAbout;
    Button btnExit;

    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if (_bluetooth == null) {
            showUnsupported();
        }
        logo = (ImageView) findViewById(R.id.imageView1);
        logo.setClickable(true);

        logo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse(getResources().getString(R.string.uri));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                    Log.e("ACTION_VIEW", e.getMessage());
                }
            }
        });

        btnSetting = (Button) findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Intent intent = new Intent();
                //intent.setClass(MainIntake.this, SettingActivity.class);
               // startActivity(intent);
            }
        });

        btnSearchDevice = (Button) findViewById(R.id.btnSearchDevice);
        btnSearchDevice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainIntake.this, SearchDeviceActivity.class);
                startActivity(intent);
            }
        });

        btnMonitor = (Button) findViewById(R.id.btnMonitor);
        btnMonitor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                _bluetooth.enable();
                Intent intent = new Intent();
               intent.setClass(MainIntake.this, MonitorActivity.class);
                startActivity(intent);
            }
        });

        btnAbout = (Button) findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Intent intent = new Intent();
                //intent.setClass(MainIntake.this, AboutActivity.class);
                //startActivity(intent);
            }
        });

        btnExit = (Button) findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });

        btnSearchDevice = (Button) findViewById(R.id.btnSearchDevice);
        btnSearchDevice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainIntake.this, SearchDeviceActivity.class);
                startActivity(intent);
            }
        });

        btnMonitor = (Button) findViewById(R.id.btnMonitor);
        btnMonitor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                _bluetooth.enable();
                Intent intent = new Intent();
                intent.setClass(MainIntake.this, MonitorActivity.class);
                startActivity(intent);
            }
        });


        btnAbout = (Button) findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Intent intent = new Intent();
               //intent.setClass(MainIntake.this, AboutActivity.class);
               // startActivity(intent);
            }
        });

       /* btnExit = (Button) findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });*/
    }
    private void showDisabled() {
        showToast("Bluetooth disabled");

    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void showUnsupported() {
        showToast("Bluetooth is unsupported by this device");

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            dialog();
            return false;
        }
        return false;
    }
    protected void dialog() {
        AlertDialog.Builder build = new AlertDialog.Builder(MainIntake.this);
        build.setTitle(R.string.message);
        build.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (_bluetooth.isEnabled()) {
                            _bluetooth.disable();
                        }
                        Controller app = new Controller();
                        app.setDevice(null);
                        MainIntake.this.finish();
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
