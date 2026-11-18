import java.util.Random;

//Setiap gen menyimpan ID lokasi Fire Station.
 
 // Contoh:
 //chromosome = [3, 5, 11]
 //
 //Artinya:
 //- Fire Station pertama ditempatkan di lokasi 3.
 //- Fire Station kedua ditempatkan di lokasi 5.
 //- Fire Station ketiga ditempatkan di lokasi 11.
public class IndividualInt extends Individual {//turunan dari  class individual
    public int totalSelKosong; // banyak sel kosong

    public IndividualInt(Random myRand, int totalSelKosong, int jumlahFS) {//membuat individu baru dengan kromosom acak
        this.myRand = myRand;
        this.totalSelKosong = totalSelKosong; //Misalnya totalSelKosong = 20, maka ID lokasi valid berada pada rentang 0-19.
        this.chromosome = new int[jumlahFS]; // panjang chromosome = jumlah FS
        this.fitness = 0;
        this.parentProbability = 0;

         // Mengisi setiap posisi kromosom dengan  ID lokasi fs yang berbeda
        for (int i = 0; i < jumlahFS; i++) {
            int id;
            do {
                id = myRand.nextInt(totalSelKosong);  // Memilih ID lokasi secara acak Rentang 0 sampai totalSelKosong - 1.
            } while (contains(this.chromosome, i, id));
            this.chromosome[i] = id;
        }
    }

    public IndividualInt(Random myRand, int[] chromosome, int totalSelKosong, int jumlahFS) {//Membuat individu menggunakan array kromosom yang sudah tersedia
        this.myRand = myRand;
        this.chromosome = chromosome;
        this.totalSelKosong = totalSelKosong;
        this.fitness = 0;
        this.parentProbability = 0;
        this.jumlahFS = jumlahFS;
    }

    public static boolean contains(int[] arr, int panjang, int id) {//Memeriksa apakah suatu ID sudah terdapat pada bagian awal array.
        for (int i = 0; i < panjang; i++) {   // Memeriksa elemen dari indeks 0 sampai indeks panajang -1
            if (arr[i] == id) {// Jika elemen sama dengan ID yang dicari
                return true; // ID ditemukan.
            }
        }
        return false; // ID tidak ditemukan
    }

    @Override
    public double setFitness(int[][] jarakTerdekat) {//Menghitung nilai fitness berdasarkan rata-rata jarak setiap rumah menuju Fire Station terdekat
        int totalCost = 0; // total jumlah jarak minimum semua rumah

        for (int i = 0; i < jarakTerdekat.length; i++) { // cek untuk setiap rumah
            int min = Integer.MAX_VALUE;// Agar jarak valid pertama dapat menggantikan nilai tersebut
            for (int id : chromosome) { // hanya mengecek p(input) buah FS
                min = Integer.min(min, jarakTerdekat[i][id]);  // Membandingkan jarak minimum sebelumnya dengan jarak rumah i ke FS id
            }
            totalCost += min; // Menjumlahkan jarak minimum rumah i ke total biaya
        }

        this.cost = (double) totalCost / jarakTerdekat.length; // rata2 jarak
        this.fitness = 1.0 / (1.0 + this.cost); // cost kecil menghasilkan fitness besar
        return this.fitness;
    }


    //Melakukan mutasi dengan mengganti satu ID
     //Fire Station menjadi ID lokasi lain yang
     //belum digunakan.
    @Override
    public void doMutation() {
        if (chromosome.length >= totalSelKosong) { // semua sel kosong yang sudah terisi FS, tidak bisa dipindah
            return;
        }

        int pos = myRand.nextInt(chromosome.length); // random pilih FS yang akan dimutasi
        int idBaru; // menyimpan calon ID lokasi baru

        do {
            idBaru = myRand.nextInt(totalSelKosong); // pilih sel kosong
        } while (contains(chromosome, chromosome.length, idBaru)); // Hal ini mencegah dua FS berada pada lokasi yang sama
        chromosome[pos] = idBaru;// pindahkan FS ke lokasi pada posisi terpilih
    }


    //Kromosom anak mengambil:
     // - Gen bagian kiri dari parent pertama
     //- Gen bagian kanan dari parent kedua
    @Override
    public Individual doCrossover(Individual other) { // melakukan single point crossover
        int p = chromosome.length; // jumlah FS
        int[] anak = new int[p]; // kromosom anak
        int pos = (p > 1) ? 1 + myRand.nextInt(p - 1) : 0; // titik potong acak

        for (int i = 0; i < p; i++) {
            int id = (i < pos) ? this.chromosome[i] : other.chromosome[i]; // kiri dari induk 1, kanan dari induk 2
            if (contains(anak, i, id)) { // ID sudah ada di anak -> 2 FS di sel yang sama (tidak valid) /duplikat
                do {
                    id = myRand.nextInt(totalSelKosong); // repair: ganti dengan ID acak yang belum dipakai
                } while (contains(anak, i, id));  // Ulangi jika ID sudah terdapat pada bagian anak yang telah terisi
            }
            anak[i] = id; // menyimpan id unik ke kromosom acak
        }

        return new IndividualInt(this.myRand, anak, this.totalSelKosong, this.jumlahFS); // buat objek anak hasil dari crossover
    }

    //Membuat salinan individu dengan kromosom
    // yang sama tetapi menggunakan array baru.
    @Override
    public Individual copy() {
        return new IndividualInt(this.myRand, this.chromosome.clone(), this.totalSelKosong, this.jumlahFS);
    }


    //Mengembalikan daftar ID lokasi Fire Station
     //Pada representasi integer, kromosom sudah
     //menyimpan daftar ID tersebut secara langsung
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

