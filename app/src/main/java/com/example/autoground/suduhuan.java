package com.example.autoground;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class suduhuan extends Fragment {
    public EditText maxlimit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.suduhuan, container, false);

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        maxlimit = getActivity().findViewById(R.id.editText117);

        registerForContextMenu(maxlimit);
    }
}
