package vxd;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.MetalToolTipUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MultiLineToolTipUI extends MetalToolTipUI {
    private final int FIXED_WIDTH = 250;
    private final List<String> lines = new ArrayList<>();

    @Override
    public void paint(Graphics g, JComponent c) {
	FontMetrics fm = g.getFontMetrics();
	Dimension size = c.getSize();

	// Paint background
	g.setColor(c.getBackground());
	g.fillRect(0, 0, size.width, size.height);

	// Paint text
	g.setColor(c.getForeground());
	String tipText = ((JToolTip)c).getTipText();
	if (tipText == null) return;

	wrapText(tipText, fm);

	int y = fm.getAscent() + 5;
	for (String line : lines) {
	    g.drawString(line, 5, y);
	    y += fm.getHeight();
	}
    }

    public static ComponentUI createUI(JComponent c) {
	return new MultiLineToolTipUI();
    }
    
    @Override
    public Dimension getPreferredSize(JComponent c) {
	FontMetrics fm = c.getFontMetrics(c.getFont());
	String tipText = ((JToolTip)c).getTipText();

	if (tipText == null) return new Dimension(0, 0);

	wrapText(tipText, fm);

	int wholeTipLength=fm.stringWidth(tipText);
	    
	int height = (lines.size() * fm.getHeight()) + 10;
	return new Dimension(wholeTipLength>FIXED_WIDTH?FIXED_WIDTH:wholeTipLength+10, height);
    }

    private void wrapText(String text, FontMetrics fm) {
	lines.clear();
	String[] words = text.split("\\s+");
	StringBuilder currentLine = new StringBuilder();

	for (String word : words) {
	    if (fm.stringWidth(currentLine + " " + word) < FIXED_WIDTH - 10) {
		if (currentLine.length() > 0) currentLine.append(" ");
		currentLine.append(word);
	    } else {
		lines.add(currentLine.toString());
		currentLine = new StringBuilder(word);
	    }
	}
	if (currentLine.length() > 0) lines.add(currentLine.toString());
    }
}
