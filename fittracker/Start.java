/*
 * Author: Matthew Dalton [C00096264]
 * Description: The user presses the start button. 
 * 	The timer will automatically commence with the option to pause and resume activity or stop the activity. 
 * 	The calculated distance (in miles or kilometers), calories burned will be displayed in real time. 
 * 	The users route is displayed on the google map. 
 * 	The user can select the type of exercise activity, currently, walking or running. 
 * 	The user can also change the map type.
 * 
 *  References - developer.android, available at http://developer.android.com/guide/topics/location/strategies.html
 *  
 */
package itcarlow.c00096264.fittracker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import itcarlow.c00096264.fittracker.R;
import itcarlow.c00096264.fittracker.Transparent;
import itcarlow.c00096264.fittrackerLogicLayer.ActivityCalculations;
import itcarlow.c00096264.fittrackerServicesLayer.DatabaseOperations;

public class Start extends FragmentActivity implements View.OnClickListener{
	protected GoogleMap googleMap;
	// Define Buttons, Labels, Text views, checkBoxes
	private TextView timeDisplay, distanceDisplay, paceDisplay, caloriesBurnedDisplay, optionsText;
	private Button startBtn, stopBtn, pauseBtn, resumeBtn;
	private ImageView optionsBtn;
	private CheckBox cbMapTypeA, cbMapTypeB, cbWalking, cbRunning;
	private long startTime, elapsedTime;
	private long secs,mins,hrs;
	private double currentLon, currentLat, prevLon, prevLat, totalDistance, weight;
	private int id, unitindex=2, key=0, checkWeight, activityType;
	private boolean isMeasuring = false, stopped = false;
	private String userId, hours, minutes, seconds, getWeight, getCheckWeight;
	private LocationManager locationManager = null;
	private LocationListener myloclist = null;
	/* 	How often should update the timer to show how much time has elapsed. Value in milliseconds. If set to 100, 
	*	then every tenth of a second we will update the timer.
	*/
	private final int REFRESH_RATE = 100; 
	private Handler mHandler = new Handler();
	private Transparent popup, popupTwo;
	
