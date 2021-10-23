import org.opencv.core.Rect;

public class GridCell {

    private Rect rect;
    private GridCell topNeighbour = null;
    private GridCell bottomNeighbour = null;
    private GridCell leftNeighbour = null;
    private GridCell rightNeighbour = null;

    public GridCell(Rect rect) {
        this.rect = rect;
    }

    public GridCell getTopNeighbour() {
        return topNeighbour;
    }

    public void setTopNeighbour(GridCell topNeighbour) {
        this.topNeighbour = topNeighbour;
    }

    public GridCell getBottomNeighbour() {
        return bottomNeighbour;
    }

    public void setBottomNeighbour(GridCell bottomNeighbour) {
        this.bottomNeighbour = bottomNeighbour;
    }

    public GridCell getLeftNeighbour() {
        return leftNeighbour;
    }

    public void setLeftNeighbour(GridCell leftNeighbour) {
        this.leftNeighbour = leftNeighbour;
    }

    public GridCell getRightNeighbour() {
        return rightNeighbour;
    }

    public void setRightNeighbour(GridCell rightNeighbour) {
        this.rightNeighbour = rightNeighbour;
    }
}
