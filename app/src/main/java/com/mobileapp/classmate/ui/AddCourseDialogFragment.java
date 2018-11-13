package com.mobileapp.classmate.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.mobileapp.classmate.R;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

public class AddCourseDialogFragment extends DialogFragment {
    public interface AddCourseDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void OnDialogNegativeClick(DialogFragment dialog);
    }

    AddCourseDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (AddCourseDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() +
            "must implement AddCourseDialogListener");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_addcourse, null))
                .setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Send dialog information to DB
                        // Exit Dialog
                        mListener.onDialogPositiveClick(AddCourseDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Exit dialog
                        mListener.OnDialogNegativeClick(AddCourseDialogFragment.this);
                    }
                })
                .setTitle(R.string.add_course_dialog);


        // Create the AlertDialog object and return it
        return builder.create();
    }
}
