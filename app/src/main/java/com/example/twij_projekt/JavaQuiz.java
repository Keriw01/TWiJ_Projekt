package com.example.twij_projekt;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class JavaQuiz extends Fragment {

    private TextView questionTextView;
    private RadioGroup optionsRadioGroup;
    private Button submitButton;
    private Button restartButton;
    private int currentQuestionIndex = 0;
    private ArrayList<QuizQuestion> quizQuestions;
    private int goodAnswers = 0;
    private int badAnswers = 0;

    public JavaQuiz() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.java_quiz, container, false);

        questionTextView = view.findViewById(R.id.questionTextView);
        optionsRadioGroup = view.findViewById(R.id.optionsRadioGroup);
        submitButton = view.findViewById(R.id.submitButton);
        restartButton = view.findViewById(R.id.restartButton);

        loadQuizQuestionsFromJson();

        displayQuestion();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartQuiz();
            }
        });

        return view;
    }

    private void loadQuizQuestionsFromJson() {
        try {
            InputStream inputStream = getActivity().getAssets().open("quiz_questions.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");

            quizQuestions = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String questionText = jsonObject.getString("question");
                JSONArray optionsArray = jsonObject.getJSONArray("options");
                ArrayList<QuizOption> options = new ArrayList<>();
                for (int j = 0; j < optionsArray.length(); j++) {
                    JSONObject optionObject = optionsArray.getJSONObject(j);
                    String optionText = optionObject.getString("text");
                    boolean isCorrect = optionObject.getBoolean("isCorrect");
                    options.add(new QuizOption(optionText, isCorrect));
                }
                QuizQuestion quizQuestion = new QuizQuestion(questionText, options);
                quizQuestions.add(quizQuestion);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayQuestion() {
        if (currentQuestionIndex < quizQuestions.size()) {
            QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);

            questionTextView.setText(currentQuestion.getQuestionText());

            optionsRadioGroup.removeAllViews();

            for (QuizOption option : currentQuestion.getOptions()) {
                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.primaryColor)));
                checkBox.setText(option.getText());
                optionsRadioGroup.addView(checkBox);

                checkBox.setChecked(option.isChecked());
            }

        } else {
            questionTextView.setText("Quiz zakończony!\nTwój wynik to " + goodAnswers + " poprawnych i " + badAnswers + " niepoprawnych.\n"+getGrade());
            optionsRadioGroup.removeAllViews();
            submitButton.setEnabled(false);
        }
    }

    private void checkAnswer() {
        QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);
        ArrayList<QuizOption> options = currentQuestion.getOptions();

        for (int i = 0; i < options.size(); i++) {
            CheckBox checkBox = (CheckBox) optionsRadioGroup.getChildAt(i);
            options.get(i).setChecked(checkBox.isChecked());
        }

        boolean isAnswerCorrect = true;

        for (QuizOption option : options) {
            if (option.isCorrect() != option.isChecked()) {
                isAnswerCorrect = false;
                break;
            }
        }

        if (isAnswerCorrect) {
            goodAnswers++;
        } else {
            badAnswers++;
        }

        currentQuestionIndex++;
        displayQuestion();
    }

    private String getGrade() {
        String grade = "Ocena: ";
        int totalQuestions = quizQuestions.size();
        double percentage = (double) goodAnswers / totalQuestions * 100;

        if (percentage >= 90) {
            return grade+"5.0";
        } else if (percentage >= 85) {
            return grade+"4.5";
        } else if (percentage >= 75) {
            return grade+"4";
        } else if (percentage >= 65) {
            return grade+"3.5";
        }else if (percentage >= 55) {
            return grade+"3";
        } else {
            return grade+"2.0";
        }
    }

    private void restartQuiz() {
        goodAnswers = 0;
        badAnswers = 0;
        currentQuestionIndex = 0;

        for (QuizQuestion question : quizQuestions) {
            for (QuizOption option : question.getOptions()) {
                option.setChecked(false);
            }
        }

        displayQuestion();
        submitButton.setEnabled(true);
    }
}
