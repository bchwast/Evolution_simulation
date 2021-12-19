package simulation.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import simulation.Vector2d;
import simulation.element.Animal;
import simulation.element.IMapElement;
import simulation.element.Plant;
import simulation.engine.IEngine;
import simulation.engine.SimulationEngine;
import simulation.map.BoundedMap;
import simulation.map.Cell;
import simulation.map.IMap;
import simulation.map.UnBoundedMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class App extends Application implements IMapUpdateObserver{
    private IMap map1;
    private IMap map2;
    private final GridPane grid1 = new GridPane();
    private final GridPane grid2 = new GridPane();
    private IEngine engine1;
    private IEngine engine2;
    private int cellSize = 15;
    private GuiElementBox elementCreator;
    private final Label genotype1 = new Label();
    private final Label genotype2 = new Label();
    private LineChart<Number, Number> lineChart1;
    private LineChart<Number, Number> lineChart2;
    private List<XYChart.Series<Number, Number>> series1 = new ArrayList<>();
    private List<XYChart.Series<Number, Number>> series2 = new ArrayList<>();
    private List<String> names = new ArrayList<>(List.of("Animals amount", "Plants amount", "Average energy",
            "Average life length", "Average children amount"));

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
                Cell cell = engine.getCellAt(new Vector2d(left + j - 1, up - i + 1));
                if (cell != null) {
//                    GridPane element = this.elementCreator.showCell(object);
//                    Label element = new Label(object.toString());
                    VBox element = this.elementCreator.showElement(cell);

                    GridPane.setHalignment(element, HPos.CENTER);
                    grid.add(element, j, i, 1, 1);
                }
            }
        }
    }

    private void createLineChart() {
        NumberAxis xAxis1 = new NumberAxis();
        NumberAxis yAxis1 = new NumberAxis();
        xAxis1.setLabel("Epoque");
        xAxis1.setAnimated(false);
        xAxis1.setAutoRanging(true);
        xAxis1.setForceZeroInRange(false);
        yAxis1.setLabel("Value");
        yAxis1.setAnimated(false);

        this.lineChart1 = new LineChart<>(xAxis1, yAxis1);
        this.lineChart1.setTitle("Left map");
        this.lineChart1.setAnimated(false);

        NumberAxis xAxis2 = new NumberAxis();
        NumberAxis yAxis2 = new NumberAxis();
        xAxis2.setLabel("Epoque");
        xAxis2.setAnimated(false);
        xAxis2.setAutoRanging(true);
        xAxis2.setForceZeroInRange(false);
        yAxis2.setLabel("Value");
        yAxis2.setAnimated(false);

        this.lineChart2 = new LineChart<>(xAxis2, yAxis2);
        this.lineChart2.setTitle("Right map");
        this.lineChart2.setAnimated(false);
        cleanSeries();
    }

    private void cleanSeries() {
        this.lineChart1.getData().clear();
        this.lineChart2.getData().clear();
        this.series1 = new ArrayList<>();
        this.series2 = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            this.series1.add(new XYChart.Series<Number, Number>());
            this.series1.get(i).setName(this.names.get(i));
            this.lineChart1.getData().add(this.series1.get(i));
            this.series2.add(new XYChart.Series<Number, Number>());
            this.series2.get(i).setName(this.names.get(i));
            this.lineChart2.getData().add(this.series2.get(i));
        }
    }

    @Override
    public void positionChanged(IEngine engine, boolean paused) {
        Platform.runLater(() -> {
            if (engine.equals(this.engine1) && !paused) {
                this.grid1.getChildren().clear();
                createGrid(this.grid1, this.map1, this.engine1);
                this.genotype1.setText(this.engine1.getGenotype());

                this.series1.get(0).getData().add(new XYChart.Data<>(this.engine1.getEpoque(), this.engine1.getAnimalsAmount()));
                this.series1.get(1).getData().add(new XYChart.Data<>(this.engine1.getEpoque(), this.engine1.getPlantsAmount()));
                this.series1.get(2).getData().add(new XYChart.Data<>(this.engine1.getEpoque(), this.engine1.getAverageEnergy()));
                this.series1.get(3).getData().add(new XYChart.Data<>(this.engine1.getEpoque(), this.engine1.getAverageLifeLength()));
                this.series1.get(4).getData().add(new XYChart.Data<>(this.engine1.getEpoque(), this.engine1.getAverageChildrenAmount()));

                for (XYChart.Series<Number, Number> series : this.series1) {
                    if (series.getData().size() > 50) {
                        series.getData().remove(0);
                    }
                }
            }
            else if (engine.equals(this.engine2) && !paused){
                this.grid2.getChildren().clear();
                createGrid(this.grid2, this.map2, this.engine2);
                this.genotype2.setText(this.engine2.getGenotype());

                this.series2.get(0).getData().add(new XYChart.Data<>(this.engine2.getEpoque(), this.engine2.getAnimalsAmount()));
                this.series2.get(1).getData().add(new XYChart.Data<>(this.engine2.getEpoque(), this.engine2.getPlantsAmount()));
                this.series2.get(2).getData().add(new XYChart.Data<>(this.engine2.getEpoque(), this.engine2.getAverageEnergy()));
                this.series2.get(3).getData().add(new XYChart.Data<>(this.engine2.getEpoque(), this.engine2.getAverageLifeLength()));
                this.series2.get(4).getData().add(new XYChart.Data<>(this.engine2.getEpoque(), this.engine2.getAverageChildrenAmount()));

                for (XYChart.Series<Number, Number> series : this.series2) {
                    if (series.getData().size() > 50) {
                        series.getData().remove(0);
                    }
                }
            }
        });
    }

    @Override
    public void magicHappened(IEngine engine) {
        Platform.runLater(() -> {
            if (engine.equals(this.engine1)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Magic has happened on the first map");
                alert.show();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Magic has happened ont he second map");
                alert.show();
            }
        });
    }

    @Override
    public void start(Stage primaryStage) {
        Button start = new Button("Start");
        Label widthL = new Label("Width: ");
        TextField widthT = new TextField();
        widthT.setText("20");
        Label heightL = new Label("Height: ");
        TextField heightT = new TextField();
        heightT.setText("20");
        Label ammL = new Label("Amount of animals: ");
        TextField ammT = new TextField();
        ammT.setText("25");
        Label startEnergyL = new Label("Start energy: ");
        TextField startEnergyT = new TextField();
        startEnergyT.setText("100");
        Label moveEnergyL = new Label("Move energy: ");
        TextField moveEnergyT = new TextField();
        moveEnergyT.setText("5");
        Label plantEnergyL = new Label("Plant energy: ");
        TextField plantEnergyT = new TextField();
        plantEnergyT.setText("20");
        Label jungleRatioL = new Label("Jungle Ratio: ");
        TextField jungleRatioT = new TextField();
        jungleRatioT.setText("0.25");
        Button stop1 = new Button("Stop left map");
        Button resume1 = new Button("Resume left map");
        Button stop2 = new Button("Stop right map");
        Button resume2 = new Button("Resume right map");
        ToggleButton magic1 = new ToggleButton("Left map magic");
        ToggleButton magic2 = new ToggleButton("right map magic");
        VBox controls = new VBox(start, new HBox(widthL, widthT), new HBox(heightL, heightT), new HBox(ammL, ammT) ,
                new HBox(startEnergyL, startEnergyT), new HBox(moveEnergyL, moveEnergyT),
                new HBox(plantEnergyL, plantEnergyT), new HBox(jungleRatioL, jungleRatioT),
                new HBox(stop1, resume1), new HBox(stop2, resume2), new HBox(magic1, magic2));
        createLineChart();
        HBox main = new HBox(new VBox(this.grid1, this.genotype1, this.lineChart1), controls,
                new VBox(this.grid2, this.genotype2, this.lineChart2));

        start.setOnAction(click -> {
            cleanSeries();
            int width = Integer.parseInt(widthT.getText());
            int height = Integer.parseInt(heightT.getText());
            int amm = Integer.parseInt(ammT.getText());
            int startEnergy = Integer.parseInt(startEnergyT.getText());
            int moveEnergy = Integer.parseInt(moveEnergyT.getText());
            int plantEnergy = Integer.parseInt(plantEnergyT.getText());
            double jungleRatio = Double.parseDouble(jungleRatioT.getText());

            this.map1 = new UnBoundedMap(width, height, jungleRatio);
            this.map2 = new BoundedMap(width, height, jungleRatio);
            this.elementCreator = new GuiElementBox(15, startEnergy);
            this.engine1 = new SimulationEngine(this.map1, amm, startEnergy, moveEnergy, plantEnergy, magic1.isSelected());
            this.engine1.addObserver(this);
            this.engine2 = new SimulationEngine(this.map2, amm, startEnergy, moveEnergy, plantEnergy, magic2.isSelected());
            this.engine2.addObserver(this);

            Thread engine1Thread = new Thread(this.engine1);
            engine1Thread.start();
            Thread engine2Thread = new Thread(this.engine2);
            engine2Thread.start();
        });

        stop1.setOnAction(click -> {
            this.engine1.pause();
        });
        resume1.setOnAction(click -> {
            this.engine1.resume();
        });

        stop2.setOnAction(click -> {
            this.engine2.pause();
        });
        resume2.setOnAction(click -> {
            this.engine2.resume();
        });


        Scene scene = new Scene(main, 1920, 1080);
        primaryStage.setTitle("Evolution Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
