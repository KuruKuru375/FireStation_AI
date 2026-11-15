
# FireStation AI – Genetic Algorithm

## 1. Gambaran Umum

Project ini mengimplementasikan **Genetic Algorithm (GA)** untuk menentukan penempatan sejumlah Fire Station pada peta berbentuk grid sehingga rata-rata jarak rumah ke Fire Station terdekat menjadi minimum.

Variasi GA yang tersedia:

- **Representasi kromosom:** Bit dan Integer.
- **Parent selection:** Roulette Wheel dan Tournament.
- **Crossover:** Single Point dan Two Point.
- **Mutation:** Random Move dan By Distance.

Project juga menyediakan pencatatan hasil setiap eksekusi GA ke file CSV.

> [Note]
> **CSV Output Form bukan tempat untuk mengatur parameter GA.**
>
> CSV Output Form hanya menentukan identitas eksperimen dan lokasi penyimpanan CSV. Parameter yang benar-benar digunakan GA tetap harus disesuaikan secara manual melalui `input.txt`, `param.txt`, dan konfigurasi runner di `main.java`.

---

## 2. Struktur Project

Berikut struktur file kode dan folder yang ada di project:

```text
FireStation_AI/
│
├── .gitignore
├── .vscode/
│   └── settings.json
│
├── main.java
├── FireStationGA.java
├── Population.java
├── Individual.java
├── IndividualBit.java
├── IndividualInt.java
├── FS.java
├── sel.java
│
├── CsvOutputForm.java
├── FireStationCsvLogger.java
│
├── OutputTools/
│   └── csv/
│       └── CsvWriter.java
│
├── input.txt
├── param.txt
│
├── TestCase/
│   ├── n20_acak_01.txt
│   ├── n20_acak_02.txt
│   ├── n20_acak_03.txt
│   ├── n20_acak_04.txt
│   ├── n20_klaster_01.txt
│   ├── n20_klaster_02.txt
│   ├── n20_klaster_03.txt
│   ├── n20_klaster_04.txt
│   ├── n20_labirin_01.txt
│   ├── n20_labirin_02.txt
│   ├── n20_labirin_03.txt
│   ├── n20_labirin_04.txt
│   ├── n40_...txt
│   ├── n60_...txt
│   └── n80_...txt
│
├── Experiment_Results/
│   └── (hasil CSV setiap tahap eksperimen)
│
└── README.md
```

Folder `TestCase/` berisi file peta yang dapat digunakan sebagai test case. Folder `Experiment_Results/` merupakan tempat penyimpanan hasil CSV.

File `.class` merupakan hasil kompilasi Java dan bukan file sumber yang perlu diedit secara manual.

---

## 3. File yang Harus Diatur Sebelum Menjalankan Eksperimen

Bagian ini penting agar hasil yang dicatat di CSV benar-benar sesuai dengan eksperimen yang sedang dilakukan.

### 3.1. `input.txt` – Peta yang Benar-benar Dijalankan

Program saat ini membaca peta dari file `input.txt`, bukan langsung dari file di folder `TestCase/`.

Format isi file:

```text
m n p
baris_peta_1
baris_peta_2
...
baris_peta_m
```

Keterangan:

| Simbol | Arti |
|---|---|
| `m` | Jumlah baris peta |
| `n` | Jumlah kolom peta |
| `p` | Jumlah Fire Station yang harus ditempatkan |
| `H` | Rumah |
| `T` | Pohon |
| `.` | Sel kosong |

**Cara mengganti test case:**

1. Tentukan file peta yang ingin diuji dari folder `TestCase/`.
2. Buka file tersebut.
3. Salin seluruh isinya.
4. Ganti seluruh isi `input.txt` dengan isi file test case tersebut.
5. Pastikan ukuran peta dan jumlah Fire Station pada header sudah benar.
6. Saat program meminta `Map ID`, masukkan nama test case yang benar agar identitas dan folder CSV sesuai.

> [!WARNING]
> Memasukkan nama file pada pertanyaan `Map ID` **tidak otomatis memuat file tersebut**. `Map ID` hanya digunakan sebagai metadata dan bagian dari lokasi output. Peta yang diproses tetap berasal dari `input.txt`.

