package OutputTools.csv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CsvWriter implements AutoCloseable {

    private final BufferedWriter writer;
    private final int columnCount;

    // Membuat file CSV baru beserta header.
    public CsvWriter(Path filePath, String... headers) throws IOException {

        if (headers == null || headers.length == 0) {
            throw new IllegalArgumentException(
                    "CSV harus memiliki minimal satu kolom.");
        }

        // Membuat folder induk jika belum tersedia.
        Path parent = filePath.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        columnCount = headers.length;

        // Membuat file baru tanpa menimpa file yang sudah ada.
        try {
            writer = Files.newBufferedWriter(
                    filePath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE);

        } catch (FileAlreadyExistsException e) {
            throw new IOException(
                    "File CSV sudah ada: " + filePath
                            + ". Gunakan nama atau lokasi file baru.",
                    e);
        }

        // Menulis header.
        try {
            writer.write(toCsvLine((Object[]) headers));
            writer.newLine();
            writer.flush();

        } catch (IOException e) {
            writer.close();
            throw e;
        }
    }

    // Menambahkan satu baris data ke CSV.
    public synchronized void writeRow(Object... values)
            throws IOException {

        if (values == null || values.length != columnCount) {
            throw new IllegalArgumentException(
                    "Jumlah data tidak sesuai dengan jumlah kolom CSV.");
        }
        writer.write(toCsvLine(values));
        writer.newLine();

        // Menyimpan hasil setiap baris.
        writer.flush();
    }

    // Mengubah data menjadi format CSV.
    private String toCsvLine(Object... values) {

        StringBuilder line = new StringBuilder();

        for (int i = 0; i < values.length; i++) {

            if (i > 0) {
                line.append(",");
            }

            String value = values[i] == null ? "" : String.valueOf(values[i]);

            // Mengamankan koma, tanda kutip, dan baris baru.
            if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
                value = value.replace("\"", "\"\"");
                line.append("\"").append(value).append("\"");
            } else {
                line.append(value);
            }
        }
        return line.toString();
    }

    // Menutup file CSV.
    @Override
    public void close() throws IOException {
        writer.close();
    }
}
