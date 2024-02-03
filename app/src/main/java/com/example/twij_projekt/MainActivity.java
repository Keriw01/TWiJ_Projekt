/*
Aplikacja JAVA PRO zawiera:

Logowanie/Rejestracja:
Aplikacja umożliwia użytkownikom logowanie się lub rejestrację, co pozwala na dostęp do kolejnych funkcji.

Autoryzacja i Tokeny:
Po udanym logowaniu użytkownik otrzymuje accessToken i refreshToken, które są używane do autoryzacji i utrzymania sesji użytkownika, zapewniając bezpieczeństwo dostępu do spersonalizowanych treści.

Slide Viewer:
Wirtualny przegląd slajdów z wykładów z danego przedmiotu. Użytkownik ma możliwość wyboru konkretnego numeru wykładu (np. W01, W02) oraz korzystania z przycisków nawigacyjnych: następny, poprzedni, 'Pierwsze' i 'Ostatnie' w celu łatwiejszego poruszania się między slajdami.

Code Viewer:
Przeglądarka kodów źródłowych, która umożliwia użytkownikom przeglądanie przykładów z języka Java, zgodnych z instrukcjami laboratoryjnymi. Umożliwia wybór konkretnego numeru laboratorium i numeru przykładu, po wciśnięciu przycisku 'Czytaj' zostanie wyświetlony wybrany kod źródłowy.

Java Quiz:
System przeprowadzania testów z przedmiotu "Programowanie w języku Java". Oferuje testy wielokrotnego wyboru z prawdziwymi pytaniami, punktacją i możliwością otrzymywania ocen. Dane do testów są wczytywane z pliku JSON, co pozwala na elastyczne zarządzanie treściami testów.

Java Tools:
W tym module znajduje się przeglądarka internetowa oraz linki do różnych narzędzi i frameworków związanych z językiem Java. Użytkownicy mają dostęp do przydatnych zasobów takich jak Java, Java Tutorials, Java Technologies itp. Ponadto, istnieje możliwość wpisania własnej strony WWW, co umożliwia dostęp do dodatkowych zasobów online.

Profile:
Tutaj znajduje się informacja o bieżącym użytkowniku (adres email) oraz przycisk 'Wyloguj'.

 */

package com.example.twij_projekt;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

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

        String refreshToken = SharedPreferencesManager.getRefreshTokenFromSharedPreferences(this);

        if (refreshToken == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }else{
            new ApiRepository.UserRepository().getUser(refreshToken, new ApiRepository.UserRepository.UserDataCallback() {
                @Override
                public void onUserReceived(String userEmail, String userId) {
                    SharedPreferencesManager.saveUserToSharedPreferences(MainActivity.this, userEmail, userId);
                    setContentView(R.layout.activity_main);

                    bottomNavigationView
                            = findViewById(R.id.bottomNavigationView);

                    bottomNavigationView
                            .setOnItemSelectedListener(MainActivity.this);
                    bottomNavigationView.setSelectedItemId(R.id.slide_viewer);
                }

                @Override
                public void onUserError(String errorMessage) {
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    SharedPreferencesManager.clearTokensFromSharedPreferences(MainActivity.this);
                    SharedPreferencesManager.clearUserFromSharedPreferences(MainActivity.this);

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);

                    finish();
                }
            });
        }
    }

    SlideViewer slideViewer = new SlideViewer();
    CodeViewer codeViewer = new CodeViewer();
    JavaQuiz javaQuiz = new JavaQuiz();
    JavaTools javaTools = new JavaTools();
    Profile profile = new Profile();

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
        else if (itemId == R.id.profile)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, profile)
                    .commit();
            return true;
        }

        return false;
    }
}
