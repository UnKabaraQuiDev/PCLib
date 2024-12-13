package lu.pcy113.pclib.swing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.sql.PseudoColumnUsage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JFrame;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.swing.JRadarChart.ChartData;
import lu.pcy113.pclib.swing.JRadarChart.JRadarChartLegend;

public class JLineGraph extends JComponent {

	private boolean _filled = true;
	private Color _fillColor = new Color(0, 0, 128, 128), _borderColor = Color.BLUE;
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

	private HashMap<String, ChartData> valueEntries = new LinkedHashMap<>();

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width = getWidth();
		int height = getHeight();
		// final int size = Math.min(width, height);

		final double minValue = overrideMinValue ? this.minValue : computeMinValue();
		final double maxValue = overrideMaxValue ? this.maxValue : computeMaxValue();

		g2d.translate(useFixedPadding ? fixedPadding : (1 - this.scaleX) * width, useFixedPadding ? fixedPadding : (1 - this.scaleY) * height);

		final double scaleX = useFixedPadding ? (double) (width - 2 * fixedPadding) / width : this.scaleX, scaleY = useFixedPadding ? (double) (height - 2 * fixedPadding) / height : this.scaleY;
		// g2d.scale(scaleX, scaleY);

		// we scale the sizes bc we don't want the text to be scaled as well
		width *= scaleX;
		height *= scaleY;

		// Minor axis
		if (useMinorAxisSteps) {
			for (double cvalue = minValue; cvalue <= maxValue + minorAxisStep / 2; cvalue += minorAxisStep) {
				int yLevel = (int) PCUtils.map(cvalue, maxValue, minValue, 0, height);

				g2d.setColor(minorAxisColor);
				g2d.drawLine(0, yLevel, width, yLevel);

				if (annotateMinorAxis) {
					g2d.setColor(annotationColor);
					final String str = String.format("%.2f", cvalue);
					g2d.drawString(str, 0 - g2d.getFontMetrics().stringWidth(str) - g2d.getFontMetrics().charWidth(' '), yLevel + g2d.getFontMetrics().getHeight() / 4);
				}
			}
		} else {
			for (int lineIndex = 1; lineIndex <= minorAxisCount; lineIndex++) {
				double cvalue = PCUtils.map(lineIndex, 1, minorAxisCount, minValue, maxValue);
				int yLevel = (int) PCUtils.map(cvalue, maxValue, minValue, 0, height);

				g2d.setColor(minorAxisColor);
				g2d.drawLine(0, yLevel, width, yLevel);

				if (annotateMinorAxis) {
					g2d.setColor(annotationColor);
					final String str = String.format("%.2f", (double) cvalue);
					g2d.drawString(str, 0 - g2d.getFontMetrics().stringWidth(str) - g2d.getFontMetrics().charWidth(' '), yLevel + g2d.getFontMetrics().getHeight() / 4);
				}
			}
		}

		// draw major axis
		g2d.setColor(majorAxisColor);
		g2d.setStroke(new BasicStroke(2));
		final int y0AxisLevel = (int) PCUtils.map(0, maxValue, minValue, 0, height);
		g2d.drawLine(0, y0AxisLevel, width, y0AxisLevel); // x axis
		g2d.drawLine(0, 0, 0, height); // y axis

