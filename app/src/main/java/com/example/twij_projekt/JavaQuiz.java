package com.example.twij_projekt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
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
    private int goodAnsewer = 0;  // Ilość dobrych odpowiedzi
    private int badAnswer = 0;  // Ilość złych odpowiedzi

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
                goodAnsewer=0;
                badAnswer=0;
                currentQuestionIndex = 0;
                displayQuestion();
                submitButton.setEnabled(true);
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
                ArrayList<String> options = new ArrayList<>();
                for (int j = 0; j < optionsArray.length(); j++) {
                    options.add(optionsArray.getString(j));
                }
                int correctOptionIndex = jsonObject.getInt("correctOptionIndex");

                QuizQuestion quizQuestion = new QuizQuestion(questionText, options, correctOptionIndex);
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
            for (int i = 0; i < currentQuestion.getOptions().size(); i++) {
                RadioButton radioButton = new RadioButton(getContext());
                radioButton.setText(currentQuestion.getOptions().get(i));
                optionsRadioGroup.addView(radioButton);
            }

        } else {
            // Quiz zakończony
            questionTextView.setText("Quiz zakończony!\nTwój wynik to "+goodAnsewer +" poprawnych i "+badAnswer+" niepoprawnych ;)");
            optionsRadioGroup.removeAllViews();
            submitButton.setEnabled(false);
        }
    }

    // Metoda sprawdzająca poprawność odpowiedzi
    private void checkAnswer() {
        int selectedOptionIndex = optionsRadioGroup.indexOfChild(
                optionsRadioGroup.findViewById(optionsRadioGroup.getCheckedRadioButtonId()));

        if (selectedOptionIndex == quizQuestions.get(currentQuestionIndex).getCorrectOptionIndex()) {
            // Poprawna odpowiedź
            goodAnsewer++;
        } else {
            // Niepoprawna odpowiedź
            badAnswer++;
        }

        // Przejście do kolejnego pytania
        currentQuestionIndex++;
        displayQuestion();
    }
}
