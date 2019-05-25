package com.example.bbbb.healthmanager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddBloodPressureFragment extends Fragment {
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private NumberPicker sysPicker, diaPicker;
    private Button okButton, backButton;
    private String userID, buttonStatus;
    private Activity activity;

    FirebaseDatabase database;
    DatabaseReference mDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userID = bundle.getString("userID");
            buttonStatus = bundle.getString("buttonStatus");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_addbp, container, false);
        sysPicker = layout.findViewById(R.id.sys_picker);
        diaPicker = layout.findViewById(R.id.dia_picker);
        okButton = layout.findViewById(R.id.ok_button);
        backButton = layout.findViewById(R.id.back_button);

        sysPicker.setMinValue(1);
        sysPicker.setMaxValue(200);
        sysPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        sysPicker.setWrapSelectorWheel(false);
        sysPicker.setValue(120);
        sysPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });

        diaPicker.setMinValue(1);
        diaPicker.setMaxValue(200);
        diaPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        diaPicker.setWrapSelectorWheel(false);
        diaPicker.setValue(80);
        diaPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                mDatabase = database.getReference();

                String split[] = getTime().split(" ");

                if(buttonStatus.equals("mbp")) {
                    mDatabase.child("users").child(userID).child("bloodPressure").child(split[0]).child("morning").child("time").setValue(split[1]);
                    mDatabase.child("users").child(userID).child("bloodPressure").child(split[0]).child("morning").child("sys").setValue(String.valueOf(sysPicker.getValue()));
                    mDatabase.child("users").child(userID).child("bloodPressure").child(split[0]).child("morning").child("dia").setValue(String.valueOf(diaPicker.getValue()));
                } else if(buttonStatus.equals("abp")) {
                    mDatabase.child("users").child(userID).child("bloodPressure").child(split[0]).child("afternoon").child("time").setValue(split[1]);
                    mDatabase.child("users").child(userID).child("bloodPressure").child(split[0]).child("afternoon").child("sys").setValue(String.valueOf(sysPicker.getValue()));
                    mDatabase.child("users").child(userID).child("bloodPressure").child(split[0]).child("afternoon").child("dia").setValue(String.valueOf(diaPicker.getValue()));
                } else if(buttonStatus.equals("ebp")){
                    mDatabase.child("users").child(userID).child("bloodPressure").child(split[0]).child("evening").child("time").setValue(split[1]);
                    mDatabase.child("users").child(userID).child("bloodPressure").child(split[0]).child("evening").child("sys").setValue(String.valueOf(sysPicker.getValue()));
                    mDatabase.child("users").child(userID).child("bloodPressure").child(split[0]).child("evening").child("dia").setValue(String.valueOf(diaPicker.getValue()));
                }

                FragmentManager fragmentManager = getActivity().getFragmentManager();
                fragmentManager.beginTransaction().remove(AddBloodPressureFragment.this).commit();
                fragmentManager.popBackStack();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                fragmentManager.beginTransaction().remove(AddBloodPressureFragment.this).commit();
                fragmentManager.popBackStack();
            }
        });

        return layout;
    }

    private String getTime() {
        long mNow;
        Date mDate;

        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);

        return mFormat.format(mDate);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }
}
