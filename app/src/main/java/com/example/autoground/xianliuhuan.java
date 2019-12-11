package com.example.autoground;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class xianliuhuan extends Fragment {
    public EditText maxlimit;
    public EditText maxwucha;
    public EditText maxoutput;
    public EditText deadarea;
    public EditText percentP;
    public EditText markI;
    public EditText weifenD;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.xianliuhuan, container, false);

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        maxlimit = getActivity().findViewById(R.id.editText110);
        maxwucha = getActivity().findViewById(R.id.editText111);
        maxoutput = getActivity().findViewById(R.id.editText112);
        deadarea = getActivity().findViewById(R.id.editText113);
        percentP = getActivity().findViewById(R.id.editText114);
        markI = getActivity().findViewById(R.id.editText115);
        weifenD = getActivity().findViewById(R.id.editText116);
        registerForContextMenu(maxlimit);
        registerForContextMenu(maxwucha);
        registerForContextMenu(maxoutput);
        registerForContextMenu(deadarea);
        registerForContextMenu(percentP);
        registerForContextMenu(markI);
        registerForContextMenu(weifenD);

    }
}
