package vxd;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.w3c.dom.*;

public class vxdPropertyChangeEventListener
        implements DocumentListener, ItemListener {
    public Element element;
    public String attribute;
    public JComponent component;

    public vxdPropertyChangeEventListener(Element e, String a,
                                          JComponent c) {
        this.element = e;
        this.attribute = a;
        this.component = c;
    }

    public void itemStateChanged(ItemEvent e) {
        element.setAttribute(attribute, (String) e.getItem());
        if (attribute.equals("Translator")) {
            vxd.controller.project.translator = (String) e.getItem();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    vxd.controller.initXSL();
                }
            });
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                vxd.controller.refreshXMLViews();
            }
        });
    }

    public void insertUpdate(DocumentEvent e) {
        changedUpdate(e);
    }

    public void removeUpdate(DocumentEvent e) {
        changedUpdate(e);
    }

    public void changedUpdate(DocumentEvent e) {
        try {
            element.
                    setAttribute(attribute, e.getDocument().getText(0, e.getDocument().getLength()));
            vxd.controller.selectedNode = null;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    vxd.controller.refreshXMLViews();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void caretPositionChanged(InputMethodEvent e) {
    }
}
