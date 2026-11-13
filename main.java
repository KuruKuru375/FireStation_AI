import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int loop = sc.nextInt();
        double total = 0;
        Random init = new Random();

        for (int ct = 1; ct <= loop; ct++) {
            long seed = init.nextLong();
            Random gen = new Random(seed);
            int m = 0, n = 0, p = 0;
            int totalGeneration = 0, maxPopulationSize = 0;
            double crossoverRate = 0.0, mutationRate = 0.0, elitismPct = 0.0;
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
                        System.out.print(peta[i][j] + " ");
                    }
                    System.out.println();
                }
            } catch (FileNotFoundException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            try { // baca data parameter genetik
                sc = new Scanner(new File("param.txt"));
                totalGeneration = sc.nextInt();
                maxPopulationSize = sc.nextInt();
                crossoverRate = sc.nextDouble();
                mutationRate = sc.nextDouble();
                elitismPct = sc.nextDouble();
            } catch (FileNotFoundException e) {
                // TODO: handle exception
                e.printStackTrace();
            }

            FS bfsPeta = new FS(m, n, peta);
            int[][] listJarakTerbaik = bfsPeta.BFS();
            for (int i = 0; i < listJarakTerbaik.length; i++) {
                for (int j = 0; j < listJarakTerbaik[i].length; j++) {
                    System.out.print(listJarakTerbaik[i][j] + " ");
                }
                System.out.println();
            }

            FireStationGA ga = new FireStationGA(gen, maxPopulationSize, totalGeneration, elitismPct, crossoverRate,
                    mutationRate, listJarakTerbaik, p);
            Individual res = ga.run();
            int[] bestRes = res.chromosome;
            // for (int i = 0; i < bestRes.length; i++) {
            // System.out.println(bestRes[i] + "IDX :" + i);
            // }

            // System.out.println("Success");

            // menampilkan jumlah fs dan rata rata cost
            System.out.printf("Jumlah fire station %d \nHasil cost: %.5f\n",p , res.cost);

            // Mengubah ID sel kosong menjadi koordinat (x,y).
            System.out.println("Koordinat FS:");
            for (int y = 0; y < m; y++) {
                for (int x = 0; x < n; x++) {

                    // Hanya sel kosong yang memiliki ID valid.
                    if (peta[y][x] == '.') {

                        int id = bfsPeta.penanda[y][x];

                        // Jika kromosom bernilai 1, berarti ada FS.
                        if (bestRes[id] == 1) {
                            System.out.println((x + 1) + " " + (y + 1));
                        }
                    }
                }
            }
        }
    }
}
