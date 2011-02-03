package com.dringenburg.helloworld;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SpanishActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("Hola Mundo!");
        setContentView(textview);
    }
}