import java.util.Random;

public class FireStationGA {
    Random myRand; // random untuk digunakan dalam proses  crossover  , mutasi
    public String tipeKromosom;  // menentukan jenis kromosom yang digunakan
    public int maxPopulationSize; // mengatur jumlah maximum individu dalam satu populasi
    public double elitisimPct; // individu terbaik  yang dipertahankan ke generasi berikutnya 
    public double crossoverRate; // probabilitas terjadinya crossover rate
    public double mutationRate; // probabilitas terjadinya mutasi pada child
    public int totalGeneration; // jumlah maksimum generasi yang boleh dijalankan
    public int patience; // berapa generasi tanpa adanya perkembangan fitness menjadi lebih baik dari bestSoFar sebelum berhenti
    public int generasiAkhir; //generasi terakhir yang dijalankan
    private double bestCostSoFar; // menyimpan nilai cost terbaik yang pernah ditemukan
    private int generasiTanpaPerkembangan; //menghitung jumlah generasi berturut turut yang tidak menghasilkan perbaikan bestcostsofar
    int[][] listJarakTerbaik; // matriks jarak yang digunakan dalam solusi
    int jumlahFS; //jumlah fire station yang akan ditempatkan
    public long batasWaktuMs; // batas maksimum waktu esekusi dalam milidetik
    public long waktuMulai; // menyimpan waktu ketika algoritma mulai dijalankan
    public String alasanBerhenti = ""; // menyimpan alasan algoritma berhenti

    public FireStationGA(Random myRand, String tipeKromosom, int maxPopulationSize, int totalGeneration,
            double elitismPct, double crossoverRate, double mutationRate,
            int[][] listJaraktTerbaik, int jumlahFS, long batasWaktu) {
        this.myRand = myRand;
        this.tipeKromosom = tipeKromosom;
        this.maxPopulationSize = maxPopulationSize;
        this.totalGeneration = totalGeneration;
        this.elitisimPct = elitismPct;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.listJarakTerbaik = listJaraktTerbaik;
        this.jumlahFS = jumlahFS;
        this.patience = (this.totalGeneration * 3) / 10; // 30% dari total generasi 
        this.generasiAkhir = 0; // generasi 0 karena algoritma belum dijalankan
        this.generasiTanpaPerkembangan = 0; 
        this.bestCostSoFar = Double.POSITIVE_INFINITY; //belum ada solusi, jadi cost pertama lebih baik
        this.batasWaktuMs = batasWaktu;
    }

    //Untuk mencatat sudah berapa generasi yang dilalui tanpa adanya kemajuan bestCostSoFar
    private void catatKemajuan(Individual best) {
        if (best.cost < bestCostSoFar - 1e-9) { //perubahan yang sangat kecil tidak dianggap sebagai peningkatan berarti
            bestCostSoFar = best.cost; //memperbarui cost terbaik yang ditemukan
            generasiTanpaPerkembangan = 0; // reset stagnasi karena ada solusi lebih baik
        } else {
            generasiTanpaPerkembangan++; // menambah jumlah generasi tanpa peningkatan
        }
    }

    public boolean terminate(int generation) { 
        if (generation >= this.totalGeneration){ // batas jumlah generasi tercapai
            this.alasanBerhenti = "BUDGET"; // jika batas  generasi tercapai maka alasan berhenti = budget
            return true;
        }else if (this.generasiTanpaPerkembangan >= this.patience){ // terjadi stagnasi
            this.alasanBerhenti = "STAGNATION"; // berhenti karena solusi terbaik tidak mengalami peningkatan
            return true;
        }else if(System.currentTimeMillis() - this.waktuMulai >= batasWaktuMs){ // batas waktu telah tercapai dengan menghitung waktu berjalan dengan mengurangi waktu sekarang dan waktu mulai
            this.alasanBerhenti = "TIME"; 
            return true;
        }

        return false;
    }

