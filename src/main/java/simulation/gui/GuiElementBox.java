package simulation.gui;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import simulation.element.IMapElement;
import simulation.element.Plant;
import simulation.map.Cell;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class GuiElementBox {
    private int startEnergy;
    private int size;
    private Image desert = null;
    private Image jungle = null;
    private Image plant = null;
    private Image under10 = null;
    private Image under20 = null;
    private Image under30 = null;
    private Image under40 = null;
    private Image under50 = null;
    private Image under60 = null;
    private Image under70 = null;
    private Image under80 = null;
    private Image under90 = null;
    private Image over90 = null;

    public GuiElementBox(int size, int startEnergy) {
        this.size = size;
        this.startEnergy = startEnergy;
        try {
            this.desert = new Image(new FileInputStream("src/main/resources/desert.png"));
            this.jungle = new Image(new FileInputStream("src/main/resources/jungle.png"));
            this.plant = new Image(new FileInputStream("src/main/resources/food.png"));
            this.under10 = new Image(new FileInputStream("src/main/resources/0_10.png"));
            this.under20 = new Image(new FileInputStream("src/main/resources/10_20.png"));
            this.under30 = new Image(new FileInputStream("src/main/resources/20_30.png"));
            this.under40 = new Image(new FileInputStream("src/main/resources/30_40.png"));
            this.under50 = new Image(new FileInputStream("src/main/resources/40_50.png"));
            this.under60 = new Image(new FileInputStream("src/main/resources/50_60.png"));
            this.under70 = new Image(new FileInputStream("src/main/resources/60_70.png"));
            this.under80 = new Image(new FileInputStream("src/main/resources/70_80.png"));
            this.under90 = new Image(new FileInputStream("src/main/resources/80_90.png"));
            this.over90 = new Image(new FileInputStream("src/main/resources/90_100.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public VBox showBackground(boolean jungle) {
        ImageView imageView;
        if (jungle) {
            imageView = new ImageView(this.jungle);
        }
        else {
            imageView = new ImageView(this.desert);
        }
        imageView.setFitHeight(size);
        imageView.setFitWidth(size);
        VBox vbox = new VBox(imageView);
        vbox.setAlignment(Pos.CENTER);

        return vbox;
    }

    public VBox showElement(Cell cell) {
        IMapElement element = cell.getFirstElement();
        ImageView imageView;
        if (element instanceof Plant) {
            imageView = new ImageView(this.plant);
        }
        else {
            if (element.getEnergy() <= 0.1 * this.startEnergy) {
                imageView = new ImageView(this.under10);
            }
            else if (element.getEnergy() <= 0.2 * this.startEnergy) {
                imageView = new ImageView(this.under20);
            }
            else if (element.getEnergy() <= 0.3 * this.startEnergy) {
                imageView = new ImageView(this.under30);
            }
            else if (element.getEnergy() <= 0.4 * this.startEnergy) {
                imageView = new ImageView(this.under40);
            }
            else if (element.getEnergy() <= 0.5 * this.startEnergy) {
                imageView = new ImageView(this.under50);
            }
            else if (element.getEnergy() <= 0.6 * this.startEnergy) {
                imageView = new ImageView(this.under60);
            }
            else if (element.getEnergy() <= 0.7 * this.startEnergy) {
                imageView = new ImageView(this.under70);
            }
            else if (element.getEnergy() <= 0.8 * this.startEnergy) {
                imageView = new ImageView(this.under80);
            }
            else if (element.getEnergy() <= 0.9 * this.startEnergy) {
                imageView = new ImageView(this.under90);
            }
            else {
                imageView = new ImageView(this.over90);
            }
        }
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        VBox vbox = new VBox(imageView);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }
}
