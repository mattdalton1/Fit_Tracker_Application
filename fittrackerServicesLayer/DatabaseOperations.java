/*
 * Author: Matthew Dalton [C00096264]
 * Description: SQLite Operations and queries for creation of a database, opening a database,
 * insertRegisterationInformation, insertActivityInformation, insertExtraUserInformation, etc.
 * 
 */
package itcarlow.c00096264.fittrackerServicesLayer;

import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import itcarlow.c00096264.fittrackerServicesLayer.TableData.TableInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOperations extends SQLiteOpenHelper{
	
	public static final int database_version = 3;
	// Create user table query  
	public String CREATE_QUERY_USER = "CREATE TABLE "+TableInfo.TABLE_USER+" ("+TableInfo.COL_USER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT ,"+TableInfo.COL_USER_NAME+" TEXT not null,"+TableInfo.COL_USER_PASS+" TEXT not null,"+TableInfo.COL_USER_GENDER+" UNSIGNED TINYINT NULL,"+TableInfo.COL_USER_HEIGHT+" DOUBLE,"+TableInfo.COL_USER_WEIGHT+" DOUBLE,"+TableInfo.COL_USER_CHECK_WEIGHT+" UNSIGNED TINYINT,"+TableInfo.COL_USER_CHECK_HEIGHT+" UNSIGNED TINYINT);";
	
	public String Create_QUERY_ACTIVITY = "CREATE TABLE "+TableInfo.TABLE_ACTIVITY+" ("+TableInfo.COL_ACTIVITY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT ,"+TableInfo.COL_ACTIVITY_DATE_CREATED+" DATE,"+TableInfo.COL_ACIVITY_TIME+" TEXT,"+TableInfo.COL_ACIVITY_DISTANCE+" TEXT,"+TableInfo.COL_ACTIVITY_CURRENT_PACE+" TEXT,"+TableInfo.COL_ACTIVITY_NOTES+" TEXT,"+TableInfo.COL_ACTIVITY_CALORIES_BURNED+" TEXT,"+TableInfo.COL_USER_FK+" INTEGER NOT NULL ,FOREIGN KEY ("+TableInfo.COL_USER_FK+") REFERENCES "+TableInfo.TABLE_USER+" ("+TableInfo.COL_USER_ID+"));";
	
	// Create Database
	public DatabaseOperations(Context context) {
		super(context, TableInfo.DATABASE_NAME, null, database_version);
		//Log.d("Database operations", "Database created");
	}
	// Create Tables
	public void onCreate(SQLiteDatabase db) {
	
		db.execSQL(CREATE_QUERY_USER);
		db.execSQL(Create_QUERY_ACTIVITY);
		
		db.execSQL("CREATE TRIGGER fk_activity_userid " + 
		" BEFORE INSERT "+
		" ON "+TableInfo.TABLE_ACTIVITY+ 
		" FOR EACH ROW BEGIN"+
		" SELECT CASE WHEN ((SELECT "+TableInfo.COL_USER_ID+" FROM "+TableInfo.TABLE_USER+
		" _ WHERE "+TableInfo.COL_USER_ID+"=new."+TableInfo.COL_USER_FK+" ) IS NULL)"+
		" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
		"  END;");
		//Log.d("Database operations", "User Table created");
		//Log.d("Database operations", "Activity Table created");
	}
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLES IF EXISTS " + TableInfo.TABLE_USER + TableInfo.TABLE_ACTIVITY);
		onCreate(db);
	}
	public void onOpen(SQLiteDatabase db){
		super.onOpen(db);
		if ( !db.isReadOnly() )
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
	}
	// Insert new user login data into database
	public void insertRegisterationInformation(DatabaseOperations dop, String name, String pass){
	
		SQLiteDatabase sql = dop.getWritableDatabase();
		ContentValues cv = new ContentValues();
		
		cv.put(TableInfo.COL_USER_NAME, name);
		cv.put(TableInfo.COL_USER_PASS, pass);
		// Insert into table
		long k = sql.insert(TableInfo.TABLE_USER, null, cv); // null if nothing 
		//Log.d("Database operations", "One row inserted into User Table");	
		sql.close();
	}
	public void insertActivityInformation(DatabaseOperations dop, String activity_time, String activity_distance, String activity_current_pace, String activity_notes, String activity_calories_burned, int userId){
		
		SQLiteDatabase sql = dop.getWritableDatabase();
		ContentValues cv = new ContentValues();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
		cv.put(TableInfo.COL_ACTIVITY_DATE_CREATED, dateFormat.format(new Date()));
		cv.put(TableInfo.COL_ACIVITY_TIME, activity_time);
		cv.put(TableInfo.COL_ACIVITY_DISTANCE, activity_distance);
		cv.put(TableInfo.COL_ACTIVITY_CURRENT_PACE, activity_current_pace);
		cv.put(TableInfo.COL_ACTIVITY_NOTES, activity_notes);
		Log.d("Notes", activity_notes);
		cv.put(TableInfo.COL_ACTIVITY_CALORIES_BURNED, activity_calories_burned);
		cv.put(TableInfo.COL_USER_FK, userId);
		
		// Insert into table
		long id = sql.insert(TableInfo.TABLE_ACTIVITY, null, cv); // null if nothing 
		//Log.d("Database operations", "One row inserted into Activity Table");	
		sql.close();
	}
	public void insertExtraUserInformation(DatabaseOperations dop, double weight, int weightType, double height, int heightType, int genderType, int userId){

		SQLiteDatabase sql = dop.getWritableDatabase();
		ContentValues cv = new ContentValues();	
		cv.put(TableInfo.COL_USER_WEIGHT, weight);
		cv.put(TableInfo.COL_USER_CHECK_WEIGHT, weightType);
		cv.put(TableInfo.COL_USER_HEIGHT, height);
		cv.put(TableInfo.COL_USER_CHECK_HEIGHT, heightType);
		cv.put(TableInfo.COL_USER_GENDER, genderType);
		long k = sql.update(TableInfo.TABLE_USER, cv, TableInfo.COL_USER_ID + "=" + userId, null);
		sql.close();
	}
	public Cursor getLoginInformation(DatabaseOperations dop){
		
		SQLiteDatabase sql = dop.getReadableDatabase();
		String[] columns = {"user_id",TableInfo.COL_USER_NAME,TableInfo.COL_USER_PASS};
		Cursor mCursor = sql.query(TableInfo.TABLE_USER, columns, null, null, null, null, null);
		return mCursor;		
	}	
	public Cursor getActivityInformation(DatabaseOperations dop, int id){
		SQLiteDatabase SQL = dop.getReadableDatabase();
		String selectQuery = "SELECT _id, activity_date_created, activity_time, activity_distance, activity_current_pace, activity_notes, activity_calories_burned, user_fk FROM userActivity WHERE user_fk ='"+id+"'";
		Cursor mCursor = SQL.rawQuery(selectQuery, null);
		if(mCursor !=null){
			mCursor.moveToFirst();
		}
		return mCursor;						
	}
	public Cursor getUserDetails(DatabaseOperations dop, int id){
		SQLiteDatabase SQL = dop.getReadableDatabase();
		String selectQuery = "SELECT user_id, user_weight, user_check_weight, user_height, user_check_height FROM user WHERE user_id ='"+id+"'";
		Cursor mCursor = SQL.rawQuery(selectQuery, null);
		if(mCursor !=null){
			mCursor.moveToFirst();
		}
		return mCursor;						
	}
}