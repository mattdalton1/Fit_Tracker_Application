/*
 * Author: Matthew Dalton
 * Description: 
 */
package itcarlow.c00096264.fittrackerServicesLayer;
import android.provider.BaseColumns;

public class TableData {
	// prevent accidental object creation
	public TableData() {}
	
	public static abstract class TableInfo implements BaseColumns
	{
		// Standard database creation String
		public static final String DATABASE_NAME = "user_info";
		// Create a User table
		public static final String TABLE_USER = "user";
			public static final String COL_USER_ID = "user_id";
			public static final String COL_USER_NAME = "user_name";
			public static final String COL_USER_PASS = "user_pass";
			public static final String COL_USER_WEIGHT ="user_weight";
			public static final String COL_USER_CHECK_WEIGHT ="user_check_weight";
			public static final String COL_USER_HEIGHT = "user_height";
			public static final String COL_USER_CHECK_HEIGHT = "user_check_height";
			public static final String COL_USER_GENDER = "user_gender";
			
		// Create a Activity table
		public static final String TABLE_ACTIVITY = "userActivity";
			public static final String COL_ACTIVITY_ID = "_id";
			public static final String COL_ACTIVITY_DATE_CREATED = "activity_date_created";
			public static final String COL_ACIVITY_TIME = "activity_time";
			public static final String COL_ACIVITY_DISTANCE = "activity_distance";
			public static final String COL_ACTIVITY_CALORIES_BURNED = "activity_calories_burned";
			public static final String COL_ACTIVITY_CURRENT_PACE = "activity_current_pace";
			public static final String COL_ACTIVITY_NOTES = "activity_notes";
			public static final String COL_USER_FK = "user_fk";
	}
}