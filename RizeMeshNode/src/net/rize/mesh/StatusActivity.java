/*
* 
*  This file is part of Rize Mesh Node
*  Copyright (C) 2011 by Michele Riso and Giuseppe Zerbo
* 
*  This file is derived from Barnacle Wifi Tether
*  Copyright (C) 2010 by Szymon Jakubczak
*
*  This program is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.rize.mesh;

import java.text.NumberFormat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Button;
import android.widget.TabHost.OnTabChangeListener;

public class StatusActivity extends android.app.TabActivity {
	private BarnacleApp app;

	private TabHost tabs;
	private ToggleButton onoff;
	//private Button announce;
	private TextView logview;
	private boolean paused;

	private TextView textDownload;
	private TextView textUpload;
	private TextView textDownloadRate;
	private TextView textUploadRate;

	final static int DLG_ABOUT = 0;
	final static int DLG_ROOT = 1;
	final static int DLG_ERROR = 2;
	final static int DLG_SUPPLICANT = 3;

	static NumberFormat nf = NumberFormat.getInstance();
	static {
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		nf.setMinimumIntegerDigits(1);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (BarnacleApp)getApplication();

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminate(true);
		setContentView(R.layout.main);
		final TextView mlog=(TextView)findViewById(R.id.mlog_info);
		mlog.setGravity(1);
		mlog.setAutoLinkMask(1);
		mlog.setLinksClickable(true);
		mlog.setText("Olsrd is not running!\n");
		// control interface
		onoff = (ToggleButton) findViewById(R.id.onoff);
		onoff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onoff.setPressed(true);
				if (onoff.isChecked()){ app.startService(); 
				onoff.setBackgroundDrawable(getResources().getDrawable(R.drawable.stop));
				mlog.setText("HttpInfo address is:\nhttp://"+app.prefs.getString("lan_gw", "localhost")+":1978/all");
				//mlog.setSelected(true);
				}else {
					app.stopService();
					onoff.setBackgroundDrawable(getResources().getDrawable(R.drawable.start));
					mlog.setText("Olsrd is not running!\n");
					//mlog.setSelected(false);
				}
			}
		});
//		announce = (Button) findViewById(R.id.announce);
//		announce.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (app.service != null) app.service.assocRequest();
//				else v.setEnabled(false);
//			}
//		});

		tabs = getTabHost();

		tabs.addTab(tabs.newTabSpec("log")
				.setIndicator("", getResources().getDrawable(R.drawable.ic_tab_app))
				.setContent(R.id.logview));
		tabs.addTab(tabs.newTabSpec("traffic")
				.setIndicator("", getResources().getDrawable(R.drawable.ic_tab_starred))
				.setContent(R.id.traffic));
		tabs.addTab(tabs.newTabSpec("clients")
				.setIndicator("", getResources().getDrawable(R.drawable.ic_tab_contacts))
				.setContent(new Intent(this, ClientsActivity.class)));
		tabs.addTab(tabs.newTabSpec("olsr")
				.setIndicator("", getResources().getDrawable(R.drawable.ic_tab_olsr))
				.setContent(new Intent(this, GetInfoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
		
		tabs.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				update();
				if ("traffic".equals(tabId)) {
					// force refresh
					BarnacleService svc = app.service;
					if (svc != null)
						svc.statsRequest(0);
				}
			}
		});

		logview = (TextView) findViewById(R.id.log_text);
		app.setStatusActivity(this);
		paused = false;
		//onoff.setEnabled(false);
		//announce.setEnabled(false);

		textDownload = ((TextView)findViewById(R.id.download));
		textUpload = ((TextView)findViewById(R.id.upload));
		textDownloadRate = ((TextView)findViewById(R.id.download_rate));
		textUploadRate = ((TextView)findViewById(R.id.upload_rate));
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		app.setStatusActivity(null);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_prefs:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.menu_about:
			showDialog(DLG_ABOUT);
			return true;
		case R.id.menu_olsrd_conf:
			startActivity(new Intent(this, OlsrdSettings.class));
			break;
			
		}
		return(super.onOptionsItemSelected(item));
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO: these should not create and remove dialogs, but restore and dismiss
		if (id == DLG_ABOUT) {
			return (new AlertDialog.Builder(this))
			.setIcon(android.R.drawable.ic_dialog_info)
			.setTitle("Help")
			.setMessage("Rize Mesh Node was developed by Michele Riso and Giuseppe Zerbo.\n\nRize Mesh Node is derived from Barnacle, developed by szym.net."
					+"\n\n"
					+"Press the central button to start Mesh. ")
/*					.setPositiveButton("Donate", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Uri uri = Uri.parse(getString(R.string.paypalUrl));
							startActivity(new Intent(Intent.ACTION_VIEW, uri));
						}})
						.setNeutralButton("Website", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Uri uri = Uri.parse(getString(R.string.websiteUrl));
								startActivity(new Intent(Intent.ACTION_VIEW, uri));
							}})*/
							.setNegativeButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) { removeDialog(DLG_ABOUT); }})
								.create();
		}
		if (id == DLG_ROOT) {
			return (new AlertDialog.Builder(this))
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Root Access")
			.setMessage("RizeMeshNode requires 'su' to access the hardware! Please, make sure you have root access.")
			/*.setPositiveButton("Help", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Uri uri = Uri.parse(getString(R.string.rootUrl));
					startActivity(new Intent(Intent.ACTION_VIEW, uri));
				}})*/
				.setNegativeButton("Close", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) { removeDialog(DLG_ROOT); }})
					.create();
		}
		if (id == DLG_ERROR) {
			return (new AlertDialog.Builder(this))
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Error")
			.setMessage("Unexpected error occured!\n\nRize Mesh App was stopped!")
/*			.setPositiveButton("Help", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Uri uri = Uri.parse(getString(R.string.fixUrl));
					startActivity(new Intent(Intent.ACTION_VIEW, uri));
				}})
				.setNegativeButton("Close", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) { removeDialog(DLG_ERROR); }})*/
					.create();
		}
		if (id == DLG_SUPPLICANT) {
			return (new AlertDialog.Builder(this))
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Supplicant not available")
			.setMessage("Rize Mesh App had trouble starting wpa_supplicant. Try again but set 'Skip wpa_supplicant' in settings.")
			.setPositiveButton("Do it now!", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					app.prefs.edit().putBoolean(getString(R.string.lan_wext), true).commit();
					app.updateToast("Settings updated, try again...", true);
				}})
				/*.setNeutralButton("More info", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Uri uri = Uri.parse(getString(R.string.wikiUrl));
						startActivity(new Intent(Intent.ACTION_VIEW, uri));
					}})*/
					.setNegativeButton("Close", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) { removeDialog(DLG_ROOT); }})
						.create();
		}
		return null;
	}

	@Override
	protected void onPause() {
		super.onPause();
		paused = true;
	}
	@Override
	protected void onResume() {
		super.onResume();
		paused = false;
		update();
		app.cleanUpNotifications();
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (BarnacleApp.ACTION_CLIENTS.equals(intent.getAction())) {
			getTabHost().setCurrentTab(2); // show clients
		}
	}

	static String format(long v) {
		if (v < 1000000) return nf.format(v /    1000.0f) + " kB";
		else             return nf.format(v / 1000000.0f) + " MB";
	}

	void update() {
		int state = app.getState();

		if (app.log != null)
			logview.setText(app.log);

		if (state == BarnacleService.STATE_STOPPED) {
			//announce.setEnabled(false);
			onoff.setChecked(false);
			return; // not ready yet! keep the old log
		}

		BarnacleService svc = app.service;
		if (svc == null) return; // unexpected race condition

		if (state == BarnacleService.STATE_STARTING) {
			setProgressBarIndeterminateVisibility(true);
			onoff.setPressed(true);
			onoff.setChecked(true);
			return;
		}

		if (state != BarnacleService.STATE_RUNNING) {
			// this is unexpected, but don't fail
			return;
		}

		// STATE_RUNNING
		setProgressBarIndeterminateVisibility(false);
		//announce.setEnabled(true);
		onoff.setPressed(false);
		onoff.setChecked(true);
		Util.TrafficStats stats = svc.stats;
		textDownload.setText(format(stats.total.tx_bytes));
		textUpload.setText(format(stats.total.rx_bytes));
		textDownloadRate.setText(format(stats.rate.tx_bytes)+"/s");
		textUploadRate.setText(format(stats.rate.rx_bytes)+"/s");
		// only request stats when visible
		if (!paused && (getTabHost().getCurrentTab() == 1))
			svc.statsRequest(1000);
	}
}

