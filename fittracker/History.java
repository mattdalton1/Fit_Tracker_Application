/*
 * Author: Matthew Dalton [C00096264]
 * Description: The registered user can view a history of their activities. 
 * 	Each activity has the date created, time completed, distance covered and pace. 
 * 	When the user clicks on an activity, a note will appear describing that particular activity.
 *
 */
package itcarlow.c00096264.fittracker;

import itcarlow.c00096264.fittracker.R;
import itcarlow.c00096264.fittrackerServicesLayer.DatabaseOperations;
import itcarlow.c00096264.fittrackerServicesLayer.TableData.TableInfo;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

public class History extends Activity{

	private TextView user_name;
	private ListView list_view;
	private String userName,userId;
	private int id;
	private Context CTX = this;
	private DatabaseOperations db;
	private Cursor cr;
	
	private SimpleCursorAdapter dataAdapter;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		db = new DatabaseOperations(CTX);
		refXML();
		displayUserName();
		displayItems();
	}
	private void refXML(){
		user_name = (TextView) findViewById(R.id.userName);
		list_view = (ListView) findViewById(R.id.list);
	}
	private void displayUserName(){
		SharedPreferences sessionDetails = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 for private mode
		SharedPreferences.Editor editor = sessionDetails.edit();
		// Retrieve user name 
		userName = sessionDetails.getString("loginName", null);
		user_name.setText(userName);
		
		userId = sessionDetails.getString("loginId", null);
		id = Integer.parseInt(userId);	
	}
	private void displayItems(){	
		
		try{
			cr = db.getActivityInformation(db,id);
			String[] columns = new String[] {
				TableInfo.COL_ACTIVITY_DATE_CREATED,
				TableInfo.COL_ACIVITY_TIME,
				TableInfo.COL_ACIVITY_DISTANCE,
				TableInfo.COL_ACTIVITY_CALORIES_BURNED,
				TableInfo.COL_ACTIVITY_CURRENT_PACE
			};
			// THE XML defined views which the data will be bound to
			int[] to = new int[] {
				R.id.date_display,
				R.id.time_display,
				R.id.distance_display,
				R.id.calories_display,
				R.id.pace_display,
			};	
			// Create the adapter using the cursor pointing to the desired data 
			dataAdapter = new SimpleCursorAdapter(this, R.layout.activity_info, cr, columns, to,0);
			// set the behind the list view
			list_view.setAdapter(dataAdapter);
		
			list_view.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> list_view, View view, int position,
					long id) {
					
					// Get the cursor, position to the corresponding row
					cr = (Cursor) list_view.getItemAtPosition(position);
					// Get the date of activity 
					String activityNotes = cr.getString(cr.getColumnIndexOrThrow("activity_notes"));
					if ( activityNotes.isEmpty() ){
						Toast.makeText(getApplicationContext(), "There is no notes added to this activity!", Toast.LENGTH_SHORT).show();			
					}
					else{
						Toast.makeText(getApplicationContext(), activityNotes, Toast.LENGTH_LONG).show();
					}
				
				}
			});
		}finally{
		}
	}
	protected void onPause(){
		super.onPause();
	}
	protected void onResume(){
		super.onResume();
	}
	protected void onStop(){
		cr.close();
		db.close();
		super.onStop();
	}
}