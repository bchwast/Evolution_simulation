package simulation.engine;

import simulation.Vector2d;
import simulation.gui.IMapUpdateObserver;
import simulation.map.Cell;

public interface IEngine extends Runnable{
    Cell getCellAt(Vector2d position);

    void addObserver(IMapUpdateObserver observer);
}