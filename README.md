# Fire Station — Genetic Algorithm

Program ini mencari posisi sejumlah Fire Station (FS) pada peta agar rata-rata jarak rumah ke FS terdekat sekecil mungkin. Pencarian menggunakan Genetic Algorithm (GA), yaitu proses memperbaiki kumpulan calon solusi dari generasi ke generasi.

Untuk percobaan pertama, ikuti langkah 1–7 di bawah menggunakan peta **Tiny Acak** (`TestCase/n10_acak_01.txt`). Setelah berhasil, gunakan alur yang sama untuk eksperimen lain.

## Sebelum mulai

Panduan ini mengasumsikan Java/JDK sudah tersedia. Buka terminal di folder proyek yang berisi `main.java`, `input.txt`, dan `param.txt`.

Ada tiga tempat untuk mengatur apa yang akan dijalankan:

| File | Yang diatur |
|---|---|
| `input.txt` | Peta dan jumlah FS yang akan ditempatkan |
| `param.txt` | Angka parameter GA, misalnya population dan crossover rate |
| `main.java` | Kombinasi metode GA dan jumlah pengulangan |

**Menu yang muncul saat program berjalan hanya mengatur identitas dan folder hasil.** Karena itu, siapkan ketiga file tersebut sebelum menjalankan program.

## 1. Siapkan peta di `input.txt`

Buka `TestCase/n10_acak_01.txt`, salin seluruh isinya, lalu tempel untuk mengganti seluruh isi `input.txt` dan simpan. Baris pertamanya adalah:

```text
10 10 3
```

Artinya: peta berisi **10 baris × 10 kolom**, dengan **3 FS** yang harus ditempatkan. Di bawahnya terdapat 10 baris peta, masing-masing berisi 10 karakter:

| Karakter | Arti |
|---|---|
| `H` | Rumah |
| `T` | Pohon |
| `.` | Sel kosong yang dapat menjadi lokasi FS |

Secara umum, header berformat `m n p`: jumlah baris, jumlah kolom, lalu jumlah FS. Jika membuat peta sendiri, sertakan rumah dan gunakan `0 < p < jumlah sel kosong` agar kedua representasi dan mutasinya dapat dijalankan.

Untuk mengganti test case nanti, salin isi file peta lain ke `input.txt`. Program selalu membaca file ini; mengetik nama peta pada menu tidak memuat file dari `TestCase/`.

## 2. Siapkan parameter di `param.txt`

Untuk percobaan pertama, gunakan isi berikut dan simpan. Masukkan angkanya saja, tanpa komentar:

```text
100
2000
0.8
0.05
0.2
```

| Baris | Arti |
|---|---|
| 1: `100` | Population: jumlah calon solusi dalam satu generasi |
| 2: `2000` | Batas generasi awal; dibaca lalu dihitung ulang oleh kode |
| 3: `0.8` | Crossover rate: peluang penggabungan dua induk, yaitu 80% |
| 4: `0.05` | Mutation rate: peluang perubahan pada anak, yaitu 5% |
| 5: `0.2` | Elitism: bagian solusi terbaik yang dipertahankan, yaitu 20% |

Gunakan titik untuk desimal. Population harus positif; crossover rate, mutation rate, dan elitism berada pada rentang 0–1.

**Batas generasi yang benar-benar dipakai = budget / population (dibulatkan ke bawah).** Budget adalah jatah evaluasi yang ditentukan kode dari jumlah baris peta aktual, bukan pilihan ukuran di menu:

| Ukuran test case | Grid | Jumlah FS | Budget | Generasi jika population 100 |
|---|---|---:|---:|---:|
| Tiny | 10 × 10 | 3 | 10.000 | 100 |
| Small | 20 × 20 | 10 | 50.000 | 500 |
| Normal | 40 × 40 | 24 | 200.000 | 2.000 |
| Large | 80 × 80 | 40 | 500.000 | 5.000 |

Jadi, contoh Tiny di atas memakai batas **100 generasi**, meskipun baris kedua berisi `2000`. Kode mengelompokkan jumlah baris `<= 10`, `<= 20`, `<= 40`, dan selebihnya ke empat budget tersebut. GA dapat berhenti lebih awal karena tidak ada perbaikan selama 30% dari batas generasi atau karena batas waktu 5 menit per run, diperiksa setiap pergantian generasi.

