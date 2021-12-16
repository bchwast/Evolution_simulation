package simulation.element;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class Genotype {
    private final int[] genotype = new int[32];
    private final Random random = new Random();

    public Genotype() {
        IntStream.range(0, this.genotype.length).forEach(i -> this.genotype[i] = this.random.nextInt(0, 8));

        Arrays.sort(this.genotype);
    }

    public Genotype(Animal firstParent, Animal secondParent) {
        int side = this.random.nextInt(0, 2);
        int stop;
        int parentsEnergy = firstParent.getEnergyValue() + secondParent.getEnergyValue();
        Animal strongerAnimal;
        Animal weakerAnimal;
        int[] strongerAnimalGenotype;
        int[] weakerAnimalGenotype;

        if (firstParent.getEnergyValue() < secondParent.getEnergyValue()) {
            weakerAnimal = firstParent;
            strongerAnimal = secondParent;
        }
        else {
            weakerAnimal = secondParent;
            strongerAnimal = firstParent;
        }

        strongerAnimalGenotype = strongerAnimal.getGenotype();
        weakerAnimalGenotype = weakerAnimal.getGenotype();

        if (side == 0) {
            stop = (strongerAnimal.getEnergyValue() / parentsEnergy) * 32;
            if (stop >= 0) System.arraycopy(strongerAnimalGenotype, 0, this.genotype, 0, stop);
            if (32 - stop >= 0) System.arraycopy(weakerAnimalGenotype, stop, this.genotype, stop, 32 - stop);
        }
        else {
            stop = (weakerAnimal.getEnergyValue() / parentsEnergy) * 32;
            if (stop >= 0) System.arraycopy(weakerAnimalGenotype, 0, this.genotype, 0, stop);
            if (32 - stop >= 0) System.arraycopy(strongerAnimalGenotype, stop, this.genotype, stop, 32 - stop);
        }

        Arrays.sort(this.genotype);
    }

    public int[] getGenotype() {
        return this.genotype;
    }

    public int getGene() {
        return this.genotype[random.nextInt(0, 32)];
    }
}
