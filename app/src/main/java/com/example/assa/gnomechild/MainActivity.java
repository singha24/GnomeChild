package com.example.assa.gnomechild;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;


public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private static TextToSpeech t1;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String text;

    private static TextToSpeech engine;
    private static BufferedReader reader;
    private static HashMap<String, Answer> answers = new HashMap<String, Answer>();

    protected static final String HOST = "assachana.uk"; // eJabbered server ip
    protected static final int PORT = 5222; // server port
    //protected static final String SERVICE = "2.223.149.8"; // not sure this is needed
    protected static String rUsername = "test"; // recipient for messages, IP is not needed, just the username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        engine = new TextToSpeech(this, this);


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
                    if (getTextToSpeak().equals("swag")) {

                        playMp3();
                    } else {
                        talk(answer(getTextToSpeak()));
                    }
                    break;
                }
            }
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
    public void onStart(){
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
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
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
            return "I have no clue mate";
        }
    }

    public String playMp3() {
        try {
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.mlg);
            mp.start();
        } catch (Exception e) {
            return "A ring a ding a ding a dong a ding dong ding dong ding ding. mate!";
        }
        return "public void bribeJudges(){. Oops.";
    }

}
