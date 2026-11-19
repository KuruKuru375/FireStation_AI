
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
// multithread library
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

public class MainTahap2A {

    // Konfigurasi satu model GA.
    static class ModelConfig {
        String modelId;
        String tipeKromosom;
        boolean pakaiTournament;
        boolean pakaiTwoPoint;
        boolean pakaiMutasiJarak;

        ModelConfig(
                String modelId,
                String tipeKromosom,
                boolean pakaiTournament,
                boolean pakaiTwoPoint,
                boolean pakaiMutasiJarak) {

            this.modelId = modelId;
            this.tipeKromosom = tipeKromosom;
            this.pakaiTournament = pakaiTournament;
            this.pakaiTwoPoint = pakaiTwoPoint;
            this.pakaiMutasiJarak = pakaiMutasiJarak;
        }
    }

    static class RunResult {
        int runIndex;
        int seed;
        Individual individual;
        int generasiAkhir;
        String alasanBerhenti;
        long executionTimeMs;

        RunResult(
                int runIndex,
                int seed,
                Individual individual,
                int generasiAkhir,
                String alasanBerhenti,
                long executionTimeMs) {

            this.runIndex = runIndex;
            this.seed = seed;
            this.individual = individual;
            this.generasiAkhir = generasiAkhir;
            this.alasanBerhenti = alasanBerhenti;
            this.executionTimeMs = executionTimeMs;
        }
    }

