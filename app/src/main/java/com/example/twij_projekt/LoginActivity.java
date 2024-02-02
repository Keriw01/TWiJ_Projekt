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
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

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

            String loginUrl = "http://audiobookhsetvo.mooo.com/api/audio_book.php/login";

            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password", password)
                    .build();

            Request request = new Request.Builder()
                    .url(loginUrl)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return null;
                }
            } catch (Exception e) {
                Log.e("LoginActivity", "Error during login request", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    JSONObject jsonResult = new JSONObject(result);

                    if (jsonResult.has("status") && jsonResult.getString("status").equals("success")) {
                        String accessToken = jsonResult.getString("accessToken");
                        String refreshToken = jsonResult.getString("refreshToken");
                        String email = jsonResult.getString("email");

                        saveUserToSharedPreferences(accessToken, refreshToken, email);

                        Toast.makeText(LoginActivity.this, "Zalogowano !", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("isLogged", true);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Logowanie nie powiodło się !", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("LoginActivity", "Error parsing JSON response", e);
                }
            } else {
                Toast.makeText(LoginActivity.this, "Logowanie nie powiodło się !", Toast.LENGTH_SHORT).show();
            }
        }

        private void saveUserToSharedPreferences(String accessToken, String refreshToken, String email) {
            SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("accessToken", accessToken);
            editor.putString("refreshToken", refreshToken);
            editor.putString("email", email);

            editor.apply();
        }
    }
}