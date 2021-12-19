package simulation.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import simulation.Vector2d;
import simulation.element.Animal;
import simulation.engine.IEngine;
import simulation.engine.SimulationEngine;
import simulation.map.BoundedMap;
import simulation.map.Cell;
import simulation.map.IMap;
import simulation.map.UnBoundedMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static javafx.application.Platform.exit;

// tworzenie wykresu inspirowane https://levelup.gitconnected.com/realtime-charts-with-javafx-ed33c46b9c8d

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
    private Animal observed = null;
    private boolean paused1 = false;
    private boolean paused2 = true;
    private VBox focus;
    private int[] domGen1;
    private int[] domGen2;
    private boolean showGen1 = false;
    private boolean showGen2 = false;
    private List<List<Double>> stats1 = new ArrayList<>();
    private List<List<Double>> stats2 = new ArrayList<>();

    @Override
    public void init() {
        IntStream.range(0, 5).forEach(i -> {
            this.stats1.add(new ArrayList<>());
            this.stats2.add(new ArrayList<>());
        });
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
                    VBox element = this.elementCreator.showElement(cell);
                    if (cell.getFirstElement() instanceof Animal) {
                        element.setOnMouseClicked((mouseEvent) -> {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Animal details");
                            alert.setHeaderText(Arrays.toString(((Animal) cell.getFirstElement()).getGenotype()));
                            alert.setContentText("Do you want to start following this animal?");
                            ButtonType yes = new ButtonType("Yes");
                            ButtonType no = new ButtonType("No");
                            alert.getButtonTypes().setAll(yes, no);

                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == yes) {
                                this.observed = (Animal) cell.getFirstElement();
                            }
                        });
                        if (this.showGen1 && Arrays.equals(((Animal) cell.getFirstElement()).getGenotype(), this.domGen1)) {
                            Circle circle = (Circle) element.getChildren().get(0);
                            element.getChildren().clear();
                            circle.setStroke(Color.WHITE);
                            element.getChildren().add(circle);
                        }
                        if (this.showGen2 && Arrays.equals(((Animal) cell.getFirstElement()).getGenotype(), this.domGen2)) {
                            Circle circle = (Circle) element.getChildren().get(0);
                            element.getChildren().clear();
                            circle.setStroke(Color.WHITE);
                            element.getChildren().add(circle);
                        }
                    }

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

    private void onFocus(Animal animal) {
        this.focus.getChildren().clear();
        Label children = new Label("This animal has: " + animal.getChildrenAmount() + " children");
        Label descendants = new Label("This animal has: " + animal.getDescendantsAmount() + " descendants");
        Label death;
        if (animal.getDeathDate() == -1) {
            death = new Label("This animal is still alive");
        }
        else {
            death = new Label("This animal died in " + animal.getDeathDate() + " epoque");
        }
        this.focus.getChildren().addAll(children, descendants, death);
    }

    @Override
    public void positionChanged(IEngine engine, boolean paused) {
        Platform.runLater(() -> {
            if (engine.equals(this.engine1) && !paused) {
                this.grid1.getChildren().clear();
                createGrid(this.grid1, this.map1, this.engine1);
                this.domGen1 = this.engine1.getGenotype();
                this.genotype1.setText(Arrays.toString(this.domGen1));

                this.series1.get(0).getData().add(new XYChart.Data<>(this.engine1.getEpoque(), this.engine1.getAnimalsAmount()));
                this.stats1.get(0).add((double) this.engine1.getAnimalsAmount());
                this.series1.get(1).getData().add(new XYChart.Data<>(this.engine1.getEpoque(), this.engine1.getPlantsAmount()));
                this.stats1.get(1).add((double) this.engine1.getPlantsAmount());
                this.series1.get(2).getData().add(new XYChart.Data<>(this.engine1.getEpoque(), this.engine1.getAverageEnergy()));
                this.stats1.get(2).add(this.engine1.getAverageEnergy());
                this.series1.get(3).getData().add(new XYChart.Data<>(this.engine1.getEpoque(), this.engine1.getAverageLifeLength()));
                this.stats1.get(3).add(this.engine1.getAverageLifeLength());
                this.series1.get(4).getData().add(new XYChart.Data<>(this.engine1.getEpoque(), this.engine1.getAverageChildrenAmount()));
                this.stats1.get(4).add(this.engine1.getAverageChildrenAmount());

                for (XYChart.Series<Number, Number> series : this.series1) {
                    if (series.getData().size() > 50) {
                        series.getData().remove(0);
                    }
                }

                if (this.observed != null && this.observed.getMap().equals(this.map1)) {
                    onFocus(this.observed);
                }
            }
            else if (engine.equals(this.engine2) && !paused){
                this.grid2.getChildren().clear();
                createGrid(this.grid2, this.map2, this.engine2);
                this.domGen2 = this.engine2.getGenotype();
                this.genotype2.setText(Arrays.toString(this.domGen2));

                this.series2.get(0).getData().add(new XYChart.Data<>(this.engine2.getEpoque(), this.engine2.getAnimalsAmount()));
                this.stats2.get(0).add((double) this.engine2.getAnimalsAmount());
                this.series2.get(1).getData().add(new XYChart.Data<>(this.engine2.getEpoque(), this.engine2.getPlantsAmount()));
                this.stats2.get(1).add((double) this.engine2.getPlantsAmount());
                this.series2.get(2).getData().add(new XYChart.Data<>(this.engine2.getEpoque(), this.engine2.getAverageEnergy()));
                this.stats2.get(2).add(this.engine2.getAverageEnergy());
                this.series2.get(3).getData().add(new XYChart.Data<>(this.engine2.getEpoque(), this.engine2.getAverageLifeLength()));
                this.stats2.get(3).add(this.engine2.getAverageLifeLength());
                this.series2.get(4).getData().add(new XYChart.Data<>(this.engine2.getEpoque(), this.engine2.getAverageChildrenAmount()));
                this.stats2.get(4).add(this.engine2.getAverageChildrenAmount());

                for (XYChart.Series<Number, Number> series : this.series2) {
                    if (series.getData().size() > 50) {
                        series.getData().remove(0);
                    }
                }

                if (this.observed != null && this.observed.getMap().equals(this.map2)) {
                    onFocus(this.observed);
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
        Button findGenotype1 = new Button("Show dominant\ngenotype on\nleft map");
        Button stopGenotype = new Button("Stop showing");
        Button findGenotype2 = new Button("Show dominant\ngenotype on\nright map");
        Button save1 = new Button("Save stats from\nleft map");
        Button save2 = new Button("Save stats from\nright map");
        VBox controls = new VBox(start, new HBox(widthL, widthT), new HBox(heightL, heightT), new HBox(ammL, ammT) ,
                new HBox(startEnergyL, startEnergyT), new HBox(moveEnergyL, moveEnergyT),
                new HBox(plantEnergyL, plantEnergyT), new HBox(jungleRatioL, jungleRatioT),
                new HBox(stop1, stop2), new HBox(resume1, resume2), new HBox(magic1, magic2),
                new HBox(findGenotype1, stopGenotype, findGenotype2), new HBox(save1, save2));
        createLineChart();
        this.focus = new VBox();
        HBox main = new HBox(new VBox(this.grid1, this.genotype1, this.lineChart1), new VBox(controls, this.focus),
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
            this.paused1 = true;
        });
        resume1.setOnAction(click -> {
            this.engine1.resume();
            this.paused1 = false;
        });

        stop2.setOnAction(click -> {
            this.engine2.pause();
            this.paused2 = true;
        });
        resume2.setOnAction(click -> {
            this.engine2.resume();
            this.paused2 = false;
        });

        findGenotype1.setOnAction(click -> {
            if (this.paused1) {
                this.showGen1 = true;
            }
        });
        findGenotype2.setOnAction(click -> {
            if (this.paused2) {
                this.showGen2 = true;
            }
        });
        stopGenotype.setOnAction(click -> {
            this.showGen1 = false;
            this.showGen2 = false;
        });

        save1.setOnAction(click -> {
            if (this.paused1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(this.stats1.get(0).get(0).toString());
                alert.show();
                try {
                    CSVConverter.convert(this.stats1, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        save2.setOnAction(click -> {
            if (this.paused2) {
                try {
                    CSVConverter.convert(this.stats2, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        Scene scene = new Scene(main, 1920, 1080);
        primaryStage.setTitle("Evolution Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        exit();
    }
}
