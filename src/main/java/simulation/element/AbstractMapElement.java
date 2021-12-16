package simulation.element;

import simulation.Vector2d;
import simulation.map.Cell;

public abstract class AbstractMapElement implements IMapElement {
    protected Vector2d position;
    protected Energy energy;
    protected Cell cell;

    @Override
    public Vector2d getPosition() {
        return this.position;
    }

    @Override
    public Energy getEnergy() {
        return this.energy;
    }

    @Override
    public int getEnergyValue() {
        return this.energy.getValue();
    }

    @Override
    public Cell getCell() {
        return this.cell;
    }

    @Override
    public void setCell(Cell cell) {
        this.cell = cell;
    }
}
