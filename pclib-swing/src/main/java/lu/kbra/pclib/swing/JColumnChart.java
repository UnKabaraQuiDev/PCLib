package lu.kbra.pclib.swing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import lu.kbra.pclib.PCUtils;

public class JColumnChart extends JComponent {

	private static final long serialVersionUID = 3907333543967066816L;
	private final List<String> titleEntries;
	private HashMap<String, ChartData> valueEntries = new HashMap<>();

	private boolean _filled = true;
	private Color _fillColor = new Color(0, 0, 128, 255), _borderColor = Color.BLUE;
	private Color majorAxisColor = Color.BLACK;
	private Color minorAxisColor = Color.DARK_GRAY;

	private boolean overrideMaxValue = false;
	private double maxValue = 10;
	private boolean overrideMinValue = true;
	private double minValue = 0;

	private boolean useMinorAxisSteps = true;
	private int minorAxisCount = 4;
	private double minorAxisStep = 1;

	private boolean useFixedPadding = true;
	private double scaleX = 0.9, scaleY = 0.9;
	private int fixedPadding = 50;

	private Color annotationColor = Color.BLACK;
	private boolean annotateMinorAxis = true;

	public JColumnChart(final List<String> titles) {
		this.titleEntries = titles;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		final Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width = this.getWidth();
		int height = this.getHeight();
		// final int size = Math.min(width, height);

		final double minValue = this.overrideMinValue ? this.minValue : this.computeMinValue();
		final double maxValue = this.overrideMaxValue ? this.maxValue : this.computeMaxValue();

		g2d.translate(this.useFixedPadding ? this.fixedPadding : (1 - this.scaleX) * width,
				this.useFixedPadding ? this.fixedPadding : (1 - this.scaleY) * height);

		final double scaleX = this.useFixedPadding ? (double) (width - 2 * this.fixedPadding) / width : this.scaleX,
				scaleY = this.useFixedPadding ? (double) (height - 2 * this.fixedPadding) / height : this.scaleY;
		// g2d.scale(scaleX, scaleY);

		// we scale the sizes bc we don't want the text to be scaled as well
		width *= scaleX;
		height *= scaleY;

		// Minor axis
		if (this.useMinorAxisSteps) {
			for (double cvalue = minValue; cvalue <= maxValue + this.minorAxisStep / 2; cvalue += this.minorAxisStep) {
				final int yLevel = (int) PCUtils.map(cvalue, maxValue, minValue, 0, height);

				g2d.setColor(this.minorAxisColor);
				g2d.drawLine(0, yLevel, width, yLevel);

				if (this.annotateMinorAxis) {
					g2d.setColor(this.annotationColor);
					final String str = String.format("%.2f", cvalue);
					g2d.drawString(str,
							0 - g2d.getFontMetrics().stringWidth(str) - g2d.getFontMetrics().charWidth(' '),
							yLevel + g2d.getFontMetrics().getHeight() / 4);
				}
			}
		} else {
			for (int lineIndex = 1; lineIndex <= this.minorAxisCount; lineIndex++) {
				final double cvalue = PCUtils.map(lineIndex, 1, this.minorAxisCount, minValue, maxValue);
				final int yLevel = (int) PCUtils.map(cvalue, maxValue, minValue, 0, height);

				g2d.setColor(this.minorAxisColor);
				g2d.drawLine(0, yLevel, width, yLevel);

				if (this.annotateMinorAxis) {
					g2d.setColor(this.annotationColor);
					final String str = String.format("%.2f", cvalue);
					g2d.drawString(str,
							0 - g2d.getFontMetrics().stringWidth(str) - g2d.getFontMetrics().charWidth(' '),
							yLevel + g2d.getFontMetrics().getHeight() / 4);
				}
			}
		}

		// draw major axis
		g2d.setColor(this.majorAxisColor);
		g2d.setStroke(new BasicStroke(2));
		final int y0AxisLevel = (int) PCUtils.map(0, maxValue, minValue, 0, height);
		g2d.drawLine(0, y0AxisLevel, width, y0AxisLevel); // x axis
		g2d.drawLine(0, 0, 0, height); // y axis

		final int mainColumnCount = this.titleEntries.size();
		final double mainColumnWidth = (double) width / mainColumnCount;
		final int subColumnCount = this.valueEntries.size();
		final double subColumnWidth = mainColumnWidth / (subColumnCount + 1);

		for (int mainColumnIndex = 0; mainColumnIndex < this.titleEntries.size(); mainColumnIndex++) {
			final String mainColumnTitle = this.titleEntries.get(mainColumnIndex);

			int subColumnIndex = 0;
			for (final Entry<String, ChartData> eScd : this.valueEntries.entrySet()) {
				final String entryTitle = eScd.getKey();
				final ChartData cd = eScd.getValue();

				final double cvalue = cd.getValue(mainColumnTitle);
				final double cheight = PCUtils.map(cvalue, maxValue, minValue, 0, height);
				Rectangle2D.Double rect = null;

				if (cvalue < 0) {
					rect = new Rectangle.Double(mainColumnWidth * mainColumnIndex + subColumnWidth * subColumnIndex,
							y0AxisLevel,
							subColumnWidth,
							PCUtils.map(cvalue, 0, minValue, 0, height - y0AxisLevel));
				} else {
					rect = new Rectangle.Double(mainColumnWidth * mainColumnIndex + subColumnWidth * subColumnIndex,
							y0AxisLevel - cheight,
							subColumnWidth,
							cheight);
				}

				if (cd.fill) {
					g2d.setColor(cd.fillColor);
					g2d.fill(rect);
				}

				g2d.setColor(cd.borderColor);
				g2d.draw(rect);

				subColumnIndex++;
			}

			g2d.setColor(this.annotationColor);
			g2d.drawString(mainColumnTitle,
					(int) (mainColumnWidth * (mainColumnIndex + 0.5) - g2d.getFontMetrics().stringWidth(mainColumnTitle) / 2),
					y0AxisLevel + g2d.getFontMetrics().getHeight() / 4 * 3);
		}
	}

