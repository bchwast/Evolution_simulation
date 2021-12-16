package simulation;

import simulation.element.Animal;

public interface IAnimalObserver {
    void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition);
}
