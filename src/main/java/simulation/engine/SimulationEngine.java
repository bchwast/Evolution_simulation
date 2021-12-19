package simulation.engine;

import simulation.Vector2d;
import simulation.element.Animal;
import simulation.element.IMapElement;
import simulation.gui.IMapUpdateObserver;
import simulation.map.Cell;
import simulation.map.IMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SimulationEngine implements IEngine{
    private IMap map;
    private List<Animal> animals = new ArrayList<>();
    private final LinkedHashMap<Vector2d, Cell> cells = new LinkedHashMap<>();
    private final List<IMapUpdateObserver> observers = new ArrayList<>();
    private final List<Animal> dead = new ArrayList<>();
    private int startEnergy;
    private int moveEnergy;
    private int plantEnergy;

    public SimulationEngine(IMap map, int animalsAmount, int startEnergy, int moveEnergy, int plantEnergy ) {
        this.map = map;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;

        for (int i = 0; i < animalsAmount; i++) {
            Animal animal = new Animal(this.map, this.startEnergy);
            this.map.place(animal);
            this.animals.add(animal);
            this.cells.put(animal.getPosition(), animal.getCell());
        }
    }

    @Override
    public void run(){
        while (true) {
            System.out.println(this.animals);
            System.out.println(this.cells);
            List<Animal> newDead = new ArrayList<>();
            for (Animal animal : this.animals) {
                if (animal.getEnergy() < this.moveEnergy) {
                    newDead.add(animal);
                    this.dead.add(animal);
                } else {
                    Vector2d oldPosition = animal.getPosition();
                    animal.move();
                    animal.decreaseEnergy(this.moveEnergy);
                    tryaddCell(animal);
                    tryRemoveCell(oldPosition);
                }
            }

            for (Animal animal: newDead) {
                animal.die();
                this.animals.remove(animal);
                tryRemoveCell(animal.getPosition());
            }

            for (Cell cell : cells.values()) {
                System.out.println(cell.getElements());
                if (cell.getElementsAmount() > 1) {
                    System.out.println("eat ");
                    cell.eat();
                    System.out.println(cell.getElements());
                }
                if (cell.getElementsAmount() > 1) {
                    System.out.println("breed");
                    Animal child = cell.tryBreeding(this.startEnergy);
                    if (child != null) {
                        this.map.place(child);
                        this.animals.add(child);
                        System.out.println(child);
                        System.out.println(cell.getElements());
                    }
                } else {
                    tryRemoveCell(cell.getPosition());
                }
            }

            growPlants();

            for (IMapUpdateObserver observer : this.observers) {
                observer.positionChanged(this);
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (this.animals.size() == 0) {
                try {
                    Thread.sleep(99999999);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void tryaddCell(IMapElement element) {
        if (this.cells.get(element.getPosition()) == null) {
            this.cells.put(element.getPosition(), element.getCell());
        }
    }

    private void tryRemoveCell(Vector2d position) {
        if (this.cells.get(position).getElementsAmount() == 0) {
            this.cells.remove(position);
        }
    }

    private void growPlants() {
        List<Cell> newCells = this.map.generatePlants(this.plantEnergy);
        for (Cell cell : newCells) {
            this.cells.put(cell.getPosition(), cell);
        }
    }

    @Override
    public Cell getCellAt(Vector2d position) {
        return this.cells.get(position);
    }

    @Override
    public void addObserver(IMapUpdateObserver observer) {
        this.observers.add(observer);
    }

}
