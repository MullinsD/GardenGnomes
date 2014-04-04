package com.example.rapidprototype;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
	Button btnSubmit;
	private XYPlot plot;
	private ArrayList<Double> metricsSeries;
	private ArrayList<String> timeSeries; 
	private String url = "http://people.eecs.ku.edu/~smar/garden_pi/data.php";
	final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

	// Creating JSON Parser object
	jsonParser jParser = new jsonParser();

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_DATA = "data";
	

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
				DatePicker pckStartDate = (DatePicker) findViewById(R.id.date_picker1);
				DatePicker pckEndDate = (DatePicker) findViewById(R.id.date_picker2);
				String startDate = getFormattedDate(pckStartDate);
				String endDate = getFormattedDate(pckEndDate);
				System.out.println("Start Date: " + startDate);
				//2014-03-14 
				getData(spnMetrics, startDate, endDate);
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
				int month = dp.getMonth();
				int day = dp.getDayOfMonth();

				formattedDate = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);

				return formattedDate;
			}

		});
	}

	private void getData(Spinner spnMetrics, String startDate, String endDate){

		try {

			String metricIn = spnMetrics.getSelectedItem().toString();
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
			params.add(new BasicNameValuePair("start_date", startDate));
			params.add(new BasicNameValuePair("end_date", endDate));
			params.add(new BasicNameValuePair("metrics", metricName));

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
