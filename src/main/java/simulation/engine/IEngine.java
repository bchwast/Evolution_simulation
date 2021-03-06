package simulation.engine;

import simulation.Vector2d;
import simulation.gui.IMapUpdateObserver;
import simulation.map.Cell;

import java.util.concurrent.ConcurrentHashMap;

public interface IEngine extends Runnable{
    Cell getCellAt(Vector2d position);

    void addObserver(IMapUpdateObserver observer);

    void pause();

    void resume();

    int[] getGenotype();

    int getAnimalsAmount();

    int getPlantsAmount();

    double getAverageEnergy();

    double getAverageLifeLength();

    double getAverageChildrenAmount();

    int getEpoque();

    void stop();

    ConcurrentHashMap<Vector2d, Cell> getCells();
}
