package main.resources.helper;

import java.util.ArrayList;

public class Logger {

    private ArrayList<String> log = new ArrayList<String>() {
        @Override
        public String toString() {
            if (log.isEmpty()) return "Nothing here";
            else {
                StringBuilder sb = new StringBuilder();
                for (String s : log) {
                    sb.append(s).append("\n");
                }
                return sb.toString();
            }
        }
    };

    public void write(String s) {
        log.add(s);
    }

    public void print(String s) {
        // TODO add log levels
        // write(s);
        System.out.println(s);
    }

    public void printJobEnd() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        return log.toString();
    }
}
