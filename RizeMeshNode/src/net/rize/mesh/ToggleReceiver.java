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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ToggleReceiver extends BroadcastReceiver {
    final static String TAG = "Barnacle.ToggleReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive " + intent.getAction());
        if (BarnacleApp.ACTION_TOGGLE.equals(intent.getAction())) {
            // potential race conditions, but they are benign
            BarnacleService service = BarnacleService.singleton;
            //Log.d(TAG, "service " + ((service == null) ? "null" : "present"));
            if (service != null) {
                if (!intent.getBooleanExtra("start", false)) {
                    Log.d(TAG, "stop");
                    service.stopRequest();
                }
            } else {
                if (intent.getBooleanExtra("start", true)) {
                    Log.d(TAG, "start");
                    context.startService(new Intent(context, BarnacleService.class));
                }
            }
        } else if (BarnacleApp.ACTION_CHECK.equals(intent.getAction())) {
            // FIXME: this is the most inefficient way of finding out the state
            BarnacleService service = BarnacleService.singleton;
            int state = (service != null) ? service.getState() : BarnacleService.STATE_STOPPED;
            BarnacleApp.broadcastState(context, state);
        }
    }
}
