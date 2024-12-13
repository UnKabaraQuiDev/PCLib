package lu.pcy113.pclib.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JFrame;

import lu.pcy113.pclib.PCUtils;

public class JRadarChart extends JComponent {

	private List<String> titleEntries;
	private HashMap<String, ChartData> valueEntries;

	private boolean _filled = true;
	private Color _fillColor = new Color(0, 0, 128, 128), _borderColor = Color.BLUE;
	private Color majorAxisColor = Color.BLACK;
	private Color minorAxisColor = Color.DARK_GRAY;

	private boolean overrideMaxValue = false;
	private double maxValue = 10;

	private boolean useMinorAxisSteps = true;
	private int minorAxisCount = 4;
	private double minorAxisStep = 1;

	private Color annotationColor = Color.BLACK;
	private boolean annotateMinorAxis = true;

	private boolean useFixedPadding = true;
	private double scale = 0.9;
	private int fixedPadding = 50;

	public JRadarChart(List<String> titleEntries, HashMap<String, ChartData> entries, Color majorAxisColor, Color minorAxisColor) {
		this.titleEntries = titleEntries;
		this.valueEntries = entries;
		this.majorAxisColor = majorAxisColor;
		this.minorAxisColor = minorAxisColor;
	}

	public JRadarChart(List<String> titleEntries, HashMap<String, ChartData> entries) {
		this.titleEntries = titleEntries;
		this.valueEntries = entries;
	}

	public JRadarChart(List<String> titleEntries) {
		this.titleEntries = titleEntries;
		this.valueEntries = new HashMap<>();
	}