#### Catatan kondisi file test case saat ini

Berdasarkan header file di folder `TestCase/` pada ZIP project:

| Kelompok nama file | Header peta saat ini | Keterangan terhadap spesifikasi tugas |
|---|---|---|
| `n20_...` | `20 20 3` | Ukuran 20×20, tetapi jumlah FS 3. Spesifikasi Small adalah 20×20 dengan 10 FS. |
| `n40_...` | `40 40 10` | Ukuran 40×40, tetapi jumlah FS 10. Spesifikasi Normal adalah 40×40 dengan 24 FS. |
| `n60_...` | `60 60 24` | Ukuran 60×60 tidak termasuk ukuran kasus resmi pada tabel tugas. |
| `n80_...` | `80 80 40` | Sesuai ukuran dan jumlah FS untuk kasus Large. |

Spesifikasi kasus pada tugas:

| Ukuran kasus | Grid | Jumlah FS | Evaluation Budget |
|---|---:|---:|---:|
| Tiny | 10 × 10 | 3 | 10.000 |
| Small | 20 × 20 | 10 | 50.000 |
| Normal | 40 × 40 | 24 | 200.000 |
| Large | 80 × 80 | 40 | 500.000 |

**Periksa dan sesuaikan test case sebelum eksperimen resmi.** Jangan mengandalkan awalan nama file saja; periksa header isi file.

### 3.2. `param.txt` – Parameter Numerik Genetic Algorithm

`param.txt` merupakan tempat mengubah parameter numerik GA. Program membaca lima baris secara berurutan.

Format:

```text
population
generations
crossover_rate
mutation_rate
elitism_percentage
```

Isi `param.txt` saat ini pada ZIP:

```text
100
2000
0.8
0.05
0.2
```

Arti setiap baris:

| Baris | Variabel di `main.java` | Parameter | Nilai saat ini |
|---|---|---|---:|
| 1 | `maxPopulationSize` | Population | 100 |
| 2 | `totalGeneration` | Generations | 2000 |
| 3 | `crossoverRate` | Crossover Rate (Pc) | 0.8 |
| 4 | `mutationRate` | Mutation Rate (Pm) | 0.05 |
| 5 | `elitismPct` | Elitism Percentage | 0.2 |

**Aturan format angka:** gunakan titik (`.`) sebagai pemisah desimal, bukan koma. `main.java` menggunakan `sc.useLocale(Locale.US)` saat membaca `param.txt`.

Contoh nilai `param.txt` untuk Tahap 1 pada peta Normal:

```text
100
2000
0.8
0.05
0.2
```

Jangan menganggap nilai di atas berlaku untuk semua ukuran peta. Population dan Generations harus disesuaikan dengan Evaluation Budget ukuran peta yang sedang diuji.

### 3.3. Menghitung `Generations` dari Evaluation Budget

Gunakan rumus:

```text
Generations = Evaluation Budget / Population
```

Contoh jika Population = 100:

| Ukuran peta | Evaluation Budget | Population | Generations |
|---|---:|---:|---:|
| Tiny | 10.000 | 100 | 100 |
| Small | 50.000 | 100 | 500 |
| Normal | 200.000 | 100 | 2.000 |
| Large | 500.000 | 100 | 5.000 |

Untuk Tahap 2A Population, Generations perlu dihitung ulang setiap kali Population diubah.

Contoh jika menguji Population = 250 pada peta Normal:

```text
200000 / 250 = 800 generations
```

Maka isi `param.txt` menjadi:

```text
250
800
0.8
0.05
0.2
```

Contoh jumlah generasi untuk variasi Population pada Tiny, Small, dan Normal:

| Population | Tiny (10.000) | Small (50.000) | Normal (200.000) |
|---:|---:|---:|---:|
| 50 | 200 | 1.000 | 4.000 |
| 100 | 100 | 500 | 2.000 |
| 250 | 40 | 200 | 800 |
| 500 | 20 | 100 | 400 |
| 1000 | 10 | 50 | 200 |

Setiap kali mengubah Population, jangan lupa mengubah Generations pada baris kedua.

