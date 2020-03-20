package com.example.autoground;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class JjhFragment extends Fragment {
    public EditText leftEdit;
    public EditText rightEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.jjhfragment, container, false);

        leftEdit = view.findViewById(R.id.editText48);
        rightEdit = view.findViewById(R.id.editText49);
        return view;
    }
}