## 3. Kenali pengaturan model di `main.java`

Untuk percobaan pertama, gunakan pengaturan yang sudah ada. Bagian berikut menentukan metode dan pengulangannya; baris-baris ini berada di beberapa bagian `main.java`:

```java
int npm_ketua = 24019;
int[] seed = new int[10];
String[] daftarKromosom = { "bit", "int" };
boolean[] pilihan = { false, true };
```

**Model** adalah satu kombinasi representasi solusi dan metode GA. `bit` dan `int` adalah dua cara menyimpan posisi FS. Arti pilihan `false` dan `true` bergantung pada variabel loop:

| Variabel | `false` | `true` |
|---|---|---|
| `pakaiTournament` (pemilihan induk) | Roulette Wheel | Tournament, dengan 3 kandidat |
| `pakaiTwoPoint` (penggabungan induk) | Single Point crossover | Two Point crossover |
| `pakaiMutasiJarak` (perubahan anak) | Random Move | By Distance |

**Seed** mengatur angka acak agar percobaan dapat diulang. `npm_ketua` membangkitkan kumpulan seed; `new int[10]` berarti 10 pengulangan untuk setiap model, dengan kumpulan seed yang sama antarmodel.

Dengan pengaturan saat ini, satu kali menjalankan program menguji **16 model × 10 pengulangan = 160 run**, pada satu peta dan satu konfigurasi parameter. Jadi, program belum selesai ketika baru mencetak satu hasil.

### Tahap 1: jalankan seluruh kombinasi

Tahap 1 membandingkan seluruh **16 kombinasi**, sedangkan Tahap 2 menggunakan **minimal 3 model terbaik hasil Tahap 1**. Pemilihan model terbaik dilakukan dari analisis hasil CSV, bukan otomatis oleh program.

Untuk Tahap 1, pertahankan `daftarKromosom = { "bit", "int" }` dan `pilihan = { false, true }`, serta jalankan tanpa filter model. Loop akan mengganti kombinasi operator secara otomatis melalui pemanggilan:

```java
Individual res = ga.run(
        pakaiTournament,
        pakaiTwoPoint,
        pakaiMutasiJarak);
```

Gunakan **30 seed per model dan peta untuk Tahap 1 dan Tahap 2** dengan mengubah `new int[10]` menjadi `new int[30]`. Pengujian akhir menggunakan model terbaik dari Tahap 2 pada peta Large dengan minimal 10 seed per peta.

Tahap 1 menggunakan ukuran Tiny, Small, dan Normal, masing-masing dengan 4 peta: 2 acak, 1 klaster, dan 1 labirin. Parameter tetapnya adalah population `100`, crossover `0.8`, mutation `0.05`, dan elitism `0.2`, dengan generasi dihitung dari budget/population.

### Tahap 2A dan 2B: pilih model dengan filter `modelId`

Untuk kedua bagian Tahap 2, gunakan **minimal 3 model terbaik dari hasil CSV Tahap 1**. Contoh berikut memilih `M03`, `M01`, dan `M04`; ID ini hanya contoh, bukan kesimpulan model terbaik.

Di `main.java`, cari bagian identitas model, lalu tambahkan filter **setelah `String modelId = ...` dan sebelum `String selectionName = ...`** Hal ini dilakukan karena program menentukan kombinasi secara otomatis jadi harus ada tambahan kode untuk memilih kombinasi terbaik yaitu:

```java
// Identitas model: tetap dihitung untuk setiap kombinasi.
modelNumber++;
String modelId = String.format("M%02d", modelNumber);

// Batasan uji coba model terpilih untuk Tahap 2A dan 2B.
// CONTOH: ganti ID ini dengan minimal 3 model terbaik dari CSV Tahap 1.
if (!modelId.equals("M03")
        && !modelId.equals("M01")
        && !modelId.equals("M04")) {
    continue;
}

String selectionName = pakaiTournament ? "tournament" : "roulette";
```

Kondisi tersebut berarti: jika model bukan M03, bukan M01, dan bukan M04, lewati kombinasi itu. Ketiga model yang dipilih tetap dijalankan dengan seluruh seed. Dengan 30 seed, satu kali menjalankan program menghasilkan **3 model × 30 seed = 90 run** untuk satu peta dan satu konfigurasi parameter.

