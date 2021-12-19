package simulation;

import simulation.element.Animal;

import java.util.Arrays;
import java.util.List;

public class Mode {
    public static int[] mode(List<Animal> animals) {
        if (animals.size() == 0) {
            return new int[0];
        }

        int[] mode = animals.get(0).getGenotype();
        int maxCount = 0;
        for (int i = 0; i < animals.size(); i++) {
            int count = 0;
            for (Animal animal : animals) {
                if (Arrays.equals(animals.get(i).getGenotype(), animal.getGenotype())) {
                    count++;
                }
            }
            if (count > maxCount) {
                maxCount = count;
                mode = animals.get(i).getGenotype();
            }
        }
        return mode;
    }
}
