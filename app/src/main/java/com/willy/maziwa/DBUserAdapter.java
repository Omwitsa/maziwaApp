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
public class DBUserAdapter
{
    public static final String KEY_ROWID = "_id";
    public static final String KEY_USERNAME= "username";
    public static final String KEY_PASSWORD = "password";
    private static final String KEY_STATUS="status";
    private static final String TAG = "DBAdapter";

    private static final String DATABASE_NAME = "usersdb";
    private static final String DATABASE_TABLE = "users";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table users (_id integer primary key autoincrement, "+ "username text not null, " + "password text not null,"+"status text not null);";


    private Context context = null;
    public DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBUserAdapter(Context ctx)
    {

        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
    public long AddUserAdmin(String user, String pass) {
    ContentValues initialValues = new ContentValues();
    initialValues.put(KEY_USERNAME, user);
    initialValues.put(KEY_PASSWORD, pass);
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

    public boolean Login(String username, String password) throws SQLException
    {
        Cursor mCursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE username=? AND password=? AND status='0'", new String[]{username,password});
        if (mCursor != null && mCursor.getCount()>0 ) {
            //ContentValues newValues=new ContentValues();
            //newValues.put("status", "1");
            //int update=db.update("" + DATABASE_TABLE + "", newValues, "username='" + username + "'AND password='"+password+"'" , null);
            //if(update>0)
            //{
                return true;
            //}
        }
        return false;
    }

}
