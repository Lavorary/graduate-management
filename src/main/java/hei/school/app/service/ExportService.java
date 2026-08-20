package hei.school.app.service;

import hei.school.app.file.bucket.BucketComponent;
import hei.school.app.mail.Email;
import hei.school.app.mail.Mailer;
import hei.school.app.mapper.UserMapper;
import hei.school.app.model.User;
import hei.school.app.repository.CursusRepository;
import hei.school.app.repository.GradeRepository;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JUser;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final CursusRepository cursusRepository;
    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private final UserMapper userMapper;
    private final BucketComponent bucketComponent;
    private final Mailer mailer;


    public void generateAndSendExcel(UUID promotionId, String requesterEmail) {
        JCursus cursus = cursusRepository.findById(promotionId)
            .orElseThrow(() -> new IllegalArgumentException("Promotion not found: " + promotionId));

        List<JUser> graduates = userRepository.findGraduatesByCursusId(promotionId);
        if (graduates.isEmpty()) {
            throw new IllegalArgumentException("No graduates found for promotion: " + cursus.getName());
        }

        File excelFile = generateExcelFile(cursus.getName(), graduates);

        try {
            String bucketKey = "exports/graduates/" + promotionId + "/" + UUID.randomUUID() + ".xlsx";
            bucketComponent.upload(excelFile, bucketKey);

            URL url = bucketComponent.presign(bucketKey, Duration.ofDays(7));

            sendEmail(requesterEmail, url, cursus.getName());
        } finally {
            if (excelFile.exists()) {
                excelFile.delete();
            }
        }
    }

    public byte[] generateExcel(UUID promotionId) {
        JCursus cursus = cursusRepository.findById(promotionId)
            .orElseThrow(() -> new IllegalArgumentException("Promotion not found: " + promotionId));

        List<JUser> graduates = userRepository.findGraduatesByCursusId(promotionId);
        if (graduates.isEmpty()) {
            throw new IllegalArgumentException("No graduates found for promotion: " + cursus.getName());
        }

        File excelFile = generateExcelFile(cursus.getName(), graduates);

        try {
            byte[] bytes = java.nio.file.Files.readAllBytes(excelFile.toPath());
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file", e);
        } finally {
            if (excelFile.exists()) {
                excelFile.delete();
            }
        }
    }

    private File generateExcelFile(String promotionName, List<JUser> graduates) {
        try {
            File file = File.createTempFile("graduates-" + promotionName.replaceAll("\\s+", "_"), ".xlsx");

            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream fos = new FileOutputStream(file)) {

                Sheet sheet = workbook.createSheet("Graduates");

                Row header = sheet.createRow(0);
                String[] columns = {"ID", "First Name", "Last Name", "Email", "Average Score", "Status"};
                for (int i = 0; i < columns.length; i++) {
                    header.createCell(i).setCellValue(columns[i]);
                }

                int rowNum = 1;
                for (JUser jUser : graduates) {
                    User user = userMapper.toModel(jUser);
                    Row row = sheet.createRow(rowNum++);

                    row.createCell(0).setCellValue(user.id().toString());
                    row.createCell(1).setCellValue(user.firstName());
                    row.createCell(2).setCellValue(user.lastName());
                    row.createCell(3).setCellValue(user.email());

                    Double average = gradeRepository.findAverageScoreByStudentId(user.id());
                    row.createCell(4).setCellValue(average != null ? average : 0.0);

                    String status = (average != null && average >= 10) ? "GRADUATED" : "NOT GRADUATED";
                    row.createCell(5).setCellValue(status);
                }

                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(fos);
            }

            return file;

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file", e);
        }
    }

    private void sendEmail(String toEmail, URL s3Url, String promotionName) {
        try {
            Email email = new Email(
                new InternetAddress(toEmail),
                List.of(),
                List.of(),
                "Graduates Export Ready - " + promotionName,
                "<p>Your graduates export for <strong>" + promotionName + "</strong> is ready.</p>" +
                "<p>Download: <a href=\"" + s3Url + "\">" + s3Url + "</a></p>",
                List.of()
            );
            mailer.accept(email);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}