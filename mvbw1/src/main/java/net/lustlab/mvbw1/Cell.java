package net.lustlab.mvbw1;

import java.util.ArrayList;
import java.util.List;

import net.lustlab.packer.PackNode;

/**
 * Container class used by the CellSpacePartitioning class
 * @author Edwin Jakobs
 */
public class Cell {

	List<PackNode> items = new ArrayList<PackNode>();
	public void addItem(PackNode item) {
		items.add(item);
	}

	public List<PackNode> getItems() {
		return items;
	}
}
