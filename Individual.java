import java.util.Random;

public abstract class Individual implements Comparable<Individual> {
    public int[] chromosome;
    public double fitness; // fitness harus double karena pembagian nya bs koma
    public double cost;
    public Random myRand;
    public double parentProbability;
    public int jumlahFS;

    public abstract double setFitness(int[][] jarakTerdekat);

    public abstract void doMutation();

    // multiswap mutation
    public abstract void doMultiSwapMutation();

    // mutation by distance
    public abstract void doMutationByDistance(int[][] jarakTerdekat);

    public abstract Individual doCrossover(Individual other);

    public abstract Individual copy();

    public abstract int[] getFSIds();

    public abstract Individual doTwoPointCrossover(Individual other); // dapat diimplementasikan oleh class bit dan
                                                                      // integer.

    @Override
    public int compareTo(Individual other) {
        if (this.fitness > other.fitness) {
            return -1;
        } else if (this.fitness < other.fitness) {
            return 1;
        }

        return 0;
    }

    protected int[] twoPointGene(Individual other) { // Gabungkan gen dua induk sebelum repair.
        int[] child = this.chromosome.clone(); // Salin kromosom induk pertama agar kromosom induk tetap utuh.
        int length = child.length; // Simpan jumlah gen pada kromosom.

        if (length < 3) { // Dua titik potong membutuhkan minimal tiga gen.
            return child; // Gunakan salinan induk pertama untuk kromosom yang terlalu pendek.
        } // Selesai memeriksa panjang kromosom.

        int cut1 = 1 + this.myRand.nextInt(length - 1); // Undi titik potong pertama di antara gen.
        int cut2; // Siapkan variabel titik potong kedua.

        do { // Undi sampai diperoleh titik potong kedua yang berbeda.
            cut2 = 1 + this.myRand.nextInt(length - 1); // Ambil titik potong pada rentang 1 sampai length - 1.
        } while (cut2 == cut1); // Ulangi kalau kedua titik potong sama.

        int left = Math.min(cut1, cut2); // Gunakan titik yang lebih kecil sebagai batas kiri.
        int right = Math.max(cut1, cut2); // Gunakan titik yang lebih besar sebagai batas kanan.

        for (int i = left; i < right; i++) { // Isi segmen tengah dari indeks left sampai right - 1.
            child[i] = other.chromosome[i]; // Salin gen induk kedua pada posisi yang sama.
        } // Gen di luar segmen tengah tetap berasal dari induk pertama.

        return child; // Kembalikan gen anak untuk diperbaiki oleh class bit atau integer.
    }
}