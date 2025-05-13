function onLoad(){
    console.info("Loading...");
}
var serverLan;
CCLAPI.addEventListener("start", function(){
    console.info("ServerLan started");
    serverLan = new ServerWebGamePostServer(8080, null, "onDatapack");
});
var players = [];
function onDatapack(datapack){
    if(!(datapack.identifier in players)){
        players.push(datapack.identifier);
    }
    for(var i = 0; i < players.length; i++){
        if(players[i] == null || players[i] == datapack.identifier){
            continue;
        }
        serverLan.sendDataPacket(players[i], {
            playerName: datapack.playerName,
            message: datapack.message
        });
    }
    console.info("Datapack received from " + datapack.playerName);
    console.info("Message: " + datapack.message);
}