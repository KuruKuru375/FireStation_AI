import java.io.File;                    
import java.io.FileNotFoundException;   
import java.util.InputMismatchException; 
import java.util.Locale;               
import java.util.Random;               
import java.util.Scanner;               

public class main {
    public static void main(String[] args) {
        Scanner pilihan = new Scanner(System.in); // Baca angka dari keyboard.

        try {
            System.out.println("Pilih kromosom"); // Tampilkan menu pertama.
            System.out.println("1. Bit");
            System.out.println("2. Integer");
            int pilihKromosom = pilihan.nextInt(); // Simpan pilihan kromosom.

            System.out.println("Pilih crossover"); // Tampilkan menu kedua.
            System.out.println("1. One-point");
            System.out.println("2. Two-point");
            int pilihCrossover = pilihan.nextInt(); // Simpan pilihan crossover.

            System.out.println("Pilih parent selection"); // Tampilkan menu ketiga.
            System.out.println("1. Roulette");
            System.out.println("2. Tournament");
            int pilihSeleksi = pilihan.nextInt(); // Simpan pilihan seleksi induk.

            // mutasi untuk nanti


            if ((pilihKromosom != 1 && pilihKromosom != 2)  // Cek pilihan kromosom.
                    || (pilihCrossover != 1 && pilihCrossover != 2) // Cek crossover.
                    || (pilihSeleksi != 1 && pilihSeleksi != 2)) {  // Cek seleksi.
                System.out.println("Setiap pilihan harus berupa angka 1 atau 2.");
                return; // Akhiri program kalau ada pilihan di luar menu.  

                //KALAU MUTASI UDH ADA TINGGAL TAMBAH
            }

            String tipeKromosom = "bit"; // Pilihan 1 memakai kromosom bit.
            if (pilihKromosom == 2) {
                tipeKromosom = "int"; // Pilihan 2 memakai kromosom integer.
            }

            boolean pakaiTwoPoint = pilihCrossover == 2;  // True jika memilih two-point.
            boolean pakaiTournament = pilihSeleksi == 2; // True jika memilih tournament.

            jalankanGA(tipeKromosom, pakaiTournament, pakaiTwoPoint); // Mulai percobaan.
        } catch (InputMismatchException e) {
            System.out.println("Periksa angka di menu, input.txt, dan param.txt.");
        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan. " + e.getMessage());
        }
    }

    private static void jalankanGA(String tipeKromosom, boolean pakaiTournament, //KALAU  MUTASI UDH ADA TAMBAH
                                  boolean pakaiTwoPoint) throws FileNotFoundException {

        // Kedua Scanner file ditutup otomatis saat blok try selesai.
        try (Scanner input = new Scanner(new File("input.txt"));
             Scanner param = new Scanner(new File("param.txt")).useLocale(Locale.ROOT)) {

            int m = input.nextInt(); // Baca jumlah baris peta.
            int n = input.nextInt(); // Baca jumlah kolom peta.
            int p = input.nextInt(); // Baca jumlah fire station yang harus dipasang.
            char[][] peta = new char[m][n]; // Siapkan array untuk menyimpan peta.

            for (int y = 0; y < m; y++) { // Baca peta baris demi baris.
                String baris = input.next(); // Ambil satu baris, misalnya ".H..T".
                for (int x = 0; x < n; x++) { // Periksa setiap karakter pada baris itu.
                    peta[y][x] = baris.charAt(x); // Simpan karakter ke koordinat array.
                }
            }

            int maxPopulationSize = param.nextInt(); // Jumlah individu per generasi.
            int totalGeneration = param.nextInt();   // Jumlah generasi yang dievaluasi.
            double crossoverRate = param.nextDouble(); // Peluang crossover per anak.
            double mutationRate = param.nextDouble();  // Peluang mutasi per anak.
            double elitismPct = param.nextDouble();    // Bagian populasi terbaik yang diteruskan.

            BFS bfsPeta = new BFS(m, n, peta); // Siapkan BFS berdasarkan peta.
            int[][] listJarakTerbaik = bfsPeta.BFS(); // Hitung matriks jarak sekali untuk 30 run.

            int npmKetua = 24019; // Basis untuk menghasilkan urutan seed dari npm ketua.
            Random pembuatSeed = new Random(npmKetua); // Mulai urutan seed dari basis yang sama.
            int batasSeed = 1 << 16; // Nilainya 2 pangkat 16, yaitu 65536.

            for (int run = 1; run <= 30; run++) { // Ulangi model yang dipilih sebanyak 30 kali.
                int seed = pembuatSeed.nextInt(batasSeed + 1); // Ambil seed untuk run ini.
                Random rnd = new Random(seed); // Buat generator acak khusus untuk run ini.

                // Constructor menerima parameter GA, matriks jarak, dan tipe kromosom.
                FireStationGA ga = new FireStationGA(
                        rnd, maxPopulationSize, totalGeneration, elitismPct,
                        crossoverRate, mutationRate, listJarakTerbaik, p, tipeKromosom
                );

                // Kirim pilihan seleksi dan crossover ke method yang menjalankan GA.
                Individual hasil = ga.run(pakaiTournament, pakaiTwoPoint);
                int[] fsIds = hasil.getFSIds(); // Ambil ID sel yang dipilih untuk menjadi FS.

                System.out.println("\nPercobaan " + run + " | Seed " + seed); // Tampilkan identitas run.
                System.out.printf(Locale.ROOT, "Jumlah FS = %d | Cost = %.5f%n", p, hasil.cost);
                System.out.println("Koordinat FS (x y)");

                for (int y = 0; y < m; y++) { // Telusuri setiap baris peta.
                    for (int x = 0; x < n; x++) { // Telusuri setiap kolom pada baris itu.
                        if (peta[y][x] == '.') { // Cek sel kosong yang boleh ditempati FS.
                            for (int fsId : fsIds) { // Cek semua ID FS yang dipilih GA.
                                if (bfsPeta.penanda[y][x] == fsId) { // Cocokkan ID sel dengan ID FS.
                                    System.out.println((x + 1) + " " + (y + 1)); // Ubah ke koordinat mulai dari 1.
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}