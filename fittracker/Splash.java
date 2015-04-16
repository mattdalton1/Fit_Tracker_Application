/*
 * Author: Matthew Dalton [C00096264]
 * Description: Splash Screen with a sound file
 */
package itcarlow.c00096264.fittracker;

import itcarlow.c00096264.fittracker.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class Splash extends Activity {

	private MediaPlayer introSong;
	
	protected void onCreate(Bundle savedInstance){
		
		super.onCreate(savedInstance);
		setContentView(R.layout.splash_screen); // Load up the splash screen
		
		introSong = MediaPlayer.create(Splash.this, R.raw.intro); // Using this class called 'Splash' and media file from the raw folder
		
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		boolean checkMediaFile = getPrefs.getBoolean("checkbox", true);
		
		if( checkMediaFile== true) introSong.start();
		
		// Want the splash activity to sleep for 5 seconds then have the main activity to start = accomplish this by use of thread
		// Use of threading = multiple things about the same time
		
		Thread timer = new Thread(){ 
			
			// Use run method
			public void run(){
				try{
					sleep(10000); // Pause activity to pause in milliseconds
					
				}catch(InterruptedException ie){
					ie.printStackTrace(); // deal with this exception
				}
				finally{
					// Start the login activity using an intent
					Intent menuPoint = new Intent(Splash.this, LoginActivity.class);
					startActivity(menuPoint);
				}
			}
		};
		timer.start();
	}
	protected void onPause(){
		super.onPause(); 
		introSong.release(); // stop music file
		finish();
	}
	protected void onResume(){
		super.onResume();
	}
	protected void onStop(){
		super.onStop();
	}
}