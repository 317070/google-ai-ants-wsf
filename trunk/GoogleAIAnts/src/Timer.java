
class Timer {

    private static long start = 0;

    static void start() {
        start = System.currentTimeMillis();
    }

    static int getTime() {
        return (int) (System.currentTimeMillis() - start);
    }

    static int getTimeLeft() {
        return (int) (GameParam.TIMEPERTURN - System.currentTimeMillis() + start);
    }
}
