package org.CreadoresProgram.CreadorCraftLan;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.concurrent.Worker;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import java.awt.SystemTray;
import java.awt.Desktop;
import java.awt.TrayIcon;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.AWTException;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.net.URL;
import org.CreadoresProgram.CreadorCraftLan.services.CreadorCraftLanServerService;
public class MainActivity extends Application{
    private String uriScheme = "OpenCreadorCraftLan://";
    private WebView webView;
    private static void showSystemNotification(String title, String message) {
      if (SystemTray.isSupported()) {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = new Image(getClass().getResource("/ic_launcher.jpeg").toExternalForm());

        TrayIcon trayIcon = new TrayIcon(image, "CreadorCraft Lan");
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);

        } catch (AWTException e) {
            System.err.println("No se pudo añadir/mostrar el icono/notificación: " + e.getMessage());
        }
      } else {
        System.out.println("NOTIFICACION [Toast]: " + title + " - " + message);
      }
    }
    public static void main(String[] args){
        String dataUrl = null;
        boolean isData = false;
        if(args != null && args.length > 0){
            for(String arg : args){
                if(arg != null && arg.startsWith(uriScheme)){
                    isData = true;
                    dataUrl = arg;
                    break;
                }
            }
        }
        if(isData){
            if(dataUrl.equals(uriScheme)){
                showSystemNotification("CreadorCraft LAN", "No se puede abrir el Servidor sin Codigo!");
            }else{
                new CreadorCraftLanServerService(dataUrl.replace(uriScheme, ""));
            }
        }else{
            launch(args);
        }
    }
    @Override
    public void start(Stage primaryStage){
        this.webView = new WebView();
        WebEngine webEngine = this.webView.getEngine();
        String currentLoadedUrl;
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                currentLoadedUrl = webEngine.getLocation();
            }
        });
        webEngine.locationProperty().addListener((observable, oldLocation, newLocation) -> {
            if (newLocation == null || newLocation.equals(currentLoadedUrl) || newLocation.equals(oldLocation)) {
                return;
            }
            if(newLocation.equals("https://creadorcraft.com")){
                try{
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(new URI("https://creadorcraftcp.blogspot.com/"));
                    }
                    final String urlToRestore = (oldLocation != null && !oldLocation.equals("about:blank")) ? oldLocation : currentLoadedUrl;
                    if (urlToRestore != null && !urlToRestore.equals(newLocation)) {
                        Platform.runLater(() -> {
                             webEngine.load(urlToRestore);
                         });
                    }
                }catch(URISyntaxException e){
                    showSystemNotification("CreadorCraft LAN", "URL mal formada, no se puede abrir!");
                    final String urlToRestore = (oldLocation != null && !oldLocation.equals("about:blank")) ? oldLocation : currentLoadedUrl;
                    if (urlToRestore != null) Platform.runLater(() -> webEngine.load(urlToRestore));
                }catch(Exception err){
                    showSystemNotification("CreadorCraft LAN", "URL mal formada, no se puede abrir!");
                    final String urlToRestore = (oldLocation != null && !oldLocation.equals("about:blank")) ? oldLocation : currentLoadedUrl;
                    if (urlToRestore != null) Platform.runLater(() -> webEngine.load(urlToRestore));
                }
            }
        });
        webEngine.load(getClass().getResource("/MainActivity.html").toExternalForm());
        StackPane root = new StackPane(this.webView);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("CreadorCraft LAN");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
