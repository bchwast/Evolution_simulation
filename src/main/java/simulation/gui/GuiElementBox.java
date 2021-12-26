package simulation.gui;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import simulation.element.IMapElement;
import simulation.element.Plant;
import simulation.map.Cell;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class GuiElementBox {
    private final int startEnergy;
    private final double size;
    private final List<Circle> animal;

    public GuiElementBox(double size, int startEnergy) {
        this.size = size;
        this.startEnergy = startEnergy;
        this.animal = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> this.animal.add(new Circle(size / 2, size / 2, (size * 0.7) / 2)));
        this.animal.get(0).setFill(Color.rgb(182,0,0));
        this.animal.get(1).setFill(Color.rgb(255,0,0));
        this.animal.get(2).setFill(Color.rgb(254,109,1));
        this.animal.get(3).setFill(Color.rgb(249,157,6));
        this.animal.get(4).setFill(Color.rgb(255,221,0));
        this.animal.get(5).setFill(Color.rgb(252,253,2));
        this.animal.get(6).setFill(Color.rgb(149,255,0));
        this.animal.get(7).setFill(Color.rgb(0,255,64));
        this.animal.get(8).setFill(Color.rgb(0,230,58));
        this.animal.get(9).setFill(Color.rgb(0,202,51));
    }


    public VBox showBackground(boolean isJungle) {
        VBox vbox;
        if (isJungle) {
            Rectangle jungle = new Rectangle(size, size);
            jungle.setFill(Color.rgb(2,131,1));
            vbox = new VBox(jungle);
            vbox.setAlignment(Pos.CENTER);
            return vbox;
        }
        return null;
    }

    public VBox showElement(Cell cell) {
        IMapElement element = cell.getFirstElement();
        VBox vbox = null;
        if (element instanceof Plant) {
            Rectangle plant = new Rectangle (size * 0.7, size * 0.7);
            plant.setFill(Color.rgb(0,128,255));
            vbox = new VBox(plant);
        }
        else {
            for (int i = 9; i >= 0; i--) {
                if (element.getEnergy() > startEnergy * 0.1 * i) {
                    Circle animalC = new Circle(size / 2, size / 2, (size * 0.7) / 2, this.animal.get(i).getFill());
                    vbox = new VBox(animalC);
                    break;
                }
            }
            if (vbox == null) {
                Circle animalC = new Circle(size / 2, size / 2, (size * 0.7) / 2, this.animal.get(0).getFill());
                vbox = new VBox(animalC);
            }
        }
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }
}
