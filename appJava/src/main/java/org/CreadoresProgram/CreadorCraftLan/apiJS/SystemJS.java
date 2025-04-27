package org.CreadoresProgram.CreadorCraftLan.apiJS;
import org.graalvm.polyglot.HostAccess;
public class SystemJS{
    @HostAccess.Export
    public void exit(){
        System.exit(0);
    }
}