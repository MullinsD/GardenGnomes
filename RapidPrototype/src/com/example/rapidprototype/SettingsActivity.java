package com.example.rapidprototype;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class SettingsActivity extends Activity {

	final Context context = this;
	Button btnSubmit;

	ArrayList<String> pidList;

	// Creating JSON Parser object
	jsonParser jParser = new jsonParser();
	private String url = "http://people.eecs.ku.edu/~smar/garden_pi/data.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_ERROR = "err_msg";
	private static final String TAG_PIDS = "pids";
	private TextView seekTempValue;
	private TextView seekMoistureValue;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		setWidgets();
		checkNetwork();
		getPids();
		onSubmit();
	}

	public void setWidgets(){
		ArrayList<Integer> minList = new ArrayList<Integer>();
		ArrayList<Integer> secList = new ArrayList<Integer>();
		for (int i = 0; i < 60; i++){
			minList.add(i);
			secList.add(i);
		}		
		Spinner spnMins = (Spinner)  findViewById(R.id.spnDurationMin);
		Spinner spnSecs = (Spinner)  findViewById(R.id.spnDurationSec);
		ArrayAdapter<Integer> adMin = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, minList);
		ArrayAdapter<Integer> adSec = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, secList);
		adMin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adSec.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnMins.setAdapter(adMin);
		spnSecs.setAdapter(adSec);

		TimePicker tp = (TimePicker) this.findViewById(R.id.timePickInterval);
		tp.setIs24HourView(true);
		onSeekChange();


	}
	public void onSeekChange(){
		SeekBar seekTemp = (SeekBar)findViewById(R.id.seekTemp); 
		seekTempValue = (TextView)findViewById(R.id.seekTempValue); 
		seekTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 
			@Override 
			public void onProgressChanged(SeekBar seekTemp, int progress, boolean fromUser) { 
				// TODO Auto-generated method stub 
				int adjusted_progress = progress + 60;
				seekTempValue.setText(String.valueOf(adjusted_progress)); 
			}

			@Override 
			public void onStartTrackingTouch(SeekBar seekTemp) { 
				// TODO Auto-generated method stub 
			} 

			@Override 
			public void onStopTrackingTouch(SeekBar seekTemp) { 
				// TODO Auto-generated method stub 
			} 
		});

		SeekBar seekMoisture = (SeekBar)findViewById(R.id.seekMoisture); 
		seekMoistureValue = (TextView)findViewById(R.id.seekMoistureValue);
		seekMoisture.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 
			@Override 
			public void onProgressChanged(SeekBar seekMoisture, int progress, boolean fromUser) { 
				// TODO Auto-generated method stub 
				int adjusted_progress = progress + 300;
				seekMoistureValue.setText(String.valueOf(adjusted_progress)); 
			}

			@Override 
			public void onStartTrackingTouch(SeekBar seekMoisture) { 
				// TODO Auto-generated method stub 
			} 

			@Override 
			public void onStopTrackingTouch(SeekBar seekMoisture) { 
				// TODO Auto-generated method stub 
			} 
		}); 
	} 

	public void onSubmit(){ 

		btnSubmit = (Button) findViewById(R.id.setBtnSubmit);
		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (getData() < 0){
					Log.e("Settings Submission Error", "Could not send settings info to database.");
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage("Unable to submit settings. Please check connection and try again.")
					.setTitle("Error")
					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {

						}
					});
					AlertDialog dialog = builder.create();
					dialog.show();
				}else{
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage("The settings have been updated.")
					.setTitle("Confirmation")
					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {

						}
					});
					AlertDialog dialog = builder.create();
					dialog.show();					

				}


			}

		});
	}

	private void getPids(){
		try{
			String pid_url = "http://people.eecs.ku.edu/~smar/garden_pi/get_pid.php";
			JSONObject json = jParser.getJSONFromUrl(pid_url, null);
			pidList = new ArrayList<String>();
			if (json.getInt(TAG_SUCCESS)==1){
				Log.i("Successful Parsing", "Parsing of JSON Response was successful");
			}else{
				Log.e("Parsing Error", "Could not parse JSON response");
				return;
			}
			// Check your log cat for JSON reponse

			// Getting JSON Array node
			JSONArray pids = json.getJSONArray(TAG_PIDS);

			// looping through all data
			for (int i = 0; i < pids.length(); i++) {
				String pid = pids.getString(i);
				Log.d("PID", pids.getString(i));
				pidList.add(pid);
			}

			Spinner spnPids = (Spinner)  findViewById(R.id.setSpnPid);
			ArrayAdapter<String> ad2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pidList);
			ad2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spnPids.setAdapter(ad2);

		} catch (JSONException e) {
			e.printStackTrace();
		}


		return;
	}

	private int getData(){
		String process_settings_url = "http://people.eecs.ku.edu/~smar/garden_pi/process_settings.php";
		try {
			Spinner spnPids = (Spinner)  findViewById(R.id.setSpnPid);
			String pidIn = spnPids.getSelectedItem().toString();

			Spinner spnMin = (Spinner)  findViewById(R.id.spnDurationMin);
			String minIn = spnMin.getSelectedItem().toString();

			Spinner spnSec = (Spinner)  findViewById(R.id.spnDurationSec);
			String secIn = spnSec.getSelectedItem().toString();

			String tempIn = seekTempValue.getText().toString();
			String moistureIn = seekMoistureValue.getText().toString();

			TimePicker startPick = (TimePicker) findViewById(R.id.timePickStart);
			String startHour = startPick.getCurrentHour().toString();
			String startMin = startPick.getCurrentMinute().toString();

			TimePicker intPick = (TimePicker) findViewById(R.id.timePickInterval);
			String intHour = intPick.getCurrentHour().toString();
			String intMin = intPick.getCurrentMinute().toString();

			String autoIn = "";
			CheckBox auto = (CheckBox) findViewById(R.id.checkAuto);
			if(auto.isChecked()){
				autoIn = "yes";
			}else{
				autoIn = "no";
			}

			String daysIn = getCheckedDays();
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pid", pidIn));
			params.add(new BasicNameValuePair("temp", tempIn));
			params.add(new BasicNameValuePair("soil_moisture", moistureIn));
			params.add(new BasicNameValuePair("days", daysIn));
			params.add(new BasicNameValuePair("durmins", minIn));
			params.add(new BasicNameValuePair("dursecs", secIn));
			params.add(new BasicNameValuePair("starthours", startHour));
			params.add(new BasicNameValuePair("startmins", startMin));
			params.add(new BasicNameValuePair("runhours", intHour));
			params.add(new BasicNameValuePair("runmins", intMin));
			params.add(new BasicNameValuePair("auto", autoIn));

			String paramList = "";
			for (int i = 0; i < params.size(); i++){
				paramList = paramList + params.get(i) + "\n";
			}

			Log.i("Setting Parameters", paramList);
			JSONObject json = jParser.getJSONFromUrl(process_settings_url, params);

			if (json.getInt(TAG_SUCCESS)==1){
				Log.i("Configurations Completed", "Settings have been set.");
			}else if (json.getInt(TAG_SUCCESS) == 0){
				Log.e("Database Error.", json.getString(TAG_ERROR));
				return -1;
			}else{
				Log.e("Settings Error", "Could not get JSON Response");
				return -1;
			}
			// Check your log cat for JSON response
			Log.d("POST Data: ", json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		}


		return 0;
	}
	
	private String getCheckedDays(){
		String days = "";
		CheckBox sun = (CheckBox) findViewById(R.id.checkBox1);
		CheckBox mon = (CheckBox) findViewById(R.id.checkBox2);
		CheckBox tues = (CheckBox) findViewById(R.id.checkBox3);
		CheckBox wed = (CheckBox) findViewById(R.id.checkBox4);
		CheckBox thurs = (CheckBox) findViewById(R.id.checkBox5);
		CheckBox fri = (CheckBox) findViewById(R.id.checkBox6);
		CheckBox sat = (CheckBox) findViewById(R.id.checkBox7);

		if(sun.isChecked())
			days = days + "sun" + "|";
		if(mon.isChecked())
			days = days + "mon" + "|";
		if(tues.isChecked())
			days = days + "tues" + "|";	
		if(wed.isChecked())
			days = days + "wed" + "|";
		if(thurs.isChecked())
			days = days + "thurs" + "|";
		if(fri.isChecked())
			days = days + "fri" + "|";
		if(sat.isChecked())
			days = days + "sat" + "|";
		
		return days;
		
	}

	private void checkNetwork(){
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()){
			Log.i("Network Connection", "Network is Connected.");
			return;
		}else{
			Log.e("Network Connection Error", "Could not connect to Internet");
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage("No Network Connection! \n Connect and try again.")
			.setTitle("Error")
			.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					SettingsActivity.this.finish();
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
			return;

		}
	}

}