     // Parameter:
     //pakaiTournament:
     //  true  = Tournament Selection.
     //  false = Roulette Wheel Selection.
     //
     // pakaiTwoPoint:
     //  true  = Two-Point Crossover.
     //  false = Crossover biasa (doCrossover).
    
     // pakaiMutasiJarak:
     //   true  = Mutasi berbasis jarak.
     //  false = Mutasi biasa.
    public Individual run(boolean pakaiTournament, boolean pakaiTwoPoint, boolean pakaiMutasiJarak) {// parameter berisi true/false yang menentukan apakah seperti crossover memakai two point / yang biasa
        int generation = 1; // memulai perhitungan dari generasi pertama
        this.waktuMulai = System.currentTimeMillis(); // mencatat waktu mulai

        //membuat objek populasi awal dengan parameter
        // Random, matriks jarak, ukuran populasi,
        // elitism, jumlah Fire Station, dan tipe kromosom.
        Population currentPop = new Population(this.myRand, this.listJarakTerbaik, this.maxPopulationSize,
                this.elitisimPct, this.jumlahFS, this.tipeKromosom);
        currentPop.randomPopulation(); // Membuat individu awal secara acak.
        currentPop.computeAllFitnesses();// Menghitung fitness seluruh individu dalam populasi awal
        catatKemajuan(currentPop.getBestIdv()); // mencatat cost terbaik pada populasi awal


        while (terminate(generation) == false) {  // proses evaluasi akan dijalankan selama hasil function terminate(generation) menghasilkan false
            Population newPop = currentPop.getNewPopulationWElit(); // membuat populasi baru dengan mempertahankan sejumlah individu melalui elitism
            while (newPop.isFilled() == false) { //mengisi populasi baru sampai kapasitasnya penuh
                Individual[] parents; // array untuk menyimpan dua parent
                //PARENT SELECTION
                if (pakaiTournament) { // True jika menu seleksi dipilih 2. Maka akan menggunakan tournament selection
                    parents = currentPop.tournamentselection(2, 3); // Dua induk, ukuran tournament untuk pemilihan setiap parent.
                } else {
                    parents = currentPop.selectParent(); // Pilihan 1 memakai roulette wheel , peluang pemilihan parent ditentukan oleh mekanisme seleksi pada populasi
                }
                
                // variabel untuk menyimpan anak (offspring) yang dihasilkan melalui proses reproduksi
                Individual child;
                //CROSSOVER
                if (this.myRand.nextDouble() < this.crossoverRate) {
                    if (pakaiTwoPoint) { //untuk test berbagai cara crossover, tergantung input run di main
                        child = parents[0].doTwoPointCrossover(parents[1]); // memakai twopoint
                    } else {
                        child = parents[0].doCrossover(parents[1]); // memakai crossover biasa
                    }
                } else {
                    child = parents[0].copy();  // Jika crossover tidak dilakukan, anak menjadi salinan parent pertama.
                    
                }

                //MUTATION
                if (this.myRand.nextDouble() < this.mutationRate) {//memastikan probabilitas mutasi sesuai dengan  nilai mutation rate
                    if (pakaiMutasiJarak) {//untuk test berbagai cara mutation, tergantung input run di main
                        child.doMutationByDistance(this.listJarakTerbaik); // mutasi berdasarkan informasi jarak yang tersedia pada matriks jarak
                    } else {
                        child.doMutation(); //mutasi metode biasa
                    }
                }
                newPop.addIndividual(child); // add new child 
            }
            generation++; //menaikkan nomor generasi
            currentPop = newPop; // mengganti populasi lama dengan populasi baru
            currentPop.computeAllFitnesses();//menghitung kembali fitur seluruh individu
            catatKemajuan(currentPop.getBestIdv()); //memeriksa apakah generasi terbaru menghasilkan cost yang lebih baik daripada bestcostsofar
        }

        this.generasiAkhir = generation; //menyimpan jumlah generasi terakhir yang dijalankan
        return currentPop.getBestIdv(); // mengembalikan individu terbaik yang terdapat pada populasi generasi terakhir
    }
}
