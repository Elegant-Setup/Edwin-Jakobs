package net.lustlab.mvbw1;

import net.lustlab.packer.Clipper;
import net.lustlab.packer.Rectangle;
import net.lustlab.packer.Vector;

/**
 * A lozenge shaped clipper 
 * @author Edwin Jakobs
 */
class LozengeClipper implements Clipper {

	final Vector region[];

	public LozengeClipper(float width, float height) {
		region = new Vector[] {
				new Vector(0, height/2),
				new Vector(width/2, 0),
				new Vector(width, height/2),
				new Vector(width/2, height)
		};
	}

	@Override
	public boolean inside(Rectangle area, Rectangle rectangle) {

		Vector points[] = new Vector[] {
				new Vector(area.topLeft.x, area.topLeft.y),
				new Vector(area.topLeft.x+area.width, area.topLeft.y),
				new Vector(area.topLeft.x+area.width, area.topLeft.y+area.height),
				new Vector(area.topLeft.x, area.topLeft.y+area.height),
		};

		boolean allInside = true;

		for (Vector point: points) {
			for (int i = 0; i < region.length; ++i) {
				Vector a = region[i];
				Vector b = region[(i+1) % region.length];

				Vector q = new Vector(point.x-a.x, point.y-a.y);
				Vector l = new Vector(b.x - a.x, b.y - a.y);

				float dot = q.x * l.x + q.y * l.y;
				if (dot < 1) {
					allInside = false;
					break;
				}

			}
		}
		return allInside;

	};
};