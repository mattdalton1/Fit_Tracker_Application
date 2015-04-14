/*
 * Author: Matthew Dalton
 * Description: To open a URL "www.keepfittracker" in Android
 * 
 */
package itcarlow.c00096264.fittracker;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class KeepFitTracker extends Activity {
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
			String url = "http://www.keepfittracker.com";
			Intent browserIntent = new Intent(Intent.ACTION_VIEW);
			browserIntent.setData(Uri.parse(url));
			startActivity(browserIntent);
			finish();
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
	protected void onDestroy(){
		super.onDestroy();
	}
}