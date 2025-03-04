/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 * if any, must include the following acknowledgment:
 * "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself,
 * if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 * "Apache JMeter" must not be used to endorse or promote products
 * derived from this software without prior written permission. For
 * written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 * "Apache JMeter", nor may "Apache" appear in their name, without
 * prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
 
package org.apache.jorphan.gui;

/**
 * Title:        Apache JMeter
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      Apache Foundation
 * @author bo.regnlin@pc.nu (Bo Regnlin)
 * @version 1.0
 */

import java.awt.Component;
import java.awt.Dimension;

/**
 * This class is a Util for awt Component and could be
 * used to place them in center of an other.
 *
 *
 * @author  <a href="mailto:bo.regnlin@pc.nu">Bo Regnlin</a>
* @version 1.0
 */
public class ComponentUtil
{
  /**
		 * Use this static method if you want to center
	  * and set its proposion compared to the size of
	  * the current users screen size.
	  * Valid percent is between +-(0-100) minus is treated
	  * as plus, bigger than 100 is always set to 100.
		 *
		 *@param  component The component you want to center and set
size on.
		 *@param  percentOfScreen The percent of the current screensize
you want
	  *        the component to be.
		 */
  public static void centerComponentInWindow(Component component, int
percentOfScreen)
  {
	 if(percentOfScreen < 0)
	 {
		centerComponentInWindow(component, - percentOfScreen);
		return;
	 }
	 if(percentOfScreen > 100)
	 {
		centerComponentInWindow(component, 100);
		return;
	 }
	 double percent = percentOfScreen / 100.d;
	 Dimension dimension = component.getToolkit().getScreenSize();
	 component.setSize((int)(dimension.getWidth()*percent),
							 (int)(dimension.getHeight()*percent));
	 centerComponentInWindow(component);
  }

  /**
		 * Use this static method if you want to center a component in
Window.
		 *
		 *@param  component The component you want to center in window.
		 */
  public static void centerComponentInWindow(Component component)
  {
	 Dimension dimension = component.getToolkit().getScreenSize();

	 component.setLocation((int)((dimension.getWidth()-component.getWidth())/2),
								  (int)((dimension.getHeight()-component.getHeight
())/2));
	 component.validate();
	 component.repaint();
  }

  /**
		 * Use this static method if you want to center
	  * a component over an other component.
		 *
		 *@param  parent The component you want to use to place it on.
		 *@param  toBeCentered The component you want to center.
		 */
  public static void centerComponentInComponent(Component parent, Component
toBeCentered)
  {
	 toBeCentered.setLocation((int)parent.getX() + (int)((parent.getWidth()-
toBeCentered.getWidth())/2),
									 (int)parent.getY() + (int)((parent.getHeight()-
toBeCentered.getHeight())/2));

	 toBeCentered.validate();
	 toBeCentered.repaint();
  }

}
