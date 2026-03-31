package lu.kbra.pclib.swing;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

public class JLabelBuilder {

	private final JLabel label;

	public JLabelBuilder() {
		this.label = new JLabel();
	}

	public static JLabelBuilder create() {
		return new JLabelBuilder();
	}

	public JLabelBuilder(final String text) {
		this.label = new JLabel(text);
	}

	public static JLabelBuilder create(final String text) {
		return new JLabelBuilder(text);
	}

	public JLabelBuilder(final String text, final int hAlignment) {
		this.label = new JLabel(text, hAlignment);
	}

	public static JLabelBuilder create(final String text, final int hAlignment) {
		return new JLabelBuilder(text, hAlignment);
	}

	public JLabelBuilder text(final String text) {
		this.label.setText(text);
		return this;
	}

	public JLabelBuilder font(final Font font) {
		this.label.setFont(font);
		return this;
	}

	public JLabelBuilder font(final String name, final int style, final int size) {
		this.label.setFont(new Font(name, style, size));
		return this;
	}

	public JLabelBuilder foreground(final Color color) {
		this.label.setForeground(color);
		return this;
	}

	public JLabelBuilder background(final Color color) {
		this.label.setOpaque(true);
		this.label.setBackground(color);
		return this;
	}

	public JLabelBuilder horizontalAlignment(final int alignment) {
		this.label.setHorizontalAlignment(alignment);
		return this;
	}

	public JLabelBuilder verticalAlignment(final int alignment) {
		this.label.setVerticalAlignment(alignment);
		return this;
	}

	public JLabelBuilder toolTip(final String tip) {
		this.label.setToolTipText(tip);
		return this;
	}

	public JLabelBuilder border(final javax.swing.border.Border border) {
		this.label.setBorder(border);
		return this;
	}

	public JLabelBuilder opaque(final boolean opaque) {
		this.label.setOpaque(opaque);
		return this;
	}

	public JLabel build() {
		return this.label;
	}

}
