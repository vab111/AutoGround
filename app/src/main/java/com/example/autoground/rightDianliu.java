package com.example.autoground;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class rightDianliu extends Fragment {
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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.youdianliu, container, false);

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        maxlimit = getActivity().findViewById(R.id.editText61);
        maxwucha = getActivity().findViewById(R.id.editText62);
        maxoutput = getActivity().findViewById(R.id.editText63);
        deadarea = getActivity().findViewById(R.id.editText64);
        percentP = getActivity().findViewById(R.id.editText65);
        markI = getActivity().findViewById(R.id.editText66);
        weifenD = getActivity().findViewById(R.id.editText67);
        actDianliu = getActivity().findViewById(R.id.editText68);
        dianliuWucha = getActivity().findViewById(R.id.editText69);
        amiDianliu = getActivity().findViewById(R.id.editText70);
        sendBtn = getActivity().findViewById(R.id.button58);
        receiveBtn = getActivity().findViewById(R.id.button59);
        testBtn = getActivity().findViewById(R.id.button61);
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
