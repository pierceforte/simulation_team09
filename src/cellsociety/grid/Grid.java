package cellsociety.grid;

import cellsociety.cell.Cell;
import cellsociety.cell.WaTor.EmptyCell;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Grid<T extends Cell> {
    private List<List<T>> cells = new ArrayList<>();

    public Grid(List<List<T>> cells) {
        this.cells = cells;
    }

    public Grid(Grid<T> gridToCopy) {
        for (int row = 0; row < gridToCopy.getNumRows(); row++) {
            cells.add(new ArrayList<>());
            for (int col = 0; col < gridToCopy.getNumCols(); col++) {
                cells.get(row).add(gridToCopy.get(row, col));
            }
        }
    }

    public List<List<T>> getCells() {
        return cells;
    }

    public T get(int row, int col){
        return cells.get(row).get(col);
    }

    public <C extends T> void set(int row, int col, C cell){
        cells.get(row).set(col, cell);
    }

    public int getNumRows(){
        return cells.size();
    }

    public int getNumCols(){
        return cells.get(0).size();
    }

    public List<T> getAllNeighbors(T cell) {
        List<T> neighbors = new ArrayList<>();

        neighbors.addAll(getCardinalNeighbors(cell));
        neighbors.addAll(getDiagonalNeighbors(cell));

        return neighbors;
    }

    // TODO: eliminate duplication of first 5 lines
    public List<T> getCardinalNeighbors(T cell) {
        List<T> cardinalNeighbors = new ArrayList<>();
        int row = cell.getRow();
        int col = cell.getCol();
        int topRow = cells.size()-1;
        int topCol = cells.get(0).size()-1;

        if (row != 0) {
            cardinalNeighbors.add(cells.get(row-1).get(col));
        }
        if (col != 0) {
            cardinalNeighbors.add(cells.get(row).get(col-1));
        }
        if (row != topRow) {
            cardinalNeighbors.add(cells.get(row+1).get(col));
        }
        if (col != topCol) {
            cardinalNeighbors.add(cells.get(row).get(col+1));
        }

        return cardinalNeighbors;
    }

    public List<T> getDiagonalNeighbors(T cell) {
        List<T> diagonalNeighbors = new ArrayList<>();
        int row = cell.getRow();
        int col = cell.getCol();
        int topRow = cells.size()-1;
        int topCol = cells.get(0).size()-1;

        if (row != 0 && col != 0) {
            diagonalNeighbors.add(cells.get(row-1).get(col-1));
        }
        if (row != topRow && col != 0) {
            diagonalNeighbors.add(cells.get(row+1).get(col-1));
        }
        if (row != 0 && col != topCol) {
            diagonalNeighbors.add(cells.get(row-1).get(col+1));
        }
        if (row != topRow && col != topCol) {
            diagonalNeighbors.add(cells.get(row+1).get(col+1));
        }

        return diagonalNeighbors;
    }

    public void executeForAllCells(Consumer<T> lambda) {
        for (List<T> row : cells) {
            for (T cell : row) {
                lambda.accept(cell);
            }
        }
    }

}
