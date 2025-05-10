package org.CreadoresProgram.CreadorCraftLan.apiJS;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Context;
import org.CreadoresProgram.ServerWebGamePost.server.ServerWebGamePostServer;
import org.CreadoresProgram.ServerWebGamePost.server.ProcessDatapackServer;
import androidx.annotation.NonNull;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.annotation.Nullable;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSON;
public class ServerWebGamePostServerJS{
    private Context context;
    private List<ServerWebGamePostServer> serversMap = new ArrayList<>();
    public ServerWebGamePostServerJS(Context context){
        this.context = context;
    }
    @HostAccess.Export
    public int openServer(@NonNull int port, @Nullable String imgSrc, @NonNull String callBjs){
        ServerWebGamePostServer server = new ServerWebGamePostServer(port, imgSrc, new ProcessDatapackServer(){
            @Override
            public void processDatapack(JSONObject datapack){
                context.eval("js", callBjs + "(" + datapack.toJSONString() + ")");
            }
        });
        serversMap.add(server);
        return serversMap.size() - 1;
    }
    @HostAccess.Export
    public void sendDataPacket(@NonNull int idServer, @NonNull String identifier, @NonNull String datapack){
        serversMap.get(idServer).sendDataPacket(identifier, JSON.parseObject(datapack));
    }
    @HostAccess.Export
    public void deletePlayer(@NonNull int idServer, @NonNull String identifier){
        serversMap.get(idServer).getPlayers().remove(identifier);
    }
    @HostAccess.Export
    public String getPlayers(@NonNull int idServer){
        JSONObject json = new JSONObject();
        for (Map.Entry<String, JSONArray> entry : serversMap.get(idServer).getPlayers().entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json.toJSONString();
    }
    @HostAccess.Export
    public void stop(@NonNull int idServer){
        serversMap.get(idServer).stop();
        serversMap.remove(idServer);
    }
    @HostAccess.Export
    public void addFilterOrigin(@NonNull int idServer, @NonNull String origin){
        serversMap.get(idServer).addFilterOrigin(origin);
    }
    @HostAccess.Export
    public void removeFilterOrigin(@NonNull int idServer, @NonNull String origin){
        serversMap.get(idServer).removeFilterOrigin(origin);
    }
    @HostAccess.Export
    public void banIp(@NonNull int idServer, @NonNull String ip){
        serversMap.get(idServer).banIp(ip);
    }
    @HostAccess.Export
    public void unbanIp(@NonNull int idServer, @NonNull String ip){
        serversMap.get(idServer).unbanIp(ip);
    }
}