package com.github.hotbugfix;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(TestHot.getBackgroundText());
    }

    public void clickHotfix(View view) {

    }

    public void clickLook(View view) {
        tv.setText(TestHot.getBackgroundText());
    }
}
