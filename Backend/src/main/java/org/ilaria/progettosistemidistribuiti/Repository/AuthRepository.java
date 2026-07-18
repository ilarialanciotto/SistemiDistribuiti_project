package org.ilaria.progettosistemidistribuiti.Repository;


import org.ilaria.progettosistemidistribuiti.Model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface AuthRepository extends JpaRepository<User, Long> {

    @Query("SELECT U FROM User U WHERE U.email=:email")
    User findByEmail(@Param("email") String email);

}
