package org.CreadoresProgram.ServerWebGamePost.client;

import lombok.Getter;
import lombok.NonNull;
import org.json.JSONObject;
import org.json.JSONArray;

public class ProcessDatapackClient {
    @Getter
    public ServerWebGamePostClient server;
    
    public ProcessDatapackClient(@NonNull ServerWebGamePostClient server) {
        this.server = server;
    }
    
    /**
     * Itera sobre el array "datapacksLot" y procesa cada datapack recibido.
     *
     * @param datapacksLot Objeto JSON que contiene el array de datapacks en la clave "datapacksLot".
     */
    public final void process(@NonNull JSONObject datapacksLot) {
        JSONArray datapacks = datapacksLot.optJSONArray("datapacksLot");
        if (datapacks != null) {
            for (int i = 0; i < datapacks.length(); i++) {
                JSONObject datapack = datapacks.optJSONObject(i);
                if (datapack != null) {
                    this.processDatapack(datapack);
                }
            }
        }
    }
    
    /**
     * Procesa individualmente cada datapack.
     *
     * @param datapack Objeto JSON con los datos del datapack.
     */
    public void processDatapack(@NonNull JSONObject datapack) {
    }
}