import java.util.Random;

// Kromosom direpresentasikan menggunakan bilangan biner:
//
// 1 = lokasi dipilih sebagai Fire Station.
// 0 = lokasi tidak dipilih sebagai Fire Station.
//
// Contoh kromosom:
// [0, 1, 0, 1, 0, 0, 1]
//
// Artinya Fire Station ditempatkan pada indeks 1, 3, dan 6
public class IndividualBit extends Individual {// class turunan dari individiual
    public IndividualBit(Random MyRand, int totalSelKosong, int jumlahFS) { //method membuat individu baru dengan kromosom acak
        this.myRand = MyRand;
        this.chromosome = new int[totalSelKosong];
        this.fitness = 0;
        this.parentProbability = 0;
        this.jumlahFS = jumlahFS;

        int ct = 0; //menghitung jumlah lokasi yang telah dipilih
        while (ct < jumlahFS) {  // Mengulang sampai jumlah FS yang dipilih sama dengan jumlah fs
           
            int idx = myRand.nextInt(totalSelKosong);//memilih indeks lokasi secara acak
            if (chromosome[idx] == 0) {//lokasi hanya dipilih jika belum memiliki fs
                chromosome[idx] = 1; // mengubah bit menjadi 1 artinya lokasi dipilih
                ct++; //menambah jumlah fs yang ditempatkan
            }
        }
    }

    public IndividualBit(Random myRand, int[] chromosome, int jumlahFS) { // membuat individu menggunakan kromosom yang sudah tersedia
        this.myRand = myRand;
        this.chromosome = chromosome;
        this.fitness = 0;
        this.parentProbability = 0;
        this.jumlahFS = jumlahFS;
    }

    @Override
    public double setFitness(int[][] jarakTerdekat) {// Menghitung kualitas solusi berdasarkan rata-rata jarak setiap rumah menuju Fire Station terdekat.
        int totalCost = 0; // Menyimpan total jarak seluruh rumah  ke Fire Station terdekat masing-masing.
       
        int [] fsIds = getFSIds(); //Mengambil daftar indeks lokasi FS yang memiliki bit bernilai 1.

        for (int i = 0; i < jarakTerdekat.length; i++) {//menungjungi setiap rumah
            int min = Integer.MAX_VALUE; // ingin mencari jarak terkecil maka harus dari paling besar

            // Memeriksa jarak rumah i ke setiap FS terpilih.
           for (int id : fsIds){
            min = Integer.min(min, jarakTerdekat[i][id]);//Menyimpan jarak paling kecil antara rumah i dan seluruh FS.
           }
           
            totalCost += min; //menambahkan jarak terdekat ke rumah ke i ke total biaya
        }

        this.cost = (double) totalCost / jarakTerdekat.length; //menghitung average cost
        this.fitness = 1.0 / (1.0 + this.cost); // lebih kecil costnya -> lebih tinggi nilai fitnessnya

        return this.fitness;
    }

    @Override
    public void doMutation() {//Melakukan mutasi dengan menukar satu bit 1 menjadi 0 dan satu bit 0 menjadi 1
        // idxOne  = indeks lokasi yang memiliki FS.
        // idxZero = indeks lokasi yang tidak memiliki FS.
        int idxOne, idxZero;

        do {
            idxOne = myRand.nextInt(chromosome.length); // memilih indeks kromosom secara acak
        } while (chromosome[idxOne] != 1);  // Ulangi selama lokasi yang ditemukan bukan lokasi fs

        do {  // Mencari lokasi kosong untuk fs baru.
            idxZero = myRand.nextInt(chromosome.length);  // memilih indeks kromosom secara acak.
        } while (chromosome[idxZero] != 0);  // Ulangi selama lokasi yang ditemukan bukan lokasi kosong

        chromosome[idxOne] = 0;   // Menghapus FS dari lokasi lama.
        chromosome[idxZero] = 1;      // Menempatkan FS pada lokasi baru.
    }

    // single crossover method
    @Override
    public Individual doCrossover(Individual other) {
        int[] newChromosome = new int[chromosome.length]; // Membuat array baru untuk kromosom anak.
        IndividualBit child = new IndividualBit(this.myRand, newChromosome, this.jumlahFS);    // Membuat objek anak menggunakan array kosong.
        int pos = this.myRand.nextInt((chromosome.length / 2)) + 2; //menentukan titik potong secara acak

        for (int i = 0; i <= pos; i++) { //Menyalin gen parent pertama dari indeks 0 sampai indeks pos
            child.chromosome[i] = this.chromosome[i];
        }

        for (int i = pos + 1; i < chromosome.length; i++) { // Menyalin gen parent kedua mulai dari indeks pos + 1 sampai indeks terakhir.
            child.chromosome[i] = other.chromosome[i];
        }

        child.fix();  // Memperbaiki jumlah bit 1 agar
        // sesuai dengan jumlahFS.

        return child;
    }

    // Membuat salinan individu dengan kromosom
     //yang memiliki isi sama
     //clone() menghasilkan array baru sehingga
     //perubahan pada salinan tidak mengubah
     //kromosom individu asal.
    @Override
    public Individual copy() {
        int[] newChromosome = this.chromosome.clone();
        return new IndividualBit(this.myRand, newChromosome, this.jumlahFS);
    }

    @Override
    public int[] getFSIds() { // ubah bit string jadi daftar ID sel kosong yang berisi FS
        int jumlah = 0; // hitung banyak Fire Station bit =1
        for (int bitFs : chromosome) {  // Memeriksa setiap bit pada kromosom.
           
            if (bitFs == 1) {  // Jika bit bernilai 1, brarti ada fs
                jumlah++;
            }
        }

        int[] ids = new int[jumlah]; // array hasil
        int k = 0; // posisi isi array hasil
        for (int i = 0; i < chromosome.length; i++) {//memeriksa posisi setiap gen
            if (chromosome[i] == 1) {  // jika posisi i merupakan lokasi FS.
                ids[k] = i;  // menyimpan indeks FS ke array hasil.
                k++; // berpindah ke posisi hasil berikutnya.
            }
        }
        return ids; //return daftark indeks fs
    }

    // Memperbaiki jumlah bit 1 dalam kromosom
     //agar tepat sama dengan jumlahFS.
    //
     // Diperlukan karena crossover dapat menghasilkan
     //kromosom dengan terlalu banyak atau terlalu
     //sedikit Fire Station.
    public void fix() {
        int totalFS = 0;
        for (int i = 0; i < chromosome.length; i++) {
            if (this.chromosome[i] == 1)
                totalFS++;
        }
        // Jika jumlah fs melebihi kebutuhan, hapus fs secara acak sampai jumlah sesuai
        while (totalFS > this.jumlahFS) {
            int idx = 0;

            do {
                idx = myRand.nextInt(chromosome.length);
            } while (chromosome[idx] != 1);

            this.chromosome[idx] = 0;

            totalFS--;
        }

        while (totalFS < this.jumlahFS) {
            int idx = 0;

            do {
                idx = myRand.nextInt(chromosome.length);
            } while (chromosome[idx] != 0);

            this.chromosome[idx] = 1;
            totalFS++;
        }
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
    
    // melakukan mutasi swap sebanyak dua kali.
     //setiap doMutation() memindahkan satu FS
     //dari lokasi terpilih ke lokasi kosong
     // Dua swap dapat menghasilkan perubahan
     //pada lebih dari satu pasangan lokasi.
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
        int rumahTerjauh = -1; // Menyimpan indeks rumah terjauh -1 berarti belum ada rumah yang dipilih
        int jarakTerjauh = -1; // Menyimpan jarak terbesar yang ditemukan.
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
}