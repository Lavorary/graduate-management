package hei.school.app.security;

import hei.school.app.mapper.UserMapper;
import hei.school.app.repository.UserRepository;
import hei.school.app.security.model.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public Principal loadUserByUsername(String email) {
    var jUser =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("No user with email: " + email));
    return new Principal(userMapper.toModel(jUser));
  }
}
