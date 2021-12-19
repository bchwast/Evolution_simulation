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

    public Animal(IMap map, int energy) {
        this.map = map;
        addObserver(map);
        this.energy = energy;
        this.genotype = new Genotype();
        Vector2d position =  new Vector2d(this.random.nextInt(this.map.getUpperRight().x + 1),
                this.random.nextInt(this.map.getUpperRight().y + 1));
        while (! this.map.canPlace(position)) {
            position =  new Vector2d(this.random.nextInt(this.map.getUpperRight().x + 1),
                    this.random.nextInt(this.map.getUpperRight().y + 1));
        }
        this.position = position;
    }

    public Animal(IMap map, Animal firstParent, Animal secondParent) {
        this.map = map;
        addObserver(map);
        this.energy = (firstParent.getEnergy() / 4) + (secondParent.getEnergy() / 4);
        this.genotype = new Genotype(firstParent, secondParent);
        this.position = firstParent.getPosition();
    }

    public void addObserver(IAnimalObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(IAnimalObserver observer) {
        this.observers.remove(observer);
    }

    public int[] getGenotype() {
        return this.genotype.getGenotype();
    }

    public void increaseEnergy(int energy) {
        this.energy += energy;
        this.cell.removeElement(this);
        this.cell.addElement(this);
    }

    public void decreaseEnergy(int energy) {
        this.energy -= energy;
    }

    public void breedEnergyDecrease() {
        this.energy -= this.getEnergy() / 4;
    }

    public void die() {
        System.out.print(this);
        System.out.println(" dead");
        this.cell.removeElement(this);
        this.map.removeElement(this);
    }

    public void move() {
        this.age += 1;
        int gene = this.genotype.getGene();
//        System.out.print(this.position);
//        System.out.print(this.direction);
//        System.out.println(gene);
        switch (gene) {
            case 0 -> {
                Vector2d newPosition = this.position.add(this.direction.toUnitVector());
                if (this.map.canMoveTo(newPosition)) {
                    Vector2d oldPosition = this.position;
                    this.position = this.map.correctPosition(newPosition);
                    System.out.print(this.cell);
                    System.out.print(" moved from ");
                    System.out.print(oldPosition);
                    System.out.print(" to ");
                    System.out.println(this.position);
                    changePosition(oldPosition, this.position);
                }
            }
            case 1 -> this.direction =  this.direction.next();
            case 2 -> this.direction = this.direction.next().next();
            case 3 -> this.direction = this.direction.next().next().next();
            case 4 -> {
                Vector2d newPosition = this.position.subtract(this.direction.toUnitVector());
                if (this.map.canMoveTo(newPosition)) {
                    Vector2d oldPosition = this.position;
                    this.position = this.map.correctPosition(newPosition);
                    System.out.print(this.cell);
                    System.out.print(" moved from ");
                    System.out.print(oldPosition);
                    System.out.print(" to ");
                    System.out.println(this.position);
                    changePosition(oldPosition, this.position);
                }
            }
            case 5 -> this.direction = this.direction.previous().previous().previous();
            case 6 -> this.direction = this.direction.previous().previous();
            case 7 -> this.direction = this.direction.previous();
            default -> {
            }
        }
        if (gene == 0 || gene == 4) {
//            System.out.print(" moved ");
//            System.out.print(this.position);
//            System.out.println(this.direction);
        }
    }

    private void changePosition(Vector2d oldPosition, Vector2d newPosition) {
        for (IAnimalObserver observer : this.observers) {
            observer.positionChanged(this, oldPosition, newPosition);
        }
    }

    @Override
    public String toString() {
        return "A " + this.position.toString() + " " + this.energy + " " + this.cell.getPosition().toString();
    }
}
