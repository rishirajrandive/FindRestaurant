package com.rishi.find.restaurant.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.rishi.find.restaurant.R;

/**
 * Fragment to filter the results based on best match or distance.
 * Created by rishi on 3/21/16.
 */
public class FilterResultFragment extends DialogFragment {
    public static final String EXTRA_OPTION =
            "com.android.find.restaurant.filter_option";

    private static final String ARG_OPTION = "option";

    private RadioGroup mRadioGroup;

    /**
     * Returns instance
     * @param sortByOption
     * @return
     */
    public static FilterResultFragment newInstance(int sortByOption) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_OPTION, sortByOption);

        FilterResultFragment fragment = new FilterResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int sortByOption = (int) getArguments().getSerializable(ARG_OPTION);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_filter_options, null);

        mRadioGroup = (RadioGroup) v.findViewById(R.id.radio_sort_options);
        ((RadioButton) mRadioGroup.findViewById(getRadioButtonId(sortByOption))).setChecked(true);


        ((Button)v.findViewById(R.id.diaglog_search)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                sendResult(Activity.RESULT_OK, getSortByOption(mRadioGroup.getCheckedRadioButtonId()));
            }
        });

        ((Button)v.findViewById(R.id.diaglog_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                sendResult(Activity.RESULT_CANCELED, sortByOption);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.sort_by)
                .create();
    }

    /**
     * Send result to the required activity
     * @param resultCode
     * @param optionId
     */
    private void sendResult(int resultCode, int optionId) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_OPTION, optionId);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    /**
     * Get option selected
     * @param radioButtonId
     * @return
     */
    private int getSortByOption(int radioButtonId){
        if(radioButtonId == R.id.sort_by_distance){
            return 1;
        }else {
            return 0;
        }
    }

    /**
     * Get the button ID
     * @param optionSelected
     * @return
     */
    private int getRadioButtonId(int optionSelected){
        if(optionSelected == 1){
            return R.id.sort_by_distance;
        }else {
            return R.id.sort_by_relevance;
        }
    }
}
