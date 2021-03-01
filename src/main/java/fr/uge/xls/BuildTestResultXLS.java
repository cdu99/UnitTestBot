package fr.uge.xls;

import fr.uge.database.TestResult;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class BuildTestResultXLS {
    public byte[] build(List<TestResult> testResults) throws IOException {
        Objects.requireNonNull(testResults);

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        testResults.sort(Comparator.comparing(TestResult::getQuestionTagNumber).thenComparing(TestResult::getTest));

        List<String> tests = new LinkedList<>();
        Row testNameRow = sheet.createRow(0);
        int testNameCellCount = 1;
        for (TestResult testResult : testResults) {
            if (!tests.contains(testResult.toString())) {
                tests.add(testResult.toString());
                testNameRow.createCell(testNameCellCount).setCellValue(testResult.toString());
                testNameCellCount++;
            }
        }

        int studentRowCount = 1;
        Map<String, Row> students = new HashMap<>();
        for (TestResult testResult : testResults) {
            if (!students.containsKey(testResult.getStudent())) {
                Row row = sheet.createRow(studentRowCount);
                row.createCell(0).setCellValue(testResult.getStudent());
                int cellIndexNumber = tests.indexOf(testResult.toString()) + 1;
                row.createCell(cellIndexNumber).setCellValue(testResult.getResult());
                students.put(testResult.getStudent(), row);
                studentRowCount++;
            } else {
                int cellIndexNumber = tests.indexOf(testResult.toString()) + 1;
                students.get(testResult.getStudent()).createCell(cellIndexNumber).setCellValue(testResult.getResult());
            }
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } finally {
            workbook.close();
        }
    }
}
