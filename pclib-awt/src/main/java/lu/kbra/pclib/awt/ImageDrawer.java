package lu.kbra.pclib.awt;

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

	private final BufferedImage image;
	private final Graphics2D graphics;
	private final int width;
	private final int height;
	private boolean isCyclic;

	public ImageDrawer(final int type, final int resX, final int resY) {
		this.width = resX;
		this.height = resY;
		this.isCyclic = false;

		this.image = new BufferedImage(this.width, this.height, type);
		this.graphics = this.image.createGraphics();

		this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		this.graphics.setComposite(new BrightnessComposite());
	}

	public void draw(final Shape shape) {
		this.graphics.draw(shape);
		if (this.isCyclic) {
			this.drawCyclic(shape, false);
		}
	}

	public void fill(final Shape shape) {
		this.graphics.fill(shape);
		if (this.isCyclic) {
			this.drawCyclic(shape, true);
		}
	}

	public void setCyclic(final boolean isCyclic) {
		this.isCyclic = isCyclic;
	}

	public void close(final String outputPath) throws IOException {
		this.graphics.dispose();
		final File outputFile = new File(outputPath);
		ImageIO.write(this.image, "png", outputFile);
	}

	public void close() {
		this.graphics.dispose();
	}

	private void drawCyclic(final Shape shape, final boolean fill) {
		final AffineTransform transform = new AffineTransform();

		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (dx == 0 && dy == 0) {
					continue;
				}
				transform.setToTranslation(dx * this.width, dy * this.height);
				final Shape translatedShape = transform.createTransformedShape(shape);
				if (fill) {
					this.graphics.fill(translatedShape);
				} else {
					this.graphics.draw(translatedShape);
				}
			}
		}
	}

	public BufferedImage getImage() {
		return this.image;
	}

	public void drawArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
		this.draw(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.OPEN));
	}

	public void drawLine(final int x1, final int y1, final int x2, final int y2) {
		this.draw(new Line2D.Double(x1, y1, x2, y2));
	}

	public void drawOval(final int x, final int y, final int width, final int height) {
		this.draw(new Ellipse2D.Double(x, y, width, height));
	}

	public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
		this.draw(new Polygon(xPoints, yPoints, nPoints));
	}

	public void drawPolygon(final Polygon p) {
		this.draw(p);
	}

	public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
		this.draw(new Polyline(xPoints, yPoints, nPoints));
	}

	public void drawRect(final int x, final int y, final int width, final int height) {
		this.draw(new Rectangle(x, y, width, height));
	}

	public void drawRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
		this.draw(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
	}

	public void fillArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
		this.fill(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.OPEN));
	}

	public void fillOval(final int x, final int y, final int width, final int height) {
		this.draw(new Ellipse2D.Double(x, y, width, height));
	}

	public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
		this.fill(new Polygon(xPoints, yPoints, nPoints));
	}

	public void fillPolygon(final Polygon p) {
		this.fill(p);
	}

	public void fillRect(final int x, final int y, final int width, final int height) {
		this.fill(new Rectangle(x, y, width, height));
	}

	public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
		this.fill(new RoundRectangle2D.Double(x, y, width, height, arcHeight, arcHeight));
	}

	public Color getBackground() {
		return this.graphics.getBackground();
	}

	public Shape getClip() {
		return this.graphics.getClip();
	}

	public Rectangle getClipBounds() {
		return this.graphics.getClipBounds();
	}

	public Rectangle getClipBounds(final Rectangle r) {
		return this.graphics.getClipBounds(r);
	}

	public Color getColor() {
		return this.graphics.getColor();
	}

	public Composite getComposite() {
		return this.graphics.getComposite();
	}

	public GraphicsConfiguration getDeviceConfiguration() {
		return this.graphics.getDeviceConfiguration();
	}

	public Font getFont() {
		return this.graphics.getFont();
	}

	public FontMetrics getFontMetrics() {
		return this.graphics.getFontMetrics();
	}

	public FontMetrics getFontMetrics(final Font f) {
		return this.graphics.getFontMetrics(f);
	}

	public FontRenderContext getFontRenderContext() {
		return this.graphics.getFontRenderContext();
	}

	public Paint getPaint() {
		return this.graphics.getPaint();
	}

	public Object getRenderingHint(final Key hintKey) {
		return this.graphics.getRenderingHint(hintKey);
	}

	public RenderingHints getRenderingHints() {
		return this.graphics.getRenderingHints();
	}

	public Stroke getStroke() {
		return this.graphics.getStroke();
	}

	public AffineTransform getTransform() {
		return this.graphics.getTransform();
	}

	@Override
	public int hashCode() {
		return this.graphics.hashCode();
	}

	public boolean hit(final Rectangle rect, final Shape s, final boolean onStroke) {
		return this.graphics.hit(rect, s, onStroke);
	}

	public boolean hitClip(final int x, final int y, final int width, final int height) {
		return this.graphics.hitClip(x, y, width, height);
	}

	public void rotate(final double theta, final double x, final double y) {
		this.graphics.rotate(theta, x, y);
	}

	public void rotate(final double theta) {
		this.graphics.rotate(theta);
	}

	public void scale(final double sx, final double sy) {
		this.graphics.scale(sx, sy);
	}

	public void setClip(final int x, final int y, final int width, final int height) {
		this.graphics.setClip(x, y, width, height);
	}

	public void setClip(final Shape clip) {
		this.graphics.setClip(clip);
	}

	public void setComposite(final Composite comp) {
		this.graphics.setComposite(comp);
	}

	public void setPaint(final Paint paint) {
		this.graphics.setPaint(paint);
	}

	public void setPaintMode() {
		this.graphics.setPaintMode();
	}

	public void setRenderingHint(final Key hintKey, final Object hintValue) {
		this.graphics.setRenderingHint(hintKey, hintValue);
	}

	public void setRenderingHints(final Map<?, ?> hints) {
		this.graphics.setRenderingHints(hints);
	}

	public void setTransform(final AffineTransform Tx) {
		this.graphics.setTransform(Tx);
	}

	public void setXORMode(final Color c1) {
		this.graphics.setXORMode(c1);
	}

	public void shear(final double shx, final double shy) {
		this.graphics.shear(shx, shy);
	}

	public void transform(final AffineTransform Tx) {
		this.graphics.transform(Tx);
	}

	public void translate(final double tx, final double ty) {
		this.graphics.translate(tx, ty);
	}

	public void translate(final int x, final int y) {
		this.graphics.translate(x, y);
	}

	public void setBackground(final Color color) {
		this.graphics.setBackground(color);
	}

	public void setColor(final Color c) {
		this.graphics.setColor(c);
	}

	public void setFont(final Font font) {
		this.graphics.setFont(font);
	}

	public void setStroke(final Stroke s) {
		this.graphics.setStroke(s);
	}

	public void clearRect(final int x, final int y, final int width, final int height) {
		this.graphics.clearRect(x, y, width, height);
	}

	public void clip(final Shape s) {
		this.graphics.clip(s);
	}

}
