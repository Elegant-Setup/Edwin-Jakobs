package net.lustlab.mvbw1;

/**
 * A template for a rectangle
 * @author Edwin Jakobs
 *
 */
class RectangleTemplate {

	final float width;
	final float height;
	
	final float red;
	final float green;
	final float blue;
	
	public RectangleTemplate(float width, float height, float red, float green, float blue) {
		this.width = width;
		this.height = height;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public RectangleTemplate(float width, float height, float color[]) {
		this(width, height, color[0], color[1], color[2]);
	}
		
	public float getBlue() {
		return blue;
	}
	
	public float getGreen() {
		return green;
	}

	public float getHeight() {
		return height;
	}

	public float getRed() {
		return red;
	}

	public float getWidth() {
		return width;
	}
	
}
