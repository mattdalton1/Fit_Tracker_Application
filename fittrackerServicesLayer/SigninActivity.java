package itcarlow.c00096264.fittrackerServicesLayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class SigninActivity extends AsyncTask<String,Void,String> {

	private Context context;
	private int byGetOrPost = 0;
	// flag 0 means get and 1 means post. (By default it is get)
	public SigninActivity(Context context,int flag) {
		this.context = context;
		this.byGetOrPost = flag;
	}
	protected void onPreExecute(){}
	protected String doInBackground(String... arg0) {
		
		if(byGetOrPost == 0){ //means by Get Method
			String Id = (String)arg0[0];
			String name = (String)arg0[1];
			String password = (String)arg0[2];
			
	         try{
	            String link = "http://www.keepfittracker.com/includes/login.php?Id="+Id+"&name="+name+"&password="+password;
	            URL url = new URL(link);
	            HttpClient client = new DefaultHttpClient();
	            HttpGet request = new HttpGet();
	            request.setURI(new URI(link));
	            HttpResponse response = client.execute(request);
	            BufferedReader in = new BufferedReader
	           (new InputStreamReader(response.getEntity().getContent()));

	           StringBuffer sb = new StringBuffer("");
	           String line="";
	           while ((line = in.readLine()) != null) {
	              sb.append(line);
	              break;
	            }
	            in.close();
	            return sb.toString();
	      }catch(Exception e){
	         return new String("Exception: " + e.getMessage());
	      }
	   }
	   else{
		   try{
			   String username = (String)arg0[0];
			   String password = (String)arg0[1];  
			   Log.d(username, password);		
	           String link="http://www.keepfittracker.com/includes/insertNewUser.php";
	           String data  = URLEncoder.encode("username", "UTF-8") 
	        		   + "=" + URLEncoder.encode(username, "UTF-8");
	           data += "&" + URLEncoder.encode("password", "UTF-8") 
	        		   + "=" + URLEncoder.encode(password, "UTF-8");
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
	}
}