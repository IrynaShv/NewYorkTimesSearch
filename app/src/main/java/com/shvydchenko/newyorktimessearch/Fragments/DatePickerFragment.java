package com.shvydchenko.newyorktimessearch.Fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle b = getArguments();
        int year, month, day;
        Integer setYear = (Integer) b.getInt("set_year");
        if (setYear != 0) {
            final Calendar c = Calendar.getInstance();
            year = setYear;
            month = b.getInt("set_month");
            day = b.getInt("set_day");
        } else {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getTargetFragment();

        return new DatePickerDialog(getActivity(), listener , year, month, day);
    }
}
