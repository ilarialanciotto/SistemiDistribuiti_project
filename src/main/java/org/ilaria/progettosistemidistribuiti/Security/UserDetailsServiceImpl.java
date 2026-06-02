package org.ilaria.progettosistemidistribuiti.Security;

import lombok.AllArgsConstructor;

import org.ilaria.progettosistemidistribuiti.Model.Entity.User;
import org.ilaria.progettosistemidistribuiti.Repository.AuthRepository;
import org.ilaria.progettosistemidistribuiti.Model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = authRepository.findByEmail(username);
        if(user==null) throw new UsernameNotFoundException("user not found by email: " + username);
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
