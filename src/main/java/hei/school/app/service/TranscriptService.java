package hei.school.app.service;

import hei.school.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TranscriptService {
    private final UserRepository userRepository;

    public void requestTranscript(UUID studentId) {
        if (!userRepository.existsById(studentId)) {
            throw new IllegalArgumentException("Student with Id : " + studentId + " not found");
        }
    }
}