### 3.4. `main.java` – Pengaturan Runner Eksperimen

`main.java` menentukan jumlah seed, membangkitkan kumpulan seed, menentukan kombinasi model yang dijalankan, membaca `input.txt` dan `param.txt`, serta memanggil GA.

Ada beberapa bagian yang perlu diperhatikan.

#### A. Jumlah Seed / Pengulangan

Cari deklarasi berikut di bagian awal `main.java`:

```java
int[] seed = new int[10];
```

**Kondisi kode saat ini adalah 10 seed.** Angka `10` pada deklarasi array itulah yang menentukan banyaknya seed yang dibuat.

Jika eksperimen membutuhkan 30 seed, ubah menjadi:

```java
int[] seed = new int[30];
```

Tidak perlu mengubah batas pada dua loop berikut karena keduanya sudah menggunakan `seed.length`:

```java
for (int i = 0; i < seed.length; i++) {
    seed[i] = rand.nextInt(batas + 1);
}
```

```java
for (int ct = 0; ct < seed.length; ct++) {
    // satu kali eksekusi GA untuk seed[ct]
}
```

Dengan demikian, setelah ukuran array diubah menjadi 30, program akan membangkitkan dan menggunakan 30 seed.

Jumlah seed yang perlu digunakan:

| Tahap eksperimen | Jumlah seed per konfigurasi |
|---|---:|
| Tahap 1 | 30 |
| Tahap 2A | 10 (mengikuti koreksi jumlah seed yang disampaikan untuk tahap ini) |
| Tahap 2B | 30 |
| Pengujian akhir | Minimal 10; gunakan 10 untuk target minimum |

**Penting:** kode saat ini hanya memiliki satu deklarasi `seed` yang berlaku untuk seluruh proses. Program belum otomatis mengganti jumlah seed berdasarkan pilihan tahap di CSV Output Form. Jadi, ubah `new int[10]` menjadi `new int[30]` sebelum menjalankan tahap yang membutuhkan 30 seed, lalu kembalikan menjadi `new int[10]` untuk tahap yang membutuhkan 10 seed.

#### B. Seed Utama Kelompok

Cari bagian:

```java
int nmp_ketua = 24019;
Random rand = new Random(nmp_ketua);
```

`nmp_ketua` merupakan seed utama untuk membangkitkan kumpulan seed eksperimen. Gunakan seed utama yang telah ditetapkan kelompok. Untuk perbandingan model yang adil, gunakan kumpulan seed yang sama pada konfigurasi model yang dibandingkan.

Jangan mengganti seed utama di tengah rangkaian eksperimen yang seharusnya menggunakan seed set yang sama.

#### C. Jumlah dan Kombinasi Model GA

Cari bagian:

```java
String[] daftarKromosom = { "bit", "int" };
boolean[] pilihan = { false, true };
```

Runner menggunakan nested loop untuk mengombinasikan representasi kromosom dan tiga pilihan operator berikut:

| Variabel loop | `false` | `true` |
|---|---|---|
| `pakaiTournament` | Roulette Wheel | Tournament |
| `pakaiTwoPoint` | Single Point | Two Point |
| `pakaiMutasiJarak` | Random Move | By Distance |

Dengan dua representasi dan dua pilihan untuk masing-masing operator, nested loop tersebut menghasilkan:

```text
2 representasi × 2 selection × 2 crossover × 2 mutation = 16 model
```

Untuk **Tahap 1**, 16 model memang diperlukan.

Untuk **Tahap 2**, hanya minimal tiga model terbaik hasil Tahap 1 yang perlu diuji. Untuk **pengujian akhir**, jalankan model terbaik dari Tahap 2 sesuai hasil seleksi kelompok.

> [!WARNING]
> Pada implementasi saat ini, nested loop di `main.java` tetap menjalankan seluruh 16 model. Memilih Tahap 2 atau Tahap Pengujian pada CSV Output Form belum otomatis membatasi model menjadi tiga model terbaik atau satu model final. Bagian runner/model loop harus disesuaikan sebelum menjalankan Tahap 2 dan pengujian akhir.

#### D. Hubungan `main.java` dengan `param.txt`