	protected double computeMaxValue() {
		return this.valueEntries.values()
				.stream()
				.flatMapToDouble(v -> v.getValues().values().stream().mapToDouble(Double::valueOf))
				.max()
				.orElse(1);
	}

	protected double computeMinValue() {
		return this.valueEntries.values()
				.stream()
				.flatMapToDouble(v -> v.getValues().values().stream().mapToDouble(Double::valueOf))
				.min()
				.orElse(0);
	}

	public class ChartData {

		protected Map<String, Double> values;
		protected boolean fill = JColumnChart.this._filled;
		protected Color fillColor = JColumnChart.this._fillColor, borderColor = JColumnChart.this._borderColor;

		public ChartData() {
		}

		public double getValue(final String key) {
			return this.values.get(key);
		}

		public ChartData(final Map<String, Double> values, final boolean fill, final Color fillColor, final Color borderColor) {
			this.values = values;
			this.fill = fill;
			this.fillColor = fillColor;
			this.borderColor = borderColor;
		}

		public Map<String, Double> getValues() {
			return this.values;
		}

		public ChartData setValues(final Map<String, Double> values) {
			this.values = values;
			return this;
		}

		public boolean isFill() {
			return this.fill;
		}

		public ChartData setFill(final boolean fill) {
			this.fill = fill;
			return this;
		}

		public Color getFillColor() {
			return this.fillColor;
		}

		public ChartData setFillColor(final Color fillColor) {
			this.fillColor = fillColor;
			return this;
		}

		public Color getBorderColor() {
			return this.borderColor;
		}

		public ChartData setBorderColor(final Color borderColor) {
			this.borderColor = borderColor;
			return this;
		}

	}

