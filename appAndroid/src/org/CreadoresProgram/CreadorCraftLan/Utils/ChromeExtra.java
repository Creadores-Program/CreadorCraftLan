package org.CreadoresProgram.CreadorCraftLan.Utils;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.webkit.JsResult;
import android.webkit.JsPromptResult;
import android.webkit.ConsoleMessage;
import android.util.Log;

public class ChromeExtra extends WebChromeClient{
    private String tag = "[CreadorCraft Lan Service Server Console]";
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        Log.i(tag, message);
        result.confirm();
        return true;
    }
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        Log.i(tag, message);
        result.cancel();
        Log.w(tag, "No Puedes Usar Confirm!");
        return true;
    }
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        Log.i(tag, message);
        result.cancel();
        Log.w(tag, "No Puedes Usar Prompt!");
        return true;
    }
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage){
        switch (consoleMessage.messageLevel()) {
            case DEBUG:
                Log.d(tag, consoleMessage.message());
                break;
            case ERROR:
                Log.e(tag, consoleMessage.message());
                break;
            case LOG:
                Log.i(tag, consoleMessage.message());
                break;
            case TIP:
                Log.i(tag, consoleMessage.message());
                break;
            case WARNING:
                Log.w(tag, consoleMessage.message());
                break;
        }
    }
}