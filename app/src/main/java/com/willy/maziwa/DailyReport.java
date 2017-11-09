package com.willy.maziwa;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import Pockdata.PocketPos;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import util.DataConstant;
import util.DateUtil;
import util.FontDefine;
import util.Printer;
import util.StringUtil;
import util.Util;

public class DailyReport extends AppCompatActivity implements OnClickListener {

    public static final String ROOT_URL = "http://amtechafrica.com/";

    SQLiteDatabase db;
    private Button mConnectBtn, mEnableBtn, mPrintDemoBtn, mPrintReceiptBtn;
    private Spinner mDeviceSp;
    StringBuffer buffer;
    ProgressDialog dialog = null;
    TextView tv;

    private ProgressDialog mProgressDlg, mConnectingDlg;

    private BluetoothAdapter mBluetoothAdapter;

    private P25Connector mConnector;
    String qty1="";// =getIntent().getStringExtra("qty").toString();
    String sno1="";
    String pin1="";
    long milis1		= System.currentTimeMillis();
    String date1		= DateUtil.timeMilisToString(milis1, "dd-MM-yyyy");

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailyreport);
        db=openOrCreateDatabase("CollectionDB", Context.MODE_PRIVATE, null);
        mConnectBtn			= (Button) findViewById(R.id.btn_connectd);
        mEnableBtn			= (Button) findViewById(R.id.btn_enabled);
        mPrintDemoBtn 		= (Button) findViewById(R.id.btn_print_demod);
        mPrintReceiptBtn 	= (Button) findViewById(R.id.btn_print_receiptd);
        mDeviceSp 			= (Spinner) findViewById(R.id.sp_deviced);
        tv = (TextView)findViewById(R.id.tvd);
        qty1 ="2342";//getIntent().getStringExtra("qty").toString();
        sno1 ="234234";//getIntent().getStringExtra("sno").toString();
        pin1 ="A345455345G";// getIntent().getStringExtra("pin").toString();

        mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            showUnsupported();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                showDisabled();
            } else {
                showEnabled();

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices != null) {
                    mDeviceList.addAll(pairedDevices);
                    updateDeviceList();
                }
            }

            mProgressDlg 	= new ProgressDialog(this);
            mProgressDlg.setMessage("Scanning...");
            mProgressDlg.setCancelable(false);
            mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mBluetoothAdapter.cancelDiscovery();
                }
            });

            mConnectingDlg 	= new ProgressDialog(this);
            mConnectingDlg.setMessage("Connecting...");
            mConnectingDlg.setCancelable(false);

            mConnector 		= new P25Connector(new P25Connector.P25ConnectionListener() {
                @Override
                public void onStartConnecting() {
                    mConnectingDlg.show();
                }

                @Override
                public void onConnectionSuccess() {
                    mConnectingDlg.dismiss();
                    showConnected();
                }

                @Override
                public void onConnectionFailed(String error) {
                    mConnectingDlg.dismiss();
                }

                @Override
                public void onConnectionCancelled() {
                    mConnectingDlg.dismiss();
                }

                @Override
                public void onDisconnected() {
                    showDisonnected();
                }
            });

            //enable bluetooth
            mEnableBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 1000);
                }
            });

            //connect/disconnect
            mConnectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    connect();
                }
            });

            //print demo text
            mPrintDemoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    printDemoContent();
                }
            });


            //print struk
            mPrintReceiptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    MilkRecords dbUser = new MilkRecords(DailyReport.this);
                    dbUser.open();


                    try {
                        Cursor myContent = dbUser.getQuantity();
                        if(myContent != null)
                        {


                            while(myContent.moveToNext()){

                                String sno =myContent.getString(0) ;
                                String quantity =myContent.getString(1) ;
                                submit(sno, quantity);
                                printStruk(sno, quantity);
                            }

                        }
                        else {
                            Toast.makeText(DailyReport.this, "No records", Toast.LENGTH_LONG).show();
                        }

                        dbUser.close();
                    } catch (Exception e) {
                        Toast.makeText(DailyReport.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }


                }
            });
        }



        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(mReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main1, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_scan) {
            mBluetoothAdapter.startDiscovery();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }

        if (mConnector != null) {
            try {
                mConnector.disconnect();
            } catch (P25ConnectionException e) {
                e.printStackTrace();
            }
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);

        super.onDestroy();
    }

    private String[] getArray(ArrayList<BluetoothDevice> data) {
        String[] list = new String[0];
        if (data == null) return list;
        int size	= data.size();
        list		= new String[size];
        for (int i = 0; i < size; i++) {
            list[i] = data.get(i).getName();
        }
        return list;
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void updateDeviceList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item2, getArray(mDeviceList));
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item1);
        mDeviceSp.setAdapter(adapter);
        mDeviceSp.setSelection(0);
    }

    private void showDisabled() {
        showToast("Bluetooth disabled");
        mEnableBtn.setVisibility(View.VISIBLE);
        mConnectBtn.setVisibility(View.GONE);
        mDeviceSp.setVisibility(View.GONE);
    }

    private void showEnabled() {
        showToast("Bluetooth enabled");
        mEnableBtn.setVisibility(View.GONE);
        mConnectBtn.setVisibility(View.VISIBLE);
        mDeviceSp.setVisibility(View.VISIBLE);
    }

    private void showUnsupported() {
        showToast("Bluetooth is unsupported by this device");
        mConnectBtn.setEnabled(false);
        mPrintDemoBtn.setEnabled(false);
        mPrintReceiptBtn.setEnabled(false);
        mDeviceSp.setEnabled(false);
    }

    private void showConnected() {
        showToast("Connected");
        mConnectBtn.setText("Disconnect");
        mPrintDemoBtn.setEnabled(true);
        mPrintReceiptBtn.setEnabled(true);
        mDeviceSp.setEnabled(false);
    }

    private void showDisonnected() {
        showToast("Disconnected");
        mConnectBtn.setText("Connect");
        mPrintDemoBtn.setEnabled(false);
        mPrintReceiptBtn.setEnabled(false);
        mDeviceSp.setEnabled(true);
    }

    private void connect() {
        if (mDeviceList == null || mDeviceList.size() == 0) {
            return;
        }

        BluetoothDevice device = mDeviceList.get(mDeviceSp.getSelectedItemPosition());
        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            try {
                createBond(device);
            } catch (Exception e) {
                showToast("Failed to pair device");
                return;
            }
        }

        try {
            if (!mConnector.isConnected()) {
                mConnector.connect(device);
            } else {
                mConnector.disconnect();

                showDisonnected();
            }
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }

    private void createBond(BluetoothDevice device) throws Exception {

        try {
            Class<?> cl 	= Class.forName("android.bluetooth.BluetoothDevice");
            Class<?>[] par 	= {};
            Method method 	= cl.getMethod("createBond", par);
            method.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void sendData(byte[] bytes) {

        try {

            mConnector.sendData(bytes);
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }

    private void printStruk(String sno, String quantity) {
        try{



            buffer.append( sno+"  \t:"+quantity+" \n");


            

        showMessage("Collection Details", buffer.toString());
        Date dNow = new Date( );
        SimpleDateFormat ft =
                new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss");
        long milis1		= System.currentTimeMillis();

        String date1		= DateUtil.timeMilisToString(milis1, "MMM dd, yyyy");
        String time1		= DateUtil.timeMilisToString(milis1, "hh:mm a");

        // System.out.println("Current Date: " + ft.format(dNow));
        String titleStr	= "KABIYET DAIRY PLANT" + "\n0718798111";

        StringBuilder content2Sb	= new StringBuilder();

        content2Sb.append("-----------------------------" + "\n");
        content2Sb.append(sno+" \t:" +quantity+ "\n");
        content2Sb.append(""+buffer.toString()+"" + "\n");
		/*content2Sb.append("Date Supplied    : "+date1+"" + "\n");
		content2Sb.append("Time Supplied   : "+time1+"" + "\n");
		*/




            String qtyyy = "";




        content2Sb.append("Total : " +qtyyy +"\n");
        content2Sb.append("--------------------------" + "\n");
        content2Sb.append("Thanks for choosing Us" + "\n");
        content2Sb.append("--------------------------" + "\n");
        content2Sb.append("Designed & Developed By" + "\n");
        content2Sb.append("AMTECH TECHNOLOGIES LTD" + "\n");
        content2Sb.append("www.amtechafrica.com" + "\n");
        long milis		= System.currentTimeMillis();
        String date		= DateUtil.timeMilisToString(milis, "dd-MM-yy / HH:mm")  + "\n\n";
        byte[] titleByte	= Printer.printfont(titleStr, FontDefine.FONT_32PX,FontDefine.Align_CENTER,
                (byte)0x1A, PocketPos.LANGUAGE_ENGLISH);
        byte[] content2Byte	= Printer.printfont(content2Sb.toString(), FontDefine.FONT_32PX,FontDefine.Align_LEFT,
                (byte)0x1A, PocketPos.LANGUAGE_ENGLISH);
        byte[] dateByte		= Printer.printfont(date, FontDefine.FONT_32PX,FontDefine.Align_LEFT, (byte)0x1A,
                PocketPos.LANGUAGE_ENGLISH);
        byte[] totalByte	= new byte[titleByte.length +content2Byte.length +  dateByte.length];
        int offset = 0;
        System.arraycopy(titleByte, 0, totalByte, offset, titleByte.length);
        offset += titleByte.length;
        System.arraycopy(content2Byte, 0, totalByte, offset, content2Byte.length);
        offset += content2Byte.length;
        System.arraycopy(dateByte, 0, totalByte, offset, dateByte.length);
        byte[] senddata = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, totalByte, 0, totalByte.length);
        sendData(senddata);

    }catch (Exception e){
        Toast.makeText(DailyReport.this,e.getMessage(), Toast.LENGTH_LONG).show();
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
    private void printDemoContent(){

        /*********** print head*******/
        String receiptHead = "************************"
                + "   AMTECH RECEIPT"+"\n"
                + "************************"
                + "\n";
        long milis		= System.currentTimeMillis();
        String date		= DateUtil.timeMilisToString(milis, "MMM dd, yyyy");
        String time		= DateUtil.timeMilisToString(milis, "hh:mm a");
        String hwDevice	= Build.MANUFACTURER;
        String hwModel	= Build.MODEL;
        String osVer	= Build.VERSION.RELEASE;
        String sdkVer	= String.valueOf(Build.VERSION.SDK_INT);
        StringBuffer receiptHeadBuffer = new StringBuffer(100);
        receiptHeadBuffer.append(receiptHead);
        receiptHeadBuffer.append(Util.nameLeftValueRightJustify(date, time, DataConstant.RECEIPT_WIDTH) + "\n");
        receiptHeadBuffer.append(Util.nameLeftValueRightJustify("Device:", hwDevice, DataConstant.RECEIPT_WIDTH) + "\n");
        receiptHeadBuffer.append(Util.nameLeftValueRightJustify("Model:",  hwModel, DataConstant.RECEIPT_WIDTH) + "\n");
        receiptHeadBuffer.append(Util.nameLeftValueRightJustify("OS ver:", osVer, DataConstant.RECEIPT_WIDTH) + "\n");
        receiptHeadBuffer.append(Util.nameLeftValueRightJustify("SDK:", sdkVer, DataConstant.RECEIPT_WIDTH));
        receiptHead = receiptHeadBuffer.toString();
        byte[] header = Printer.printfont(receiptHead + "\n", FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);
        /*********** print English text*******/
        StringBuffer sb = new StringBuffer();
        for(int i=1; i<128; i++)
            sb.append((char)i);
        String content = sb.toString().trim();
        byte[] englishchartext24 			= Printer.printfont(content + "\n",FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);
        byte[] englishchartext32			= Printer.printfont(content + "\n",FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);
        byte[] englishchartext24underline	= Printer.printfont(content + "\n",FontDefine.FONT_32PX_UNDERLINE,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);
        //2D Bar Code
        byte[] barcode = StringUtil.hexStringToBytes("1d 6b 02 0d 36 39 30 31 32 33 34 35 36 37 38 39 32");


        /*********** print Tail*******/
        String receiptTail =  "Designed by Ben@Amtech Technologies" + "\n"
                + "************************" + "\n";
        String receiptWeb =  "** www.londatiga.net ** " + "\n\n\n";
        byte[] foot = Printer.printfont(receiptTail,FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);
        byte[] web	= Printer.printfont(receiptWeb,FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);
        byte[] totladata =  new byte[header.length + englishchartext24.length + englishchartext32.length + englishchartext24underline.length +
                + barcode.length
                + foot.length + web.length
                ];
        int offset = 0;
        System.arraycopy(header, 0, totladata, offset, header.length);
        offset += header.length;
        System.arraycopy(englishchartext24, 0, totladata, offset, englishchartext24.length);
        offset+= englishchartext24.length;
        System.arraycopy(englishchartext32, 0, totladata, offset, englishchartext32.length);
        offset+=englishchartext32.length;
        System.arraycopy(englishchartext24underline, 0, totladata, offset, englishchartext24underline.length);
        offset+=englishchartext24underline.length;
        System.arraycopy(barcode, 0, totladata, offset, barcode.length);
        offset+=barcode.length;
        System.arraycopy(foot, 0, totladata, offset, foot.length);
        offset+=foot.length;
        System.arraycopy(web, 0, totladata, offset, web.length);
        offset+=web.length;
        byte[] senddata = PocketPos.FramePack(PocketPos.FRAME_TOF_PRINT, totladata, 0, totladata.length);
        sendData(senddata);
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state 	= intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    showEnabled();
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    showDisabled();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<BluetoothDevice>();

                mProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mProgressDlg.dismiss();

                updateDeviceList();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mDeviceList.add(device);

                showToast("Found device " + device.getName());
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED) {
                    showToast("Paired");

                    connect();
                }
            }
        }
    };

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

    }

    private void submit(String sno, String quantity) {
        //Here we will handle the http request to insert user to mysql db
        //Creating a RestAdapter

        final ProgressDialog loading = ProgressDialog.show(this,"Submitting Data","Please wait...",false,false);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ROOT_URL) //Setting the Root URL
                .build(); //Finally building the adapter

        //Creating object for our interface
        syncAPI api = adapter.create(syncAPI.class);

        //Defining the method insertuser of our interface
        api.sync(

                //Passing the values by getting it from editTexts
                sno,
               quantity,


                //Creating an anonymous callback
                new Callback<ArrayList<SyncCall>>() {
                    @Override
                    public void success(ArrayList<SyncCall> result, Response response) {
                        loading.dismiss();

                        //String name=result.get(0).getName().toString();

                        String myString1 =result.get(0).getSno();
                        String myString2 = new String("Wrong");
                        if(myString1.equals(myString2)) {


                            Toast.makeText(DailyReport.this, "Failed to submit records ", Toast.LENGTH_LONG).show();

                        }
                        else {

                            Toast.makeText(DailyReport.this, "Submission successful", Toast.LENGTH_LONG).show();



                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        loading.dismiss();

                        //If any error occured displaying the error as toast
                        Toast.makeText(DailyReport.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }


}