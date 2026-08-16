package hei.school.app.conf;

import hei.school.app.mapper.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperTestConfig {
  @Bean
  public UserMapper userMapper() {
    return new UserMapperImpl();
  }

  @Bean
  public CursusMapper cursusMapper() {
    return new CursusMapperImpl();
  }

  @Bean
  public GroupMapper groupMapper() {
    return new GroupMapperImpl();
  }

  @Bean
  public CourseMapper courseMapper() {
    return new CourseMapperImpl();
  }

  @Bean
  public ExamMapper examMapper() {
    return new ExamMapperImpl();
  }

  @Bean
  public GradeMapper gradeMapper() {
    return new GradeMapperImpl();
  }

  @Bean
  public ScoreHistoryMapper scoreHistoryMapper() {
    return new ScoreHistoryMapperImpl();
  }

  @Bean
  public GroupMembershipMapper groupMembershipMapper() {
    return new GroupMembershipMapperImpl();
  }
}
