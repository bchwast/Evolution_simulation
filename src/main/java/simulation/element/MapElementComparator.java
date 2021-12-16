package simulation.element;

import java.util.Comparator;

public class MapElementComparator implements Comparator<IMapElement> {

    @Override
    public int compare(IMapElement firstElement, IMapElement secondElement) {
        if (firstElement instanceof Plant) {
            return 1;
        }
        else if (secondElement instanceof Plant) {
            return -1;
        }

        return Integer.compare(firstElement.getEnergyValue(), secondElement.getEnergyValue());
    }
}
