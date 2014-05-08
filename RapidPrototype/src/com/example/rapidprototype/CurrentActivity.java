package com.example.rapidprototype;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import android.widget.CompoundButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.view.View;
import android.view.View.OnClickListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;


public class CurrentActivity extends Activity {
	Button btnSubmit;

	final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

	// Creating JSON Parser object
	jsonParser jParser = new jsonParser();

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_DATA = "data";
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.current_layout);
		
	    handleButtons();	
		//onSubmit();
        getMetrics();
	}
	
	private void handleButtons()
	{
		ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) 
		        {
		        	String process_settings_url = "http://people.eecs.ku.edu/~drmullin/GardenGnomesWebApp/on_off.php";
		    		try {
		    			
		    			List<NameValuePair> params = new ArrayList<NameValuePair>();
		    			params.add(new BasicNameValuePair("Light", "on"));
		    			

		    			JSONObject json = jParser.getJSONFromUrl(process_settings_url, params);
		    			
		    			if (json.getInt(TAG_SUCCESS)==1){
		    				Log.i("Test", "Light Changed");
		    			}else{

		    				Log.e("Settings Error", "Could not get JSON Response");
		    			
		    			}
		    			// Check your log cat for JSON reponse
		    			Log.d("POST Data: ", json.toString());
		    		} catch (JSONException e) {
		    			e.printStackTrace();
		    		
		    		}


		        }
		         else {
		        	 String process_settings_url = "http://people.eecs.ku.edu/~drmullin/GardenGnomesWebApp/on_off.php";
			    		try {
			    			
			    			List<NameValuePair> params = new ArrayList<NameValuePair>();
			    			params.add(new BasicNameValuePair("Light", "off"));
			    			

			    			JSONObject json = jParser.getJSONFromUrl(process_settings_url, params);
			    			
			    			if (json.getInt(TAG_SUCCESS)==1){
			    				Log.i("Configurations Completed", "Settings have been set.");
			    			}else{

			    				Log.e("Settings Error", "Could not get JSON Response");
			    			
			    			}
			    			// Check your log cat for JSON reponse
			    			Log.d("POST Data: ", json.toString());
			    		} catch (JSONException e) {
			    			e.printStackTrace();
			    		
			    		}
		            
		        }
		    }
		});
		
		ToggleButton water = (ToggleButton) findViewById(R.id.toggleButton1);
		water.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) 
		        {
		        	String process_settings_url = "http://people.eecs.ku.edu/~drmullin/GardenGnomesWebApp/on_off_sprinkler.php";
		    		try {
		    			
		    			List<NameValuePair> params = new ArrayList<NameValuePair>();
		    			params.add(new BasicNameValuePair("Water", "on"));
		    			

		    			JSONObject json = jParser.getJSONFromUrl(process_settings_url, params);
		    			
		    			if (json.getInt(TAG_SUCCESS)==1){
		    				Log.i("Test", "Sprinkler Changed");
		    			}else{

		    				Log.e("Settings Error", "Could not get JSON Response");
		    			
		    			}
		    			// Check your log cat for JSON reponse
		    			Log.d("POST Data: ", json.toString());
		    		} catch (JSONException e) {
		    			e.printStackTrace();
		    		
		    		}


		        }
		         else {
		        	 String process_settings_url = "http://people.eecs.ku.edu/~drmullin/GardenGnomesWebApp/on_off_sprinkler.php";
			    		try {
			    			
			    			List<NameValuePair> params = new ArrayList<NameValuePair>();
			    			params.add(new BasicNameValuePair("Water", "off"));
			    			

			    			JSONObject json = jParser.getJSONFromUrl(process_settings_url, params);
			    			
			    			if (json.getInt(TAG_SUCCESS)==1){
			    				Log.i("Configurations Completed", "Settings have been set.");
			    			}else{

			    				Log.e("Settings Error", "Could not get JSON Response");
			    			
			    			}
			    			// Check your log cat for JSON reponse
			    			Log.d("POST Data: ", json.toString());
			    		} catch (JSONException e) {
			    			e.printStackTrace();
			    		
			    		}
		            
		        }
		    }
		});
		
	}
	private void updateText(String t, String h, String s, String l, String w) {
		ToggleButton light = (ToggleButton) findViewById(R.id.toggleButton);
		ToggleButton water = (ToggleButton) findViewById(R.id.toggleButton1);
		TextView temp = (TextView) findViewById(R.id.Temp);
		temp.setText("Current Temperature: " + t + (char) 0x00B0 + " F");
		TextView soil = (TextView) findViewById(R.id.Soil);
		
		if(Integer.parseInt(s) < 510)
			soil.setText("Current Soil Moisture: " + s + "Consider watering your System");
		else
			soil.setText("Current Soil Moisture: " + s);
		
		TextView humd = (TextView) findViewById(R.id.Humd);
		humd.setText("Current Humidity: " + h + "%");

		if(l.equals("on"))
		{
			light.setChecked(true);
		}
		else if(l.equals("off"))
		{	
			light.setChecked(false);
		}
		if(w.equals("on"))
		{
			water.setChecked(true);
		}
		else if(w.equals("off"))
		{	
			water.setChecked(false);
		}
	}
/*public void onSubmit(){ 

		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {


			}

		});
	}*/


private void getMetrics(){
		try{
			String pid_url = "http://people.eecs.ku.edu/~drmullin/GardenGnomesWebApp/get_metrics.php";
			JSONObject json = jParser.getJSONFromUrl(pid_url, null);
			if (json.getInt(TAG_SUCCESS)==1){
				Log.i("Successful Parsing", "Parsing of JSON Response was successful");
			}else{
				Log.e("Parsing Error", "Could not parse JSON response");
				return;
			}
			Log.d("Post Data: ", json.toString());
			// Getting JSON Array node

			updateText(json.getString("temp"), json.getString("humd"), json.getString("soil"), json.getString("light"), json.getString("water"));
			Log.i("Test", "Text Updated");

		} catch (JSONException e) {
			e.printStackTrace();
		}


		return;

}
}





