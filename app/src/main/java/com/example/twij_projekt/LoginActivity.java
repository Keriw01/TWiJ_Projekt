package com.example.twij_projekt;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textRegisterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textRegisterView = findViewById(R.id.textViewRegister);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pobierz dane logowania z pól tekstowych
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Wykonaj zadanie logowania w tle
                new LoginTask().execute(email, password);
            }
        });

        textRegisterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            // Ustaw URL API logowania
            String loginUrl = "http://audiobookhsetvo.mooo.com/api/audio_book.php/login";

            // Utwórz klienta OkHttp
            OkHttpClient client = new OkHttpClient();

            // Utwórz ciało żądania z danymi logowania
            RequestBody requestBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password", password)
                    .build();

            // Utwórz żądanie HTTP POST
            Request request = new Request.Builder()
                    .url(loginUrl)
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
                Log.e("LoginActivity", "Error during login request", e);
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

                        // Przykład wyświetlenia wyniku w postaci toasta
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Przejdź do MainActivity lub wykonaj inne akcje związane z zalogowaniem
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("isLogged", true);
                        startActivity(intent);
                        finish();
                    } else {
                        // W przypadku błędu wyświetl informację o nieudanym logowaniu
                        Toast.makeText(LoginActivity.this, "Login failed. Check your credentials.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // Błąd parsowania JSON
                    Log.e("LoginActivity", "Error parsing JSON response", e);
                }
            } else {
                // W przypadku błędu wyświetl informację o nieudanym logowaniu
                Toast.makeText(LoginActivity.this, "Login failed. Check your credentials.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}