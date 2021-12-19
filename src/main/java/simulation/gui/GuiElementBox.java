package simulation.gui;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import simulation.element.IMapElement;
import simulation.element.Plant;
import simulation.map.Cell;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class GuiElementBox {
    private final int startEnergy;
    private final int size;
    private List<Circle> animal;

    public GuiElementBox(int size, int startEnergy) {
        this.size = size;
        this.startEnergy = startEnergy;
        this.animal = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> this.animal.add(new Circle(size / 2, size / 2, (size - 2) / 2)));
        animal.get(0).setFill(Color.rgb(182,0,0));
        animal.get(1).setFill(Color.rgb(255,0,0));
        animal.get(2).setFill(Color.rgb(254,109,1));
        animal.get(3).setFill(Color.rgb(249,157,6));
        animal.get(4).setFill(Color.rgb(255,221,0));
        animal.get(5).setFill(Color.rgb(252,253,2));
        animal.get(6).setFill(Color.rgb(149,255,0));
        animal.get(7).setFill(Color.rgb(0,255,64));
        animal.get(8).setFill(Color.rgb(0,230,58));
        animal.get(9).setFill(Color.rgb(0,202,51));
    }


    public VBox showBackground(boolean isJungle) {
        VBox vbox;
        if (isJungle) {
            Rectangle jungle = new Rectangle(size, size);
            jungle.setFill(Color.rgb(2,131,1));
            vbox = new VBox(jungle);
        }
        else {
            Rectangle desert = new Rectangle(size, size);
            desert.setFill(Color.rgb(192,108,4));
            vbox = new VBox(desert);
        }
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    public VBox showElement(Cell cell) {
        IMapElement element = cell.getFirstElement();
        VBox vbox = null;
        if (element instanceof Plant) {
            Rectangle plant = new Rectangle (size - 5, size - 5);
            plant.setFill(Color.rgb(0,128,255));
            vbox = new VBox(plant);
        }
        else {
            for (int i = 9; i >= 0; i--) {
                if (element.getEnergy() > startEnergy * 0.1 * i) {
                    Circle animalC = new Circle(size / 2, size / 2, (size - 2) / 2, this.animal.get(i).getFill());
                    vbox = new VBox(animalC);
                    break;
                }
            }
            if (vbox == null) {
                Circle animalC = new Circle(size / 2, size / 2, (size - 2) / 2, this.animal.get(0).getFill());
                vbox = new VBox(animalC);
            }
        }
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }
}
