package fr.uge.xls;

import fr.uge.database.TestResult;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class BuildTestResultXLS {
    // WIP
    public byte[] build(List<TestResult> testResults) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        int rowCount = 0;

        for (TestResult test : testResults) {
            Row row = sheet.createRow(++rowCount);
            writeXLS(test, row);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } finally {
            workbook.close();
        }
    }

    private void writeXLS(TestResult test, Row row) {
        Cell cell = row.createCell(1);
        cell.setCellValue(test.getTest());

        cell = row.createCell(2);
        cell.setCellValue(test.getStudent());

        cell = row.createCell(3);
        cell.setCellValue(test.getResult());
    }
}
