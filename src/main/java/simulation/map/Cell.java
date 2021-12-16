package simulation.map;

import simulation.Vector2d;
import simulation.element.Animal;
import simulation.element.IMapElement;
import simulation.element.MapElementComparator;

import java.util.List;
import java.util.TreeSet;

public class Cell {
    private IMap map;
    private TreeSet<IMapElement> elements = new TreeSet<>(new MapElementComparator());
    private Vector2d position;
    private boolean jungle;

    public Cell(Vector2d position, IMapElement element) {
        this.position = position;
        this.elements.add(element);
    }

    public TreeSet<IMapElement> getElements() {
        return this.elements;
    }

    public int getElementsAmount() {
        return this.elements.size();
    }

    public IMapElement getFirstElement() {
        return this.elements.first();
    }

    public void addElement(IMapElement element) {
        this.elements.add(element);
    }

    public void removeElement(IMapElement element) {
        this.elements.remove(element);
    }

    public List<Animal> getHighestEnergyAnimals {

    }

    public Animal[] getParents() {
        Animal firstParent = (Animal) this.getFirstElement();
        removeElement(firstParent);
        Animal secondParent = (Animal) this.getFirstElement();
        addElement(firstParent);

        return new Animal[]{firstParent, secondParent};
    }
}
