import java.util.Random;

public class Individual implements Comparable<Individual> {
    public int[] chromosome;
    public double fitness;
    public Random myRand;
    public double parentProbability;
    public int jumlahFS;

    public Individual(Random MyRand, int totalSelKosong, int jumlahFS) {
        this.myRand = MyRand;
        this.chromosome = new int[totalSelKosong];
        this.fitness = 0.0;
        this.parentProbability = 0;
        this.jumlahFS = jumlahFS;

        int ct = 0;
        while (ct < jumlahFS) {
            int idx = myRand.nextInt(totalSelKosong);
            if (chromosome[idx] == 0) {
                chromosome[idx] = 1;
                ct++;
            }
        }
    }

    public Individual(Random myRand, int[] chromosome, int jumlahFS) {
        this.myRand = myRand;
        this.chromosome = chromosome;
        this.fitness = 0;
        this.parentProbability = 0;
        this.jumlahFS = jumlahFS;
    }

    public double setFitness(int[][] jarakTerdekat) {
        int cost = 0;
        double fitCurrent = 0.0;
        int sumJarakTerdekat = 0;
        double fit = 0.0;

        for (int i = 0; i < jarakTerdekat.length; i++) {
            cost = 0;
            sumJarakTerdekat = 0;
            for (int j = 0; j < jarakTerdekat[i].length; j++) {
                sumJarakTerdekat += jarakTerdekat[i][j];
                if (chromosome[j] == 1) {
                    cost += jarakTerdekat[i][j];
                }
            }
            fitCurrent += ((double) cost * 1.0) / ((double) sumJarakTerdekat * 1.0);
        }

        fit = fitCurrent / (double) jarakTerdekat.length; // jarakTerdekat.length = total rumah yang ada
        this.fitness = 1.0 / (1.0 + fit);
        return fit;
    }

    public void doMutation() {
        int idxOne, idxZero;

        do {
            idxOne = myRand.nextInt(chromosome.length);
        } while (chromosome[idxOne] != 1);

        do {
            idxZero = myRand.nextInt(chromosome.length);
        } while (chromosome[idxZero] != 0);

        chromosome[idxOne] = 0;
        chromosome[idxZero] = 1;
    }

    // single crossover
    public Individual doCrossover(Individual other) {
        int[] newChromosome = new int[chromosome.length];
        Individual child = new Individual(this.myRand, newChromosome, this.jumlahFS);
        int pos = this.myRand.nextInt((chromosome.length / 2)) + 2;

        for (int i = 0; i <= pos; i++) {
            child.chromosome[i] = this.chromosome[i];
        }

        for (int i = pos + 1; i < chromosome.length; i++) {
            child.chromosome[i] = other.chromosome[i];
        }

        child.fix();
        return child;
    }

    public void fix() {
        int totalFS = 0;
        for (int i = 0; i < chromosome.length; i++) {
            if (this.chromosome[i] == 1)
                totalFS++;
        }

        while (totalFS > this.jumlahFS) {
            int idx = 0;

            do {
                idx = myRand.nextInt(chromosome.length);
            } while (chromosome[idx] != 1);

            this.chromosome[idx] = 0;

            totalFS--;
        }

        while (totalFS < this.jumlahFS) {
            int idx = 0;

            do {
                idx = myRand.nextInt(chromosome.length);
            } while (chromosome[idx] != 0);

            this.chromosome[idx] = 1;
            totalFS++;
        }
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
