import java.util.Random;

public class Individual implements Comparable<Individual>{
    public int[] chromosome;
    public int fitness;
    public Random myRand;
    public double parentProbability;

    public Individual(Random MyRand, int totalSelKosong, int jumlahFS) {
        this.myRand = MyRand;
        this.chromosome = new int[totalSelKosong];
        this.fitness = 0;
        this.parentProbability = 0;

        int ct = 0;
        while (ct < jumlahFS) {
            int idx = myRand.nextInt(totalSelKosong);
            if (chromosome[idx] == 0) {
                chromosome[idx] = 1;
                ct++;
            }
        }
    }

    public Individual(Random myRand, int[] chromosome) {
        this.myRand = myRand;
        this.chromosome = chromosome;
        this.fitness = 0;
        this.parentProbability = 0;
    }

    public int setFitness(int[][] jarakTerdekat) {
        int min = 0;
        int cost = 0;

        for (int i = 0; i < jarakTerdekat.length; i++) {
            min = Integer.MAX_VALUE;
            for (int j = 0; j < jarakTerdekat[i].length; j++) {
                if (chromosome[j] == 1) {
                    min = Integer.min(min, jarakTerdekat[i][j]);
                }
            }
            cost += min;
        }

        return cost;
    }
    
    public void doMutation() {
        
    }
    
    //single crossover
    public Individual doCrossover(Individual other) {
        int[] newChromosome = new int[chromosome.length];
        Individual child = new Individual(this.myRand, newChromosome);
        int pos = this.myRand.nextInt((chromosome.length / 2)) + 1;

        for (int i = 0; i <= pos; i++) {
            child.chromosome[i] = this.chromosome[i];
        }
        
        for (int i = 0; i < chromosome.length; i++) {
            child.chromosome[i] = other.chromosome[i];
        }

        return child;
    }
    
    @Override
    public int compareTo(Individual other) {
        // TODO Auto-generated method stub
        if (this.fitness > other.fitness) {
            return -1;
        } else if (this.fitness < other.fitness) {
            return 1;
        }

        return 0;
    }
}
