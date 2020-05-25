import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static java.lang.Math.random;


public class MonteKarlo {
    static final long maxpoint = 100000000L;
    static final double rad = 1;
    static final long startpoint = 5000L;

    public void MathPI(int threads) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(threads);
        long currentPoints = startpoint;
        while (currentPoints <= maxpoint) {
            long ms = System.currentTimeMillis();
            long pointsInCircle = forkJoinPool.invoke(new ForkJoinCheck(currentPoints, threads));
            double p = (double) pointsInCircle / currentPoints * 4;
            System.out.println("PI is " + p + " THREADS - " + threads + " ITERATIONS - " + currentPoints + " TIME - " + (System.currentTimeMillis() - ms)+ "ms");
            currentPoints *= 2;
        }
    }

    public static class ForkJoinCheck extends RecursiveTask<Long> {
        long NumPoints;
        int NumThreads;

        ForkJoinCheck(long currentPoints, int numberOfThreads) {
            this.NumPoints = currentPoints;
            this.NumThreads = numberOfThreads;
        }

        @Override
        protected Long compute() {
            if (NumThreads > 1) {
                return ForkJoinTask.invokeAll(SubTasks())
                        .stream()
                        .mapToLong(ForkJoinTask::join)
                        .sum();
            } else {
                return processing(NumPoints);
            }
        }

        private Long processing(long currentPoints) {
            long pointsInCircle = 0;
            long m = System.currentTimeMillis();
            for (int i = 0; i < currentPoints; i++) {
                double x = random() * rad;
                double y = random() * rad;
                if (vec(x, y) < rad) {
                    pointsInCircle++;
                }
            }
            System.out.println("time "+(System.currentTimeMillis() - m)+" current "+currentPoints);
            return pointsInCircle;
        }

        double vec(double x, double y) {
            return Math.pow((x * x + y * y), 0.5);
        }

        private Collection<ForkJoinCheck> SubTasks() {
            List<ForkJoinCheck> DivTasks = new ArrayList<>();
            DivTasks.add(new ForkJoinCheck(NumPoints/2, NumThreads/2));
            DivTasks.add(new ForkJoinCheck(NumPoints - NumPoints/2, NumThreads - NumThreads/2));
            return DivTasks;
        }

    }

}
