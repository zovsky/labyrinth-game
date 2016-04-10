package com.zovsky.labyrinth;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class RulesActivity extends Activity {

    private WebView rules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        rules = (WebView) findViewById(R.id.rules_web_view);
        String rules_str = getResources().getString(R.string.rules_html);
        rules.loadDataWithBaseURL(null, rules_str, "text/html", "utf-8", null);
    }
}
