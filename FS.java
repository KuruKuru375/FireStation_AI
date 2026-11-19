import java.util.LinkedList;
import java.util.Queue;

//class ini bertugas menghitung jarak terpendek dari setiap rumah ke seluruh kandidat lokasi fire station menggunakan algoritma BFS
public class FS {
    public int m;//jumlah baris
    public int n;////jumlah kolom 
    public char peta[][]; // menyimpan isi peta seperti H=rumah , T=pohon
    public int penanda[][]; // sel kosong diber id mulai dari 0 rumah dan pohon diberi penanda -1

    public FS(int m, int n, char peta[][]) {
        this.m = m;
        this.n = n;
        this.peta = peta;
        this.penanda = new int[m][n];
    }

    public int[][] BFS() {
        int jumlahRumah = 0; // menyimpan jumlah rumah pada peta
        int jumlahSelKosong = 0; // menyimpan jumalh sel kosong yang dapat dipilih sebagai lokasi fire station

        //1. menghitung rumah dan sel kosong
        for (int i = 0; i < m; i++) { //loop setiap baris peta
            for (int j = 0; j < n; j++) { // periksa setiap kolom
                if (peta[i][j] == '.') { // jika lokasi kosong
                    penanda[i][j] = jumlahSelKosong;  // berikan id unik pada sel kosong 
                    jumlahSelKosong++; // menambah jumlah kandidat lokasi fs
                } else if (peta[i][j] == 'H') { // jika sel merupakan rumah
                    penanda[i][j] = -1;//rumah tidak termasuk kandidat maka dikasi -1
                    jumlahRumah++;
                } else {
                    penanda[i][j] = -1; // Penanda  juga untuk pohon
                }
            }
        }

        //2. menyimpan koordinat seluruh rumah
        int selRumah[][] = new int[jumlahRumah][2];//array dua dimensi untuk menyimpan posisi setiap rumah
        int idxHouse = 0; //indeks untuk memasukan posisi rumah ke dalam array sel rumah

        for (int i = 0; i < m; i++) { // cek setiap baris dan kolom
            for (int j = 0; j < n; j++) {
                if (peta[i][j] == 'H') { // jika sel merupakan  rumah
                    selRumah[idxHouse][0] = i; //menyimpan posisi baris rumah
                    selRumah[idxHouse][1] = j;//menyimpan posisi kolom rumah
                    idxHouse++;//pindah ke indeks rumah berikutnya
                }
            }
        }
        //3. membuat matrix jarak dan queue BFS
        //baris=jumlah rumah
        //kolom=jumlah kandidat lokasi FS
        int jarakTerdekat[][] = new int[jumlahRumah][jumlahSelKosong];//seluruh elemen awal nilainya 0
        Queue<sel> q = new LinkedList<>();

        //4. menjalankan bfs dari setiap rumah
        for (int i = 0; i < jumlahRumah; i++) {
            int x = selRumah[i][1];//koordinat kolom rumah
            int y = selRumah[i][0];//koordinat baris rumah
            sel rumah = new sel(x, y); // objek sel sebagai titik awal bfs
            q.add(rumah); // masukan posisi rumah ke queue

            //selama queue blom kosong ambil satu sel dan periksa ke empat tetangga
            while (!q.isEmpty()) {
                sel curr = q.poll();
                x = curr.x;
                y = curr.y;

                int dx[] = { -1, 0, 1, 0 }; // Kiri, Atas, Kanan, Bawah
                int dy[] = { 0, -1, 0, 1 }; // kiri, atas, kanan, bawah

                for (int j = 0; j < 4; j++) {
                    int r = y + dy[j]; // menghitung baris tetangga
                    int c = x + dx[j];//menghitung kolom tetangga

                    if ((c < 0 || c >= n || r < 0 || r >= m)) { // Jika out of bound dari peta yang diberikan
                        continue;
                    } else if (peta[r][c] == 'T' || peta[r][c] == 'H' || jarakTerdekat[i][penanda[r][c]] != 0) {
                        continue;
                    } else if (peta[y][x] == 'H') { // Jika posisi sekarang rumah, maka jarak terdekat tetangganya pasti
                                                    // 1
                        jarakTerdekat[i][penanda[r][c]] = 1; // menetapkan jarak tetangga
                        q.add(new sel(c, r));//masukkan tetangga ke queue untuk diperiksa berikutnya
                        continue;//lanjutkan ke arah berikutnya
                    }

                    //. menghitung jarak sel berikutnya
                    jarakTerdekat[i][penanda[r][c]] = jarakTerdekat[i][penanda[y][x]] + 1; // hitung jarak tetangga dari jarak sel saat ini +1
                    q.add(new sel(c, r)); // memasukkan tetangga ke queue
                }
            }
        }
        //5.menangani lokasi yang tidak terjangkau
        for (int i = 0; i < jarakTerdekat.length; i++) {
            for (int j = 0; j < jarakTerdekat[i].length; j++) {
                if (jarakTerdekat[i][j] == 0) { // nilai 0 berarti lokasi kosong tidak pernah dikunjungi bfs
                    jarakTerdekat[i][j] = m * n; // penalti m*n
                }
            }
        }

        return jarakTerdekat;
    }
}
