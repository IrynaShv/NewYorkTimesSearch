package com.shvydchenko.newyorktimessearch.Fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.shvydchenko.newyorktimessearch.R;
import com.shvydchenko.newyorktimessearch.interfaces.SearchPresenterInterface;
import com.shvydchenko.newyorktimessearch.models.FilterOptions;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    SearchPresenterInterface mListener;
    private Spinner sortOrder;
    private String selectedSortOrder;
    private String formattedDate;
    private EditText etBeforeDate;
    private ListView lvCategories;
    private ArrayAdapter<String> categoriesAdapter;
    private FilterOptions previousFilterOptions;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SearchPresenterInterface) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    public FilterDialogFragment() {
    }

    public static FilterDialogFragment newInstance(String title, FilterOptions previousFilterOptions) {
        FilterDialogFragment frag = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelable("filter_options", Parcels.wrap(previousFilterOptions));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        formattedDate = null;
        selectedSortOrder = null;
        getDialog().setTitle(getArguments().getString("title"));

        previousFilterOptions = Parcels.unwrap(getArguments().getParcelable("filter_options"));
        setSortOrder(view);
        setEtBeforeDate(view);
        setCategories(view);

        Button btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LinkedHashMap <String, Boolean> categories = mapCategories();
                FilterOptions dataToSend = new FilterOptions(selectedSortOrder.toLowerCase(), formattedDate, categories);
                 mListener.onSubmit(Parcels.wrap(dataToSend));
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        final Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        formattedDate = sdf.format(c.getTime());
        etBeforeDate.setText(new SimpleDateFormat("EEE, d MMM yyyy").format(c.getTime()));
    }

    public void showDatePickerDialog(View v) {
        Bundle b = new Bundle();
        if(previousFilterOptions != null && previousFilterOptions.getFormattedDate()!= null) {
            try {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                c.setTime(sdf.parse(previousFilterOptions.getFormattedDate()));
                b.putInt("set_year", c.get(Calendar.YEAR));
                b.putInt("set_month", c.get(Calendar.MONTH));
                b.putInt("set_day", c.get(Calendar.DAY_OF_MONTH));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        DialogFragment picker = new DatePickerFragment();
        picker.setArguments(b);
        picker.setTargetFragment(this, 300);
        picker.show(getFragmentManager(), "datePicker");
    }

    public LinkedHashMap <String, Boolean> mapCategories() {
        if (categoriesAdapter.getCount() > 0) {
            LinkedHashMap<String, Boolean> categories = new LinkedHashMap<String, Boolean>();
            for (int i = 0; i < categoriesAdapter.getCount(); i++) {
                categories.put(lvCategories.getItemAtPosition(i).toString(), lvCategories.isItemChecked(i));
            }
            return categories;
        }
        return null;
    }

    public void setSortOrder(View view) {
        sortOrder = (Spinner) view.findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.sort_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortOrder.setAdapter(adapter);

        if(previousFilterOptions != null && previousFilterOptions.getSortOrder()!= null) {
            String currentSelection = previousFilterOptions.getSortOrder();
            int spinnerPosition = adapter.getPosition(currentSelection.substring(0, 1).toUpperCase() + currentSelection.substring(1));
            sortOrder.setSelection(spinnerPosition);
        }

        sortOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                selectedSortOrder = adapter.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void setEtBeforeDate(View view) {
        etBeforeDate = (EditText) view.findViewById(R.id.etBeforeDate);
        if(previousFilterOptions != null && previousFilterOptions.getFormattedDate()!= null) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            try {
                c.setTime(sdf.parse(previousFilterOptions.getFormattedDate()));
                formattedDate = previousFilterOptions.getFormattedDate();
                etBeforeDate.setText(new SimpleDateFormat("EEE, d MMM yyyy").format(c.getTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        etBeforeDate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

    }

    public void setCategories( View view) {
        String[] categories = getResources().getStringArray(R.array.categories_array);
        if(previousFilterOptions != null && previousFilterOptions.getCategories()!= null) {
            categories = previousFilterOptions.getCategories().keySet().toArray(
                    new String[previousFilterOptions.getCategories().keySet().size()]);
        }
        categoriesAdapter =  new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_multiple_choice, categories);
        lvCategories = (ListView) view.findViewById(R.id.categories_list);
        lvCategories.setAdapter(categoriesAdapter);

        for (int i = 0; i < categoriesAdapter.getCount(); i++) {
            if(previousFilterOptions != null && previousFilterOptions.getCategories()!= null) {
                Boolean isChecked = previousFilterOptions.getCategories().get(
                        lvCategories.getItemAtPosition(i).toString());
                lvCategories.setItemChecked(i, isChecked);
            } else {
                lvCategories.setItemChecked(i, false);
            }
        }
    }
}