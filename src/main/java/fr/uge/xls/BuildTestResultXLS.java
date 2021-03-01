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
        Sheet sheet = workbook.createSheet(testResults.get(0).getTest_file() + "Result");
        testResults.sort(Comparator.comparing(TestResult::getQuestionTagNumber).thenComparing(TestResult::getTest));

        Row testNameRow = sheet.createRow(0);
        LinkedList<String> tests = writeTestsRow(testResults, testNameRow);
        writeStudentResults(testResults, sheet, tests);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } finally {
            workbook.close();
        }
    }

    private LinkedList<String> writeTestsRow(List<TestResult> testResults, Row row) {
        LinkedList<String> tests = new LinkedList<>();
        int cellCount = 1;
        for (TestResult testResult : testResults) {
            if (!tests.contains(testResult.toString())) {
                tests.add(testResult.toString());
                row.createCell(cellCount).setCellValue(testResult.toString());
                cellCount++;
            }
        }
        return tests;
    }

    private void writeStudentResults(List<TestResult> testResults, Sheet sheet, LinkedList<String> tests) {
        int rowCount = 1;
        Map<String, Row> students = new HashMap<>();
        for (TestResult testResult : testResults) {
            String student = testResult.getStudent();
            if (!students.containsKey(student)) {
                Row row = sheet.createRow(rowCount);
                row.createCell(0).setCellValue(student);
                row.createCell(tests.indexOf(testResult.toString()) + 1).setCellValue(testResult.getResult());
                students.put(student, row);
                rowCount++;
            } else {
                students.get(student)
                        .createCell(tests.indexOf(testResult.toString()) + 1)
                        .setCellValue(testResult.getResult());
            }
        }
    }
}
