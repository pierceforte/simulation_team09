package cellsociety.cell.wator;
import cellsociety.grid.Grid;

import java.util.ArrayList;
import java.util.List;

/**
 * This class inherits from the abstract class LivingWaTorCell.
 *
 * This class's purpose is to update the shark cells in the grid. Sharks must choose where to move on each update,
 * reproduce after a set time period, and keep track of their energy, which is gathered by eating fish. If a shark
 * runs out of energy, it dies.
 *
 * @author Pierce Forte
 */
public class SharkCell extends LivingWaTorCell{
    public static final String STATE = WaTorCell.SHARK;
    public static final int CHRONONS_TO_REPRODUCE = 5;
    public static final int DEFAULT_SHARK_ENERGY = 4;
    public static final int ENERGY_FOR_EATING_FISH = 2;

    private List<Integer> posOfFishToEatNext;
    private int energy;

    /**
     * The constructor to create a SharkCells's backend.
     * @param row The row in which the cell is located
     * @param col The column in which the cell is located
     */
    public SharkCell(int row, int col) {
        super(STATE, row, col);
        energy = DEFAULT_SHARK_ENERGY;
    }

    /**
     * Get how much energy the shark has.
     * @return How much energy the shark has
     */
    public int getEnergy() {
        return energy;
    }

    /**
     * Get the position of the fish that this shark will eat next
     * @return The row, column pair of the fish to be eaten
     */
    public List<Integer> getPosOfFishToEatNext() {
        return posOfFishToEatNext;
    }

    // TODO: clean this method up

    @Override
    public Grid<WaTorCell> setWhatToDoNext(List<WaTorCell> neighbors, Grid<WaTorCell> nextGrid) {
        // if shark has no energy, it dies and becomes an EMPTY cell
        if (energy == 0) {
            nextGrid.set(getNextRow(), getNextCol(), new EmptyCell(getRow(), getCol()));
            return nextGrid;
        }
        List<List<Integer>> potentialNewPositions = new ArrayList<>();
        // first look for any nearby fish
        for (WaTorCell cell : neighbors) {
            if (cell.getState().equals(FISH)) {
                potentialNewPositions.add(List.of(cell.getRow(), cell.getCol()));
            }
        }
        boolean willEatFish = !potentialNewPositions.isEmpty();
        // if no fish, look for potential empty spaces to move to
        if (!willEatFish) {
            potentialNewPositions = getAdjacentEnterableCells(neighbors, nextGrid, List.of(SHARK));
        }
        // if no potential empty spaces to move to, don't move
        if (potentialNewPositions.isEmpty()) {
            nextGrid.set(getNextRow(), getNextCol(), this);
            return nextGrid;
        }
        nextGrid = setNextPosition(potentialNewPositions, nextGrid);
        nextGrid = handleReproduction(nextGrid);
        updateEnergy(willEatFish, neighbors);
        return nextGrid;
    }

    @Override
    protected int getChrononsToReproduce() {
        return CHRONONS_TO_REPRODUCE;
    }

    private void updateEnergy(boolean willEatFish, List<WaTorCell> neighbors) {
        if (willEatFish) {
            energy += ENERGY_FOR_EATING_FISH;
            for (WaTorCell cell : neighbors) {
                if (cell.getRow() == getNextRow() && cell.getCol() == getNextCol()) {
                    posOfFishToEatNext = List.of(getNextRow(), getNextCol());
                }
            }
        }
        else {
            energy--;
        }
    }

}
