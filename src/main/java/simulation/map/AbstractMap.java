package simulation.map;

import simulation.Vector2d;
import simulation.element.Animal;
import simulation.element.IMapElement;
import simulation.element.Plant;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractMap implements IMap{
    protected final int width;
    protected final int height;
    protected Vector2d lowerLeft;
    protected Vector2d upperRight;
    protected Vector2d jungleLowerLeft;
    protected Vector2d jungleUpperRight;
    protected LinkedHashMap<Vector2d, Cell> cells = new LinkedHashMap<>();
    protected Set<IMapElement> elements = new LinkedHashSet<>();

    @Override
    public Vector2d getLowerLeft() {
        return this.lowerLeft;
    }

    @Override
    public Vector2d getUpperRight() {
        return this.upperRight;
    }

    @Override
    public Vector2d getJungleLowerLeft() {
        return this.jungleLowerLeft;
    }

    @Override
    public Vector2d getJungleUpperRight() {
        return this.jungleUpperRight;
    }

    private boolean insertInCell(IMapElement element) {
        Vector2d position = element.getPosition();
        Cell cell = cells.get(position);
        if (cell == null) {
            Cell newCell = new Cell(position, element);
            this.cells.put(position, newCell);
            element.setCell(newCell);
            return true;
        }
        else {
            if (element instanceof Plant && cell.getFirstElement() instanceof Plant) {
                element.setCell(cell);
                return false;
            }
            else {
                cell.addElement(element);
                element.setCell(cell);
                return true;
            }
        }
    }

    @Override
    public boolean place(IMapElement element) {
        this.elements.add(element);
        return insertInCell(element);
    }

    public void remove(IMapElement element) {
        this.elements.remove(element);
    }

    @Override
    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition) {
        Cell oldCell = this.cells.get(oldPosition);
        oldCell.removeElement(animal);
        insertInCell(animal);
    }
}
