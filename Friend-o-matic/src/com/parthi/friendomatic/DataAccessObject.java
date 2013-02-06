package com.parthi.friendomatic;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataAccessObject {

  // Database fields
  private SQLiteDatabase database;
  private Database dbHelper;
  private String[] allColumns = { Database.COLUMN_ID,
		  Database.COLUMN_COMMENT };

  public DataAccessObject(Context context) {
    dbHelper = new Database(context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public Data createData(String data)
  {
    ContentValues values = new ContentValues();
    values.put(Database.COLUMN_COMMENT, data);
    long insertId = database.insert(Database.TABLE_COMMENTS, null,
        values);
    Cursor cursor = database.query(Database.TABLE_COMMENTS,
        allColumns, Database.COLUMN_ID + " = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    Data newdata = cursorToData(cursor);
    cursor.close();
    return newdata;
  }

  public void deleteData(String id)
  {
    System.out.println("Data deleted with id: " + id);
    database.delete(Database.TABLE_COMMENTS, Database.COLUMN_ID
        + " = " + id, null);
  }

  public List<Data> getAllData()
  {
    List<Data> data = new ArrayList<Data>();

    Cursor cursor = database.query(Database.TABLE_COMMENTS,
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while(!cursor.isAfterLast())
    {
    	Data comment = cursorToData(cursor);
    	data.add(comment);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return data;
  }

  private Data cursorToData(Cursor cursor)
  {
	  Data data = new Data();
      data.setFriendID(cursor.getString(0));
      
      return data;
  }
} 