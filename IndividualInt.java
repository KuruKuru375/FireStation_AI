import java.util.Random;

//representasi Integer : panjang chromosome = p (jumlah FS nya), contoh [3,5,11]

public class IndividualInt extends Individual {

    public int totalSelKosong; // banyak sel kosong

    public IndividualInt(Random myRand, int totalSelKosong, int jumlahFS) {
        this.myRand = myRand;
        this.totalSelKosong = totalSelKosong;
        this.chromosome = new int[jumlahFS]; // panjang chromosome = jumlah FS
        this.fitness = 0;
        this.parentProbability = 0;

        for (int i = 0; i < jumlahFS; i++) {
            int id;
            do {
                id = myRand.nextInt(totalSelKosong);
            } while (contains(this.chromosome, i, id));
            this.chromosome[i] = id;
        }
    }

    public IndividualInt(Random myRand, int[] chromosome, int totalSelKosong) {
        this.myRand = myRand;
        this.chromosome = chromosome;
        this.totalSelKosong = totalSelKosong;
        this.fitness = 0;
        this.parentProbability = 0;
    }

    public static boolean contains(int[] arr, int panjang, int id) {
        for (int i = 0; i < panjang; i++) {
            if (arr[i] == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double setFitness(int[][] jarakTerdekat) {
        int totalCost = 0; // jumlah jarak semua rumah

        for (int i = 0; i < jarakTerdekat.length; i++) { // cek untuk setiap rumah
            int min = Integer.MAX_VALUE;
            for (int id : chromosome) { // hanya mengecek p(input) buah FS
                min = Integer.min(min, jarakTerdekat[i][id]);
            }
            totalCost += min;
        }

        this.cost = (double) totalCost / jarakTerdekat.length; // rata2 jarak
        this.fitness = 1.0 / (1.0 + this.cost); // cost kecil -> fitness besar
        return this.fitness;
    }

    // single bit mutation
    @Override
    public void doMutation() {
        if (chromosome.length >= totalSelKosong) { // semua sel kosong yang sudah terisi FS, tidak bisa dipindah
            return;
        }

        int pos = myRand.nextInt(chromosome.length); // random pilih FS yang akan dimutasi
        int idBaru;

        do {
            idBaru = myRand.nextInt(totalSelKosong); // pilih sel kosong
        } while (contains(chromosome, chromosome.length, idBaru)); // ga boleh ganti yang sudah berisi fs
        chromosome[pos] = idBaru;// pindahkan FS
    }

    @Override
    public Individual doCrossover(Individual other) {
        int p = chromosome.length; // jumlah FS
        int[] anak = new int[p]; // kromosom anak
        int pos = (p > 1) ? 1 + myRand.nextInt(p - 1) : 0; // titik potong acak

        for (int i = 0; i < p; i++) {
            int id = (i < pos) ? this.chromosome[i] : other.chromosome[i]; // kiri dari induk 1, kanan dari induk 2
            if (contains(anak, i, id)) { // ID sudah ada di anak -> 2 FS di sel yang sama (tidak valid)
                do {
                    id = myRand.nextInt(totalSelKosong); // repair: ganti dengan ID acak yang belum dipakai
                } while (contains(anak, i, id));
            }
            anak[i] = id;
        }

        return new IndividualInt(this.myRand, anak, this.totalSelKosong);
    }

    @Override
    public Individual copy() {
        return new IndividualInt(this.myRand, this.chromosome.clone(), this.totalSelKosong);
    }

    @Override
    public int[] getFSIds() {
        return this.chromosome.clone();
    }

    @Override // Implementasi two-point crossover khusus untuk kromosom integer.
    public Individual doTwoPointCrossover(Individual other) { // Terima induk kedua.
        int[] child = twoPointGene(other); // Gabungkan daftar ID dari kedua induk.

        for (int i = 0; i < child.length; i++) { // Periksa setiap ID pada kromosom anak.
            if (contains(child, i, child[i])) { // Cari apakah ID ini sudah muncul sebelum indeks i.
                child[i] = -1; // Tandai kemunculan duplikat agar bisa diganti.
            } // Selesai memeriksa satu ID.
        } // Semua duplikat sudah ditandai.

        for (int i = 0; i < child.length; i++) { // Cari posisi yang perlu diisi ulang.
            if (child[i] == -1) { // Perbaiki posisi yang tadi ditandai.
                int newId; // Siapkan variabel untuk ID pengganti.

                do { // Terus undi sampai memperoleh ID yang tersedia.
                    newId = this.myRand.nextInt(this.totalSelKosong); // Undi ID dari semua sel kosong.
                } while (contains(child, child.length, newId)); // Ulangi jika ID sudah dipakai di bagian mana pun.

                child[i] = newId; // Isi posisi duplikat dengan ID yang unik.
            } // Selesai memperbaiki satu posisi, semua posisi akan memiliki id yang unik
        } 

        return new IndividualInt(this.myRand, child, this.totalSelKosong); // Buat objek anak dengan tepat p ID unik.
    } 
}
