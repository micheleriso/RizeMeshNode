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
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OlsrdSettings extends Activity {

	OlsrdSettings olsrdsettings=this;
	final static String TAG = "OlsrdSettings";

	@Override
	public void onCreate(Bundle savedInstanceState){	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.olsrdconfig);

		reload(); //ricarico il file di configurazione all'avvio dell'activity

		final Button reloadButton=(Button)findViewById(R.id.button_reload);
		reloadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				new AlertDialog.Builder(olsrdsettings)
				.setMessage("\nDo you wanna reload the olsrd conf file?\n\n*** Be Carefull ***\n\nThis can't be undone!!!")

				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						reload();
					}
				})

				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})

				.show();			
			}
		});

		final Button saveButton=(Button)findViewById(R.id.button_save);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(olsrdsettings)
				.setMessage("\nDo you wanna save newest olsrd conf file?\n\n*** Be Carefull ***\n\nThis can't be undone!!!")

				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if( save() ){
							Toast.makeText(olsrdsettings, "Saved!", Toast.LENGTH_LONG).show();							
						}
						else{
							Toast.makeText(olsrdsettings, "Save failed!", Toast.LENGTH_LONG).show();	
						}
					}
				})

				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})

				.show();			
			}
		});

	}

	private void reload(){

		final EditText edit=(EditText)findViewById(R.id.olsrd_edit);
		edit.setText(""); //ripulisco la finestra

		try{
			FileInputStream fIn = null; 
			InputStreamReader isr = null;
			fIn = openFileInput("olsrd_conf");       
			isr = new InputStreamReader(fIn); 
			BufferedReader br = new BufferedReader(isr);
			String s;
			while((s = br.readLine()) != null) {
				edit.append(s);
				edit.append("\n");
			}
			isr.close(); 
			fIn.close(); 
		}catch(Exception e){
			Log.e("Errore Lettura File",e.toString());
			edit.setText("No configuration file found!");
		}

	}

	private boolean save(){

		final EditText edit=(EditText)findViewById(R.id.olsrd_edit);		
		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;
		try{
			fOut = openFileOutput("olsrd_conf",MODE_PRIVATE);      
			osw = new OutputStreamWriter(fOut);
			osw.write(edit.getText().toString());
			osw.flush();
		}
		catch (Exception e) {      
			Log.e(TAG,"Errore Scrittura File",e.fillInStackTrace());
			return false;
		}		
		return true;
	}

}
