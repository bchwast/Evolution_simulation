package simulation.element;

import simulation.Vector2d;

public class Plant extends AbstractMapElement{
    private final Vector2d position;
    private final int plantEnergy;
    private Cell cell;

    public Plant(Vector2d position, int plantEnergy) {
        this.position = position;
        this.plantEnergy = plantEnergy;
    }
}
