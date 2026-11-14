import java.util.Random;

public class IndividualBit extends Individual {
    public IndividualBit(Random MyRand, int totalSelKosong, int jumlahFS) {
        this.myRand = MyRand;
        this.chromosome = new int[totalSelKosong];
        this.fitness = 0;
        this.parentProbability = 0;
        this.jumlahFS = jumlahFS;

        int ct = 0;
        while (ct < jumlahFS) {
            int idx = myRand.nextInt(totalSelKosong);
            if (chromosome[idx] == 0) {
                chromosome[idx] = 1;
                ct++;
            }
        }
    }

    public IndividualBit(Random myRand, int[] chromosome, int jumlahFS) {
        this.myRand = myRand;
        this.chromosome = chromosome;
        this.fitness = 0;
        this.parentProbability = 0;
        this.jumlahFS = jumlahFS;
    }

    @Override
    public double setFitness(int[][] jarakTerdekat) {
        int totalCost = 0;

        for (int i = 0; i < jarakTerdekat.length; i++) {
            int min = Integer.MAX_VALUE; // ingin mencari jarak terkecil maka harus dari paling besar

            for (int j = 0; j < jarakTerdekat[i].length; j++) {
                if (chromosome[j] == 1) {
                    min = Integer.min(min, jarakTerdekat[i][j]);
                }
            }
            // jika kromosom tidak mempunyai satu pun fs
            if (min == Integer.MAX_VALUE) {
                this.cost = Double.POSITIVE_INFINITY;
                this.fitness = 0.0;
                return this.fitness;
            }
            totalCost += min;
        }

        this.cost = (double) totalCost / jarakTerdekat.length;
        this.fitness = 1.0 / (1.0 + this.cost); // lebih kecil -> lebih tinggi nilai fitnessnya

        return this.fitness;
    }

    @Override
    public void doMutation() {
        int idxOne, idxZero;

        do {
            idxOne = myRand.nextInt(chromosome.length);
        } while (chromosome[idxOne] != 1);

        do {
            idxZero = myRand.nextInt(chromosome.length);
        } while (chromosome[idxZero] != 0);

        chromosome[idxOne] = 0;
        chromosome[idxZero] = 1;
    }

    // single crossover
    @Override
    public Individual doCrossover(Individual other) {
        int[] newChromosome = new int[chromosome.length];
        IndividualBit child = new IndividualBit(this.myRand, newChromosome, this.jumlahFS);
        int pos = this.myRand.nextInt((chromosome.length / 2)) + 2;

        for (int i = 0; i <= pos; i++) {
            child.chromosome[i] = this.chromosome[i];
        }

        for (int i = pos + 1; i < chromosome.length; i++) {
            child.chromosome[i] = other.chromosome[i];
        }

        child.fix();
        return child;
    }

    public void fix() {
        int totalFS = 0;
        for (int i = 0; i < chromosome.length; i++) {
            if (this.chromosome[i] == 1)
                totalFS++;
        }
        while (totalFS > this.jumlahFS) {
            int idx = myRand.nextInt(chromosome.length);
            if (chromosome[idx] == 1) {
                chromosome[idx] = 0;
                totalFS--;
            }
        }

        while (totalFS < this.jumlahFS) {
            int idx = myRand.nextInt(chromosome.length);
            if (chromosome[idx] == 0) {
                chromosome[idx] = 1;
                totalFS++;
            }
        }
    }

    @Override
    public Individual copy() {
        int[] newChromosome = this.chromosome.clone();
        return new IndividualBit(this.myRand, newChromosome, this.jumlahFS);
    }

    @Override
    public int[] getFSIds() { // ubah bit string jadi daftar ID sel kosong yang berisi FS
        int jumlah = 0; // hitung banyak Fire Station
        for (int bitFs : chromosome) {
            if (bitFs == 1) {
                jumlah++;
            }
        }

        int[] ids = new int[jumlah]; // array hasil
        int k = 0; // posisi isi array hasil
        for (int i = 0; i < chromosome.length; i++) {
            if (chromosome[i] == 1) {
                ids[k] = i;
                k++;
            }
        }
        return ids;
    }

    @Override // Implementasi two-point crossover khusus untuk kromosom bit.
    public Individual doTwoPointCrossover(Individual other) { // Terima induk kedua.
        int[] genes = twoPointGene(other); // Gabungkan bit dari kedua induk.
        IndividualBit child = new IndividualBit( // Buat objek anak dari hasil gabungan.
                this.myRand, // Gunakan generator random yang sama agar seed tetap konsisten.
                genes, // Masukkan array bit yang sudah digabung.
                this.jumlahFS // Simpan jumlah stasiun yang wajib dimiliki anak.
        ); // Selesai membuat objek anak.

        child.fix(); // Tambahkan atau hapus bit 1 sampai jumlahnya tepat sama dengan p.
        return child; // Kembalikan anak dengan tepat p fire station.
    } 
}
