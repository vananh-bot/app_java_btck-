package Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerService {

    private ScheduledExecutorService scheduler;
    private static SchedulerService instance;

    public static SchedulerService getInstance() {
        if (instance == null) {
            instance = new SchedulerService();
        }
        return instance;
    }

    public void startDeadlineScheduler() {

        if (scheduler != null && !scheduler.isShutdown()) return;

        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("[Scheduler] run");

                new DeadlineNotificationService()
                        .checkUpcomingDeadlines();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.MINUTES);
    }

    public void stopScheduler() {
        if (scheduler != null) scheduler.shutdown();
    }
}