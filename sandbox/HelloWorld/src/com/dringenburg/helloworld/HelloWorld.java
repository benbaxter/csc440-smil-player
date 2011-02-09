package com.dringenburg.helloworld;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class HelloWorld extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Reusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, EnglishActivity.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("english").setIndicator("English").setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, GermanActivity.class);
	    spec = tabHost.newTabSpec("german").setIndicator("German").setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, SpanishActivity.class);
	    spec = tabHost.newTabSpec("spanish").setIndicator("Spanish").setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(2);
	}
}