package fm.icelink.chat.websync4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IdDB extends SQLiteOpenHelper {

    public static final String ID_TABLE = "ID_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_CONTACT = "CONTACT";
    

    public IdDB(@Nullable Context context) {
        super(context, "currentID.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + ID_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CONTACT + " TEXT, unique ("+COLUMN_CONTACT+"))";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ID_TABLE, "1", null);
        ContentValues cv = new ContentValues();
        Log.d("ppppp+",contacts.getContact());
        cv.put(COLUMN_CONTACT, contacts.getContact());
        long insert = db.insert(ID_TABLE, null, cv);
        if (insert == -1) return false;
        return true;
    }

    public List<Contacts> getEveryone() {
        List<Contacts> returnList = new ArrayList<>();
        //get data from the database
        String queryString = "SELECT * FROM " + ID_TABLE;
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
        //int listSize = returnList.size();
        return returnList;
    }

    public String getId() {
        String res = "";
        String queryString = "SELECT * FROM " + ID_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            res = cursor.getString(1);
        }
        cursor.close();
        //db.close();
        return res;
    }

    public boolean isEmpty() {
        String queryString = "SELECT * FROM " + ID_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.getCount() == 0)
            return true;
        else
            return false;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(ID_TABLE, "1", null);
    }
}
