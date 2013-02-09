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
  private String[] allColumns = { Database.COLUMN_ID, Database.COLUMN_NAME };

  public DataAccessObject(Context context)
  {
	  dbHelper = new Database(context);
  }

  public void open() throws SQLException
  {
	  database = dbHelper.getWritableDatabase();
  }

  public void close()
  {
	  dbHelper.close();
  }

  public void createEntry(String id, String name)
  {
	  ContentValues values = new ContentValues();
	  values.put(Database.COLUMN_ID, id);
	  values.put(Database.COLUMN_NAME, name);
	  long insertId = database.insertOrThrow(Database.TABLE, null, values);
	  Cursor cursor = database.query(Database.TABLE, allColumns, Database.COLUMN_NAME + " = " + insertId, null, null, null, null);
	  cursor.close();
  }

  public void deleteData(String id)
  {
	  database.delete(Database.TABLE, Database.COLUMN_ID + "='" + id +"'", null);
  }

  public List<Data> getAllData()
  {
    List<Data> data = new ArrayList<Data>();

    Cursor cursor = database.query(Database.TABLE, allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    
    while(!cursor.isAfterLast())
    {
    	Data entry = cursorToData(cursor);
    	data.add(entry);
    	cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return data;
  }

  public Data getFirstEntry()
  {
	    Cursor cursor = database.query(Database.TABLE, allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    
	    Data data = cursorToData(cursor);
	 
	    // Make sure to close the cursor
	    cursor.close();
	    return data;
  }
  
  public int getSize()
  {
	  Cursor cursor = database.rawQuery("SELECT Count(*) from " + Database.TABLE, null);
	  
	  cursor.moveToFirst();
	  
	  int size = cursor.getInt(0);
	  
	  return size;
  }
  
  private Data cursorToData(Cursor cursor)
  {
	  Data data = new Data();
      data.setFriendID(cursor.getString(0));
      data.setFriendName(cursor.getString(1));
      
      return data;
  }
} 