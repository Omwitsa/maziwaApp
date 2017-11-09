package com.willy.maziwa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Alberto on 1/17/2016.
 */
public class DBFarmers
{
    public static final String KEY_ROWID = "_id";
    public static final String KEY_FNAME = "fname";
    public static final String KEY_ID = "id_no";
    public static final String KEY_PHONE = "phone_no";
    public static final String KEY_BBRANCH = "bbranch";
    public static final String KEY_BANK= "bank";
    public static final String KEY_ACCOUNT = "account";
    public static final String KEY_BRANCH = "branch";
    public static final String KEY_LOCATION = "location";
    private static final String TAG = "DBAdapter";
    private static final String KEY_STATUS="status";

    private static final String DATABASE_NAME = "farmers";
    private static final String DATABASE_TABLE2= "users";
    public static final String DATABASE_TABLE = "farmers";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table farmers (_id integer primary key autoincrement, "+ "fname text not null, " + "id_no text not null, " + "phone_no text not null, " + "bbranch text not null, " + "bank text not null, " + "account text not null, " + "branch text not null, " + "location text not null,"+"status text not null);";



    private Context context = null;
    public DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBFarmers(Context ctx)
    {

        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
    public long AddFarmers(String fname, String id, String phone, String bbranchv, String bankv, String accountv,String branchv,String locationv) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_FNAME, fname);
        initialValues.put(KEY_ID, id);
        initialValues.put(KEY_PHONE,phone);
        initialValues.put(KEY_BBRANCH,bbranchv);
        initialValues.put(KEY_BANK,bankv);
        initialValues.put(KEY_ACCOUNT,accountv);
        initialValues.put(KEY_BRANCH,branchv);
        initialValues.put(KEY_LOCATION,locationv);
        initialValues.put(KEY_STATUS,"0");
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
                {
            db.execSQL(DATABASE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS users");
            onCreate(db);
        }
    }


    public void open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
    }


    public void close()
    {
        DBHelper.close();
    }

    public Cursor getMembers() throws SQLException{
        Cursor c = db.rawQuery("select * from " + DATABASE_TABLE + "",null);

        return c;
    }

}
