package engine.system.loops;

public abstract class Loop implements Runnable {

    private int target;
    private double nsPerTick;
    private long lastTime;
    private long timer;
    private double unprocessed = 0.0;
    private boolean running;
    private int ips, realips;
    private Thread thread;
    private String name;

    public Loop(int target, String name) {
        this.target = target;
        this.name = name;
        nsPerTick = 1000000000.0 / target;
        lastTime = System.nanoTime();
        timer = System.currentTimeMillis();
        thread = new Thread(this, name);
    }

    @Override
    public void run() {
        while (running) {
            nsPerTick = 1000000000.0 / target;
            long now = System.nanoTime();
            unprocessed += (now - lastTime) / nsPerTick;
            lastTime = now;

            if(unprocessed >= 1.0){
                tick();
                unprocessed--;
                ips++;
            }

            try {
                if(!(ips < 0)) Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            if(System.currentTimeMillis() - 1000 > timer){
                timer += 1000;
                realips = ips;
                ips = 0;
            }
        }
    }

    public final void startLoop() {
        if(running) throw new IllegalStateException("Loop Already Running!");
        running = true;
        this.thread.start();
    }

    public final void stopLoop() {
        if(!running) throw new IllegalStateException("Loop is not Running!");
        running = false;
    }

    public abstract void tick();

    public int getTarget() {
        return target;
    }

    public final void setTarget(int target) {
        this.target = target;
        nsPerTick = 1000000000.0 / target;
    }

    public final double getUnprocessed() {
        return unprocessed;
    }

    public final boolean isRunning() {
        return running;
    }

    public final int getIps() {
        return realips;
    }

    public final String getName() {
        return name;
    }
}