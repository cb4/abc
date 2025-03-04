/*
 * @(#)DrawingEditorProxy.java
 *
 * Copyright (c) 2007-2010 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package org.jhotdraw.draw;

import org.jhotdraw.draw.tool.Tool;
import java.awt.Container;
import java.awt.Cursor;
import java.beans.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import org.jhotdraw.beans.AbstractBean;

/**
 * DrawingEditorProxy.
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Proxy</em><br>
 * To remove the need for null-handling, {@code AbstractTool} makes use of
 * a proxy for {@code DrawingEditor}.
 * Subject: {@link DrawingEditor}; Proxy: {@link DrawingEditorProxy};
 * Client: {@link org.jhotdraw.draw.tool.AbstractTool}.
 * <hr>
 *
 *
 * @author Werner Randelshofer
 * @version $Id: DrawingEditorProxy.java 647 2010-01-24 22:52:59Z rawcoder $
 */
public class DrawingEditorProxy extends AbstractBean implements DrawingEditor {
    private DrawingEditor target;

    private class Forwarder implements PropertyChangeListener, Serializable {
    
          public void propertyChange(PropertyChangeEvent evt) {
              firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
          }
    }
    private Forwarder forwarder;
    
    /** Creates a new instance. */
    public DrawingEditorProxy() {
        forwarder = new Forwarder();
    }
    
    /**
     * Sets the target of the proxy.
     */
    public void setTarget(DrawingEditor newValue) {
        if (target != null) {
            target.removePropertyChangeListener(forwarder);
        }
        this.target = newValue;
        if (target != null) {
            target.addPropertyChangeListener(forwarder);
        }
    }
    /**
     * Gets the target of the proxy.
     */
    public DrawingEditor getTarget() {
        return target;
    }
    
    
    
    public void add(DrawingView view) {
        target.add(view);
    }
    
    
    public void remove(DrawingView view) {
        target.remove(view);
    }
    
    
    public Collection<DrawingView> getDrawingViews() {
        return target.getDrawingViews();
    }
    
    
    public DrawingView getActiveView() {
        return (target == null) ? null : target.getActiveView();
    }
    
    
    public void setActiveView(DrawingView newValue) {
        target.setActiveView(newValue);
    }
    
    public DrawingView getFocusedView() {
        return (target == null) ? null : target.getActiveView();
    }
    
    
    public void setTool(Tool t) {
        target.setTool(t);
    }
    
    
    public Tool getTool() {
        return target.getTool();
    }
    
    
    public void setCursor(Cursor c) {
        target.setCursor(c);
    }
    
    
    public DrawingView findView(Container c) {
        return target.findView(c);
    }
    
    
    public <T> void setDefaultAttribute(AttributeKey<T> key, T value) {
        target.setDefaultAttribute(key, value);
    }
    
    
    public <T> T getDefaultAttribute(AttributeKey<T> key) {
        return target.getDefaultAttribute(key);
    }
    
    
    public void applyDefaultAttributesTo(Figure f) {
        target.applyDefaultAttributesTo(f);
    }
    
    
    public Map<AttributeKey, Object> getDefaultAttributes() {
        return target.getDefaultAttributes();
    }
    
    
    public void setEnabled(boolean newValue) {
        target.setEnabled(newValue);
    }
    
    
    public boolean isEnabled() {
        return target.isEnabled();
    }

    
    public <T> void setHandleAttribute(AttributeKey<T> key, T value) {
        target.setHandleAttribute(key, value);
    }

    
    public <T> T getHandleAttribute(AttributeKey<T> key) {
        return target.getHandleAttribute(key);
    }

    
    public void setInputMap(InputMap newValue) {
        target.setInputMap(newValue);
    }

    
    public InputMap getInputMap() {
        return target.getInputMap();
    }

    
    public void setActionMap(ActionMap newValue) {
        target.setActionMap(newValue);
    }

    
    public ActionMap getActionMap() {
        return target.getActionMap();
    }
}
