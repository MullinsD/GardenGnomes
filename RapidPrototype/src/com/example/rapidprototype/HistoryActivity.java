package com.example.rapidprototype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

public class HistoryActivity extends Activity {
	Button btnSubmit;
	private XYPlot plot;
	private ArrayList<Number> metricsSeries;
	private ArrayList<String> timeSeries; 
	private String url = "http://people.eecs.ku.edu/~smar/garden_pi/data.php";
	
	// Creating JSON Parser object
	jsonParser jParser = new jsonParser();

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_DATA = "data";

	JSONArray metrics = null;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_layout);
		onSubmit();

	}
	public void onSubmit(){ 
		
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Spinner spnMetrics = (Spinner) findViewById(R.id.spinner1);
				getData(spnMetrics);
				plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
				String plotTitle = spnMetrics.getSelectedItem().toString() + " vs. Time";

				XYSeries series1 = new SimpleXYSeries(
						metricsSeries,          // SimpleXYSeries takes a List so turn our array into a List
						SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
						plotTitle); 

				LineAndPointFormatter series1Format = new LineAndPointFormatter();
				series1Format.setPointLabelFormatter(new PointLabelFormatter());
				series1Format.configure(getApplicationContext(),
						R.xml.line_point_formatter_with_plf1);
				// Create a formatter to use for drawing a series using LineAndPointRenderer
				// and configure it from xml:
				plot.addSeries(series1, series1Format);

				// reduce the number of range labels
				plot.setTicksPerRangeLabel(3);
				plot.getGraphWidget().setDomainLabelOrientation(-45);
			}

		});
	}
	
	private void getData(Spinner spnMetrics){
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			try {
				JSONObject json = jParser.makeHttpRequest(url, "POST", params);

				// Check your log cat for JSON reponse
				Log.d("Data: ", json.toString());
				String metric_in = spnMetrics.getSelectedItem().toString();
				String metric_name = "";
				
				if (metric_in.equals("Temperature")){
					metric_name = "temp";
				}else if (metric_in.equals("Humidity")){
					metric_name = "humd";
				}else if (metric_in.equals("Soil Moisture")){
					metric_name = "soil";
				}else {
					metric_name = "error";
					Log.e("Invalid Spinner Entry", "ERROR: Could not comprehend spinner entry for metrics value");
				}
				// Getting JSON Array node
				metrics = json.getJSONArray(TAG_DATA);

				// looping through All Contacts
				for (int i = 0; i < metrics.length(); i++) {
					JSONObject c = metrics.getJSONObject(i);

					String time = c.getString("time");
					Double metric_data = Double.parseDouble(c.getString(metric_name));
					metricsSeries.add(metric_data);
					timeSeries.add(time);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}


			return;
	}
}
	/**
	 * Background Async Task to Create new product
	 * *
	class GetJSONData extends AsyncTask<String, String, String> {
	
		

		@Override
		protected String doInBackground(String... arg0) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url, "GET", params);

			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			String metric_name = "";
			if (metric_in == "Temperature"){
				metric_name = "temp";
			}else if (metric_in == "Humidity"){
				metric_name = "humd";
			}else if (metric_in == "Soil Moisture"){
				metric_name = "soil";
			}else {
				metric_name = "error";
				Log.e("Invalid Spinner Entry", "ERROR: Could not comprehend spinner entry for metrics value");
			}

			try {

				// Getting JSON Array node
				metrics = json.getJSONArray(TAG_DATA);

				// looping through All Contacts
				for (int i = 0; i < metrics.length(); i++) {
					JSONObject c = metrics.getJSONObject(i);

					String time = c.getString("time");
					Double metric_data = Double.parseDouble(c.getString(metric_name));
					metricsSeries.add(metric_data);
					timeSeries.add(time);

					/* // tmp hashmap for single contact
	                         HashMap<String, String> metric = new HashMap<String, String>();

	                         // adding each child node to HashMap key => value
	                         metric.put("time", time);
	                         metric.put(metric_name, metric_data);

	                         // adding contact to contact list
	                         metricsList.add(metric);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}


			return null;
		}*/
