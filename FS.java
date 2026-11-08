import java.util.LinkedList;
import java.util.Queue;

public class FS {
    public int m;
    public int n;
    public char peta[][];
    public int penanda[][];

    public FS(int m, int n, char peta[][]) {
        this.m = m;
        this.n = n;
        this.peta = peta;
        this.penanda = new int[m][n];
    }

    public int[][] BFS() {
        int jumlahRumah = 0;
        int jumlahSelKosong = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (peta[i][j] == '.') {
                    penanda[i][j] = jumlahSelKosong;
                    jumlahSelKosong++;
                } else if (peta[i][j] == 'H') {
                    penanda[i][j] = -1;
                    jumlahRumah++;
                } else {
                    penanda[i][j] = -1; // Penanda untuk pohon
                }
            }
        }

        int selRumah[][] = new int[jumlahRumah][2];
        int idxHouse = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (peta[i][j] == 'H') {
                    selRumah[idxHouse][0] = i;
                    selRumah[idxHouse][1] = j;
                    idxHouse++;
                }
            }
        }

        int jarakTerdekat[][] = new int[jumlahRumah][jumlahSelKosong];
        Queue<sel> q = new LinkedList<>();

        for (int i = 0; i < jumlahRumah; i++) {
            int x = selRumah[i][1];
            int y = selRumah[i][0];
            sel rumah = new sel(x, y);
            q.add(rumah);

            while (!q.isEmpty()) {
                sel curr = q.poll();
                x = curr.x;
                y = curr.y;

                int dx[] = { -1, 0, 1, 0 }; // Kiri, Atas, Kanan, Bawah
                int dy[] = { 0, -1, 0, 1 }; // kiri, atas, kanan, bawah

                for (int j = 0; j < 4; j++) {
                    int r = y + dy[j];
                    int c = x + dx[j];

                    if ((c < 0 || c >= n || r < 0 || r >= m)) { // Jika out of bound dari peta yang diberikan
                        continue;
                    } else if (peta[r][c] == 'T' || peta[r][c] == 'H' || jarakTerdekat[i][penanda[r][c]] != 0) {
                        continue;
                    } else if (peta[y][x] == 'H') { // Jika posisi sekarang rumah, maka jarak terdekat tetangganya pasti
                                                    // 1
                        jarakTerdekat[i][penanda[r][c]] = 1;
                        q.add(new sel(c, r));
                        continue;
                    }

                    jarakTerdekat[i][penanda[r][c]] = jarakTerdekat[i][penanda[y][x]] + 1;
                    q.add(new sel(c, r));
                }
            }
        }

        return jarakTerdekat;
    }
}
