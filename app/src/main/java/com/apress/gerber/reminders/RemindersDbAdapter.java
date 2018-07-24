package com.apress.gerber.reminders;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class RemindersDbAdapter {
    //these are the column names
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_IMPORTANT = "important";

    //these are the corresponding indices
    public static final int INDEX_ID = 0;
    public static final int INDEX_CONTENT = INDEX_ID + 1;
    public static final int INDEX_IMPORTANT = INDEX_ID + 2;

    //used for logging
    private static final String TAG = "RemindersDbAdapter";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "dba_reminders";
    private static final String TABLE_NAME = "tbl_reminders";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    //SQL statement used to create the database
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " + COL_ID +
                    " INTEGER PRIMARY KEY autoincrement, " + COL_CONTENT +
                    " TEXT, " + COL_IMPORTANT + " INTEGER );";

    private static class  DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version "+oldVersion+" to "+newVersion+", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public RemindersDbAdapter(Context ctx){
        this.mCtx = ctx;
    }

    //open
    public void open() throws SQLException{
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
    }

    //close
    public void close(){
        if(mDbHelper != null){
            mDbHelper.close();
        }
    }

    //CREATE
    //note that the id will be created for you automatically
    public void createReminder(String name, boolean important){
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, name);
        values.put(COL_IMPORTANT, important ? 1:0);
        mDb.insert(TABLE_NAME, null, values);
    }

    //overloaded to take a reminder
    public long createReminder(Reminder reminder){
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, reminder.getContent());//Contact Name
        values.put(COL_IMPORTANT, reminder.getImportant());//Contact Phone Number

        //inserting Row
        return  mDb.insert(TABLE_NAME,null, values);
    }

    //READ
    public Reminder fetchReminderById(int id){
        Cursor cursor = mDb.query(TABLE_NAME, new String[]{
                COL_ID, COL_CONTENT, COL_IMPORTANT}, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return new Reminder(
                cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_CONTENT),
                cursor.getInt(INDEX_IMPORTANT)
        );
    }

    public Cursor fetchAllReminders(){
        Cursor mCursor = mDb.query(TABLE_NAME, new String[]{
                COL_ID,COL_CONTENT, COL_IMPORTANT}, null, null, null, null, null);
        if(mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //UPDATE
    public void updateReminder(Reminder reminder){
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, reminder.getContent());
        values.put(COL_IMPORTANT, reminder.getImportant());
        mDb.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(reminder.getId())});
    }

    //DELETE
    public void deleteReminderById(int nid){
        mDb.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(nid)});
    }

    public void deleteAllReminders(){
        mDb.delete(TABLE_NAME, null, null);
    }
}
