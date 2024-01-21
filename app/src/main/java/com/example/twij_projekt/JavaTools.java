package com.example.twij_projekt;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

public class JavaTools extends Fragment {

    private EditText urlEditText;
    private WebView myWebView;
    private Spinner linkSpinner;

    public JavaTools() {
        // wymagane puste publiczne konstruktory
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.java_tools, container, false);

        urlEditText = view.findViewById(R.id.urlEditText);
        Button goButton = view.findViewById(R.id.goButton);
        myWebView = view.findViewById(R.id.myWebView);
        linkSpinner = view.findViewById(R.id.linkSpinner);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());

        // Dodanie predefiniowanych linków do Spinnera
        String[] links = {"https://www.oracle.com/java/technologies/", "https://docs.oracle.com/en/java/",
                "https://docs.oracle.com/javase/tutorial/"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, links);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        linkSpinner.setAdapter(adapter);

        // Obsługa zdarzenia wyboru linku z Spinnera
        linkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedLink = (String) parentView.getItemAtPosition(position);
                myWebView.loadUrl(selectedLink);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Nie trzeba implementować, ale jest wymagane
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customUrl = urlEditText.getText().toString();
                if (!customUrl.isEmpty()) {
                    myWebView.loadUrl(customUrl);
                }
            }
        });

        return view;
    }
}
