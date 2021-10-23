import java.util.List;

public class Grid {

    private GridCell[][] grid;

    public Grid(int width, int height) {
        grid = new GridCell[width][height];
    }

    public void setCell(int x, int y, GridCell gridCell) {
        grid[x][y] = gridCell;
    }

    public boolean isComplete() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == null)
                    return false;
            }
        }
        return true;
    }

    public GridCell[][] getGrid() {
        return grid;
    }
}
