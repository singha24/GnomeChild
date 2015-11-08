package com.example.assa.gnomechild;

/**
 * Created by Aaron on 08/11/2015.
 */
public class Answer {
    private double value;
    private String text;

    public Answer(String[] strings){
        value = Double.parseDouble(strings[1]);
        text = "";
        int x = 2;

        text+= strings[x];
        x++;
        while (x < strings.length){
            text+= ",";
            text+= strings[x];
            x++;

        }
    }

    public double getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
