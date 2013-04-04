package net.lustlab.mvbw1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lustlab.packer.*;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * MVBW—1 main program
 * @author Edwin Jakobs
 *
 */
public class MVBW1 extends PApplet {

	private static final float margin = 0.25f;

	private static final long serialVersionUID = 1L;

	private static final float removeGain = 2.5f;
	private static final float removeBias = 0.5f;

	private static final float gridDistortionGain = 0.25f;

	private static final float minimumDistanceBetweenBueTiles = 50;

	private static final boolean useBigRectangles = false;

	private static final int canvasHeight = 720;
	private static final int canvasWidth = 720;

	private static final int numberOfPerturbations = 10 * (canvasWidth/720) * (canvasHeight/720) ;

	private CellSpacePartitioning csp = new CellSpacePartitioning(canvasWidth, canvasHeight, 100,100) ;

	private Map<RectangleTemplate, Boolean> insertable;

	private Map<RectangleTemplate, List<PackNode>> availableSectors;


	boolean drawPDF = false;
	boolean drawNeighbourDebug = false;
	boolean turboBoost = false;
	boolean full = false;

	private PackNode root;
	private Packer standardPacker;
	private Sampler<RectangleTemplate> sampler = new Sampler<RectangleTemplate>();

	private List<PackNode> populatedNodes;
	boolean drawEmpty = false;

	final float[] mRed = new float[] { 241 ,13 ,26 };
	final float[] mBlack = new float[] { 30, 30, 30 };
	final float[] mBlue = new float[] { 2, 35, 173 };
	final float[] mYellow = new float[] { 254, 208, 1 };
	final float[] mWhite = new float[] { 255, 255, 255};
	final float[] mGray = new float[] { 208, 227 ,223 }; 
	final float[] mDarkGray = new float[] { 208*0.75f, 227*0.75f ,223*0.75f }; 

	public void setup() {
		size(720,720);
		frame.setTitle("MVBW—1");

		prepareCanvas();
	}

	private void prepareCanvas() {
		csp = new CellSpacePartitioning(canvasWidth, canvasHeight, 100,100) ;

		setupSampler();

		root = new PackNode(new Rectangle(0, 0, canvasWidth, canvasHeight));
		populatedNodes = new ArrayList<PackNode>();

		insertable = new HashMap<RectangleTemplate, Boolean>();
		full = false;

		for (RectangleTemplate desc: sampler.options()) {
			insertable.put(desc, true);
		}

		LozengeClipper clipper = new LozengeClipper(canvasWidth, canvasHeight);

		CenteredBinarySplitter splitter = new CenteredBinarySplitter(root.getArea());
		splitter.setInvert(true);
		standardPacker = new Packer(new RandomOrderer(), splitter, clipper);

		int vs = (int) ((6.0* canvasWidth) / 720);
		int hs = (int) ((6.0* canvasHeight) / 720);

		CanvasTools.makeGrid(root, gridDistortionGain, vs, hs);
		CanvasTools.insertBars(root, mWhite, clipper, populatedNodes);
		CanvasTools.decimateCanvas(root, 50, 50);
		CanvasTools.perturbCanvas(root, numberOfPerturbations, sampler, clipper, populatedNodes);
		for (PackNode node: populatedNodes) {
			csp.addItem(node);
		}

		availableSectors = new HashMap<RectangleTemplate, List<PackNode>>();
		for (RectangleTemplate template: sampler.options()) {
			List<PackNode> sectors = new ArrayList<PackNode>();

			for (PackNode node: root.children) {
				sectors.add(node);

			}
			availableSectors.put(template, sectors);
		}
	}

