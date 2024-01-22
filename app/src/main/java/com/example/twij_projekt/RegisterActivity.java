package com.example.twij_projekt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonRegister;
    private TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextEmail = findViewById(R.id.editTextEmailRegister);
        editTextPassword = findViewById(R.id.editTextPasswordRegister);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                new RegisterActivity.RegisterTask().execute(email, password);
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private class RegisterTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            // Ustaw URL API logowania
            String registerUrl = "http://audiobookhsetvo.mooo.com/api/audio_book.php/registration";

            // Utwórz klienta OkHttp
            OkHttpClient client = new OkHttpClient();

            // Utwórz ciało żądania z danymi logowania
            RequestBody requestBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password", password)
                    .build();

            // Utwórz żądanie HTTP POST
            Request request = new Request.Builder()
                    .url(registerUrl)
                    .post(requestBody)
                    .build();

            try {
                // Wykonaj żądanie i pobierz odpowiedź
                Response response = client.newCall(request).execute();

                // Sprawdź, czy żądanie zakończyło się sukcesem (kod 200)
                if (response.isSuccessful()) {
                    // Pobierz odpowiedź jako string
                    return response.body().string();
                } else {
                    // Jeśli kod odpowiedzi nie jest 200, zwróć null lub inny kod błędu
                    return null;
                }
            } catch (Exception e) {
                // Obsłuż błąd w przypadku niepowodzenia żądania
                Log.e("RegisterActivity", "Error during registerActivity request", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Sprawdź, czy wynik nie jest pusty
            if (result != null) {
                try {
                    // Otrzymany wynik to JSON, przetwórz go
                    JSONObject jsonResult = new JSONObject(result);

                    // Sprawdź status w odpowiedzi
                    if (jsonResult.has("status") && jsonResult.getString("status").equals("success")) {
                        // Pobierz tokeny z JSON
                        String accessToken = jsonResult.getString("accessToken");
                        String refreshToken = jsonResult.getString("refreshToken");

                        // Zapisz tokeny w pamięci podręcznej (SharedPreferences)
                        saveTokensToSharedPreferences(accessToken, refreshToken);
                        // Przykład wyświetlenia wyniku w postaci toasta
                        Toast.makeText(RegisterActivity.this, "Register successful!", Toast.LENGTH_SHORT).show();

                        // Przejdź do MainActivity lub wykonaj inne akcje związane z zalogowaniem
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.putExtra("isLogged", true);
                        startActivity(intent);
                        finish();
                    } else {
                        // W przypadku błędu wyświetl informację o nieudanym logowaniu
                        Toast.makeText(RegisterActivity.this, "Register failed.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // Błąd parsowania JSON
                    Log.e("RegisterActivityActivity", "Error parsing JSON response", e);
                }
            } else {
                // W przypadku błędu wyświetl informację o nieudanym logowaniu
                Toast.makeText(RegisterActivity.this, "RegisterActivity failed.", Toast.LENGTH_SHORT).show();
            }
        }
        // Metoda do zapisywania tokenów w pamięci podręcznej (SharedPreferences)
        private void saveTokensToSharedPreferences(String accessToken, String refreshToken) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("accessToken", accessToken);
            editor.putString("refreshToken", refreshToken);

            // Zapisz zmiany
            editor.apply();
        }
    }
}