	public JRadarChart() {
		this.valueEntries = new HashMap<>();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		List<String> titles = new ArrayList<String>();

		final int MAX = 100;

		for (int i = 0; i <= MAX; i++) {
			titles.add(Integer.toString(i));
		}

		JRadarChart radarChart = new JRadarChart(titles);

		List<Double> values = new ArrayList<>(MAX);
		for (int i = 0; i <= MAX; i++) {
			values.add((double) i / MAX);
		}
		radarChart.createData("Entry 1").setValues(values);
		radarChart.createData("Entry 2").setValues(PCUtils.reversed(new ArrayList<>(values))).setFillColor(new Color(128, 0, 0, 128)).setBorderColor(Color.RED);
		radarChart.createData("Entry 3").setValues(PCUtils.shuffled(new ArrayList<>(values))).setFillColor(new Color(0, 128, 0, 128)).setBorderColor(Color.GREEN);

		radarChart.setUseMinorAxisSteps(false);
		radarChart.setMinorAxisStep(0.5);

		frame.getContentPane().setLayout(new BorderLayout());

		frame.getContentPane().add(radarChart);

		frame.addMouseWheelListener(new MouseAdapter() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				radarChart.setMinorAxisCount(PCUtils.clampGreaterOrEquals(radarChart.getMinorAxisCount() + e.getWheelRotation(), 1));
				radarChart.repaint();
			}
		});

		frame.getContentPane().add(radarChart.createLegend(false, true), BorderLayout.SOUTH);
		frame.getContentPane().add(radarChart.createLegend(true, true), BorderLayout.EAST);

		Arrays.stream(frame.getContentPane().getComponents()).filter(v -> v instanceof JRadarChartLegend).forEach(e -> e.setBackground(Color.LIGHT_GRAY));

		frame.setSize(600, 600);
		frame.setVisible(true);
	}

	public ChartData createData(String title) {
		ChartData chartData = new ChartData();
		valueEntries.put(title, chartData);
		return chartData;
	}

	public double computeMaxValue() {
		return valueEntries.values().stream().flatMapToDouble(t -> t.values.stream().mapToDouble(Double::doubleValue)).max().orElse(maxValue);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (valueEntries == null || valueEntries.size() < 1) {
			return;
		}

		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(super.getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width = getWidth();
		int height = getHeight();
		int centerX = width / 2;
		int centerY = height / 2;
		int radius = Math.min(width, height) / 2;

		// Draw axes and grid
		int numAxes = titleEntries.size();
		double angleStep = 2 * Math.PI / numAxes;

		g2d.translate(centerX, centerY);

		double scale = useFixedPadding ? Math.min(width - 2 * fixedPadding, height - 2 * fixedPadding) / (2.0 * radius) : this.scale;
		g2d.scale(scale, scale);

		// Major axis (entries)
		g2d.setColor(majorAxisColor);
		for (int i = 0; i < numAxes; i++) {
			double angle = i * angleStep;
			int x = (int) (radius * Math.cos(angle));
			int y = (int) (radius * Math.sin(angle));
			g2d.drawLine(0, 0, x, y);
		}

		double maxValue = overrideMaxValue ? this.maxValue : computeMaxValue();

		// Minor axis (values)
		if (useMinorAxisSteps) {
			for (double cvalue = minorAxisStep; cvalue <= maxValue; cvalue += minorAxisStep) {
				double levelRadius = radius * cvalue / maxValue;

				Polygon polygon = new Polygon();
				for (int i = 0; i < numAxes; i++) {
					double angle = i * angleStep;
					int x = (int) (levelRadius * Math.cos(angle));
					int y = (int) (levelRadius * Math.sin(angle));
					polygon.addPoint(x, y);
				}

				g2d.setColor(minorAxisColor);
				g2d.draw(polygon);

				if (annotateMinorAxis) {
					g2d.setColor(annotationColor);
					g2d.drawString(Double.toString((double) cvalue), polygon.xpoints[0], polygon.ypoints[0]);
				}
			}
		} else {
			for (int level = 1; level <= minorAxisCount; level++) {
				double levelRadius = radius * level / minorAxisCount;

				Polygon polygon = new Polygon();
				for (int i = 0; i < numAxes; i++) {
					double angle = i * angleStep;
					int x = (int) (levelRadius * Math.cos(angle));
					int y = (int) (levelRadius * Math.sin(angle));
					polygon.addPoint(x, y);
				}

				g2d.setColor(minorAxisColor);
				g2d.draw(polygon);

				if (annotateMinorAxis) {
					g2d.setColor(annotationColor);
					g2d.drawString(Double.toString((double) level / minorAxisCount * maxValue), polygon.xpoints[0], polygon.ypoints[0] + g2d.getFontMetrics().getHeight());
				}
			}
		}

		// annotate major axis
		for (int i = 0; i < numAxes; i++) {
			double angle = i * angleStep;

			g2d.rotate(angle);
			g2d.drawString(titleEntries.get(i), radius, 0);
			g2d.rotate(-angle);
		}

		// Draw radar chart
		for (Entry<String, ChartData> eScd : valueEntries.entrySet()) {

			final String entryTitle = eScd.getKey();
			final ChartData cd = eScd.getValue();

			if (cd.values.size() < titleEntries.size()) {
				throw new IndexOutOfBoundsException("Not enough values for entry: " + entryTitle + ", expected " + titleEntries.size() + " but got " + cd.values.size());
			}

			Polygon radarPolygon = new Polygon();
			for (int i = 0; i < numAxes; i++) {
				double value = cd.getValue(i) / maxValue;
				double angle = i * angleStep;
				int x = (int) (value * radius * Math.cos(angle));
				int y = (int) (value * radius * Math.sin(angle));
				radarPolygon.addPoint(x, y);

			}

			if (cd.fill) {
				g2d.setColor(cd.fillColor);
				g2d.fill(radarPolygon);
			}

			g2d.setColor(cd.borderColor);
			g2d.draw(radarPolygon);
		}
	}

	public class ChartData {

		protected List<Double> values;
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

	public class JRadarChartLegend extends JComponent {

		private final boolean vertical;
		private final boolean wrap;

		public JRadarChartLegend(boolean vertical, boolean wrap) {
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

	protected JComponent createLegend(boolean vertical, boolean wrap) {
		return new JRadarChartLegend(vertical, wrap);
	}

	public void overrideMaxValue(double maxValue) {
		this.overrideMaxValue = true;
		this.maxValue = maxValue;
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

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
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

	public List<String> getTitleEntries() {
		return titleEntries;
	}

	public void setTitleEntries(List<String> titleEntries) {
		this.titleEntries = titleEntries;
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

}
