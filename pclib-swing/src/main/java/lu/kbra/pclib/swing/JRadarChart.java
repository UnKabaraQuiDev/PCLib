package lu.kbra.pclib.swing;

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
import javax.swing.WindowConstants;

import lu.kbra.pclib.PCUtils;

public class JRadarChart extends JComponent {

	public class ChartData {

		protected List<Double> values;
		protected boolean fill = JRadarChart.this.nextFilled;
		protected Color fillColor = JRadarChart.this.nextFillColor, borderColor = JRadarChart.this.nextBorderColor;

		public ChartData() {
		}

		public ChartData(final List<Double> values, final boolean fill, final Color fillColor, final Color borderColor) {
			this.values = values;
			this.fill = fill;
			this.fillColor = fillColor;
			this.borderColor = borderColor;
		}

		public Color getBorderColor() {
			return this.borderColor;
		}

		public Color getFillColor() {
			return this.fillColor;
		}

		public double getValue(final int i) {
			return this.values.get(i);
		}

		public List<Double> getValues() {
			return this.values;
		}

		public boolean isFill() {
			return this.fill;
		}

		public ChartData setBorderColor(final Color borderColor) {
			this.borderColor = borderColor;
			return this;
		}

		public ChartData setFill(final boolean fill) {
			this.fill = fill;
			return this;
		}

		public ChartData setFillColor(final Color fillColor) {
			this.fillColor = fillColor;
			return this;
		}

		public ChartData setValues(final List<Double> values) {
			this.values = values;
			return this;
		}

	}

	public class JRadarChartLegend extends JComponent {

		private static final long serialVersionUID = -414038432554747967L;
		private final boolean vertical;
		private final boolean wrap;

		public JRadarChartLegend(final boolean vertical, final boolean wrap) {
			this.vertical = vertical;
			this.wrap = wrap;
		}

