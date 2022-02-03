package gsshop.mobile.v2.sso.utils;

public class Worker {
    public Worker() {
        thread_ = new SimpleThread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    WorkerItem item = queue_.pop();
                    if (onTask_ != null) onTask_.onTask(item.code, item.text);
                }
            }
        });
        thread_.start();
    }

    public void add(int code, String text) {
        queue_.push(new WorkerItem(code, text));
    }

    public void setOnTask(OnTaskListener onTask) {
        onTask_ = onTask;
    }

    private OnTaskListener onTask_ = null;

    private SimpleThread thread_ = null;
    private SuspensionQueue<WorkerItem> queue_ = new SuspensionQueue<>();

    private class WorkerItem {
        int code;
        String text;
        WorkerItem(int code, String text) {
            this.code = code;
            this.text = text;
        }
    }
}
