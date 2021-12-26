package simulation.map;

import simulation.IAnimalObserver;
import simulation.Vector2d;
import simulation.element.IMapElement;

import java.util.List;

public interface IMap extends IAnimalObserver {
    Vector2d getLowerLeft();

    Vector2d getUpperRight();

    Vector2d getJungleLowerLeft();

    Vector2d getJungleUpperRight();

    void place(IMapElement element);

    void removeElement(IMapElement element);

    boolean canMoveTo(Vector2d position);

    Vector2d correctPosition(Vector2d position);

    List<Cell> generatePlants(int plantEnergy);

    boolean canPlace(Vector2d position);

    boolean isJungle(Vector2d position);

    void decreasePlants();

    int getPlantsAmm();
}
