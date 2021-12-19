package simulation.map;

import simulation.Vector2d;

public class UnBoundedMap extends AbstractMap{

    public UnBoundedMap(int width, int height, double jungleRatio) {
        super(width, height, jungleRatio);
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return true;
    }

    @Override
    public Vector2d correctPosition(Vector2d position) {
        return new Vector2d(Math.floorMod(position.x, this.width), Math.floorMod(position.y, this.height));
    }

}
