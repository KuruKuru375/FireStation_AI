
# FireStation AI - Genetic Algorithm

Project ini merupakan implementasi Genetic Algorithm (GA) untuk menentukan lokasi optimal fire station berdasarkan persebaran rumah, pohon, dan sel kosong pada peta grid.

Program menggunakan Java dan menyediakan pencatatan hasil eksperimen ke dalam file CSV.

## 1. Struktur Folder Project

```text
FireStation_AI/
│
├── input.txt
├── param.txt
├── main.java
│
├── FireStationGA.java
├── Population.java
├── Individual.java
├── IndividualBit.java
├── IndividualInt.java
├── FS.java
├── BFS.java
│
├── CsvOutputForm.java
├── FireStationCsvLogger.java
│
├── OutputTools/
│   └── csv/
│       └── CsvWriter.java
│
├── TestCase/
│
├── Experiment_Results/
│
└── README.md
```

## 2. Persiapan Sebelum Menjalankan Program

Pastikan Java Development Kit (JDK) sudah terpasang.

Periksa versi Java melalui CMD:

```cmd
java -version
javac -version
```

Pastikan file berikut tersedia di root folder project:

- `input.txt` : berisi data peta yang akan diuji.
- `param.txt` : berisi konfigurasi hyperparameter Genetic Algorithm.

### 2.1 Konfigurasi input.txt

File `input.txt` berisi informasi peta grid.

Format:

```text
m n p
baris_peta_1
baris_peta_2
...
baris_peta_m
```

Keterangan:

| Simbol | Keterangan |
|---|---|
| H | Rumah |
| T | Pohon |
| . | Sel kosong |

Contoh:

```text
10 10 3
..........
..H.......
..........
.....T....
..........
..........
...H......
..........
..........
..........
```

Sebelum menjalankan eksperimen, salin isi peta yang ingin diuji dari folder `TestCase/` ke `input.txt`.

### 2.2 Konfigurasi param.txt

File `param.txt` berisi konfigurasi hyperparameter GA.

Urutan parameter:

```text
Population
Generation
Crossover Probability
Mutation Probability
Elitism Percentage
```

Contoh:

```text
100
2000
0,8
0,05
0,2
```

Keterangan:

| Parameter | Nilai |
|---|---:|
| Population | 100 |
| Generation | 2000 |
| Crossover Probability | 0.8 |
| Mutation Probability | 0.05 |
| Elitism Percentage | 0.2 |

Sesuaikan nilai parameter dengan eksperimen yang sedang dilakukan.

**Catatan:** Nilai yang dipilih melalui `CsvOutputForm` merupakan identitas dan lokasi penyimpanan output. Nilai tersebut tidak otomatis mengubah konfigurasi GA di `param.txt`. Pastikan keduanya sesuai.

## 3. Compile Program

Buka CMD pada direktori utama project `FireStation_AI`.

Jalankan perintah:

```cmd
javac -encoding UTF-8 -cp . -d . *.java OutputTools/csv/CsvWriter.java
```

Jika tidak terdapat pesan error, program berhasil dikompilasi.

## 4. Menjalankan Program

Jalankan perintah:

```cmd
java -cp . main
```

Program akan menampilkan menu CSV Output Setup.

```text
================================
          CSV OUTPUT SETUP
================================
Pilih tahap eksperimen:

1. Hasil Eksperimen Tahap 1
2. Hasil Eksperimen Tahap 2
3. CSV Integration Test
4. Tahap Pengujian

Pilihan:
```

Pilih tahap eksperimen sesuai kebutuhan.

## 5. Panduan Penggunaan CSV Output Form

### 5.1 Eksperimen Tahap 1

Pilih:

```text
1. Hasil Eksperimen Tahap 1
```

Selanjutnya pilih:

1. Ukuran peta: Tiny, Small, atau Normal.
2. Jenis peta: Random, Cluster, atau Maze.
3. Masukkan ID peta.

Contoh:

```text
Pilihan: 1

Pilih ukuran peta:
1. Tiny
2. Small
3. Normal

Pilihan: 2

Pilih jenis peta:
1. Random
2. Cluster
3. Maze

Pilihan: 1

Masukkan ID peta:
small_random_01
```

Output akan disimpan di:

```text
Experiment_Results/
└── HasilEksperimenTahap1/
    └── Small/
        └── small_random_01/
            └── results.csv
```

### 5.2 Eksperimen Tahap 2A

Pilih:

```text
2. Hasil Eksperimen Tahap 2
1. Tahap 2A - Pengujian Hyperparameter
```

Pilih hyperparameter yang ingin diuji:

```text
1. Population
2. Crossover
3. Mutation
4. Elitism
```

#### A. Pengujian Population

Khusus Population, tersedia lima pilihan nilai:

```text
Pilih nilai Population:

1. 50
2. 100
3. 250
4. 500
5. 1000
```

Contoh pengujian Population 100:

1. Pilih Tahap 2.
2. Pilih Tahap 2A.
3. Pilih Population.
4. Pilih nilai 100.
5. Pilih ukuran peta.
6. Pilih jenis peta.
7. Masukkan ID peta.

Contoh:

```text
Pilihan: 2
Pilihan: 1
Pilihan: 1
Pilihan: 2

Pilih ukuran peta:
1. Tiny
2. Small
3. Normal

Pilihan: 2

Pilih jenis peta:
1. Random
2. Cluster
3. Maze

Pilihan: 1

Masukkan ID peta:
small_random_01
```

Output akan tersimpan di:

