package TalEdgeColoring;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;

/**
 * Created by Jakub on 17.05.2017.
 */
public class MemoryHelper {
    public void resetPeak(){
        ManagementFactory.getMemoryPoolMXBeans().forEach(f -> f.resetPeakUsage());
    }

    public long getPeakUsage(){
        long usage = 0;
        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean pool : pools) {
            usage += pool.getPeakUsage().getUsed();
        }
        return usage;
    }
}
