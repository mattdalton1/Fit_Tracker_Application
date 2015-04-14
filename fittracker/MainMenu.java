/*
 * Author: Matthew Dalton
 * Description: The main menu activity
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