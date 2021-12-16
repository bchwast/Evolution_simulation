package simulation.map;

import simulation.Vector2d;

public class UnBoundedMap extends AbstractMap{

    @Override
    public boolean canMoveTo(Vector2d position) {
        return true;
    }

    @Override
    public Vector2d correctPosition(Vector2d position) {
        return new Vector2d(position.x % this.width, position.y % this.height);
    }

}