```text
Experiment_Results/
└── HasilEksperimenTahap2/
    └── Tahap2A/
        └── Population/
            └── 100/
                └── Small/
                    └── small_random_01/
                        └── results.csv
```

Struktur tersebut berlaku untuk kelima nilai Population:

```text
Population/
├── 50/
├── 100/
├── 250/
├── 500/
└── 1000/
```

Setiap nilai memiliki folder tersendiri agar hasil eksperimen tidak tercampur.

#### B. Pengujian Crossover, Mutation, dan Elitism

Untuk hyperparameter lainnya, pilih:

- Crossover
- Mutation
- Elitism

Masukkan nilai hyperparameter yang sedang diuji melalui `param.txt`.

Struktur output tetap:

```text
Tahap2A/
├── Crossover/
│   └── Small/
│       └── small_random_01/
│           └── results.csv
│
├── Mutation/
│   └── Small/
│       └── small_random_01/
│           └── results.csv
│
└── Elitism/
    └── Small/
        └── small_random_01/
            └── results.csv
```

### 5.3 Eksperimen Tahap 2B

Pilih:

```text
2. Hasil Eksperimen Tahap 2
2. Tahap 2B - Kombinasi Hyperparameter
```

Selanjutnya pilih:

1. Ukuran peta.
2. Jenis peta.
3. ID peta.

Struktur output:

```text
Experiment_Results/
└── HasilEksperimenTahap2/
    └── Tahap2B/
        └── Small/
            └── small_random_01/
                └── results.csv
```

Tahap 2B tidak menggunakan pemisahan folder berdasarkan nilai Population seperti Tahap 2A.

### 5.4 CSV Integration Test

Pilih:

```text
3. CSV Integration Test
```

Masukkan ID peta untuk pengujian sistem pencatatan CSV.

Output:

```text
Experiment_Results/
└── CSV_Integration_Test/
    └── Test/
        └── test_map/
            └── results.csv
```

Mode ini digunakan untuk memastikan sistem CSV dapat membuat folder dan menulis hasil dengan benar.

### 5.5 Tahap Pengujian

Pilih:

```text
4. Tahap Pengujian
```

Ukuran peta otomatis ditetapkan menjadi Large.

Selanjutnya pilih jenis peta dan masukkan ID peta.

Output:

```text
Experiment_Results/
└── TahapPengujian/
    └── Large/
        └── large_random_01/
            └── results.csv
```

## 6. Melihat Hasil CSV

Setelah program selesai dijalankan, seluruh hasil eksperimen dapat ditemukan di folder:

```text
FireStation_AI/Experiment_Results/
```

Contoh lokasi hasil Tahap 2A Population 100:

```text
Experiment_Results/HasilEksperimenTahap2/Tahap2A/Population/100/Small/small_random_01/results.csv
```

Untuk membuka hasil:

1. Buka folder `Experiment_Results`.
2. Masuk ke folder tahap eksperimen yang diinginkan.
3. Masuk ke folder hyperparameter dan nilainya jika menggunakan Tahap 2A Population.
4. Masuk ke folder ukuran peta.
5. Masuk ke folder ID peta.
6. Buka `results.csv`.

File CSV dapat dibuka menggunakan:

- Microsoft Excel
- Google Sheets
- Visual Studio Code
- Notepad

**Disarankan menggunakan Microsoft Excel atau Google Sheets** untuk memudahkan analisis hasil eksperimen.

## 7. Struktur Folder Hasil Eksperimen

Secara keseluruhan, struktur penyimpanan hasil eksperimen adalah:

```text
Experiment_Results/
│
├── HasilEksperimenTahap1/
│   ├── Tiny/
│   ├── Small/
│   └── Normal/
│
├── HasilEksperimenTahap2/
│   ├── Tahap2A/
│   │   ├── Population/
│   │   │   ├── 50/
│   │   │   ├── 100/
│   │   │   ├── 250/
│   │   │   ├── 500/
│   │   │   └── 1000/
│   │   │
│   │   ├── Crossover/
│   │   ├── Mutation/
│   │   └── Elitism/
│   │
│   └── Tahap2B/
│       ├── Tiny/
│       ├── Small/
│       └── Normal/
│
├── CSV_Integration_Test/
│   └── Test/
│
└── TahapPengujian/
    └── Large/
```

Setiap folder kasus peta akan memiliki file `results.csv`.

## 8. Catatan Penting

1. Pastikan `input.txt` berisi peta yang sesuai dengan identitas eksperimen yang dipilih.
2. Pastikan konfigurasi `param.txt` sesuai dengan hyperparameter yang sedang diuji.
3. Gunakan ID peta yang konsisten agar hasil eksperimen mudah ditelusuri.
4. Setiap kombinasi identitas output harus memiliki lokasi file yang berbeda.
5. Sistem CSV menggunakan mekanisme pembuatan file baru. Jika `results.csv` pada lokasi yang sama sudah ada, program tidak akan menimpanya. Gunakan ID peta atau folder eksperimen yang berbeda untuk hasil baru.
6. Menu CSV Output Form mengatur metadata dan lokasi penyimpanan hasil, bukan keseluruhan konfigurasi atau alur eksperimen GA.

**Perhatian mengenai runner saat ini:** `main.java` yang tersedia masih menjalankan loop 16 kombinasi model dengan 30 seed. Pemilihan menu Tahap 2A, Tahap 2B, atau Tahap Pengujian belum otomatis mengubah loop eksperimen, jumlah seed, maupun parameter GA. Pastikan runner eksperimen yang digunakan sudah sesuai dengan tahap yang sedang dikerjakan.
