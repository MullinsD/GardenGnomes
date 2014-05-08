package com.example.rapidprototype;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

public class HistoryActivity extends Activity {
	final Context context = this;

	private XYPlot plot;
	private ArrayList<Double> metricsSeries;
	private ArrayList<String> timeSeries; 
	private ArrayList<String> pidList; 

	private String url = "http://people.eecs.ku.edu/~smar/garden_pi/data.php";

	// Creating JSON Parser object
	jsonParser jParser = new jsonParser();

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_DATA = "data";
	private static final String TAG_PIDS = "pids";


	public void onCreate(Bundle savedInstanceState) {
		checkNetwork();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_layout);
		getPids();
		onSubmit();
	}
	
	
	
	public void onSubmit(){ 

		Button btnSubmit = (Button) findViewById(R.id.histBtnSubmit);
		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Spinner spnMetrics = (Spinner) findViewById(R.id.spnMetrics);
				Spinner spnGran = (Spinner) findViewById(R.id.spnGranularity);
				Spinner spnPids = (Spinner)  findViewById(R.id.spnPids);

				DatePicker pckStartDate = (DatePicker) findViewById(R.id.date_picker1);
				DatePicker pckEndDate = (DatePicker) findViewById(R.id.date_picker2);
				String startDate = getFormattedDate(pckStartDate);
				String endDate = getFormattedDate(pckEndDate);
				System.out.println("Start Date: " + startDate);
				//Check Internet before Getting Data
				getData(spnMetrics, spnGran, spnPids, startDate, endDate);
				String metricName = spnMetrics.getSelectedItem().toString();
				String plotTitle = metricName + " vs. Time";
				//connects plot with layout
				plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
				if (plot.getVisibility() == 0){ plot.clear();} //clears it so that the series aren't stacking on top of each other

				XYSeries series1 = new SimpleXYSeries( metricsSeries, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);

				LineAndPointFormatter series1Format = new LineAndPointFormatter();
				series1Format.setPointLabelFormatter(new PointLabelFormatter());
				series1Format.configure(getApplicationContext(),
						R.xml.line_point_formatter_with_plf1);

				// Create a formatter to use for drawing a series using LineAndPointRenderer
				// and configure it from xml:
				//rwl.writeLock().lock();
				plot.addSeries(series1, series1Format);
				//rwl.writeLock().unlock();
				// Configure plot labels
				plot.setTitle(plotTitle);
				plot.setRangeLabel(metricName);
				//plot.setTicksPerRangeLabel(3);
				plot.getGraphWidget().setDomainLabelOrientation(-45);


				//shows and plots the graph
				plot.setVisibility(0);
				plot.redraw();


			}

			private String getFormattedDate(DatePicker dp){
				String formattedDate = "";

				int year = dp.getYear();
				int month = dp.getMonth() + 1;
				int day = dp.getDayOfMonth();

				formattedDate = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);

				return formattedDate;
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
			
			Spinner spnPids = (Spinner)  findViewById(R.id.spnPids);
			ArrayAdapter<String> ad1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pidList);
			ad1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spnPids.setPrompt("Select Pi ID");
			spnPids.setAdapter(ad1);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}


		return;
	}

	private void getData(Spinner spnMetrics, Spinner spnGran, Spinner spnPid, String startDate, String endDate){

		try {

			String metricIn = spnMetrics.getSelectedItem().toString();
			String granIn = spnGran.getSelectedItem().toString();
			String pidIn = spnPid.getSelectedItem().toString();
			granIn = granIn.substring(3);
			Log.i("Gran In ", granIn);
			String metricName = "";
			if (metricIn.equals("Temperature")){
				metricName = "temp";
			}else if (metricIn.equals("Humidity")){
				metricName = "humd";
			}else if (metricIn.equals("Soil Moisture")){
				metricName = "soil";
			}else {
				metricName = "error";
				Log.e("Invalid Spinner Entry", "ERROR: Could not comprehend spinner entry for metrics value");
			}

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pid", pidIn));
			params.add(new BasicNameValuePair("start_date", startDate));
			params.add(new BasicNameValuePair("end_date", endDate));
			params.add(new BasicNameValuePair("metrics", metricName));
			params.add(new BasicNameValuePair("gran", granIn));

			JSONObject json = jParser.getJSONFromUrl(url, params);
			metricsSeries = new ArrayList<Double>();
			timeSeries = new ArrayList<String>();
			if (json.getInt(TAG_SUCCESS)==1){
				Log.i("Successful Parsing", "Parsing of JSON Response was successful");
			}else{

				Log.e("Parsing Error", "Could not parse JSON response");
				return;
			}
			// Check your log cat for JSON reponse
			Log.d("Data: ", json.toString());

			// Getting JSON Array node
			JSONArray metrics = json.getJSONArray(TAG_DATA);

			// looping through all data
			for (int i = 0; i < metrics.length(); i++) {
				JSONObject c = metrics.getJSONObject(i);
				Double metric_data =Double.parseDouble(c.getString(metricName));
				String time = c.getString("time");
				metricsSeries.add(metric_data);
				timeSeries.add(time);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}


		return;
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
					HistoryActivity.this.finish();
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
			return;

		}
	}
}