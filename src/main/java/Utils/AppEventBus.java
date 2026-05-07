package Utils;

import java.util.ArrayList;
import java.util.List;

public class AppEventBus {

    public interface Listener {
        void onNotificationChanged();
    }

    private static final List<Listener> listeners = new ArrayList<>();

    public static void subscribe(Listener l) {
        listeners.add(l);
        System.out.println("subscribe success. total listener = " + listeners.size());
    }

    public static void emitNotificationChange() {
        System.out.println("emit event...");
        for (Listener l : listeners) {
            l.onNotificationChanged();
        }
    }
}