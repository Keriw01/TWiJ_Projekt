package com.example.twij_projekt;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView
        .OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Sprawdź, czy użytkownik jest zalogowany
        // (tutaj powinna być logika sprawdzająca stan zalogowania)
        boolean isLogged = getIntent().getBooleanExtra("isLogged", false);
        if (!isLogged) {
            // Jeśli użytkownik nie jest zalogowany, przekieruj do LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Zabezpieczenie przed powrotem do MainActivity z powrotem
            return;
        }else{
            // Jeśli użytkownik jest zalogowany, wyświetl MainActivity

            setContentView(R.layout.activity_main);

            bottomNavigationView
                    = findViewById(R.id.bottomNavigationView);

            bottomNavigationView
                    .setOnItemSelectedListener(this);
            bottomNavigationView.setSelectedItemId(R.id.slide_viewer);
        }
    }

    SlideViewer slideViewer = new SlideViewer();
    CodeViewer codeViewer = new CodeViewer();
    JavaQuiz javaQuiz = new JavaQuiz();
    JavaTools javaTools = new JavaTools();

    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item)
    {

        int itemId = item.getItemId();

        if (itemId == R.id.slide_viewer) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, slideViewer)
                    .commit();
            return true;
        } else if (itemId == R.id.code_viewer) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, codeViewer)
                    .commit();
            return true;
        } else if (itemId == R.id.java_quiz) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, javaQuiz)
                    .commit();
            return true;
        } else if (itemId == R.id.java_tools)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, javaTools)
                    .commit();
            return true;
        }

        return false;
    }
}
