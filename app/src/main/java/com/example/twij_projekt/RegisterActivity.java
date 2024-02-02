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

            String registerUrl = "http://audiobookhsetvo.mooo.com/api/audio_book.php/registration";

            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password", password)
                    .build();

            Request request = new Request.Builder()
                    .url(registerUrl)
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
                Log.e("RegisterActivity", "Error during registerActivity request", e);
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

                        Toast.makeText(RegisterActivity.this, "Zarejestrowano !", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.putExtra("isLogged", true);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Rejestracja nie udana !", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("RegisterActivityActivity", "Error parsing JSON response", e);
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Rejestracja nie udana !", Toast.LENGTH_SHORT).show();
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
