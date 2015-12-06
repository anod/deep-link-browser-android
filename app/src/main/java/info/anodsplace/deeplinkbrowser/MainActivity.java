package info.anodsplace.deeplinkbrowser;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements DeepLinkWebViewClient.Listener {

    private static final String PREFS_NAME = "prefs";
    public static final String KEY_DOMAIN = "domain";
    public static final String KEY_URL = "url";
    private WebView mWebView;
    private Toolbar mToolbar;
    private SharedPreferences mSettings;
    private EditText mUrlView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        mUrlView = (EditText)mToolbar.findViewById(android.R.id.edit);
        mUrlView.clearFocus();
        mUrlView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    String url = v.getText().toString().trim();
                    if (!url.startsWith("http")) {
                        url = "http://"+url;
                    }
                    onUrlChange(url);
                    mWebView.loadUrl(url);
                    return true;
                }
                return false;
            }
        });

        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mSettings = getSharedPreferences(PREFS_NAME, 0);
        String domain = mSettings.getString(KEY_DOMAIN, null);
        String url = mSettings.getString(KEY_URL, getString(R.string.default_url));

        setWebViewClient(domain);
        if (TextUtils.isEmpty(domain)) {
            showDomainNameDialog();
        }

        mUrlView.setText(url);
        mWebView.loadUrl(url);

    }

    private void showDomainNameDialog() {
        final AppCompatEditText editBox = new AppCompatEditText(this);
        editBox.setText(R.string.default_domain_name);
        new AlertDialog.Builder(this)
                .setView(editBox)
                .setTitle(R.string.title_domain_name)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String domain = editBox.getText().toString();
                        if (!TextUtils.isEmpty(domain)) {
                            mSettings.edit().putString(KEY_DOMAIN, domain).apply();
                            setWebViewClient(domain);
                        }
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void setWebViewClient(String domain) {
        mWebView.setWebViewClient(new DeepLinkWebViewClient(this, domain, this));
    }

    @Override
    public void onUrlChange(String url) {
        mUrlView.setText(url);
        mSettings.edit().putString(KEY_URL, url).apply();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }


}
