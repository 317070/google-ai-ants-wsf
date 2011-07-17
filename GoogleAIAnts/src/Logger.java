
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

class Logger {

    private static BufferedWriter out;
    private static boolean logging = true;
    private static final String LOGFILE = "MyLog.txt";

    static void log(String str) {
        if (!logging) {
            return;
        }
        if (out == null) {
            if ((new File(LOGFILE)).exists()) {
                try {
                    out = new BufferedWriter(new FileWriter(LOGFILE));
                } catch (IOException ex) {
                    logging = false;
                }
                log("Started Logging");
            } else {
                logging = false;
                return;
            }
        }

        try {
            String res = "[";
            res += (Timer.getTime()) / 1000;
            res += ":";
            res += (Timer.getTime()) % 1000;
            res += "]=";
            res += str + "\n";
            out.write(res);
        } catch (IOException e) {
        }
    }

    static void log(Exception e) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        log(result.toString());
    }

    static void close() {
        try {
            out.flush();
            out.close();
        } catch (IOException ex) {
        }
        out = null;
    }

    static void main(String[] args) {
        //Test the logger
        log("Testing");
        log("Working");
    }

    static void log(int cost) {
        log("" + cost);
    }
    
    static void flush(){
        try {
            out.flush();
        } catch (IOException ex) {
            Logger.log(ex);
        }
    }
}
