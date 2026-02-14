package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import models.Client;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.InputStreamReader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class WorkingWithFiles {

    private ClassLoader cl = WorkingWithFiles.class.getClassLoader();

    @DisplayName ("Чтение и проверка xlsx внутри zip")
    @Test
    void xlsxFileParsingTest() throws Exception {
        try (ZipInputStream inputStream = new ZipInputStream(
                cl.getResourceAsStream("sample.zip")
        )) {
            ZipEntry entry;
            String i = "primer.xlsx";
            boolean file = false;
            while ((entry = inputStream.getNextEntry()) != null) {
                if (entry.getName().equals(i)) {
                    file = true;
                    XLS xls = new XLS(inputStream);
                    String actualValue = xls.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
                    assertTrue(actualValue.contains("Privet"));
                }
            }
            assertTrue(file, i + " отсутствует в архиве");
        }
    }

    @DisplayName ("Чтение и проверка pdf внутри zip")
    @Test
    void pdfFileInZipParsingTest() throws Exception {
        try (ZipInputStream inputStream = new ZipInputStream(
                cl.getResourceAsStream("sample.zip")
        )) {
            ZipEntry entry;
            String i = "665858e879050.pdf";
            boolean file = false;
            while ((entry = inputStream.getNextEntry()) != null) {
                if (entry.getName().equals(i)) {
                    file = true;
                    PDF pdf = new PDF(inputStream);
                    assertEquals(12,pdf.numberOfPages);
                }
            }
            assertTrue(file, i + " отсутствует в архиве");
        }
    }

    @DisplayName ("Чтение и проверка csv внутри zip")
    @Test
    void csvFileInZipParsingTest() throws Exception {
        try (ZipInputStream inputStream = new ZipInputStream(
                cl.getResourceAsStream("sample.zip")
        )) {
            ZipEntry entry;
            String i = "FullCountry.csv";
            boolean file = false;
            while ((entry = inputStream.getNextEntry()) != null) {
                if (entry.getName().endsWith(i)) {
                    file = true;
                    CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));

                    List<String[]> data = csvReader.readAll();
                    assertEquals(2, data.size());
                    Assertions.assertArrayEquals(
                            new String[] {"Russia", "Moscow"},
                            data.get(0)
                    );
                    Assertions.assertArrayEquals(
                            new String[] {"USA", "Chicago"},
                            data.get(1)
                    );
                }
            }
            assertTrue(file, i + " отсутствует в архиве");
        }
    }

    @DisplayName ("Разбор json файла")
    @Test
    public void testReadClientJson() throws Exception {
       ObjectMapper objectMapper = new ObjectMapper();
       InputStream inputStream = getClass().getResourceAsStream("/client.json");

       Client client = objectMapper.readValue(inputStream, Client.class);

       assertEquals(1, client.getId());
       assertEquals("Timur", client.getName());
       assertEquals("fdhgdzfgzfd@example.ru", client.getEmail());
   }
}
