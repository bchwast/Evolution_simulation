package simulation.element;

import simulation.Vector2d;

public class Plant extends AbstractMapElement{
    private final int plantEnergy;

    public Plant(Vector2d position, int plantEnergy) {
        this.position = position;
        this.plantEnergy = plantEnergy;
        this.energy = Integer.MAX_VALUE;
    }

    public int getPlantEnergy() {
        return this.plantEnergy;
    }

    @Override
    public String toString() {
        return "P " + this.position.toString();
    }
}