Nilai numerik yang dibaca dari `param.txt` diteruskan ke constructor `FireStationGA` melalui bagian berikut:

```java
FireStationGA ga = new FireStationGA(
    rnd,
    tipeKromosom,
    maxPopulationSize,
    totalGeneration,
    elitismPct,
    crossoverRate,
    mutationRate,
    listJarakTerbaik,
    p
);
```

Jadi, sumber parameter numerik tetap `param.txt`. `main.java` membaca nilai tersebut dan meneruskannya ke GA.

### 3.5. Ringkasan: Kalau Mau Mengubah Apa, Buka File Mana?

| Yang ingin diubah | File dan bagian yang diubah |
|---|---|
| Peta yang benar-benar dijalankan | Salin isi test case pilihan ke `input.txt` |
| Population | `param.txt`, baris 1 |
| Generations | `param.txt`, baris 2 |
| Crossover Rate (Pc) | `param.txt`, baris 3 |
| Mutation Rate (Pm) | `param.txt`, baris 4 |
| Elitism Percentage | `param.txt`, baris 5 |
| Seed utama kelompok | `main.java`, variabel `nmp_ketua` |
| Jumlah seed/run | `main.java`, deklarasi `int[] seed = new int[...];` |
| Kombinasi operator/model yang dijalankan | `main.java`, `daftarKromosom`, `pilihan`, dan nested loop model |
| Tahap, Map ID, Map Type, dan lokasi CSV | Pilihan saat menjalankan `CsvOutputForm.java` |
| Header dan isi kolom CSV | `FireStationCsvLogger.java` |
| Penulisan file CSV dan delimiter | `OutputTools/csv/CsvWriter.java` |

---

## 4. Rencana dan Ketentuan Eksperimen

### 4.1. Tahap 1 – Mencari Kombinasi Operator GA Terbaik

Tujuan Tahap 1 adalah membandingkan 16 model GA.

Ketentuan:

- 2 representasi kromosom.
- 2 parent selection.
- 2 crossover.
- 2 mutation.
- Ukuran peta: Tiny, Small, dan Normal.
- Setiap ukuran menggunakan 4 peta: 2 Random, 1 Cluster, dan 1 Maze.
- Setiap model dan peta diuji 30 kali menggunakan kumpulan seed yang sama.
- Hyperparameter baseline: Population 100, Pc 0.8, Pm 0.05, dan Elitism 0.2.
- Generations disesuaikan dengan Evaluation Budget.

Total run yang ditargetkan:

```text
16 model × 3 ukuran peta × 4 peta × 30 seed = 5.760 run
```

Setelah Tahap 1 selesai, analisis CSV dan tentukan minimal tiga model terbaik berdasarkan kriteria evaluasi kelompok.

### 4.2. Tahap 2A – Pengujian Hyperparameter secara Berurutan (OFAT)

Tahap 2A menggunakan minimal tiga model terbaik dari Tahap 1. Test case yang digunakan harus berbeda dari Tahap 1, dengan komposisi 2 Random, 1 Cluster, dan 1 Maze untuk masing-masing ukuran Tiny, Small, dan Normal.

Pengujian dilakukan secara berurutan. Satu hyperparameter diubah, sedangkan hyperparameter lain dipertahankan.

Urutan pengujian:

1. Population.
2. Crossover Rate.
3. Mutation Rate.
4. Elitism Percentage.

Untuk Population, contoh lima nilai yang digunakan dalam instruksi tugas adalah:

```text
50, 100, 250, 500, 1000
```

Untuk Crossover, Mutation, dan Elitism, tentukan lima nilai uji sesuai rancangan kelompok/instruksi dosen.

Setelah satu hyperparameter diuji, tentukan nilai terbaiknya dari hasil CSV. Gunakan nilai terbaik tersebut sebagai nilai tetap ketika melanjutkan tuning hyperparameter berikutnya.

Jumlah seed Tahap 2A mengikuti koreksi yang disampaikan untuk tahap ini, yaitu 10 seed per konfigurasi.

Target jumlah run dengan 3 model, 4 hyperparameter, 5 nilai, 4 peta, 3 ukuran, dan 10 seed:

