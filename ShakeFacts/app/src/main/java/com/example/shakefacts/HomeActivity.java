package com.example.shakefacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.shakefacts.db.FactListDataBase;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Vibrator vibrator;
    private TextInputLayout factTextInputLayout;
    private TextView factTextView;

    private FactListDataBase factListDatabase;

    private boolean isAccelerometerSensorAvalaible;
    private float currentX, currentY, currentZ;
    private float lastX, lastY, lastZ;
    private boolean notFirst = false;
    private float xDifference, yDifference, zDifference;
    private  final float shakeTreshold = 7f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //Megnézi van e gyorsulásmérő szenzor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerSensorAvalaible = true;
        }
        else {
            factTextView.setText("Accelerometer not found on this device");
            isAccelerometerSensorAvalaible = false;
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        currentX = event.values[0];
        currentY = event.values[1];
        currentZ = event.values[2];

        //Ha a ciklus nem először fut le akkor
        if(notFirst) {
            //kiszámolja a különbségeket
            xDifference = Math.abs(lastX - currentX);
            yDifference = Math.abs(lastY - currentY);
            zDifference = Math.abs(lastZ - currentZ);

            //és ha ez a határétéknék nagyobb, akkor
            if((xDifference > shakeTreshold && yDifference > shakeTreshold) ||
                    (xDifference > shakeTreshold && zDifference > shakeTreshold) ||
                    yDifference > shakeTreshold && zDifference > shakeTreshold)
            {
                runOnUiThread( new Thread(() -> {
                    searching();
                    FetchData fetchData = new FetchData();
                    fetchData.run();
                }));

            }
        }

        lastX = currentX;
        lastY= currentY;
        lastZ = currentZ;
        notFirst = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ablak, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.elozmenyek) {
            Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void searching() {
        vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect
                .DEFAULT_AMPLITUDE));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(isAccelerometerSensorAvalaible) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isAccelerometerSensorAvalaible) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    class FetchData extends Thread {

        String data = "";
        @Override
        public void run() {

            TextView factTextview = findViewById(R.id.factTextView);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                TextInputLayout factText= findViewById(R.id.factTextInputLayout);
                Editable fact = factText.getEditText().getText();
                String a = fact.toString();
                if(a.equals("")) {
                    factTextview.setText("I can't find an interesting fact for you");
                }
                else if(a.equals("") == false) {
                    URL url = new URL("https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext=&formatversion=2&titles=" + a);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        data = data + line;
                    }

                    if(data.contains("\"extract\":\"") == false) {
                        factTextview.setText("Check the word above and try again.");
                    }
                    // A . kaakterek kiszedése

                    String extract = data.substring(data.lastIndexOf("\"extract\":\"") + 11, data.lastIndexOf("\""));
                    StringBuilder sb = new StringBuilder();
                    sb.append(extract.charAt(0));
                    for (int i = 1; i < extract.length(); i++) {
                        if (Character.isDigit(extract.charAt(i - 1)) && extract.charAt(i) == '.') {    //A számokat nem bontja így szét
                            sb.append(extract.charAt(i));
                            i = i + 1;
                        }
                        else if(extract.charAt(i-1) == 92 && extract.charAt(i) == '\"') {
                            sb.deleteCharAt(i-1);
                            sb.append("\"");
                        }
                        else if (Character.isDigit((extract.charAt(i - 1))) == false && extract.charAt(i) == '.') {
                            sb.append(".\n");
                        }
                        sb.append(extract.charAt(i));
                    }

                    String preformatted = sb.toString();
                    StringBuilder sb2 = new StringBuilder();
                    for (int i = 0; i < preformatted.length() - 1; i++) {
                        if (preformatted.charAt(i) == 92 && preformatted.charAt(i + 1) == 'n') {
                            sb.append(" ");
                        }
                        sb2.append(preformatted.charAt(i));
                    }
                    sb.append(extract.charAt(0));
                    String formatted = sb2.toString();
                    String[] contents = formatted.split("\n");

                    List<String> trimmedSentences = new ArrayList<String>();
                    for (int i = 0; i < contents.length; i++) {
                        if (contents[i].charAt(0) == '.' && contents[i].charAt(1) == ' ') {
                            StringBuilder sb3 = new StringBuilder();
                            for (int j = 2; j < contents[i].length(); j++) {
                                sb3.append(contents[i].charAt(j));
                            }
                            String contentElement = sb3.toString();
                            trimmedSentences.add(contentElement);
                        } else if (contents[i].charAt(0) == '.' && contents[i].charAt(1) == 92 && contents[i].charAt(2) == 'n') {
                            StringBuilder sb3 = new StringBuilder();
                            for (int j = 3; j < contents[i].length(); j++) {
                                sb3.append(contents[i].charAt(j));
                            }
                            String contentElement = sb3.toString();
                            trimmedSentences.add(contentElement);
                        }
                    }

                    Random rand = new Random();
                    int random = rand.nextInt(trimmedSentences.size());
                    String newFact = trimmedSentences.get(random);

                    String lastFact = (String) factTextview.getText();
                    while(lastFact.equals(newFact)) {
                        if(trimmedSentences.size() == 1) {
                            break;
                        }
                        random = rand.nextInt(trimmedSentences.size());
                        newFact = trimmedSentences.get(random);
                    }

                    factTextview.setText(newFact);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}