import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int nmp_ketua = 24019;
        Random rand = new Random(nmp_ketua);
        int[] seed = new int[30];
        int batas = 1 << 16;
        for (int i = 0; i < seed.length; i++) {
            seed[i] = rand.nextInt(batas + 1);
        }

        int totalGeneration = 0, maxPopulationSize = 0;
        double crossoverRate = 0.0, mutationRate = 0.0, elitismPct = 0.0;
        String tipeKromosom = "int";
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
            // TODO: handle exception
            e.printStackTrace();
        }
        try { // baca data parameter genetik
            sc = new Scanner(new File("param.txt"));
            maxPopulationSize = sc.nextInt();
            totalGeneration = sc.nextInt();
            crossoverRate = sc.nextDouble();
            mutationRate = sc.nextDouble();
            elitismPct = sc.nextDouble();
        } catch (FileNotFoundException e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        BFS bfsPeta = new BFS(m, n, peta);
        int[][] listJarakTerbaik = bfsPeta.BFS();

        double total = 0;

        for (int ct = 0; ct < 30; ct++) {
            Random rnd = new Random(seed[ct]);

            FireStationGA ga = new FireStationGA(rnd, maxPopulationSize, totalGeneration, elitismPct, crossoverRate,mutationRate, listJarakTerbaik, p, tipeKromosom);
            Individual res = ga.run();
            int[] bestRes = res.chromosome;

            // menampilkan jumlah fs dan rata rata cost
            System.out.printf("Jumlah fire station %d \nHasil cost: %.5f\n", p, res.cost);

            // Mengubah ID sel kosong menjadi koordinat (x,y).
            int[] fsIds = res.getFSIds();
            System.out.println("Koordinat FS:");
            for (int y = 0; y < m; y++) {
                for (int x = 0; x < n; x++) {

                    // Hanya sel kosong yang memiliki ID valid.
                    if (peta[y][x] == '.') {

                        int id = bfsPeta.penanda[y][x];

                        // Jika kromosom bernilai 1, berarti ada FS.
                        for (int fsId : fsIds) {
                            if (fsId == id) {
                                System.out.println((x + 1) + " " + (y + 1));
                            }
                        }
                    }
                }
            }
            System.out.println();
        }
    }
}