```text
3 model × (4 hyperparameter × 5 nilai × 10 seed) × 4 peta × 3 ukuran
= 7.200 run
```

**Pengingat konfigurasi:** setiap nilai hyperparameter yang diuji harus benar-benar diubah di `param.txt`. Untuk Population, ubah baris 1 dan hitung ulang Generations pada baris 2. Pilihan nilai Population pada CSV Output Form hanya membuat folder dengan label nilai tersebut.

### 4.3. Tahap 2B – Kombinasi Hyperparameter

Tahap 2B juga menggunakan minimal tiga model terbaik hasil Tahap 1 dan test case yang sama dengan Tahap 2A.

Berdasarkan hasil Tahap 2A:

- Pilih dua nilai Population terbaik.
- Pilih dua nilai Crossover Rate terbaik.
- Gunakan nilai Mutation Rate terbaik.
- Gunakan nilai Elitism Percentage terbaik.

Lakukan seluruh kombinasi yang ditentukan. Saat Population berubah, Generations harus dihitung ulang berdasarkan Evaluation Budget.

Jumlah seed: 30 seed per konfigurasi.

Target jumlah run sesuai rumus tugas:

```text
3 model × 2 nilai Population × 2 nilai Crossover
× 3 ukuran × 4 peta × 30 seed
= 4.320 run
```

### 4.4. Pengujian Akhir

Model terbaik dari langkah terakhir Tahap 2 diuji pada enam test case Large:

- 2 peta Random.
- 2 peta Cluster.
- 2 peta Maze.

Setiap peta diuji minimal 10 kali dengan seed berbeda.

Target minimum:

```text
6 test case × 10 run = 60 run
```

Gunakan konfigurasi model dan hyperparameter terbaik hasil Tahap 2. Jangan menganggap memilih menu Tahap Pengujian otomatis membuat runner hanya menjalankan model final; konfigurasi runner di `main.java` harus sesuai dengan model yang akan diuji.

---

## 5. CSV Output Form – Pengaturan Lokasi Hasil

CSV Output Form digunakan untuk memberi identitas pada hasil dan menentukan lokasi penyimpanan file `results.csv`.

Menu yang tersedia:

```text
1. Hasil Eksperimen Tahap 1
2. Hasil Eksperimen Tahap 2
3. CSV Integration Test
4. Tahap Pengujian
```

Saat diminta, pengguna mengisi atau memilih tahap eksperimen, jenis eksperimen (untuk Tahap 2), parameter yang diuji (untuk Tahap 2A), ukuran peta, jenis peta, dan Map ID.

**Data tersebut terutama dipakai sebagai metadata dan pembentuk lokasi output.** Pilihan itu tidak otomatis mengganti isi `input.txt`, nilai di `param.txt`, jumlah seed, maupun kombinasi model di `main.java`.

### 5.1. Lokasi Output Berdasarkan Tahap

#### Tahap 1

```text
Experiment_Results/
└── HasilEksperimenTahap1/
    └── <Tiny|Small|Normal>/
        └── <Map_ID>/
            └── results.csv
```

#### Tahap 2A – Population

Khusus parameter Population, form meminta nilai Population dan menyertakannya sebagai nama subfolder:

```text
Experiment_Results/
└── HasilEksperimenTahap2/
    └── Tahap2A/
        └── Population/
            └── <50|100|250|500|1000>/
                └── <Tiny|Small|Normal>/
                    └── <Map_ID>/
                        └── results.csv
```

**Penting:** nilai Population yang dipilih di form hanya menjadi label folder. Pengguna tetap harus mengubah `param.txt` baris 1 ke nilai yang sama dan Generations pada baris 2.

#### Tahap 2A – Crossover, Mutation, atau Elitism

```text
Experiment_Results/
└── HasilEksperimenTahap2/
    └── Tahap2A/
        └── <Crossover|Mutation|Elitism>/
            └── <Tiny|Small|Normal>/
                └── <Map_ID>/
                    └── results.csv
```

Untuk tahap ini, angka parameter yang sedang diuji tetap harus diubah manual pada baris `param.txt` yang sesuai.

#### Tahap 2B

