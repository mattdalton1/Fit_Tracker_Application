/*
 * Author: Matthew Dalton [C00096264]
 * Description: After the user stops the exercise activity, a screen will appear presenting 
 * 	the stats of the recent activity. These include the duration of the activity, the distance covered, 
 * 	calories burned, etc. The user can leave a note describing the recent activity. 
 * 	The user has the option to send these details to the website for other users to view or 
 * 	just save it to the user’s phone.
 *
 * 
 */
package itcarlow.c00096264.fittracker;

import itcarlow.c00096264.fittracker.R;
import itcarlow.c00096264.fittrackerServicesLayer.DatabaseOperations;
import itcarlow.c00096264.fittrackerServicesLayer.SendActivityInfoToRMDB;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ReviewStats extends Activity implements View.OnClickListener{

	private TextView distanceResult, timeResult, paceResult, caloriesBurnedResult;
	private EditText activityNotes;
	private Button saveActivityBtn;
	private CheckBox saveToRemoteDB;
	// Object of Context class to DataBaseOperations class
	private Context ctx = this;
	private DatabaseOperations db;
	private String mIntentTime, mIntentDistance, mIntentPace, mIntentCaloriesBurned, notes, sessionUserId, sessionUserName;
	private int id;
	private boolean saveToDB = false;
	
	protected void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		setContentView(R.layout.review_stats);
		db = new DatabaseOperations(ctx); // Create a context
		refXML();
		retieveSessionDetails();
		
		mIntentDistance = savedInstance != null ? savedInstance.getString("distanceKey"):null;
		mIntentTime = savedInstance != null ? savedInstance.getString("timeKey"):null;
		mIntentPace = savedInstance != null ? savedInstance.getString("paceKey"):null;
		mIntentCaloriesBurned = savedInstance != null ? savedInstance.getString("caloriesBurnedKey"):null;
	
		Bundle extras = getIntent().getExtras();
		mIntentDistance = extras !=null ? extras.getString("distanceKey") : "nothing passed in";
		mIntentTime = extras !=null ? extras.getString("timeKey") : "nothing passed in";
		mIntentPace = extras !=null ? extras.getString("paceKey") : "nothing passed in";
		mIntentCaloriesBurned = extras !=null ? extras.getString("caloriesBurnedKey") : "nothing passed in";
		
		// Set values to text views
		distanceResult.setText(mIntentDistance);
		timeResult.setText(mIntentTime);
		paceResult.setText(mIntentPace);
		caloriesBurnedResult.setText(mIntentCaloriesBurned);
	
		saveActivityBtn.setOnClickListener(this);
		saveToRemoteDB.setOnClickListener(this);
	}
	private void retieveSessionDetails(){
		SharedPreferences sessionDetails = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 for private mode
		SharedPreferences.Editor editor = sessionDetails.edit();
		// Retrieve user id 
		sessionUserId = sessionDetails.getString("loginId", null);
		sessionUserName = sessionDetails.getString("loginName", null);
		id = Integer.parseInt(sessionUserId);	
	}
	private void refXML(){
		distanceResult = (TextView)findViewById(R.id.reviewMiles);
		timeResult = (TextView)findViewById(R.id.durationReview);
		paceResult = (TextView)findViewById(R.id.pace);
		caloriesBurnedResult = (TextView)findViewById(R.id.caloriesReview);
		activityNotes = (EditText)findViewById(R.id.reviewText);
	    saveActivityBtn = (Button)findViewById(R.id.saveBtn);
		saveToRemoteDB = (CheckBox)findViewById(R.id.shareOption);
	}
	private void saveActivityToSQLite(){
		try{
			notes  = activityNotes.getText().toString();
			db.insertActivityInformation(db, mIntentTime, mIntentDistance, mIntentPace, notes, mIntentCaloriesBurned, id);
			Intent myIntent = new Intent(ReviewStats.this, MainMenu.class); 
			ReviewStats.this.startActivity(myIntent);
		}finally{
			db.close();
		}
	}
	// Send data to remote database
	private void sendActivityTODB(String n, String d, String p, String cB, String t, String sessionUserName){
		new SendActivityInfoToRMDB(this,0).execute(n, d, p, cB, t, sessionUserName);
	}
	public void onClick(View v) {
		
		switch(v.getId()){
			case R.id.shareOption:	
				if(saveToRemoteDB.isChecked()){
    	    		saveToDB=true;
    	    	}
    	    	break;
    	    	
			case R.id.saveBtn:			
				if(saveToDB==true){			
					// Save activity info to SQLite database and remote database 
					saveActivityToSQLite();
					sendActivityTODB(notes, mIntentDistance, mIntentPace, mIntentCaloriesBurned, mIntentTime, sessionUserName);
					// Display a toast message if the data has been entered successfully
					String msg = "Data entered successfully!";
					//Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
					saveToDB=false;
					saveToRemoteDB.setChecked(false);
				}
				else{
					// Save activity info ONLY to SQLite database 
					saveActivityToSQLite();
					String msg = "Data entered successfully!";
					//Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				}
				break;
		}	
	}
	protected void onPause(){
		super.onPause();
	}
	protected void onResume(){
		super.onResume();
	}
	protected void onStop(){
		super.onStop();
	}
}