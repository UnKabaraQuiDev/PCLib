package lu.pcy113.pclib.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;

import lu.pcy113.pclib.PCUtils;

public class JLineGraph extends JComponent {

	private Map<String, Double> data;
	private Color lineColor = Color.RED;

	public JLineGraph(HashMap<String, Double> data) {
		this.data = data;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// Setup rendering hints for smooth lines
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width = getWidth();
		int height = getHeight();

		int padding = 50;
		int graphWidth = width - 2 * padding;
		int graphHeight = height - 2 * padding;

		// Determine value range
		double maxValue = data.values().stream().mapToDouble(v -> v).max().orElse(1);
		double minValue = Math.min(0, data.values().stream().mapToDouble(v -> v).min().orElse(0));

		double range = maxValue - minValue;

		// Draw horizontal lines and labels
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.setStroke(new BasicStroke(1));

		int numHorizontalLines = 10;
		for (int i = 0; i <= numHorizontalLines; i++) {
			int y = padding + (int) (graphHeight - i * (graphHeight / (double) numHorizontalLines));
			double value = minValue + i * (range / numHorizontalLines);
			g2d.drawLine(padding, y, width - padding, y);

			// Draw labels on the left
			g2d.setColor(Color.BLACK);
			g2d.drawString(String.format("%.2f", value), padding - 40, y + 5);
			g2d.setColor(Color.LIGHT_GRAY);
		}

		// Draw axes
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2));
		final int y0 = data.size() > 0 ? (int) PCUtils.map(0, maxValue, minValue, 0, height) : height-padding;
		g2d.drawLine(padding, y0, width - padding, y0); // X-axis
		g2d.drawLine(padding, padding, padding, height - padding); // Y-axis

		if(data.size() <= 1) {
			return;
		}
		
		// Draw the graph line
		g2d.setColor(lineColor);
		g2d.setStroke(new BasicStroke(2));

		int pointCount = data.size();
		if (pointCount > 1) {
			int previousX = 0, previousY = 0;
			int index = 0;
			for (Map.Entry<String, Double> entry : data.entrySet()) {
				double value = entry.getValue();
				int x = padding + index * (graphWidth / (pointCount - 1));
				int y = padding + (int) (graphHeight - ((value - minValue) / range * graphHeight));

				if (index > 0) {
					g2d.drawLine(previousX, previousY, x, y);
				}

				previousX = x;
				previousY = y;
				index++;
			}
		}

		// Draw x-axis labels
		g2d.setColor(Color.BLACK);
		int index = 0;
		for (String label : data.keySet()) {
			int x = padding + index * (graphWidth / (pointCount - 1));
			g2d.drawString(label, x - 10, y0 + 20);
			index++;
		}
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public Map<String, Double> getData() {
		return data;
	}

	public void setData(Map<String, Double> data) {
		this.data = data;
	}
	
	public static void main(String[] args) {
		// Sample data
		LinkedHashMap<String, Double> sampleData = new LinkedHashMap<>();
		for (int i = 0; i < 100; i++) {
			sampleData.put("" + i, Math.sin((double) i / 10) < 0 ? -0.5 : Math.sin((double) i /10));
		}

		// Create and show frame
		JFrame frame = new JFrame("Line Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.add(new JLineGraph(sampleData));
		frame.setVisible(true);
		
		frame = new JFrame("Line Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.add(new JLineGraph(new LinkedHashMap<String, Double>()));
		frame.setVisible(true);
	}
}
