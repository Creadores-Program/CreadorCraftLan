package org.CreadoresProgram.CreadorCraftLan.apiJS;
import org.CreadoresProgram.ServerWebGamePost.server.ServerWebGamePostServer;
import org.CreadoresProgram.ServerWebGamePost.server.ProcessDatapackServer;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import androidx.annotation.Nullable;
import org.json.JSONObject;
import org.json.JSONArray;

public class ServerWebGamePostServerJS{
    private WebView webView;
    private List<ServerWebGamePostServer> serversMap = new ArrayList<>();
    public ServerWebGamePostServerJS(WebView webView){
        this.webView = webView;
    }

    @JavascriptInterface
    public int openServer(@NonNull int port, @Nullable String imgSrc, @NonNull String callBjs){
        ServerWebGamePostServer server = new ServerWebGamePostServer(port, imgSrc, new ProcessDatapackServer(){
            @Override
            public void processDatapack(JSONObject datapack){
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.evaluateJavascript(callBjs + "(" + datapack.toString() + ")", null);
                    }
                });
            }
        });
        serversMap.add(server);
        return serversMap.size() - 1;
    }
    @JavascriptInterface
    public void sendDataPacket(@NonNull int idServer, @NonNull String identifier, @NonNull String datapack){
        serversMap.get(idServer).sendDataPacket(identifier, new JSONObject(datapack));
    }
    @JavascriptInterface
    public void deletePlayer(@NonNull int idServer, @NonNull String identifier){
        serversMap.get(idServer).getPlayers().remove(identifier);
    }
    @JavascriptInterface
    public String getPlayers(@NonNull int idServer){
        JSONObject json = new JSONObject();
        for (Map.Entry<String, JSONArray> entry : serversMap.get(idServer).getPlayers().entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json.toString();
    }
    @JavascriptInterface
    public void stop(@NonNull int idServer){
        serversMap.get(idServer).stop();
        serversMap.remove(idServer);
    }
    @JavascriptInterface
    public void addFilterOrigin(@NonNull int idServer, @NonNull String origin){
        serversMap.get(idServer).addFilterOrigin(origin);
    }
    @JavascriptInterface
    public void removeFilterOrigin(@NonNull int idServer, @NonNull String origin){
        serversMap.get(idServer).removeFilterOrigin(origin);
    }
    @JavascriptInterface
    public void banIp(@NonNull int idServer, @NonNull String ip){
        serversMap.get(idServer).banIp(ip);
    }
    @JavascriptInterface
    public void unbanIp(@NonNull int idServer, @NonNull String ip){
        serversMap.get(idServer).unbanIp(ip);
    }
}