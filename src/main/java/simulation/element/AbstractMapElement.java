package simulation.element;

import simulation.Vector2d;
import simulation.map.Cell;

public abstract class AbstractMapElement implements IMapElement {
    protected Vector2d position;
    protected int energy;
    protected Cell cell;

    @Override
    public Vector2d getPosition() {
        return this.position;
    }

    @Override
    public int getEnergy() {
        return this.energy;
    }

    @Override
    public Cell getCell() {
        return this.cell;
    }

    @Override
    public void setCell(Cell cell) {
        this.cell = cell;
    }

    @Override
    public int compareTo(IMapElement other) {
        return Integer.compare(other.getEnergy(), this.getEnergy());
    }

}
