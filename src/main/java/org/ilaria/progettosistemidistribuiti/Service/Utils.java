package org.ilaria.progettosistemidistribuiti.Service;

import lombok.AllArgsConstructor;

import org.ilaria.progettosistemidistribuiti.Model.Entity.User;
import org.ilaria.progettosistemidistribuiti.Repository.AuthRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class Utils {

    private final AuthRepository authRepository;

    public User findUserRegister(String email) {
        return authRepository.findByEmail(email);
    }


}
