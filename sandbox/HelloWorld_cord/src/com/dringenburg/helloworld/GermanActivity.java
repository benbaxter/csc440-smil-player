package com.dringenburg.helloworld;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class GermanActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("Hallo Welt!");
        setContentView(textview);
    }
}