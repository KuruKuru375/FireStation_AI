import java.util.Random;

public class FireStationGA {
    Random myRand;
    public String tipeKromosom;
    public int maxPopulationSize;
    public double elitisimPct;
    public double crossoverRate;
    public double mutationRate;
    public int totalGeneration;
    int[][] listJarakTerbaik;
    int jumlahFS;

    public FireStationGA(Random myRand, int maxPopulationSize, int totalGeneration, double elitismPct,
            double crossoverRate, double mutationRate,
            int[][] listJaraktTerbaik, int jumlahFS, String tipeKromosom) {
        this.myRand = myRand;
        this.maxPopulationSize = maxPopulationSize;
        this.totalGeneration = totalGeneration;
        this.elitisimPct = elitismPct;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.listJarakTerbaik = listJaraktTerbaik;
        this.jumlahFS = jumlahFS;
        this.tipeKromosom = tipeKromosom;
    }

    public boolean terminate(int generation) {
        if (generation >= this.totalGeneration) {
            return true;
        }

        return false;
    }

    public Individual run() {
        return run(false, false); // Pemanggilan lama memakai roulette dan one-point.
    }

    public Individual run(boolean pakaiTournament, boolean pakaiTwoPoint) {
        int generation = 1;// Populasi awal dihitung sebagai generasi pertama.

        Population currentPop = new Population(this.myRand, this.listJarakTerbaik, this.maxPopulationSize,
                this.elitisimPct, this.jumlahFS, this.tipeKromosom);
        currentPop.randomPopulation();// Buat individu awal secara acak
        currentPop.computeAllFitnesses(); // Hitung fitness dan urutkan populasi.

        while (terminate(generation) == false) {
            Population newPop = currentPop.getNewPopulationWElit();
            while (newPop.isFilled() == false) {
                Individual[] parents;// array parrent untuk menyiapkan metode selectparent
                while (!newPop.isFilled()) { // Isi sisa tempat di populasi baru dengan anak.

                    if (pakaiTournament) { // True jika menu seleksi dipilih 2.
                        parents = currentPop.tournamentselection(2, 3); // Dua induk, tiga undian per induk.
                    } else {
                        parents = currentPop.selectParent(); // Pilihan 1 memakai roulette.
                    }
                    Individual child;
                    if (this.myRand.nextDouble() < this.crossoverRate) { // jika angka random < probabilitas crossover
                                                                         // lakukan crossover
                        child = parents[0].doCrossover(parents[1]);
                    } else { // jika tidak, anak = salinan induk pertama
                        child = parents[0].copy();
                    }
                    if (this.myRand.nextDouble() < this.mutationRate) {
                        child.doMutation();
                    }
                    newPop.addIndividual(child);
                }
            }
            generation++;
            currentPop = newPop;
            currentPop.computeAllFitnesses();
        }

        return currentPop.getBestIdv();
    }
}
