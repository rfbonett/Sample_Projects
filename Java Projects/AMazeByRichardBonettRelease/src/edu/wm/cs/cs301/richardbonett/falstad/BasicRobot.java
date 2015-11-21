package edu.wm.cs.cs301.richardbonett.falstad;

import java.util.HashMap;

import edu.wm.cs.cs301.richardbonett.ui.GLRendererNew;

/**
 * A BasicRobot is a Robot that has a Maze object that it acts on with simple movement/rotation methods. The 
 * BasicRobot is used by RobotDrivers to maneuver a Maze towards the exit. A BasicRobot can have at most four 
 * DistanceSensors for each side Forward, Right, Left, and Backward, as well as a sensor to determine whether
 * or not it is currently in a room. 
 * 
 * Responsibility: serve as the intermediary between a maze-solving algorithm (i.e. RobotDriver) and a maze
 * 
 * Collaborators: a Maze to explore, a RobotDriver to maneuver the robot, and DistanceSensors to manage the
 * robot's distance sensing capabilities
 * 
 * @author Rich
 *
 */
public class BasicRobot implements Robot {

	private float energy;
	private Maze maze;
	private boolean hasRoomSensor;
	
	/**
	 * A BasicRobot can be constructed with no parameters, but is essentially useless until setMaze() is called
	 */
	public BasicRobot() {
	}
	
	@Override
	public void rotate(Turn turn) throws Exception {
		switch(turn) {
		case LEFT :
			energy -= 3;
			maze.rotate(Math.PI/2);
			maze.enqueueOperation(Operation.ROTATE_LEFT);
			break;
		case RIGHT :
			energy -= 3;
			maze.rotate(-Math.PI/2);
			maze.enqueueOperation(Operation.ROTATE_RIGHT);
			break;
		case AROUND :
			energy -= 6;
			maze.rotate(Math.PI);
			maze.enqueueOperation(Operation.ROTATE_AROUND);
			break;
		}
		if (energy <= 0) {
			throw new Exception();
		}
	}

	@Override
	public void move(int distance) throws Exception {
		while (distance > 0) {
			if (distanceToObstacle(Direction.FORWARD) == 0)
				break;
			if (energy < 5) 
				throw new Exception();
			maze.move(1);
			maze.enqueueOperation(Operation.MOVE_FORWARD);
			distance -= 1;
			energy -= 5;
		}
	}

	@Override
	public int[] getCurrentPosition() throws Exception {
		int[] curPos;
		try {
			curPos = maze.getCurrentPosition();
		}
		catch (Exception OutOfBoundsException) {
			throw OutOfBoundsException;
		}
		return curPos;
	}

	@Override
	public void setMaze(Maze maze) {
		energy = 2500;
		this.maze = maze;
	}

	@Override
	public boolean isAtGoal() {
		try {
			return maze.isAtEndPosition(getCurrentPosition()[0], getCurrentPosition()[1]);
		}
		catch (Exception e) {
			return true;
		}
		
	}

	@Override
	public boolean canSeeGoal(Direction direction)
			throws UnsupportedOperationException {
		return false;
	}

	@Override
	public boolean isInsideRoom() throws UnsupportedOperationException {
		if (!hasRoomSensor())
			throw new UnsupportedOperationException();
		else 
			return maze.isInRoom();
	}

	@Override
	public boolean hasRoomSensor() {
		return hasRoomSensor;
	}

	@Override
	public int[] getCurrentDirection() {
		int[] curDirection = {(int) Math.cos(maze.angle), (int) Math.sin(maze.angle)};
		return curDirection;
	}

	@Override
	public float getBatteryLevel() {
		return energy;
	}

	@Override
	public void setBatteryLevel(float level) {
		energy = level;
		
	}

	@Override
	public float getEnergyForFullRotation() {
		return 12;
	}

	@Override
	public float getEnergyForStepForward() {
		return 5;
	}

	@Override
	public boolean hasStopped() {
		return energy <= 0;
	}

	@Override
	public int distanceToObstacle(Direction direction)
			throws UnsupportedOperationException {
		energy -= 1;
		switch(direction) {
		case FORWARD :
			return maze.distanceFront();
		case RIGHT :
			return maze.distanceRight();
		case LEFT :
			return maze.distanceLeft();
		case BACKWARD : default :
			return maze.distanceBack();
		}
	}

	@Override
	public boolean hasDistanceSensor(Direction direction) {
		return true;
	}
	

}
