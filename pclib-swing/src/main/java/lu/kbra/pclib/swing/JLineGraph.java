package lu.kbra.pclib.swing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.IntToDoubleFunction;

import javax.swing.JComponent;
import javax.swing.JFrame;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.datastructure.pair.Pair;

public class JLineGraph extends JComponent {

	private static final long serialVersionUID = 7877997589403595065L;
	private boolean _filled = true, _border = true;
	private Color _fillColor = new Color(0, 0, 128, 128), _borderColor = Color.BLUE;
	private float _borderWidth = 2;

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

	private Map<String, LineChartData> valueEntries = new LinkedHashMap<>();

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

		for (final Entry<String, LineChartData> eScd : this.valueEntries.entrySet()) {

//			final String entryTitle = eScd.getKey();
			final LineChartData cd = eScd.getValue();

			if (cd.getLength() < 1) {
				continue;
			}

			final double widthStep = (double) width / cd.getLength();

			Shape valuesShape;
			if (cd instanceof ChartData) {
				final ChartData rcd = (ChartData) cd;
				if (cd.isFill()) {
					final Polygon polygon = new Polygon();

					polygon.addPoint(0, (int) PCUtils.map(0, maxValue, minValue, 0, height));

					for (int i = 0; i < cd.getLength(); i++) {
						final int yLevel = (int) PCUtils.map(rcd.getValue(i), maxValue, minValue, 0, height);
						polygon.addPoint((int) (widthStep * i), yLevel);
					}

					polygon.addPoint((int) (widthStep * (cd.getLength() - 1)), (int) PCUtils.map(0, maxValue, minValue, 0, height));

					valuesShape = polygon;
				} else {
					final Path2D path = new Path2D.Double();

					for (int i = 0; i < cd.getLength(); i++) {
						final int yLevel = (int) PCUtils.map(rcd.getValue(i), maxValue, minValue, 0, height);
						final float x = (float) (widthStep * i);

						if (i == 0) {
							path.moveTo(x, yLevel);
						} else {
							path.lineTo(x, yLevel);
						}
					}

					valuesShape = path;
				}
			} else if (cd instanceof RangeChartData) {
				final RangeChartData rcd = (RangeChartData) cd;

				if (cd.isFill()) {
					final Polygon polygon = new Polygon();

					for (int i = 0; i < rcd.getLength(); i++) {
						final double max = rcd.getMaxValue(i);
						final int y = (int) PCUtils.map(max, maxValue, minValue, 0, height);
						final int x = (int) (widthStep * i);
						polygon.addPoint(x, y);
					}

					for (int i = rcd.getLength() - 1; i >= 0; i--) {
						final double min = rcd.getMinValue(i);
						final int y = (int) PCUtils.map(min, maxValue, minValue, 0, height);
						final int x = (int) (widthStep * i);
						polygon.addPoint(x, y);
					}

					valuesShape = polygon;
				} else {
					final Path2D path = new Path2D.Float();

					// upper line (max)
					for (int i = 0; i < rcd.getLength(); i++) {
						final double max = rcd.getMaxValue(i);
						final int y = (int) PCUtils.map(max, maxValue, minValue, 0, height);
						final float x = (float) (widthStep * i);

						if (i == 0) {
							path.moveTo(x, y);
						} else {
							path.lineTo(x, y);
						}
					}

					// lower line (min)
					for (int i = 0; i < rcd.getLength(); i++) {
						final double min = rcd.getMinValue(i);
						final int y = (int) PCUtils.map(min, maxValue, minValue, 0, height);
						final float x = (float) (widthStep * i);

						path.moveTo(x, y);
						if (i > 0) {
							final double prevMin = rcd.getMinValue(i - 1);
							final int py = (int) PCUtils.map(prevMin, maxValue, minValue, 0, height);
							final float px = (float) (widthStep * (i - 1));
							path.lineTo(px, py);
							path.lineTo(x, y);
						}
					}

					valuesShape = path;
				}
			} else {
				throw new UnsupportedOperationException("Unknown Chart data: " + cd.getClass());
			}

			if (cd.isFill()) {
				g2d.setColor(cd.getFillColor());
				g2d.fill(valuesShape);
			}

			if (cd.isBorder()) {
				g2d.setStroke(new BasicStroke(cd.getBorderWidth()));
				g2d.setColor(cd.getBorderColor());
				g2d.draw(valuesShape);
			}
		}
	}

	protected double computeMaxValue() {
		return this.valueEntries.values().stream().mapToDouble(LineChartData::computeMaxValue).max().orElse(1);
	}

	protected double computeMinValue() {
		return this.valueEntries.values().stream().mapToDouble(LineChartData::computeMinValue).min().orElse(0);
	}

	public interface LineChartData {

		float getBorderWidth();

		boolean isBorder();

		Color getBorderColor();

		Color getFillColor();

		boolean isFill();

		double computeMaxValue();

		double computeMinValue();

		int getLength();

	}

	public class ChartData implements LineChartData {

		protected Collection<Double> values = new ArrayList<>();
		protected IntToDoubleFunction valueGetter = i -> ((List<Double>) this.values).get(i);
		protected boolean fill = JLineGraph.this._filled, border = JLineGraph.this._border;
		protected Color fillColor = JLineGraph.this._fillColor, borderColor = JLineGraph.this._borderColor;
		protected float borderWidth = JLineGraph.this._borderWidth;

		public ChartData() {
		}

		public ChartData(final List<Double> values, final boolean fill, final Color fillColor, final Color borderColor) {
			this.values = values;
			this.fill = fill;
			this.fillColor = fillColor;
			this.borderColor = borderColor;
		}

		public ChartData(
				final List<Double> values,
				final boolean fill,
				final boolean border,
				final Color fillColor,
				final Color borderColor,
				final float width) {
			this.values = values;
			this.fill = fill;
			this.border = border;
			this.fillColor = fillColor;
			this.borderColor = borderColor;
			this.borderWidth = width;
		}

		@Override
		public double computeMaxValue() {
			return this.values.parallelStream().mapToDouble(Double::valueOf).max().orElse(0);
		}

		@Override
		public double computeMinValue() {
			return this.values.parallelStream().mapToDouble(Double::valueOf).min().orElse(0);
		}

		@Override
		public int getLength() {
			return this.values.size();
		}

		public double getValue(final int i) {
			return this.valueGetter.applyAsDouble(i);
		}

		public Collection<Double> getValues() {
			return this.values;
		}

		public ChartData setValues(final Collection<Double> values, final IntToDoubleFunction func) {
			this.values = values;
			this.valueGetter = func;
			return this;
		}

		public ChartData setValues(final List<Double> values) {
			this.values = values;
			this.valueGetter = values::get;
			return this;
		}

		@Override
		public boolean isFill() {
			return this.fill;
		}

		public ChartData setFill(final boolean fill) {
			this.fill = fill;
			return this;
		}

		@Override
		public Color getFillColor() {
			return this.fillColor;
		}

		public ChartData setFillColor(final Color fillColor) {
			this.fillColor = fillColor;
			return this;
		}

		@Override
		public Color getBorderColor() {
			return this.borderColor;
		}

		public ChartData setBorderColor(final Color borderColor) {
			this.borderColor = borderColor;
			return this;
		}

		public ChartData setBorder(final boolean border) {
			this.border = border;
			return this;
		}

		@Override
		public boolean isBorder() {
			return this.border;
		}

		public ChartData setBorderWidth(final float width) {
			this.borderWidth = width;
			return this;
		}

		@Override
		public float getBorderWidth() {
			return this.borderWidth;
		}

	}

	public ChartData createSeries(final String title) {
		final ChartData chartData = new ChartData();
		this.valueEntries.put(title, chartData);
		return chartData;
	}

	/**
	 * key < value
	 */
	public class RangeChartData implements LineChartData {

		protected Collection<Pair<Double, Double>> values = new ArrayList<>();
		protected IntToDoubleFunction valueMinGetter = i -> ((List<Pair<Double, Double>>) this.values).get(i).getKey();
		protected IntToDoubleFunction valueMaxGetter = i -> ((List<Pair<Double, Double>>) this.values).get(i).getValue();
		protected boolean fill = JLineGraph.this._filled, border = JLineGraph.this._border;
		protected Color fillColor = JLineGraph.this._fillColor, borderColor = JLineGraph.this._borderColor;
		protected float borderWidth = JLineGraph.this._borderWidth;

		public RangeChartData() {
		}

		public RangeChartData(final List<Pair<Double, Double>> values, final boolean fill, final Color fillColor, final Color borderColor) {
			this.values = values;
			this.fill = fill;
			this.fillColor = fillColor;
			this.borderColor = borderColor;
		}

		public RangeChartData(
				final List<Pair<Double, Double>> values,
				final boolean fill,
				final boolean border,
				final Color fillColor,
				final Color borderColor,
				final float width) {
			this.values = values;
			this.fill = fill;
			this.border = border;
			this.fillColor = fillColor;
			this.borderColor = borderColor;
			this.borderWidth = width;
		}

		@Override
		public double computeMaxValue() {
			return this.values.parallelStream().mapToDouble(Pair::getValue).max().orElse(0);
		}

		@Override
		public double computeMinValue() {
			return this.values.parallelStream().mapToDouble(Pair::getKey).min().orElse(0);
		}

		@Override
		public int getLength() {
			return this.values.size();
		}

		public double getMinValue(final int i) {
			return this.valueMinGetter.applyAsDouble(i);
		}

		public double getMaxValue(final int i) {
			return this.valueMaxGetter.applyAsDouble(i);
		}

		public Collection<Pair<Double, Double>> getValues() {
			return this.values;
		}

		public LineChartData setValues(
				final Collection<Pair<Double, Double>> values,
				final IntToDoubleFunction funcMin,
				final IntToDoubleFunction funcMax) {
			this.values = values;
			this.valueMinGetter = funcMin;
			this.valueMaxGetter = funcMax;
			return this;
		}

		public LineChartData setValues(final List<Pair<Double, Double>> values) {
			this.values = values;
			this.valueMinGetter = i -> values.get(i).getKey();
			this.valueMaxGetter = i -> values.get(i).getValue();
			return this;
		}

		@Override
		public boolean isFill() {
			return this.fill;
		}

		public LineChartData setFill(final boolean fill) {
			this.fill = fill;
			return this;
		}

		@Override
		public Color getFillColor() {
			return this.fillColor;
		}

		public LineChartData setFillColor(final Color fillColor) {
			this.fillColor = fillColor;
			return this;
		}

		@Override
		public Color getBorderColor() {
			return this.borderColor;
		}

		public LineChartData setBorderColor(final Color borderColor) {
			this.borderColor = borderColor;
			return this;
		}

		public LineChartData setBorder(final boolean border) {
			this.border = border;
			return this;
		}

		@Override
		public boolean isBorder() {
			return this.border;
		}

		public LineChartData setBorderWidth(final float width) {
			this.borderWidth = width;
			return this;
		}

		@Override
		public float getBorderWidth() {
			return this.borderWidth;
		}

	}

	public RangeChartData createRangeSeries(final String title) {
		final RangeChartData chartData = new RangeChartData();
		this.valueEntries.put(title, chartData);
		return chartData;
	}

	public class JLineGraphLegend extends JComponent {

		private static final long serialVersionUID = 3613580467753621672L;
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

			for (final Entry<String, LineChartData> item : JLineGraph.this.valueEntries.entrySet()) {
				final String title = item.getKey();
				final Color fillColor = item.getValue().isFill() ? item.getValue().getFillColor() : item.getValue().getBorderColor(),
						borderColor = item.getValue().getBorderColor();

				// Draw color square
				g2d.setColor(fillColor);
				g2d.fillRect(x, y, squareSize, squareSize);
				g2d.setColor(borderColor);
				g2d.drawRect(x, y, squareSize, squareSize);

				// Draw title text
				final int textX = x + squareSize + padding;
				final int textY = y + squareSize / 2 + fm.getAscent() / 2 - 2;
				g2d.setColor(JLineGraph.this.annotationColor);
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
				height = Math.max(squareSize + paddingSize * 2, fm.getHeight()) * JLineGraph.this.valueEntries.size() + paddingSize;
				width = squareSize + paddingSize * 3
						+ JLineGraph.this.valueEntries.keySet().stream().mapToInt(fm::stringWidth).max().orElse(0);
			} else {
				width = (squareSize + paddingSize) * JLineGraph.this.valueEntries.size() + paddingSize;
				height = Math.max(squareSize + paddingSize * 2, fm.getHeight());
			}

			return new Dimension(width, height);
		}

	}

	public JComponent createLegend(final boolean vertical, final boolean wrap) {
		return new JLineGraphLegend(vertical, wrap);
	}

	public void overrideMaxValue(final double maxValue) {
		this.overrideMaxValue = true;
		this.maxValue = maxValue;
	}

	public void overrideMinValue(final double minValue) {
		this.overrideMinValue = true;
		this.minValue = minValue;
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

	public Map<String, LineChartData> getValueEntries() {
		return this.valueEntries;
	}

	public void setValueEntries(final Map<String, LineChartData> valueEntries) {
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

	public boolean isNextBorder() {
		return this._border;
	}

	public void setNextBorder(final boolean _border) {
		this._border = _border;
	}

	public float getNextBorderWidth() {
		return this._borderWidth;
	}

	public void setNextBorderWidth(final float f) {
		this._borderWidth = f;
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
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		final JLineGraph graph = new JLineGraph();

		final int MAX = 100;

		final List<Double> values = new ArrayList<>(MAX);
		for (int i = 0; i <= MAX; i++) {
			values.add(2 * (double) i / MAX - 1);
		}
		graph.createSeries("Entry 1").setValues(values);
		graph.createSeries("Entry 2")
				.setValues(PCUtils.reversed(new ArrayList<>(values)))
				.setFillColor(new Color(128, 0, 0, 128))
				.setBorderColor(Color.RED);
		graph.createSeries("Entry 3")
				.setValues(PCUtils.shuffled(new ArrayList<>(values)))
				.setFillColor(new Color(0, 128, 0, 128))
				.setBorderColor(Color.GREEN);

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
