package simulation.map;

import simulation.Vector2d;

public class BoundedMap extends AbstractMap {

    public BoundedMap(int width, int height, double jungleRatio) {
        super(width, height, jungleRatio);
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.precedes(this.getUpperRight()) && position.follows(this.getLowerLeft());
    }

    @Override
    public Vector2d correctPosition(Vector2d position) {
        return position;
    }
}
