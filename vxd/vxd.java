package vxd;

import javax.swing.*;
import javax.swing.event.*;
import java.util.regex.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.xml.parsers.*;
import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.io.*;
import javax.swing.tree.*;
import java.util.regex.*;

import org.apache.crimson.tree.*;
import org.apache.crimson.jaxp.*;
import org.apache.crimson.parser.*;
import org.apache.crimson.util.*;

import vxd.vxdcontroller;
import vxd.vxdproject;

public class vxd {
    public static final boolean DEBUG = false;
    public static final boolean DEBUG2 = false;
    public static final String XMLCONFIGFILE = "vxd.xml";
    public static final String XMLCONFIGDIR = "Config/";
    public static String SAVEFILEDIR = null;
    public static String DEPLOYMENTDIR = null;
    public static Document config;
    public static String title;
    public static String connectedIconsViewPNGBASE64HTMLDATAURL = "";
    public static int XSIZE;
    public static int YSIZE;
    public static int CANVASWIDTH;
    public static int CANVASHEIGHT;
    public static int LEFT_PANE_WIDTH;
    public static int RIGHT_PANE_WIDTH;
    public static int DEFAULT_CONTROL_WIDTH;
    public static int iconsperrow;
    public static int xiconsize;
    public static int yiconsize;
    public static int iconborder;
    public static int xdragsize;
    public static int ydragsize;
    public static JFrame frame;
    public static JPanel toolBarPanel;
    public static JComponent viewPanel;
    public static JDialog topDialog;
    public static JTextField textField;
    public static JTextField passwdtextField;
    public static String mainPassword = "";
    public static JComboBox changeElementField;
    public static JComboBox languageCombo;
    public static JComboBox translatorCombo;
    public static JComboBox deployerCombo;
    public static JMenuBar menuBar;
    public static JToolBar toolBar;
    public static NodeList languages;
    public static NodeList translators;
    public static NodeList platforms;
    public static vxdproject project;
    public static vxdcontroller controller;
    public static ActionListener mainMenuOpenListener;
    public static JMenuItem mainMenuItemOpen;
    public static String mainLoadFile = null;
    public static Element changeElementElement = null;
    public static vxdDragIcon changeElementDragIcon = null;
    public static Element changeElementReplacement = null;

