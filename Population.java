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
    int sumRank = 0; //Variable akan digunakan untuk jika menggunakan pemilihan parent berdasarkan rank
    Random myRand;

    public Population(Random myRand, int[][] listJarakTerbaik, int maxPopulationSize, double elitismPct, int jumlahFS) {
        this.myRand = myRand;
        this.maxPopulationSize = maxPopulationSize;
        this.population = new ArrayList<Individual>();
        this.elistismPct = elitismPct;
        this.listJarakTerbaik = listJarakTerbaik;
        this.totalSelKosong = this.listJarakTerbaik[0].length;
        this.jumlahFS = jumlahFS;
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
            this.addIndividual(new Individual(this.myRand, this.totalSelKosong, this.jumlahFS));
        }
    }

    public void computeAllFitnesses() {
        for (int i = 0; i < this.populationSize; i++) {
            ((Individual) this.population.get(i)).setFitness(listJarakTerbaik);
        }

        this.population.sort((idv1, idv2) -> idv1.compareTo(idv2));
    }
        
    public Population getNewPopulationWElit() {
        Population newPop = new Population(this.myRand, this.listJarakTerbaik, this.populationSize, this.elistismPct,
                this.jumlahFS);
        int n = (int) (this.elistismPct * this.maxPopulationSize);

        for (int i = 0; i < n; i++) {
            boolean result = newPop.addIndividual(this.population.get(i));
        }

        return newPop;
    }
    
    public boolean isFilled() {
        return this.maxPopulationSize == this.populationSize;
    }

    public Individual[] selectParent() { //Pemilihan parent berdasarkan roulett wheel
        Individual[] parents = new Individual[2];

        double sumfitness = 0;
        for (int i = 0; i < this.population.size(); i++) {
            sumfitness =  (sumfitness + this.population.get(i).fitness);
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
    
    public Individual getBestIdv() {
        return this.population.get(0);
    }
}
