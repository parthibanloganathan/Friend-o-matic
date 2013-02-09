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
  private String[] allColumns = { Database.COLUMN_NAME };

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

  public void createEntry(String entry)
  {
	  ContentValues values = new ContentValues();
	  values.put(Database.COLUMN_NAME, entry);
	  long insertId = database.insertOrThrow(Database.TABLE_NAME, null, values);
	  Cursor cursor = database.query(Database.TABLE_NAME,
		        allColumns, Database.COLUMN_NAME + " = " + insertId, null,
		        null, null, null);
	  
	 /* if(cursor.moveToFirst())
	  {
		  Data newdata = cursorToData(cursor);
		  cursor.close();
	  }*/
  }

  public void deleteData(String data)
  {
	  System.out.println("Data deleted with id: " + data);
	  database.delete(Database.TABLE_NAME, Database.COLUMN_NAME + " = '" + data + "'", null);
  }

  public List<Data> getAllData()
  {
    List<Data> data = new ArrayList<Data>();

    Cursor cursor = database.query(Database.TABLE_NAME, allColumns, null, null, null, null, null);

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
	    Cursor cursor = database.query(Database.TABLE_NAME, allColumns, null, null, null, null, null);

	    boolean a = cursor.moveToFirst();
	    System.out.println("cursor: " + a);
	    
	    Data data = cursorToData(cursor);
	 
	    // Make sure to close the cursor
	    cursor.close();
	    return data;
  }
  
  public int getSize()
  {
	  Cursor cursor = database.rawQuery("SELECT Count(*) from " + Database.TABLE_NAME, null);
	  
	  cursor.moveToFirst();
	  
	  int size = cursor.getInt(0);
	  
	  return size;
  }
  
  private Data cursorToData(Cursor cursor)
  {
	  Data data = new Data();
      data.setFriendID(cursor.getString(0));
      
      return data;
  }
} 