/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ninj0x
 */
public class RMIUtils {
    
    /**
     * This method takes a host name. It then sets the security policy 
     * and connects to a computer. Returned is a reference to the SpaceAPI
     * it connected to.
     * 
     * @param host
     * @return SpaceAPI or null
     */
    public static SpaceAPI connectToSpace(String host) {
        System.setSecurityManager(new RMISecurityManager());

        String serverDomainName = (host == null) ? "localhost" : host;
        String url = "//" + serverDomainName + "/" + SpaceAPI.SERVICE_NAME;

        // Can throw exceptions - see API for java.rmi.Naming.lookup
        SpaceAPI space;
        try {
            space = (SpaceAPI) Naming.lookup(url);
            return space;
        } catch (NotBoundException ex) {
            Logger.getLogger(RMIUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(RMIUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(RMIUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(1);
        return null;
    }
}
