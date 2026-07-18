package org.ilaria.progettosistemidistribuiti.Repository;


import jakarta.transaction.Transactional;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {


    @Modifying
    @Transactional
    @Query("UPDATE Ticket T SET T.state = :newState WHERE T.id = :id")
    void updateState(@Param("id")long id, @Param("newState")String newState);

    @Query("SELECT T FROM Ticket T WHERE T.id=:id")
    Ticket findById(@Param("id")long id);

    long countByStateIgnoreCase(String chiuso);

    @Query("SELECT COUNT(T) FROM Ticket T WHERE T.priority_level_AI >= :priority AND LOWER(T.state) != LOWER(:state)")
    long countCriticalUnresolved(@Param("priority") int priority, @Param("state") String state);

    @Query("SELECT T.category_AI, COUNT(T) FROM Ticket T WHERE T.priority_level_AI >= 4 GROUP BY T.category_AI")
    List<Object[]> countHighPriorityByCategory();

}
