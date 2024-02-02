package com.example.twij_projekt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class SlideViewer extends Fragment {
    ImageView iv_display;
    Button btn_right, btn_left, btn_first_image, btn_last_image;
    Spinner spinnerLectureNumber;

    Map<String, int[]> lectureImagesMap = new HashMap<>();

    Map<String, Integer> currentImageIndexMap = new HashMap<>();

    public SlideViewer() {

        lectureImagesMap.put("W01", new int[]{R.drawable.w01_1, R.drawable.w01_2, R.drawable.w01_3, R.drawable.w01_4, R.drawable.w01_5, R.drawable.w01_6, R.drawable.w01_7, R.drawable.w01_8, R.drawable.w01_9});
        lectureImagesMap.put("W02", new int[]{R.drawable.w02_1, R.drawable.w02_2, R.drawable.w02_3, R.drawable.w02_4, R.drawable.w02_5});
        lectureImagesMap.put("W03", new int[]{R.drawable.w03_1, R.drawable.w03_2, R.drawable.w03_3, R.drawable.w03_4, R.drawable.w03_5, R.drawable.w03_6, R.drawable.w03_7, R.drawable.w03_8});

        for (String lecture : lectureImagesMap.keySet()) {
            currentImageIndexMap.put(lecture, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_viewer, container, false);

        iv_display = view.findViewById(R.id.iv_display);
        btn_right = view.findViewById(R.id.btn_right);
        btn_left = view.findViewById(R.id.btn_left);
        btn_first_image = view.findViewById(R.id.btn_first_image);
        btn_last_image = view.findViewById(R.id.btn_last_image);
        spinnerLectureNumber = view.findViewById(R.id.spinner_lecture_number);

        btn_right.setOnClickListener(v -> {
            String selectedLecture = (String) spinnerLectureNumber.getSelectedItem();
            int[] images = lectureImagesMap.get(selectedLecture);

            currentImageIndexMap.put(selectedLecture, (currentImageIndexMap.get(selectedLecture) + 1) % images.length);
            iv_display.setImageResource(images[currentImageIndexMap.get(selectedLecture)]);
        });

        btn_left.setOnClickListener(v -> {
            String selectedLecture = (String) spinnerLectureNumber.getSelectedItem();
            int[] images = lectureImagesMap.get(selectedLecture);

            currentImageIndexMap.put(selectedLecture, (currentImageIndexMap.get(selectedLecture) - 1 + images.length) % images.length);
            iv_display.setImageResource(images[currentImageIndexMap.get(selectedLecture)]);
        });

        btn_first_image.setOnClickListener(v -> {
            String selectedLecture = (String) spinnerLectureNumber.getSelectedItem();
            int[] images = lectureImagesMap.get(selectedLecture);

            currentImageIndexMap.put(selectedLecture, 0);
            iv_display.setImageResource(images[currentImageIndexMap.get(selectedLecture)]);
        });

        btn_last_image.setOnClickListener(v -> {
            String selectedLecture = (String) spinnerLectureNumber.getSelectedItem();
            int[] images = lectureImagesMap.get(selectedLecture);

            currentImageIndexMap.put(selectedLecture, images.length - 1);
            iv_display.setImageResource(images[currentImageIndexMap.get(selectedLecture)]);
        });

        String[] lectureNumbers = {"W01", "W02", "W03"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, lectureNumbers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLectureNumber.setAdapter(adapter);

        spinnerLectureNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedLecture = (String) parentView.getItemAtPosition(position);

                if (lectureImagesMap.containsKey(selectedLecture)) {
                    int[] images = lectureImagesMap.get(selectedLecture);
                    currentImageIndexMap.put(selectedLecture, 0);
                    iv_display.setImageResource(images[currentImageIndexMap.get(selectedLecture)]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        return view;
    }
}