    public static void main(String[] args) throws IOException {

        // =====================================================
        // 1. INPUT IDENTITAS EKSPERIMEN UNTUK CSV
        // =====================================================

        Scanner console = new Scanner(System.in);
        CsvOutputForm outputForm = CsvOutputForm.ask(console);

        String stage = outputForm.getStage();
        String experimentId = outputForm.getExperimentId();
        String mapId = outputForm.getMapId();
        String mapType = outputForm.getMapType();

        // =====================================================
        // 2. MEMBUAT 30 SEED YANG SAMA UNTUK SEMUA MODEL
        // =====================================================

        int npmKetua = 24019;
        Random seedGenerator = new Random(npmKetua);

        int[] seeds = new int[30];
        int batasSeed = 1 << 16;

        for (int i = 0; i < seeds.length; i++) {
            seeds[i] = seedGenerator.nextInt(batasSeed + 1);
        }

        String seedSetId = "SEEDSET_" + npmKetua;

        // =====================================================
        // 3. MEMBACA PETA DARI input.txt
        // =====================================================

        int m;
        int n;
        int p;
        char[][] peta;

        try (Scanner inputScanner = new Scanner(new File("input.txt"))) {

            m = inputScanner.nextInt();
            n = inputScanner.nextInt();
            p = inputScanner.nextInt();

            peta = new char[m][n];

            for (int i = 0; i < m; i++) {
                String baris = inputScanner.next();

                for (int j = 0; j < n; j++) {
                    peta[i][j] = baris.charAt(j);
                }
            }
        }

        System.out.println("Ukuran map : " + m + " x " + n);
        System.out.println("Jumlah FS  : " + p);
        System.out.println("Map ID     : " + mapId);

        // =====================================================
        // 4. MEMBACA HYPERPARAMETER DARI param.txt
        // =====================================================

        int maxPopulationSize;
        double crossoverRate;
        double mutationRate;
        double elitismPct;

        try (Scanner paramScanner = new Scanner(new File("param.txt"))) {

            paramScanner.useLocale(Locale.US);

            maxPopulationSize = paramScanner.nextInt();

            // Nilai generation dari param.txt dibaca,
            // tetapi tidak digunakan karena akan dihitung
            // berdasarkan evaluation budget.
            int generationDariFile = paramScanner.nextInt();

            crossoverRate = paramScanner.nextDouble();
            mutationRate = paramScanner.nextDouble();
            elitismPct = paramScanner.nextDouble();
        }

        // =====================================================
        // 5. MENENTUKAN EVALUATION BUDGET
        // =====================================================

        int evaluationBudget;

        if (m <= 10) {
            evaluationBudget = 10000;
        } else if (m <= 20) {
            evaluationBudget = 50000;
        } else if (m <= 40) {
            evaluationBudget = 200000;
        } else {
            evaluationBudget = 500000;
        }

        // Generation otomatis mengikuti population size.
        int totalGeneration = evaluationBudget / maxPopulationSize;

        long batasWaktuMs = 5 * 60 * 1000;

        System.out.println("\n=== PARAMETER EKSPERIMEN ===");
        System.out.println("Population       : " + maxPopulationSize);
        System.out.println("Evaluation Budget: " + evaluationBudget);
        System.out.println("Generation       : " + totalGeneration);
        System.out.println("Crossover Rate   : " + crossoverRate);
        System.out.println("Mutation Rate    : " + mutationRate);
        System.out.println("Elitism          : " + elitismPct);
        System.out.println("Jumlah Seed      : " + seeds.length);

        // =====================================================
        // 6. MENGHITUNG JARAK BFS
        // =====================================================

        FS bfsPeta = new FS(m, n, peta);
        int[][] listJarakTerbaik = bfsPeta.BFS();

        // =====================================================
        // 7. MENENTUKAN MODEL YANG DIJALANKAN
        // =====================================================

        ModelConfig[] daftarModel = {
                // M01: Bit, Roulette, Single Point, Random Move
                new ModelConfig("M01", "bit", false, false, false),

                // M03: Bit, Roulette, Two Point, Random Move
                new ModelConfig("M03", "bit", false, true, false),

                // M04: Bit, Roulette, Two Point, By Distance
                new ModelConfig("M04", "bit", false, true, true)
        };

        // =====================================================
        // 8. MENJALANKAN EKSPERIMEN DAN MENCATAT CSV
        // =====================================================

        try (FireStationCsvLogger logger = new FireStationCsvLogger(outputForm.getOutputPath())) {

            for (ModelConfig model : daftarModel) {

                String modelId = model.modelId;
                String tipeKromosom = model.tipeKromosom;

                boolean pakaiTournament = model.pakaiTournament;
                boolean pakaiTwoPoint = model.pakaiTwoPoint;
                boolean pakaiMutasiJarak = model.pakaiMutasiJarak;

                String selectionName = pakaiTournament ? "tournament" : "roulette";

                String crossoverName = pakaiTwoPoint ? "twopoint" : "singlepoint";

                String mutationName = pakaiMutasiJarak ? "bydistance" : "randommove";

                double totalCost = 0;
                double bestCost = Double.POSITIVE_INFINITY;
                double worstCost = Double.NEGATIVE_INFINITY;

                System.out.println("\n======================================");
                System.out.println("MENJALANKAN MODEL " + modelId);
                System.out.println("Encoding  : " + tipeKromosom);
                System.out.println("Selection : " + selectionName);
                System.out.println("Crossover : " + crossoverName);
                System.out.println("Mutation  : " + mutationName);
                System.out.println("======================================");

                // Setiap model menggunakan himpunan seed yang sama.
                for (int ct = 0; ct < seeds.length; ct++) {

                    Random rnd = new Random(seeds[ct]);

                    FireStationGA ga = new FireStationGA(
                            rnd,
                            tipeKromosom,
                            maxPopulationSize,
                            totalGeneration,
                            elitismPct,
                            crossoverRate,
                            mutationRate,
                            listJarakTerbaik,
                            p,
                            batasWaktuMs);

                    // Mengukur waktu eksekusi.
                    long startTime = System.nanoTime();

                    Individual res = ga.run(
                            pakaiTournament,
                            pakaiTwoPoint,
                            pakaiMutasiJarak);

                    long executionTimeMs = (System.nanoTime() - startTime) / 1_000_000;

                    // Mengambil posisi Fire Station.
                    int[] fsIds = res.getFSIds();

                    String fsPositions = FireStationCsvLogger.formatFsPositions(
                            fsIds,
                            bfsPeta,
                            peta);

                    long evaluationsUsed = (long) ga.generasiAkhir * maxPopulationSize;

                    // Identitas setiap run harus unik.
                    String runId = String.format(
                            "%s_%s_R%02d",
                            mapId,
                            modelId,
                            ct + 1);

                    // Menyimpan hasil ke CSV.
                    logger.logRun(
                            runId,
                            stage,
                            experimentId,
                            modelId,
                            tipeKromosom,
                            selectionName,
                            crossoverName,
                            mutationName,
                            mapId,
                            mapType,
                            m,
                            n,
                            p,
                            maxPopulationSize,
                            totalGeneration,
                            evaluationBudget,
                            crossoverRate,
                            mutationRate,
                            elitismPct,
                            seedSetId,
                            seeds[ct],
                            ct,
                            res.cost,
                            res.fitness,
                            evaluationsUsed,
                            ga.generasiAkhir,
                            executionTimeMs,
                            ga.alasanBerhenti,
                            "SUCCESS",
                            fsPositions);

                    // Rekap statistik cost.
                    totalCost += res.cost;
                    bestCost = Math.min(bestCost, res.cost);
                    worstCost = Math.max(worstCost, res.cost);

                    System.out.printf(
                            "%s | Run %02d/%02d | Seed: %d | Cost: %.5f | Fitness: %.5f | Stop: %s%n",
                            modelId,
                            ct + 1,
                            seeds.length,
                            seeds[ct],
                            res.cost,
                            res.fitness,
                            ga.alasanBerhenti);

                    System.out.println("Koordinat FS:");

                    for (int y = 0; y < m; y++) {
                        for (int x = 0; x < n; x++) {

                            if (peta[y][x] == '.') {

                                int id = bfsPeta.penanda[y][x];

                                for (int fsId : fsIds) {
                                    if (fsId == id) {
                                        System.out.println(
                                                (x + 1) + " " + (y + 1));
                                    }
                                }
                            }
                        }
                    }

                    System.out.println();
                }

                // =================================================
                // 9. REKAP HASIL SATU MODEL
                // =================================================

                System.out.println("\n=== REKAP " + modelId + " ===");

                System.out.printf(
                        "Average Cost : %.5f%n",
                        totalCost / seeds.length);

                System.out.printf(
                        "Best Cost    : %.5f%n",
                        bestCost);

                System.out.printf(
                        "Worst Cost   : %.5f%n",
                        worstCost);
            }
        }

        System.out.println(
                "\nSemua hasil Tahap 2A berhasil dicatat ke CSV.");
    }
}
