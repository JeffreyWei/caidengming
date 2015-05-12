package com.wj.caidengmi2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SqlHelper {
	public  static final String DB_NAME = "data/data/com.game.zhongqiuguess/databases/dengmi.db";

	public void Insert(Context context,String table, ContentValues values){
		SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(DB_NAME, null); 
		try{
			db.insert(table, null, values);
		}catch(Exception e){
				e.getStackTrace();
		}
		db.close();
}
	
	public void CreateTable(Context context,String table){
		SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase( 
				DB_NAME, null); 
		String sql="CREATE TABLE " + table + " ( id text not null, type text not null, score text not null);";
		try{
			db.execSQL(sql);
		}catch(Exception e){
			e.getStackTrace();
		}
		db.close();
	}
	
	public void Update(Context context,String table, ContentValues values, String whereClause, String[] whereArgs){
		SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(DB_NAME, null); 
		try{
			db.update(table, values, whereClause, whereArgs); 
		}catch(Exception e){
			e.getStackTrace();
		}
		db.close();
	}
	
	public Cursor Query(Context context,String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
		SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(DB_NAME, null); 
		Cursor cursor = null ;
		try{
			cursor=db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
			Log.i("wordroid=", "query");
			Log.i("countofcursor=",""+cursor.getCount());
		}catch(Exception e){
			e.getStackTrace();
		}
		db.close();
		return cursor;
		
	}
	
	public void Delete(Context context,String table, String whereClause, String[] whereArgs){
		SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase( 
				DB_NAME, null); 
		try{
			db.delete(table, whereClause, whereArgs);
			Log.i("wordroid=", "delete");
			}
			catch(Exception e){
				e.getStackTrace();
			}
		db.close();
	}
	
	public void DeleteTable(Context context,String table){

		SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase( 
				DB_NAME, null); 
		String sql="drop table " + table;
		try{
		db.execSQL(sql);
		Log.i("wordroid=", sql);
		}
		catch(Exception e){
			e.getStackTrace();
		}
		db.close();
	}
}
