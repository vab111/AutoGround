package com.example.autoground;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;

public class njFragment extends Fragment {
    public EditText leixing;
    public EditText pinpai;
    public EditText xinghao;
    public EditText nianfen;
    public EditText width;
    public EditText back;
    public EditText pianyi;
    public RadioButton leftBtn;
    public RadioButton rightBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.njsz, container, false);

        return view;
    }
    public void leftSelected()
    {
        leftBtn.setChecked(true);
        rightBtn.setChecked(false);
    }
    public void rightSelected()
    {
        leftBtn.setChecked(false);
        rightBtn.setChecked(true);

    }
    @Override
    public void onStart()
    {
        super.onStart();
        leixing = getActivity().findViewById(R.id.editText20);
        pinpai = getActivity().findViewById(R.id.editText21);
        xinghao = getActivity().findViewById(R.id.editText22);
        nianfen = getActivity().findViewById(R.id.editText23);
        width = getActivity().findViewById(R.id.editText24);
        back = getActivity().findViewById(R.id.editText25);
        pianyi = getActivity().findViewById(R.id.editText26);
        leftBtn = getActivity().findViewById(R.id.radioButton);
        rightBtn = getActivity().findViewById(R.id.radioButton2);
        registerForContextMenu(leixing);
        registerForContextMenu(pinpai);
        registerForContextMenu(xinghao);
        registerForContextMenu(nianfen);
        registerForContextMenu(width);
        registerForContextMenu(back);
        registerForContextMenu(pianyi);
        registerForContextMenu(leftBtn);
        registerForContextMenu(rightBtn);
    }
}