```text
Experiment_Results/
└── HasilEksperimenTahap2/
    └── Tahap2B/
        └── <Tiny|Small|Normal>/
            └── <Map_ID>/
                └── results.csv
```

#### Pengujian Akhir

```text
Experiment_Results/
└── TahapPengujian/
    └── Large/
        └── <Map_ID>/
            └── results.csv
```

#### CSV Integration Test

```text
Experiment_Results/
└── CSV_Integration_Test/
    └── Test/
        └── <Map_ID>/
            └── results.csv
```

### 5.2. Contoh Struktur Lengkap Folder Hasil Akhir

Setelah seluruh rangkaian eksperimen selesai, susunan folder hasil yang dituju adalah sebagai berikut. Placeholder `<Map_ID>` diganti dengan nama test case yang benar-benar digunakan.

```text
Experiment_Results/
│
├── HasilEksperimenTahap1/
│   ├── Tiny/
│   │   ├── <Tiny_Random_01>/results.csv
│   │   ├── <Tiny_Random_02>/results.csv
│   │   ├── <Tiny_Cluster_01>/results.csv
│   │   └── <Tiny_Maze_01>/results.csv
│   ├── Small/
│   │   ├── <Small_Random_01>/results.csv
│   │   ├── <Small_Random_02>/results.csv
│   │   ├── <Small_Cluster_01>/results.csv
│   │   └── <Small_Maze_01>/results.csv
│   └── Normal/
│       ├── <Normal_Random_01>/results.csv
│       ├── <Normal_Random_02>/results.csv
│       ├── <Normal_Cluster_01>/results.csv
│       └── <Normal_Maze_01>/results.csv
│
├── HasilEksperimenTahap2/
│   ├── Tahap2A/
│   │   ├── Population/
│   │   │   ├── 50/
│   │   │   │   └── <Tiny|Small|Normal>/<Map_ID>/results.csv
│   │   │   ├── 100/
│   │   │   │   └── <Tiny|Small|Normal>/<Map_ID>/results.csv
│   │   │   ├── 250/
│   │   │   │   └── <Tiny|Small|Normal>/<Map_ID>/results.csv
│   │   │   ├── 500/
│   │   │   │   └── <Tiny|Small|Normal>/<Map_ID>/results.csv
│   │   │   └── 1000/
│   │   │       └── <Tiny|Small|Normal>/<Map_ID>/results.csv
│   │   ├── Crossover/
│   │   │   └── <Tiny|Small|Normal>/<Map_ID>/results.csv
│   │   ├── Mutation/
│   │   │   └── <Tiny|Small|Normal>/<Map_ID>/results.csv
│   │   └── Elitism/
│   │       └── <Tiny|Small|Normal>/<Map_ID>/results.csv
│   │
│   └── Tahap2B/
│       └── <Tiny|Small|Normal>/<Map_ID>/results.csv
│
├── TahapPengujian/
│   └── Large/
│       ├── <Large_Random_01>/results.csv
│       ├── <Large_Random_02>/results.csv
│       ├── <Large_Cluster_01>/results.csv
│       ├── <Large_Cluster_02>/results.csv
│       ├── <Large_Maze_01>/results.csv
│       └── <Large_Maze_02>/results.csv
│
└── CSV_Integration_Test/
    └── Test/<Map_ID>/results.csv
```

Struktur di atas merupakan susunan target hasil akhir. Folder dan file akan terbentuk ketika eksperimen dijalankan melalui program; tidak semua folder tersebut sudah tersedia di repository saat ini.

### 5.3. File CSV yang Sudah Ada

`CsvWriter.java` membuat file baru dengan opsi `CREATE_NEW`. Artinya, program tidak menimpa file `results.csv` yang sudah ada.

Jika file pada lokasi yang sama sudah ada, proses pembuatan CSV akan gagal. Untuk menjalankan ulang eksperimen dengan lokasi yang sama, pindahkan/arsipkan file sebelumnya atau gunakan Map ID/lokasi output yang berbeda.

---

## 6. Isi dan Bentuk Hasil CSV

Satu baris CSV mewakili satu kali eksekusi GA (*one run*). Header CSV saat ini terdiri dari 30 kolom:

