package org.CreadoresProgram.ServerWebGamePost.server;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.ResponseException;
import org.json.JSONArray;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public final class ServerWebGamePostServer {

    private int port;
    public int getPort() {
        return port;
    }

    public NanoExtendedServer nanoServer;
    public NanoExtendedServer getNanoServer() {
        return nanoServer;
    }

    public ProcessDatapackServer processDatapacks;
    public ProcessDatapackServer getProcessDatapacks() {
        return processDatapacks;
    }

    private HashMap<String, JSONArray> players;
    public HashMap<String, JSONArray> getPlayers() {
        return players;
    }

    public ArrayList<String> bannedIps = new ArrayList<>();

    private ArrayList<String> filters = new ArrayList<>();
    public ArrayList<String> getFilters() {
        return filters;
    }

    private String imgSrc;

    public ServerWebGamePostServer(@NonNull int port) {
        this(port, null);
    }

    public ServerWebGamePostServer(@NonNull int port, @Nullable String imgSrc) {
        this(port, imgSrc, new ProcessDatapackServer());
    }

    public ServerWebGamePostServer(@NonNull int port, @Nullable String imgSrc, @NonNull ProcessDatapackServer processDatapacks) {
        this.port = port;
        this.imgSrc = imgSrc;
        this.processDatapacks = processDatapacks;
        this.processDatapacks.setServer(this);
        this.players = new HashMap<>();
        
        this.nanoServer = new NanoExtendedServer(this.port);
        try {
            this.nanoServer.start();
            System.out.println("Servidor iniciado en el puerto " + this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDataPacket(@NonNull String identifier, @NonNull JSONObject datapack) {
        if (!this.players.containsKey(identifier)) {
            this.players.put(identifier, new JSONArray());
        }
        this.players.get(identifier).put(datapack);
    }

    public void stop() {
        if (this.nanoServer != null) {
            this.nanoServer.stop();
            System.out.println("Servidor detenido");
        }
    }

    public void banIp(@NonNull String ip) {
        this.bannedIps.add(ip);
    }

    public void unbanIp(@NonNull String ip) {
        this.bannedIps.remove(ip);
    }

    public void addFilterOrigin(@NonNull String origin) {
        this.filters.add(origin);
    }

    public void removeFilterOrigin(@NonNull String origin) {
        this.filters.remove(origin);
    }

    private class NanoExtendedServer extends NanoHTTPD {

        public NanoExtendedServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String uri = session.getUri();
            NanoHTTPD.Method method = session.getMethod();
            String clientIp = session.getRemoteIpAddress();

            if (bannedIps.contains(clientIp)) {
                return NanoHTTPD.newFixedLengthResponse(Response.Status.FORBIDDEN, "text/plain", "Access Forbidden");
            }

            if ("/favicon.ico".equals(uri)) {
                if (method == Method.GET) {
                    if (imgSrc == null) {
                        return NanoHTTPD.newFixedLengthResponse("");
                    }
                    try {
                        File logo = new File(imgSrc);
                        FileInputStream fis = new FileInputStream(logo);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesRead);
                        }
                        fis.close();
                        byte[] logoBytes = baos.toByteArray();
                        return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "image/jpeg",
                                new ByteArrayInputStream(logoBytes), logoBytes.length);
                    } catch (Exception e) {
                        System.err.println(e);
                        return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", e.toString());
                    }
                } else {
                    return NanoHTTPD.newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, "text/plain", "Method Not Allowed");
                }
            } else if ("/ServerWebGamePost".equals(uri)) {
                if (method == Method.OPTIONS) {
                    String allow;
                    if (filters.isEmpty()) {
                        allow = "*";
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (String origin : filters) {
                            sb.append(origin).append(",");
                        }
                        if (sb.length() > 0) {
                            sb.setLength(sb.length() - 1);
                        }
                        allow = sb.toString();
                    }
                    Response response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "text/plain", "OK");
                    response.addHeader("Access-Control-Allow-Origin", allow);
                    response.addHeader("Access-Control-Allow-Methods", "POST");
                    response.addHeader("Access-Control-Allow-Headers", "Content-Type");
                    return response;
                } else if (method == Method.POST) {
                    try {
                        HashMap<String, String> files = new HashMap<>();
                        session.parseBody(files);
                        String result = processDatapacks.handleRequest(session, files);
                        Response response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", result);
                        
                        String allow;
                        if (filters.isEmpty()) {
                            allow = "*";
                        } else {
                            StringBuilder sb = new StringBuilder();
                            for (String origin : filters) {
                                sb.append(origin).append(",");
                            }
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 1);
                            }
                            allow = sb.toString();
                        }
                        response.addHeader("Access-Control-Allow-Origin", allow);
                        response.addHeader("Access-Control-Allow-Methods", "POST");
                        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
                        return response;
                    } catch (IOException | ResponseException e) {
                        e.printStackTrace();
                        return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", e.toString());
                    }
                } else {
                    return NanoHTTPD.newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, "text/plain", "Method Not Allowed");
                }
            }
            return NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found");
        }
    }
}