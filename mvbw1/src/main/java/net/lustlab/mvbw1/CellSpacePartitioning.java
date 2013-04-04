package net.lustlab.mvbw1;


import net.lustlab.packer.PackNode;

/**
 * Implementation of a very simple space partioning scheme for rectangles. 
 * Divides a finite 2D space up in a number of cells.  
 * @author Edwin Jakobs
 *
 */
public class CellSpacePartitioning {

	Cell cells[];
	
	float cellWidth;
	float cellHeight;
	
	float areaWidth;
	float areaHeight;
	int horizontalCells;
	int verticalCells;
	
	CellSpacePartitioning(float areaWidth, float areaHeight, float cellWidth, float cellHeight) {
		
		horizontalCells = (int) Math.ceil(areaWidth / cellWidth);
		verticalCells = (int) Math.ceil(areaHeight / cellHeight);
		
		cells = new Cell[horizontalCells * verticalCells];
		for (int y= 0 ;y < verticalCells; ++y) {
			for (int x = 0; x < horizontalCells; ++x) {
				cells[y*horizontalCells+x] = new Cell();
			}
		}
		
		this.areaHeight = areaHeight;
		this.areaWidth = areaWidth;
		this.cellWidth = cellWidth;
		this.cellHeight  = cellHeight;
	}
	
	/**
	 * Adds an item to the csp.
	 * @param item
	 */
	public void addItem(PackNode item) {
		
		int minx = leftXIndex(item);
		int miny = topYIndex(item);
		
		int maxx = rightXIndex(item);
		int maxy = bottomYIndex(item);
		
		for (int y = miny; y <= maxy; ++y) {
			for (int x = minx; x <= maxx; ++x) {
				cells[y * horizontalCells + x].addItem(item);
			}
		}
	}

	/**
	 * Returns all cells that are touched by the item
	 * @param item the item to perform the query for
	 * @return
	 */
	public Cell[] itemCells(PackNode item) {
		int minx = leftXIndex(item);
		int miny = topYIndex(item);
		
		int maxx = rightXIndex(item);
		int maxy = bottomYIndex(item);

		Cell result[] = new Cell[(maxx-minx+1) * (maxy - miny + 1)];
		
		int idx = 0;
		for (int y = miny; y <= maxy; ++y) {
			for (int x = minx; x <= maxx; ++x) {
				result[idx++] = cells[y * horizontalCells + x];
			}
		}
		return result;
	}
	
	/**
	 * Returns all cells touched by the item and some extra radius, this is useful for neighbour quries.
	 * @param item the item to perform the query for
	 * @param radius
	 * @return
	 */
	public Cell[] itemCellsWithRadius(PackNode item, float radius) {
		int minx = mapX(item.getArea().topLeft.x - radius);
		int miny = mapY(item.getArea().topLeft.y - radius);
		
		if (minx < 0)
			minx = 0;
		if (miny < 0)
			miny = 0;
		
		int maxx = mapX(item.getArea().topLeft.x + item.getArea().width + radius);
		int maxy = mapY(item.getArea().topLeft.y + item.getArea().height + radius);

		if (maxx >= horizontalCells) {
			maxx = horizontalCells-1;
		}
		if (maxy >= verticalCells) {
			maxy = verticalCells-1;
		}
		
		
		Cell result[] = new Cell[(maxx-minx+1) * (maxy - miny + 1)];
		
		int idx = 0;
		for (int y = miny; y <= maxy; ++y) {
			for (int x = minx; x <= maxx; ++x) {
				result[idx++] = cells[y * horizontalCells + x];
			}
		}
		return result;
	}

	private int bottomYIndex(PackNode item) {
		return mapY(item.getArea().topLeft.y + item.getArea().height);
	}

	private int mapY(float y) {
		return (int) (y/cellHeight);
	}

	private int mapX(float x) {
		return (int) (x/cellWidth);
	}

	private int rightXIndex(PackNode item) {
		return mapX(item.getArea().topLeft.x+item.getArea().width);
	}

	private int topYIndex(PackNode item) {
		return mapY(item.getArea().topLeft.y);
	}

	private int leftXIndex(PackNode item) {
		return mapX(item.getArea().topLeft.x);
	}
	
}