		for (Entry<String, ChartData> eScd : valueEntries.entrySet()) {

			final String entryTitle = eScd.getKey();
			final ChartData cd = eScd.getValue();
			
			if(cd.getValues().size() < 1) {
				continue;
			}
			
			final double widthStep = width / cd.getValues().size();

			Polygon valuesPolygon = new Polygon();
			valuesPolygon.addPoint(0, (int) PCUtils.map(0, maxValue, minValue, 0, height));

			for (int i = 0; i < cd.getValues().size(); i++) {
				int yLevel = (int) PCUtils.map(cd.getValue(i), maxValue, minValue, 0, height);
				valuesPolygon.addPoint((int) (widthStep * i), yLevel);
			}

			valuesPolygon.addPoint((int) (widthStep * (cd.getValues().size() - 1)), (int) PCUtils.map(0, maxValue, minValue, 0, height));

			if (cd.fill) {
				g2d.setColor(cd.fillColor);
				g2d.fill(valuesPolygon);
			}

			g2d.setColor(cd.borderColor);
			g2d.draw(valuesPolygon);
		}
	}

	protected double computeMaxValue() {
		return valueEntries.values().stream().flatMapToDouble(v -> v.getValues().stream().mapToDouble(Double::valueOf)).max().orElse(1);
	}

	protected double computeMinValue() {
		return valueEntries.values().stream().flatMapToDouble(v -> v.getValues().stream().mapToDouble(Double::valueOf)).min().orElse(0);
	}

	public class ChartData {

		protected List<Double> values = new ArrayList<Double>();
		protected boolean fill = _filled;
		protected Color fillColor = _fillColor, borderColor = _borderColor;

		public ChartData() {
		}

		public double getValue(int i) {
			return values.get(i);
		}

		public ChartData(List<Double> values, boolean fill, Color fillColor, Color borderColor) {
			this.values = values;
			this.fill = fill;
			this.fillColor = fillColor;
			this.borderColor = borderColor;
		}

		public List<Double> getValues() {
			return values;
		}

		public ChartData setValues(List<Double> values) {
			this.values = values;
			return this;
		}

		public boolean isFill() {
			return fill;
		}

		public ChartData setFill(boolean fill) {
			this.fill = fill;
			return this;
		}

		public Color getFillColor() {
			return fillColor;
		}

		public ChartData setFillColor(Color fillColor) {
			this.fillColor = fillColor;
			return this;
		}

		public Color getBorderColor() {
			return borderColor;
		}

		public ChartData setBorderColor(Color borderColor) {
			this.borderColor = borderColor;
			return this;
		}

	}

	public ChartData createSeries(String title) {
		ChartData chartData = new ChartData();
		valueEntries.put(title, chartData);
		return chartData;
	}

	public class JLineGraphLegend extends JComponent {

		private final boolean vertical;
		private final boolean wrap;

		public JLineGraphLegend(boolean vertical, boolean wrap) {
			this.vertical = vertical;
			this.wrap = wrap;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			final Graphics2D g2d = (Graphics2D) g;

			g2d.setColor(super.getBackground());
			g2d.fillRect(0, 0, getWidth(), getHeight());

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			final int squareSize = 15; // Size of the color square
			final int padding = 5; // Padding between items
			int x = padding, y = padding;

			FontMetrics fm = g.getFontMetrics();

			for (Entry<String, ChartData> item : valueEntries.entrySet()) {
				final String title = item.getKey();
				final Color fillColor = item.getValue().getFillColor(), borderColor = item.getValue().getBorderColor();

				// Draw color square
				g2d.setColor(fillColor);
				g2d.fillRect(x, y, squareSize, squareSize);
				g2d.setColor(borderColor);
				g2d.drawRect(x, y, squareSize, squareSize);

				// Draw title text
				int textX = x + squareSize + padding;
				int textY = y + squareSize / 2 + fm.getAscent() / 2 - 2;
				g2d.setColor(annotationColor);
				g2d.drawString(title, textX, textY);

				// Update coordinates for next item
				if (vertical) {
					y += squareSize + padding;
				} else {
					int itemWidth = squareSize + padding + fm.stringWidth(title) + padding;
					if (wrap && x + itemWidth > getWidth()) {
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
			final FontMetrics fm = getFontMetrics(getFont());

			int width = 20;
			int height = 20;

			final int squareSize = 15;
			final int paddingSize = 5;

			if (vertical) {
				height = Math.max(squareSize + paddingSize * 2, fm.getHeight()) * valueEntries.size() + paddingSize;
				width = squareSize + paddingSize * 3 + valueEntries.keySet().stream().mapToInt(fm::stringWidth).max().orElse(0);
			} else {
				width = (squareSize + paddingSize) * valueEntries.size() + paddingSize;
				height = Math.max(squareSize + paddingSize * 2, fm.getHeight());
			}

			return new Dimension(width, height);
		}

	}

	public JComponent createLegend(boolean vertical, boolean wrap) {
		return new JLineGraphLegend(vertical, wrap);
	}

	public void overrideMaxValue(double maxValue) {
		this.overrideMaxValue = true;
		this.maxValue = maxValue;
	}

	public void overrideMinValue(double minValue) {
		this.overrideMinValue = true;
		this.minValue = minValue;
	}
	
	public void resetOverrideMaxValue() {
		this.overrideMaxValue = false;
	}

	public boolean isUseMinorAxisSteps() {
		return useMinorAxisSteps;
	}

	public void setUseMinorAxisSteps(boolean useMinorAxisSteps) {
		this.useMinorAxisSteps = useMinorAxisSteps;
	}

	public int getMinorAxisCount() {
		return minorAxisCount;
	}

	public void setMinorAxisCount(int minorAxisCount) {
		this.minorAxisCount = minorAxisCount;
	}

	public double getMinorAxisStep() {
		return minorAxisStep;
	}

	public void setMinorAxisStep(double minorAxisStep) {
		this.minorAxisStep = minorAxisStep;
	}

	public Color getMajorAxisColor() {
		return majorAxisColor;
	}

	public void setMajorAxisColor(Color majorAxisColor) {
		this.majorAxisColor = majorAxisColor;
	}

	public Color getMinorAxisColor() {
		return minorAxisColor;
	}

	public void setMinorAxisColor(Color minorAxisColor) {
		this.minorAxisColor = minorAxisColor;
	}

	public Color getAnnotationColor() {
		return annotationColor;
	}

	public void setAnnotationColor(Color annotationColor) {
		this.annotationColor = annotationColor;
	}

	public boolean isAnnotateMinorAxis() {
		return annotateMinorAxis;
	}

	public void setAnnotateMinorAxis(boolean annotateMinorAxis) {
		this.annotateMinorAxis = annotateMinorAxis;
	}

	public double getScaleX() {
		return scaleX;
	}

	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}
	
	public void setScale(double x, double y) {
		this.scaleX = x;
		this.scaleY = y;
	}

	public HashMap<String, ChartData> getValueEntries() {
		return valueEntries;
	}

	public void setValueEntries(HashMap<String, ChartData> valueEntries) {
		this.valueEntries = valueEntries;
	}

	public boolean isUseFixedPadding() {
		return useFixedPadding;
	}

	public void setUseFixedPadding(boolean useFixedPadding) {
		this.useFixedPadding = useFixedPadding;
	}

	public int getFixedPadding() {
		return fixedPadding;
	}

	public void setFixedPadding(int fixedPadding) {
		this.fixedPadding = fixedPadding;
	}

	public boolean isNextFilled() {
		return _filled;
	}

	public void setNextFilled(boolean _filled) {
		this._filled = _filled;
	}

	public Color getNextFillColor() {
		return _fillColor;
	}

	public void setNextFillColor(Color _fillColor) {
		this._fillColor = _fillColor;
	}

	public Color getNextBorderColor() {
		return _borderColor;
	}

	public void setNextBorderColor(Color _borderColor) {
		this._borderColor = _borderColor;
	}

	public static void main(String[] args) {
		// Create and show frame
		JFrame frame = new JFrame("Line Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		JLineGraph graph = new JLineGraph();

		final int MAX = 100;

		List<Double> values = new ArrayList<>(MAX);
		for (int i = 0; i <= MAX; i++) {
			values.add(2 * (double) i / MAX - 1);
		}
		graph.createSeries("Entry 1").setValues(values);
		graph.createSeries("Entry 2").setValues(PCUtils.reversed(new ArrayList<>(values))).setFillColor(new Color(128, 0, 0, 128)).setBorderColor(Color.RED);
		graph.createSeries("Entry 3").setValues(PCUtils.shuffled(new ArrayList<>(values))).setFillColor(new Color(0, 128, 0, 128)).setBorderColor(Color.GREEN);

		graph.useMinorAxisSteps = true;
		graph.minorAxisStep = 0.1;

		graph.overrideMaxValue = false;
		graph.maxValue = 10;

		graph.overrideMinValue = false;
		graph.minValue = 0;

		frame.getContentPane().add(graph, BorderLayout.CENTER);

		frame.getContentPane().add(graph.createLegend(false, true), BorderLayout.SOUTH);
		frame.getContentPane().add(graph.createLegend(true, true), BorderLayout.EAST);

		Arrays.stream(frame.getContentPane().getComponents()).filter(v -> v instanceof JLineGraphLegend).forEach(e -> e.setBackground(Color.LIGHT_GRAY));

		frame.setSize(600, 600);
		frame.setVisible(true);

	}
}
