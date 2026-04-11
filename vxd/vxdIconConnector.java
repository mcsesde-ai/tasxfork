package vxd;

import org.w3c.dom.*;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.*;
import java.util.Vector;

import vxd.vxd;
import vxd.vxdDragIcon;
public class vxdIconConnector implements ActionListener {
    public static final int CONNECTION = 1;
    public static final int AGGREGATION = 2;
    public static final int ARROWSIZE = 7;

    public vxdDragIcon icona;
    public vxdDragIcon iconb;
    public int type;
    public boolean visible;
    public Element element;

    public vxdIconConnector(vxdDragIcon a, vxdDragIcon b, int type) {
        icona = a;
        iconb = b;
        this.type = type;
        visible = true;
        element = null;
    }

    public vxdIconConnector(vxdDragIcon a, vxdDragIcon b, int type, Element e) {
        this(a, b, type);
        element = e;
    }

    public void setVisible(boolean v) {
        visible = v;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setElement(Element e) {
        element = e;
    }


    public void actionPerformed(ActionEvent e) {
        vxd.controller.DEBUG_STACK_TRACE(e);
        if (e.getActionCommand().equals("OPEN")) {
            try {
                String os = System.getProperty("os.name").startsWith("Windows") ? "Windows" : System.getProperty("os.name").startsWith("Mac") ? "Mac" : "Linux";

		String separator=System.getProperty("os.name").startsWith("Windows")?"\\":"/";
		String browser=vxd.config.getDocumentElement().getAttribute("browser"+os);
		String editor=vxd.config.getDocumentElement().getAttribute("editor"+os);
		String fileManager=vxd.config.getDocumentElement().getAttribute("fileManager"+os);
                java.lang.Runtime.getRuntime().exec(element.getAttributeNode("ShellCommand").getNodeValue().
							replace("\\",separator).							replace("\\",separator).							replace("\\",separator).							replace("\\",separator).
							replace("\\",separator).
							replace("[LANGUAGE]", vxd.project.language).
							replace("[LANGUAGE]", vxd.project.language).
							replace("[TRANSLATOR]", vxd.project.translator).
							replace("[TRANSLATOR]", vxd.project.translator).
							replace("[PROJECT]",vxd.project.name).
							replace("[PROJECT]",vxd.project.name).
							replace("[BROWSER]",browser).
							replace("[EDITOR]",editor).
							replace("[FILEMANAGER]",fileManager));

            } catch (Exception exc) {
                JOptionPane.showMessageDialog(vxd.frame, "Error: " + exc.getMessage());
                exc.printStackTrace();
            }
        } else if (e.getActionCommand().equals("EXTERNALURL")) {
            try {
		String os=System.getProperty("os.name").startsWith("Windows")?"Windows":"Linux";
                java.lang.Runtime.getRuntime().exec(vxd.config.getDocumentElement()
						    .getAttribute("browser" + os) + " " + element.getAttributeNode("ExternalLinkURL").getNodeValue());
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(vxd.frame, "Error: " + exc.getMessage());
                exc.printStackTrace();
            }
        } else if(e.getActionCommand().equals("SELECTCONNECTED")) {
	    String destID=element.getAttribute("DestID");
	    if(destID!=null){
		    vxdDragIcon selectedDragIcon = vxd.controller.iconConnectionView.getDragIconByID(destID);
            selectedDragIcon.mousePressed(null);;
	    }
	} else if (e.getActionCommand().equals("DELETE")) {
            element.getParentNode().removeChild(element);
            vxd.controller.iconConnectionView.connectors.remove(this);
            vxd.controller.selectedNode = new TreePath(vxd.controller.project.programXML.getDocumentElement());
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    vxd.controller.iconConnectionView.validateIconsAndConnectors();
                }
            });
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    vxd.controller.refreshXMLViews();
                }
            });
        } else {
            if (element.getAttribute(e.getActionCommand()).equals("TRUE"))
                element.getAttributeNode(e.getActionCommand()).
                        setValue("FALSE");
            else
                element.getAttributeNode(e.getActionCommand()).
                        setValue("TRUE");
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    vxd.controller.refreshXMLViews();
                }
            });
        }
    }
}
