package main.resources.helper;

import java.util.ArrayList;

public class Logger {

    ArrayList<String> log = new ArrayList<String>() {
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

    @Override
    public String toString() {
        return log.toString();
    }
}
