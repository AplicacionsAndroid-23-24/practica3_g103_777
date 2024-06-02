package com.tecnocampus.practica3_g103_777;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ResultFragment extends Fragment {
    private int correctAnswers;

    public ResultFragment(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        TextView resultText = view.findViewById(R.id.result_text);
        resultText.setText("Correct Answers: " + correctAnswers);

        return view;
    }
}