	private final static double[] multipliers = {
		1.0,1.0936133,0.001,0.000621371192
	};
	private final static String[] unitstrings = {
		"m", "y", "km", "mi"
	};
	private ArrayList<LatLng> arrayPoints = null;
	private PolylineOptions polylineOptions;
	private Context CTX = this;
	private DatabaseOperations db;
	private Cursor cr;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.google_map);
		db = new DatabaseOperations(CTX);
		refXML(); 
		getUserDetails();
		startBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);
		pauseBtn.setOnClickListener(this);
		resumeBtn.setOnClickListener(this);
		cbMapTypeA.setOnClickListener(this);
		cbMapTypeB.setOnClickListener(this);
		cbWalking.setOnClickListener(this);
		cbRunning.setOnClickListener(this);
		arrayPoints = new ArrayList<LatLng>();	
	}
	// Initialize referencing points 
	private void refXML(){
		distanceDisplay = (TextView) findViewById(R.id.distance);
		timeDisplay = (TextView) findViewById(R.id.time);
		//paceDisplay = (TextView) findViewById(R.id.pace);
		caloriesBurnedDisplay = (TextView) findViewById(R.id.calories);
		optionsText = (TextView) findViewById(R.id.optionsText);
		startBtn = (Button) findViewById(R.id.startBtn);
		stopBtn = (Button) findViewById(R.id.stopBtn);
		pauseBtn = (Button) findViewById(R.id.pauseBtn);
		resumeBtn = (Button) findViewById(R.id.resumeBtn);
		cbMapTypeA = (CheckBox) findViewById(R.id.mapTypeSatellite);
		cbMapTypeB = (CheckBox) findViewById(R.id.mapTypeTerrain);
		cbRunning = (CheckBox) findViewById(R.id.running);
		cbWalking = (CheckBox) findViewById(R.id.walking);
		// Pop up window in Activity with map
		popup = (Transparent)findViewById(R.id.popup_window);
		popupTwo = (Transparent)findViewById(R.id.popup_windowTwo);
		popup.setVisibility(View.GONE);
		popupTwo.setVisibility(View.GONE);
		optionsBtn = (ImageView)findViewById(R.id.optionsBtn);
		optionsBtn.setVisibility(View.INVISIBLE);
		optionsText.setVisibility(View.INVISIBLE);
	}
	private void getUserDetails(){
		SharedPreferences sessionDetails = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 for private mode
		SharedPreferences.Editor editor = sessionDetails.edit();		
		userId = sessionDetails.getString("loginId", null);
		id = Integer.parseInt(userId);
		try{
			cr = db.getUserDetails(db,id);
			cr.moveToFirst();
			getWeight= cr.getString(1); // Check if weight in lbs or k.g.
			getCheckWeight = cr.getString(2);	// Get the value of the user's weight
			if(getWeight == null || getCheckWeight == null){
				checkWeight = 0;
				weight = 0.0;
			}
			else{
				checkWeight = Integer.parseInt(getCheckWeight);
				weight = Double.parseDouble(getWeight);
			}	
		}finally{
			cr.close();
			db.close();
		}
	}
	// For Top Menu item list
	public boolean onCreateOptionsMenu(Menu menu){
			super.onCreateOptionsMenu(menu);
			MenuInflater theMenu = getMenuInflater();
			theMenu.inflate(R.menu.main_activity_menu, menu);
			return true;
	}
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.appSettings:
			Intent myIntent = new Intent(Start.this, Settings.class); 
			Start.this.startActivity(myIntent);	
			return true;
		}
		return false;
	}
    private void setUpMapIfNeeded() {    	
    	// Do a null check to confirm that we have not already instantiated the map
    	if( googleMap==null ){
    		// Try to obtain the map from the supportMapFragment
    		googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.the_map)).getMap();
    		// Check successful in obtaining the map
    		if( googleMap !=null ){ setUpMap(); }
    	}
    } 
    public void setUpMap()
    {	
    	if (locationManager == null){
    		// Get LocationManager object from System Service LOCATION_SERVICE
    		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE); // require access course location permission in the manifest file
    	}
    // Set up the map   	
    	// Enable or disable MyLocation Layer of Google Map
    	googleMap.setMyLocationEnabled(true); // draw an indication of current location of device + can get updates
    	googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // include satellite maps, normal and terrain maps
    	// Zoom in the Google Map
    	googleMap.animateCamera((CameraUpdateFactory.zoomTo(30))); // The higher the number the closer it zooms in 
    	
    // Create a criteria object to retrieve provider - how accurate do we need, power, depends on the settings you have on maps
    	final Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	criteria.setAltitudeRequired(false);
    	criteria.setBearingRequired(false);
    	criteria.setCostAllowed(true);
    	criteria.setPowerRequirement(Criteria.POWER_LOW);
    	
    // Get the name of the best provider
    	final String provider = locationManager.getBestProvider(criteria, true);
    	// Define a listener that responds to location updates
    	myloclist = new MyLocListener();
	    	/*
	    	 *  From locationManager object calls the method named as requestLocationUpdates(). This methods accepts four parameters, namely: 
	    	 *  The default GPS provider available in the mobile device
			 *	Minimum time internal between successive notification
			 *	Minimum distance internal between successive notification in meters
			 *	The LocationListener object, whose onLocationChanged() method will be called for each location update
	    	 */
	    	// locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, myloclist); 
	    	// The LocationManager is a service that listens for Network from the device. This code requests that the system call this LocationListener every 10 seconds (10000 milliseconds) provided that the user has moved at least 10 meters from their previous position.
	    locationManager.requestLocationUpdates(provider, 8000, 0, myloclist); 
	    	// The LocationManager is a service that listens for GPS coordinates from the device. This code requests that the system call this LocationListener every 8 seconds (8000 milliseconds) provided that the user has moved at least 5 meters from their previous position.
    }
    /*
     * Create an inner class named as ”MylocListener’. This class implements ‘LocationListerner’ interface.
     * It is used for receiving notification from the LocationManager class when the location changes. 
     * LocationManager class is an inbuilt class in Android which allows applications to obtain latitude and longitude co-ordinates of the mobile device. 
     * 
     * LocationListener interface has four unimplemented methods which have to be implemented in MylocListener class.
    		onLocationChanged()- Whenever location has been changed for a device the LocationManager class will implicitly call this method passing the new location has a parameter in the form of location object.
    		onProviderDisabled
    		onStatusChanged
    		onProviderEnabled
     */ 
    public class MyLocListener implements LocationListener{
    	
    	public void onLocationChanged(Location loc) {
    				
    		loc.getAccuracy(); //  describes the deviation in meters. So, the smaller the number, the better the accuracy.
    	
    		if ( loc.getAccuracy() < 50 ){
    		    			
	    		// Set current position
    			currentLon = loc.getLongitude();
	    		currentLat = loc.getLatitude();
	    			
	    		LatLng latlng = new LatLng(currentLat, currentLon);
    			
		    	// Change Marker settings
		    	MarkerOptions markerOptions = new MarkerOptions();
		    	markerOptions.title("Creating A Route");
		    	markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot));
		    		
		    	// Use camera and zoom
		    	googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,4));
		    	googleMap.animateCamera(CameraUpdateFactory.zoomTo(18), null);
		    		
		    	// Add marker position
		    	markerOptions.position(latlng);
		    	googleMap.addMarker(markerOptions);
		    		
		    	// Setting polyline in the map
		    	polylineOptions = new PolylineOptions();
		    	polylineOptions.color(Color.BLUE);
		    	polylineOptions.width(8);
		    	arrayPoints.add(latlng);
		    	polylineOptions.addAll(arrayPoints);
		    	googleMap.addPolyline(polylineOptions);	
		    	
		    	if (arrayPoints.size() > 1 && isMeasuring == true){	
		    		//LatLng previousPoint = arrayPoints.get(arrayPoints.size()-1);
		    		LatLng previousPoint = arrayPoints.get(0);
		    		prevLon = previousPoint.longitude;
		    		prevLat = previousPoint.latitude;
		    		
		    		// Getting URL to the Google Directions API
		    		String url = getDirectionsUrl(prevLat,prevLon,currentLat,currentLon);  
		    		
		    		DownloadTask downloadTask = new DownloadTask();
                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
		    	}
	    	}
    	}
    	@Override
    	public void onProviderDisabled(String arg0) {
    	}
    	@Override
    	public void onProviderEnabled(String arg0) {
    	}
    	@Override
    	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    	}
    }
    // Click handlers for buttons
    public void onClick(View view){ 	
    	switch(view.getId()){
    		case R.id.startBtn:
    			setUpMapIfNeeded();
    			startMeasuring();
    			cbRunning.setChecked(true);
    			activityType=1;
    			optionsBtn.setVisibility(View.VISIBLE);
    			optionsText.setVisibility(View.VISIBLE);
    			startBtn.setVisibility(View.INVISIBLE); 		
    			pauseBtn.setVisibility(View.VISIBLE);
    			if(stopped){
    				startTime = System.currentTimeMillis() - elapsedTime;
    			}
    			else{
    				startTime = System.currentTimeMillis();
    			}
    			// Checks to ensure that there are no instances of the startTimer runnable currently running and then starts a new thread to update the timer every one tenth of a second.
    			mHandler.removeCallbacks(startTimer);
    	        mHandler.postDelayed(startTimer, 0);
    		break;
    		
    		case R.id.pauseBtn:
    			pauseBtn.setVisibility(View.INVISIBLE);
    			resumeBtn.setVisibility(View.VISIBLE); 	
    			mHandler.removeCallbacks(startTimer);
    			stopped = true;
    			onPause();
    			break;
    			
    		case R.id.resumeBtn:
    			resumeBtn.setVisibility(View.INVISIBLE);
    			pauseBtn.setVisibility(View.VISIBLE); 
    			// Check if the stop watch was stopped if it was then the stopwatch should resume from where it left off, this is done by adding the already elapsed time to the new calculations of the elapsed time.
    			if(stopped){
    				startTime = System.currentTimeMillis() - elapsedTime;
    			}
    			else{
    				startTime = System.currentTimeMillis();
    			}
    			// Checks to ensure that there are no instances of the startTimer runnable currently running and then starts a new thread to update the timer every one tenth of a second.
    			mHandler.removeCallbacks(startTimer);
    	        mHandler.postDelayed(startTimer, 0);
    	        onResume();
    			break;
    		
    		case R.id.stopBtn:
    			stopMeasuring();
    			// The handler stops the application from looping in the runnable event
    			mHandler.removeCallbacks(startTimer);
    			stopped = true;
    		 	// Create an Intent 
    			Intent myIntent = new Intent(Start.this, ReviewStats.class); // Context is the first parameter, this is used because the Activity class is a subclass of Context
    			myIntent.putExtra("distanceKey", distanceDisplay.getText().toString());
    			myIntent.putExtra("timeKey", timeDisplay.getText().toString());
    			//myIntent.putExtra("paceKey", paceDisplay.getText().toString());
    			myIntent.putExtra("caloriesBurnedKey", caloriesBurnedDisplay.getText().toString());
    			Start.this.startActivity(myIntent);	
    			break;
    		
    		case R.id.optionsBtn:
    			if(key==0){
    				key=1;
    				popup.setVisibility(View.VISIBLE);
    				popupTwo.setVisibility(View.VISIBLE);
    				break;
    			}
    			else if(key==1){
    				key=0;
    				popup.setVisibility(View.GONE);
    				popupTwo.setVisibility(View.GONE);
    				break;
    			}
    		case R.id.mapTypeSatellite:
    			
    	    	if(cbMapTypeA.isChecked()){
    	    		cbMapTypeB.setChecked(false);
    	    		googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    	    	}
    	    	break;
    		case R.id.mapTypeTerrain:
    			
    	    	if(cbMapTypeB.isChecked()){
    	    		cbMapTypeA.setChecked(false);
    	    		googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    	    	}
    	    	break;
    		case R.id.walking:
    			
    	    	if(cbWalking.isChecked()){
    	    		cbRunning.setChecked(false);
    	    		activityType=1;
    	    	}
    	    	break;
    		case R.id.running:
    			
    	    	if(cbRunning.isChecked()){
    	    		cbWalking.setChecked(false);
    	    		activityType=2;
    	    	}
    	    	break;
    	}
    }
    /*
    *	The procedure takes the time in milliseconds and 
    *	splits in into hours, minutes, seconds 
    *	Since we want the time in a consistent
    * 	format like 00:00:00 we check the hours, minutes and seconds to see 
    * 	if they are less than 10, if they are we add an extra zero to the front of the number, 
    * 	so it would display as 05 instead of just 5.
    * 
    * 	The modulo function (%) is used to remove any remainders of the hour, minute or second. 
    * 	If we have 2.5 minutes elapsed we want the minute portion of the string to be 2 not 2.5, the .5 will be converted to 30 seconds 
    *   and represented in the seconds portion of the string.
    */
    private void updateTimer (float time){
    	secs = (long)(time/1000);
    	mins = (long)((time/1000)/60);
    	hrs = (long)(((time/1000)/60)/60);
    	/* Convert the seconds to String
		 * and format to ensure it has
		 * a leading zero when required
		 */
    	secs = secs % 60;
    	seconds=String.valueOf(secs);
    	if(secs == 0){
    		seconds = "00";
    	}
    	if(secs < 10 && secs > 0){
    		seconds = "0"+seconds;
    	}
    	/* Convert the minutes to String and format the String */
    	mins = mins % 60;
		minutes=String.valueOf(mins);
    	if(mins == 0){
    		minutes = "00";
    	}
    	if(mins <10 && mins > 0){
    		minutes = "0"+minutes;
    	}
    	/* Convert the hours to String and format the String */
    	hours=String.valueOf(hrs);
    	if(hrs == 0){
    		hours = "00";
    	}
    	if(hrs <10 && hrs > 0){
    		hours = "0"+hours;
    	}
    	/* Setting the timer text to the elapsed time */
		timeDisplay.setText(hours + ":" + minutes + ":" + seconds);    
    }
    /*
     * The timer Runnable
     * This code calculates the elapsed time by subtracting the current time from the start time,
     * then updates the TextView with the elapsed time so it can be seen visually,
     * then waits for 1/10 second (REFRESH_RATE set to) and checks the elapsed time again.
     */
    private Runnable startTimer = new Runnable() {
    	public void run(){
    		elapsedTime = System.currentTimeMillis() - startTime;
    		updateTimer(elapsedTime);
    		mHandler.postDelayed(this, REFRESH_RATE);
    	}
    };
    /*
     * 	Code below is to calculate the distance between two geographical points.
     */
	private void startMeasuring(){
		isMeasuring = true;
	}
	private void stopMeasuring(){
		isMeasuring = false;
	}
	private String getDirectionsUrl(double preLat,double preLon, double currLat, double currLon){
		
		String str_origin = "origin="+preLat+","+preLon;
		String str_dest = "destination="+currLat+","+currLon;
		
	    // Sensor enabled
        String sensor = "sensor=false";  
        
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
		
        // Output format
        String output = "json";
        
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        
        return url;
		//double distance = calcGeoDistance(prevLat, prevLon, currentLat, currentLon);
		//distance = roundDecimal(distance, 3);
		//totalDistance += distance;
		//totalDistance = roundDecimal(distance,2);
		//String totalDistanceText = "" + totalDistance + " " + unitstrings[unitindex];
		//calculateCaloriesBurned(totalDistance);
		//distanceDisplay.setText(totalDistanceText);
	}
	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException{
		String data ="";
		InputStream iStream =null;
		HttpURLConnection urlConnection = null;
	    
		try{
			URL url = new URL(strUrl);
			// Creating an http connection to communicate with url 
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url 
            urlConnection.connect();
            // Reading data from url 
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb  = new StringBuffer();
            String line = "";
            while( ( line = br.readLine())  != null){
                    sb.append(line);
            }
            data = sb.toString();
            br.close();
		}catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
		}finally{
            iStream.close();
            urlConnection.disconnect();
		}
		 return data;
	}
	   // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{  
    	// Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
        	// For storing data from web service
            String data = "";
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;        
        }
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {            
            super.onPostExecute(result);            
            
            ParserTask parserTask = new ParserTask();
            
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
                
        }        
        
    }
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {
			 	
				JSONObject jObject;    
	            List<List<HashMap<String, String>>> routes = null;     
	            try{
	                jObject = new JSONObject(jsonData[0]);
	                DirectionsJSONParser parser = new DirectionsJSONParser();
	                
	                // Starts parsing data
	                routes = parser.parse(jObject);    
	            }catch(Exception e){
	                e.printStackTrace();
	            }
	            return routes;
		}
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";
            
            if(result.size()<1){
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);    
                    
                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");                        
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;   
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);    
                    
                    points.add(position);      
                }
             // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);  
           }
           // calculateCaloriesBurned(distance);
           distanceDisplay.setText(distance);
           // Drawing polyline in the Google Map for the i-th route
           googleMap.addPolyline(lineOptions);        
       }
    }
	private void calculateCaloriesBurned(String strDistance){
		double distance = Double.parseDouble(strDistance);
		if (activityType==1){
			String caloriesBurnedText = "" + Math.round(ActivityCalculations.walkingCaloriesBurned(distance, weight, checkWeight));
			caloriesBurnedDisplay.setText(caloriesBurnedText);
		}
		else if(activityType==2){
			String caloriesBurnedText = "" + Math.round(ActivityCalculations.runningCaloriesBurned(distance, weight, checkWeight));
			caloriesBurnedDisplay.setText(caloriesBurnedText);
		}
	}
	public void stopListening(){
    	try{
    		if (locationManager != null && myloclist != null){
    			locationManager.removeUpdates(myloclist);
            }        
    		locationManager = null;
       }
       catch (final Exception ex){             
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
	protected void onDestroy(){
		arrayPoints.clear();
		stopListening();
		super.onDestroy();
	}
    
}