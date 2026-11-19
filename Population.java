import java.util.ArrayList;
import java.util.Random;

public class Population {
    public ArrayList<Individual> population; // arraylist untuk menyimpan seluruh individu yang terdapat dalam satu populasi
    private int maxPopulationSize; //jumlah maximum individu yang ditampung dalam populasi
    private int populationSize = 0;
    public double elistismPct;
    private int[][] listJarakTerbaik;
    private int totalSelKosong;
    private int jumlahFS;
    int sumRank = 0; // Variable akan digunakan untuk jika menggunakan pemilihan parent berdasarkan
                     // rank
    Random myRand;
    private String tipeKromosom;

    public Population(Random myRand, int[][] listJarakTerbaik, int maxPopulationSize, double elitismPct, int jumlahFS,
            String tipeKromosom) {
        this.myRand = myRand;
        this.maxPopulationSize = maxPopulationSize;
        this.population = new ArrayList<Individual>();
        this.elistismPct = elitismPct;
        this.listJarakTerbaik = listJarakTerbaik;
        this.totalSelKosong = this.listJarakTerbaik[0].length;
        this.jumlahFS = jumlahFS;
        this.tipeKromosom = tipeKromosom;
        for (int i = 1; i <= this.maxPopulationSize; i++) {//menghitung total ranking populasi
            this.sumRank += i;
        }
    }

    public boolean addIndividual(Individual newIdv) {
        if (this.populationSize >= this.maxPopulationSize) { //memeriksa apakah jumlah individu sudah mencapai kapasitas maksimum
            return false; // penambahan ditolak karena populasi penuh
        }

        this.population.add(newIdv); // menambahkan individu ke arraylist
        this.populationSize++; // menambah penghitung jumlah individu
        return true;
    }

    public void randomPopulation() { // membentuk populasi awal secaraacak
        for (int i = 0; i < this.maxPopulationSize; i++) { // pembentukan sebanyak kapasitas maximym populasi
            if (this.tipeKromosom.equals("int")) { // jika int maka kromosom berupa integer 
                this.addIndividual(new IndividualInt(this.myRand, this.totalSelKosong, this.jumlahFS));
            } else {
                this.addIndividual(new IndividualBit(this.myRand, this.totalSelKosong, this.jumlahFS)); // yang lainnya merupakan kromosom bit
            }
        }
    }

    public void computeAllFitnesses() { //menghitung fitness seluruh indiidu dalam populasi
        for (int i = 0; i < this.populationSize; i++) {
            ((Individual) this.population.get(i)).setFitness(listJarakTerbaik); //menghitung fitness individu ke i berdasarkan jarak
        }

        this.population.sort((idv1, idv2) -> idv1.compareTo(idv2)); // mengurutkan berdasarkan fitness (descending)
    }

    public Population getNewPopulationWElit() { // membentuk populasi generasi berikutnya dengan mekanisme elitism
        Population newPop = new Population(this.myRand, this.listJarakTerbaik, this.populationSize, this.elistismPct,
                this.jumlahFS, this.tipeKromosom);  // populasi baru menggunakan population size dari populasi sekarang
        int n = (int) (this.elistismPct * this.maxPopulationSize); // membuang bagiand desimal

        for (int i = 0; i < n; i++) {
            boolean result = newPop.addIndividual(this.population.get(i)); //menyimpan referensi individu yang sama 
        }

        return newPop;
    }

    public boolean isFilled() { // memeriksa apakah populasi sudah penuh
        return this.maxPopulationSize == this.populationSize;
    }

     //ROULETTE WHEEL SELECTION
     // Memilih dua parent berdasarkan fitness.
     // Individu dengan fitness lebih tinggi  memiliki peluang terpilih lebih besar.
    //  Tetapi individu dengan fitness lebih rendah masih memiliki kesempatan untuk terpilih jika probabilitasnya positif
    public Individual[] selectParent() { // Pemilihan parent berdasarkan roulett wheel
        Individual[] parents = new Individual[2];// Membuat array berukuran dua untuk menyimpan dua parent terpilih.
        
        //1.Menghitung Fitness
        double sumfitness = 0; //menyimpan penjumlahan fitness seluruh individu dalam populasi
        for (int i = 0; i < this.population.size(); i++) {
            sumfitness = sumfitness + this.population.get(i).fitness; // menambahkan fitness individu ke total
        }

            //2.Menghitung probabilitas setiap individu
        for (int i = 0; i < this.population.size(); i++) {
            ((Individual) this.population.get(i)).parentProbability = (1.0 * this.population.get(i).fitness)
                    / sumfitness; // membagi fitness individu dengan total fitness  seluruh populasi
        }

        // 3. memilih dua parent
        for (int n = 0; n < 2; n++) {
            int i = -1;
            double prob = this.myRand.nextDouble();
            double sum = 0.0;
            do {
                i++; // berpindah ke individu berikutnya
                sum = sum + this.population.get(i).parentProbability; // menjumlahkan probabilitas individu secara bertahap
            } while (sum < prob); // ketika sum >= prob individu pada indeks i terpilih
            parents[n] = this.population.get(i); // menyimpan individu terpilih sebagai parent ke -n
        }

        return parents;
    }


   //TOURNAMENT SELECTION
     // Memilih parent dengan mengadakan turnamen  antara beberapa kandidat acak
     
     //Kandidat dengan fitness tertinggi  akan menjadi pemenang turnamen
    
     // Parameter:
      //jumlahinduk    = jumlah parent yang dipilih
      //jumlahKandidat = banyak peserta setiap turnamen
    public Individual[] tournamentselection(int jumlahinduk, int jumlahKandidat) {
        if (jumlahinduk < 1 || jumlahKandidat < 1) { // Pastikan kedua jumlah minimal satu.
            throw new IllegalArgumentException("Jumlah induk dan kandidat harus positif."); // Hentikan jika input
                                                                                            // salah.
        }

        Individual[] parents = new Individual[jumlahinduk]; // Sediakan tempat untuk setiap pemenang turnamen.

        for (int i = 0; i < parents.length; i++) { // Jalankan satu turnamen untuk setiap induk.
            int indeksAwal = this.myRand.nextInt(this.population.size()); // Undi kandidat pertama.
            Individual terbaik = this.population.get(indeksAwal); // Jadikan kandidat pertama pemenang sementara.

            for (int j = 1; j < jumlahKandidat; j++) { // Mulai dari 1 karena satu kandidat sudah diundi.
                int indeks = this.myRand.nextInt(this.population.size()); // Undi kandidat berikutnya.
                Individual kandidat = this.population.get(indeks); // Ambil individu dari populasi.

                if (kandidat.fitness > terbaik.fitness) { // Bandingkan fitness kedua individu.
                    terbaik = kandidat; // Simpan kandidat jika fitness-nya lebih tinggi.
                }
            }

            parents[i] = terbaik; // Simpan pemenang turnamen ini sebagai satu induk.
        }

        return parents; // Kembalikan semua induk yang terpilih.

    }

    public Individual getBestIdv() { // mengambil individu terbatik dari populasi
        return this.population.get(0);
    }
}