		@Override
		public Dimension getPreferredSize() {
			final FontMetrics fm = this.getFontMetrics(this.getFont());

			int width = 20;
			int height = 20;

			final int squareSize = 15;
			final int paddingSize = 5;

			if (this.vertical) {
				height = Math.max(squareSize + paddingSize * 2, fm.getHeight()) * JRadarChart.this.valueEntries.size() + paddingSize;
				width = squareSize + paddingSize * 3
						+ JRadarChart.this.valueEntries.keySet().stream().mapToInt(fm::stringWidth).max().orElse(0);
			} else {
				width = (squareSize + paddingSize) * JRadarChart.this.valueEntries.size() + paddingSize;
				height = Math.max(squareSize + paddingSize * 2, fm.getHeight());
			}

			return new Dimension(width, height);
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

			for (final Entry<String, ChartData> item : JRadarChart.this.valueEntries.entrySet()) {
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
				g2d.setColor(JRadarChart.this.annotationColor);
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

	}

	private static final long serialVersionUID = 2196403827676843080L;

	public static void main(final String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final List<String> titles = new ArrayList<>();

		final int MAX = 100;

		for (int i = 0; i <= MAX; i++) {
			titles.add(Integer.toString(i));
		}

		final JRadarChart radarChart = new JRadarChart(titles);

		final List<Double> values = new ArrayList<>(MAX);
		for (int i = 0; i <= MAX; i++) {
			values.add((double) i / MAX);
		}
		radarChart.createSeries("Entry 1").setValues(values);
		radarChart.createSeries("Entry 2")
				.setValues(PCUtils.reversed(new ArrayList<>(values)))
				.setFillColor(new Color(128, 0, 0, 128))
				.setBorderColor(Color.RED);
		radarChart.createSeries("Entry 3")
				.setValues(PCUtils.shuffled(new ArrayList<>(values)))
				.setFillColor(new Color(0, 128, 0, 128))
				.setBorderColor(Color.GREEN);

		radarChart.setUseMinorAxisSteps(false);
		radarChart.setMinorAxisStep(0.5);

		frame.getContentPane().setLayout(new BorderLayout());

		frame.getContentPane().add(radarChart);

		frame.addMouseWheelListener(new MouseAdapter() {
			@Override
			public void mouseWheelMoved(final MouseWheelEvent e) {
				radarChart.setMinorAxisCount(PCUtils.clampGreaterOrEquals(radarChart.getMinorAxisCount() + e.getWheelRotation(), 1));
				radarChart.repaint();
			}
		});

		frame.getContentPane().add(radarChart.createLegend(false, true), BorderLayout.SOUTH);
		frame.getContentPane().add(radarChart.createLegend(true, true), BorderLayout.EAST);

		Arrays.stream(frame.getContentPane().getComponents())
				.filter(JRadarChartLegend.class::isInstance)
				.forEach(e -> e.setBackground(Color.LIGHT_GRAY));

		frame.setSize(600, 600);
		frame.setVisible(true);
	}

	private List<String> titleEntries;
	private HashMap<String, ChartData> valueEntries;
	private boolean nextFilled = true;

	private Color nextFillColor = new Color(0, 0, 128, 128), nextBorderColor = Color.BLUE;
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

	public JRadarChart() {
		this.valueEntries = new HashMap<>();
	}

	public JRadarChart(final List<String> titleEntries) {
		this.titleEntries = titleEntries;
		this.valueEntries = new HashMap<>();
	}

	public JRadarChart(final List<String> titleEntries, final HashMap<String, ChartData> entries) {
		this.titleEntries = titleEntries;
		this.valueEntries = entries;
	}

	public JRadarChart(
			final List<String> titleEntries,
			final HashMap<String, ChartData> entries,
			final Color majorAxisColor,
			final Color minorAxisColor) {
		this.titleEntries = titleEntries;
		this.valueEntries = entries;
		this.majorAxisColor = majorAxisColor;
		this.minorAxisColor = minorAxisColor;
	}

	public double computeMaxValue() {
		return this.valueEntries.values()
				.stream()
				.flatMapToDouble(t -> t.values.stream().mapToDouble(Double::doubleValue))
				.max()
				.orElse(this.maxValue);
	}

	public JComponent createLegend(final boolean vertical, final boolean wrap) {
		return new JRadarChartLegend(vertical, wrap);
	}

	public ChartData createSeries(final String title) {
		final ChartData chartData = new ChartData();
		this.valueEntries.put(title, chartData);
		return chartData;
	}

	public Color getAnnotationColor() {
		return this.annotationColor;
	}

	public int getFixedPadding() {
		return this.fixedPadding;
	}

	public Color getMajorAxisColor() {
		return this.majorAxisColor;
	}

	public Color getMinorAxisColor() {
		return this.minorAxisColor;
	}

	public int getMinorAxisCount() {
		return this.minorAxisCount;
	}

	public double getMinorAxisStep() {
		return this.minorAxisStep;
	}

	public Color getNextBorderColor() {
		return this.nextBorderColor;
	}

	public Color getNextFillColor() {
		return this.nextFillColor;
	}

	public double getScale() {
		return this.scale;
	}

	public List<String> getTitleEntries() {
		return this.titleEntries;
	}

	public HashMap<String, ChartData> getValueEntries() {
		return this.valueEntries;
	}

	public boolean isAnnotateMinorAxis() {
		return this.annotateMinorAxis;
	}

	public boolean isNextFilled() {
		return this.nextFilled;
	}

	public boolean isUseFixedPadding() {
		return this.useFixedPadding;
	}

	public boolean isUseMinorAxisSteps() {
		return this.useMinorAxisSteps;
	}

	public void overrideMaxValue(final double maxValue) {
		this.overrideMaxValue = true;
		this.maxValue = maxValue;
	}

	public void resetOverrideMaxValue() {
		this.overrideMaxValue = false;
	}

	public void setAnnotateMinorAxis(final boolean annotateMinorAxis) {
		this.annotateMinorAxis = annotateMinorAxis;
	}

	public void setAnnotationColor(final Color annotationColor) {
		this.annotationColor = annotationColor;
	}

	public void setFixedPadding(final int fixedPadding) {
		this.fixedPadding = fixedPadding;
	}

	public void setMajorAxisColor(final Color majorAxisColor) {
		this.majorAxisColor = majorAxisColor;
	}

	public void setMinorAxisColor(final Color minorAxisColor) {
		this.minorAxisColor = minorAxisColor;
	}

	public void setMinorAxisCount(final int minorAxisCount) {
		this.minorAxisCount = minorAxisCount;
	}

	public void setMinorAxisStep(final double minorAxisStep) {
		this.minorAxisStep = minorAxisStep;
	}

	public void setNextBorderColor(final Color nextBorderColor) {
		this.nextBorderColor = nextBorderColor;
	}

	public void setNextFillColor(final Color nextFillColor) {
		this.nextFillColor = nextFillColor;
	}

	public void setNextFilled(final boolean nextFilled) {
		this.nextFilled = nextFilled;
	}

	public void setScale(final double scale) {
		this.scale = scale;
	}

	public void setTitleEntries(final List<String> titleEntries) {
		this.titleEntries = titleEntries;
	}

	public void setUseFixedPadding(final boolean useFixedPadding) {
		this.useFixedPadding = useFixedPadding;
	}

	public void setUseMinorAxisSteps(final boolean useMinorAxisSteps) {
		this.useMinorAxisSteps = useMinorAxisSteps;
	}

	public void setValueEntries(final HashMap<String, ChartData> valueEntries) {
		this.valueEntries = valueEntries;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		if (this.valueEntries == null || this.valueEntries.size() < 1) {
			return;
		}

		final Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(super.getBackground());
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		final int width = this.getWidth();
		final int height = this.getHeight();
		final int centerX = width / 2;
		final int centerY = height / 2;
		final int radius = Math.min(width, height) / 2;

		// Draw axes and grid
		final int numAxes = this.titleEntries.size();
		final double angleStep = 2 * Math.PI / numAxes;

		g2d.translate(centerX, centerY);

		final double scale = this.useFixedPadding ? Math.min(width - 2 * this.fixedPadding, height - 2 * this.fixedPadding) / (2.0 * radius)
				: this.scale;
		g2d.scale(scale, scale);

		// Major axis (entries)
		g2d.setColor(this.majorAxisColor);
		for (int i = 0; i < numAxes; i++) {
			final double angle = i * angleStep;
			final int x = (int) (radius * Math.cos(angle));
			final int y = (int) (radius * Math.sin(angle));
			g2d.drawLine(0, 0, x, y);
		}

		final double maxValue = this.overrideMaxValue ? this.maxValue : this.computeMaxValue();

		// Minor axis (values)
		if (this.useMinorAxisSteps) {
			for (double cvalue = this.minorAxisStep; cvalue <= maxValue; cvalue += this.minorAxisStep) {
				final double levelRadius = radius * cvalue / maxValue;

				final Polygon polygon = new Polygon();
				for (int i = 0; i < numAxes; i++) {
					final double angle = i * angleStep;
					final int x = (int) (levelRadius * Math.cos(angle));
					final int y = (int) (levelRadius * Math.sin(angle));
					polygon.addPoint(x, y);
				}

				g2d.setColor(this.minorAxisColor);
				g2d.draw(polygon);

				if (this.annotateMinorAxis) {
					g2d.setColor(this.annotationColor);
					g2d.drawString(Double.toString(cvalue), polygon.xpoints[0], polygon.ypoints[0]);
				}
			}
		} else {
			for (int level = 1; level <= this.minorAxisCount; level++) {
				final double levelRadius = radius * level / this.minorAxisCount;

				final Polygon polygon = new Polygon();
				for (int i = 0; i < numAxes; i++) {
					final double angle = i * angleStep;
					final int x = (int) (levelRadius * Math.cos(angle));
					final int y = (int) (levelRadius * Math.sin(angle));
					polygon.addPoint(x, y);
				}

				g2d.setColor(this.minorAxisColor);
				g2d.draw(polygon);

				if (this.annotateMinorAxis) {
					g2d.setColor(this.annotationColor);
					g2d.drawString(Double.toString((double) level / this.minorAxisCount * maxValue),
							polygon.xpoints[0],
							polygon.ypoints[0] + g2d.getFontMetrics().getHeight());
				}
			}
		}

		// annotate major axis
		for (int i = 0; i < numAxes; i++) {
			final double angle = i * angleStep;

			g2d.rotate(angle);
			g2d.drawString(this.titleEntries.get(i), radius, 0);
			g2d.rotate(-angle);
		}

		// Draw radar chart
		for (final Entry<String, ChartData> eScd : this.valueEntries.entrySet()) {

			final String entryTitle = eScd.getKey();
			final ChartData cd = eScd.getValue();

			if (cd.values.size() < this.titleEntries.size()) {
				throw new IndexOutOfBoundsException("Not enough values for entry: " + entryTitle + ", expected " + this.titleEntries.size()
						+ " but got " + cd.values.size());
			}

			final Polygon radarPolygon = new Polygon();
			for (int i = 0; i < numAxes; i++) {
				final double value = cd.getValue(i) / maxValue;
				final double angle = i * angleStep;
				final int x = (int) (value * radius * Math.cos(angle));
				final int y = (int) (value * radius * Math.sin(angle));
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

}
