import java.util.Random;

public class FireStationGA {
    Random myRand;
    public String tipeKromosom;
    public int maxPopulationSize;
    public double elitisimPct;
    public double crossoverRate;
    public double mutationRate;
    public int totalGeneration;
    public int patience; // berapa generasi tanpa adanya perkembangan fitness menjadi lebih baik dari bestSoFar sebelum berhenti
    public int generasiAkhir; //generasi terakhir yang dijalankan
    private double bestCostSoFar;
    private int generasiTanpaPerkembangan;
    int[][] listJarakTerbaik;
    int jumlahFS;
    public long batasWaktuMs;
    public long waktuMulai;
    public String alasanBerhenti = "";

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
        this.generasiAkhir = 0;
        this.generasiTanpaPerkembangan = 0;
        this.bestCostSoFar = Double.POSITIVE_INFINITY; //belum ada solusi, jadi cost pertama lebih baik
        this.batasWaktuMs = batasWaktu;
    }

    //Untuk mencatat sudah berapa generasi yang dilalui tanpa adanya kemajudan bestCostSoFar
    private void catatKemajuan(Individual best) {
        if (best.cost < bestCostSoFar - 1e-9) {
            bestCostSoFar = best.cost;
            generasiTanpaPerkembangan = 0;
        } else {
            generasiTanpaPerkembangan++;
        }
    }

    public boolean terminate(int generation) {
        if (generation >= this.totalGeneration){
            this.alasanBerhenti = "BUDGET";
            return true;
        }else if (this.generasiTanpaPerkembangan >= this.patience){
            this.alasanBerhenti = "STAGNATION";
            return true;
        }else if(System.currentTimeMillis() - this.waktuMulai >= batasWaktuMs){
            this.alasanBerhenti = "TIME";
            return true;
        }

        return false;
    }

    public Individual run(boolean pakaiTournament, boolean pakaiTwoPoint, boolean pakaiMutasiJarak) {
        int generation = 1;
        this.waktuMulai = System.currentTimeMillis();

        Population currentPop = new Population(this.myRand, this.listJarakTerbaik, this.maxPopulationSize,
                this.elitisimPct, this.jumlahFS, this.tipeKromosom);
        currentPop.randomPopulation();
        currentPop.computeAllFitnesses();
        catatKemajuan(currentPop.getBestIdv());

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
            catatKemajuan(currentPop.getBestIdv());
        }

        this.generasiAkhir = generation;
        return currentPop.getBestIdv();
    }
}
