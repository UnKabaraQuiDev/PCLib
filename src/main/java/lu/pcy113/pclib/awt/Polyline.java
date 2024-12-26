package lu.pcy113.pclib.awt;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Polyline implements Shape {

	private final Path2D path;

	public Polyline(double[] xPoints, double[] yPoints, int nPoints) {
		if (xPoints == null || yPoints == null || xPoints.length != yPoints.length || nPoints > xPoints.length) {
			throw new IllegalArgumentException("Invalid points or length.");
		}

		path = new Path2D.Double();
		if (nPoints > 0) {
			path.moveTo(xPoints[0], yPoints[0]);
			for (int i = 1; i < nPoints; i++) {
				path.lineTo(xPoints[i], yPoints[i]);
			}
		}
	}

	public Polyline(int[] xPoints, int[] yPoints, int nPoints) {
		if (xPoints == null || yPoints == null || xPoints.length != yPoints.length || nPoints > xPoints.length) {
			throw new IllegalArgumentException("Invalid points or length.");
		}

		path = new Path2D.Double();
		if (nPoints > 0) {
			path.moveTo(xPoints[0], yPoints[0]);
			for (int i = 1; i < nPoints; i++) {
				path.lineTo(xPoints[i], yPoints[i]);
			}
		}
	}

	@Override
	public Rectangle getBounds() {
		return path.getBounds();
	}

	@Override
	public Rectangle2D getBounds2D() {
		return path.getBounds2D();
	}

	@Override
	public boolean contains(double x, double y) {
		return path.contains(x, y);
	}

	@Override
	public boolean contains(Point2D p) {
		return path.contains(p);
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return path.intersects(x, y, w, h);
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return path.intersects(r);
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return path.contains(x, y, w, h);
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return path.contains(r);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return path.getPathIterator(at);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return path.getPathIterator(at, flatness);
	}

}