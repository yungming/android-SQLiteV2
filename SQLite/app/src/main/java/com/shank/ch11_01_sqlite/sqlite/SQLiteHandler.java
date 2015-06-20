package com.shank.ch11_01_sqlite.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = SQLiteHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	static final String DB_NAME = "MyPhoneDBII";  //資料庫名稱
	static final String TB_NAME = "MyPhoneTBII";  //資料表名稱

	public SQLiteHandler(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOGIN_TABLE = "CREATE TABLE IF NOT EXISTS " + TB_NAME
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "name TEXT, "
				+ "phone TEXT, "
				+"img TEXT, "
				+ "email TEXT)";
		db.execSQL(CREATE_LOGIN_TABLE);

		Log.d(TAG, "Database tables created");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);

		// Create tables again
		onCreate(db);
	}
	public void update(int recid ,String tepName,String tepTel,String tepEmail,String image1)
	{  SQLiteDatabase db = this.getReadableDatabase();
		ContentValues cv = new ContentValues(3);
		cv.put("name",  tepName);
		cv.put("phone", tepTel);
		cv.put("email", tepEmail);
		cv.put("img", image1);
		db.update(TB_NAME, cv, "_id=" + recid, null);
	}

	public HashMap<String, String> getUserDetails(int id) {
		HashMap<String, String> user = new HashMap<>();
		String selectQuery = "SELECT * FROM " + TB_NAME + " WHERE _id = " + id + ";";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			user.put("name", cursor.getString(1));
			user.put("email", cursor.getString(4));
			user.put("phone", cursor.getString(2));
			user.put("img", cursor.getString(3));

		}
		cursor.close();
		db.close();
		// return user
		return user;
	}


	public Cursor getall() {
		String countQuery = "SELECT  * FROM " + TB_NAME;
		SQLiteDatabase db = this.getReadableDatabase();

		return db.rawQuery(countQuery, null);
	}


	public Cursor getsome(String name)
	{
		String countQuery = "SELECT * FROM " + TB_NAME + " WHERE name LIKE '" + name + "%';";
		SQLiteDatabase db = this.getReadableDatabase();

		return db.rawQuery(countQuery, null);
	}

	public void add(String tepName,String tepTel,String tepEmail,String image1)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("name", tepName);
		cv.put("phone", tepTel);
		cv.put("img", image1);
		cv.put("email", tepEmail);
		long id =db.insert(TB_NAME, null, cv);
		db.close();
	}

	public void delete(int recid)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TB_NAME, "_id = " + recid, null);
	}



}
