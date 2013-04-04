package net.lustlab.mvbw1;

import java.util.List;

import net.lustlab.packer.Clipper;
import net.lustlab.packer.GreedySplitter;
import net.lustlab.packer.GutterPresplitter;
import net.lustlab.packer.IrregularGutterPresplitter;
import net.lustlab.packer.PackNode;
import net.lustlab.packer.PackNodeTools;
import net.lustlab.packer.Packer;
import net.lustlab.packer.RandomOrderer;
import net.lustlab.packer.Rectangle;
import net.lustlab.packer.SingleAxisGreedySplitter;

class CanvasTools {

	static void decimateCanvas(PackNode root, float minWidth, float minHeight) {
		IrregularGutterPresplitter cutPresplitter = new IrregularGutterPresplitter(new float[]{}, new float[]{});
		cutPresplitter.setGutter(15);
		cutPresplitter.setOutputGutters(false);
		cutPresplitter.setOutputCells(true);
		List<PackNode> leafNodes = PackNodeTools.findLeafNodes(root);
		for (PackNode leaf: leafNodes) {
			if (!leaf.isTaken()) {
	
				if (leaf.getArea().width > 50) {
					cutPresplitter.setHorizontalDivisions(new float[]{0.5f, 0.5f});
					cutPresplitter.setVerticalDivisions(new float[]{1.0f});
					leaf.children = cutPresplitter.split(leaf);
				}
				else if (leaf.getArea().height > 50) {
					cutPresplitter.setVerticalDivisions(new float[]{0.5f, 0.5f});
					cutPresplitter.setHorizontalDivisions(new float[]{1.0f});
					leaf.children = cutPresplitter.split(leaf);
					
				}
			}
		}
	}

	static void perturbCanvas(PackNode root, int count, Sampler<RectangleTemplate> sampler, LozengeClipper clipper, List<PackNode> populatedNodes) {
		// perturb initial grid
		GreedySplitter perturbSplitter = new GreedySplitter();
		Packer perturbPacker = new Packer(new RandomOrderer(), perturbSplitter, clipper);
	
		for (int i = 0; i < count; ++i) {
			perturbSplitter.setHorizontalBias(0.5f*(float)Math.random() * 2 - 1);
			perturbSplitter.setVerticalBias(0.5f*(float)Math.random() * 2 - 1);
			perturbSplitter.setHorizontalDivisions(1+ (int)(Math.random()* 4));
			perturbSplitter.setVerticalDivisions(1+ (int)(Math.random()* 4));
	
			RectangleTemplate td = sampler.sample();
			PackNode dest = perturbPacker.insert(root, new Rectangle(0, 0, td.width, td.height), td);
			if (dest != null) {
				populatedNodes.add(dest);
			}
		}
	}

	static void distortDivisions(float[] vsplits, float gain) {
		for (int i = 0; i < vsplits.length; ++i) {
			int other;
			do {
				other = (int) (Math.random() * vsplits.length);
			} while (i == other);
			
			float amount = (float) (Math.random() * gain / vsplits.length);
			if (vsplits[other] > amount) {
				vsplits[i] += amount;
				vsplits[other] -= amount;
			}
		}
	}

	static void insertBars(PackNode root, float[] color, Clipper clipper, List<PackNode> populatedNodes) {
	
		RectangleTemplate[] sectorTiles = {
				new RectangleTemplate(100, 40, color),	
				new RectangleTemplate(100, 15, color),
				new RectangleTemplate(50, 40, color),
				new RectangleTemplate(50, 15, color),
		};
	
		float height = root.getArea().height;
		
		Packer halfGreedyPacker = new Packer(new RandomOrderer(), new SingleAxisGreedySplitter(), clipper);
		
		// place white rectangles inside sectors
		for (int i = 0; i < root.children.length; ++i) {
			PackNode sector = root.children[i];
			float sectorWidth = sector.getArea().width-10;
			float scale = 1 - Math.abs((sector.getArea().topLeft.y + sector.getArea().height/2) - height/2.0f) / (height/2.0f);
	
			if (Math.random() < 0.9) {
				if (Math.random() < 0.75) {
					{	PackNode dest = halfGreedyPacker.insert(sector,new Rectangle(0, 0, sectorWidth, sectorTiles[0].height*scale), sectorTiles[0]);
						if (dest != null) {
							populatedNodes.add(dest);
						}
					}
					for (int n = 0; n < 2; ++n) {
						if (Math.random()<0.5) {
							PackNode dest = halfGreedyPacker.insert(sector,new Rectangle(0, 0, sectorWidth, sectorTiles[1].height*scale), sectorTiles[1]);
							if (dest != null) {
								populatedNodes.add(dest);
							}

						}
					}
	
				}
				else {
					GutterPresplitter presplitter = new GutterPresplitter(2,1);
					presplitter.setGutter(5);
					sector.children = presplitter.split(sector);
					int sub = (int) (Math.random() * sector.children.length);
					{	PackNode dest = halfGreedyPacker.insert(sector.children[sub],new Rectangle(0, 0, sectorWidth/2, sectorTiles[2].height*scale), sectorTiles[2]);
						if (dest != null) {
							populatedNodes.add(dest);
						}
					}
					for (int n = 0; n < 2; ++n) {
						if (Math.random()<0.5) {
							PackNode dest = halfGreedyPacker.insert(sector.children[sub],new Rectangle(0, 0, sectorWidth/2, sectorTiles[3].height*scale), sectorTiles[3]);
							if (dest != null) {
								populatedNodes.add(dest);
							}
						}
					}
				}
			}
		}
	}

	static void makeGrid(PackNode root, float distortionGain, int vs, int hs) {
	
		float[] vsplits = new float[vs];
		vsplits[0] = 1.75f / vs;
		for (int i = 1; i < vs-1; ++i) {
			vsplits[i] = 1.0f / vs;
		}
		vsplits[vs-1] = 0.25f / vs;
		distortDivisions(vsplits, distortionGain);
	
		
		float[] hsplits = new float[hs];
		hsplits[0] = 1.75f / hs;
		for (int i = 1; i < hs-1; ++i) {
			hsplits[i] = 1.0f / hs;
		}
		hsplits[hs-1] = 0.25f / hs;
		distortDivisions(hsplits, distortionGain);
			
		IrregularGutterPresplitter irregularPresplitter = new IrregularGutterPresplitter(vsplits, hsplits);
	
		irregularPresplitter.setGutter(100);
		irregularPresplitter.setOutputCells(false);
		irregularPresplitter.setOutputGutters(true);
		
		// create sectors
		root.children = irregularPresplitter.split(root);
	}

}