Pertahankan array berikut beserta urutan loop agar ID model tetap menunjuk kombinasi yang sama dengan Tahap 1:

```java
String[] daftarKromosom = { "bit", "int" };
boolean[] pilihan = { false, true };
```

Pertahankan juga pemanggilan:

```java
Individual res = ga.run(
        pakaiTournament,
        pakaiTwoPoint,
        pakaiMutasiJarak);
```

Dengan begitu, operator yang dijalankan dan label CSV memakai nilai yang sama. Mengganti argumen `ga.run(...)` menjadi nilai tetap sementara loop tetap berjalan dapat membuat hasil salah label. Mengubah isi atau urutan array juga dapat mengubah pemetaan ID model.

Filter ini adalah tambahan manual; `main.java` saat ini belum memasangnya. Untuk berganti model pilihan, ubah ID pada filter, simpan, lalu compile ulang. Untuk kembali menjalankan Tahap 1, hapus/nonaktifkan blok `if` filter. Untuk pengujian akhir, gunakan filter serupa yang hanya meloloskan satu model terbaik dari Tahap 2.

**Memilih Tahap 2A atau 2B pada menu hanya mengatur label dan folder hasil.** Filter model, jumlah seed, peta di `input.txt`, dan parameter di `param.txt` tetap harus disiapkan sebelum run. Filter di atas berlaku untuk semua pilihan menu selama masih terpasang.

## 4. Compile dan jalankan program

Di terminal pada folder proyek, jalankan dua perintah berikut satu per satu:

```powershell
javac -encoding UTF-8 -cp . -d out *.java OutputTools/csv/CsvWriter.java
java -cp out main
```

Perintah pertama menerjemahkan kode Java menjadi file `.class` di folder `out`. Perintah kedua menjalankan program. Compile ulang jika mengubah file `.java`; perubahan `input.txt` atau `param.txt` cukup disimpan sebelum menjalankan ulang program.

Program akan menampilkan menu tahap eksperimen:

| Pilihan | Digunakan untuk |
|---|---|
| `1` | Tahap 1: membandingkan kombinasi model |
| `2` | Tahap 2: dilanjutkan pilihan `1` untuk 2A (uji satu parameter) atau `2` untuk 2B (kombinasi parameter) |
| `3` | CSV Integration Test: hanya untuk mencoba pencatatan CSV, bukan hasil eksperimen resmi |
| `4` | Tahap Pengujian akhir, dengan label ukuran Large |

**Untuk mengikuti contoh pertama, ketik `1`, lalu Enter.** Menu `3` juga tetap menjalankan GA sesuai konfigurasi yang ada.

Pada Tahap 2A, ada pilihan Population, Crossover, Mutation, atau Elitism. Khusus Population, tersedia pilihan label `50`, `100`, `250`, `500`, dan `1000`. Nilai yang diuji tetap harus diubah sendiri di `param.txt`.

## 5. Pilih ukuran dan jenis peta

Untuk contoh Tiny Acak, jawab dua pertanyaan berikut:

| Pertanyaan | Jawaban contoh | Pilihan lain |
|---|---|---|
| Ukuran peta | `1` = Tiny | `2` = Small, `3` = Normal |
| Jenis peta | `1` = Acak | `2` = Klaster, `3` = Labirin |

Pilihan ukuran muncul pada Tahap 1 dan 2. Tahap Pengujian (`4`) langsung menggunakan label Large; CSV Integration Test (`3`) menggunakan label Test tanpa pertanyaan ukuran atau jenis peta.

Cocokkan jawaban dengan peta yang sudah disalin ke `input.txt`. Pilihan ini menjadi keterangan hasil dan tidak mengganti isi peta.

## 6. Masukkan Map ID, lalu tunggu hasil

Map ID adalah nama untuk mengenali test case pada hasil. Untuk contoh pertama, masukkan **`n10_acak_01_pertama`**, lalu Enter. Akhiran `_pertama` membedakan hasil percobaan ini dari eksperimen berikutnya.

Gunakan huruf, angka, underscore (`_`), atau minus (`-`), tanpa `.txt`. Setelah ini, program mulai menghitung jarak pada peta dengan BFS, lalu menjalankan GA untuk seluruh model dan seed.

