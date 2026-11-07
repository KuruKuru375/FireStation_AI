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
            String input = sc.next();
            for (int j = 0; j < n; j++) {
                peta[i][j] = input.charAt(j);
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
        
        int selRumah[][] = new int[jumlahRumah][2];
        int idxHouse = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
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

                int dx[] = { -1, 0, 1, 0 }; //Kiri, Atas, Kanan, Bawah
                int dy[] = { 0, -1, 0, 1 }; //kiri, atas, kanan, bawah

                for (int j = 0; j < 4; j++) {
                    int r = y + dy[j];
                    int c = x + dx[j];

                    if ((c < 0 || c >= n || r < 0 || r >= m)) { //Jika out of bound dari peta yang diberikan
                        continue;
                    } else if (peta[r][c] == 'T' || peta[r][c] == 'H' || jarakTerdekat[penanda[r][c]][i] != 0) { // Jika posisi tetangga adalah pohona atau rumah skip, dan jika sudah divisit skip.
                        continue;
                    } else if (peta[y][x] == 'H') { //Jika posisi sekarang rumah, maka jarak terdekat tetangganya pasti 1
                        jarakTerdekat[penanda[r][c]][i] = 1;
                        q.add(new sel(c, r));
                        continue;
                    }

                    jarakTerdekat[penanda[r][c]][i] = jarakTerdekat[penanda[y][x]][i] + 1;
                    q.add(new sel(c, r));
                }
            }
        }
                
        for (int i = 0; i < jumlahSelKosong; i++) {
            System.out.print(jarakTerdekat[i][0] + " ");
        }
    }
}
