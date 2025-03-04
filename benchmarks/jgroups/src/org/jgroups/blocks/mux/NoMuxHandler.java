package org.jgroups.blocks.mux;

import java.io.Serializable;

/**
 * Returned by {@link MuxUpHandler} when a message is received with a specific {@link MuxHeader}, but no corresponding handler is registered.
 * @author Paul Ferraro
 * @version $Id: NoMuxHandler.java,v 1.1 2010/04/13 17:57:07 ferraro Exp $
 */
public class NoMuxHandler implements Serializable {
    private static final long serialVersionUID = -694135384125080323L;
    
    private short id;
    
    public NoMuxHandler(short id) {
        this.id = id;
    }
    
    public short getId() {
        return id;
    }

    
    public boolean equals(Object object) {
        if ((object == null) || !(object instanceof NoMuxHandler)) return false;
        NoMuxHandler handler = (NoMuxHandler) object;
        return (id == handler.id);
    }

    
    public int hashCode() {
        return id;
    }
}