	/**
	 * Set up the sampler. These are the rectangle templates that will be used for the composition 
	 */
	private void setupSampler() {
		sampler = new Sampler<RectangleTemplate>();
		sampler.add(new RectangleTemplate(10, 10, mBlack), 0.15);
		sampler.add(new RectangleTemplate(20, 10, mBlack), 0.04);
		sampler.add(new RectangleTemplate(10, 20, mBlack), 0.04);

		sampler.add(new RectangleTemplate(10, 10, mRed), 0.4);
		//sampler.add(new TileDescription(10, 10, mBlue), 0.4);
		sampler.add(new RectangleTemplate(10, 10, mYellow), 0.4);
		sampler.add(new RectangleTemplate(10, 10, mGray), 0.25);
		sampler.add(new RectangleTemplate(10, 10, mWhite), 0.25);

		sampler.add(new RectangleTemplate(10, 5, mRed), 0.4/10);
		sampler.add(new RectangleTemplate(10, 5, mBlue), 0.4/10);
		sampler.add(new RectangleTemplate(10, 5, mYellow), 0.4/10);
		sampler.add(new RectangleTemplate(10, 5, mGray), 0.4/10);
		sampler.add(new RectangleTemplate(10, 5, mBlack), 0.4/40);
		sampler.add(new RectangleTemplate(10, 5, mWhite), 0.4/50);

		sampler.add(new RectangleTemplate(5, 10, mRed), 0.4/10);
		sampler.add(new RectangleTemplate(5, 10, mBlue), 0.4/10);
		sampler.add(new RectangleTemplate(5, 10, mYellow), 0.4/10);
		sampler.add(new RectangleTemplate(5, 10, mGray), 0.4/10);
		sampler.add(new RectangleTemplate(5, 10, mBlack), 0.4/40);
		sampler.add(new RectangleTemplate(5, 10, mWhite), 0.4/50);

		sampler.add(new RectangleTemplate(20, 20, mBlack), 0.04);
		sampler.add(new RectangleTemplate(20, 20, mRed), 0.5);
		sampler.add(new RectangleTemplate(20, 20, mYellow), 0.5);
		sampler.add(new RectangleTemplate(20, 20, mGray), 0.5);
		//		sampler.add(new TileDescription(20, 20, mBlue), 0.5);
		sampler.add(new RectangleTemplate(20, 20, mWhite), 0.3);

		sampler.add(new RectangleTemplate(10, 20, mBlack), 0.04/2);
		sampler.add(new RectangleTemplate(10, 20, mRed), 0.5/2);
		sampler.add(new RectangleTemplate(10, 20, mYellow), 0.5/2);
		sampler.add(new RectangleTemplate(10, 20, mGray), 0.5/2);
		sampler.add(new RectangleTemplate(10, 20, mBlue), 0.5/2);

		sampler.add(new RectangleTemplate(20, 10, mBlack), 0.04/2);
		sampler.add(new RectangleTemplate(20, 10, mRed), 0.5/2);
		sampler.add(new RectangleTemplate(20, 10, mYellow), 0.5/2);
		sampler.add(new RectangleTemplate(20, 10, mGray), 0.5/2);
		sampler.add(new RectangleTemplate(20, 10, mBlue), 0.5/2);

		//sampler.add(new TileDescription(80, 60, mWhite), 0.1);
		//sampler.add(new TileDescription(80, 60, mGray), 0.1);
		//sampler.add(new TileDescription(60, 80, mWhite), 0.1);
		//sampler.add(new TileDescription(60, 80, mGray), 0.1);

		if (useBigRectangles) {
			sampler.add(new RectangleTemplate(15, 60, mYellow), 0.1);
			sampler.add(new RectangleTemplate(15, 60, mRed), 0.1);
			sampler.add(new RectangleTemplate(15, 60, mGray), 0.1);
			sampler.add(new RectangleTemplate(15, 60, mDarkGray), 0.1);

			sampler.add(new RectangleTemplate(60, 15, mYellow), 0.1);
			sampler.add(new RectangleTemplate(60, 15, mRed), 0.1);
			sampler.add(new RectangleTemplate(60, 15, mGray), 0.1);
			sampler.add(new RectangleTemplate(60, 15, mDarkGray), 0.1);
		}

		//sampler.add(new TileDescription(80, 30, mRed), 0.01);
		//sampler.add(new TileDescription(80, 60, mYellow), 0.01);
		//sampler.add(new TileDescription(30, 80, mRed), 0.01);
		//sampler.add(new TileDescription(60, 40, mYellow), 0.01);

		sampler.seal();
	}


	public void keyPressed() {
		switch (key) {
		case 'x': 
			turboBoost = !turboBoost;
			break;
		case 't':
			drawEmpty = !drawEmpty;
			break;
		case 'p':
			drawPDF = true;
			break;
		case 'r':
			prepareCanvas();
			break;
		}
	}

	public void draw() {
		pushMatrix();

		if (!drawPDF) {
			scale(width*1.0f/ canvasWidth);
		}

		PGraphics drawer = g;

		if (drawPDF) {
			drawer = createGraphics(canvasWidth, canvasHeight, PDF, "mvbw—1-composition-" + System.currentTimeMillis() + ".pdf");
			beginRecord(drawer);
		}

		insertRectangles();

		background(255);
		//List<PackNode> leafNodes = PackNodeTools.findLeafNodes(root);

		drawRectangles(drawer, populatedNodes);
		if (drawPDF) {
			endRecord();
			drawPDF = false;
		}

		popMatrix();

		fill(0);
		text("rectangles: " + populatedNodes.size(), 30, 30);
	}

	private void drawRectangles(PGraphics drawer, List<PackNode> leafNodes) {
		for (PackNode node: leafNodes) {
			drawer.noStroke();
			drawer.fill(0);
			if (node.isTaken() && node.getData() != null) {
				RectangleTemplate data = (RectangleTemplate) node.getData();
				drawer.fill(data.red, data.green, data.blue);
				drawer.rect(
						node.getArea().topLeft.x + margin, 
						node.getArea().topLeft.y + margin, 
						node.getArea().width - 2*margin, 
						node.getArea().height- 2*margin);
			}
			else {
				if (drawEmpty) {
					drawer.noFill();
					drawer.stroke(0, 40);
					drawer.rect(
							node.getArea().topLeft.x + margin, 
							node.getArea().topLeft.y + margin, 
							node.getArea().width - 2*margin, 
							node.getArea().height -2*margin);
				}
			}
		}
	}


