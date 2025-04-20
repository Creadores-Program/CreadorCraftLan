package org.CreadoresProgram.CreadorCraftLan.services;
import android.app.Service;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.ValueCallback;
import android.util.Log;
import android.util.Base64;
import java.nio.charset.StandardCharsets;
import org.CreadoresProgram.CreadorCraftLan.Utils.ChromeExtra;
import android.os.IBinder;
import org.json.JSONArray;
import org.CreadoresProgram.CreadorCraftLan.apiJS.ServerWebGamePostServerJS;

public class CreadorCraftLanServerService extends Service{
    private String tag = "[CreadorCraftLan Service]";
    private int notificacionId = 12;
    public static final String ACTION_STOP_SERVICE = "org.CreadoresProgram.CreadorCraftLan.ACTION_STOP_SERVICE";
    private WebView webView;

    @Override
    public void onCreate(){
        super.onCreate();
        Log.i(tag, "Iniciando Servicio CreadorCraft LAN...");
        Log.i(tag, "Creadores Program© 2025");
        Log.i(tag, "'La Revolucion del Codigo'");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP_SERVICE.equals(intent.getAction())) {
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }
        String serverFilesBase = intent.getStringExtra("dataFilesJS");
        startWebView();
        String serverFiles = new String(Base64.decode(serverFilesBase, Base64.DEFAULT), StandardCharsets.UTF_8);
        JSONArray serverFilesJson = new JSONArray(serverFiles);
        for(int i = 0; i < serverFilesJson.length(); i++){
            registerFile(serverFilesJson.getJSONObject(i).getString("content"), serverFilesJson.getJSONObject(i).getString("path"));
        }
        evalJS("let maniloa = require('manifest.json');\nlet mainJS = require(maniloa.main);\nmainJS.onLoad();");
        evalJS("CCLAPI.dispatchEvent(new CustomEvent('start', { cancelable: false }));");
        return START_STICKY;
    }
    private void startWebView(){
        webView = new WebView(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(false);
        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setDomStorageEnabled(false);
        webSettings.setDatabaseEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new ChromeExtra());
        webView.addJavascriptInterface(new ServerWebGamePostServerJS(webView), "ServerWebGamePostServerJava");
        webView.loadUrl("file:///android_asset/LanServerAPI/Load.html");
    }
    private void evalJS(String code){
        webView.post(new Runnable(){
            @Override
            public void run(){
                webView.evaluateJavascript("Babel.transform("+org.json.JSONObject.quote(code)+", {presets:['es2015']}).code", new ValueCallback<String>(){
                    @Override
                    public void onReceiveValue(String value){
                        webView.post(new Runnable(){
                            @Override
                            public void run(){
                                webView.evaluateJavascript("eval("+value+");", null);
                            }
                        })
                    }
                });
            }
        });
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(tag, "Apagando Servicio CreadorCraft LAN...");
        evalJS("CCLAPI.dispatchEvent(new CustomEvent('stop', { cancelable: false }));");
        webView.post(new Runnable(){
            @Override
            public void run(){
                webview.destroy();
            }
        });
        Log.i(tag, "Apagado y Destruido!");
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void registerFile(String code, String fileDir){
        webView.post(new Runnable(){
            @Override
            public void run(){
                webView.evaluateJavascript("Babel.transform("+org.json.JSONObject.quote(code)+", {presets:['es2015']}).code", new ValueCallback<String>(){
                    @Override
                    public void onReceiveValue(String value){
                        webView.post(new Runnable(){
                            @Override
                            public void run(){
                                webView.evaluateJavascript("if("+org.json.JSONObject.quote(fileDir)+".endsWith('.json')){ require.register("+org.json.JSONObject.quote(fileDir)+", function(module){ module.exports = JSON.parse("+value+"); }); }else{ require.register("+org.json.JSONObject.quote(fileDir)+", "+value+"); }", null);
                            }
                        })
                    }
                });
            }
        });
    }
}