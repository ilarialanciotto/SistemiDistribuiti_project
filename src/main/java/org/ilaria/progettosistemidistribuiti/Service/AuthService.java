package org.ilaria.progettosistemidistribuiti.Service;

import lombok.RequiredArgsConstructor;

import org.ilaria.progettosistemidistribuiti.Model.DTO.UserDTO;
import org.ilaria.progettosistemidistribuiti.Model.Entity.User;
import org.ilaria.progettosistemidistribuiti.Repository.AuthRepository;
import org.ilaria.progettosistemidistribuiti.Security.JwtResponse;
import org.ilaria.progettosistemidistribuiti.Security.JwtUtils;
import org.ilaria.progettosistemidistribuiti.Service.Mapper.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;



@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final Utils utils;

    public void register(UserDTO dto) {
        User user = userMapper.toEntity(dto);
        if (utils.findUserRegister(dto.getEmail())!=null) { throw new RuntimeException("Email already registered"); }

        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if(!StringUtils.hasText(dto.getPassword()) || !dto.getEmail().matches(regex))
            throw new RuntimeException("Password or email format incorrect");
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        authRepository.save(user);
    }

    public ResponseEntity<?> login(UserDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userPrincipal);
        String role = userPrincipal.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("NO_ROLE");
        return ResponseEntity.ok(new JwtResponse(jwt,userPrincipal.getUsername(), role));
    }

}

