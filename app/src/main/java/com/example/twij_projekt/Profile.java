package com.example.twij_projekt;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Profile extends Fragment {

    private TextView emailTextView;
    private Button logoutButton;


    public Profile() {
        // Wymagane puste publiczne konstruktory
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);

        // Inicjalizacja elementów interfejsu użytkownika
        emailTextView = view.findViewById(R.id.emailTextView);
        logoutButton = view.findViewById(R.id.logoutButton);


        // Pobranie AccessToken z pamięci podręcznej
        String accessToken = getAccessTokenFromSharedPreferences();
        String email = getEmailFromSharedPreferences();

        // Sprawdź, czy AccessToken jest dostępny
        if (email != null) {
            emailTextView.setText(email);
        } else if(accessToken != null){
            // Wykonaj zapytanie do API o dane użytkownika
            new FetchUserDataTask().execute(accessToken);
        }else{
            // AccessToken nie jest dostępny, obsłuż tę sytuację
            Toast.makeText(requireContext(), "Access token nie dostępny. Zaloguj się ponownie.", Toast.LENGTH_SHORT).show();

            handleLogout();
        }

        // Obsługa przycisku wylogowania
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Wylogowano !", Toast.LENGTH_SHORT).show();
                handleLogout();
            }
        });



        return view;
    }

    // Metoda do obsługi wylogowywania
    private void handleLogout() {
        clearTokensFromSharedPreferences();

        // Uruchom LoginActivity
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);

        // Zakończ bieżącą aktywność
        requireActivity().finish();
    }

    private String getAccessTokenFromSharedPreferences() {
        Context context = requireContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null);
    }

    private String getEmailFromSharedPreferences() {
        Context context = requireContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        return sharedPreferences.getString("email", null);
    }


    private void clearTokensFromSharedPreferences() {
        Context context = requireContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("accessToken");
        editor.remove("refreshToken");
        editor.remove("email");

        // Zapisz zmiany
        editor.apply();
    }

    private class FetchUserDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String accessToken = params[0];
            String userDataUrl = "http://audiobookhsetvo.mooo.com/api/audio_book.php/user";

            try {
                // Utwórz URL i połączenie HttpURLConnection
                URL url = new URL(userDataUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // Ustaw nagłówki żądania
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", accessToken);

                // Pobierz dane odpowiedzi
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                // Zamknij połączenie i odczyt
                reader.close();
                urlConnection.disconnect();

                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    // Przetwórz dane użytkownika
                    JSONObject jsonResult = new JSONObject(result);

                    // Sprawdź, czy otrzymano dane użytkownika
                    if (jsonResult.has("email")) {
                        String userEmail = jsonResult.getString("email");
                        emailTextView.setText(userEmail);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Obsłuż błąd pobierania danych użytkownika
                Toast.makeText(requireContext(), "Błąd podczas pobierania danych o użytkowniku", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
