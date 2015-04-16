/*
 * Author: Matthew Dalton [C00096264]
 * Description: The user enters his or her user name and password. 
 * 	The application communicates with the application database to check the credentials. 
 * 	If the input matches a name and password stored in the database, the menu screen will appear, 
 * 	else a message will appear notifying the user that the login has failed and the user must enter 
 * 	the correct user name and password.
 *
 */
package itcarlow.c00096264.fittracker;

import itcarlow.c00096264.fittracker.R;
import itcarlow.c00096264.fittrackerServicesLayer.DatabaseOperations;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements View.OnClickListener{
	private Button login, register;
	private EditText username, userpass;
	private String user_name, user_pass;
	private Context CTX = this;
	private String loginName, loginId, loginPass;
	private Boolean loginStatus;
	private DatabaseOperations dop;
	private Cursor cr;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		refXML();
		dop = new DatabaseOperations(CTX);
		login.setOnClickListener(this);	
		register.setOnClickListener(this);
	}
	private void refXML(){
		username = (EditText) findViewById(R.id.username);
		userpass = (EditText) findViewById(R.id.userpass);
		login = (Button) findViewById(R.id.loginBtn);
		register = (Button) findViewById(R.id.registerBtn);
	}
	public void onClick(View v) {
		
		switch(v.getId()){
			case R.id.loginBtn:
				user_name = username.getText().toString();
				user_pass = userpass.getText().toString();
				
				if(user_name.isEmpty() || user_pass.isEmpty()){
					Toast.makeText(getBaseContext(), "Please enter details!", Toast.LENGTH_LONG).show();   
				}
				else{
					/////////////////////////////////////////
					try{
						user_name = username.getText().toString().replace("\\s+", ""); // Removes all whitespace and non visible characters such as tab, \n 
						user_pass = userpass.getText().toString().replace("\\s+", "");
						
						cr = dop.getLoginInformation(dop);
						cr.moveToFirst();
						loginStatus = false;
						loginName ="";
						do{
							if(user_name.equals(cr.getString(1))&&(user_pass.equals(cr.getString(2)))){
								loginStatus = true;
								loginId = cr.getString(0);
								loginName = cr.getString(1);
								loginPass = cr.getString(2);
							
								SharedPreferences sessionDetails = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 for private mode
								SharedPreferences.Editor editor = sessionDetails.edit();
								editor.putString("loginId", loginId);
								editor.putString("loginName", loginName);
								editor.putString("loginPass", loginPass);
								editor.commit(); // Commit changes
							}
						}while(cr.moveToNext());
				
						if(loginStatus)
						{
							Toast.makeText(getBaseContext(), "Login Success----\n Welcome "+loginName, Toast.LENGTH_LONG).show();
							Intent anIntent = new Intent(this, MainMenu.class);
							startActivity(anIntent);
						}
						else
						{
							Toast.makeText(getBaseContext(), "Login Failed---- ", Toast.LENGTH_LONG).show();
						}
					}finally{
						cr.close();
						dop.close();
					}
				}
				break;
				
			case R.id.registerBtn:
				// Create an Intent to open Register activity
				dop.close();
    			Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class); // Context is the first parameter, this is used because the Activity class is a subclass of Context
    			LoginActivity.this.startActivity(registerIntent);	
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