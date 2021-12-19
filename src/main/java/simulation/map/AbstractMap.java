package simulation.map;

import simulation.Vector2d;
import simulation.element.Animal;
import simulation.element.IMapElement;
import simulation.element.Plant;

import java.util.*;

public abstract class AbstractMap implements IMap{
    protected final int width;
    protected final int height;
    protected Vector2d lowerLeft;
    protected Vector2d upperRight;
    protected Vector2d jungleLowerLeft;
    protected Vector2d jungleUpperRight;
    protected LinkedHashMap<Vector2d, Cell> cells = new LinkedHashMap<>();
    protected List<Vector2d> plantablePositions = new ArrayList<>();
    protected List<Vector2d> plantableJunglePositions = new ArrayList<>();

    public AbstractMap(int width, int height, double jungleRatio) {
        this.width = width;
        this.height = height;
        this.lowerLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(width - 1, height - 1);
        this.jungleLowerLeft = new Vector2d((int) (width * (1 - jungleRatio) / 2), (int) (height * (1 - jungleRatio) / 2));
        this.jungleUpperRight = new Vector2d((int) (this.jungleLowerLeft.x + width * jungleRatio),
                (int) (this.jungleLowerLeft.y + height * jungleRatio));

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Vector2d position = new Vector2d(i, j);
                if (position.precedes(this.jungleUpperRight) && position.follows(this.jungleLowerLeft)) {
                    this.plantableJunglePositions.add(position);
                }
                this.plantablePositions.add(position);
            }
        }
    }

    @Override
    public Vector2d getLowerLeft() {
        return this.lowerLeft;
    }

    @Override
    public Vector2d getUpperRight() {
        return this.upperRight;
    }

    @Override
    public Vector2d getJungleLowerLeft() {
        return this.jungleLowerLeft;
    }

    @Override
    public Vector2d getJungleUpperRight() {
        return this.jungleUpperRight;
    }

    private boolean insertInCell(IMapElement element) {
        Vector2d position = element.getPosition();
        Cell cell = this.cells.get(position);
        if (cell == null) {
            Cell newCell = new Cell(position, element, this);
            if (position.follows(getJungleLowerLeft()) && position.precedes(getJungleUpperRight())) {
                newCell.setJungle();
            }
            this.cells.put(position, newCell);
            element.setCell(newCell);
        }
        else {
            cell.addElement(element);
            element.setCell(cell);
        }
        this.plantableJunglePositions.remove(position);
        this.plantablePositions.remove(position);
        return true;
    }

    @Override
    public boolean place(IMapElement element) {
        return insertInCell(element);
    }

    @Override
    public void removeElement(IMapElement element) {
        tryClearCell(this.cells.get(element.getPosition()));
    }


    @Override
    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition) {
        Cell oldCell = this.cells.get(oldPosition);
//        System.out.println(oldPosition);
//        System.out.println(oldCell);
        oldCell.removeElement(animal);
        tryClearCell(oldCell);
        insertInCell(animal);
    }

    private void tryClearCell(Cell cell) {
        if (cell.getElementsAmount() == 0) {
            if (cell.isJungle()) {
                this.plantableJunglePositions.add(cell.getPosition());
            }
            else {
                this.plantablePositions.add(cell.getPosition());
            }
            this.cells.remove(cell.getPosition());
        }
    }

    @Override
    public List<Cell> generatePlants(int plantEnergy) {
        Random random = new Random();
        List<Cell> cells = new ArrayList<>();
        if (this.plantableJunglePositions.size() > 0) {
            Vector2d junglePosition = this.plantableJunglePositions.get(random.nextInt(this.plantableJunglePositions.size()));
            Plant firstPlant = new Plant(junglePosition, plantEnergy);
            place(firstPlant);
            cells.add(firstPlant.getCell());
        }
        if (this.plantablePositions.size() > 0) {
            Vector2d position = this.plantablePositions.get(random.nextInt(this.plantablePositions.size()));
            Plant secondPlant = new Plant(position, plantEnergy);
            place(secondPlant);
            cells.add(secondPlant.getCell());
        }
        return cells;
    }

    @Override
    public boolean canPlace(Vector2d position) {
        return this.plantablePositions.contains(position) || this.plantableJunglePositions.contains(position);
    }

    @Override
    public boolean isJungle(Vector2d position) {
        return position.follows(getJungleLowerLeft()) && position.precedes(getJungleUpperRight());
    }
}