    public static void main(String[] args) {
	SwingUtilities.invokeLater(() -> {
		try {
		    //		    UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());
		    UIManager.put("ToolTipUI", MultiLineToolTipUI.class.getName());
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    });
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            config = builder.parse(new File(XMLCONFIGDIR + XMLCONFIGFILE));
            if (args.length > 0) {
                vxd.mainLoadFile = args[0];

                System.out.println("File Argument:" + args[0]);
            }
            Element root = config.getDocumentElement();
            title = root.getAttribute("title");
	    vxd.CANVASWIDTH = Integer.parseInt(root.getAttribute("canvaswidth"));
	    vxd.CANVASHEIGHT = Integer.parseInt(root.getAttribute("canvasheight"));

	    LEFT_PANE_WIDTH = Integer.parseInt(root.getAttribute("leftpanewidth"));
	    RIGHT_PANE_WIDTH = Integer.parseInt(root.getAttribute("rightpanewidth"));
	    DEFAULT_CONTROL_WIDTH = Integer.parseInt(root.getAttribute("defaultpropertywidth"));
            iconsperrow = Integer.parseInt(root.getAttribute("iconsperrow"));
            xiconsize = Integer.parseInt(root.getAttribute("xiconsize"));
            yiconsize = Integer.parseInt(root.getAttribute("yiconsize"));
            iconborder = Integer.parseInt(root.getAttribute("iconborder"));
            xdragsize = Integer.parseInt(root.getAttribute("xdragsize"));
            ydragsize = Integer.parseInt(root.getAttribute("ydragsize"));
            vxd.SAVEFILEDIR = root.getAttribute("savefiledir");
            vxd.DEPLOYMENTDIR = root.getAttribute("deploymentdir");
            vxd.languages = root.getElementsByTagName("language");
            vxd.translators = root.getElementsByTagName("translator");
            vxd.platforms = root.getElementsByTagName("platform");

	    //            MultiLineToolTipUI.installUI();
	    
            menuBar = new JMenuBar();
            MenuItemActionListener listener = new MenuItemActionListener();
            NodeList menus = root.getElementsByTagName("menu");
            addMenus(menuBar, menus, listener);

            toolBar = new JToolBar();
            NodeList images = root.getElementsByTagName("toolbarbutton");
            addToolButtons(toolBar, images, listener);
            vxd.toolBarPanel = new JPanel(new BorderLayout());
            toolBarPanel.add(toolBar, "West");
            Image viewimg = Toolkit.getDefaultToolkit().getImage(
								 root.getAttribute("splashimage"));
            Image iconimg = Toolkit.getDefaultToolkit().getImage(
								 root.getAttribute("iconimage"));
            vxd.viewPanel = new JLabel(new ImageIcon(viewimg));

            frame = new JFrame(title);
            frame.setJMenuBar(menuBar);
            frame.setIconImage(iconimg);
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(toolBarPanel, "North");
            frame.getContentPane().add(viewPanel, "Center");
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());
	    XSIZE = screenSize.width - scnMax.left - scnMax.right;
	    YSIZE = screenSize.height - scnMax.top - scnMax.bottom;
	    frame.setSize(XSIZE,YSIZE);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
            frame.setVisible(true);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
			int YorN = JOptionPane.showConfirmDialog(vxd.frame
								 .getContentPane(), vxd.config.getDocumentElement()
								 .getAttribute("exitstring"), vxd.config
								 .getDocumentElement().getAttribute("exittitle"),
								 JOptionPane.YES_NO_OPTION);

			if (YorN == JOptionPane.YES_OPTION)
			    System.exit(0);
		    }
		});
            if (vxd.mainLoadFile != null) {
                SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ActionEvent ae = new ActionEvent(vxd.mainMenuItemOpen, 0, "OPEN");
			    vxd.mainMenuOpenListener.actionPerformed(ae);
			}
		    });
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void addToolButtons(JToolBar toolBar, NodeList images,
                                      ActionListener listener) {
        for (int i = 0; i < images.getLength(); ++i) {
            Element node = (Element) images.item(i);
            Image img = Toolkit.getDefaultToolkit().getImage(
							     node.getAttribute("image"));
            ImageFilter transparency = new BlackToTransparentFilter();
            ImageProducer producer = new FilteredImageSource(img.getSource(),
							     transparency);
            Image transparentimg = Toolkit.getDefaultToolkit().createImage(
									   producer);
            Image smallimg = transparentimg.getScaledInstance(xiconsize,
							      yiconsize, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(smallimg);
            JButton toolbutton = new JButton(icon);
            toolbutton.setToolTipText(node.getAttribute("text"));
            toolbutton.setActionCommand(node.getAttribute("command"));
            toolbutton.addActionListener(listener);
            toolbutton.setBorderPainted(false);
            toolbutton.setMargin(new Insets(iconborder, iconborder, iconborder,
					    iconborder));
            if (node.getAttribute("enabled").equals("TRUE"))
                toolBar.add(toolbutton);
        }
    }

    public static void addMenus(JMenuBar menuBar, NodeList menus,
                                ActionListener listener) {
        for (int i = 0; i < menus.getLength(); ++i) {
            Element menuel = (Element) menus.item(i);
            JMenu menu = new JMenu(menuel.getAttribute("name"));
            System.out.println(menuel.getAttribute("name"));
            menu.setMnemonic(menuel.getAttribute("letter").charAt(0));
            NodeList menuitms = menuel.getElementsByTagName("menuitem");
            for (int j = 0; j < menuitms.getLength(); ++j) {
                Element node = (Element) menuitms.item(j);
                Image img = Toolkit.getDefaultToolkit().getImage(
								 node.getAttribute("image"));
                ImageFilter transparency = new BlackToTransparentFilter();
                ImageProducer producer = new FilteredImageSource(img
								 .getSource(), transparency);
                Image transparentimg = Toolkit.getDefaultToolkit().createImage(
									       producer);
                Image smallimg = transparentimg.getScaledInstance(xiconsize,
								  yiconsize, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(smallimg);
                JMenuItem menuItem = new JMenuItem(node.getAttribute("text"),
						   icon);
                menuItem.setActionCommand(node.getAttribute("command"));
                menuItem.addActionListener(listener);
                if (node.getAttribute("text").equals("Open")) {
                    vxd.mainMenuOpenListener = listener;
                    vxd.mainMenuItemOpen = menuItem;
                }
                if (node.getAttribute("enabled").equals("TRUE"))
                    menu.add(menuItem);
            }
            menuBar.add(menu);
        }
    }

    public static class LanguageActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String lang = "";
            for (int m = 0; m < vxd.languages.getLength(); ++m) {
                Element anode = (Element) languages.item(m);
                String txt = anode.getAttribute("name");
                if (txt.equals((String) vxd.languageCombo.getSelectedItem()))
                    lang = anode.getAttribute("language");
            }
            translatorCombo.removeAllItems();
            for (int i = 0; i < vxd.translators.getLength(); ++i) {
                Element node = (Element) translators.item(i);
                if (node.getAttribute("enabled").equals("TRUE")
		    && node.getAttribute("supportedlanguages")
		    .indexOf(lang) > -1) {
                    translatorCombo.addItem(node.getAttribute("name"));
                }
            }
            (new TranslatorActionListener()).actionPerformed(null);
        }
    }

    public static class TranslatorActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String transl = "";
            for (int m = 0; m < vxd.translators.getLength(); ++m) {
                Element anode = (Element) translators.item(m);
                String txt = anode.getAttribute("name");
                if (txt.equals((String) vxd.translatorCombo.getSelectedItem()))
                    transl = anode.getAttribute("translator");
            }
            deployerCombo.removeAllItems();
            for (int i = 0; i < vxd.platforms.getLength(); ++i) {
                Element node = (Element) platforms.item(i);
                if (node.getAttribute("enabled").equals("TRUE")
		    && node.getAttribute("supportedtranslators").indexOf(
									 transl) > -1)
                    deployerCombo.addItem(node.getAttribute("name"));
            }
        }
    }

    public static class MenuItemActionListener implements ActionListener {
        public static DocumentBuilderFactory factory = DocumentBuilderFactoryImpl.newInstance();
        ;
        public static DocumentBuilder builder;

        static {
            try {
                builder = (DocumentBuilder) factory.newDocumentBuilder();
            } catch (Exception e) {
                ;
            }
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            builder.setErrorHandler(new DefaultHandler() {
		    public void error(SAXParseException exception) throws SAXParseException {
			throw exception;
		    }

		    public void fatalError(SAXParseException exception) throws SAXParseException {
			throw exception;
		    }

		    public void warning(SAXParseException exception) throws SAXParseException {
			throw exception;
		    }
		});
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("NEWXML")) {
                JDialog newproj = new JDialog(vxd.frame, "New Project", true);
                newproj.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                newproj.setSize(325, 375);
                topDialog = newproj;
                newproj.setLocationRelativeTo(null);
                JLabel namelabel = new JLabel("Enter The Name of the Project");
                namelabel.setHorizontalAlignment(JLabel.CENTER);
                namelabel.setVerticalAlignment(JLabel.CENTER);
                JTextField projname = new JTextField();
                projname.setText("ProjectName");
                projname.selectAll();
                vxd.textField = projname;
                projname.setColumns(15);
                JPanel projnamepanel = new JPanel();
                projnamepanel.setLayout(new FlowLayout());
                projnamepanel.add(projname);
                JLabel langlabel = new JLabel("Choose the Project Language");
                langlabel.setHorizontalAlignment(JLabel.CENTER);
                langlabel.setVerticalAlignment(JLabel.CENTER);
                JComboBox langs = new JComboBox();
                vxd.languageCombo = langs;
                for (int i = 0; i < vxd.languages.getLength(); ++i) {
                    Element node = (Element) languages.item(i);
                    if (node.getAttribute("enabled").equals("TRUE"))
                        langs.addItem(node.getAttribute("name"));
                }
                JPanel langspanel = new JPanel();
                langspanel.setLayout(new FlowLayout());
                langspanel.add(langs);
                JLabel translabel = new JLabel("Choose the Language Translator");
                translabel.setHorizontalAlignment(JLabel.CENTER);
                translabel.setVerticalAlignment(JLabel.CENTER);
                JComboBox trans = new JComboBox();
                String firstTransl = null;
                for (int i = 0; i < vxd.translators.getLength(); ++i) {
                    Element node = (Element) translators.item(i);
                    if (node.getAttribute("enabled").equals("TRUE")
			&& node.getAttribute("supportedlanguages").indexOf(
									   ((Element) languages.item(0))
									   .getAttribute("language")) > -1) {
                        if (firstTransl == null)
                            firstTransl = node.getAttribute("translator");
                        trans.addItem(node.getAttribute("name"));
                    }
                }
                JPanel transpanel = new JPanel();
                transpanel.setLayout(new FlowLayout());
                transpanel.add(trans);
                vxd.translatorCombo = trans;
                vxd.languageCombo
		    .addActionListener(new LanguageActionListener());
                vxd.translatorCombo
		    .addActionListener(new TranslatorActionListener());
                JLabel deploylabel = new JLabel(
						"Choose the Deployment Platform");
                deploylabel.setHorizontalAlignment(JLabel.CENTER);
                deploylabel.setVerticalAlignment(JLabel.CENTER);
                JComboBox deploy = new JComboBox();
                vxd.deployerCombo = deploy;
                for (int i = 0; i < vxd.platforms.getLength(); ++i) {
                    Element node = (Element) platforms.item(i);
                    if (node.getAttribute("enabled").equals("TRUE")
			&& node.getAttribute("supportedtranslators")
			.indexOf(firstTransl) > -1) {
                        deploy.addItem(node.getAttribute("name"));
                    }
                }
                JPanel deploypanel = new JPanel();
                deploypanel.setLayout(new FlowLayout());
                deploypanel.add(deploy);
                JPanel btnpanel = new JPanel();
                btnpanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                btnpanel.setLayout(new FlowLayout());
                JButton okbtn = new JButton("OK");
                okbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    if (vxd.textField.getText() == null
                                || vxd.textField.getText().length() == 0) {
				JOptionPane.showMessageDialog(vxd.frame,
							      "The Project Name Cannot Be Blank");
			    } else {
				String lang = "";
				for (int i = 0; i < vxd.languages.getLength(); ++i) {
				    Element node = (Element) languages.item(i);
				    String txt = node.getAttribute("name");
				    if (txt.equals((String) vxd.languageCombo
						   .getSelectedItem()))
					lang = node.getAttribute("language");
				}
				String trans = "";
				for (int i = 0; i < vxd.translators.getLength(); ++i) {
				    Element node = (Element) translators.item(i);
				    String txt = node.getAttribute("name");
				    if (txt.equals((String) vxd.translatorCombo
						   .getSelectedItem())) {
					try {
					    String conf = node
                                                .getAttribute("configfile");
					    DocumentBuilderFactory factory = DocumentBuilderFactory
                                                .newInstance();
					    DocumentBuilder builder = factory
                                                .newDocumentBuilder();
					    Document tconfig = builder
                                                .parse(new File(conf));
					    Element root = tconfig
                                                .getDocumentElement();
					    Class cls = Class
                                                .forName(((Element) (root
								     .getElementsByTagName("eventhandler")
								     .item(0)))
							 .getAttribute("class"));
					    ActionListener listener = (ActionListener) cls
                                                .newInstance();
					    NodeList menus = root
                                                .getElementsByTagName("menu");
					    addMenus(vxd.menuBar, menus, listener);
					    NodeList images = root
                                                .getElementsByTagName("toolbarbutton");
					    addToolButtons(vxd.toolBar, images,
							   listener);
					    trans = node.getAttribute("translator");
					} catch (Exception exx) {
					    exx.printStackTrace();
					}
				    }
				}
				String deploy = "";
				for (int i = 0; i < vxd.platforms.getLength(); ++i) {
				    Element node = (Element) platforms.item(i);
				    String txt = node.getAttribute("name");
				    if (txt.equals((String) vxd.deployerCombo
						   .getSelectedItem())) {
					try {
					    String conf = node
                                                .getAttribute("configfile");
					    DocumentBuilderFactory factory = DocumentBuilderFactory
                                                .newInstance();
					    DocumentBuilder builder = factory
                                                .newDocumentBuilder();
					    Document tconfig = builder
                                                .parse(new File(conf));
					    Element root3 = tconfig
                                                .getDocumentElement();
					    Class cls = Class
                                                .forName(((Element) (root3
								     .getElementsByTagName("eventhandler")
								     .item(0)))
							 .getAttribute("class"));
					    ActionListener listener = (ActionListener) cls
                                                .newInstance();
					    NodeList menus = root3
                                                .getElementsByTagName("menu");
					    addMenus(vxd.menuBar, menus, listener);
					    NodeList images = root3
                                                .getElementsByTagName("toolbarbutton");
					    addToolButtons(vxd.toolBar, images,
							   listener);
					    deploy = node.getAttribute("platform");
					} catch (Exception exx) {
					    exx.printStackTrace();
					}
				    }
				}
				topDialog.setVisible(false);
				topDialog.dispose();
				topDialog = null;
				vxd.project = new vxdproject(vxd.textField
							     .getText(), lang, trans, deploy);
				vxd.controller = new vxdcontroller(vxd.project);
			    }
			}
		    });
                JButton cancelbtn = new JButton("Cancel");
                cancelbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    topDialog.setVisible(false);
			    topDialog.dispose();
			    topDialog = null;
			}
		    });
                newproj.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
			    topDialog.setVisible(false);
			    topDialog.dispose();
			    topDialog = null;
			}
		    });
                btnpanel.add(okbtn);
                btnpanel.add(cancelbtn);
                newproj.getContentPane().setLayout(new GridLayout(10, 1));
                newproj.getContentPane().add(namelabel);
                newproj.getContentPane().add(projname);
                newproj.getContentPane().add(langlabel);
                newproj.getContentPane().add(langspanel);
                newproj.getContentPane().add(translabel);
                newproj.getContentPane().add(transpanel);
                newproj.getContentPane().add(deploylabel);
                newproj.getContentPane().add(deploypanel);
                newproj.getContentPane().add(new JPanel());
                newproj.getContentPane().add(btnpanel);
                newproj.setVisible(true);
            } else if (e.getActionCommand().equals("OPEN")) {
                try {
                    JFileChooser fc = new JFileChooser();
                    if (mainLoadFile == null) {
                        fc.setCurrentDirectory(new File(SAVEFILEDIR));
                        fc.showOpenDialog(vxd.frame);
                    }
                    JDialog passwd = new JDialog(vxd.frame, "Enter Password", true);
                    passwd.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                    passwd.setSize(325, 175);
                    topDialog = passwd;
                    passwd.setLocationRelativeTo(null);
                    JLabel passwdlabel = new JLabel("Enter Password (Blank for Unencrypted)");
                    passwdlabel.setHorizontalAlignment(JLabel.CENTER);
                    passwdlabel.setVerticalAlignment(JLabel.CENTER);
                    JPasswordField passwdtextfield = new JPasswordField();
                    passwdtextfield.setText("");
                    passwdtextfield.setEchoChar('*');
                    vxd.passwdtextField = passwdtextfield;
                    passwdtextfield.setColumns(15);
                    JPanel passwdpanel = new JPanel();
                    passwdpanel.setLayout(new GridLayout(2, 1));
                    passwdpanel.add(passwdlabel);
                    passwdpanel.add(passwdtextfield);
                    JPanel btnpanel = new JPanel();
                    btnpanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                    btnpanel.setLayout(new FlowLayout());
                    JButton okbtn = new JButton("OK");

                    okbtn.addActionListener(new vxdOpenFileActionListener(fc.getSelectedFile(), builder));
                    JButton cancelbtn = new JButton("Cancel");
                    cancelbtn.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
				topDialog.setVisible(false);
				topDialog.dispose();
				topDialog = null;
			    }
			});
                    passwd.addWindowListener(new WindowAdapter() {
			    public void windowClosing(WindowEvent e) {
				topDialog.setVisible(false);
				topDialog.dispose();
				topDialog = null;
			    }
			});
                    passwd.getContentPane().setLayout(new GridLayout(4, 1));
                    passwd.getContentPane().add(passwdpanel);
                    passwd.getContentPane().add(new JPanel());
                    btnpanel.add(okbtn);
                    btnpanel.add(cancelbtn);
                    passwd.getContentPane().add(btnpanel);
                    passwd.setVisible(true);
                } catch (Exception iie) {
                    iie.printStackTrace();
                }
            } else if (e.getActionCommand().equals("SAVE")) {
                if (vxd.controller == null || vxd.controller.project == null) {
                    JOptionPane.showMessageDialog(vxd.frame,
						  "No Project Loaded to Save");
                    return;
                }
                JDialog passwd = new JDialog(vxd.frame, "Enter Password", true);
                passwd.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                passwd.setSize(325, 175);
                topDialog = passwd;
                passwd.setLocationRelativeTo(null);
                JLabel passwdlabel = new JLabel("Enter Password");
                passwdlabel.setHorizontalAlignment(JLabel.CENTER);
                passwdlabel.setVerticalAlignment(JLabel.CENTER);
                JPasswordField passwdtextfield = new JPasswordField();
                passwdtextfield.setText(vxd.mainPassword);
                passwdtextfield.setEchoChar('*');
                vxd.passwdtextField = passwdtextfield;
                passwdtextfield.setColumns(15);
                JPanel passwdpanel = new JPanel();
                passwdpanel.setLayout(new GridLayout(2, 1));
                passwdpanel.add(passwdlabel);
                passwdpanel.add(passwdtextfield);
                JPanel btnpanel = new JPanel();
                btnpanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                btnpanel.setLayout(new FlowLayout());
                JButton okbtn = new JButton("OK");
                okbtn.addActionListener(new vxdSaveFileActionListener());
                JButton cancelbtn = new JButton("Cancel");
                cancelbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    topDialog.setVisible(false);
			    topDialog.dispose();
			    topDialog = null;
			}
		    });
                passwd.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
			    topDialog.setVisible(false);
			    topDialog.dispose();
			    topDialog = null;
			}
		    });
                passwd.getContentPane().setLayout(new GridLayout(4, 1));
                passwd.getContentPane().add(passwdpanel);
                passwd.getContentPane().add(new JPanel());
                btnpanel.add(okbtn);
                btnpanel.add(cancelbtn);
                passwd.getContentPane().add(btnpanel);
                passwd.setVisible(true);
            } else if (e.getActionCommand().equals("EXIT")) {
                int YorN = JOptionPane.showConfirmDialog(vxd.frame
							 .getContentPane(), "Are You Sure You Want to Exit?",
							 "Exit Program", JOptionPane.YES_NO_OPTION);
                if (YorN == JOptionPane.YES_OPTION)
                    System.exit(0);
            } else {
                try {
                    String os = System.getProperty("os.name").startsWith("Windows") ? "Windows" : System.getProperty("os.name").startsWith("Mac") ? "Mac" : "Linux";
                    String separator = System.getProperty("os.name").startsWith("Windows") ? "\\" : "/";
                    String browser = vxd.config.getDocumentElement().getAttribute("browser" + os);
                    String editor = vxd.config.getDocumentElement().getAttribute("editor" + os);
                    String fileManager = vxd.config.getDocumentElement().getAttribute("fileManager" + os);
                    java.lang.Runtime.getRuntime().exec(e.getActionCommand().
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
                } catch (Exception re) {
                    JOptionPane.showMessageDialog(vxd.frame, "Error: " + re.getMessage());
                }
            }
        }
    }

    public static class BlackToTransparentFilter extends RGBImageFilter {
        public static final boolean DISABLED = true;

        public BlackToTransparentFilter() {
            canFilterIndexColorModel = true;
        }

        public int filterRGB(int x, int y, int rgb) {
            if (DISABLED) {
                return rgb;
            }
            if ((rgb & 0x00ffffff) == 0x00)
                return 0x00;
            else
                return rgb;
        }
    }
}
