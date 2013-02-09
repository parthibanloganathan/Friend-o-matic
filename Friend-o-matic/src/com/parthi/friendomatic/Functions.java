package com.parthi.friendomatic;

public class Functions
{
    /**
     * Add to database
     */
    public static void addData(DataAccessObject datasource, String id, String name)
    {
	    datasource.createEntry(id, name);
    }
    
    /**
     * Delete from database
     */
    public static void deleteData(DataAccessObject datasource, String id)
    {
    	datasource.deleteData(id);
    }
}