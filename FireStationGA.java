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

    public FireStationGA(Random myRand, String tipeKromosom, int maxPopulationSize, int totalGeneration,
            double elitismPct, double crossoverRate, double mutationRate,
            int[][] listJaraktTerbaik, int jumlahFS) {
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

    public Individual run(boolean pakaiTournament, boolean pakaiTwoPoint, boolean pakaiMutasiJarak) {
        int generation = 1;

        Population currentPop = new Population(this.myRand, this.listJarakTerbaik, this.maxPopulationSize,
                this.elitisimPct, this.jumlahFS, this.tipeKromosom);
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

                Individual child;
                if (this.myRand.nextDouble() < this.crossoverRate) {
                    if (pakaiTwoPoint) { //untuk test berbagai cara crossover, tergantung input run di main
                        child = parents[0].doTwoPointCrossover(parents[1]);
                    } else {
                        child = parents[0].doCrossover(parents[1]);
                    }
                } else {
                    child = parents[0].copy();
                }

                if (this.myRand.nextDouble() < this.mutationRate) {
                    if (pakaiMutasiJarak) {//untuk test berbagai cara mutation, tergantung input run di main
                        child.doMutationByDistance(this.listJarakTerbaik);
                    } else {
                        child.doMutation();
                    }
                }
                newPop.addIndividual(child); // add new child
            }
            generation++;
            currentPop = newPop;
            currentPop.computeAllFitnesses();
        }

        return currentPop.getBestIdv();
    }
}
