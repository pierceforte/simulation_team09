package cellsociety.simulation;

import cellsociety.cell.percolation.PercolationCell;
import cellsociety.grid.Grid;

import java.util.List;
import java.util.Map;

public class PercolationSimModel extends SimModel<PercolationCell>{
    public static final String CONFIG_FILE_PREFIX = "Percolation";

    public PercolationSimModel(List<List<String>> cellStates, SimController simController) {
        super(cellStates, simController);
    }

    @Override
    protected Map<String, Class> getCellTypesMap() {
        return Map.of(PercolationCell.OPEN, PercolationCell.class,
                PercolationCell.CLOSED, PercolationCell.class,
                PercolationCell.FULL, PercolationCell.class);
    }

    @Override
    protected void setNextStates(Grid<PercolationCell> grid) {
        for (int row = 0; row < grid.getNumRows(); row++) {
            for (int col = 0; col < grid.getNumCols(); col++) {
                PercolationCell cell = grid.get(row,col);
                cell.setWhatToDoNext(getNeighbors(cell));
            }
        }
    }

    @Override
    protected void updateStates(Grid<PercolationCell> grid) {
        // update actual states of current grid
        grid.executeForAllCells(cell -> cell.updateState());
    }

    @Override
    protected String getConfigFileIdentifier() {
        return CONFIG_FILE_PREFIX;
    }

    @Override
    protected List<PercolationCell> getNeighbors(PercolationCell cell) {
        return getGrid().getCardinalNeighbors(cell);
    }

}