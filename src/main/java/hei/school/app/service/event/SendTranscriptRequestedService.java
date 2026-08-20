package hei.school.app.service.event;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import hei.school.app.endpoint.event.model.SendTranscriptRequested;
import hei.school.app.file.bucket.BucketComponent;
import hei.school.app.mail.Email;
import hei.school.app.mail.Mailer;
import hei.school.app.mapper.GradeMapper;
import hei.school.app.mapper.ScoreHistoryMapper;
import hei.school.app.mapper.UserMapper;
import hei.school.app.model.Grade;
import hei.school.app.model.ScoreHistory;
import hei.school.app.model.User;
import hei.school.app.repository.GradeRepository;
import hei.school.app.repository.ScoreHistoryRepository;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JUser;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SendTranscriptRequestedService implements Consumer<SendTranscriptRequested> {
    private final GradeRepository gradeRepository;
    private final ScoreHistoryRepository scoreHistoryRepository;
    private final UserRepository userRepository;
    private final GradeMapper gradeMapper;
    private final ScoreHistoryMapper scoreHistoryMapper;
    private final UserMapper userMapper;
    private final BucketComponent bucketComponent;
    private final Mailer mailer;

    @SneakyThrows
    @Override
    public void accept(SendTranscriptRequested sendTranscriptRequested) {
        UUID studentId = sendTranscriptRequested.getStudentId();


        JUser studentEntity = userRepository
            .findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student with Id : " + studentId + " not found"));

        User student = userMapper.toModel(studentEntity);


        List<Grade> grades = gradeMapper.toModel(gradeRepository.findByStudent_Id(studentId));


        File pdfFile = buildPdfFile(student, grades);

        try {
            String bucketKey = "transcripts/" + studentId + "/" + UUID.randomUUID() + ".pdf";
            bucketComponent.upload(pdfFile, bucketKey);

            URL url = bucketComponent.presign(bucketKey, Duration.ofDays(7));

            sendEmail(student.email(), url);
        } finally {
            pdfFile.delete();
        }
    }

    private File buildPdfFile(User student, List<Grade> grades) {
        try {
            File file = File.createTempFile("transcript-" + student.id(), ".pdf");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                Document document = new Document();
                PdfWriter.getInstance(document, fos);
                document.open();

                document.add(new Paragraph("Transcript - " + student.firstName() + " " + student.lastName()));
                document.add(new Paragraph(" "));

                PdfPTable table = new PdfPTable(3);
                table.addCell("Course");
                table.addCell("Exam date");
                table.addCell("Score");

                for (Grade grade : grades) {
                    List<ScoreHistory> history = scoreHistoryMapper.toModel(
                        scoreHistoryRepository.findByGradeIdOrderByGradedAtAsc(grade.id()));
                    BigDecimal latestScore = history.isEmpty() ? null : history.getLast().score();

                    table.addCell(grade.exam().course().title());
                    table.addCell(grade.exam().examDate().toString());
                    table.addCell(latestScore == null ? "N/A" : latestScore.toString());
                }

                document.add(table);
                document.close();
            }
            return file;
        } catch (IOException | DocumentException e) {
            throw new RuntimeException("Failed to generate transcript PDF", e);
        }
    }

    private void sendEmail(String studentEmail, URL s3Url) {
        InternetAddress to;
        try {
            to = new InternetAddress(studentEmail);
        } catch (AddressException e) {
            throw new RuntimeException("Invalid student email: " + studentEmail, e);
        }

        Email email = new Email(
            to,
            List.of(),
            List.of(),
            "Your transcript is ready",
            "<p>Your transcript is ready. Download it here (link valid 7 days):</p>"
                + "<p><a href=\"" + s3Url + "\">" + s3Url + "</a></p>",
            List.of()
        );

        mailer.accept(email);
    }
}