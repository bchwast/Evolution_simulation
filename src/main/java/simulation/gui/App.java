package simulation.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import simulation.Vector2d;
import simulation.element.IMapElement;
import simulation.engine.IEngine;
import simulation.engine.SimulationEngine;
import simulation.map.BoundedMap;
import simulation.map.Cell;
import simulation.map.IMap;
import simulation.map.UnBoundedMap;

public class App extends Application implements IMapUpdateObserver{
    private IMap map1;
    private IMap map2;
    private final GridPane grid1 = new GridPane();
    private final GridPane grid2 = new GridPane();
    private IEngine engine1;
    private IEngine engine2;
    private int cellSize = 30;
    private GuiElementBox elementCreator;

    @Override
    public void init() {
    }

    private void createGrid(GridPane grid, IMap map, IEngine engine) {
        int left = map.getLowerLeft().x;
        int down = map.getLowerLeft().y;
        int right = map.getUpperRight().x;
        int up = map.getUpperRight().y;

        grid.setGridLinesVisible(false);
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

        grid.setGridLinesVisible(true);

        Label yx = new Label("y \\ x");
        grid.add(yx, 0, 0, 1, 1);
        grid.getColumnConstraints().add(new ColumnConstraints(this.cellSize));
        grid.getRowConstraints().add(new RowConstraints(this.cellSize));
        GridPane.setHalignment(yx, HPos.CENTER);

        for (int i = 1; i <= right - left + 1; i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(this.cellSize));
            Label label = new Label(String.format("%d", left + i - 1));
            GridPane.setHalignment(label, HPos.CENTER);
            grid.add(label, i, 0, 1, 1);
        }

        for (int i = 1; i <= up - down + 1; i++) {
            grid.getRowConstraints().add(new RowConstraints(this.cellSize));
            Label label = new Label(String.format("%d", up - i + 1));
            GridPane.setHalignment(label, HPos.CENTER);
            grid.add(label, 0, i, 1, 1);
        }

        for (int i = 1; i <= up - down + 1; i++) {
            for (int j = 1; j <= right - left + 1; j++) {
                Vector2d position = new Vector2d(left + j - 1, up - i + 1);
                VBox background = this.elementCreator.showBackground(map.isJungle(position));
                GridPane.setHalignment(background, HPos.CENTER);
                grid.add(background, j, i, 1, 1);
                Cell object = engine.getCellAt(new Vector2d(left + j - 1, up - i + 1));
                if (object instanceof Cell) {
//                    GridPane element = this.elementCreator.showCell(object);
//                    Label element = new Label(object.toString());
                    VBox element = this.elementCreator.showElement(object);
                    GridPane.setHalignment(element, HPos.CENTER);
                    grid.add(element, j, i, 1, 1);
                }
            }
        }
    }

    @Override
    public void positionChanged(IEngine engine) {
        Platform.runLater(() -> {
            if (engine.equals(this.engine1)) {
                this.grid1.getChildren().clear();
                createGrid(this.grid1, this.map1, this.engine1);
            }
            else {
                this.grid2.getChildren().clear();
                createGrid(this.grid2, this.map2, this.engine2);
            }
        });
    }

    @Override
    public void start(Stage primaryStage) {
        Button start = new Button("Start");
        TextField width = new TextField();
        TextField height = new TextField();
        TextField amm = new TextField();
        VBox controls = new VBox(start, width, height, amm);
        HBox main = new HBox(this.grid1, controls, this.grid2);

        start.setOnAction(click -> {
            this.map1 = new UnBoundedMap(15, 15, 0.5);
            this.map2 = new BoundedMap(15, 15, 0.5);
            this.elementCreator = new GuiElementBox(30, 200);
            this.engine1 = new SimulationEngine(this.map1, 20, 2000, 10, 20);
            this.engine1.addObserver(this);
            this.engine2 = new SimulationEngine(this.map2, 20, 2000, 10, 20);
            this.engine2.addObserver(this);

            Thread engine2Thread = new Thread(this.engine2);
            engine2Thread.start();
            Thread engine1Thread = new Thread(this.engine1);
            engine1Thread.start();
        });

        Scene scene = new Scene(main, 1920, 1080);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
