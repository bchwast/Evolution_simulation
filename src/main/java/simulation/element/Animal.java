package simulation.element;

import simulation.IAnimalObserver;
import simulation.MapDirection;
import simulation.Vector2d;
import simulation.map.IMap;

import java.util.*;

public class Animal extends AbstractMapElement{
    private MapDirection direction = MapDirection.getRandom();
    private Genotype genotype;
    private int age = 0;
    private int deathDate = -1;
    private final IMap map;
    private final List<IAnimalObserver> observers = new ArrayList<>();
    private final Set<Animal> children = new HashSet<>();

    public Animal(IMap map, int energy) {
        this.map = map;
        addObserver(map);
        this.energy = energy;
        this.genotype = new Genotype();
        Random random = new Random();
        Vector2d position =  new Vector2d(random.nextInt(this.map.getUpperRight().x + 1),
                random.nextInt(this.map.getUpperRight().y + 1));
        while (! this.map.canPlace(position)) {
            position =  new Vector2d(random.nextInt(this.map.getUpperRight().x + 1),
                    random.nextInt(this.map.getUpperRight().y + 1));
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

    public int[] getGenotype() {
        return this.genotype.getGenotype();
    }

    public void setGenotype(int[] genotype){
        this.genotype = new Genotype(genotype);
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

    public void die(int epoque) {
        System.out.print(this);
        System.out.println(" dead");
        this.cell.removeElement(this);
        this.map.removeElement(this);
        this.deathDate = epoque;
    }

    public void move() {
        this.age += 1;
        int gene = this.genotype.getGene();
        switch (gene) {
            case 0 -> {
                Vector2d newPosition = this.position.add(this.direction.toUnitVector());
                if (this.map.canMoveTo(newPosition)) {
                    Vector2d oldPosition = this.position;
                    this.position = this.map.correctPosition(newPosition);
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
                    changePosition(oldPosition, this.position);
                }
            }
            case 5 -> this.direction = this.direction.previous().previous().previous();
            case 6 -> this.direction = this.direction.previous().previous();
            case 7 -> this.direction = this.direction.previous();
            default -> {
            }
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

    public int getAge() {
        return this.age;
    }

    public void addChild(Animal child) {
        this.children.add(child);
    }

    public int getChildrenAmount() {
        return this.children.size();
    }

    public int getDeathDate() {
        return this.deathDate;
    }

    public int getDescendantsAmount() {
        int descendants = 0;
        if (getChildrenAmount() != 0) {
            for (Animal child : this.children) {
                descendants += child.getDescendantsAmount();
                descendants++;
            }
        }
        return descendants;
    }

    public IMap getMap() {
        return this.map;
    }
}
