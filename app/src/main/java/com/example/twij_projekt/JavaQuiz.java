package com.example.twij_projekt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class JavaQuiz extends Fragment {

    private TextView questionTextView;  // Pole tekstowe do wyświetlania pytania
    private RadioGroup optionsRadioGroup;  // Grupa przycisków radio do opcji odpowiedzi
    private Button submitButton;  // Przycisk do przesyłania odpowiedzi
    private Button restartButton;  // Przycisk do rozpoczęcia od nowa
    private int currentQuestionIndex = 0;  // Indeks bieżącego pytania
    private ArrayList<QuizQuestion> quizQuestions;  // Lista pytań w quizie
    private int goodAnswers = 0;  // Ilość dobrych odpowiedzi
    private int badAnswers = 0;  // Ilość złych odpowiedzi

    // Konstruktor klasy JavaQuiz
    public JavaQuiz() {
        // Konieczny pusty konstruktor publiczny
    }

    // Metoda wywoływana przy tworzeniu interfejsu fragmentu
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.java_quiz, container, false);

        // Inicjalizacja widoków z pliku XML
        questionTextView = view.findViewById(R.id.questionTextView);
        optionsRadioGroup = view.findViewById(R.id.optionsRadioGroup);
        submitButton = view.findViewById(R.id.submitButton);
        restartButton = view.findViewById(R.id.restartButton);

        // Wczytanie pytań z pliku JSON
        loadQuizQuestionsFromJson();

        // Wyświetlenie pierwszego pytania
        displayQuestion();

        // Ustawienie obsługi zdarzenia dla przycisku "submit"
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obsługa przesyłania odpowiedzi
                checkAnswer();
            }
        });

        // Ustawienie obsługi zdarzenia dla przycisku "restart"
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Rozpoczęcie quizu od nowa
                restartQuiz();
            }
        });

        return view;
    }

    // Metoda wczytująca pytania z pliku JSON
    private void loadQuizQuestionsFromJson() {
        try {
            // Wczytanie pliku JSON z folderu "assets"
            InputStream inputStream = getActivity().getAssets().open("quiz_questions.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");

            // Parsowanie JSON i uzupełnienie listy quizQuestions
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

    // Metoda wyświetlająca bieżące pytanie
    private void displayQuestion() {
        if (currentQuestionIndex < quizQuestions.size()) {
            QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);

            // Wyświetlenie treści pytania
            questionTextView.setText(currentQuestion.getQuestionText());

            // Wyczyszczenie poprzednich opcji odpowiedzi
            optionsRadioGroup.removeAllViews();

            // Dodanie przycisków radio dla każdej opcji
            for (QuizOption option : currentQuestion.getOptions()) {
                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setText(option.getText());
                optionsRadioGroup.addView(checkBox);

                // Zaznacz opcje, które były wcześniej zaznaczone
                checkBox.setChecked(option.isChecked());
            }

        } else {
            // Quiz zakończony
            questionTextView.setText("Quiz zakończony!\nTwój wynik to " + goodAnswers + " poprawnych i " + badAnswers + " niepoprawnych.\n"+getGrade());
            optionsRadioGroup.removeAllViews();
            submitButton.setEnabled(false);
        }
    }

    // Metoda sprawdzająca poprawność odpowiedzi
    private void checkAnswer() {
        QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);
        ArrayList<QuizOption> options = currentQuestion.getOptions();

        // Sprawdzenie odpowiedzi
        for (int i = 0; i < options.size(); i++) {
            CheckBox checkBox = (CheckBox) optionsRadioGroup.getChildAt(i);
            options.get(i).setChecked(checkBox.isChecked());
        }

        // Sprawdzenie poprawności odpowiedzi
        boolean isAnswerCorrect = true;

        for (QuizOption option : options) {
            if (option.isCorrect() != option.isChecked()) {
                isAnswerCorrect = false;
                break;
            }
        }

        // Aktualizacja wyniku
        if (isAnswerCorrect) {
            goodAnswers++;
        } else {
            badAnswers++;
        }

        // Przejście do kolejnego pytania
        currentQuestionIndex++;
        displayQuestion();
    }

    // Metoda obliczająca ocenę
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

    // Metoda restartująca quiz
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
