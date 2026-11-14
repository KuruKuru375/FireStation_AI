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
    int m, n; // ukuran peta

    public FireStationGA(Random myRand, String tipeKromosom, int maxPopulationSize, int totalGeneration,
            double elitismPct, double crossoverRate, double mutationRate,
            int[][] listJaraktTerbaik, int jumlahFS, int m, int n) {
        this.myRand = myRand;
        this.tipeKromosom = tipeKromosom;
        this.maxPopulationSize = maxPopulationSize;
        this.totalGeneration = totalGeneration;
        this.elitisimPct = elitismPct;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.listJarakTerbaik = listJaraktTerbaik;
        this.jumlahFS = jumlahFS;
    }

    public boolean terminate(int generation) {
        if (generation >= this.totalGeneration) {
            return true;
        }

        return false;
    }

    public Individual run(boolean pakaiTournament, boolean pakaiTwoPoint) {
        int generation = 1;

        Population currentPop = new Population(this.myRand, this.listJarakTerbaik, this.maxPopulationSize,
                this.elitisimPct, this.jumlahFS, this.tipeKromosom, this.m, this.n);
        currentPop.randomPopulation();
        currentPop.computeAllFitnesses();

        while (terminate(generation) == false) {
            Population newPop = currentPop.getNewPopulationWElit();
            while (newPop.isFilled() == false) {
                Individual[] parents;

                if (pakaiTournament) { // True jika menu seleksi dipilih 2.
                    parents = currentPop.tournamentselection(2, 3); // Dua induk, tiga undian per induk.
                } else {
                    parents = currentPop.selectParent(); // Pilihan 1 memakai roulette.
                }
                
                if (this.myRand.nextDouble() < this.crossoverRate) {
                    Individual child = parents[0].doCrossover(parents[1]);
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