        //TAHAP MEMPERBAIKI DUPLIKAT
        for (int i = 0; i < child.length; i++) { // Cari posisi yang perlu diisi ulang.
            if (child[i] == -1) { // Perbaiki posisi yang tadi ditandai.
                int newId; // Siapkan variabel untuk ID pengganti.

                do { // Terus undi sampai memperoleh ID yang tersedia.
                    newId = this.myRand.nextInt(this.totalSelKosong); // Undi ID dari semua sel kosong.
                } while (contains(child, child.length, newId)); // Ulangi jika ID sudah dipakai di bagian mana pun.

                child[i] = newId; // Isi posisi duplikat dengan ID yang unik.
            } // Selesai memperbaiki satu posisi, semua posisi akan memiliki id yang unik
        }

        return new IndividualInt(this.myRand, child, this.totalSelKosong, this.jumlahFS); // Buat objek anak dengan
                                                                                          // tepat p ID unik.
    }


    //Melakukan mutasi sebanyak dua kali.
     //Setiap doMutation() mengganti satu ID FS dengan ID lokasi lain yang belum digunakan
    // Dua kali mutasi tidak selalu berarti dua posisi berbeda berubah pada hasil akhir
    @Override
    public void doMultiSwapMutation() {
        int jumlahSwap = 2;    // Menentukan jumlah operasi mutasi

        for (int i = 0; i < jumlahSwap; i++) {  // Mengulang proses mutasi sebanyak dua kali
            doMutation();  // Melakukan satu penggantian ID lokasi
        }

    }
    // mutation by distance

    // method pembantu mutation by distance untuk mencari rumah yang paling jauh
    // dari kandidat solusi
    private int rumahTerjauh(int[][] jarakTerdekat) {
        int rumahTerjauh = -1;
        int jarakTerjauh = -1;
        // loop setiap rumah
        for (int rumah = 0; rumah < jarakTerdekat.length; rumah++) {

            int minJarakRumahFS = Integer.MAX_VALUE;

            // loop seluruh lokasi FS yang terpilih
            for (int k = 0; k < chromosome.length; k++) {

                // Ambil ID lokasi FS dari kromosom
                int id = chromosome[k];

                // Cari jarak minimum rumah ke FS terdekat
                minJarakRumahFS = Math.min(
                        minJarakRumahFS,
                        jarakTerdekat[rumah][id]);
            }

            // Cari rumah dengan jarak minimum terjauh
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
        // 1. Cari rumah dengan jarak terjauh dari FS
        int idxRumahTerjauh = rumahTerjauh(jarakTerdekat);

        if (idxRumahTerjauh == -1)
            return;

        //2.Mencari lokasi fs baru
        // index rumah terdekat
        int addIdx = -1;
        // minimum jarak rumah terdekat ke FS baru
        int minJarak = Integer.MAX_VALUE;
        for (int k = 0; k < totalSelKosong; k++) {

            if (!contains(chromosome, chromosome.length, k)) {  // Hanya mempertimbangkan lokasi  yang belum dipilih sebagai FS


                int distance = jarakTerdekat[idxRumahTerjauh][k]; // Mengambil jarak rumah terjauh menuju kandidat lokasi k

                if (distance < minJarak) {  // Jika kandidat ini memiliki jarak lebih kecil daripada kandidat sebelumnya
                    minJarak = distance; //memperbarui jarak terkecil
                    addIdx = k; // menyimpan id lokasi baru
                }
            }
        }

        if (addIdx == -1) // Jika seluruh lokasi telah digunakan dan tidak ada kandidat baru hentikan proses mutasi
            return;

            //3. mencari FS lama yang paling baik diganti
        int bestRemoveIdx = -1;  // Indeks posisi kromosom dari FS yang akan diganti
        double biayaTerbaik = Double.POSITIVE_INFINITY; // menyimpan avg cost paling kecil dari seluruh percobaan penggantian

        for (int removeIdx = 0; removeIdx < chromosome.length; removeIdx++) { // mengganti setiap fs yang tersedia

            double totalCost = 0; // total cost simulasi

            //4. evaluasi cost setelah pertukaran
            for (int rumah = 0; rumah < jarakTerdekat.length; rumah++) { // memeriksa seluruh rumah
                int min = jarakTerdekat[rumah][addIdx];   // Jarak minimum awal menggunakan jarak rumah ke kandidat fs baru

                  // Memeriksa seluruh FS lama.
                for (int k = 0; k < chromosome.length; k++) {

                    if (k != removeIdx) {  // Mengabaikan FS yang sedang disimulasikan akan diapus

                        int id = chromosome[k]; //mengambil id fs lama yang masi dipertahankan

                        min = Math.min(min, jarakTerdekat[rumah][id]); // membandingkan jarak rumah ke fs yang lama tersisa dengan jarak minimum sebelumnya
                    }
                }

                totalCost += min; //menambahkan jarak terdekat rumah ini ke total cost simulasi
            }

            // rata-rata cost setelah pertukaran
            double newCost = totalCost / jarakTerdekat.length;

            if (newCost < biayaTerbaik) { // Jika rata-rata cost simulasi lebih kecil dibanding alternatif penggantian
                biayaTerbaik = newCost; //menyimpan nilai cost terbaik
                bestRemoveIdx = removeIdx; // mentimpan indeks fs yang akan diganti
            }
        }

        //5.menjalankan mutasi
        if (bestRemoveIdx != -1) {//jika ditemukan fs lama yang akan diganti

            chromosome[bestRemoveIdx] = addIdx; //menngganti id fs lama dengan id lokasi baru
        }
    }
}