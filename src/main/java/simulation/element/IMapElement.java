package simulation.element;

import simulation.Vector2d;
import simulation.map.Cell;

public interface IMapElement extends Comparable<IMapElement>{
    Vector2d getPosition();

    int getEnergy();

    Cell getCell();

    void setCell(Cell cell);
}
