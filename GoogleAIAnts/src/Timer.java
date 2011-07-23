
class Timer {

    private static long start = 0;

    static void start() {
        start = System.currentTimeMillis();
    }

    static int getTime() {
        return (int) (System.currentTimeMillis() - start);
    }

    static int getTimeLeft() {
        return (int) (GameParam.turnTime - System.currentTimeMillis() + start);
    }
    
    private static int tic = 0;
    static void tic(){
        tic = getTime();
    }
    static int toc(){
        return getTime()-tic;
    }
}