```csv
run_id,stage,experiment_id,model_id,representation,selection,crossover,mutation_operator,map_id,map_type,grid_rows,grid_cols,num_fs,population,generations,evaluation_budget,pc,pm,elitism_rate,seed_set_id,seed,run_number,best_cost,best_fitness,evaluations_used,generations_completed,execution_time_ms,termination_reason,run_status,fs_positions
```

Kelompok informasi yang dicatat:

| Kelompok | Kolom | Isi |
|---|---|---|
| Identitas eksekusi | `run_id`, `stage`, `experiment_id`, `model_id` | Identitas run, tahap, eksperimen, dan model |
| Operator GA | `representation`, `selection`, `crossover`, `mutation_operator` | Konfigurasi model GA |
| Test case | `map_id`, `map_type`, `grid_rows`, `grid_cols`, `num_fs` | Identitas dan karakteristik peta |
| Parameter | `population`, `generations`, `evaluation_budget`, `pc`, `pm`, `elitism_rate` | Parameter GA yang dicatat saat run |
| Seed | `seed_set_id`, `seed`, `run_number` | Identitas seed dan nomor pengulangan |
| Hasil | `best_cost`, `best_fitness`, `evaluations_used` | Hasil solusi dan jumlah evaluasi |
| Eksekusi | `generations_completed`, `execution_time_ms`, `termination_reason`, `run_status` | Informasi proses run |
| Solusi | `fs_positions` | Koordinat Fire Station yang ditemukan |

Contoh potongan satu baris:

```csv
INPUT_TEST_M01_R00,TEST,CSV_INTEGRATION,M01,bit,roulette,singlepoint,randommove,INPUT_TEST,TEST_ONLY,20,20,3,100,2000,200000,0.8,0.05,0.2,SEEDSET_24019,55564,0,5.95,0.14388489208633093,200000,2000,655,GENERATION_LIMIT,SUCCESS,"(13,5);(5,14);(16,17)"
```

Kolom `fs_positions` berisi koordinat Fire Station dalam format `(x,y)` dan dipisahkan dengan titik koma.

### Membuka CSV di Excel

Jika seluruh data tampil pada satu kolom:

1. Buka Excel.
2. Pilih tab **Data**.
3. Pilih **From Text/CSV**.
4. Pilih file `results.csv`.
5. Atur delimiter menjadi **Comma (,)**.
6. Pastikan preview menunjukkan kolom-kolom terpisah, lalu klik **Load**.

---

## 7. Catatan Implementasi Saat Ini

Bagian ini menjelaskan kondisi kode yang ada di ZIP. Catatan ini penting agar pengguna tahu bagian mana yang sudah berjalan otomatis dan bagian mana yang masih perlu diatur secara manual.

| Bagian | Kondisi implementasi saat ini | Yang perlu dilakukan untuk eksperimen resmi |
|---|---|---|
| Jumlah seed | `main.java` memakai `int[] seed = new int[10];` | Ubah menjadi `[30]` untuk Tahap 1 dan Tahap 2B; gunakan `[10]` untuk Tahap 2A dan target minimum pengujian akhir. |
| Model GA | Nested loop menjalankan 16 kombinasi model | Untuk Tahap 2, sesuaikan runner agar hanya menjalankan minimal 3 model terbaik. Untuk pengujian akhir, jalankan model terbaik yang dipilih. |
| Peta | `main.java` selalu membaca `input.txt` | Salin isi test case yang akan dijalankan ke `input.txt` sebelum run. |
| Hyperparameter | Dibaca dari `param.txt` | Ubah lima baris `param.txt` sesuai konfigurasi eksperimen. |
| Ukuran dan tipe peta di form | Dipakai sebagai metadata dan folder output | Cocokkan pilihan dengan isi aktual `input.txt`. |
| Map ID | Dipakai sebagai metadata dan nama folder | Isi dengan ID peta yang benar; Map ID bukan pemilih file peta. |
| Population pada Tahap 2A | Nilai pilihan menjadi nama folder Population | Samakan pilihan form dengan nilai Population pada `param.txt` baris 1. |
| Evaluation Budget | Dihitung dari `population × generations` | Hitung Generations sesuai budget resmi; kode tidak otomatis mengubah Generations. |
| Tahap eksperimen | Form menyimpan label tahap eksperimen | Label tahap belum otomatis mengubah jumlah seed atau daftar model yang dijalankan. |

