package com.dev;


import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.LogEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.List;

@Plugin(name = "CustomHttpAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class CustomHttpAppender extends AbstractAppender {

    private final String endpoint;
    private final Path localLogFile;
    private final boolean append;

    protected CustomHttpAppender(String name, String endpoint, Path localLogFile, boolean append) {
        super(name, null, PatternLayout.createDefaultLayout(), true);
        this.endpoint = endpoint;
        this.localLogFile = localLogFile;
        this.append = append;
        createLogFileIfNotExists();
    }

    @PluginFactory
    public static CustomHttpAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginAttribute("endpoint") String endpoint,
            @PluginAttribute("localLogFile") String localLogFile,
            @PluginAttribute("append") boolean append) {

        return new CustomHttpAppender(name, endpoint, Paths.get(localLogFile), append);
    }

    @Override
    public void append(LogEvent event) {
        String logMessage = new String(getLayout().toByteArray(event));

        if (isInternetAvailable()) {
            sendLogToEndpoint(logMessage);
            flushLocalLogs();
        } else {
            writeLogToLocalFile(logMessage);
        }
    }

    private boolean isInternetAvailable() {
        try {
            URL url = new URL("http://www.google.com");
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
            urlConnect.setConnectTimeout(1000);
            urlConnect.getContent();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void sendLogToEndpoint(String logMessage) {
        System.out.println("sending log through http---: "+logMessage);
    }

    private void writeLogToLocalFile(String logMessage) {
        try {
            createLogFileIfNotExists();
            try (BufferedWriter writer = Files.newBufferedWriter(localLogFile, append ? StandardOpenOption.APPEND : StandardOpenOption.CREATE)) {
                writer.write(logMessage);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void flushLocalLogs() {
        if (!Files.exists(localLogFile)) {
            return;
        }

        try {
            List<String> logs = Files.readAllLines(localLogFile);
            for (String log : logs) {
                sendLogToEndpoint(log);
            }
            Files.delete(localLogFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createLogFileIfNotExists() {
        try {
            if (!Files.exists(localLogFile)) {
                Files.createDirectories(localLogFile.getParent());
                Files.createFile(localLogFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

