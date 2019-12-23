package com.example.autoground;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class julihuan extends Fragment {
    public EditText maxlimit;
    public EditText maxwucha;
    public EditText maxoutput;
    public EditText deadarea;
    public EditText percentP;
    public EditText markI;
    public EditText weifenD;
    public EditText actDianliu;
    public EditText dianliuWucha;
    public Button sendBtn;
    public Button receiveBtn;
    public Button testBtn;
    public Button recordABtn;
    public Button recordBBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.julihuan, container, false);

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        maxlimit = getActivity().findViewById(R.id.editText101);
        maxwucha = getActivity().findViewById(R.id.editText102);
        maxoutput = getActivity().findViewById(R.id.editText103);
        deadarea = getActivity().findViewById(R.id.editText104);
        percentP = getActivity().findViewById(R.id.editText105);
        markI = getActivity().findViewById(R.id.editText106);
        weifenD = getActivity().findViewById(R.id.editText107);
        actDianliu = getActivity().findViewById(R.id.editText108);
        dianliuWucha = getActivity().findViewById(R.id.editText109);
        sendBtn = getActivity().findViewById(R.id.button58);
        receiveBtn = getActivity().findViewById(R.id.button59);
        testBtn = getActivity().findViewById(R.id.button60);
        recordABtn = getActivity().findViewById(R.id.button63);
        recordBBtn = getActivity().findViewById(R.id.button65);
        registerForContextMenu(maxlimit);
        registerForContextMenu(maxwucha);
        registerForContextMenu(maxoutput);
        registerForContextMenu(deadarea);
        registerForContextMenu(percentP);
        registerForContextMenu(markI);
        registerForContextMenu(weifenD);
        registerForContextMenu(actDianliu);
        registerForContextMenu(dianliuWucha);
        registerForContextMenu(sendBtn);
        registerForContextMenu(receiveBtn);
        registerForContextMenu(testBtn);
        registerForContextMenu(recordABtn);
        registerForContextMenu(recordBBtn);
    }
}