	private void insertRectangles() {
		if (!full) {
			tiles: for (int tile = 0; tile < (turboBoost && !drawPDF?100:1); tile++) {
				int tries = 0;
				PackNode dest = null;

				while (tries < 100 && dest == null) {
					RectangleTemplate td = null;

					int sampleTries = 0;
					do {
						td = sampler.sample();
						sampleTries++;
					} while(!insertable.get(td) && sampleTries < 1000);
					
					if (sampleTries>=1000) {
						System.out.println("exceeded 1000 sample tries");
					}
					
					if (insertable.get(td)) {
						int s = availableSectors.get(td).size();
						PackNode startNode = null;
						if (s > 0) {
							// first try to insert into chosen sector ...
							startNode = availableSectors.get(td).get((int)(Math.random() * s));
							dest = standardPacker.insert(startNode, new Rectangle(0, 0, td.width, td.height), td);
						}
						if (dest == null) {
							// ... if that failed seal the sector and retry from the root
							availableSectors.get(td).remove(startNode);
							dest = standardPacker.insert(root, new Rectangle(0, 0, td.width, td.height), td);
						}
						if (dest == null) {
							// if insert from the root failed, remove option from sampler
							sampler.removeOption(td);
							insertable.put(td, false);
							boolean allFalse = true;
							for (boolean val: insertable.values()) {
								if (val) {
									allFalse = false;
									break;
								}
							}
							if (allFalse) {
								full = true;
								System.out.println("full!");
								break tiles;
							}
						}

					}

					tries++;
				}
				if (tries >= 100) {
					System.out.println("exceeded 100 insertion tries");
				}

				// perform duplicate neighbour color check for the inserted rectangle has been inserted
				if (dest != null) {
					//List<PackNode> leafNodes = PackNodeTools.findLeafNodes(root);
					populatedNodes.add(dest);
					if (!remove(dest, populatedNodes)) {
						csp.addItem(dest);
					}

				}
			}
		}
	}

	/*
	 * Removes rectangle when it does not meet given requirements 
	 */
	private boolean remove(PackNode target, List<PackNode> leafNodes) {
		// find the neighbours of the inserted node
		//List<PackNode> neighbours = SpatialQueries.findNeighbours(leafNodes, leafNodes.indexOf(target));
		List<PackNode> neighbours = SpatialQueries.findNeighbours(csp, target);
		RectangleTemplate ttd = (RectangleTemplate) target.getData();

		float cx = target.getArea().topLeft.x + target.getArea().width / 2;
		float cy = target.getArea().topLeft.y + target.getArea().height / 2;

		float dx = canvasWidth/2 - cx;
		float dy = canvasHeight/2 - cy;

		float ndx = Math.abs(dx) / (canvasWidth/2);
		float ndy = Math.abs(dy) / (canvasHeight/2);

		ndx-=removeBias;
		ndy-=removeBias;

		ndx*=removeGain;
		ndy*=removeGain;

		boolean evacuateTarget = false;

		if (Math.random() < Math.max(ndx, ndy)) {
			evacuateTarget = false;
			target.setData(null);
			populatedNodes.remove(target);
			return true;
		}

		float m = 0;
		float dc[] = new float[] { ttd.red, ttd.green, ttd.blue }; 

		for (PackNode neighbour: neighbours) {
			if (neighbour.isTaken()) {
				RectangleTemplate ntd = (RectangleTemplate) neighbour.getData();
				if (ntd != null) {
					float nc[] = { ntd.red, ntd.green, ntd.blue };

					// is the neighbour's color equal to that of the target?
					if (nc[0] == dc[0] && nc[1] == dc[1] && nc[2] == dc[2]) {
						evacuateTarget = true;
					}
				}
				if (drawNeighbourDebug) {
					fill(0,255,0);
					rect(neighbour.getArea().topLeft.x+m, neighbour.getArea().topLeft.y+m, neighbour.getArea().width-2*m, neighbour.getArea().height-2*m);
				}
			}
		}	


		if (dc[0] == mBlue[0] && dc[1] == mBlue[1] && dc[2] == mBlue[2]) {
			PackNode nearest = SpatialQueries.findNearestWithSameColor(csp,target);
			if (nearest != null) {
				if (SpatialQueries.nodeDistanceSquared(target, nearest) < minimumDistanceBetweenBueTiles*minimumDistanceBetweenBueTiles) {
					evacuateTarget = true;
				}
			}
		}

		if ((ttd.getWidth() == 15 && ttd.getHeight() == 60) || (ttd.getWidth() == 60 && ttd.getHeight() == 15) ) {
			PackNode nearest = SpatialQueries.findNearestWithSameSize(leafNodes,leafNodes.indexOf(target));
			if (nearest != null) {
				if (SpatialQueries.nodeDistanceSquared(target, nearest) < 60*60) {
					evacuateTarget = true;
				}
			}
		}

		if (drawNeighbourDebug) {
			fill(255,0,255);
			rect(target.getArea().topLeft.x+m, target.getArea().topLeft.y+m, target.getArea().width-2*m, target.getArea().height-2*m);
		}

		if (evacuateTarget) {
			populatedNodes.remove(target);
			target.evacuate();
		}

		return evacuateTarget;
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { MVBW1.class.getName() } );
	}

}
