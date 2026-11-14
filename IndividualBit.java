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

    @Override
    public void doMultiSwapMutation() {
        int jumlahSwap = 2;

        for (int i = 0; i < jumlahSwap; i++) {
            doMutation();
        }

    }
    // mutation by distance

    // method pembantu mutation by distance untuk mencari rumah yang paling jauh
    // dari kandidat solusi
    private int rumahTerjauh(int[][] jarakTerdekat) {
        int rumahTerjauh = -1;
        int jarakTerjauh = -1;
        // loop tiap rumah
        for (int rumah = 0; rumah < jarakTerdekat.length; rumah++) {
            int minJarakRumahFS = Integer.MAX_VALUE;
            // loop tiap kandidat posisi kosong terpilih di kromosom
            for (int k = 0; k < chromosome.length; k++) {
                if (chromosome[k] == 1) {
                    // cari jarak minimum dari semua kandidat ke rumah
                    minJarakRumahFS = Math.min(minJarakRumahFS, jarakTerdekat[rumah][k]);
                }
            }
            // jika jarak minimum dari rumah ke posisi kandidat lebih besar daripada rumah
            // lain
            // maka simpan jarak sebagai jarak rumah terjauh yang dicapai dari kandidat
            if (minJarakRumahFS > jarakTerjauh) {
                jarakTerjauh = minJarakRumahFS;
                rumahTerjauh = rumah;
            }
        }
        return rumahTerjauh;
    }

    // mencari kandidat posisi FS alternatif dari kromosom sekarang berdasarkan
    // jarak rumah terjauh dari kandidat kromosom
    @Override
    public void doMutationByDistance(int[][] jarakTerdekat) {
        // mencari indeks rumah terjauh dari kandidat posisi FS di kromosom sekarang
        int idxRumahTerjauh = rumahTerjauh(jarakTerdekat);
        // mencari kandidat lokasi FS baru dari kromosom sekarang yang belum dipilih
        int addIdx = -1;
        // menyimpan jarak terkecil antara rumah terjauh ke kandidat lokasi baru
        int minJarak = Integer.MAX_VALUE;
        for (int k = 0; k < chromosome.length; k++) {
            if (chromosome[k] == 0) {
                // jarak rumah terjauh ke kandidat lokasi baru
                int distance = jarakTerdekat[idxRumahTerjauh][k];
                // jika kandidat baru lebih dekat dari kandidat sebelumnya maka simpan
                if (distance < minJarak) {
                    minJarak = distance;
                    addIdx = k;
                }
            }
        }
        if (addIdx == -1)
            return;
        // cari kandidat posisi FS lama di kromosom sekarang yang cocok untuk digantikan
        int bestRemoveIdx = -1;
        // avg cost terkecil
        double biayaTerbaik = Double.POSITIVE_INFINITY;
        for (int removeIdx = 0; removeIdx < chromosome.length; removeIdx++) {
            // cari gen kromosom sekarang yang terpilih untuk digantikan
            if (chromosome[removeIdx] == 1) {
                // cost setelah ditukar posisi gen
                double totalCost = 0;
                // loop tiap rumah hitung jarak total ke posisi baru di kromosom sekarang
                for (int rumah = 0; rumah < jarakTerdekat.length; rumah++) {
                    // jarak minimum awal menggunakan jarak rumah ke posisi baru
                    int min = jarakTerdekat[rumah][addIdx];
                    for (int k = 0; k < chromosome.length; k++) {
                        if (chromosome[k] == 1 && k != removeIdx) {
                            // simpan jarak terkecil ke tiap rumah ke kandidat posisi yang tidak diubah dan
                            // baru
                            min = Math.min(min, jarakTerdekat[rumah][k]);
                        }
                    }
                    totalCost += min;
                }
                // rata rata jarak rumah setelah menukar
                double newCost = totalCost / jarakTerdekat.length;
                // jika rata rata jarak rumah setelah menukar lebih kecil dari pertukaran
                // sebelumnya maka simpan
                if (newCost < biayaTerbaik) {
                    biayaTerbaik = newCost;
                    bestRemoveIdx = removeIdx;
                }
            }
        }
        // jika ada yang ditukar maka tukar gen pada kromosom dengan kandidat posisi
        // baru
        if (bestRemoveIdx != -1) {
            chromosome[bestRemoveIdx] = 0;
            chromosome[addIdx] = 1;
        }
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
}
