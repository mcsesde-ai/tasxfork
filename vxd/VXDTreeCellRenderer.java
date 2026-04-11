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

public class VXDTreeCellRenderer extends DefaultTreeCellRenderer {
    public VXDTreeCellRenderer() {
	super();
    }
    public Component getTreeCellRendererComponent(JTree tree, Object value,
						  boolean sel, boolean expanded, boolean leaf, int row,
						  boolean hasFocus) {
    	if(!vxd.controller.completelyLoaded)
			return this;
	Component comp=super.getTreeCellRendererComponent(tree,value,sel,expanded,leaf,row,hasFocus);
	Element destelement=null;
	try{
	    DefaultTreeCellRenderer rcomp=(DefaultTreeCellRenderer)this;
	    rcomp.setText(((Element)value).getAttribute("Name"));
	    Element element = (Element) value;
	    ActionListener selectedIcon=null;
	    boolean iconSet=false;
	    try {
		if (element != null) {
		    selectedIcon=(ActionListener)vxd.controller.iconConnectionView.getDragIconByID(element.getAttribute("ID"));
		    if (selectedIcon == null) {
			selectedIcon=(ActionListener)vxd.controller.iconConnectionView.getIconConnectorByID(element.getAttribute("ID"));
		    }
		    if(selectedIcon!=null){
			ImageIcon icon=null;
			ImageIcon destImageIcon=null;
			if(selectedIcon instanceof vxdIconConnector){
			    vxdDragIcon dragicon=((vxdIconConnector)selectedIcon).iconb;
			    
			    ActionListener destIcon=(ActionListener)((vxdIconConnector)selectedIcon).iconb;
			    icon=dragicon.overlayicon==null?dragicon.icon:dragicon.overlayicon;
			    if(destIcon!=null && destIcon instanceof vxdDragIcon){
				destImageIcon=((vxdDragIcon)destIcon).overlayicon==null?((vxdDragIcon)destIcon).icon:((vxdDragIcon)destIcon).overlayicon;
				destelement=((vxdDragIcon)destIcon).element;
				rcomp.setText(((Element)value).getAttribute("Name")+" : "+((Element)((vxdDragIcon)destIcon).element).getAttribute("Name"));
			    }else{
								rcomp.setText(((Element)value).getAttribute("Name"));
			    }
			    if(destImageIcon!=null){
				rcomp.setIcon(destImageIcon);
				iconSet=true;
			    }else if(icon!=null){
				rcomp.setIcon(icon);
				iconSet=true;
			    }
			}else if(selectedIcon instanceof vxdDragIcon){
			    vxdDragIcon dragicon=((vxdDragIcon)selectedIcon);
			    icon=dragicon.overlayicon==null?dragicon.icon:dragicon.overlayicon;
			    if(icon!=null){
				rcomp.setIcon(icon);
				iconSet=true;
			    }
			}
		    }
		}
	    }catch(Exception iiex){;}
	    if(!iconSet){
		rcomp.setIcon(vxd.controller.GetIconButton(((Element)value).getTagName()).icon);
	    }
	}catch(Exception rex){}
	if(sel){
	    String desttext=destelement==null?"":destelement.toString();
	    vxd.controller.statusText.setText(value.toString()+" : "+desttext);
	}
	return this;
    }    
}
