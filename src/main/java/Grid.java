import org.opencv.core.Rect;

import java.util.List;

public class Grid {

    private GridCell[][] grid;

    public Grid(List<List<Rect>> allLines) {
        grid = new GridCell[allLines.size()][allLines.get(0).size()];

        int y = -1;

        for (List<Rect> line : allLines) {
            ++y;
            int x = -1;
            for (Rect cell : line) {
                ++x;
                grid[y][x] = new GridCell(cell);
            }
        }
    }

    public void setCell(int x, int y, GridCell gridCell) {
        grid[x][y] = gridCell;
    }

    public GridCell[][] getGrid() {
        return grid;
    }
}
