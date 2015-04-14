/*
 * Author: Matthew Dalton
 * Description: 
 */
package itcarlow.c00096264.fittracker;

import itcarlow.c00096264.fittracker.R;
import itcarlow.c00096264.fittrackerServicesLayer.DatabaseOperations;
import itcarlow.c00096264.fittrackerServicesLayer.SigninActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity{

	private EditText USER_NAME, USER_PASS, CON_PASS;
	private String user_name, user_pass, con_pass;
	private Button USER_REG;
	
	// Object of Context class to DataBaseOperations class
	private Context CTX = this;
	private DatabaseOperations db;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		refXML();
		db = new DatabaseOperations(CTX);
		USER_REG.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				user_name = USER_NAME.getText().toString().replace("\\s+", ""); // Removes all whitespace and non visible characters such as tab, \n 
				user_pass = USER_PASS.getText().toString().replace("\\s+", "");
				con_pass = CON_PASS.getText().toString().replace("\\s+", "");
				
				if(user_name.isEmpty() || user_pass.isEmpty() || con_pass.isEmpty()){
					Toast.makeText(getBaseContext(), "Please enter details!", Toast.LENGTH_LONG).show();   
				}
				else{	
					if(!(user_pass.equals(con_pass))){
						Toast.makeText(getBaseContext(), "Passwords not matching!", Toast.LENGTH_LONG).show();
					}
					else if(user_pass.length() < 8 || con_pass.length() < 8 ){
						Toast.makeText(getBaseContext(), "The Passwords must have a minimum of 8 characters!", Toast.LENGTH_LONG).show();
					}
					else{	
						saveDataToSQLite(user_name, user_pass);
						sendUserInfoToRMDB(user_name, user_pass);
					}
				}
			}
		});
	}
	private void refXML(){
		USER_NAME = (EditText) findViewById(R.id.userName);
		USER_PASS = (EditText) findViewById(R.id.userPass);
		CON_PASS= (EditText) findViewById(R.id.confirmPass);
		USER_REG = (Button) findViewById(R.id.userReg);
	}
	// Send data to MySQL 
	private void sendUserInfoToRMDB(String name, String password){
		new SigninActivity(this,1).execute(name, password);
	}
	// Save data to SQLlite Database
	private void saveDataToSQLite(String name, String pass){
		try{
			db.putLoginInformation(db, name, pass);
			Toast.makeText(getBaseContext(), "Please login "+user_name, Toast.LENGTH_LONG).show();
			Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class); // Context is the first parameter, this is used because the Activity class is a subclass of Context
			RegisterActivity.this.startActivity(registerIntent);
		}finally{
			db.close();
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