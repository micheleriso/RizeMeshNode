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

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.text.method.DigitsKeyListener;
import android.widget.Toast;

/**
* EditTextPreference that allows IP addresses only
*/
public class IPPreference extends EditTextPreference {
    public IPPreference(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }
    public IPPreference(Context context, AttributeSet attrs) { super(context, attrs); }
    public IPPreference(Context context) { super(context); }

    @Override
    protected void onAddEditTextToDialogView(View dialogView, EditText editText) {
        editText.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        super.onAddEditTextToDialogView(dialogView, editText);
    }

    public static boolean validate(String addr) {
        try {
            //if(addr.length() == 0) // don't want empty address
            //	return false;
            if(java.net.InetAddress.getByName(addr) == null)
                return false;
        } catch (java.net.UnknownHostException e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            // verify now that it's an IP
            String addr = getEditText().getText().toString();
            if (!validate(addr)) {
                Toast.makeText(getContext(),
                              getContext().getString(R.string.invalidip),
                              Toast.LENGTH_SHORT).show();
                positiveResult = false;
            }
        }
        super.onDialogClosed(positiveResult);
    }
}
