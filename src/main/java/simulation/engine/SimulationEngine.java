package simulation.engine;

import simulation.Mode;
import simulation.Vector2d;
import simulation.element.AbstractMapElement;
import simulation.element.Animal;
import simulation.element.IMapElement;
import simulation.element.Plant;
import simulation.gui.IMapUpdateObserver;
import simulation.map.Cell;
import simulation.map.IMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimulationEngine implements IEngine{
    private final IMap map;
    private final List<Animal> animals = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<Vector2d, Cell> cells = new ConcurrentHashMap<>();
    private final List<IMapUpdateObserver> observers = new ArrayList<>();
    private final List<Animal> dead = new CopyOnWriteArrayList<>();
    private final int startEnergy;
    private final int moveEnergy;
    private final int plantEnergy;
    private boolean paused = false;
    private int epoque = 0;
    private final boolean magic;
    private int magicLeft = 3;
    private int delay;

    public SimulationEngine(IMap map, int animalsAmount, int startEnergy, int moveEnergy, int plantEnergy, boolean magic,
                            int delay) {
        this.map = map;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        this.magic = magic;
        this.delay = delay;

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
            if (! this.paused) {
                this.epoque++;
                List<Animal> newDead = new ArrayList<>();
                for (Animal animal : this.animals) {
                    if (animal.getEnergy() < this.moveEnergy) {
                        newDead.add(animal);
                        this.dead.add(animal);
                    } else {
                        Vector2d oldPosition = animal.getPosition();
                        animal.move();
                        animal.decreaseEnergy(this.moveEnergy);
                        tryAddCell(animal);
                        tryRemoveCell(oldPosition);
                    }
                }

                for (Animal animal: newDead) {
                    animal.die(this.epoque);
                    this.animals.remove(animal);
                    tryRemoveCell(animal.getPosition());
                }

                if (this.magic && this.magicLeft > 0 && getAnimalsAmount() == 5) {
                    List<Animal> magicAnimals = new ArrayList<>();
                    for (Animal animal : this.animals) {
                        Animal newAnimal = new Animal(this.map, this.startEnergy);
                        newAnimal.setGenotype(animal.getGenotype());
                        this.map.place(newAnimal);
                        magicAnimals.add(newAnimal);
                        this.cells.put(newAnimal.getPosition(), newAnimal.getCell());
                    }
                    this.animals.addAll(magicAnimals);
                    this.magicLeft--;
                    for (IMapUpdateObserver observer: this.observers) {
                        observer.magicHappened(this);
                    }
                }

                for (Cell cell : cells.values()) {
                    if (cell.getFirstElement() instanceof Plant && cell.getElementsAmount() > 1) {
                        cell.eat();
                    }
                    if (cell.getElementsAmount() > 1) {
                        Animal child = cell.tryBreeding(this.startEnergy);
                        if (child != null) {
                            this.map.place(child);
                            this.animals.add(child);
                        }
                    } else {
                        tryRemoveCell(cell.getPosition());
                    }
                }
                growPlants();
            }

            for (IMapUpdateObserver observer : this.observers) {
                observer.positionChanged(this, this.paused);
            }

            try {
                Thread.sleep(this.delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (this.animals.size() <= 0) {
                try {
                    Thread.sleep(99999999);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void tryAddCell(IMapElement element) {
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

    @Override
    public void pause() {
        this.paused = true;
    }

    @Override
    public void resume() {
        this.paused = false;
    }

    @Override
    public int[] getGenotype() {
        return Mode.mode(this.animals);
    }

    @Override
    public int getAnimalsAmount() {
        return this.animals.size();
    }

    @Override
    public int getPlantsAmount() {
        return this.map.getPlantsAmm();
    }

    @Override
    public double getAverageEnergy() {
        return this.animals.stream().mapToDouble(AbstractMapElement::getEnergy).average().orElse(0.0);
    }

    @Override
    public double getAverageLifeLength() {
        return this.dead.stream().mapToDouble(Animal::getAge).average().orElse(0.0);
    }

    @Override
    public double getAverageChildrenAmount() {
        return this.animals.stream().mapToDouble(Animal::getChildrenAmount).average().orElse(0.0);
    }

    @Override
    public int getEpoque() {
        return this.epoque;
    }

    @Override
    public void stop() {
        this.animals.clear();
    }

    @Override
    public ConcurrentHashMap<Vector2d, Cell> getCells() {
        return this.cells;
    }
}
