package de.rouvenkruse.cordova.plugins.cookieemperor;
 
import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.WebSettings;
import android.webkit.WebView;
 
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewClient;
import org.apache.cordova.engine.SystemWebViewEngine;
 

public class CustomWebViewEngine extends SystemWebViewEngine {

    public static class CustomWebView extends SystemWebView {

        public CustomWebView(Context context) {
            super(context);
        }

        public CustomWebView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
 
        @Override
        public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
            InputConnection connection = super.onCreateInputConnection(outAttrs);
 
            // Many Samsung phones don't show decimal points on html number inputs by default.
            if ((outAttrs.inputType & InputType.TYPE_CLASS_NUMBER) == InputType.TYPE_CLASS_NUMBER)
                outAttrs.inputType |= InputType.TYPE_NUMBER_FLAG_DECIMAL;
 
            return connection;
        }
    }
 
    public CustomWebViewEngine(Context context, CordovaPreferences preferences) {
        this(new CustomWebView(context), preferences);
    }
 
    public CustomWebViewEngine(SystemWebView webView) {
        super(webView);
        setWebViewClient(webView);
    }

    public CustomWebViewEngine(SystemWebView webView, CordovaPreferences preferences) {
        super(webView, preferences);
        setWebViewClient(webView);
    }

    private void setWebViewClient(SystemWebView webView) {
        webView.setWebViewClient(new SystemWebViewClient(this) {

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.v("LOADING_RESOURCE", "Loading: " + url);
                CookieEmperor.loadResource(url);
                super.onLoadResource(view, url);
            }
        });
        WebSettings settings = webView.getSettings();
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 6.0; LGMS631 Build/MRA58K; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/56.0.2924.87 Mobile Safari/537.36");
    }
}