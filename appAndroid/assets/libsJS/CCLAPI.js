window.CCLAPI = class CCLAPI{
    static #stopEventsCallBacks = [];
    static #startEventsCallBacks = [];
    constructor() {
        if (new.target !== CCLAPI) {
          throw new Error("Esta clase no puede ser extendida.");
        }
    }
    static dispatchEvent(event){
        switch(event.type.toLowerCase()){
            case "stop":
                for(let callb of this.#stopEventsCallBacks){
                    callb(event);
                }
                break;
            case "start":
                for(let callb of this.#startEventsCallBacks){
                    callb(event);
                }
                break;
            default:
                throw new Error("Error al Lanzar el Evento " + event.type+" Evento no compatible!");
                break;
        }
    }
    static addEventListener(event, callb){
        switch(event.toLowerCase()){
            case "stop":
                this.#stopEventsCallBacks.push(callb);
                break;
            case "start":
                this.#startEventsCallBacks.push(callb);
                break;
            default:
                console.warn("Error al Registrar el evento "+event+", Evento no compatible!");
                break;
        }
    }
    static exit(){
        Android.exit();
    }
};