package simulation.engine;

import simulation.Vector2d;
import simulation.element.Animal;
import simulation.element.Energy;
import simulation.map.Cell;
import simulation.map.IMap;

import java.util.LinkedHashMap;
import java.util.List;

public class SimulationEngine implements IEngine{
    private IMap map;
    private List<Animal> animals;
    private LinkedHashMap<Vector2d, Cell> cells;
    private Energy startEnergy;
    private Energy moveEnergy;
    private Energy plantEnergy;

    public SimulationEngine(IMap map, int animalsAmount, Energy startEnergy, Energy moveEnergy, Energy plantEnergy ) {
        this.map = map;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
    }

    private Animal breed(Animal[] parents) {
        Animal child = new Animal(this.map, parents[0], parents[1]);
        parents[0].breedEnergyDecrease();
        parents[1].breedEnergyDecrease();
        return child;
    }

    @Override
    public void run(){
        for (Animal animal : this.animals) {
            if (animal.getEnergyValue() <= 0) {
                animal.die();
                this.animals.remove(animal);
            }
            else {
                animal.move();
            }
        }

    };
}
