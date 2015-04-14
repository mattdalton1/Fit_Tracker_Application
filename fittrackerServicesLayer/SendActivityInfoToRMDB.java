/*
 * Author: Matthew Dalton
 * Description: MySQL Operations
 */
package itcarlow.c00096264.fittrackerServicesLayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import android.content.Context;
import android.os.AsyncTask;

public class SendActivityInfoToRMDB extends AsyncTask<String,Void,String> {
	private Context context;
	private int byGetOrPostData = 0;
	
	public SendActivityInfoToRMDB(Context context, int flag){
		this.context = context;
		this.byGetOrPostData = flag;
	}
	protected void onPreExecute(){}
	protected String doInBackground(String... arg0) {
	
		if(byGetOrPostData == 0){
			String notes = (String)arg0[0];
			String distance = (String)arg0[1];
			String pace = (String)arg0[2];
			String caloriesBurned = (String)arg0[3];
			String timeCompleted = (String)arg0[4];
			String nameOfUser =(String)arg0[5];	
			try{
				String link="http://www.keepfittracker.com/includes/insertActivityInfo.php";	            
				String data  = URLEncoder.encode("notes", "UTF-8") 
		        + "=" + URLEncoder.encode(notes, "UTF-8");
				
		        data += "&" + URLEncoder.encode("distance", "UTF-8") 
		        		+ "=" + URLEncoder.encode(distance, "UTF-8");
		        data += "&" + URLEncoder.encode("pace", "UTF-8") 
		    	        + "=" + URLEncoder.encode(pace, "UTF-8");
		        data += "&" + URLEncoder.encode("caloriesBurned", "UTF-8") 
		    	        + "=" + URLEncoder.encode(caloriesBurned, "UTF-8");
		        data += "&" + URLEncoder.encode("timeCompleted", "UTF-8") 
		    	        + "=" + URLEncoder.encode(timeCompleted, "UTF-8");
		        data += "&" + URLEncoder.encode("nameOfUser", "UTF-8") 
		    	        + "=" + URLEncoder.encode(nameOfUser, "UTF-8");
		          
		        URL url = new URL(link);
		        URLConnection conn = url.openConnection(); 
		        conn.setDoOutput(true); 
		        OutputStreamWriter wr = new OutputStreamWriter
		        (conn.getOutputStream()); 
		        wr.write( data ); 
		        wr.flush(); 
		        BufferedReader reader = new BufferedReader
		        (new InputStreamReader(conn.getInputStream()));
		        StringBuilder sb = new StringBuilder();
		        String line = null;
		           // Read Server Response
		        while((line = reader.readLine()) != null)
		        {
		        	sb.append(line);
		            break;
		        }
		        return sb.toString();
		        
			}catch(Exception e){
				return new String("Exception: " + e.getMessage());
			}
		}
		else{
			return null;
		}
	}	
}