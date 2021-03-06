/*
 * Copyright (C) 2012 ENTERTAILION, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.entertailion.android.overlay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.entertailion.android.overlay.utils.Analytics;
import com.entertailion.android.overlay.utils.Utils;

/**
 * Main UI to let the user configure the app
 * 
 */
public class ConfigActivity extends Activity {
	public static final String LOG_CAT = "ConfigActivity";
	public static final String PREFS_NAME = "preferences";
	public static String PREFERENCE_TYPE = "preference.type";
	public static String PREFERENCE_TYPE_ANDROID = "preference.type.android";
	public static String PREFERENCE_TYPE_SNOW = "preference.type";
	public static String PREFERENCE_TYPE_CHRISTMAS = "preference.type.christmas";
	public static String PREFERENCE_TYPE_CHRISTMAS_LIGHTS = "preference.type.christmaslights";
	public static String PREFERENCE_TYPE_NEW_YEARS = "preference.type.newyears";
	public static String PREFERENCE_TYPE_SMILEYS = "preference.type.smileys";
	public static String PREFERENCE_TYPE_STARS = "preference.type.stars";
	public static String PREFERENCE_TYPE_VALENTINES = "preference.type.valentines";
	public static String PREFERENCE_TYPE_DEFAULT = PREFERENCE_TYPE_ANDROID;
	public static String PREFERENCE_TIMING = "preference.timing";
	public static int PREFERENCE_TIMING_DEFAULT = 60;
	public static String PREFERENCE_DURATION = "preference.duration";
	public static int PREFERENCE_DURATION_DEFAULT = 2;
	public static String PREFERENCE_ON_OFF = "preference.onoff";
	public static final int CONFIG_COUNT = 20;
	public static String LAST_TIME_RUN = "last.time.run";
	private MoverView moverView;
	private Spinner typeSpinner;
	private Spinner timingSpinner;
	private Spinner durationSpinner;
	private ToggleButton toggleButton;
	private int width, height;
	private Handler handler = new Handler();
	private boolean changed; // track configuration changes

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.config);

		moverView = (MoverView) findViewById(R.id.moverView);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;

		typeSpinner = (Spinner) findViewById(R.id.spinnerType);
		List<String> typeList = new ArrayList<String>();
		typeList.add(getString(R.string.type_1));
		typeList.add(getString(R.string.type_2));
		typeList.add(getString(R.string.type_3));
		typeList.add(getString(R.string.type_4));
		typeList.add(getString(R.string.type_5));
		typeList.add(getString(R.string.type_6));
		typeList.add(getString(R.string.type_7));
		typeList.add(getString(R.string.type_8));
		ArrayAdapter<String> typeDataAdapter = new ArrayAdapter<String>(ConfigActivity.this, android.R.layout.simple_spinner_item, typeList);
		typeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinner.setAdapter(typeDataAdapter);
		typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				changed = true;
				String type = PREFERENCE_TYPE_ANDROID;
				switch (pos) {
				case 0: // Android
					type = PREFERENCE_TYPE_ANDROID;
					Mover androidMover = new AndroidMover(ConfigActivity.this, width, height, CONFIG_COUNT, true);
					moverView.setMover(androidMover);
					break;
				case 1: // Snow
					type = PREFERENCE_TYPE_SNOW;
					Mover snowMover = new SnowMover(ConfigActivity.this, width, height, CONFIG_COUNT, true);
					moverView.setMover(snowMover);
					break;
				case 2: // Christmas
					type = PREFERENCE_TYPE_CHRISTMAS;
					Mover christmasMover = new ChristmasMover(ConfigActivity.this, width, height, CONFIG_COUNT, true);
					moverView.setMover(christmasMover);
					break;
				case 3: // Christmas lights
					type = PREFERENCE_TYPE_CHRISTMAS_LIGHTS;
					Mover christmasLightsMover = new ChristmasLightsMover(ConfigActivity.this, width, height, CONFIG_COUNT, true);
					moverView.setMover(christmasLightsMover);
					break;
				case 4: // New Years
					type = PREFERENCE_TYPE_NEW_YEARS;
					Mover newYearsMover = new NewYearsMover(ConfigActivity.this, width, height, CONFIG_COUNT, true);
					moverView.setMover(newYearsMover);
					break;
				case 5: // Smileys
					type = PREFERENCE_TYPE_SMILEYS;
					Mover smileysMover = new SmileyMover(ConfigActivity.this, width, height, CONFIG_COUNT, true);
					moverView.setMover(smileysMover);
					break;
				case 6: // Stars
					type = PREFERENCE_TYPE_STARS;
					Mover starsMover = new StarsMover(ConfigActivity.this, width, height, CONFIG_COUNT, true);
					moverView.setMover(starsMover);
					break;
				case 7: // Valentines
					type = PREFERENCE_TYPE_VALENTINES;
					Mover valentinesMover = new ValentinesMover(ConfigActivity.this, width, height, CONFIG_COUNT, true);
					moverView.setMover(valentinesMover);
					break;
				default:
					break;
				}

				SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = prefs.edit();
				edit.putString(PREFERENCE_TYPE, type);
				edit.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
		final SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
		String type = preferences.getString(PREFERENCE_TYPE, PREFERENCE_TYPE_DEFAULT);
		if (type.equals(PREFERENCE_TYPE_ANDROID)) {
			typeSpinner.setSelection(0); // android
		} else if (type.equals(PREFERENCE_TYPE_SNOW)) {
			typeSpinner.setSelection(1); // snow
		} else if (type.equals(PREFERENCE_TYPE_CHRISTMAS)) {
			typeSpinner.setSelection(2); // christmas
		} else if (type.equals(PREFERENCE_TYPE_CHRISTMAS_LIGHTS)) {
			typeSpinner.setSelection(3); // christmas lights
		} else if (type.equals(PREFERENCE_TYPE_NEW_YEARS)) {
			typeSpinner.setSelection(4); // new years
		} else if (type.equals(PREFERENCE_TYPE_SMILEYS)) {
			typeSpinner.setSelection(5); // smileys
		} else if (type.equals(PREFERENCE_TYPE_STARS)) {
			typeSpinner.setSelection(6); // stars
		} else if (type.equals(PREFERENCE_TYPE_VALENTINES)) {
			typeSpinner.setSelection(7); // valentines
		}

		timingSpinner = (Spinner) findViewById(R.id.spinnerTiming);
		List<String> timingList = new ArrayList<String>();
		timingList.add(getString(R.string.timing_1));
		timingList.add(getString(R.string.timing_2));
		timingList.add(getString(R.string.timing_3));
		ArrayAdapter<String> timingDataAdapter = new ArrayAdapter<String>(ConfigActivity.this, android.R.layout.simple_spinner_item, timingList);
		timingDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		timingSpinner.setAdapter(timingDataAdapter);
		timingSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				changed = true;
				int timing = 60;
				switch (pos) {
				case 0: // 30 mins
					timing = 30;
					break;
				case 1: // 1 hours
					timing = 60;
					break;
				case 2: // 2 hours
					timing = 120;
					break;
				default:
					break;
				}
				SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = prefs.edit();
				edit.putInt(PREFERENCE_TIMING, timing);
				edit.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
		int timing = preferences.getInt(PREFERENCE_TIMING, PREFERENCE_TIMING_DEFAULT);
		switch (timing) {
		case 30: // 30 mins
			timingSpinner.setSelection(0);
			break;
		case 60: // 1 hours
			timingSpinner.setSelection(1);
			break;
		case 120: // 2 hours
			timingSpinner.setSelection(2);
			break;
		default:
			break;
		}

		durationSpinner = (Spinner) findViewById(R.id.spinnerDuration);
		List<String> durationList = new ArrayList<String>();
		durationList.add(getString(R.string.duration_1));
		durationList.add(getString(R.string.duration_2));
		durationList.add(getString(R.string.duration_3));
		durationList.add(getString(R.string.duration_4));
		ArrayAdapter<String> durationDataAdapter = new ArrayAdapter<String>(ConfigActivity.this, android.R.layout.simple_spinner_item, durationList);
		durationDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		durationSpinner.setAdapter(durationDataAdapter);
		durationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				changed = true;
				SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = prefs.edit();
				edit.putInt(PREFERENCE_DURATION, pos + 1);
				edit.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
		int duration = preferences.getInt(PREFERENCE_DURATION, PREFERENCE_DURATION_DEFAULT);
		durationSpinner.setSelection(duration - 1);

		toggleButton = (ToggleButton) findViewById(R.id.onOffButton);
		toggleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean checked = toggleButton.isChecked();
				SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = prefs.edit();
				edit.putBoolean(PREFERENCE_ON_OFF, checked);
				edit.commit();

				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
				Intent alarmIntent = new Intent(ConfigActivity.this, AlarmReceiver.class);
				alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(ConfigActivity.this, 0, alarmIntent, 0);
				if (checked) { // ON
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					if (calendar.get(Calendar.MINUTE) < 30) {
						calendar.set(Calendar.MINUTE, 30);
					} else {
						calendar.set(Calendar.MINUTE, 0);
						calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
					}

					// configure the alarm manager to invoke the mover activity
					// every 30 mins
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 30, pendingIntent);

					int timing = preferences.getInt(PREFERENCE_TIMING, PREFERENCE_TIMING_DEFAULT);
					// set the default for first time run
					edit.putLong(ConfigActivity.LAST_TIME_RUN, System.currentTimeMillis() - (timing + 1) * 1000 * 60); 
					edit.commit();
				} else {
					alarmManager.cancel(pendingIntent);
					pendingIntent.cancel();
				}
			}

		});
		boolean onOff = preferences.getBoolean(PREFERENCE_ON_OFF, false);
		toggleButton.setChecked(onOff);

		// Set the context for Google Analytics
		Analytics.createAnalytics(this);
		Utils.logDeviceInfo(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Start Google Analytics for this activity
		Analytics.startAnalytics(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Stop Google Analytics for this activity
		Analytics.stopAnalytics(this);
	}

	/**
	 * @see android.app.Activity#onPause()
	 */
	public void onPause() {
		if (changed && toggleButton.isChecked()) {
			// show the current mover if the user changed the config and goes to
			// live TV
			handler.post(new Runnable() {
				public void run() {
					AlarmReceiver.startMover(ConfigActivity.this, true);
				}
			});
		}
		super.onPause();
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	public void onResume() {
		super.onResume();
		changed = false;

		Analytics.logEvent(Analytics.OVERLAY_CONFIG);
	}

}
