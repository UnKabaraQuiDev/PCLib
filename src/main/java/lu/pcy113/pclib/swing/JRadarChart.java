package lu.pcy113.pclib.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import lu.pcy113.pclib.datastructure.pair.Pair;

public class JRadarChart extends JComponent {

	private List<Pair<String, Double>> entries;

	private boolean filled = true;
	private Color fillColor = new Color(100, 150, 255, 100);

	private Color borderColor = Color.BLUE;
	private Color majorAxisColor = Color.BLACK;
	private Color minorAxisColor = Color.DARK_GRAY;

	private boolean overrideMaxValue = false;
	private double maxValue = 10;

	private boolean useMinorAxisSteps = true;
	private int minorAxisCount = 4;
	private double minorAxisStep = 1;

	private Color annotationColor = Color.BLACK;
	private boolean annotateMinorAxis = true;

	private boolean useFixedPadding = false;
	private double scale = 0.9;
	private int fixedPadding = 50;

	public JRadarChart(List<Pair<String, Double>> entries, boolean filled, Color fillColor, Color borderColor, Color majorAxisColor, Color minorAxisColor) {
		this.entries = entries;
		this.filled = filled;
		this.fillColor = fillColor;
		this.borderColor = borderColor;
		this.majorAxisColor = majorAxisColor;
		this.minorAxisColor = minorAxisColor;
	}

	public JRadarChart(List<Pair<String, Double>> entries) {
		this.entries = entries;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();

		List<Pair<String, Double>> data = Arrays.asList(new Pair<>("Metric A", 0.8), new Pair<>("Metric B", 2.0), new Pair<>("Metric C", 0.9), new Pair<>("Metric D", 0.7), new Pair<>("Metric E", 5.0));

		// Create radar chart
		JRadarChart radarChart = new JRadarChart(data);

		radarChart.setUseMinorAxisSteps(false);

		frame.getContentPane().add(radarChart);

		frame.setSize(600, 600);
		frame.setVisible(true);
	}

	public double computeMaxValue() {
		return entries.stream().mapToDouble(Pair<String, Double>::getValue).max().orElse(maxValue);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (entries == null || entries.size() < 1) {
			return;
		}

		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width = getWidth();
		int height = getHeight();
		int centerX = width / 2;
		int centerY = height / 2;
		int radius = Math.min(width, height) / 2;

		// Draw axes and grid
		int numAxes = entries.size();
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
			g2d.drawString(entries.get(i).getKey(), radius, 0);
			g2d.rotate(-angle);
		}

		// Draw radar chart
		Polygon radarPolygon = new Polygon();
		for (int i = 0; i < numAxes; i++) {
			Pair<String, Double> entry = entries.get(i);
			double value = entry.getValue() / maxValue;
			double angle = i * angleStep;
			int x = (int) (value * radius * Math.cos(angle));
			int y = (int) (value * radius * Math.sin(angle));
			radarPolygon.addPoint(x, y);

		}

		if (filled) {
			g2d.setColor(fillColor);
			g2d.fill(radarPolygon);
		}

		g2d.setColor(borderColor);
		g2d.draw(radarPolygon);
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

	public boolean isFilled() {
		return filled;
	}

	public void setFilled(boolean filled) {
		this.filled = filled;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
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

}
