package simulation.map;

import simulation.Vector2d;
import simulation.element.Animal;
import simulation.element.IMapElement;
import simulation.element.Plant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cell {
    private IMap map;
    private final List<IMapElement> elements = new ArrayList<>();
    private final Vector2d position;
    private boolean jungle = false;

    public Cell(Vector2d position, IMapElement element, IMap map) {
        this.position = position;
        this.elements.add(element);
        this.map = map;
    }

    public List<IMapElement> getElements() {
        return this.elements;
    }

    public int getElementsAmount() {
        return this.elements.size();
    }

    public IMapElement getFirstElement() {
        return this.elements.get(0);
    }

    public void addElement(IMapElement element) {
        this.elements.add(element);
        Collections.sort(this.elements);
    }

    public void removeElement(IMapElement element) {
        if (! this.elements.remove(element)) {
//            this.elements.remove(element);
            System.out.print(false);
            System.out.println(element);
        };
        System.out.println(true);
    }

    public List<Animal> getHighestEnergyAnimals(){
        List<Animal> animals = new ArrayList<>();
        animals.add((Animal) getFirstElement());
        removeElement(getFirstElement());

        while (getElementsAmount() > 0 && getFirstElement().equals(animals.get(animals.size() - 1))) {
            animals.add((Animal) getFirstElement());
            removeElement(getFirstElement());
        }

        return animals;
    }

    public Animal[] getParents() {
        Animal firstParent = (Animal) this.getFirstElement();
//        this.elements.pollFirst();
        removeElement(firstParent);
        Animal secondParent = (Animal) this.getFirstElement();
        addElement(firstParent);

        return new Animal[]{firstParent, secondParent};
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public void eat() {
        IMapElement food = getFirstElement();
        if (food instanceof Plant) {
//            System.out.println(food);
//            System.out.println(this.elements);
            this.elements.remove(0);
//            System.out.println(getFirstElement());
//            System.out.println(this.elements);
            List<Animal> animals = getHighestEnergyAnimals();

            for (Animal animal : animals) {
                animal.increaseEnergy(((Plant) food).getPlantEnergy() / animals.size());
            }
        }
    }

    private Animal breed(Animal[] parents) {
        Animal child = new Animal(this.map, parents[0], parents[1]);
//        this.map.place(child);
        parents[0].breedEnergyDecrease();
        parents[1].breedEnergyDecrease();
        return child;
    }

    public Animal tryBreeding(int startEnergy) {
        if (getElementsAmount() >= 2) {
            Animal[] parents = getParents();
            if (parents[0].getEnergy() > startEnergy / 2 && parents[1].getEnergy() > startEnergy / 2) {
                return breed(parents);
            }
        }
        return null;
    }

    public boolean isJungle() {
        return this.jungle;
    }

    public void setJungle() {
        this.jungle = true;
    }

    @Override
    public String toString() {
        return this.getElements().toString();
    }
}