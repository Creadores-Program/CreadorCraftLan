package org.CreadoresProgram.ServerWebGamePost.server;

import lombok.NonNull;
import lombok.Setter;
import org.json.JSONObject;
import org.json.JSONArray;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.util.HashMap;

public class ProcessDatapackServer {

    @Setter
    public ServerWebGamePostServer server;

    public ProcessDatapackServer() {
    }

    /**
     * Procesa la petición POST para la ruta "/ServerWebGamePost".
     * Se verifican los filtros de acceso (por IP y "Origin"), se parsea el body (almacenado en "postData")
     * y se delega el procesamiento del datapack.
     *
     * @param session sesión de la petición
     * @param files   mapa con el contenido del cuerpo (clave "postData")
     * @return Cadena JSON con la respuesta
     * @throws IOException
     * @throws NanoHTTPD.ResponseException en casos de acceso prohibido o falla en el parseo
     */
    public String handleRequest(NanoHTTPD.IHTTPSession session, HashMap<String, String> files)
            throws IOException, NanoHTTPD.ResponseException {
        if (server.bannedIps.contains(session.getRemoteIpAddress())) {
            throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.FORBIDDEN, "Forbidden");
        }
        String origin = session.getHeaders().get("origin");
        if (!server.getFilters().isEmpty() && (origin == null || !server.getFilters().contains(origin))) {
            throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.FORBIDDEN, "Forbidden");
        }
        String postData = files.get("postData");
        if (postData == null) {
            postData = "";
        }
        JSONObject datapack = new JSONObject(postData);
        processDatapack(datapack);
        JSONObject reponDatapacks = new JSONObject();
        String identifier = datapack.optString("identifier");
        JSONArray playerArray = server.getPlayers().get(identifier);
        if (playerArray == null) {
            playerArray = new JSONArray();
        }
        reponDatapacks.put("datapacksLot", playerArray);
        server.getPlayers().put(identifier, new JSONArray());
        return reponDatapacks.toString();
    }

    /**
     * Implementa la lógica para procesar cada datapack recibido.
     *
     * @param datapack El objeto JSON recibido en la petición.
     */
    public void processDatapack(@NonNull JSONObject datapack) {
    }
}