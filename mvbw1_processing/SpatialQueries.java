//package net.lustlab.mvbw1;


import java.util.ArrayList;
import java.util.List;

import net.lustlab.packer.PackNode;
import net.lustlab.packer.Rectangle;

class SpatialQueries {

	/**
	 * Floating point equality test
	 * @param a left test value
	 * @param b right test value
	 * @return true iff difference between a and b is within epsilon
	 */
	static boolean fpeq(float a, float b) {
		float d = a-b;
		return d < 0.0001 && d > -0.0001; 
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return true iff rectangle a and b touch
	 */
	static boolean touching(Rectangle a, Rectangle b) {

		// here we assume that rectangles never overlap!
		float alx = a.topLeft.x;
		float arx = a.topLeft.x + a.width;

		float blx = b.topLeft.x;
		float brx = b.topLeft.x + b.width;

		float aty = a.topLeft.y;
		float aby = a.topLeft.y + a.height;

		float bty = b.topLeft.y;
		float bby = b.topLeft.y + b.height;

		float lx = Math.max(alx, blx);
		float rx = Math.min(arx, brx);

		float ty = Math.max(aty, bty);
		float by = Math.min(aby, bby);

		float ho = rx - lx;
		float vo = by - ty;


		// touching means that either ho > 0 and bottom and top coincide
		// or that vo > 0 and left and right coincide 

		boolean touching = false;

		if (ho > 0) {
			if (fpeq(aty, bby) || fpeq(bty, aby)) {
				touching = true;
			}
		}
		else if (vo > 0) {
			if (fpeq(alx, brx) || fpeq(arx, blx)) {
				touching = true;
			}
		}


		return touching;
	}


	static public List<PackNode> findNeighbours(List<PackNode> input, int idx) {

		List<PackNode> result = new ArrayList<PackNode>();
		PackNode node = input.get(idx);
		Rectangle a = node.getArea();

		for (PackNode o: input) {
			if (o != node && touching(a, o.getArea())) {
				result.add(o);
			}
		}
		return result;
	}

	static PackNode findNearestWithSameColor(List<PackNode> input, int idx) {

		PackNode query = input.get(idx);
		RectangleTemplate qd = (RectangleTemplate) query.getData();
		float minimumDistance = Float.POSITIVE_INFINITY;
		PackNode nearest = null;
		for (PackNode node: input) {

			if (node == query)
				continue;

			RectangleTemplate nd = (RectangleTemplate) node.getData();
			if (nd != null && qd != null)  {
				if (nd.getBlue() == qd.getBlue() && nd.getRed() == qd.getRed() && nd.getGreen() == qd.getGreen()) {

					float d = nodeDistanceSquared(query, node);
					if (d < minimumDistance) {
						minimumDistance = d;
						nearest = node;
					}
				}
			}
		}

		return nearest;

	}

	static PackNode findNearestWithSameSize(List<PackNode> input, int idx) {

		PackNode query = input.get(idx);
		RectangleTemplate qd = (RectangleTemplate) query.getData();
		float minimumDistance = Float.POSITIVE_INFINITY;
		PackNode nearest = null;
		for (PackNode node: input) {

			if (node == query)
				continue;

			RectangleTemplate nd = (RectangleTemplate) node.getData();
			if (nd != null && qd != null) {
				if (nd.getWidth() == qd.getWidth() && nd.getHeight() == qd.getHeight()) {

					float d = nodeDistanceSquared(query, node);
					if (d < minimumDistance) {
						minimumDistance = d;
						nearest = node;
					}
				}
			}
		}

		return nearest;

	}
	//	public static void main(String args[]) {
	//		
	//		PackNode a = new PackNode(new Rectangle(0,0, 100,100));
	//		PackNode b = new PackNode(new Rectangle(100,0, 100,100));
	//		PackNode c = new PackNode(new Rectangle(200,0, 100,100));
	//		PackNode d = new PackNode(new Rectangle(0,100, 100,100));
	//		PackNode e = new PackNode(new Rectangle(0,-100, 100,100));
	//		
	//		List<PackNode> nodes = new ArrayList<PackNode>();
	//		nodes.add(a);
	//		nodes.add(b);
	//		nodes.add(c);
	//		nodes.add(d);
	//		nodes.add(e);
	//		
	//		Purger p = new Purger();
	//		List<PackNode> neighbours = p.findNeighbours(nodes, 0);
	//		System.out.println(neighbours.size());
	//		
	//	}

	static float nodeDistanceSquared(PackNode query, PackNode node) {
		float nx = node.getArea().topLeft.x + node.getArea().width / 2;
		float ny = node.getArea().topLeft.y + node.getArea().height / 2;

		float dx = nx - (query.getArea().topLeft.x + query.getArea().width / 2);
		float dy = ny - (query.getArea().topLeft.y + query.getArea().height / 2);
		float d = dx * dx + dy * dy;
		return d;
	}

}
