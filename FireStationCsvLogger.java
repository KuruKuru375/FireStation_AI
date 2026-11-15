import OutputTools.csv.CsvWriter;
import java.io.IOException;
import java.nio.file.Path;

public class FireStationCsvLogger implements AutoCloseable {

    private final CsvWriter csv;

    // Header CSV eksperimen Fire Stations.
    private static final String[] HEADERS = {
        "run_id",
        "stage",
        "experiment_id",
        "model_id",
        "representation",
        "selection",
        "crossover",
        "mutation_operator",
        "map_id",
        "map_type",
        "grid_rows",
        "grid_cols",
        "num_fs",
        "population",
        "generations",
        "evaluation_budget",
        "pc",
        "pm",
        "elitism_rate",
        "seed_set_id",
        "seed",
        "run_number",
        "best_cost",
        "best_fitness",
        "evaluations_used",
        "generations_completed",
        "execution_time_ms",
        "termination_reason",
        "run_status",
        "fs_positions"
    };

    // Membuat logger dan membuka file CSV.
    public FireStationCsvLogger(Path filePath) throws IOException {
        csv = new CsvWriter(filePath, HEADERS);
    }

    // Mencatat hasil satu kali eksekusi GA.
    public void logRun(
            String runId,
            String stage,
            String experimentId,
            String modelId,
            String representation,
            String selection,
            String crossover,
            String mutationOperator,
            String mapId,
            String mapType,
            int gridRows,
            int gridCols,
            int numFS,
            int population,
            int generations,
            int evaluationBudget,
            double pc,
            double pm,
            double elitismRate,
            String seedSetId,
            int seed,
            int runNumber,
            double bestCost,
            double bestFitness,
            long evaluationsUsed,
            int generationsCompleted,
            long executionTimeMs,
            String terminationReason,
            String runStatus,
            String fsPositions
    ) throws IOException {

        csv.writeRow(
            runId,
            stage,
            experimentId,
            modelId,
            representation,
            selection,
            crossover,
            mutationOperator,
            mapId,
            mapType,
            gridRows,
            gridCols,
            numFS,
            population,
            generations,
            evaluationBudget,
            pc,
            pm,
            elitismRate,
            seedSetId,
            seed,
            runNumber,
            bestCost,
            bestFitness,
            evaluationsUsed,
            generationsCompleted,
            executionTimeMs,
            terminationReason,
            runStatus,
            fsPositions
        );
    }

    // Mengubah ID sel FS menjadi koordinat (x,y).
    public static String formatFsPositions(int[] fsIds,FS bfsPeta,char[][] peta) {
        StringBuilder positions = new StringBuilder();
        int rows = peta.length;
        int cols = peta[0].length;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                // Hanya memeriksa sel kosong.
                if (peta[y][x] == '.') {
                    int cellId = bfsPeta.penanda[y][x];
                    for (int fsId : fsIds) {
                        if (fsId == cellId) {
                            if (positions.length() > 0) {
                                positions.append(";");
                            }
                            positions.append("(")
                                     .append(x + 1)
                                     .append(",")
                                     .append(y + 1)
                                     .append(")");
                        }
                    }
                }
            }
        }
        return positions.toString();
    }

    // Menutup file CSV.
    @Override
    public void close() throws IOException {
        csv.close();
    }
}
