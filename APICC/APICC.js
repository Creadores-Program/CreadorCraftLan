class APICreadorCraftLan{
    static urlPrefix = "OpenCreadorCraftLan://";
    carpeta = "";
    jsonToServer = [];
    constructor(carpeta){
        this.carpeta = carpeta;
    }
    async start(){
        await GameProps.getFileGame().folder(this.carpeta).forEach(async function(relativePath, file){
            if(file.dir){
                return;
            }
            if(relativePath.endsWith(".json") || relativePath.endsWith(".js") || relativePath.endsWith(".txt") || relativePath.endsWith(".xml") || relativePath.endsWith(".html") || relativePath.endsWith(".css") || relativePath.endsWith(".md") || relativePath.endsWith(".properties") || relativePath.endsWith(".yml") || relativePath.endsWith(".yaml") || relativePath.endsWith(".log")){
                this.jsonToServer.push({
                    path: relativePath,
                    content: await file.async("string")
                });
            }else{
                this.jsonToServer.push({
                    path: relativePath,
                    content: await file.async("base64")
                });
            }
        }.bind(this));
        window.open(APICreadoCraftLan.urlPrefix + btoa(JSON.stringify(this.jsonToServer)), "_blank");
    }
}
