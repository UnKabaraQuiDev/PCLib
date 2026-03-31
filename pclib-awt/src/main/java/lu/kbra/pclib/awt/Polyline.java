package lu.kbra.pclib.awt;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Polyline implements Shape {

	private final Path2D path;

	public Polyline(final double[] xPoints, final double[] yPoints, final int nPoints) {
		if (xPoints == null || yPoints == null || xPoints.length != yPoints.length || nPoints > xPoints.length) {
			throw new IllegalArgumentException("Invalid points or length.");
		}

		this.path = new Path2D.Double();
		if (nPoints > 0) {
			this.path.moveTo(xPoints[0], yPoints[0]);
			for (int i = 1; i < nPoints; i++) {
				this.path.lineTo(xPoints[i], yPoints[i]);
			}
		}
	}

	public Polyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
		if (xPoints == null || yPoints == null || xPoints.length != yPoints.length || nPoints > xPoints.length) {
			throw new IllegalArgumentException("Invalid points or length.");
		}

		this.path = new Path2D.Double();
		if (nPoints > 0) {
			this.path.moveTo(xPoints[0], yPoints[0]);
			for (int i = 1; i < nPoints; i++) {
				this.path.lineTo(xPoints[i], yPoints[i]);
			}
		}
	}

	@Override
	public Rectangle getBounds() {
		return this.path.getBounds();
	}

	@Override
	public Rectangle2D getBounds2D() {
		return this.path.getBounds2D();
	}

	@Override
	public boolean contains(final double x, final double y) {
		return this.path.contains(x, y);
	}

	@Override
	public boolean contains(final Point2D p) {
		return this.path.contains(p);
	}

	@Override
	public boolean intersects(final double x, final double y, final double w, final double h) {
		return this.path.intersects(x, y, w, h);
	}

	@Override
	public boolean intersects(final Rectangle2D r) {
		return this.path.intersects(r);
	}

	@Override
	public boolean contains(final double x, final double y, final double w, final double h) {
		return this.path.contains(x, y, w, h);
	}

	@Override
	public boolean contains(final Rectangle2D r) {
		return this.path.contains(r);
	}

	@Override
	public PathIterator getPathIterator(final AffineTransform at) {
		return this.path.getPathIterator(at);
	}

	@Override
	public PathIterator getPathIterator(final AffineTransform at, final double flatness) {
		return this.path.getPathIterator(at, flatness);
	}

}
