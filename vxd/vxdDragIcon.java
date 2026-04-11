package vxd;

import vxd.vxd.BlackToTransparentFilter;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import org.w3c.dom.*;

import java.util.*;
import javax.swing.tree.*;

import vxd.vxd;
import vxd.vxdDropTarget;
import vxd.vxdJButton;
import vxd.vxdAttribute;

public class vxdDragIcon extends JComponent
        implements MouseListener, MouseMotionListener, vxdDropTarget,
        ActionListener {
    public vxdJButton button;
    public Image image;
    public ImageIcon icon;
    public ImageIcon overlayicon;
    public Image overlay = null;
    public Element element;
    public Point dragStart = null;
    public String prevName = "";

    public vxdDragIcon(vxdJButton button, int x, int y, Element e) {
        this.setElement(e);
        this.button = button;
        this.image = button.dragimage;
        Image smallimg = image.getScaledInstance(vxd.xiconsize, vxd.yiconsize, Image.SCALE_SMOOTH);
        this.icon = new ImageIcon(smallimg);
        this.overlayicon = null;
        try {
            MediaTracker mt = new MediaTracker(vxd.frame);
            mt.addImage(smallimg, 0);
            mt.waitForAll();
        } catch (Exception exw) {
            ;
        }
        String ttip = button.name;
        if (e != null) {
            String tttip = e.getAttribute("Notes");
            if (tttip != null && tttip.length() > 0) {
                ttip = tttip;
            }
        }
        setToolTipText((ttip));
        Dimension d = new Dimension(image.getWidth(null) + vxd.iconborder * 2,
                image.getHeight(null) + vxd.iconborder * 2);
        setPreferredSize(d);
        setLocation(x - d.width / 2, y - d.height / 2);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Unescapes a string that contains standard Java escape sequences.
     * <ul>
     * <li><strong>&#92;b &#92;f &#92;n &#92;r &#92;t &#92;" &#92;'</strong> :
     * BS, FF, NL, CR, TAB, double and single quote.</li>
     * <li><strong>&#92;X &#92;XX &#92;XXX</strong> : Octal character
     * specification (0 - 377, 0x00 - 0xFF).</li>
     * <li><strong>&#92;uXXXX</strong> : Hexadecimal based Unicode character.</li>
     * </ul>
     *
     * @param st
     *            A string optionally containing standard java escape sequences.
     * @return The translated string.
     */
    public String unescapeJavaString(String st) {

        StringBuilder sb = new StringBuilder(st.length());

        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == st.length() - 1) ? '\\' : st
                        .charAt(i + 1);
                // Octal escape?
                if (nextChar >= '0' && nextChar <= '7') {
                    String code = "" + nextChar;
                    i++;
                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                            && st.charAt(i + 1) <= '7') {
                        code += st.charAt(i + 1);
                        i++;
                        if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                                && st.charAt(i + 1) <= '7') {
                            code += st.charAt(i + 1);
                            i++;
                        }
                    }
                    sb.append((char) Integer.parseInt(code, 8));
                    continue;
                }
                switch (nextChar) {
                    case '\\':
                        ch = '\\';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\"':
                        ch = '\"';
                        break;
                    case '\'':
                        ch = '\'';
                        break;
                    // Hex Unicode: u????
                    case 'u':
                        if (i >= st.length() - 5) {
                            ch = 'u';
                            break;
                        }
                        int code = Integer.parseInt(
                                "" + st.charAt(i + 2) + st.charAt(i + 3)
                                        + st.charAt(i + 4) + st.charAt(i + 5), 16);
                        sb.append(Character.toChars(code));
                        i += 5;
                        continue;
                }
                i++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element e) {
        this.element = e;
        String name = null;
        if (element != null)
            this.prevName = name = element.getAttribute("Name");
        if (name != null) {
            try {
                if(!(new java.io.File("Work/" + vxd.controller.project.name + "/Images/" + name + ".jpg")).exists()){
                    this.overlay=null;
                    this.overlayicon=null;
                    repaint();
                    return;
                }
                Image img = Toolkit.getDefaultToolkit().
                    getImage("Work/" + vxd.controller.project.name + "/Images/" + name + ".jpg");
                MediaTracker mt = new MediaTracker(vxd.frame);
                mt.addImage(img, 0);
                mt.waitForAll();
                if ((mt.statusAll(false) & MediaTracker.ERRORED) != 0) {
                    this.overlay=null;
                    this.overlayicon=null;
                    repaint();
                    return;
                }
                ImageFilter transparency = new BlackToTransparentFilter();
                ImageProducer producer = new FilteredImageSource(img.getSource(), transparency);
                Image transparentimg = Toolkit.getDefaultToolkit().createImage(producer);
                // Image smallimg = img.getScaledInstance(vxd.xdragsize,vxd.ydragsize, Image.SCALE_SMOOTH);
                Image smallimg = transparentimg.getScaledInstance(vxd.xdragsize, vxd.ydragsize, Image.SCALE_SMOOTH);
                MediaTracker mta = new MediaTracker(vxd.frame);
                mta.addImage(smallimg, 0);
                mta.waitForAll();
                if ((mta.statusAll(false) & MediaTracker.ERRORED) != 0) {
                    this.overlay=null;
                    this.overlayicon=null;
                    repaint();
                    return;
                }
                this.overlay = smallimg;
                Image overlayiconimg = smallimg.getScaledInstance(vxd.xiconsize, vxd.yiconsize, Image.SCALE_SMOOTH);
                MediaTracker mtb = new MediaTracker(vxd.frame);
                mtb.addImage(overlayiconimg, 0);
                mtb.waitForAll();
                if ((mtb.statusAll(false) & MediaTracker.ERRORED) != 0) {
                    this.overlay=null;
                    this.overlayicon=null;
                    repaint();
                    return;
                }
                this.overlayicon = new ImageIcon(overlayiconimg);
                this.repaint();
            } catch (Exception emt) {
                this.overlay=null;
                this.overlayicon=null;
                repaint();
            }
        }
    }


    public void paint(Graphics g) {
        g.drawImage(image, vxd.iconborder, vxd.iconborder, null);
        if (overlay != null)
            g.drawImage(overlay, vxd.iconborder + getWidth() / 4, vxd.iconborder + getWidth() / 4, getWidth() / 2, getWidth() / 2, null);
        String name = null;
        if (element != null)
            name = element.getAttribute("Name");
        if (name != null) {
            g.setColor(getBackground());
            for (int i = -1; i <= 1; ++i)
                for (int j = -1; j <= 1; ++j) {
                    int xx = getWidth() / 2 -
                            g.getFontMetrics().stringWidth(name) / 2 + i;
                    g.drawString(name, xx < 0 ? 0 : xx, getHeight() - 5 + j);
                }
            g.setColor(getForeground());
            int xx = getWidth() / 2 -
                    g.getFontMetrics().stringWidth(name) / 2;
            g.drawString(name, xx < 0 ? 0 : xx,
                    getHeight() - 5);
        }
        if (element != null) {
            String tttip = element.getAttribute("Notes");
            if (tttip != null && tttip.length() > 0) {
                setToolTipText(tttip);
            }
        }
        if(!element.getAttribute("Name").equals(prevName)){
            this.setElement(this.element);
        }
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void mousePressed(MouseEvent e) {
        if (e != null) {
            vxd.controller.DEBUG_STACK_TRACE(e);
            if (e.getButton() == MouseEvent.BUTTON2 ||
                    e.getButton() == MouseEvent.BUTTON3)
                return;
            dragStart = e.getPoint();
        }
        vxd.controller.iconConnectionView.remove(this);
        vxd.controller.iconConnectionView.add(this, 0);
        Element root = (Element) vxd.controller.tree.getModel().getRoot();
        Element node = element;
        Vector v = new Vector();
        int i = 0;
        do {
            v.insertElementAt(node, i);
            node = (Element) node.getParentNode();
            ++i;
        }
        while (node != null && node != root);
        v.insertElementAt(node, i);
        Object[] objs = new Object[i + 1];
        int j = 0;
        for (; i >= 0; --i)
            objs[j++] = v.elementAt(i);
        TreePath path = new TreePath(objs);
        vxd.controller.selectedNode = path;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                vxd.controller.refreshXMLViews();
            }
        });
        vxd.controller.statusText.setText(element.toString());
    }

    public void mouseDragged(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON2 ||
                e.getButton() == MouseEvent.BUTTON3)
            return;
        if (dragStart == null)
            return;
        Point pt = SwingUtilities.convertPoint(this, e.getPoint(), getParent());
        setLocation(pt.x - dragStart.x, pt.y - dragStart.y);
        getParent().repaint();
    }

    public void mouseReleased(MouseEvent e) {
        vxd.controller.DEBUG_STACK_TRACE(e);
        if (((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) || SwingUtilities.isRightMouseButton(e)) {
            mouseReleasedButton2(e);
            return;
        }
        Point pt = SwingUtilities.convertPoint(this, e.getPoint(), getParent());
        setLocation(pt.x - dragStart.x, pt.y - dragStart.y);
        Point lpt = getLocation();
        element.getAttributeNode("XPos").setValue(Integer.toString(lpt.x));
        element.getAttributeNode("YPos").setValue(Integer.toString(lpt.y));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                vxd.controller.refreshXMLViews();
            }
        });
        dragStart = null;
    }

    public void mouseMoved(MouseEvent e) {
        ;
    }

    public void mouseEntered(MouseEvent e) {
        ;
    }

    public void mouseExited(MouseEvent e) {
        ;
    }

    public void mouseClicked(MouseEvent e) {
        ;
    }

    public void mouseReleasedButton2(MouseEvent e) {
        vxd.controller.DEBUG_STACK_TRACE(e);
        if (((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) || SwingUtilities.isRightMouseButton(e)) {
            JPopupMenu popup = new JPopupMenu();
            popup.setLightWeightPopupEnabled(true);
            popup.setBorderPainted(true);
            JMenuItem del = new JMenuItem("Delete");
            del.setActionCommand("DELETE");
            del.addActionListener(this);
            popup.add(del);
            JMenuItem change = new JMenuItem("Change Element");
            change.setActionCommand("CHANGE");
            change.addActionListener(this);
            popup.add(change);
            JMenuItem open = new JMenuItem("Open ShellCommand");
            open.setActionCommand("OPEN");
            open.addActionListener(this);
            popup.add(open);
            JMenuItem externalurl = new JMenuItem("Open External Link URL");
            externalurl.setActionCommand("EXTERNALURL");
            externalurl.addActionListener(this);
            popup.add(externalurl);
            Vector v = vxd.controller.getAttributes(element.getTagName());
            Enumeration en = v.elements();
            while (en.hasMoreElements()) {
                vxdAttribute a = (vxdAttribute) en.nextElement();
                if (a.combo != null &&
                        a.combo.size() == 2 &&
                        a.combo.contains("TRUE") &&
                        a.combo.contains("FALSE")) {
                    JCheckBoxMenuItem cb = new JCheckBoxMenuItem(a.name);
                    cb.setActionCommand(a.name);
                    cb.addActionListener(this);
                    if (element.getAttribute(a.name).equals("TRUE"))
                        cb.setState(true);
                    else
                        cb.setState(false);
                    popup.add(cb);
                }
            }
            popup.show(this, e.getX(), e.getY());
        }
    }

    public void actionPerformed(ActionEvent e) {
        vxd.controller.DEBUG_STACK_TRACE(e);
 String os = System.getProperty("os.name").startsWith("Windows") ? "Windows" : System.getProperty("os.name").startsWith("Mac") ? "Mac" : "Linux";
        String separator = System.getProperty("os.name").startsWith("Windows") ? "\\" : "/";
        String browser = vxd.config.getDocumentElement().getAttribute("browser" + os);
        String editor = vxd.config.getDocumentElement().getAttribute("editor" + os);
        String fileManager = vxd.config.getDocumentElement().getAttribute("fileManager" + os);
        if (e.getActionCommand().equals("CHANGE")) {
            try {
                JDialog change = new JDialog(vxd.frame, "Change Element", true);
                change.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                change.setSize(325, 175);
                vxd.topDialog = change;
                change.setLocationRelativeTo(null);
                JLabel changelabel = new JLabel("Choose Element to Change To");
                changelabel.setHorizontalAlignment(JLabel.CENTER);
                changelabel.setVerticalAlignment(JLabel.CENTER);
                JComboBox selectElement = new JComboBox(){
                    @Override public void addItem(Object obj){
                        int count = getItemCount();
                        String toAdd = (String) obj;

                        java.util.List<String> items = new ArrayList<String>();
                        for(int i = 0; i < count; i++){
                            items.add((String)getItemAt(i));
                        }

                        if(items.size() == 0){
                            super.addItem(toAdd);
                            return;
                        }else{
                            if(toAdd.compareTo(items.get(0)) <= 0){
                                insertItemAt(toAdd, 0);
                            }else{
                                int lastIndexOfHigherNum = 0;
                                for(int i = 0; i < count; i++){
                                    if(toAdd.compareTo(items.get(i)) > 0){
                                        lastIndexOfHigherNum = i;
                                    }
                                }
                                insertItemAt(toAdd, lastIndexOfHigherNum+1);
                            }
                        }
                    }
                };
                Element root = vxd.project.languageElements.getDocumentElement();
                NodeList icons = root.getElementsByTagName("Icon");
                for (int i = 0; i < icons.getLength(); ++i) {
                    Element node = (Element) icons.item(i);
                    String name = node.getAttribute("Name");
                    selectElement.addItem(name);
                }
                vxd.changeElementField = selectElement;
                JPanel changepanel = new JPanel();
                changepanel.setLayout(new GridLayout(2, 1));
                changepanel.add(changelabel);
                changepanel.add(vxd.changeElementField);
                JPanel btnpanel = new JPanel();
                btnpanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                btnpanel.setLayout(new FlowLayout());
                JButton okbtn = new JButton("OK");
                okbtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(e.getActionCommand().equals("OK")) {
                            try {
                                Node parentNode = vxd.changeElementElement.getParentNode();
                                Element replaceElement = vxd.changeElementElement.getOwnerDocument().createElement(vxd.changeElementField.getSelectedItem().toString());
                                parentNode.appendChild(replaceElement);
                                vxd.changeElementReplacement = replaceElement;
                                vxd.controller.copyAttributes(vxd.changeElementElement, replaceElement);
                                NodeList childrenNodes = vxd.changeElementElement.getChildNodes();
                                while (childrenNodes.getLength() > 0) {
                                    Node child = childrenNodes.item(0);
                                    replaceElement.appendChild(child);
                                }
                                vxd.changeElementElement.getParentNode().removeChild(vxd.changeElementElement);
                                parentNode.appendChild(replaceElement);
                                Element root = vxd.project.languageElements.getDocumentElement();
                                NodeList images = root.getElementsByTagName("Icon");
                                for (int i = 0; i < images.getLength(); ++i) {
                                    Element node = (Element) images.item(i);
                                    if (vxd.changeElementField.getSelectedItem().toString().equals(node.getAttribute("Name"))) {
                                        Image img = Toolkit.getDefaultToolkit().getImage(node.getAttribute("Image"));
                                        ImageFilter transparency = new vxd.BlackToTransparentFilter();
                                        ImageProducer producer = new FilteredImageSource(img.getSource(), transparency);
                                        Image transparentimg = Toolkit.getDefaultToolkit().createImage(producer);
                                        Image dragimage = transparentimg.getScaledInstance(vxd.xdragsize,
                                                vxd.ydragsize, Image.SCALE_SMOOTH);
                                        try {
                                            MediaTracker mt = new MediaTracker(vxd.frame);
                                            mt.addImage(dragimage, 0);
                                            mt.waitForAll();
                                            vxd.changeElementDragIcon.element = replaceElement;
                                            vxd.changeElementDragIcon.image = dragimage;
                                            vxd.changeElementDragIcon.repaint();
                                            vxd.controller.selectedNode = new TreePath(replaceElement);
                                            SwingUtilities.invokeLater(new Runnable() {
                                                public void run() {
                                                    vxd.topDialog.setVisible(false);
                                                    vxd.topDialog.dispose();
                                                    vxd.topDialog = null;
                                                }
                                            });
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
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                            } catch (Exception aoe) {
                                aoe.printStackTrace();
                            }
                        }
                    }
                });
                JButton cancelbtn = new JButton("Cancel");
                cancelbtn.addActionListener(new

                                                    ActionListener() {
                                                        public void actionPerformed(ActionEvent e) {
                                                            vxd.topDialog.setVisible(false);
                                                            vxd.topDialog.dispose();
                                                            vxd.topDialog = null;
                                                        }
                                                    });
                change.addWindowListener(new

                                                 WindowAdapter() {
                                                     public void windowClosing(WindowEvent e) {
                                                         vxd.topDialog.setVisible(false);
                                                         vxd.topDialog.dispose();
                                                         vxd.topDialog = null;
                                                     }
                                                 });
                change.getContentPane().

                        setLayout(new GridLayout(4, 1));
                change.getContentPane().

                        add(changepanel);
                change.getContentPane().

                        add(new JPanel());
                btnpanel.add(okbtn);
                btnpanel.add(cancelbtn);
                change.getContentPane().

                        add(btnpanel);

                vxd.changeElementElement = element;
                vxd.changeElementDragIcon = this;
                change.setVisible(true);
            } catch (Exception oe) {
                oe.printStackTrace();
            }
        } else if (e.getActionCommand().

                equals("OPEN")) {
            try {
                java.lang.Runtime.getRuntime().exec(element.getAttributeNode("ShellCommand").getNodeValue().
                        replace("\\", separator).
                        replace("\\", separator).
                        replace("\\", separator).
                        replace("\\", separator).
                        replace("\\", separator).
                        replace("[LANGUAGE]", vxd.project.language).
                        replace("[LANGUAGE]", vxd.project.language).
                        replace("[TRANSLATOR]", vxd.project.translator).
                        replace("[TRANSLATOR]", vxd.project.translator).
                        replace("[PROJECT]", vxd.project.name).
                        replace("[PROJECT]", vxd.project.name).
                        replace("[BROWSER]", browser).
                        replace("[EDITOR]", editor).
                        replace("[FILEMANAGER]", fileManager));
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(vxd.frame, "Error: " + exc.getMessage());
                exc.printStackTrace();
            }
        } else if (e.getActionCommand().

                equals("EXTERNALURL")) {
            try {
                java.lang.Runtime.getRuntime().exec(vxd.config.getDocumentElement()
                        .getAttribute("browser" + os) + " " + element.getAttributeNode("ExternalLinkURL").getNodeValue().
                        replace("\\", separator).
                        replace("\\", separator).
                        replace("[LANGUAGE]", vxd.project.language).
                        replace("[LANGUAGE]", vxd.project.language).
                        replace("[TRANSLATOR]", vxd.project.translator).
                        replace("[TRANSLATOR]", vxd.project.translator).
                        replace("[PROJECT]", vxd.project.name).
                        replace("[PROJECT]", vxd.project.name).
                        replace("[BROWSER]", browser).
                        replace("[EDITOR]", editor).
                        replace("[FILEMANAGER]", fileManager));
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(vxd.frame, "Error: " + exc.getMessage());
                exc.printStackTrace();
            }
        } else if (e.getActionCommand().

                equals("DELETE")) {
            element.getParentNode().removeChild(element);
            vxd.controller.iconConnectionView.remove(this);
            vxd.controller.selectedNode = new TreePath(vxd.controller.project.
                    programXML.getDocumentElement());
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
        } else if (e.getActionCommand().

                equals("SELECTCONNECTED")) {
            ;
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
