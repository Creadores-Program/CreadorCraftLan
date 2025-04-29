package org.CreadoresProgram.CreadorCraftLan.services;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.CreadoresProgram.CreadorCraftLan.apiJS.*;
import org.CreadoresProgram.CreadorCraftLan.MainActivity;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import ds.desktop.notify.DesktopNotify;

public class CreadorCraftLanServerService{
    private String tag = "[CreadorCraftLan Service]";
    private Context contextJS;
    public CreadorCraftLanServerService(String serverFilesBase){
        try{
            this.contextJS = Context.newBuilder("js").allowHostAccess(HostAccess.EXPLICIT).build();
            this.contextJS.getBindings("js").putMember("ServerWebGamePostServerJava", new ServerWebGamePostServerJS(this.contextJS));
            this.contextJS.getBindings("js").putMember("SystemJava", new SystemJS());
            this.contextJS.eval(loadScriptFromResources("/libsJS/require.min.js"));
            this.contextJS.eval(loadScriptFromResources("/libsJS/customEventPoly.js"));
            this.contextJS.eval(loadScriptFromResources("/libsJS/ServerWebGamePostClient.js"));
            this.contextJS.eval(loadScriptFromResources("/libsJS/ServerWebGamePostServer.js"));
            this.contextJS.eval(loadScriptFromResources("/libsJS/CCLAPI.js"));
        }catch(Exception err){
            System.err.println("Error al ejecutar JS!");
        }
        String serverFiles = new String(Base64.getDecoder().decode(serverFilesBase), StandardCharsets.UTF_8);
        JSONArray serverFilesJson = JSON.parseArray(serverFiles);
        for(int i = 0; i < serverFilesJson.size(); i++){
            registerFile(serverFilesJson.getJSONObject(i).getString("content"), serverFilesJson.getJSONObject(i).getString("path"));
        }
        getNotification();
        try{
            this.contextJS.eval("js", "let maniloa = require('manifest.json');\nlet mainJS = require(maniloa.main);\nmainJS.onLoad();");
            this.contextJS.eval("js", "CCLAPI.dispatchEvent(new CustomEvent('start', { cancelable: false }));");
        }catch(Exception err){
            System.err.println("Error al ejecutar JS!" + err.getMessage());
        }
    }
    private Source loadScriptFromResources(String resourcePath) throws IOException {
        URL resourceUrl = MainActivity.class.getResource(resourcePath);

        if (resourceUrl == null) {
            return null;
        }
        return Source.newBuilder("js", resourceUrl)
            .name(resourcePath.substring(resourcePath.lastIndexOf('/') + 1))
            .build();
    }
    private void registerFile(String code, String fileDir){
      try{
        if(fileDir.toLowerCase().endsWith(".js")){
            this.contextJS.eval("js", "require.register("+JSON.toJSONString(fileDir)+", "+JSON.toJSONString(code)+");");
        }else if(fileDir.toLowerCase().endsWith(".json")){
            this.contextJS.eval("js", "require.register("+JSON.toJSONString(fileDir)+", function(module){ module.exports = JSON.parse("+JSON.toJSONString(code)+"); });");
        }else{
            this.contextJS.eval("js", "require.register("+JSON.toJSONString(fileDir)+", function(module){ module.exports = "+JSON.toJSONString(code)+"; });");
        }
      }catch(Exception err){
        System.err.println("Error al ejecutar JS!" + err.getMessage());
      }
    }
    private void getNotification(){
        DesktopNotify.showDesktopMessage("CreadorCraftLan Service Server", "Servidor Iniciado!", DesktopNotify.DEFAULT, Toolkit.getDefaultToolkit().getImage(MainActivity.class.getResource("/ic_notification.png")), (evt)->{
            contextJS.eval("js", "CCLAPI.dispatchEvent(new CustomEvent('stop', { cancelable: false }));");
            contextJS.close();
        });
    }
}
