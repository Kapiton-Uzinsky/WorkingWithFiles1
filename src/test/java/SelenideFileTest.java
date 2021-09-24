import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.io.Zip.unzip;

public class SelenideFileTest {

    @Test
    void downloadFileTest() throws Exception {
        open("https://github.com/selenide/selenide/blob/master/README.md");
        File download = $("#raw-url").download();
        String result;
        try (InputStream is = new FileInputStream(download)) {
            result = new String(is.readAllBytes(), "UTF-8");
        }
        assertThat(result).contains("Selenide = UI Testing Framework powered by Selenium WebDriver");
    }

    @Test
    void uploadFilesTest() {
        open("https://the-internet.herokuapp.com/upload");
        $("input[type='file']").uploadFromClasspath("123.txt");
        $("#file-submit").click();
        $("#uploaded-files").shouldHave(text("123.txt"));
    }

    @Test
    void downloadPDFTest() throws Exception {
        open("https://junit.org/junit5/docs/current/user-guide/");
        File download = $(byText("PDF download")).download();
        PDF parsed = new PDF(download);
        assertThat(parsed.author).contains("Marc Philipp");
    }

    @Test
    void downloadExcelTest() throws Exception {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("175teachers.xlsx")) {
            XLS parsed = new XLS(stream);
            assertThat(parsed.excel.getSheetAt(1).getRow(9).getCell(1).getStringCellValue())
                    .isEqualTo("Березкина Ольга Дмитриевна");
        }
    }

    @Test
    void parseCsvTest() throws Exception {
        URL url = getClass().getClassLoader().getResource("testFile.csv");
        CSVReader reader = new CSVReader(new FileReader(new File(url.toURI())));

        List<String[]> strings = reader.readAll();

        assertThat(strings).contains(
                new String[]{"composers", "composition"},
                new String[]{"Mozart", "TheMarriageOfFigaro"},
                new String[]{"Beethoven", "SymphonyNo5"}
        );
    }

    @Test
    void zipFileWithPasswordTest() throws IOException, ZipException {
        String zipFilePath = "src/test/resources/123.zip";
        String unzipFolderPath = "src/test/resources/unzip";
        String zipPassword = "123";
        String unzipTxtFilePath = "src/test/resources/unzip/123.txt";
        String expectedData = "Hi! I'm test file!)";

        ZipFile zipFile = new ZipFile(zipFilePath);
        if (zipFile.isEncrypted()) {
            zipFile.setPassword(zipPassword);
        }
        zipFile.extractAll(unzipFolderPath);
    }


}

