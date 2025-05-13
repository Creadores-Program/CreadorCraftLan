hostPlayer.addEventListener("click", async function(){
    let server = new APICreadorCraftLan("ServerLan");
    await server.start();
    conectPlayer.click();
});
conectPlayer.addEventListener("click", function(){
    let host = "localhost";
    let port = "8080";
    let client = new ServerWebGamePostClient(host, port, false);
    client.setProcessDatapacks(function(datapack){
        GameProps.sendPlayerMessage(datapack.playerName +": "+ datapack.message);
    });
    GameProps.addEventListener("playerMessageEvent", function(event){
        let message = event.detail.message;
        client.sendDataPacket({
            identifier: GameProps.getPlayerId(),
            playerName: GameProps.getPlayerName(),
            message: message
        });
    });
    ContentG.innerHTML = "<div id='instrucGame'>Envia un mensaje en el Chat!</div>";
});