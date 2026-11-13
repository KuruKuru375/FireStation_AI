import java.util.Random;

//representasi Integer : panjang chromosome = p (jumlah FS nya), contoh [3,5,11]

public class IndividualInt extends Individual {
   public int[] chromosome;
    public double fitness; // fitness harus double karena pembagian nya bs koma
    public double cost;
    public Random myRand;
    public double parentProbability;
    public int totalSelKosong; // banyak sel kosong

    public IndividualInt(Random myRand, int totalSelKosong, int jumlahFS){
        this.myRand = myRand;
        this.totalSelKosong = totalSelKosong;
        this.chromosome = new int[jumlahFS]; //panjang chromosome = jumlah FS
        this.fitness = 0;
        this.parentProbability = 0;

        for (int i = 0; i < jumlahFS; i++){
            int id;
            do {
                id = myRand.nextInt(totalSelKosong);
            }while(contains(this.chromosome, i, id));
                this.chromosome[i] = id;
        }
    }

    public IndividualInt(Random myRand, int []chromosome, int totalSelKosong){
        this.myRand = myRand;
        this.chromosome = chromosome;
        this.totalSelKosong = totalSelKosong;
        this.fitness = 0;
        this.parentProbability = 0;
    }

    public static boolean contains(int[] arr, int panjang, int id){
        for (int i = 0; i < panjang; i++){
            if (arr[i] == id){
                return true;
            }
        }
        return false;
    }

    @Override 
    public double setFitness(int[][] jarakTerdekat){
        return 0.0;
    }

    @Override 
    public void doMutation(){

    }

    @Override 
    public Individual doCrossover(Individual other){
        return new IndividualInt(myRand, chromosome, totalSelKosong);
    }

    @Override 
    public Individual copy(){
        return new IndividualInt(myRand, chromosome, totalSelKosong);
    }

    @Override 
    public int[] getFSIds(){
        return new int[this.totalSelKosong];
    }
}
