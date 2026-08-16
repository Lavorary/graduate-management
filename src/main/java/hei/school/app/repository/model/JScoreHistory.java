package hei.school.app.repository.model;

import hei.school.app.enums.Reason;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "score_history")
@Builder
public class JScoreHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "grade_id")
  private JGrade grade;

  @Column(name = "score", nullable = false, precision = 10, scale = 2)
  private BigDecimal score;

  @Column(name = "graded_at", nullable = false)
  @CreationTimestamp
  private Instant gradedAt;

  @Column(name = "reason")
  @Enumerated(EnumType.STRING)
  private Reason reason;

  @Column(name = "explanation", columnDefinition = "TEXT", nullable = false)
  private String explanation;
}
