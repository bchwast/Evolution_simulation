package simulation.element;

import simulation.Vector2d;
import simulation.map.Cell;

public interface IMapElement {
    Vector2d getPosition();

    Energy getEnergy();

    int getEnergyValue();

    Cell getCell();

    void setCell(Cell cell);
}
