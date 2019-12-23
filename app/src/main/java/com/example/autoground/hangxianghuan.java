package com.example.autoground;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class hangxianghuan extends Fragment {
    public EditText maxlimit;
    public EditText maxwucha;
    public EditText maxoutput;
    public EditText deadarea;
    public EditText percentP;
    public EditText markI;
    public EditText weifenD;
    public EditText actDianliu;
    public EditText dianliuWucha;
    public EditText amiDianliu;
    public Button sendBtn;
    public Button receiveBtn;
    public Button testBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.hangxianghuan, container, false);

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        maxlimit = getActivity().findViewById(R.id.editText91);
        maxwucha = getActivity().findViewById(R.id.editText92);
        maxoutput = getActivity().findViewById(R.id.editText93);
        deadarea = getActivity().findViewById(R.id.editText94);
        percentP = getActivity().findViewById(R.id.editText95);
        markI = getActivity().findViewById(R.id.editText96);
        weifenD = getActivity().findViewById(R.id.editText97);
        actDianliu = getActivity().findViewById(R.id.editText98);
        dianliuWucha = getActivity().findViewById(R.id.editText99);
        amiDianliu = getActivity().findViewById(R.id.editText100);
        sendBtn = getActivity().findViewById(R.id.button58);
        receiveBtn = getActivity().findViewById(R.id.button59);
        testBtn = getActivity().findViewById(R.id.button64);
        registerForContextMenu(maxlimit);
        registerForContextMenu(maxwucha);
        registerForContextMenu(maxoutput);
        registerForContextMenu(deadarea);
        registerForContextMenu(percentP);
        registerForContextMenu(markI);
        registerForContextMenu(weifenD);
        registerForContextMenu(actDianliu);
        registerForContextMenu(dianliuWucha);
        registerForContextMenu(amiDianliu);
        registerForContextMenu(sendBtn);
        registerForContextMenu(receiveBtn);
        registerForContextMenu(testBtn);
    }
}
