package dev.yelpalekshitij.cli;

import java.util.*;

public final class ArgumentParser {
    private ArgumentParser() {
        /* This utility class should not be instantiated */
    }

    private static final Set<String> REQUIRED = Set.of("start", "end", "transportation-method");

    public static Map<String, String> parse(String[] args) {

        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("No arguments provided");
        }

        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (!arg.startsWith("--")) {
                throw new IllegalArgumentException("Invalid argument: " + arg);
            }

            String key;
            String value;
            String stripped = arg.substring(2);

            if (stripped.contains("=")) {
                String[] parts = stripped.split("=", 2);
                key = parts[0];
                value = parts[1];
            } else {
                key = stripped;
                if (i + 1 >= args.length || args[i+1].startsWith("-")) {
                    throw new IllegalArgumentException("Missing value for " + key);
                }
                value = args[++i];
            }

            if (map.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate argument: " + key);
            }

            map.put(key, value);
        }

        REQUIRED.forEach(req -> {
            if (!map.containsKey(req)) {
                throw new IllegalArgumentException("Missing required argument: " + req);
            }
        });

        return Map.copyOf(map);
    }
}
