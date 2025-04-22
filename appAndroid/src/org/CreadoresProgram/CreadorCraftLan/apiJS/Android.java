package org.CreadoresProgram.CreadorCraftLan.apiJS;
import org.CreadoresProgram.CreadorCraftLan.services.CreadorCraftLanServerService;
import android.webkit.JavascriptInterface;
public class Android{
    private CreadorCraftLanServerService mService;
    public Android(CreadorCraftLanServerService service){
        this.mService = service;
    }
    @JavascriptInterface
    public void exit(){
        mService.stopForeground(true);
        mService.stopSelf();
    }
}