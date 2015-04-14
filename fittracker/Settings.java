/*
 * Author: Matthew Dalton
 * Description: 
 */
package itcarlow.c00096264.fittracker;

import itcarlow.c00096264.fittracker.R;
import itcarlow.c00096264.fittrackerServicesLayer.DatabaseOperations;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class Settings extends Activity implements View.OnClickListener{

	private	EditText heightInput, weightInput;
	private CheckBox clb, ckg, ccm ,cin;
	private RadioButton rMale, rFemale;
	private Context ctx;
	private double heightValue, weightValue;
	private String sessionUserId;
	private int userId, genderType, heightType, weightType;
	private boolean checkedGender = false, checkedWeight=false, checkedHeight=false;
	private Context CTX = this;
	private DatabaseOperations db;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_details);
		db = new DatabaseOperations(CTX);
		refXML();
		retieveSessionDetails();
	}
	private void refXML(){	
		weightInput = (EditText) findViewById(R.id.weight);
		heightInput = (EditText) findViewById(R.id.height);
		clb = (CheckBox) findViewById(R.id.lb);
		ckg = (CheckBox) findViewById(R.id.kg);
		ccm = (CheckBox) findViewById(R.id.cm);
		cin = (CheckBox) findViewById(R.id.in);
		rMale = (RadioButton) findViewById(R.id.male);
		rFemale = (RadioButton) findViewById(R.id.female);
	}
	private void retieveSessionDetails(){
		SharedPreferences sessionDetails = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 for private mode
		SharedPreferences.Editor editor = sessionDetails.edit();
		// Retrieve user id 
		sessionUserId = sessionDetails.getString("loginId", null);
		userId = Integer.parseInt(sessionUserId);	
	}
	private void saveDataToSQLite(){
		try{
			db.putExtraUserInformation(db, weightValue, weightType, heightValue, heightType, genderType, userId);
			Intent saveUserDetailsIntent = new Intent(Settings.this, MainMenu.class); // Context is the first parameter, this is used because the Activity class is a subclass of Context
			Settings.this.startActivity(saveUserDetailsIntent);	
		
		}finally{
			db.close();
		}
	}
	public void onClick(View view) {
		switch(view.getId()){			
			case R.id.leaveUserDetails:
				Intent leaveUserDetailsIntent = new Intent(Settings.this, MainMenu.class); // Context is the first parameter, this is used because the Activity class is a subclass of Context
    			Settings.this.startActivity(leaveUserDetailsIntent);	
				break;
				
			case R.id.saveUserDetails:	
				if (weightInput.getText().length() == 0 || heightInput.getText().length() == 0){
					Toast.makeText(this, "Please enter a valid number!",
						Toast.LENGTH_LONG).show();
					return;
				}
				if (checkedGender && checkedWeight && checkedHeight){
					heightValue = Double.parseDouble(heightInput.getText().toString());
					weightValue = Double.parseDouble(weightInput.getText().toString());
					saveDataToSQLite();
				}
				else{
					Toast.makeText(this, "Please select all fields!",
							Toast.LENGTH_LONG).show();
						return;
				}
				break; 
				
			case R.id.female:
				rFemale.setChecked(true);
				rMale.setChecked(false);
				genderType=0;
				checkedGender=true;
				break;
			case R.id.male:
				rMale.setChecked(true);
				rFemale.setChecked(false);
				genderType=1;
				checkedGender=true;
				break;
			case R.id.lb:
				clb.setChecked(true);
				ckg.setChecked(false);
				weightType=0;
				checkedWeight=true;
				break;
			case R.id.kg:
				ckg.setChecked(true);
				clb.setChecked(false);
				weightType=1;
				checkedWeight=true;
				break;
			case R.id.in:
				cin.setChecked(true);
				ccm.setChecked(false);
				heightType=0;
				checkedHeight=true;
				break;
			case R.id.cm:
				ccm.setChecked(true);
				cin.setChecked(false);
				heightType=1;
				checkedHeight=true;
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