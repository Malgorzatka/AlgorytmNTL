package TalEdgeColoring.graph;

/**
 * Created by Jakub on 16.05.2017.
 */
public class ColoringResult {
    private int chromaticIndex;
    private long time;
    private long memory;

    public ColoringResult(int chromaticIndex, long time, long memory) {
        this.chromaticIndex = chromaticIndex;
        this.time = time;
        this.memory = memory;
    }

    public int getChromaticIndex() {
        return chromaticIndex;
    }

    @Override
    public String toString(){
        return chromaticIndex + ";" + time + ";" + memory;
    }

    public void setChromaticIndex(int chromaticIndex) {
        this.chromaticIndex = chromaticIndex;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }
}
