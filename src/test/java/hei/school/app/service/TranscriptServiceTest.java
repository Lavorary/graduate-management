package hei.school.app.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.app.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TranscriptServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private TranscriptService transcriptService;

    @Test
    void should_pass_when_student_exists() {
        // Given
        UUID studentId = UUID.randomUUID();
        when(userRepository.existsById(studentId)).thenReturn(true);

        // When
        transcriptService.requestTranscript(studentId);

        // Then
        verify(userRepository).existsById(studentId);
    }

    @Test
    void should_throw_when_student_not_found() {
        // Given
        UUID studentId = UUID.randomUUID();
        when(userRepository.existsById(studentId)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> transcriptService.requestTranscript(studentId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Student with Id : " + studentId + " not found");
    }
}