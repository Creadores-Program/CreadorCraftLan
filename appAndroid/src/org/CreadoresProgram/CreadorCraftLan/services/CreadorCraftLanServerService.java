package org.CreadoresProgram.CreadorCraftLan.services;
import android.app.Service;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.ValueCallback;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.util.Base64;
import java.nio.charset.StandardCharsets;
import org.CreadoresProgram.CreadorCraftLan.Utils.ChromeExtra;
import org.CreadoresProgram.CreadorCraftLan.MainActivity;
import org.CreadoresProgram.CreadorCraftLan.apiJS.Android;
import android.os.IBinder;
import org.json.JSONArray;
import org.CreadoresProgram.CreadorCraftLan.apiJS.ServerWebGamePostServerJS;
import org.CreadoresProgram.CreadorCraftLan.R;

public class CreadorCraftLanServerService extends Service{
    private String tag = "[CreadorCraftLan Service]";
    private int notificacionId = 12;
    private static final String CHANNEL_ID = "CreadorCraftLanService";
    public static final String ACTION_STOP_SERVICE = "org.CreadoresProgram.CreadorCraftLan.ACTION_STOP_SERVICE";
    private WebView webView;

    @Override
    public void onCreate(){
        super.onCreate();
        Log.i(tag, "Iniciando Servicio CreadorCraft LAN...");
        Log.i(tag, "Creadores Program© 2025");
        Log.i(tag, "'La Revolucion del Codigo'");
        createNotificationChannel();
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
        startForeground(notificacionId, getNotification());
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
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                if(url.equals("file:///android_asset/LanServerAPI/Load.html")){
                    return false;
                }else{
                    return true;
                }
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
                String url = request.getUrl().toString();
                if(url.equals("file:///android_asset/LanServerAPI/Load.html")){
                    return false;
                }else{
                    return true;
                }
            }
        });
        webView.setWebChromeClient(new ChromeExtra());
        webView.addJavascriptInterface(new ServerWebGamePostServerJS(webView), "ServerWebGamePostServerJava");
        webView.addJavascriptInterface(new Android(this), "Android");
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
    private Notification getNotification(){
        int pendingIntentFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        }else{
            pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        int stopRequestCode = 0;
        int activityRequestCode = 1;
        Intent stopIntent = new Intent(this, CreadorCraftLanServerService.class);
        stopIntent.setAction(ACTION_STOP_SERVICE);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, stopRequestCode, stopIntent, pendingIntentFlags);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, activityRequestCode, notificationIntent, pendingIntentFlags);
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        }else{
            builder = new Notification.Builder(this);
        }
        builder.setContentTitle("CreadorCraftLan Service Server")
            .setContentText("Servidor Iniciado!")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(Notification.PRIORITY_DEFAULT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        return builder.build();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Servidor CreadorCraftLan Info",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setDescription("Notifica cuando esta Activo un servidor LAN de CreadorCraft y permite apagarlo!");
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
                Log.d(tag, "Notification Channel created.");
            } else {
                 Log.e(tag, "Failed to get NotificationManager.");
            }
        }
    }
}