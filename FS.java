import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class FS {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int m = sc.nextInt();
        int n = sc.nextInt();
        int p = sc.nextInt();
        char peta[][] = new char[m][n];
        int penanda[][] = new int[m][n];

        int jumlahSelKosong = 0;
        int jumlahRumah = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                peta[i][j] = sc.next().charAt(0);
                if (peta[i][j] == '.') {
                    penanda[i][j] = jumlahSelKosong;
                    jumlahSelKosong++;
                } else if (peta[i][j] == 'H') {
                    penanda[i][j] = -1; 
                    jumlahRumah++;
                } else {
                    penanda[i][j] = -1; //Penanda untuk pohon
                }
            }
        }
        
        boolean selKosong[][] = new boolean[m][n];
        int selRumah[][] = new int[jumlahRumah][2];
        int idxHouse = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                selKosong[i][j] = false;
                System.out.print(penanda[i][j] + " ");
                if (peta[i][j] == 'H') {
                    selRumah[idxHouse][0] = i;
                    selRumah[idxHouse][1] = j;
                    idxHouse++;
                }
            }
            System.out.println();
        }

        int jarakTerdekat[][] = new int[jumlahSelKosong][jumlahRumah];
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
                int idxCurr = penanda[x][y];
                boolean isRumah = (peta[curr.y][curr.x] == 'H'); //penanda apakah sel sekarang adalah rumah atau bukan

                // int dx[] = {-1, 0, 1, 0 }; //Kiri, Atas, Kanan, Bawah
                // int dy[] = {0, -1, 0, 1 }; //kiri, atas, kanan, bawah

                // for (int j = 0; i < 4; j++) {
                //     int r = y + dy[j];
                //     int c = x + dx[j];
                //     if ((c < 0 || c >= n || r < 0 || r >= m)) {
                //         continue;
                //     } else if (peta[y][x] == 'H') {
                //         jarakTerdekat[penanda[r][c]][i] = 1;
                //         q.add(new sel(c, r));
                //     } 
                // }
              
                if (curr.x - 1 >= 0) {
                    int kiri = penanda[curr.y][curr.x - 1];
                    if (kiri != - 1 &&isRumah && jarakTerdekat[kiri][i] == 0) {
                        q.add(new sel(curr.x + 1, curr.y));
                        jarakTerdekat[kiri][i] = 1;
                    } else if(kiri != -1 && jarakTerdekat[kiri][i] == 0){
                        q.add(new sel(curr.y, curr.x - 1));
                        jarakTerdekat[kiri][i] = jarakTerdekat[idxCurr][i] + 1;
                    }
                }   

                if (curr.y - 1 >= 0) {
                    int atas = penanda[curr.y - 1][curr.x];
                    if (atas != - 1 && isRumah && jarakTerdekat[atas][i] == 0) {
                        q.add(new sel(curr.x, curr.y - 1));
                        jarakTerdekat[atas][i] = 1;
                    } else if(atas != -1 && jarakTerdekat[atas][i] == 0){
                        q.add(new sel(curr.y - 1, curr.x));
                        jarakTerdekat[atas][i] = jarakTerdekat[idxCurr][i] + 1;
                    }
                }

                if (curr.x + 1 < n) {
                    int kanan = penanda[curr.y][curr.x + 1];
                    if (kanan != - 1 && isRumah && jarakTerdekat[kanan][i] == 0) { 
                        q.add(new sel(curr.x + 1, curr.y));
                        jarakTerdekat[kanan][i] = 1;
                    } else if(kanan != -1 && jarakTerdekat[kanan][i] == 0){
                        q.add(new sel(curr.y, curr.x + 1));
                        jarakTerdekat[kanan][i] = jarakTerdekat[idxCurr][i] + 1;
                    }
                }

                if (curr.y + 1 < m) {
                    int bawah = penanda[curr.y + 1][curr.x];
                    if (bawah != - 1 && isRumah && jarakTerdekat[bawah][i] == 0) {
                        q.add(new sel(curr.x , curr.y + 1));
                        jarakTerdekat[bawah][i] = 1;
                    } else if(bawah != - 1 && jarakTerdekat[bawah][i] == 0){
                        q.add(new sel(curr.y + 1, curr.x));
                        jarakTerdekat[bawah][i] = jarakTerdekat[idxCurr][i] + 1;
                    }
                }
            }
        }
        
        for (int i = 0; i < jumlahSelKosong; i++) {
            System.out.print(jarakTerdekat[i][0] + " ");
        }
       
    }
}
