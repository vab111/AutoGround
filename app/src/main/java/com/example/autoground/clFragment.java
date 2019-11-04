package com.example.autoground;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class clFragment extends Fragment {
    private EditText pinpai;
    private EditText xinghao;
    private EditText nianfen;
    private EditText chepai;
    private EditText front;
    private EditText back;
    private EditText zhouju;
    private EditText chegao;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.clsz, container, false);
        pinpai = view.findViewById(R.id.editText12);
        xinghao = view.findViewById(R.id.editText13);
        nianfen = view.findViewById(R.id.editText14);
        chepai = view.findViewById(R.id.editText15);
        front = view.findViewById(R.id.editText16);
        back = view.findViewById(R.id.editText17);
        zhouju = view.findViewById(R.id.editText18);
        chegao = view.findViewById(R.id.editText19);
        return view;
    }
}
