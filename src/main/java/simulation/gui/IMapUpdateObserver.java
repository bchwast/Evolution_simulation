package simulation.gui;

import simulation.engine.IEngine;

public interface IMapUpdateObserver {
    void positionChanged(IEngine engine, boolean paused);

    void magicHappened(IEngine engine);
}
