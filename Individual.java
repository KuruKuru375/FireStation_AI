import java.util.Random;

public abstract class Individual implements Comparable<Individual> {
    public int[] chromosome;
    public double fitness; // fitness harus double karena pembagian nya bs koma
    public double cost;
    public Random myRand;
    public double parentProbability;

    public abstract double setFitness(int[][] jarakTerdekat);
    public abstract void doMutation();
    public abstract Individual doCrossover(Individual other);
    public abstract Individual copy();
    public abstract int[] getFSIds();

    @Override 
    public int compareTo(Individual other){
        if (this.fitness > other.fitness){
            return -1;
        }else if(this.fitness < other.fitness){
            return 1;
        }

        return 0;
    }
}
