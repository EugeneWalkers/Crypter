import java.util.ArrayList;
import java.util.List;

public class LGramData {
    private final List<Integer> distances;
    private final int length;
    private int frequency;


    {
        distances = new ArrayList<>();
        frequency = 0;
    }

    public LGramData(final int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public List<Integer> getDistances() {
        return distances;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void add(final int distance) {
        distances.add(distance);
    }

    public void add(final List<Integer> distances) {
        this.distances.addAll(distances);
    }

    @Override
    public String toString() {
        return distances.toString()+"\nlength = " + length + "\nfrequency = " + frequency;
    }
}
