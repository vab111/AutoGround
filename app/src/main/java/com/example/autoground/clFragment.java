package com.example.autoground;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class clFragment extends Fragment {
    public EditText pinpai;
    public EditText xinghao;
    public EditText nianfen;
    public EditText chepai;
    public EditText front;
    public EditText back;
    public EditText zhouju;
    public EditText chegao;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.clsz, container, false);

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        pinpai = getActivity().findViewById(R.id.editText12);
        xinghao = getActivity().findViewById(R.id.editText13);
        nianfen = getActivity().findViewById(R.id.editText14);
        chepai = getActivity().findViewById(R.id.editText15);
        front = getActivity().findViewById(R.id.editText16);
        back = getActivity().findViewById(R.id.editText17);
        zhouju = getActivity().findViewById(R.id.editText18);
        chegao = getActivity().findViewById(R.id.editText19);
        registerForContextMenu(pinpai);
        registerForContextMenu(xinghao);
        registerForContextMenu(nianfen);
        registerForContextMenu(chegao);
        registerForContextMenu(chepai);
        registerForContextMenu(front);
        registerForContextMenu(back);
        registerForContextMenu(zhouju);
    }
    }
