package vxd;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WrappingToolTip extends JToolTip {
    private final int fixedWidth = 250; // Set your constant width here
    private List<String> lines = new ArrayList<>();

    public WrappingToolTip() {
	super();
    }

        @Override
	public void paintComponent(Graphics g) {
	    // Standard background/border painting
	    g.setColor(getBackground());
	    g.fillRect(0, 0, getWidth(), getHeight());
	    g.setColor(getForeground());

	    FontMetrics fm = g.getFontMetrics();
	    int y = fm.getAscent() + 5; // Start with a small padding

	    // Recalculate lines for the current font/text
	    wrapText(getComponent().getToolTipText(), fm);

	    for (String line : lines) {
		g.drawString(line, 5, y);
		y += fm.getHeight();
	    }
	}

        @Override
	public Dimension getPreferredSize() {
	    String text = getComponent().getToolTipText();
	    if (text == null || text.isEmpty()) return new Dimension(0, 0);

	    FontMetrics fm = getFontMetrics(getFont());
	    wrapText(text, fm);

	    int height = (lines.size() * fm.getHeight()) + 10; // Text height + padding
	    return new Dimension(fixedWidth, height);
	}

    private void wrapText(String text, FontMetrics fm) {
	lines.clear();
	String[] words = text.split("\\s+");
	StringBuilder currentLine = new StringBuilder();

	for (String word : words) {
	    // Check if adding the next word exceeds our fixed width
	    if (fm.stringWidth(currentLine.toString() + " " + word) < fixedWidth - 10) {
		if (currentLine.length() > 0) currentLine.append(" ");
		currentLine.append(word);
	    } else {
		// Commit the current line and start a new one with the word
		lines.add(currentLine.toString());
		currentLine = new StringBuilder(word);
	    }
	}

	if (currentLine.length() > 0) {
	    lines.add(currentLine.toString());
	}
    }
}
