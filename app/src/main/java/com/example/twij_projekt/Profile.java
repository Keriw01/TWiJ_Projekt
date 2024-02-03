package com.example.twij_projekt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class Profile extends Fragment {

    private TextView emailTextView, changePassword;
    private Button logoutButton;


    public Profile() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);

        emailTextView = view.findViewById(R.id.emailTextView);
        changePassword = view.findViewById(R.id.changePassword);
        logoutButton = view.findViewById(R.id.logoutButton);

        String refreshToken = SharedPreferencesManager.getRefreshTokenFromSharedPreferences(requireContext());
        String email = SharedPreferencesManager.getEmailFromSharedPreferences(requireContext());;

        if (email != null) {
            emailTextView.setText(email);
        } else {
            new ApiRepository.UserRepository().getUser(refreshToken, new ApiRepository.UserRepository.UserDataCallback() {
                @Override
                public void onUserReceived(String userEmail, String userId) {
                    SharedPreferencesManager.saveUserToSharedPreferences(requireContext(), userEmail, userId);
                    emailTextView.setText(userEmail);
                }

                @Override
                public void onUserError(String errorMessage) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    handleLogout();
                }
            });
        }

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(requireContext(), ChangePasswordActivity.class);
            startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Wylogowano !", Toast.LENGTH_SHORT).show();
                handleLogout();
            }
        });

        return view;
    }

    private void handleLogout() {
        Context context = requireContext();

        SharedPreferencesManager.clearTokensFromSharedPreferences(context);
        SharedPreferencesManager.clearUserFromSharedPreferences(context);

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);

        requireActivity().finish();
    }
}
