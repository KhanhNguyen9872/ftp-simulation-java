package com.ftpsimulation.server.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandSplit {
    private Pattern pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");
    private Matcher matcher = null;

    public CommandSplit(String command) {
        this.matcher = pattern.matcher(command);
    }

    public String nextCommand() {
        if (matcher.find()) {
            if (matcher.group(1) != null) {
                // Add the quoted text without quotes
                return matcher.group(1);
            } else {
                // Add the non-quoted text
                return matcher.group(2);
            }
        }
        
        return null;
    }
}
