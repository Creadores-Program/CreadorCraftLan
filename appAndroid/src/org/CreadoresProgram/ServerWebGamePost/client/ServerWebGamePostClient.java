package org.CreadoresProgram.ServerWebGamePost.client;

import androidx.annotation.NonNull;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

public final class ServerWebGamePostClient {
    private String domain;
    private int port;
    private boolean isHttps;
    
    public ProcessDatapackClient processDatapacks;
    public ProcessDatapackClient getProcessDatapacks() {
        return processDatapacks;
    }
    public void setProcessDatapacks(ProcessDatapackClient processDatapacks) {
        this.processDatapacks = processDatapacks;
    }
    
    public String userAgent = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Mobile Safari/537.36";
    public String getUserAgent() {
        return userAgent;
    }
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    private OkHttpClient httpClient = new OkHttpClient();
    
    public ServerWebGamePostClient(@NonNull String domain, @NonNull int port, @NonNull boolean isHttps) {
        this.domain = domain;
        this.port = port;
        this.isHttps = isHttps;
        this.processDatapacks = new ProcessDatapackClient(this);
    }
    
    public void sendDataPacket(@NonNull JSONObject datapack) {
        try {
            String prefix = this.isHttps ? "https://" : "http://";
            String datapackStr = datapack.toString();
            String url = prefix + this.domain + ":" + this.port + "/ServerWebGamePost";
            
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(datapackStr, mediaType);
            
            Request request = new Request.Builder()
                    .url(url)
                    .header("Content-Type", "application/json")
                    .header("User-Agent", this.userAgent)
                    .post(requestBody)
                    .build();
            
            Response response = httpClient.newCall(request).execute();
            if (response.body() != null) {
                String responseBodyStr = response.body().string();
                JSONObject responseJson = new JSONObject(responseBodyStr);
                this.processDatapacks.process(responseJson);
            } else {
                System.err.println("Response body is null");
            }
        } catch (Exception e) {
            System.err.println("Error sending data packet: " + e);
        }
    }
}