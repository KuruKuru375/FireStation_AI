import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class CsvOutputForm {

    // Menyimpan identitas eksperimen untuk kebutuhan CSV.
    private final String stage;
    private final String experimentId;
    private final String mapSize;
    private final String mapId;
    private final String mapType;

    // Menyimpan lokasi file CSV.
    private final Path outputPath;

    // Constructor untuk menyimpan seluruh informasi.
    private CsvOutputForm(
            String stage,
            String experimentId,
            String mapSize,
            String mapId,
            String mapType,
            Path outputPath) {

        this.stage = stage;
        this.experimentId = experimentId;
        this.mapSize = mapSize;
        this.mapId = mapId;
        this.mapType = mapType;
        this.outputPath = outputPath;
    }

    // Meminta identitas eksperimen dari tester.
    public static CsvOutputForm ask(Scanner scanner) {

        System.out.println("\n================================");
        System.out.println("          CSV OUTPUT SETUP");
        System.out.println("================================");

        // Memilih tahap eksperimen.
        System.out.println("Pilih tahap eksperimen:");
        System.out.println("1. Hasil Eksperimen Tahap 1");
        System.out.println("2. Hasil Eksperimen Tahap 2");
        System.out.println("3. CSV Integration Test");
        System.out.println("4. Tahap Pengujian");

        int stageChoice = readChoice(scanner, 1, 4);

        String stage;
        String experimentId;
        String mapSize;
        String mapType;
        String parameter = "";
        String populationValue = "";

        // Menentukan folder dan identitas eksperimen.
        switch (stageChoice) {

            case 1:

                stage = "HasilEksperimenTahap1";
                experimentId = "Eksperimen1_KombinasiModel";

                mapSize = askMapSize(scanner);
                mapType = askMapType(scanner);
                break;

            case 2:

                stage = "HasilEksperimenTahap2";

                System.out.println("\nPilih jenis eksperimen:");
                System.out.println("1. Tahap 2A - Pengujian Hyperparameter");
                System.out.println("2. Tahap 2B - Kombinasi Hyperparameter");

                int experimentChoice = readChoice(scanner, 1, 2);

                if (experimentChoice == 1) {

                    System.out.println("\nPilih hyperparameter:");
                    System.out.println("1. Population");
                    System.out.println("2. Crossover");
                    System.out.println("3. Mutation");
                    System.out.println("4. Elitism");

                    int parameterChoice = readChoice(scanner, 1, 4);

                    switch (parameterChoice) {

                        case 1:
                            parameter = "Population";
                            break;

                        case 2:
                            parameter = "Crossover";
                            break;

                        case 3:
                            parameter = "Mutation";
                            break;

                        case 4:
                            parameter = "Elitism";
                            break;
                    }

                    // Khusus Population, tester memilih salah satu
                    // dari lima nilai yang ditentukan.
                    if (parameter.equals("Population")) {
                        populationValue = askPopulationValue(scanner);
                    }

                    experimentId = "Tahap2A_" + parameter;

                } else {

                    experimentId = "Tahap2B_KombinasiHyperparameter";
                }

                mapSize = askMapSize(scanner);
                mapType = askMapType(scanner);
                break;

            case 3:

                // Mode khusus untuk menguji sistem CSV.
                stage = "TEST";
                experimentId = "CSV_Integration_Test";
                mapSize = "Test";
                mapType = "TEST_ONLY";

                System.out.println("\nMode CSV Integration Test.");
                break;

            default:

                // Pengujian model terbaik menggunakan peta Large.
                stage = "TahapPengujian";
                experimentId = "ModelTerbaik_Large";
                mapSize = "Large";

                System.out.println("\nUkuran peta ditetapkan: Large");

                mapType = askMapType(scanner);
                break;
        }

        // Meminta ID kasus peta.
        System.out.print("\nMasukkan ID peta: ");
        String mapId = readMapId(scanner);

        // Menentukan lokasi output berdasarkan tahap.
        Path outputPath;

        switch (stageChoice) {

            case 1:

                outputPath = Paths.get(
                        "Experiment_Results",
                        stage,
                        mapSize,
                        mapId,
                        "results.csv");
                break;

            case 2:

                if (experimentId.startsWith("Tahap2A")) {

                    // Khusus Tahap 2A Population, tambahkan
                    // folder berdasarkan nilai Population.
                    if (parameter.equals("Population")) {

                        outputPath = Paths.get(
                                "Experiment_Results",
                                stage,
                                "Tahap2A",
                                parameter,
                                populationValue,
                                mapSize,
                                mapId,
                                "results.csv");

                    } else {

                        // Hyperparameter Tahap 2A lainnya
                        // tetap menggunakan struktur sebelumnya.
                        outputPath = Paths.get(
                                "Experiment_Results",
                                stage,
                                "Tahap2A",
                                parameter,
                                mapSize,
                                mapId,
                                "results.csv");
                    }

                } else {

                    // Struktur Tahap 2B tidak berubah.
                    outputPath = Paths.get(
                            "Experiment_Results",
                            stage,
                            "Tahap2B",
                            mapSize,
                            mapId,
                            "results.csv");
                }

                break;

            case 3:

                outputPath = Paths.get(
                        "Experiment_Results",
                        "CSV_Integration_Test",
                        mapSize,
                        mapId,
                        "results.csv");
                break;

            default:

                outputPath = Paths.get(
                        "Experiment_Results",
                        stage,
                        mapSize,
                        mapId,
                        "results.csv");
                break;
        }

        // Menampilkan ringkasan pilihan tester.
        System.out.println("\n================================");
        System.out.println("        OUTPUT INFORMATION");
        System.out.println("================================");
        System.out.println("Stage       : " + stage);
        System.out.println("Experiment  : " + experimentId);

        if (experimentId.equals("Tahap2A_Population")) {
            System.out.println("Population  : " + populationValue);
        }

        System.out.println("Map Size    : " + mapSize);
        System.out.println("Map ID      : " + mapId);
        System.out.println("Map Type    : " + mapType);
        System.out.println("Output      : " + outputPath);
        System.out.println("================================\n");

        return new CsvOutputForm(
                stage,
                experimentId,
                mapSize,
                mapId,
                mapType,
                outputPath);
    }

    // Meminta ukuran peta untuk Tahap 1 dan Tahap 2.
    private static String askMapSize(Scanner scanner) {

        System.out.println("\nPilih ukuran peta:");
        System.out.println("1. Tiny");
        System.out.println("2. Small");
        System.out.println("3. Normal");

        int choice = readChoice(scanner, 1, 3);

        switch (choice) {
            case 1:
                return "Tiny";

            case 2:
                return "Small";

            default:
                return "Normal";
        }
    }

    // Meminta nilai Population khusus Tahap 2A.
    private static String askPopulationValue(Scanner scanner) {

        System.out.println("\nPilih nilai Population:");
        System.out.println("1. 50");
        System.out.println("2. 100");
        System.out.println("3. 250");
        System.out.println("4. 500");
        System.out.println("5. 1000");

        int choice = readChoice(scanner, 1, 5);

        switch (choice) {
            case 1:
                return "50";

            case 2:
                return "100";

            case 3:
                return "250";

            case 4:
                return "500";

            default:
                return "1000";
        }
    }

    // Meminta jenis peta.
    private static String askMapType(Scanner scanner) {

        System.out.println("\nPilih jenis peta:");
        System.out.println("1. Acak");
        System.out.println("2. Klaster");
        System.out.println("3. Labirin");

        int choice = readChoice(scanner, 1, 3);

        switch (choice) {
            case 1:
                return "Acak";

            case 2:
                return "Klaster";

            default:
                return "Labirin";
        }
    }

    // Membaca pilihan angka dan memvalidasi input.
    private static int readChoice(
            Scanner scanner,
            int minimum,
            int maximum) {

        while (true) {

            System.out.print("Pilihan: ");

            String input = scanner.nextLine().trim();

            try {

                int choice = Integer.parseInt(input);

                if (choice >= minimum && choice <= maximum) {
                    return choice;
                }

            } catch (NumberFormatException e) {
                // Meminta input ulang jika bukan angka.
            }

            System.out.println(
                    "Pilihan tidak valid. Masukkan angka "
                            + minimum + " sampai " + maximum + ".");
        }
    }

    // Membaca ID peta dan membatasi karakter yang diperbolehkan.
    private static String readMapId(Scanner scanner) {

        while (true) {

            String mapId = scanner.nextLine().trim();

            if (mapId.matches("[a-zA-Z0-9_-]+")) {
                return mapId;
            }

            System.out.print(
                    "ID tidak valid. Gunakan huruf, angka, "
                            + "underscore, atau tanda minus: ");
        }
    }

    // Getter identitas eksperimen.
    public String getStage() {
        return stage;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public String getMapSize() {
        return mapSize;
    }

    public String getMapId() {
        return mapId;
    }

    public String getMapType() {
        return mapType;
    }

    public Path getOutputPath() {
        return outputPath;
    }
}