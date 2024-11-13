package com.example.newtaskmanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaskListActivity extends AppCompatActivity {

    private TextView tvTaskList, tvLocation;
    private EditText etTask, etDeadline, etNotes;
    private Button btnAddTask, btnBack, btnSpeakTask, btnSpeakNotes;
    private ListView lvTaskList;
    private TaskAdapter taskAdapter;
    private LocationManager locationManager;
    private SpeechRecognizer speechRecognizer;
    private int currentRequestCode;

    private static final int REQUEST_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        tvTaskList = findViewById(R.id.tvTaskList);
        tvLocation = findViewById(R.id.tvLocation);
        tvLocation.setVisibility(View.GONE);
        etTask = findViewById(R.id.etTask);
        etDeadline = findViewById(R.id.etDeadline);
        etNotes = findViewById(R.id.etNotes);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnBack = findViewById(R.id.btnBack);
        btnSpeakTask = findViewById(R.id.btnSpeakTask);
        btnSpeakNotes = findViewById(R.id.btnSpeakNotes);
        lvTaskList = findViewById(R.id.lvTaskList);

        taskAdapter = new TaskAdapter(this);
        lvTaskList.setAdapter(taskAdapter);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        checkPermissions();

        btnSpeakTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechRecognition(1);
            }
        });

        btnSpeakNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechRecognition(2);
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = etTask.getText().toString();
                String deadline = etDeadline.getText().toString();
                String notes = etNotes.getText().toString();

                if (task.isEmpty() || deadline.isEmpty() || notes.isEmpty()) {
                    Toast.makeText(TaskListActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    Task newTask = new Task(task, deadline, notes);
                    taskAdapter.addTask(newTask);
                    etTask.setText("");
                    etDeadline.setText("");
                    etNotes.setText("");
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        speechRecognizer.setRecognitionListener(new RecognitionListenerImpl());
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO
            }, REQUEST_PERMISSIONS);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                double latitude = location.getLatitude ();
                double longitude = location.getLongitude();

                Geocoder geocoder = new Geocoder(TaskListActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    String locationText = String.format("Latitude: %.3f, Longitude: %.3f", latitude, longitude);
                    if (addresses != null && !addresses.isEmpty()) {
                        String placeName = addresses.get(0).getFeatureName();
                        locationText += " (" + placeName + ")";
                    } else {
                        locationText += " (Unknown)";
                    }
                    tvLocation.setText(locationText);
                } catch (IOException e) {
                    e.printStackTrace();
                    tvLocation.setText(String.format("Latitude: %.3f, Longitude: %.3f (Unknown)", latitude, longitude));
                }
                tvLocation.setVisibility(View.VISIBLE);
            } else {
                tvLocation.setVisibility(View.GONE);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {
            tvLocation.setVisibility(View.VISIBLE);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(TaskListActivity.this, "Please enable GPS", Toast.LENGTH_SHORT).show();
            tvLocation.setVisibility(View.GONE);
        }
    };

    private void startSpeechRecognition(int requestCode) {
        currentRequestCode = requestCode;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        speechRecognizer.startListening(intent);
    }

    private class RecognitionListenerImpl implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {}

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {}

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                if (spokenText != null) {
                    if (currentRequestCode == 1) {
                        etTask.setText(spokenText);
                    } else if (currentRequestCode == 2) {
                        etNotes.setText(spokenText);
                    }
                }
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}