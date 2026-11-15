import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class main {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        // Meminta identitas eksperimen untuk keperluan output CSV dan kerapihan
        // penempatan folder hasil Experimen
        CsvOutputForm outputForm = CsvOutputForm.ask(sc);

        int nmp_ketua = 24019;
        Random rand = new Random(nmp_ketua);
        int[] seed = new int[10];
        int batas = 1 << 16;
        for (int i = 0; i < seed.length; i++) {
            seed[i] = rand.nextInt(batas + 1);
        }

        int totalGeneration = 0, maxPopulationSize = 0;
        double crossoverRate = 0.0, mutationRate = 0.0, elitismPct = 0.0;
        int m = 0, n = 0, p = 0;
        char[][] peta = new char[1][1];
        try { // Baca input soal Fire Station
            sc = new Scanner(new File("input.txt"));
            m = sc.nextInt();
            n = sc.nextInt();
            p = sc.nextInt();
            peta = new char[m][n];

            System.out.println(m + " " + n + " " + p);

            for (int i = 0; i < m; i++) {
                String input = sc.next();
                for (int j = 0; j < n; j++) {
                    peta[i][j] = input.charAt(j);
                }
            }
        } catch (FileNotFoundException e) {
            // TODO: handle
            e.printStackTrace();
        }
        try { // baca data parameter genetik
            sc = new Scanner(new File("param.txt"));
            sc.useLocale(Locale.US);
            maxPopulationSize = sc.nextInt();
            totalGeneration = sc.nextInt();
            crossoverRate = sc.nextDouble();
            mutationRate = sc.nextDouble();
            elitismPct = sc.nextDouble();
        } catch (FileNotFoundException e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        int evaluationBudget;
        if (m <= 10){
            evaluationBudget = 10000; // tiny
        }else if(m <= 20){
            evaluationBudget = 50000; //small
        }else if(m <= 40){
            evaluationBudget = 200000;//normal
        }else {
            evaluationBudget = 500000;//large
        }

        totalGeneration = evaluationBudget / maxPopulationSize;
        long batasWaktuMs = 5 * 60 * 1000; // 5 menit

        FS bfsPeta = new FS(m, n, peta);
        int[][] listJarakTerbaik = bfsPeta.BFS();

        String[] daftarKromosom = { "bit", "int" };
        boolean[] pilihan = { false, true };

        // Mengambil identitas eksperimen dari input tester.
        String stage = outputForm.getStage();
        String experimentId = outputForm.getExperimentId();
        String mapId = outputForm.getMapId();
        String mapType = outputForm.getMapType();

        // Identitas kelompok random seed.
        String seedSetId = "SEEDSET_" + nmp_ketua;

        int modelNumber = 0;

        // Membuka logger untuk seluruh kombinasi model.
        try (FireStationCsvLogger logger = new FireStationCsvLogger(
                outputForm.getOutputPath())) {
            for (String tipeKromosom : daftarKromosom) {
                for (boolean pakaiTournament : pilihan) {
                    for (boolean pakaiTwoPoint : pilihan) {
                        for (boolean pakaiMutasiJarak : pilihan) {

                            // Identitas model.
                            modelNumber++;
                            String modelId = String.format("M%02d", modelNumber);

                            String selectionName = pakaiTournament ? "tournament" : "roulette";

                            String crossoverName = pakaiTwoPoint ? "twopoint" : "singlepoint";

                            String mutationName = pakaiMutasiJarak ? "bydistance" : "randommove";

                            double totalCost = 0;
                            double bestCost = Double.POSITIVE_INFINITY;
                            double worstCost = Double.NEGATIVE_INFINITY;

                            for (int ct = 0; ct < seed.length; ct++) {

                                Random rnd = new Random(seed[ct]);

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

                                // Mengukur waktu eksekusi satu run.
                                long startTime = System.nanoTime();

                                Individual res = ga.run(
                                        pakaiTournament,
                                        pakaiTwoPoint,
                                        pakaiMutasiJarak);

                                long executionTimeMs = (System.nanoTime() - startTime) / 1_000_000;

                                // Mengambil hasil GA.
                                int[] fsIds = res.getFSIds();

                                String fsPositions = FireStationCsvLogger.formatFsPositions(
                                        fsIds,
                                        bfsPeta,
                                        peta);

                                long evaluationsUsed = (long) ga.generasiAkhir * maxPopulationSize;

                                // Menyusun identitas satu kali eksekusi.
                                String runId = String.format(
                                        "%s_%s_R%02d",
                                        mapId,
                                        modelId,
                                        ct);

                                // Menulis hasil satu run ke CSV.
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
                                        seed[ct],
                                        ct,
                                        res.cost,
                                        res.fitness,
                                        evaluationsUsed,
                                        ga.generasiAkhir,
                                        executionTimeMs,
                                        ga.alasanBerhenti,
                                        "SUCCESS",
                                        fsPositions);

                                // Statistik cost dan fitness
                                System.out.printf("cost : %.5f fitness : %.5f%n", res.cost, res.fitness);

                                totalCost += res.cost;
                                bestCost = Math.min(bestCost, res.cost);
                                worstCost = Math.max(worstCost, res.cost);

                                System.out.println("Loop ke " + ct);
                                System.out.println("Koordinat FS:");

                                for (int y = 0; y < m; y++) {
                                    for (int x = 0; x < n; x++) {
                                        if (peta[y][x] == '.') {
                                            int id = bfsPeta.penanda[y][x];
                                            for (int fsId : fsIds) {
                                                if (fsId == id) {
                                                    // print kordinat
                                                    System.out.println((x + 1) + " " + (y + 1));
                                                }
                                            }
                                        }
                                    }
                                }
                                System.out.println();
                                System.out.println("Total generation " + totalGeneration);
                                System.out.println("generasi terakhir : " + ga.generasiAkhir);
                                System.out.println("Success");
                            }

                            // Rekap Hasil
                            System.out.println("Model: " + tipeKromosom + " " + selectionName + " " + crossoverName
                                    + " " + mutationName);
                            System.out.printf("Average cost : %.5f%n", totalCost / seed.length);
                            System.out.printf("Best Cost    : %.5f%n", bestCost);
                            System.out.printf("Worst Cost   : %.5f%n", worstCost);
                        }
                    }
                }
            }
        }
        System.out.println("Semua hasil eksperimen berhasil dicatat ke CSV.");
    }
}
