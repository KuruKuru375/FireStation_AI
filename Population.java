import java.util.ArrayList;
import java.util.Random;

public class Population {
    public ArrayList<Individual> population;
    private int maxPopulationSize;
    private int populationSize = 0;
    public double elistismPct;
    private int[][] listJarakTerbaik;
    private int totalSelKosong;
    private int jumlahFS;
    private int m;
    private int n;
    int sumRank = 0; // Variable akan digunakan untuk jika menggunakan pemilihan parent berdasarkan
                     // rank
    Random myRand;
    private String tipeKromosom;

    public Population(Random myRand, int[][] listJarakTerbaik, int maxPopulationSize, double elitismPct, int jumlahFS,
            String tipeKromosom, int m, int n) {
        this.myRand = myRand;
        this.maxPopulationSize = maxPopulationSize;
        this.population = new ArrayList<Individual>();
        this.elistismPct = elitismPct;
        this.listJarakTerbaik = listJarakTerbaik;
        this.totalSelKosong = this.listJarakTerbaik[0].length;
        this.jumlahFS = jumlahFS;
        this.tipeKromosom = tipeKromosom;
        for (int i = 1; i <= this.maxPopulationSize; i++) {
            this.sumRank += i;
        }
    }

    public boolean addIndividual(Individual newIdv) {
        if (this.populationSize >= this.maxPopulationSize) {
            return false;
        }

        this.population.add(newIdv);
        this.populationSize++;
        return true;
    }

    public void randomPopulation() {
        for (int i = 0; i < this.maxPopulationSize; i++) {
            if (this.tipeKromosom.equals("int")) {
                this.addIndividual(new IndividualInt(this.myRand, this.totalSelKosong, this.jumlahFS));
            } else {
                this.addIndividual(new IndividualBit(this.myRand, this.totalSelKosong, this.jumlahFS));
            }
        }
    }

    public void computeAllFitnesses() {
        for (int i = 0; i < this.populationSize; i++) {
            ((Individual) this.population.get(i)).setFitness(listJarakTerbaik, m, n);
        }

        this.population.sort((idv1, idv2) -> idv1.compareTo(idv2));
    }

    public Population getNewPopulationWElit() {
        Population newPop = new Population(this.myRand, this.listJarakTerbaik, this.populationSize, this.elistismPct,
                this.jumlahFS, this.tipeKromosom, this.m, this.n);
        int n = (int) (this.elistismPct * this.maxPopulationSize);

        for (int i = 0; i < n; i++) {
            boolean result = newPop.addIndividual(this.population.get(i));
        }

        return newPop;
    }

    public boolean isFilled() {
        return this.maxPopulationSize == this.populationSize;
    }

    public Individual[] selectParent() { // Pemilihan parent berdasarkan roulett wheel
        Individual[] parents = new Individual[2];

        double sumfitness = 0;
        for (int i = 0; i < this.population.size(); i++) {
            sumfitness = sumfitness + this.population.get(i).fitness;
        }

        for (int i = 0; i < this.population.size(); i++) {
            ((Individual) this.population.get(i)).parentProbability = (1.0 * this.population.get(i).fitness)
                    / sumfitness;
        }

        for (int n = 0; n < 2; n++) {
            int i = -1;
            double prob = this.myRand.nextDouble();
            double sum = 0.0;
            do {
                i++;
                sum = sum + this.population.get(i).parentProbability;
            } while (sum < prob);
            parents[n] = this.population.get(i);
        }

        return parents;
    }

    // tournament selection : mengambil beberapa individu secara acak dari populasi
    // lalu mencari yang terbaik
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

    public Individual getBestIdv() {
        return this.population.get(0);
    }
}
