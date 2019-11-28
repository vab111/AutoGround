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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.njsz, container, false);
        leixing = view.findViewById(R.id.editText20);
        pinpai = view.findViewById(R.id.editText21);
        xinghao = view.findViewById(R.id.editText22);
        nianfen = view.findViewById(R.id.editText23);
        width = view.findViewById(R.id.editText24);
        back = view.findViewById(R.id.editText25);
        pinpai = view.findViewById(R.id.editText26);
        leftBtn = view.findViewById(R.id.radioButton);
        rightBtn = view.findViewById(R.id.radioButton2);
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
}
