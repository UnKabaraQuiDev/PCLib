package lu.kbra.pclib.swing;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

public class JLabelBuilder {

	private final JLabel label;

	public JLabelBuilder() {
		label = new JLabel();
	}

	public static JLabelBuilder create() {
		return new JLabelBuilder();
	}

	public JLabelBuilder(String text) {
		label = new JLabel(text);
	}

	public static JLabelBuilder create(String text) {
		return new JLabelBuilder(text);
	}

	public JLabelBuilder(String text, int hAlignment) {
		label = new JLabel(text, hAlignment);
	}

	public static JLabelBuilder create(String text, int hAlignment) {
		return new JLabelBuilder(text, hAlignment);
	}

	public JLabelBuilder text(String text) {
		label.setText(text);
		return this;
	}

	public JLabelBuilder font(Font font) {
		label.setFont(font);
		return this;
	}

	public JLabelBuilder font(String name, int style, int size) {
		label.setFont(new Font(name, style, size));
		return this;
	}

	public JLabelBuilder foreground(Color color) {
		label.setForeground(color);
		return this;
	}

	public JLabelBuilder background(Color color) {
		label.setOpaque(true);
		label.setBackground(color);
		return this;
	}

	public JLabelBuilder horizontalAlignment(int alignment) {
		label.setHorizontalAlignment(alignment);
		return this;
	}

	public JLabelBuilder verticalAlignment(int alignment) {
		label.setVerticalAlignment(alignment);
		return this;
	}

	public JLabelBuilder toolTip(String tip) {
		label.setToolTipText(tip);
		return this;
	}

	public JLabelBuilder border(javax.swing.border.Border border) {
		label.setBorder(border);
		return this;
	}

	public JLabelBuilder opaque(boolean opaque) {
		label.setOpaque(opaque);
		return this;
	}

	public JLabel build() {
		return label;
	}

}