	public ChartData createSeries(final String title) {
		final ChartData chartData = new ChartData();
		this.valueEntries.put(title, chartData);
		return chartData;
	}

	public class JLineGraphLegend extends JComponent {

		private static final long serialVersionUID = 5653679243764754983L;
		private final boolean vertical;
		private final boolean wrap;

		public JLineGraphLegend(final boolean vertical, final boolean wrap) {
			this.vertical = vertical;
			this.wrap = wrap;
		}

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			final Graphics2D g2d = (Graphics2D) g;

			g2d.setColor(super.getBackground());
			g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			final int squareSize = 15; // Size of the color square
			final int padding = 5; // Padding between items
			int x = padding, y = padding;

			final FontMetrics fm = g.getFontMetrics();

			for (final Entry<String, ChartData> item : JColumnChart.this.valueEntries.entrySet()) {
				final String title = item.getKey();
				final Color fillColor = item.getValue().getFillColor(), borderColor = item.getValue().getBorderColor();

				// Draw color square
				g2d.setColor(fillColor);
				g2d.fillRect(x, y, squareSize, squareSize);
				g2d.setColor(borderColor);
				g2d.drawRect(x, y, squareSize, squareSize);

				// Draw title text
				final int textX = x + squareSize + padding;
				final int textY = y + squareSize / 2 + fm.getAscent() / 2 - 2;
				g2d.setColor(JColumnChart.this.annotationColor);
				g2d.drawString(title, textX, textY);

				// Update coordinates for next item
				if (this.vertical) {
					y += squareSize + padding;
				} else {
					final int itemWidth = squareSize + padding + fm.stringWidth(title) + padding;
					if (this.wrap && x + itemWidth > this.getWidth()) {
						x = padding;
						y += squareSize + padding;
					} else {
						x += itemWidth;
					}
				}
			}
		}

