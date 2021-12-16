package simulation.map;

import simulation.IAnimalObserver;
import simulation.Vector2d;
import simulation.element.Animal;
import simulation.element.IMapElement;

public interface IMap extends IAnimalObserver {
    boolean place(Animal animal);

    Vector2d getLowerLeft();

    Vector2d getUpperRight();

    Vector2d getJungleLowerLeft();

    Vector2d getJungleUpperRight();

    boolean place(IMapElement element);

    void remove(IMapElement element);

    boolean canMoveTo(Vector2d position);

    Vector2d correctPosition(Vector2d position);

//    boolean insertInCell(IMapElement element);
}
