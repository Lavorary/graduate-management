package hei.school.app.service;

import hei.school.app.DTOs.CursusDTO;
import hei.school.app.mapper.CursusMapper;
import hei.school.app.model.Cursus;
import hei.school.app.repository.CursusRepository;
import hei.school.app.repository.model.JCursus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CursusService {
    private final CursusRepository cursusRepository;
    private final CursusMapper cursusMapper;

    public CursusDTO create(String name, String description, String year) {
        JCursus saved =
                cursusRepository.save(
                        JCursus.builder().name(name).description(description).year(year).build());
        return toDto(saved);
    }

    public CursusDTO getById(UUID id) {
        JCursus entity =
                cursusRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Cursus with Id : " + id + " not found"));
        return toDto(entity);
    }

    public List<CursusDTO> findByYear(String year) {
        return cursusRepository.findByYear(year).stream().map(this::toDto).toList();
    }

    public List<CursusDTO> findAll() {
        return cursusRepository.findAll().stream().map(this::toDto).toList();
    }

    private CursusDTO toDto(JCursus entity) {
        Cursus model = cursusMapper.toModel(entity);
        return CursusDTO.builder()
                .id(model.id())
                .name(model.name())
                .description(model.description())
                .year(entity.getYear())
                .build();
    }
}
