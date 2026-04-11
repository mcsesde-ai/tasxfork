package vxd;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.xml.parsers.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import org.apache.crimson.tree.*;
import org.apache.crimson.jaxp.*;
import org.apache.crimson.parser.*;
import org.apache.crimson.util.*;

import vxd.*;

public class vxdOpenFileActionListener implements ActionListener {
    protected File selectedFile=null;
    protected DocumentBuilder builder=null;
    
    public vxdOpenFileActionListener(File selectedFile,DocumentBuilder builder){
	this.selectedFile=selectedFile;
	this.builder=builder;
    }
    
    public void actionPerformed(ActionEvent e) {
	boolean loadUnencrypted = true;
	if (vxd.passwdtextField.getText() == null
	    || vxd.passwdtextField.getText().length() == 0) {
	    JOptionPane.showMessageDialog(vxd.frame,
					  "Loading Unencrypted");
	} else {
	    loadUnencrypted = false;
	}
	String passwdString= vxd.passwdtextField.getText();
	if( vxd.mainPassword==""){
	    vxd.mainPassword=passwdString;
	}
	vxd.topDialog.setVisible(false);
	vxd.topDialog.dispose();
	vxd.topDialog = null;
	try {
	    Document tconfig = null;
	    File file;
	    if (vxd.mainLoadFile == null) {
		file = this.selectedFile;
	    } else {
		file = new File(vxd.mainLoadFile);
	    }
	    int filelength=(int)file.length();
	    FileInputStream rdr = new FileInputStream(file);
	    DataInputStream dta = new DataInputStream(rdr);
	    byte[] filedata = new byte[(int) filelength];
	    dta.readFully(filedata);
	    String serverxml = new String(filedata);
	    rdr.close();
	    dta.close();
	    if (!loadUnencrypted) {
		int iter = 0;
		int accum = 0;
		for (int di = 0; di < filelength; ++di) {
		    accum += (int) passwdString.charAt(iter++ % passwdString.length());
		    filedata[di] ^= (int) passwdString.charAt(iter % passwdString.length());
		    filedata[di] -= accum;
		    ++iter;
		}
	    }
	    File fdel = new File(file.getAbsolutePath() + ".decrypted");
	    if (fdel.exists()) {
		fdel.delete();
	    }
	    FileOutputStream fcrypt = new FileOutputStream(file.getAbsolutePath() + ".decrypted");
	    ByteArrayOutputStream outcrypt = new ByteArrayOutputStream();
	    outcrypt.write(filedata, 0, (int) filelength);
	    outcrypt.writeTo(fcrypt);
	    outcrypt.flush();
	    outcrypt.close();
	    BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));   
	    String str;   
	    if(!vxd.config.getDocumentElement().getAttribute("debugdecryption")
	       .equals("false")){
		System.out.println("Decrypted Press Enter...");
		str = obj.readLine();
	    }else{
		System.out.println("Decrypted");
	    }
	    File f = new File(file.getAbsolutePath() + ".decrypted");
	    file = f;
	    Document loadeddoc;
	try {
	   	loadeddoc = (Document) this.builder.parse(f);
	} catch(org.xml.sax.SAXParseException spe){
		JOptionPane.showMessageDialog(vxd.frame,"Password Incorrect or Corrupt File.");
		return;
	}
	    Document xdoc = new XmlDocument();
	    Element root = loadeddoc.getDocumentElement();
	    Document programDoc = xdoc;
	    Element newroot = xdoc.createElement(root.getTagName());
	    for (int xi = 0; xi < root.getAttributes().getLength(); ++xi) {
		String attrn = root.getAttributes().item(xi).getNodeName();
		newroot.setAttribute(attrn, root.getAttribute(attrn));
	    }
	    programDoc.appendChild(newroot);
	    root = newroot;
	    String name = root.getAttribute("Title");
	    String lang = root.getAttribute("Language");
	    String deploy = root.getAttribute("Platform");
	    String trans = root.getAttribute("Translator");

	    for (int i = 0; i < vxd.translators.getLength(); ++i) {
		Element node = (Element) vxd.translators.item(i);
		String txt = node.getAttribute("name");
		if (txt.equals(trans)) {
		    try {
			String conf = node
			    .getAttribute("configfile");


			tconfig = this.builder
			    .parse(new File(conf));

			Element root1 = tconfig
			    .getDocumentElement();
			Class cls = Class
			    .forName(((Element) (root1
						 .getElementsByTagName("eventhandler")
						 .item(0)))
				     .getAttribute("class"));
			ActionListener listener = (ActionListener) cls
			    .newInstance();
			NodeList menus = root1
			    .getElementsByTagName("menu");
			vxd.addMenus(vxd.menuBar, menus, listener);
			NodeList images = root1
			    .getElementsByTagName("toolbarbutton");
			vxd.addToolButtons(vxd.toolBar, images,
				       listener);
			trans = node.getAttribute("translator");
		    } catch (Exception exx) {
			exx.printStackTrace();
		    }
		}
	    }

	    for (int i = 0; i < vxd.platforms.getLength(); ++i) {
		Element node = (Element) vxd.platforms.item(i);
		String txt = node.getAttribute("platform");
		if (txt.equals(deploy)) {
		    try {
			String conf = node
			    .getAttribute("configfile");
			// TODO: this recreation of a factory and builder inside a loop looks wasteful, fix later
			DocumentBuilderFactory factory = DocumentBuilderFactory
			    .newInstance();

			builder = factory
			    .newDocumentBuilder();

			tconfig = builder
			    .parse(new File(conf));

			Element root2 = tconfig
			    .getDocumentElement();
			Class cls = Class
			    .forName(((Element) (root2
						 .getElementsByTagName("eventhandler")
						 .item(0)))
				     .getAttribute("class"));
			ActionListener listener = (ActionListener) cls
			    .newInstance();
			NodeList menus = root2
			    .getElementsByTagName("menu");
			vxd.addMenus(vxd.menuBar, menus, listener);
			NodeList images = root2
			    .getElementsByTagName("toolbarbutton");
			vxd.addToolButtons(vxd.toolBar, images,
				       listener);
			deploy = node.getAttribute("platform");
		    } catch (Exception exx) {
			exx.printStackTrace();
		    }
		}
	    }
	    vxd.project = new vxdproject(name,
					 lang, trans, deploy);
	    vxd.controller = new vxdcontroller(vxd.project, false, root, loadeddoc);

	} catch (Exception okex) {
	    okex.printStackTrace();
	}
    }
}
