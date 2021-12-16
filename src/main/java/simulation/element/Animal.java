package simulation.element;

import simulation.IAnimalObserver;
import simulation.MapDirection;
import simulation.Vector2d;
import simulation.map.IMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Animal extends AbstractMapElement{
    private final Random random = new Random();
    private MapDirection direction = MapDirection.getRandom();
    private final Genotype genotype;
    private int age = 0;
    private IMap map;
    private final List<IAnimalObserver> observers = new ArrayList<>();
    private Animal[] parents;
    private Set<Animal> children;

    public Animal(IMap map, Energy energy) {
        this.map = map;
        addObserver(map);
        this.energy = energy;
        this.genotype = new Genotype();
        this.position = new Vector2d(this.random.nextInt(0, this.map.getLowerLeft().x + 1),
                this.random.nextInt(0, this.map.getUpperRight().y + 1));
    }

    public Animal(IMap map, Animal firstParent, Animal secondParent) {
        this.map = map;
        addObserver(map);
        this.energy = new Energy((firstParent.getEnergyValue() / 4) + (secondParent.getEnergyValue() / 4));
        this.genotype = new Genotype(firstParent, secondParent);
        this.position = firstParent.getPosition();
    }

    private void addObserver(IAnimalObserver observer) {
        this.observers.add(observer);
    }

    private void removeObserver(IAnimalObserver observer) {
        this.observers.remove(observer);
    }

    public int[] getGenotype() {
        return this.genotype.getGenotype();
    }

    public void increaseEnergy(Energy energy) {
        this.energy.add(energy);
        this.cell.removeElement(this);
        this.cell.addElement(this);
    }

    public void decreaseEnergy(Energy energy) {
        this.energy.subtract(energy);
    }

    public void breedEnergyDecrease() {
        this.energy.subtract(new Energy(this.getEnergyValue() / 4));
    }

    public boolean die() {
        this.cell.removeElement(this);
        this.map.remove(this);
    }

    public void move() {
        switch (this.genotype.getGene()) {
            case 0 -> {
                Vector2d newPosition = this.position.add(this.direction.toUnitVector());
                if (this.map.canMoveTo(newPosition)) {
                    Vector2d oldPosition = this.position;
                    this.position = this.map.correctPosition(newPosition);
                    changePosition(oldPosition, this.position);
                }
            }
        }
    }

    private void changePosition(Vector2d oldPosition, Vector2d newPosition) {
        for (IAnimalObserver observer : this.observers) {
            observer.positionChanged(this, oldPosition, newPosition);
        }
    }
}
