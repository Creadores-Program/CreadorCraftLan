class ServerWebGamePostServer {
    getPort(){
        return this.port;
    }
    getProcessDatapacks(){
        return this.processDatapacks;
    }
    getPlayers(){
        return ServerWebGamePostServerJava.getPlayers(this.idJava);
    }
    constructor(port, imgSrc, processDatapacks){
        this.port = port;
        this.idJava = ServerWebGamePostServerJava.openServer(port, imgSrc, processDatapacks);
        this.imgSrc = imgSrc;
        this.processDatapacks = processDatapacks;
    }
    stop(){
        ServerWebGamePostServerJava.stop(this.idJava);
    }
    sendDataPacket(identifier, datapack){
        ServerWebGamePostServerJava.sendDataPacket(this.idJava, identifier, JSON.stringify(datapack));
    }
    deletePlayer(identifier){
        ServerWebGamePostServerJava.deletePlayer(this.idJava, identifier);
    }
    addFilterOrigin(filter){
        ServerWebGamePostServerJava.addFilterOrigin(this.idJava, filter);
    }
    removeFilterOrigin(filter){
        ServerWebGamePostServerJava.removeFilterOrigin(this.idJava, filter);
    }
    banIp(ip){
        ServerWebGamePostServerJava.banIp(this.idJava, ip);
    }
    unbanIp(ip){
        ServerWebGamePostServerJava.unbanIp(this.idJava, ip);
    }
}
Object.freeze(ServerWebGamePostServer.prototype);