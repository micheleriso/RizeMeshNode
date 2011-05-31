/*
* 
*  This file is part of Rize Mesh Node
*  Copyright (C) 2011 by Michele Riso and Giuseppe Zerbo
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GetInfoActivity extends Activity {

	final static String TAG = "GetInfoActivity";
	Process process=null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getinfo);

		refresh(); //invoco la visualizzazione dei dati all'avvio del tab

		final Button refresh=(Button)findViewById(R.id.button_info);

		refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				refresh();

			}
		});
	}


	private void refresh(){

		final TextView log=(TextView)findViewById(R.id.log_info);

		log.setText(""); //ripulisco la finestra

		if ( startProcess() ){

			try{
				FileInputStream fIn = null; 
				InputStreamReader isr = null;
				fIn = openFileInput("log.dat");       
				isr = new InputStreamReader(fIn); 
				BufferedReader br = new BufferedReader(isr);
				String s;
				while((s = br.readLine()) != null) {

					log.append(s);
					log.append("\n");
				}
				isr.close(); 
				fIn.close(); 
			}catch(Exception e){
				Log.e("Errore Lettura File",e.toString());
				log.setText("There is no log.\nHave you started Olsrd?");
				Toast.makeText(getBaseContext(), "There is no log.\nHave you started Olsrd?", Toast.LENGTH_LONG).show();
			}
		}
		else Toast.makeText(getBaseContext(), "Failed to get olsr log", Toast.LENGTH_LONG).show();

	}

	private boolean startProcess() {
		// start the process
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("./get_info").directory(getFilesDir());
			// TODO: consider putting brncl.ini in pb.environment() instead of using ./setup
			process = pb.start(); //Runtime.getRuntime().exec(cmd);

		} catch (Exception e) {
			Log.e(TAG, "start failed " + e.toString());
			return false;
		}
		return true;
	}


}
