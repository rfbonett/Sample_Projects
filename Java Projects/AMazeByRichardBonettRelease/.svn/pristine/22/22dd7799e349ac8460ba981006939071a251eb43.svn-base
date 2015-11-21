package edu.wm.cs.cs301.richardbonett.falstad;

import java.util.ArrayList;

/**
 * This class extends the functionality of MazeBuilder by providing a different algorithm for maze generation. 
 * The MazeBuilder class is still used for adding rooms to the maze and handling a separate thread for communication with the Maze class.
 * 
 * @author rfbonett
 */
public class MazeBuilderKruskal extends MazeBuilder implements Runnable{
	private int[][] forest;
	private ArrayList<Wall> candidates;
	private int trees;
	
	/**
	 * Constructor for maze generation using Kruskal's algorithm
	 */
	public MazeBuilderKruskal() {
		super();
		System.out.println("MazeBuilderKruskal uses Kruskal's algorithm to generate maze.");
	}
	
	/**
	 * Constructor with a parameter to allow for maze generation to be either deterministic or random
	 * @param det will cause deterministic maze generation if set to true
	 */
	public MazeBuilderKruskal(boolean det) {
		super(det);
		System.out.println("MazeBuilderKruskal uses Kruskal's algorithm to generate maze.");
	}

	/**
	 * This method generates pathways into the maze by using Kruskal's algorithm to generate a spanning tree for a graph.
	 * The nodes of the graph are individual cells in the maze. As the algorithm proceeds, it connects nodes by randomly 
	 * selecting walls for deletion from a candidate list of walls. The nodes connect to form trees, which eventually combine
	 * to form one spanning tree as the algorithm proceeds to completion. 
	 */
	@Override
	protected void generatePathways() {
		// Creates a forest, where initially every cell (excluding rooms) is its own tree
		// Creates a list of candidates comprising breakable walls in the maze
		forest = new int[width][height];
		candidates = new ArrayList<Wall>();
		trees = 0;
		buildCandidateListAndTrees();
		Wall curWall;
		
		// While the candidate list is nonempty and multiple trees exist in the forest, randomly selects a 
		// wall from the candidate list. If the wall's deletion would connect two trees in the forest, the 
		// wall is deleted and the trees connected, else the wall is simply discarded. 
		while (!candidates.isEmpty() && trees > 1) {
			curWall = extractWallFromCandidateSetRandomly();
			if (combineTrees(curWall)) {
				trees -= 1;
				cells.deleteWall(curWall.x, curWall.y, curWall.dx, curWall.dy);
			}
		}
	}
	
	/**
	 * Pick a random wall from the list of candidates, remove the wall and return it.
	 * @return candidate from the list, randomly chosen. 
	 */
	private Wall extractWallFromCandidateSetRandomly() {
		return candidates.remove(random.nextIntWithinInterval(0, candidates.size()-1)); 
	}
	
	/**
	 * Check if deleting this wall would connect two separate trees in the forest, and if so,
	 * delete this wall and connect the two trees. 
	 * @param wall the wall to check for deletion
	 * @return boolean value representing if the wall was deleted (true) or not (false)
	 */
	private boolean combineTrees(Wall wall) {
		// Determine the trees of the two cells adjoining this wall. Trees are represented by
		// int values, with two cells of the same tree having the same value. 
		int curTree = forest[wall.x][wall.y];
		int destTree = forest[wall.x + wall.dx][wall.y + wall.dy];
		// If the int values for both cells' trees are the same, then they are of the same tree
		if (curTree == destTree) 
			return false;
		// Otherwise, all values of one cell's tree must be overwritten with the other's tree value, 
		// thus connecting the two trees implicitly.
		else {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (forest[x][y] == destTree) {
						forest[x][y] = curTree;
					}
				}
			}
			return true;
		}
	}
	
	/**
	 * Builds the candidate list of walls and forest list of trees simultaneously by iterating through possible 
	 * (x, y) coordinates in the maze. The candidate list contains all walls that can be broken initially, excluding
	 * the maze borders and non-door room borders. The forest represents each individual cell as a tree initially, 
	 * aside from rooms, which are represented as a single tree each. Trees themselves are represented by int values, 
	 * with two cells of the same tree sharing the same value, itself different from that of a cell in another tree. 
	 */
	private void buildCandidateListAndTrees() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				// Applies tags to cells according to their tree; rooms share the same tag
				if (cells.isInRoom(x, y)) {
					if (cells.hasWallOnTop(x, y) && cells.hasWallOnLeft(x,  y)) {
						forest[x][y] = trees;
						trees += 1;
					}
					else if (cells.hasWallOnLeft(x, y)) {
						forest[x][y] = forest[x][y - 1];
					}
					else {
						forest[x][y] = forest[x-1][y];
					}
				}
				else {
					forest[x][y] = trees;
					trees += 1;
				}
				// Adds walls to the candidate list. Duplication is avoided by only considering bot/right walls for every cell.
				if (cells.canGo(x, y, 0, 1))
						candidates.add(new Wall(x, y, 0, 1));
				
				if (cells.canGo(x, y, 1, 0))
						candidates.add(new Wall(x, y, 1, 0));
					
			}
		}
	}
}
