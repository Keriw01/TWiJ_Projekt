package com.example.twij_projekt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;

public class CodeViewer extends Fragment {
    private Button btn;
    private Spinner labSpinner;
    private ArrayAdapter<CharSequence> labAdapter;
    private Spinner codeSpinner;
    private ArrayAdapter<CharSequence> codeAdapter;
    private WebView webView;

    public CodeViewer() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.code_viewer, container, false);

        // Inicjalizacja elementów interfejsu użytkownika
        btn = view.findViewById(R.id.btnReadTxtFile);
        labSpinner = view.findViewById(R.id.labSpinner);
        codeSpinner = view.findViewById(R.id.codeSpinner);
        webView = view.findViewById(R.id.txtFile);  // Dodaj inicjalizację WebView

        // Inicjalizacja adaptera dla Spinnera z numerem laboratorium
        labAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.lab_numbers, android.R.layout.simple_spinner_item);
        labAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        labSpinner.setAdapter(labAdapter);

        // Obsługa zmiany wybranego laboratorium
        labSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Wywołanie funkcji aktualizującej listę plików dla codeSpinner
                updateCodeSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Implementacja wymagana przez interfejs
            }
        });

        // Inicjalizacja adaptera dla Spinnera z numerem pliku
        codeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        codeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        codeSpinner.setAdapter(codeAdapter);

        // Obsługa przycisku "Czytaj"
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pobranie numeru laboratorium i pliku oraz zbudowanie nazwy pliku
                String labNumber = labSpinner.getSelectedItem().toString();
                String codeNumber = codeSpinner.getSelectedItem().toString();
                String fileName = "L" + labNumber + "_" + codeNumber + ".txt";

                try {
                    // Odczytanie pliku z zasobów
                    InputStream inputStream = requireContext().getAssets().open(fileName);
                    int size = inputStream.available();
                    byte[] buffer = new byte[size];
                    inputStream.read(buffer);
                    inputStream.close();

                    // Konwersja zawartości pliku do stringa
                    String text = new String(buffer);

                    // Zastosowanie kodu Prism do kolorowania składni
                    String formattedCode = applyPrism(text);

                    // Konfiguracja ustawień dla WebView
                    WebSettings webSettings = webView.getSettings();
                    webSettings.setSupportZoom(true);
                    webSettings.setBuiltInZoomControls(true);
                    webSettings.setTextZoom(70);
                    webSettings.setDefaultFontSize(12);
                    webView.getSettings().setJavaScriptEnabled(true);

                    // Wyświetlenie sformatowanego kodu w WebView
                    webView.loadDataWithBaseURL(null, formattedCode, "text/html", "UTF-8", null);

                    // Wyświetlenie komunikatu o sukcesie
                    Toast.makeText(getContext(), "Udało się odczytać plik", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    // Obsługa błędu odczytu pliku
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Błąd odczytu pliku", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    // Funkcja do zastosowania Prism do kolorowania składni
    private String applyPrism(String code) {
        return "<html>" +
                "<head>" +
                "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/prismjs@1.29.0/themes/prism.min.css\" />" +
                "<script src=\"https://cdn.jsdelivr.net/npm/prismjs@1.29.0/prism.min.js\"></script>" +
                "<script src=\"https://cdn.jsdelivr.net/npm/prismjs@1.29.0/components/prism-javascript.min.js\"></script>" +
                "</head>" +
                "<body>" +
                "<pre><code class=\"language-javascript\">" + code + "</code></pre>" +
                "<script>Prism.highlightAll();</script>" +
                "</body>" +
                "</html>";
    }

    // Funkcja do aktualizacji listy plików w codeSpinner w zależności od wybranego laboratorium
    private void updateCodeSpinner() {
        // Pobranie numeru wybranego laboratorium
        String selectedLabNumber = labSpinner.getSelectedItem().toString();

        // Pobranie zasobów dla danego laboratorium
        int labResource = getResources().getIdentifier("code_numbers_" + selectedLabNumber, "array", requireContext().getPackageName());

        if (labResource != 0) {
            // Znaleziono zasoby dla danego laboratorium
            String[] codeNumbers = getResources().getStringArray(labResource);

            // Aktualizacja adaptera dla codeSpinner
            codeAdapter.clear();
            codeAdapter.addAll(codeNumbers);
            codeAdapter.notifyDataSetChanged();
        } else {
            // Brak zasobów dla danego laboratorium
            Toast.makeText(getContext(), "Brak plików dla wybranego laboratorium", Toast.LENGTH_SHORT).show();
        }
    }

}
