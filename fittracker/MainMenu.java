/*
 * Author: Matthew Dalton [C00096264]
 * Description: The user can swipe left, where a navigation drawer is used (a panel that transitions in
 *  from the left edge of the screen and displays the app’s main navigation options). 
 *  The options for the user are 
 *		Settings - To update the user’s details such as gender, weight and height. 
 *		Start - To start and record an exercise activity.  
 *		History - To view the details of all the saved activities.
 *		KeepFitTracker - To open a browser that will direct the user to the social website.
 *
 */
package itcarlow.c00096264.fittracker;

import itcarlow.c00096264.fittracker.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainMenu extends Activity {

	protected String[] menu;
	protected DrawerLayout dLayout;
	protected ListView dList;
	protected ArrayAdapter<String> adapter;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		menu = new String[]{"Settings", "Start", "History", "KeepFitTracker"};
		dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		dList = (ListView) findViewById(R.id.left_drawer);
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,menu);
		dList.setAdapter(adapter);
		dList.setSelector(android.R.color.holo_blue_dark);
		dList.setOnItemClickListener(new OnItemClickListener(){
			 
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				dLayout.closeDrawers();
				String openClass = menu[position]; // pass in position when item clicked 
				try{
					// Setting up a class and referencing the other class such as 'GoogleMap'
					Class selected = Class.forName("itcarlow.c00096264.fittracker." + openClass);
					// Set up an Indent
					Intent selectedIntent = new Intent(MainMenu.this, selected);
					startActivity(selectedIntent); // then open that class
				}catch(ClassNotFoundException e){
					e.printStackTrace();
				}
			}
		 });
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