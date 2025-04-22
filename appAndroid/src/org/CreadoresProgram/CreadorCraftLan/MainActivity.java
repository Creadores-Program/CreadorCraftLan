package org.CreadoresProgram.CreadorCraftLan;
import android.app.Activity;
import android.os.Bundle;
import android.net.Uri;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.graphics.Color;
import android.widget.Toast;
import org.CreadoresProgram.CreadorCraftLan.services.CreadorCraftLanServerService;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;

public class MainActivity extends Activity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        if(Intent.ACTION_VIEW.equals(action) && data != null){
            String baseData = data.getPath();
            if(baseData != null && baseData.length() > 1){
                LoadServer(baseData.substring(1));
            }else{
                Toast.makeText(this, "No se puede abrir el Servidor sin Codigo!", Toast.LENGTH_SHORT).show();
            }
            finish();
            return;
        }else{
            setContentView(R.layout.layout_main);
            WebView webView = findViewById(R.id.webview);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url){
                    if(url.equals("file:///android_asset/MainActivity.html")){
                        return false;
                    }
                    onCCLink(url);
                    return true;
                }
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
                    String url = request.getUrl().toString();
                    if(url.equals("file:///android_asset/MainActivity.html")){
                        return false;
                    }
                    onCCLink(url);
                    return true;
                }
            });
            WebSettings webSettings = webView.getSettings();
            webSettings.setBuiltInZoomControls(false);
            webSettings.setDisplayZoomControls(false);
            webSettings.setSupportZoom(false);
            webSettings.setUseWideViewPort(true);
            webView.setBackgroundColor(Color.BLACK);
            webView.loadUrl("file:///android_asset/MainActivity.html");
        }
    }
    private void LoadServer(String baseData){
        Intent serviceServerLan = new Intent(this, CreadorCraftLanServerService.class);
        serviceServerLan.putExtra("dataFilesJS", baseData);
        startService(serviceServerLan);
    }
    private void onCCLink(String url){
        if(url.equals("https://creadorcraft.com")){
            Uri ccpag = Uri.parse("https://creadorcraftcp.blogspot.com/");
            Intent intent = new Intent(Intent.ACTION_VIEW, ccpag);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }else{
                Toast.makeText(this, "No se encontró CreadorCraft.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}