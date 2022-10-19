package de.tost.jvisualizer.util;

public class StopWatch {

    private long start;
    private long end;
    private boolean running;

    public void start(){
        if(running)
            return;
        start = System.currentTimeMillis();
        running = true;
    }

    public void stop(){
        if(!running)
            return;
        end = System.currentTimeMillis();
        running = false;
    }

    public boolean isRunning(){
        return running;
    }

    public long getElapsed(){
        if(running){
            return System.currentTimeMillis() - start;
        } else {
            return end-start;
        }
    }

    public void printInfo(){
        System.out.println("--------------StopWatch-------------");
        System.out.println("Elapsed: " + getElapsed() + " ms");
        System.out.println("------------------------------------");
    }
}
