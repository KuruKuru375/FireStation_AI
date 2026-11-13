import java.util.Random;

public class Individual implements Comparable<Individual> {
    public int[] chromosome;
    public double fitness; // fitness harus double karena pembagian nya bs koma
    public double cost;
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

    public double setFitness(int[][] jarakTerdekat) {
        int totalCost = 0;

        for (int i = 0; i < jarakTerdekat.length; i++) {
            int min = Integer.MAX_VALUE; // ingin mencari jarak terkecil maka harus dari paling besar

            for (int j = 0; j < jarakTerdekat[i].length; j++) {
                if (chromosome[j] == 1) {
                    min = Integer.min(min, jarakTerdekat[i][j]);
                }
            }
            // jika kromosom tidak mempunyai satu pun fs
            if (min == Integer.MAX_VALUE) {
                this.cost = Double.POSITIVE_INFINITY;
                this.fitness = 0.0;
                return this.fitness;
            }
            totalCost += min;
        }

        this.cost = (double) totalCost / jarakTerdekat.length;
        this.fitness =  1.0 / (1.0+this.cost);

        return this.fitness;
    }

    public void doMutation() {
        int  jumlahkromosom1=0;
        for(int bit:chromosome){
            if(bit==1){// menghitung seberapa banyak pilih fs di kromsosom
                jumlahkromosom1++;
            }
        }
        if(jumlahkromosom1==chromosome.length||jumlahkromosom1==0){ // mengatasi jika semua bit kromosom 1 atau 0 agar tidak infinit loop
            return; 
        }
        // random pilih idx
        int idx = myRand.nextInt(chromosome.length);

        int oldBit = chromosome[idx];

        // single flip bit mutation
        chromosome[idx] = 1 - chromosome[idx];

        // repair agar jumlah Fire Station sama
        int idxRepair;
        do {
            idxRepair = myRand.nextInt(chromosome.length);
        } while (idxRepair == idx || chromosome[idxRepair] != chromosome[idx]);
        {
            chromosome[idxRepair] = oldBit;
        }
    }

    public Individual copy() {
        int [] newChromosome = this.chromosome.clone();
        return new Individual(this.myRand, newChromosome);
    }

    // single crossover
    public Individual doCrossover(Individual other) {
        int[] newChromosome = new int[chromosome.length];
        Individual child = new Individual(this.myRand, newChromosome);

        // asumsi single crossover titik potongnya ditengah
        int pos = chromosome.length / 2;

        int jumlahFS = 0;
        for (int i = 0; i < chromosome.length; i++) {
            if (chromosome[i] == 1) {
                jumlahFS++;
            }
        }

        // bagian kiri
        for (int i = 0; i < pos; i++) {
            child.chromosome[i] = this.chromosome[i];
        }

        // bagian kanan
        for (int i = pos; i < chromosome.length; i++) {
            child.chromosome[i] = other.chromosome[i];
        }

        // cek anak jumlah Fire Station pada anak agar tidak melewati ketentuan FS pada
        // input
        int jumlahFSAnak = 0;
        for (int i = 0; i < chromosome.length; i++) {
            if (child.chromosome[i] == 1) {
                jumlahFSAnak++;
            }
        }

        // kalo jumlah Fire Station Anak terlalu banyak
        while (jumlahFSAnak > jumlahFS) {
            int idx = myRand.nextInt(chromosome.length);

            if (child.chromosome[idx] == 1) {
                child.chromosome[idx] = 0;
                jumlahFSAnak--;
            }
        }

        // kalo jumlah Fire Station Anak terlalu dikit
        while (jumlahFSAnak < jumlahFS) {
            int idx = myRand.nextInt(chromosome.length);

            if (child.chromosome[idx] == 0) {
                child.chromosome[idx] = 1;
                jumlahFSAnak++;
            }
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
