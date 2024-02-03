package com.example.twij_projekt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button backButton, changePasswordButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        backButton = findViewById(R.id.backButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPassword = currentPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Wprowadź wszystkie pola", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "Nowe hasło i potwierdzenie hasła nie są zgodne", Toast.LENGTH_SHORT).show();
                    return;
                }

                String refreshToken = SharedPreferencesManager.getRefreshTokenFromSharedPreferences(ChangePasswordActivity.this);
                String userId = SharedPreferencesManager.getUserIdFromSharedPreferences(ChangePasswordActivity.this);

                new ApiRepository.AuthRepository().refreshTokens(refreshToken, userId, new ApiRepository.AuthRepository.AuthCallback() {
                    @Override
                    public void onTokensReceived(String accessToken, String refreshToken) {

                        SharedPreferencesManager.saveTokensToSharedPreferences(ChangePasswordActivity.this, accessToken, refreshToken);

                        System.out.println(accessToken);
                        new ApiRepository.UserUpdateRepository().changePassword(currentPassword, newPassword, userId, new ApiRepository.UserUpdateRepository.UserUpdateCallback() {
                            @Override
                            public void onUserReceived(String message) {
                                Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onUserError(String errorMessage) {
                                Toast.makeText(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onTokensError(String errorMessage) {
                        Toast.makeText(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        handleLogout();
                    }
                });
            }
        });
    }

    private void handleLogout() {

        SharedPreferencesManager.clearTokensFromSharedPreferences(ChangePasswordActivity.this);
        SharedPreferencesManager.clearUserFromSharedPreferences(ChangePasswordActivity.this);

        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
        startActivity(intent);

        finish();
    }
}
