package com.tecnocampus.practica3_g103_777;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

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
        Button nextButton = view.findViewById(R.id.next_button);

        // Decodificar el texto de la pregunta a Unicode
        questionText.setText(HtmlCompat.fromHtml(question.getQuestionText(), HtmlCompat.FROM_HTML_MODE_LEGACY));

        List<String> options = question.getAllOptions();
        optionsGroup.removeAllViews();
        for (String option : options) {
            RadioButton radioButton = new RadioButton(getContext());
            // Decodificar el texto de la opción a Unicode
            radioButton.setText(HtmlCompat.fromHtml(option, HtmlCompat.FROM_HTML_MODE_LEGACY));
            optionsGroup.addView(radioButton);
        }

        // Inicialmente, el botón Next está invisible
        nextButton.setVisibility(View.INVISIBLE);

        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Hacer visible el botón Next cuando una opción es seleccionada
            nextButton.setVisibility(View.VISIBLE);
        });

        nextButton.setOnClickListener(v -> {
            int selectedOptionId = optionsGroup.getCheckedRadioButtonId();
            if (selectedOptionId == -1) {
                // No se ha seleccionado ninguna opción
                Toast.makeText(getContext(), "Please select an option.", Toast.LENGTH_SHORT).show();
            } else {
                // Se ha seleccionado una opción
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
