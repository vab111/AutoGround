package com.example.autoground;

import android.icu.util.ValueIterator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class txFragment extends Fragment {
    public EditText toGround;
    public EditText toBack;
    public EditText toMid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.txsz, container, false);
        toGround = view.findViewById(R.id.editText27);
        toBack = view.findViewById(R.id.editText28);
        toMid = view.findViewById(R.id.editText29);
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        toGround = getActivity().findViewById(R.id.editText27);
        toBack = getActivity().findViewById(R.id.editText28);
        toMid = getActivity().findViewById(R.id.editText29);
        registerForContextMenu(toBack);
        registerForContextMenu(toGround);
        registerForContextMenu(toMid);
    }
}
