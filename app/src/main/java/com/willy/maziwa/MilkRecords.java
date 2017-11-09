package com.willy.maziwa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Editable;
import android.util.Log;

/**
 * Created by Alberto on 1/17/2016.
 */
public class MilkRecords
{
    public static final String KEY_ROWID = "_id";

    private static final String KEY_STATUS="status";
    private static final String TAG = "DBAdapter";
    public static final String KEY_QUANTITY= "quantity";
    public static final String KEY_SNO= "sno";

    private static final String DATABASE_NAME = "Maziwa";
    private static final String DATABASE_TABLE = "records";

    private static final int DATABASE_VERSION = 1;


    private static final String DATABASE_CREATE =
            "create table records (_id integer primary key autoincrement, "+ "quantity text not null, " + "sno text not null,"+"status text not null);";
    private Context context = null;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public MilkRecords(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
    public long AddRecords(Editable sno, String quantity,int status) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_QUANTITY, String.valueOf(sno));
        initialValues.put(KEY_SNO, quantity);
        initialValues.put(KEY_STATUS,status);
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


    public String[] select2() throws SQLException {
        Cursor cursor = db.rawQuery("SELECT quantity,sno FROM " + DATABASE_TABLE + " ", null);
        String[] dbresult = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                dbresult= new String[]{cursor.getString(0), cursor.getString(1)};
            } while (cursor.moveToNext());

        }
        return dbresult;
    }
    public Cursor getQuantity() throws SQLException{
        Cursor c = db.rawQuery("select sno,quantity from " + DATABASE_TABLE + "",null);

        return c;
    }
}
