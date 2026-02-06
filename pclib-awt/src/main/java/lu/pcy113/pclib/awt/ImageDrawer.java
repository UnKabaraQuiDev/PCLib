package lu.pcy113.pclib.awt;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageDrawer {

	private BufferedImage image;
	private Graphics2D graphics;
	private int width;
	private int height;
	private boolean isCyclic;

	public ImageDrawer(int type, int resX, int resY) {
		this.width = resX;
		this.height = resY;
		this.isCyclic = false;

		image = new BufferedImage(width, height, type);
		graphics = image.createGraphics();

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		graphics.setComposite(new BrightnessComposite());
	}

	public void draw(Shape shape) {
		graphics.draw(shape);
		if (isCyclic) {
			drawCyclic(shape, false);
		}
	}

	public void fill(Shape shape) {
		graphics.fill(shape);
		if (isCyclic) {
			drawCyclic(shape, true);
		}
	}

	public void setCyclic(boolean isCyclic) {
		this.isCyclic = isCyclic;
	}

	public void close(String outputPath) throws IOException {
		graphics.dispose();
		File outputFile = new File(outputPath);
		ImageIO.write(image, "png", outputFile);
	}

	public void close() {
		graphics.dispose();
	}

	private void drawCyclic(Shape shape, boolean fill) {
		AffineTransform transform = new AffineTransform();

		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (dx == 0 && dy == 0)
					continue;
				transform.setToTranslation(dx * width, dy * height);
				Shape translatedShape = transform.createTransformedShape(shape);
				if (fill) {
					graphics.fill(translatedShape);
				} else {
					graphics.draw(translatedShape);
				}
			}
		}
	}

	public BufferedImage getImage() {
		return image;
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		draw(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.OPEN));
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		draw(new Line2D.Double(x1, y1, x2, y2));
	}

	public void drawOval(int x, int y, int width, int height) {
		draw(new Ellipse2D.Double(x, y, width, height));
	}

	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		draw(new Polygon(xPoints, yPoints, nPoints));
	}

	public void drawPolygon(Polygon p) {
		draw(p);
	}

	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		draw(new Polyline(xPoints, yPoints, nPoints));
	}

	public void drawRect(int x, int y, int width, int height) {
		draw(new Rectangle(x, y, width, height));
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		draw(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
	}

	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		fill(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.OPEN));
	}

	public void fillOval(int x, int y, int width, int height) {
		draw(new Ellipse2D.Double(x, y, width, height));
	}

	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		fill(new Polygon(xPoints, yPoints, nPoints));
	}

	public void fillPolygon(Polygon p) {
		fill(p);
	}

	public void fillRect(int x, int y, int width, int height) {
		fill(new Rectangle(x, y, width, height));
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		fill(new RoundRectangle2D.Double(x, y, width, height, arcHeight, arcHeight));
	}

	public Color getBackground() {
		return graphics.getBackground();
	}

	public Shape getClip() {
		return graphics.getClip();
	}

	public Rectangle getClipBounds() {
		return graphics.getClipBounds();
	}

	public Rectangle getClipBounds(Rectangle r) {
		return graphics.getClipBounds(r);
	}

	public Color getColor() {
		return graphics.getColor();
	}

	public Composite getComposite() {
		return graphics.getComposite();
	}

	public GraphicsConfiguration getDeviceConfiguration() {
		return graphics.getDeviceConfiguration();
	}

	public Font getFont() {
		return graphics.getFont();
	}

	public FontMetrics getFontMetrics() {
		return graphics.getFontMetrics();
	}

	public FontMetrics getFontMetrics(Font f) {
		return graphics.getFontMetrics(f);
	}

	public FontRenderContext getFontRenderContext() {
		return graphics.getFontRenderContext();
	}

	public Paint getPaint() {
		return graphics.getPaint();
	}

	public Object getRenderingHint(Key hintKey) {
		return graphics.getRenderingHint(hintKey);
	}

	public RenderingHints getRenderingHints() {
		return graphics.getRenderingHints();
	}

	public Stroke getStroke() {
		return graphics.getStroke();
	}

	public AffineTransform getTransform() {
		return graphics.getTransform();
	}

	public int hashCode() {
		return graphics.hashCode();
	}

	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		return graphics.hit(rect, s, onStroke);
	}

	public boolean hitClip(int x, int y, int width, int height) {
		return graphics.hitClip(x, y, width, height);
	}

	public void rotate(double theta, double x, double y) {
		graphics.rotate(theta, x, y);
	}

	public void rotate(double theta) {
		graphics.rotate(theta);
	}

	public void scale(double sx, double sy) {
		graphics.scale(sx, sy);
	}

	public void setClip(int x, int y, int width, int height) {
		graphics.setClip(x, y, width, height);
	}

	public void setClip(Shape clip) {
		graphics.setClip(clip);
	}

	public void setComposite(Composite comp) {
		graphics.setComposite(comp);
	}

	public void setPaint(Paint paint) {
		graphics.setPaint(paint);
	}

	public void setPaintMode() {
		graphics.setPaintMode();
	}

	public void setRenderingHint(Key hintKey, Object hintValue) {
		graphics.setRenderingHint(hintKey, hintValue);
	}

	public void setRenderingHints(Map<?, ?> hints) {
		graphics.setRenderingHints(hints);
	}

	public void setTransform(AffineTransform Tx) {
		graphics.setTransform(Tx);
	}

	public void setXORMode(Color c1) {
		graphics.setXORMode(c1);
	}

	public void shear(double shx, double shy) {
		graphics.shear(shx, shy);
	}

	public void transform(AffineTransform Tx) {
		graphics.transform(Tx);
	}

	public void translate(double tx, double ty) {
		graphics.translate(tx, ty);
	}

	public void translate(int x, int y) {
		graphics.translate(x, y);
	}

	public void setBackground(Color color) {
		graphics.setBackground(color);
	}

	public void setColor(Color c) {
		graphics.setColor(c);
	}

	public void setFont(Font font) {
		graphics.setFont(font);
	}

	public void setStroke(Stroke s) {
		graphics.setStroke(s);
	}

	public void clearRect(int x, int y, int width, int height) {
		graphics.clearRect(x, y, width, height);
	}

	public void clip(Shape s) {
		graphics.clip(s);
	}

}
