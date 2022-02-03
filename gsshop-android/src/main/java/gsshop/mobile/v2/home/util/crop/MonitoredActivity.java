package gsshop.mobile.v2.home.util.crop;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Iterator;
abstract class MonitoredActivity extends Activity {
    private final ArrayList<LifeCycleListener> listeners = new ArrayList();

    MonitoredActivity() {
    }

    public void addLifeCycleListener(LifeCycleListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public void removeLifeCycleListener(LifeCycleListener listener) {
        this.listeners.remove(listener);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iterator var2 = this.listeners.iterator();

        while(var2.hasNext()) {
            LifeCycleListener listener = (LifeCycleListener)var2.next();
            listener.onActivityCreated(this);
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        Iterator var1 = this.listeners.iterator();

        while(var1.hasNext()) {
            LifeCycleListener listener = (LifeCycleListener)var1.next();
            listener.onActivityDestroyed(this);
        }

    }

    protected void onStart() {
        super.onStart();
        Iterator var1 = this.listeners.iterator();

        while(var1.hasNext()) {
            LifeCycleListener listener = (LifeCycleListener)var1.next();
            listener.onActivityStarted(this);
        }

    }

    protected void onStop() {
        super.onStop();
        Iterator var1 = this.listeners.iterator();

        while(var1.hasNext()) {
            LifeCycleListener listener = (LifeCycleListener)var1.next();
            listener.onActivityStopped(this);
        }

    }

    public static class LifeCycleAdapter implements LifeCycleListener {
        public LifeCycleAdapter() {
        }

        public void onActivityCreated(MonitoredActivity activity) {
        }

        public void onActivityDestroyed(MonitoredActivity activity) {
        }

        public void onActivityStarted(MonitoredActivity activity) {
        }

        public void onActivityStopped(MonitoredActivity activity) {
        }
    }

    public interface LifeCycleListener {
        void onActivityCreated(MonitoredActivity var1);

        void onActivityDestroyed(MonitoredActivity var1);

        void onActivityStarted(MonitoredActivity var1);

        void onActivityStopped(MonitoredActivity var1);
    }
}