		@Override
		public Dimension getPreferredSize() {
			final FontMetrics fm = this.getFontMetrics(this.getFont());

			int width = 20;
			int height = 20;

			final int squareSize = 15;
			final int paddingSize = 5;

			if (this.vertical) {
				height = Math.max(squareSize + paddingSize * 2, fm.getHeight()) * JColumnChart.this.valueEntries.size() + paddingSize;
				width = squareSize + paddingSize * 3
						+ JColumnChart.this.valueEntries.keySet().stream().mapToInt(fm::stringWidth).max().orElse(0);
			} else {
				width = (squareSize + paddingSize) * JColumnChart.this.valueEntries.size() + paddingSize;
				height = Math.max(squareSize + paddingSize * 2, fm.getHeight());
			}

			return new Dimension(width, height);
		}

	}

	protected JComponent createLegend(final boolean vertical, final boolean wrap) {
		return new JLineGraphLegend(vertical, wrap);
	}

	public void overrideMaxValue(final double maxValue) {
		this.overrideMaxValue = true;
		this.maxValue = maxValue;
	}

	public void resetOverrideMaxValue() {
		this.overrideMaxValue = false;
	}

	public boolean isUseMinorAxisSteps() {
		return this.useMinorAxisSteps;
	}

	public void setUseMinorAxisSteps(final boolean useMinorAxisSteps) {
		this.useMinorAxisSteps = useMinorAxisSteps;
	}

	public int getMinorAxisCount() {
		return this.minorAxisCount;
	}

	public void setMinorAxisCount(final int minorAxisCount) {
		this.minorAxisCount = minorAxisCount;
	}

	public double getMinorAxisStep() {
		return this.minorAxisStep;
	}

	public void setMinorAxisStep(final double minorAxisStep) {
		this.minorAxisStep = minorAxisStep;
	}

	public Color getMajorAxisColor() {
		return this.majorAxisColor;
	}

	public void setMajorAxisColor(final Color majorAxisColor) {
		this.majorAxisColor = majorAxisColor;
	}

	public Color getMinorAxisColor() {
		return this.minorAxisColor;
	}

	public void setMinorAxisColor(final Color minorAxisColor) {
		this.minorAxisColor = minorAxisColor;
	}

	public Color getAnnotationColor() {
		return this.annotationColor;
	}

	public void setAnnotationColor(final Color annotationColor) {
		this.annotationColor = annotationColor;
	}

	public boolean isAnnotateMinorAxis() {
		return this.annotateMinorAxis;
	}

	public void setAnnotateMinorAxis(final boolean annotateMinorAxis) {
		this.annotateMinorAxis = annotateMinorAxis;
	}

	public double getScaleX() {
		return this.scaleX;
	}

	public void setScaleX(final double scaleX) {
		this.scaleX = scaleX;
	}

	public double getScaleY() {
		return this.scaleY;
	}

	public void setScaleY(final double scaleY) {
		this.scaleY = scaleY;
	}

	public void setScale(final double x, final double y) {
		this.scaleX = x;
		this.scaleY = y;
	}

	public HashMap<String, ChartData> getValueEntries() {
		return this.valueEntries;
	}

	public void setValueEntries(final HashMap<String, ChartData> valueEntries) {
		this.valueEntries = valueEntries;
	}

	public boolean isUseFixedPadding() {
		return this.useFixedPadding;
	}

	public void setUseFixedPadding(final boolean useFixedPadding) {
		this.useFixedPadding = useFixedPadding;
	}

	public int getFixedPadding() {
		return this.fixedPadding;
	}

	public void setFixedPadding(final int fixedPadding) {
		this.fixedPadding = fixedPadding;
	}

	public boolean isNextFilled() {
		return this._filled;
	}

	public void setNextFilled(final boolean _filled) {
		this._filled = _filled;
	}

	public Color getNextFillColor() {
		return this._fillColor;
	}

	public void setNextFillColor(final Color _fillColor) {
		this._fillColor = _fillColor;
	}

	public Color getNextBorderColor() {
		return this._borderColor;
	}

	public void setNextBorderColor(final Color _borderColor) {
		this._borderColor = _borderColor;
	}

	public static void main(final String[] args) {
		// Create and show frame
		final JFrame frame = new JFrame("Line Graph");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		final JColumnChart graph = new JColumnChart(Arrays.asList("a", "baaaaa", "c", "d"));

		final Map<String, Double> values = new HashMap<>();
		graph.titleEntries.forEach(c -> values.put(c, 1.0));
		graph.createSeries("Entry 1").setValues(values);

		final Map<String, Double> values2 = new HashMap<>();
		graph.titleEntries.forEach(c -> values2.put(c, PCUtils.randomDoubleRange(0, 5)));
		graph.createSeries("Entry 2").setValues(values2).setFillColor(new Color(128, 0, 0, 255)).setBorderColor(Color.RED);

		final Map<String, Double> values3 = new HashMap<>();
		graph.titleEntries.forEach(c -> values3.put(c, -2.0));
		graph.createSeries("Entry 3").setValues(values3).setFillColor(new Color(0, 128, 0, 255)).setBorderColor(Color.GREEN);

		graph.useMinorAxisSteps = true;
		graph.minorAxisStep = 0.1;

		graph.overrideMaxValue = false;
		graph.maxValue = 10;

		graph.overrideMinValue = false;
		graph.minValue = 0;

		frame.getContentPane().add(graph, BorderLayout.CENTER);

		frame.getContentPane().add(graph.createLegend(false, true), BorderLayout.SOUTH);
		frame.getContentPane().add(graph.createLegend(true, true), BorderLayout.EAST);

		Arrays.stream(frame.getContentPane().getComponents())
				.filter(JLineGraphLegend.class::isInstance)
				.forEach(e -> e.setBackground(Color.LIGHT_GRAY));

		frame.setSize(600, 600);
		frame.setVisible(true);

	}
}
