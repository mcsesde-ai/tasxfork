
package vxd;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class JVXDTree extends JTree {
    public JVXDTree(){
	super();
    }
    @Override
    public String getToolTipText( MouseEvent e ) {
	try {
	    TreePath path = this.getPathForLocation(e.getX(), e.getY());
	    Element element = (Element) path.getLastPathComponent();
	    return element.getAttribute("Notes");
	}catch(Exception ex){ ; }
	return "";
    }
}
