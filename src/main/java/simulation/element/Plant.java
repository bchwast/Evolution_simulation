package simulation.element;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import simulation.Vector2d;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Plant extends AbstractMapElement{
    private final int plantEnergy;

    public Plant(Vector2d position, int plantEnergy) {
        this.position = position;
        this.plantEnergy = plantEnergy;
        this.energy = Integer.MAX_VALUE;
    }

    public int getPlantEnergy() {
        return this.plantEnergy;
    }

    @Override
    public String toString() {
        return "P " + this.position.toString();
    }
}
