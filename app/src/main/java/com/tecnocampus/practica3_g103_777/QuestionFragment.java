package com.tecnocampus.practica3_g103_777;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

public class QuestionFragment extends Fragment {

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;

    public QuestionFragment(List<Question> questions) {
        this.questions = questions;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        // Mostrar la primera pregunta
        showQuestion(view, questions.get(currentQuestionIndex));

        return view;
    }

    private void showQuestion(View view, Question question) {
        TextView questionText = view.findViewById(R.id.question_text);
        RadioGroup optionsGroup = view.findViewById(R.id.options_group);

        questionText.setText(question.getQuestionText());
        List<String> options = question.getAllOptions();
        optionsGroup.removeAllViews();
        for (String option : options) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(option);
            optionsGroup.addView(radioButton);
        }

        Button nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(v -> {
            int selectedOptionId = optionsGroup.getCheckedRadioButtonId();
            if (selectedOptionId != -1) {
                RadioButton selectedRadioButton = view.findViewById(selectedOptionId);
                String selectedAnswer = selectedRadioButton.getText().toString();
                if (selectedAnswer.equals(question.getCorrectAnswer())) {
                    correctAnswers++;
                }
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.size()) {
                    showQuestion(view, questions.get(currentQuestionIndex));
                } else {
                    ((MainActivity) getActivity()).showResults(correctAnswers);
                }
            }
        });
    }
}