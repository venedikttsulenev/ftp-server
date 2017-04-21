package concurrentUtils;

public class Dispatcher implements Stoppable {
    private final Channel<Stoppable> channel;
    private final ThreadPool threadPool;
    private volatile boolean isAlive;
    public Dispatcher(Channel<Stoppable> channel, ThreadPool threadPool) {
        this.channel = channel;
        this.threadPool = threadPool;
        new Thread(this)
                .start();
    }
    @Override
    public void run() {
        isAlive = true;
        while (isAlive)
            threadPool.execute(channel.take());
    }
    @Override
    public void stop() {
        isAlive = false; /* Check if isAlive equals false already and throw exception??? */
        for (Stoppable task : channel.getElements())
            task.stop();
    }
}
