package de.rouvenkruse.cordova.plugins.cookieemperor;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.CookieManager;

public class CookieEmperor extends CordovaPlugin {

    private static CallbackContext loadResourceCallbackContext;
    private static String loadResourceFilter;

    public static final String ACTION_GET_ALL_COOKIES = "getAllCookies";
    public static final String ACTION_GET_COOKIE_VALUE = "getCookieValue";
    public static final String ACTION_SET_COOKIE_VALUE = "setCookieValue";
    public static final String ACTION_SET_COOKIE_MULTI = "setCookieMulti";
    public static final String ACTION_CLEAR_COOKIES = "clearCookies";

    public static final String ACTION_ON_LOAD_RESOURCE = "onLoadResource";

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (ACTION_GET_ALL_COOKIES.equals(action)) {
            return this.getAllCookies(args, callbackContext);
        }
        else if (ACTION_GET_COOKIE_VALUE.equals(action)) {
            return this.getCookie(args, callbackContext);
        }
        else if (ACTION_SET_COOKIE_VALUE.equals(action)) {
            return this.setCookie(args, callbackContext);
        }
        else if (ACTION_SET_COOKIE_MULTI.equals(action)) {
            return this.setCookieMulti(args, callbackContext);
        }
        else if (ACTION_ON_LOAD_RESOURCE.equals(action)) {
            return this.onLoadResource(args, callbackContext);
        }
        else if (ACTION_CLEAR_COOKIES.equals(action)) {
            CookieManager cookieManager = CookieManager.getInstance();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                    @Override
                    public void onReceiveValue(Boolean value) {
                        callbackContext.success();
                    }
                });
                cookieManager.flush();
            }
            else {
                cookieManager.removeAllCookie();
                callbackContext.success();
            }
            return true;
        } else {
            callbackContext.error("Invalid action");
            return false;
        }
    }

    /**
     * returns all cookies for domain
     * @param args
     * @param callbackContext
     * @return
     */
    private boolean onLoadResource(JSONArray args, final CallbackContext callbackContext) {
        try {
            CookieEmperor.loadResourceFilter = args.getString(0);
            CookieEmperor.loadResourceCallbackContext = callbackContext;
            return true;
        }
        catch(JSONException e) {
            callbackContext.error("JSON parsing error");
            return false;
        }
    }

    /**
     * Invokes callback when the custom webview loads a resource matching the filter.
     * @param bundle
     * @return
     */
    public static void loadResource(String url) {
        final CallbackContext callbackContext = CookieEmperor.loadResourceCallbackContext;
        // TODO(ram): Test loadResourceFilter
        if (callbackContext != null && url != null) {
            try {
                JSONObject json = new JSONObject();
                json.put("url", url);

                PluginResult pluginresult = new PluginResult(PluginResult.Status.OK, json);
                pluginresult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginresult);
            }
            catch(JSONException e) {
                callbackContext.error("JSON parsing error");
            }
        }
    }

    /**
     * returns all cookies for domain
     * @param args
     * @param callbackContext
     * @return
     */
    private boolean getAllCookies(JSONArray args, final CallbackContext callbackContext) {
        try {
            final String url = args.getString(0);

            cordova
                    .getThreadPool()
                    .execute(new Runnable() {
                        public void run() {
                            try {
                                CookieManager cookieManager = CookieManager.getInstance();
                                String[] cookies = cookieManager.getCookie(url).split("; ");
                                String[] cookiePairs;
                                String cookieKey = "";
                                String cookieValue = "";

                                String cookieJSONString = "{";
                                for (int i = 0; i < cookies.length; i++) {
                                    cookiePairs = cookies[i].split("=");
                                    cookieKey = cookiePairs[0].trim();
                                    cookieValue = cookiePairs[1].trim();
                                    cookieJSONString += cookieKey + ": \"" + cookieValue + "\"";
                                    if (i < cookies.length - 1) {
                                        cookieJSONString += ", ";
                                    }
                                }
                                cookieJSONString += "}";

                                JSONObject json = new JSONObject(cookieJSONString);
                                PluginResult res = new PluginResult(PluginResult.Status.OK, json);
                                callbackContext.sendPluginResult(res);
                            }
                            catch (Exception e) {
                                callbackContext.error(e.getMessage());
                            }
                        }
                    });

            return true;
        }
        catch(JSONException e) {
            callbackContext.error("JSON parsing error");
        }

        return false;
    }

    /**
     * returns cookie under given key
     * @param args
     * @param callbackContext
     * @return
     */
    private boolean getCookie(JSONArray args, final CallbackContext callbackContext) {
        try {
            final String url = args.getString(0);
            final String cookieName = args.getString(1);

            cordova
                    .getThreadPool()
                    .execute(new Runnable() {
                        public void run() {
                            try {
                                CookieManager cookieManager = CookieManager.getInstance();
                                String[] cookies = cookieManager.getCookie(url).split("; ");
                                String cookieValue = "";

                                for (int i = 0; i < cookies.length; i++) {
                                    if (cookies[i].contains(cookieName + "=")) {
                                        cookieValue = cookies[i].split("=")[1].trim();
                                        break;
                                    }
                                }

                                JSONObject json = null;

                                if (cookieValue != "") {
                                    json = new JSONObject("{cookieValue:\"" + cookieValue + "\"}");
                                }

                                if (json != null) {
                                    PluginResult res = new PluginResult(PluginResult.Status.OK, json);
                                    callbackContext.sendPluginResult(res);
                                }
                                else {
                                    callbackContext.error("Cookie not found!");
                                }
                            }
                            catch (Exception e) {
                                callbackContext.error(e.getMessage());
                            }
                        }
                    });

            return true;
        }
        catch(JSONException e) {
            callbackContext.error("JSON parsing error");
        }

        return false;
    }

    /**
     * sets cookie value under given key
     * @param args
     * @param callbackContext
     * @return boolean
     */
    private boolean setCookie(JSONArray args, final CallbackContext callbackContext) {
        try {
            final String url = args.getString(0);
            final String cookieName = args.getString(1);
            final String cookieValue = args.getString(2);

            cordova
                    .getThreadPool()
                    .execute(new Runnable() {
                        public void run() {
                            try {
                                CookieManager cookieManager = CookieManager.getInstance();
                                cookieManager.setCookie(url, cookieName + "=" + cookieValue);

                                PluginResult res = new PluginResult(PluginResult.Status.OK, "Successfully added cookie");
                                callbackContext.sendPluginResult(res);
                            }
                            catch (Exception e) {
                                callbackContext.error(e.getMessage());
                            }
                        }
                    });

            return true;
        }
        catch(JSONException e) {
            callbackContext.error("JSON parsing error");
        }

        return false;
    }

    /**
     * sets cookie value under given key
     * @param args
     * @param callbackContext
     * @return boolean
     */
    private boolean setCookieMulti(JSONArray args, final CallbackContext callbackContext) {
        try {
            final String url = args.getString(0);
            final String cookie = args.getString(1);

            cordova
                    .getThreadPool()
                    .execute(new Runnable() {
                        public void run() {
                            try {
                                CookieManager cookieManager = CookieManager.getInstance();
                                cookieManager.setCookie(url, cookie);

                                PluginResult res = new PluginResult(PluginResult.Status.OK, "Successfully added cookie");
                                callbackContext.sendPluginResult(res);
                            }
                            catch (Exception e) {
                                callbackContext.error(e.getMessage());
                            }
                        }
                    });

            return true;
        }
        catch(JSONException e) {
            callbackContext.error("JSON parsing error");
        }

        return false;
    }
}
