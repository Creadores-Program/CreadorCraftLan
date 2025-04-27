(function (globalScope) {
    if (typeof globalScope.CustomEvent === "function") {
        return;
    }

    /**
     * Constructor para crear objetos que simulan un CustomEvent.
     * @param {string} type El nombre/tipo del evento.
     * @param {object} [params] Opciones para el evento.
     * @param {boolean} [params.bubbles=false] Si el evento debería "burbujear" (conceptual).
     * @param {boolean} [params.cancelable=false] Si el evento puede ser "cancelado" (conceptual).
     * @param {*} [params.detail=null] Datos adicionales asociados al evento.
     */
    function CustomEvent(type, params) {

        params = params || {};
        this.type = String(type);
        this.bubbles = Boolean(params.bubbles);
        this.cancelable = Boolean(params.cancelable);
        this.detail = params.detail !== undefined ? params.detail : null;
        this.timeStamp = Date.now();
        this.isTrusted = false;
        this.defaultPrevented = false;
        this.cancelBubble = this.bubbles;
        this.preventDefault = function() {
            if (this.cancelable) {
                this.defaultPrevented = true;
            }
        };
        this.stopPropagation = function() {
            this.cancelBubble = true;
        };
        this.stopImmediatePropagation = function() {
             this.stopPropagation(); 
        };
    }
    globalScope.CustomEvent = CustomEvent;

})(this);