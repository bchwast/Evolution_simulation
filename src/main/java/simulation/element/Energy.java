package simulation.element;

public class Energy {
    private int value;

    public Energy(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void add(Energy other) {
        this.value += other.value;
    }

    public void subtract(Energy other) {
        this.value -= other.value;
    }


}
