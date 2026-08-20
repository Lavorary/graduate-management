package hei.school.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import hei.school.app.file.bucket.BucketComponent;
import hei.school.app.mail.Mailer;
import hei.school.app.mapper.UserMapper;
import hei.school.app.model.User;
import hei.school.app.repository.CursusRepository;
import hei.school.app.repository.GradeRepository;
import hei.school.app.repository.UserRepository;
import hei.school.app.repository.model.JCursus;
import hei.school.app.repository.model.JUser;
import hei.school.app.security.model.UserRole;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

  @Mock private CursusRepository cursusRepository;
  @Mock private UserRepository userRepository;
  @Mock private GradeRepository gradeRepository;
  @Mock private UserMapper userMapper;
  @Mock private BucketComponent bucketComponent;
  @Mock private Mailer mailer;

  @InjectMocks private ExportService exportService;

  @Test
  void should_generate_excel_bytes() throws Exception {
    // Given
    UUID promotionId = UUID.randomUUID();
    JCursus cursus = new JCursus();
    cursus.setId(promotionId);
    cursus.setName("DevLog 2026");

    JUser jUser = new JUser();
    jUser.setId(UUID.randomUUID());
    jUser.setFirstName("John");
    jUser.setLastName("Doe");
    jUser.setEmail("john@test.com");

    User user = new User(jUser.getId(), "John", "Doe", UserRole.ADMIN, "john@test.com", null);

    when(cursusRepository.findById(promotionId)).thenReturn(Optional.of(cursus));
    when(userRepository.findGraduatesByCursusId(promotionId)).thenReturn(List.of(jUser));
    when(userMapper.toModel(jUser)).thenReturn(user);
    when(gradeRepository.findAverageScoreByStudentId(jUser.getId())).thenReturn(15.5);

    // When
    byte[] excelData = exportService.generateExcel(promotionId);

    // Then
    assertThat(excelData).isNotNull();
    assertThat(excelData.length).isGreaterThan(0);

    // Verify Excel content
    try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
      Sheet sheet = workbook.getSheetAt(0);
      assertThat(sheet.getPhysicalNumberOfRows()).isEqualTo(2); // Header + 1 student

      Row headerRow = sheet.getRow(0);
      assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("ID");
      assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("First Name");

      Row dataRow = sheet.getRow(1);
      assertThat(dataRow.getCell(1).getStringCellValue()).isEqualTo("John");
      assertThat(dataRow.getCell(4).getNumericCellValue()).isEqualTo(15.5);
      assertThat(dataRow.getCell(5).getStringCellValue()).isEqualTo("GRADUATED");
    }
  }

  @Test
  void should_throw_when_promotion_not_found() {
    // Given
    UUID promotionId = UUID.randomUUID();
    when(cursusRepository.findById(promotionId)).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> exportService.generateExcel(promotionId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Promotion not found: " + promotionId);
  }

  @Test
  void should_throw_when_no_graduates_found() {
    // Given
    UUID promotionId = UUID.randomUUID();
    JCursus cursus = new JCursus();
    cursus.setId(promotionId);
    cursus.setName("DevLog 2026");

    when(cursusRepository.findById(promotionId)).thenReturn(Optional.of(cursus));
    when(userRepository.findGraduatesByCursusId(promotionId)).thenReturn(List.of());

    // Then
    assertThatThrownBy(() -> exportService.generateExcel(promotionId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No graduates found for promotion: DevLog 2026");
  }

  @Test
  void should_handle_student_with_no_grades() throws Exception {
    // Given
    UUID promotionId = UUID.randomUUID();
    JCursus cursus = new JCursus();
    cursus.setId(promotionId);
    cursus.setName("DevLog 2026");

    JUser jUser = new JUser();
    jUser.setId(UUID.randomUUID());
    jUser.setFirstName("Jane");
    jUser.setLastName("Smith");
    jUser.setEmail("jane@test.com");

    User user = new User(jUser.getId(), "Jane", "Smith", UserRole.ADMIN, "jane@test.com", null);

    when(cursusRepository.findById(promotionId)).thenReturn(Optional.of(cursus));
    when(userRepository.findGraduatesByCursusId(promotionId)).thenReturn(List.of(jUser));
    when(userMapper.toModel(jUser)).thenReturn(user);
    when(gradeRepository.findAverageScoreByStudentId(jUser.getId())).thenReturn(null); // No grades

    // When
    byte[] excelData = exportService.generateExcel(promotionId);

    // Then
    assertThat(excelData).isNotNull();

    try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
      Sheet sheet = workbook.getSheetAt(0);
      Row dataRow = sheet.getRow(1);
      assertThat(dataRow.getCell(4).getNumericCellValue()).isEqualTo(0.0);
      assertThat(dataRow.getCell(5).getStringCellValue()).isEqualTo("NOT GRADUATED");
    }
  }
}
