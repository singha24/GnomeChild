package com.example.assa.gnomechild;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TreeMap;

import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private static TextToSpeech t1;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String text;

    private static TextToSpeech engine;
    private static BufferedReader reader;
    private static HashMap<String, Answer> answers = new HashMap<String, Answer>();
    private static int count;

    private ImageView picture;
    private static Random rand = new Random();

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Button alarm;

    final static int RQS_1 = 1;


    private static String[] phrases = new String[]{"It's been a long day, without you my friend, now i'm sad.",
    "Oh poppy cock!", "I like bit butts and I cannot lie", "do do doo, do do do dooo, do do do, do du do do do it dooo", "u wot mate", "shut ya gabber"};

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        engine = new TextToSpeech(this, this);

        count = 0;

        picture = (ImageView) findViewById(R.id.imageView3);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarm = (Button) findViewById(R.id.button);
        alarm.setVisibility(View.INVISIBLE);
        /*alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });*/

    }

    public void showTimePicker(){
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);



        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        setAlarm(c);

                    }
                }, hour, minute, false);
        tpd.setTitle("Set Alarm Time");
        tpd.show();
    }

    private void setAlarm(Calendar targetCal) {

        Toast.makeText(getApplicationContext(),
                "Sweet dreams ;)",
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
                pendingIntent);

    }


    public static String getRandomThing() {
        int i = rand.nextInt(phrases.length - 1);
        return phrases[i];
    }

    public void heatMap(){
        Intent intent = new Intent(MainActivity.this, WebActivity.class);
        startActivity(intent);
    }


    public void fabClicked(View v) {
        promptSpeechInput();
    }

    public void setSpeechText(String text) {
        this.text = text;
    }

    public String getTextToSpeak() {
        return text;
    }

    //Speech Input
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void netflixAndChill() {
        AlertDialog.Builder alertadd = new AlertDialog.Builder(
                this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.and_chill, null);
        alertadd.setView(view );
        alertadd.setCancelable(false);
        alertadd.setNeutralButton("Accept?", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
                playWeed();
            }
        });

        alertadd.show();
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    setSpeechText(result.get(0));
                    Toast.makeText(getApplicationContext(),
                            getTextToSpeak(),
                            Toast.LENGTH_SHORT).show();
                    //Filthy IF statements
                    if (getTextToSpeak().toLowerCase().contains("netflix")) {
                        netflixAndChill();
                    }

                    if(getTextToSpeak().toLowerCase().contains("heat")){
                        heatMap();
                    }

                    if (getTextToSpeak().toLowerCase().contains("swag")) {
                        playMp3();
                    } else {
                        talk(answer(getTextToSpeak()));
                    }
                    break;
                }
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            picture.setImageBitmap(imageBitmap);
        }
    }

    public String toString(int i) {
        String s = String.valueOf(i);
        return s;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            Toast.makeText(getApplicationContext(),
                    "I spek da engrish!",
                    Toast.LENGTH_SHORT).show();
            engine.setLanguage(Locale.UK);
        }
    }

    private void talk(String text) {
        engine.setPitch((float) 5.0);
        //engine.setSpeechRate();

        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }

        //engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void init() {
        try {
            InputStreamReader is = new InputStreamReader(getAssets()
                    .open("gnomechild.csv"));

            BufferedReader br;
            br = new BufferedReader(new BufferedReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                answers.put(split[0].toLowerCase(), new Answer(split));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String answer(String q) {
        String[] words = q.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        TreeMap<Double, String> possibleAnswers = new TreeMap<Double, String>();

        for (String word : words) {
            try {
                Answer answer = answers.get(word);
                possibleAnswers.put(answer.getValue(), answer.getText());
            } catch (NullPointerException e) {
            }
        }

        try {
            return possibleAnswers.firstEntry().getValue();
        } catch (NullPointerException e) {
            count++;
            if (count == 3) {
                return "A ring a ding a ding a dong a ding dong ding dong ding ding. mate!";
            } else if (count > 3) {
                String s = getRandomThing();
                return s;
            }
            String s = getRandomThing();
            return s;
        }
    }

    public void playWeed() {
        for (int i = 0; i < 3; i++) {
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.weed);
            mp.start();
        }

    }

    public String playMp3() {
        try {
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.mlg);
            mp.start();
        } catch (Exception e) {

        }
        return "public. void. bribeJudges(){. errrrrrr. Oops."; //un reachable
    }

    public void camera(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

}
