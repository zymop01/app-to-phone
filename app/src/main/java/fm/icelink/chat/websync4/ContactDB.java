package fm.icelink.chat.websync4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactDB extends SQLiteOpenHelper {

    public static final String CONTACTS_TABLE = "CONTACTS_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_CONTACT = "CONTACT";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_RINGTONE = "RINGTONE";

    public static final String HISTORY_TABLE = "HISTORY_TABLE";
    public static final String MISSING_CALLS_TABLE = "MISSING_CALLS_TABLE";
    public static final String NAME = "NAME";
    public static final String MSG = "MESSAGE";
    //public static final String COLUMN_ID = "ID";
    //public static final String COLUMN_CONTACT = "CONTACT";

    public ContactDB(@Nullable Context context) {
        super(context, "contacts.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + CONTACTS_TABLE +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CONTACT + " TEXT, " + COLUMN_RINGTONE + " INTEGER, " +
                COLUMN_NAME + " TEXT, " +
                "unique ("+COLUMN_CONTACT+"))";
        String createHistoryTableStatement = "CREATE TABLE " + HISTORY_TABLE +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CONTACT + " TEXT, " + COLUMN_NAME + " TEXT, " +
                COLUMN_RINGTONE + " INTEGER )";
        String createMissingCallsTableStatement = "CREATE TABLE " + MISSING_CALLS_TABLE +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CONTACT + " TEXT, " + COLUMN_NAME + " TEXT, " +
                COLUMN_RINGTONE + " INTEGER )";
        db.execSQL(createTableStatement);
        db.execSQL(createHistoryTableStatement);
        db.execSQL(createMissingCallsTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean createMsgTable(String tableName) {
        final SQLiteDatabase db = getWritableDatabase();

        Cursor c = null;
        boolean tableExists = false;
        /* get cursor on it */
        try
        {
            c = db.query(tableName, null,
                    null, null, null, null, null);
            tableExists = true;
            c.close();
        }
        catch (Exception e) {
            /* fail */
        }
        String str = "";
        if (!tableExists) {
            String createTableStatement = "CREATE TABLE " + tableName +
                    " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MSG + " TEXT)";
            db.execSQL(createTableStatement);
            str = "Table: " + tableName + " created";
        }
        else {
            str = "Table: " + tableName + " already exists";
        }


        //final SQLiteDatabase db = getWritableDatabase();
        //String CREATE_TABLE_NEW_USER = "CREATE TABLE " + user + " (" + UserInfo.NOTES + " TEXT)";

        //db.close();
        return !tableExists;
    }

    public boolean addMsg(String tableName, String msg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MSG, msg);
        long insert = db.insert(tableName, null, cv);
        if (insert == -1) return false;
        return true;
    }

    public List<String> displayMsg(String tableName) {
        List<String> returnList = new ArrayList<>();
        Log.d("TableName+", tableName);
        //get data from the database
        String queryString = "SELECT * FROM " + tableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            //loop through the cursor (result set) and create new customer objects. Put them into the return list.
            do {
                String msg = cursor.getString(1);
                returnList.add(msg);

            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Collections.reverse(returnList);
        return returnList;
    }

    public boolean addOne(@NonNull Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        //Cursor cursor = db.rawQuery("SELECT * FROM " + COLUMN_CONTACT + " WHERE " +
                //COLUMN_CONTACT + " = ?", new String[] {contacts.getContact()});
        long insert = -1;
        cv.put(COLUMN_CONTACT, contacts.getContact());
        cv.put(COLUMN_RINGTONE, contacts.getRingtone());

        try {
            insert = db.insert(CONTACTS_TABLE, null, cv);
        }
        catch (Exception e) {

        }
        /*
        if (cursor.moveToFirst()) {
        }
        else {

        }

         */
        if (insert == -1) return false;
        return true;
    }

    public boolean addOneToHistory(@NonNull Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_CONTACT, contacts.getContact());
        cv.put(COLUMN_RINGTONE, contacts.getRingtone());
        long insert = db.insert(HISTORY_TABLE, null, cv);
        if (insert == -1) return false;
        return true;
    }

    public boolean addOneToMissingCalls(@NonNull Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_CONTACT, contacts.getContact());
        cv.put(COLUMN_RINGTONE, contacts.getRingtone());
        long insert = db.insert(MISSING_CALLS_TABLE, null, cv);
        if (insert == -1) return false;
        return true;
    }

    public List<Contacts> getEveryone() {
        List<Contacts> returnList = new ArrayList<>();
        //get data from the database
        String queryString = "SELECT * FROM " + CONTACTS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            //loop through the cursor (result set) and create new customer objects. Put them into the return list.
            do {
                int ID = cursor.getInt(0);
                String contact = cursor.getString(1);
                int ringtone = cursor.getInt(2);
                Contacts newContact = new Contacts(ID, contact, ringtone);
                returnList.add(newContact);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        int listSize = returnList.size();
        return returnList;
    }

    public List<String> getEveryRingtone() {
        List<String> returnList = new ArrayList<>();
        //get data from the database
        String queryString = "SELECT * FROM " + CONTACTS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            //loop through the cursor (result set) and create new customer objects. Put them into the return list.
            do {
                //int ID = cursor.getInt(0);
                //String contact = cursor.getString(1);
                int ringtone = cursor.getInt(2);
                //Contacts newContact = new Contacts(ID, contact, ringtone);
                if (ringtone == 1) {
                    returnList.add("By the seaside");
                } else if (ringtone == 2) {
                    returnList.add("playtime");
                } else {
                    returnList.add("silk");
                }
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        int listSize = returnList.size();
        return returnList;
    }



    public List<Contacts> getEveryoneInHistory() {
        List<Contacts> returnList = new ArrayList<>();
        //get data from the database
        String queryString = "SELECT * FROM " + HISTORY_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            //loop through the cursor (result set) and create new customer objects. Put them into the return list.
            do {
                int ID = cursor.getInt(0);
                String contact = cursor.getString(1);
                int ringtone = cursor.getInt(2);
                Contacts newContact = new Contacts(ID, contact, ringtone);
                returnList.add(newContact);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        int listSize = returnList.size();
        return returnList;
    }

    public List<Contacts> getEveryoneInMissingCalls() {
        List<Contacts> returnList = new ArrayList<>();
        //get data from the database
        String queryString = "SELECT * FROM " + MISSING_CALLS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            //loop through the cursor (result set) and create new customer objects. Put them into the return list.
            do {
                int ID = cursor.getInt(0);
                String contact = cursor.getString(1);
                int ringtone = cursor.getInt(2);
                Contacts newContact = new Contacts(ID, contact, ringtone);
                returnList.add(newContact);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        int listSize = returnList.size();
        return returnList;
    }


    public boolean deleteOne(@NonNull Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + CONTACTS_TABLE + " WHERE " + COLUMN_ID + " = " + contacts.getId();

        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public void deleteTable(String str) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS '" + str + "'");
    }



    public boolean deleteHistory(@NonNull Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + HISTORY_TABLE + " WHERE " + COLUMN_ID + " = " + contacts.getId();

        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public boolean deleteMissingCall(@NonNull Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + MISSING_CALLS_TABLE + " WHERE " + COLUMN_ID + " = " + contacts.getId();

        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public int getRingtoneById(@NonNull String str) {
        //String queryString = "select * FROM " + CONTACTS_TABLE + " WHERE " + COLUMN_CONTACT + " like '%str%'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CONTACTS_TABLE, new String[] { COLUMN_ID,
                        COLUMN_CONTACT, COLUMN_RINGTONE }, COLUMN_CONTACT + "=?",
                new String[] { str }, null, null, null, null);

        //Cursor cursor = db.rawQuery(queryString, null);
        int ringtone = 1;

        if (cursor.moveToFirst()) {
             ringtone = cursor.getInt(2);
        }
        android.util.Log.d("pppp+", Integer.toString(ringtone));

        return ringtone;
    }


    public boolean updateRingtone(@NonNull Contacts contacts, int i) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CONTACT, contacts.getContact());
        cv.put(COLUMN_RINGTONE, i);
        int update = db.update(CONTACTS_TABLE, cv, COLUMN_ID + "=" + contacts.getId(), null);
        return  update > 0;
    }
}
