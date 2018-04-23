package com.kudubisa.app.navdrawertemplate.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.kudubisa.app.navdrawertemplate.R;

import java.util.Calendar;

/**
 * Created by asus on 4/15/18.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener{

    public DatePickerFragment() {
    }

    public static DatePickerFragment newInstance(int rId){
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("rId", rId);
        datePickerFragment.setArguments(args);
        return datePickerFragment;
    }

    private int rId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.rId = getArguments().getInt("rId");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        TextView date = (TextView) getActivity().findViewById(this.rId);
        int month = datePicker.getMonth()+1;
        int day = datePicker.getDayOfMonth();
        int year = datePicker.getYear();
        date.setText(year+"-"+month+"-"+day);
    }

}