### Contoh: Mengubah Jumlah Run dari 10 Menjadi 30

Kondisi kode saat ini di `main.java`:

```java
int[] seed = new int[10];
```

Jika sedang menjalankan Tahap 1, jumlah run per model per peta harus 30 seed. Ubah menjadi:

```java
int[] seed = new int[30];
```

Loop pembangkitan seed dan loop eksekusi tidak perlu diganti karena keduanya memakai `seed.length`.

Jika setelahnya menjalankan Tahap 2A yang membutuhkan 10 seed sesuai koreksi dosen, kembalikan deklarasi tersebut menjadi:

```java
int[] seed = new int[10];
```

Hal yang sama berlaku untuk pengujian akhir dengan target minimum 10 run per peta. Tahap 2B kembali menggunakan 30 seed.

### Contoh: Memilih Population 250

Misalnya CSV Output Form dipilih:

```text
Tahap       : Tahap 2A
Parameter   : Population
Population  : 250
Map Size    : Normal
Map ID      : normal_random_01
```

Pilihan tersebut membuat folder output memakai label `Population/250`. Namun apabila `param.txt` masih berisi:

```text
100
2000
0.8
0.05
0.2
```

GA tetap menggunakan Population 100 dan Generations 2000.

Agar benar-benar menguji Population 250 pada Normal dengan Evaluation Budget 200.000, ubah `param.txt` menjadi:

```text
250
800
0.8
0.05
0.2
```

### Contoh: Mengganti Test Case

Jika ingin menjalankan `n80_klaster_01.txt`:

1. Buka `TestCase/n80_klaster_01.txt`.
2. Salin seluruh isinya ke `input.txt`.
3. Pastikan header peta adalah `80 80 40`.
4. Atur `param.txt` sesuai Population dan Evaluation Budget Large.
5. Pilih menu Tahap Pengujian.
6. Pilih jenis peta Cluster.
7. Masukkan Map ID `n80_klaster_01`.

Dengan begitu, peta yang dijalankan dan metadata CSV merujuk pada test case yang sama.

---

## 8. Cara Compile dan Menjalankan Program

Jalankan perintah dari root folder `FireStation_AI`.

### Compile

```cmd
javac -encoding UTF-8 -cp . -d . *.java OutputTools/csv/CsvWriter.java
```

### Run

```cmd
java -cp . main
```

Saat program berjalan, ikuti pertanyaan pada CSV Output Form. Sebelum menjalankan, pastikan `input.txt`, `param.txt`, jumlah seed, dan konfigurasi model di `main.java` sudah disesuaikan dengan eksperimen yang akan dilakukan.

---

## 9. Checklist Sebelum Menjalankan Eksperimen

Gunakan checklist berikut setiap kali akan menjalankan satu kelompok eksperimen.

- [ ] Isi `input.txt` sudah berasal dari test case yang benar.
- [ ] Header `input.txt` (baris, kolom, jumlah FS) sudah sesuai spesifikasi ukuran peta.
- [ ] Map ID dan Map Type pada CSV Output Form sesuai dengan peta aktual.
- [ ] Population pada `param.txt` sudah benar.
- [ ] Generations pada `param.txt` sudah dihitung sesuai Evaluation Budget.
- [ ] Crossover Rate pada `param.txt` sudah benar.
- [ ] Mutation Rate pada `param.txt` sudah benar.
- [ ] Elitism Percentage pada `param.txt` sudah benar.
- [ ] Jumlah seed pada `main.java` sudah sesuai tahap (10 atau 30).
- [ ] Daftar model pada runner `main.java` sudah sesuai tahap (16 model, minimal 3 model terpilih, atau model final).
- [ ] Lokasi output CSV yang dipilih belum berisi `results.csv` dari run sebelumnya.

**Pengingat akhir:** konfigurasi eksperimen menentukan apa yang benar-benar dijalankan GA; CSV Output Form hanya menentukan bagaimana dan di mana hasil tersebut dicatat.
