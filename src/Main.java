import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class Main {
    public record Pair<F, S>(F first, S second) {
    }

    public static void main(String[] args) throws IOException {
        try (var lines = Files.lines(Path.of("/home/itamar/projects/1brc/measurements.txt"))) {
            var reports = lines.parallel()
                               .map(stationReport -> new Pair<>(stationReport.substring(0,
                                       stationReport.indexOf(";")),
                                       Double.parseDouble(
                                               stationReport.substring(
                                                       stationReport.indexOf(";") + 1))))
                               .collect(Collectors.groupingByConcurrent(Pair::first,
                                       Collectors.summarizingDouble(Pair::second)));
            var messages = reports.entrySet()
                                  .stream()
                                  .parallel()
                                  .map(entry -> String.format("%s=%.1f/%.1f/%.1f, ",
                                          entry.getKey(),
                                          entry.getValue()
                                               .getMin(),
                                          entry.getValue()
                                               .getAverage(),
                                          entry.getValue()
                                               .getMax()))
                                  .toList();
            System.out.print("{");
            messages.forEach(System.out::print);
            System.out.print("}");
        }
    }
}