**Contoh berikut adalah urutan input dari awal program**, dimulai saat menu pertama muncul setelah menjalankan `java -cp out main`. Ini merangkum jawaban pada langkah 4–6; jika sudah mengisinya, tidak perlu memasukkan ulang.

Ketik setiap jawaban ketika pertanyaan yang sesuai muncul, lalu tekan Enter:

| Urutan sejak program dimulai | Pertanyaan pada layar | Jawaban yang diketik |
|---|---|---|
| 1 | Pilih tahap eksperimen | `1` → Tahap 1 |
| 2 | Pilih ukuran peta | `1` → Tiny |
| 3 | Pilih jenis peta | `1` → Acak |
| 4 | Masukkan ID peta | `n10_acak_01_pertama` |

Jadi, urutan input lengkap dari menu pertama adalah:

```text
1
1
1
n10_acak_01_pertama
```

Setelah jawaban keempat, program mulai menjalankan GA menggunakan `input.txt`, `param.txt`, dan konfigurasi `main.java` yang sudah disiapkan. Jawaban menu di atas hanya mengatur identitas dan folder hasil.

Hasilnya tersimpan di:

```text
Experiment_Results/HasilEksperimenTahap1/Tiny/n10_acak_01_pertama/results.csv
```

Lokasi juga ditampilkan pada bagian `Output`. Tunggu sampai muncul pesan **“Semua hasil eksperimen berhasil dicatat ke CSV.”**

## 7. Pilihan menu hanya menentukan identitas dan folder hasil

**Memasukkan angka saat program berjalan TIDAK otomatis mengubah parameter eksperimen.** Pilihan tahap, ukuran peta, jenis eksperimen, dan nilai Population pada menu hanya menentukan label pencatatan serta folder tempat CSV disimpan. Konfigurasi yang benar-benar dijalankan tetap harus disiapkan sebelum run:

| Ingin mengubah | Yang harus dilakukan |
|---|---|
| Population, crossover rate, mutation rate, atau elitism | Edit nilai pada `param.txt`, lalu simpan |
| Peta dan jumlah FS | Ganti isi `input.txt`, lalu simpan |
| Model/operator dan jumlah pengulangan | Edit `main.java`, lalu compile ulang |

**Contoh:** pada Tahap 2A → Population, memilih opsi `3` (label `250`) hanya membuat folder hasil memuat `Population/250`. Jika baris pertama `param.txt` masih `100`, GA tetap berjalan dengan **population 100**, meskipun nama foldernya `250`. Agar benar-benar menguji population 250, ubah baris pertama `param.txt` menjadi `250` sebelum menjalankan program.

Hal yang sama berlaku untuk peta: memilih label Normal tidak mengubah peta Tiny yang masih ada di `input.txt`. Batas generasi tetap dihitung otomatis dari budget peta aktual dan population sebagaimana dijelaskan pada langkah 2, bukan dari pilihan menu.

### Membaca hasil dan mengulang eksperimen

Buka `results.csv` dengan Excel atau aplikasi pengolah data. Satu baris berisi satu run: model, parameter aktual, seed, cost, fitness, waktu, alasan berhenti, dan posisi FS. **Cost lebih kecil berarti solusi lebih baik**; fitness dihitung sebagai `1 / (1 + cost)`. Koordinat FS berbentuk `(x,y)` = (kolom, baris), dimulai dari 1.

Untuk percobaan berikutnya, ganti peta di `input.txt`, ubah parameter di `param.txt` sesuai kebutuhan, lalu jalankan lagi. Jika mengubah model atau jumlah seed di `main.java`, compile ulang terlebih dahulu.

File `results.csv` yang sudah ada tidak ditimpa atau ditambahi. Gunakan Map ID berbeda, misalnya `n10_acak_01_kedua`, atau pindahkan hasil sebelumnya sebelum menjalankan ulang. Pada Tahap 2A Population, folder juga memuat nilai population pilihan menu; parameter lain tidak memiliki subfolder nilai uji, sehingga gunakan ID tambahan seperti `n10_acak_01_pc08` untuk membedakan hasil.

Jika `input.txt` atau `param.txt` tidak ditemukan, pastikan terminal berada di folder proyek. Jika CSV terbuka dalam satu kolom di Excel, impor lewat **Data → From Text/CSV** dan pilih pemisah koma.
