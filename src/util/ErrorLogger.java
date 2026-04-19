package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Simple file logger for exception handling examples.
 * Uses Streams to build log lines before writing them to error.log.
 */
public final class ErrorLogger {

    private static final Path LOG_FILE = Path.of("error.log");
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ErrorLogger() {
    }

    public static void log(String message) {
        writeLines(Stream.of(formatHeader(message), ""));
    }

    public static void log(String message, Exception exception) {
        Stream<String> stackTraceLines = exception == null
            ? Stream.empty()
            : Arrays.stream(exception.getStackTrace())
                .map(element -> "    at " + element);

        Stream<String> logLines = Stream.concat(
            Stream.of(
                formatHeader(message),
                exception == null ? "Cause: none" : "Cause: " + exception.getClass().getSimpleName() + " - " + exception.getMessage()
            ),
            Stream.concat(stackTraceLines, Stream.of(""))
        );

        writeLines(logLines);
    }

    private static String formatHeader(String message) {
        return "[" + LocalDateTime.now().format(TIMESTAMP_FORMAT) + "] " + message;
    }

    private static void writeLines(Stream<String> lines) {
        List<String> content = lines.collect(Collectors.toList());
        try {
            Files.write(LOG_FILE, content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ioException) {
            System.err.println("Failed to write to error.log: " + ioException.getMessage());
        }
    }
}
