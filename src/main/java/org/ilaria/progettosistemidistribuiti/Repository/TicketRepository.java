package org.ilaria.progettosistemidistribuiti.Repository;


import jakarta.transaction.Transactional;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT T FROM Ticket T WHERE T.id=:id")
    Ticket findById(@Param("id")long id);

    @Modifying
    @Transactional
    @Query("UPDATE Ticket T SET T.category_AI = :categoryAI, T.keyword_AI = :keywordAI, " +
            "T.priority_level_AI = :priorityAI, T.state = :state WHERE T.id = :id")
    void update(@Param("id") long id,
                @Param("categoryAI") String categoryAI,
                @Param("priorityAI") Integer priorityAI,
                @Param("keywordAI") String keywordAI,
                @Param("state") String state);

    @Query("SELECT T FROM Ticket T  WHERE T.problem_title=:problemTitle")
    Ticket getTicketByProblem_title(@Param("problemTitle") String problemTitle);

    @Modifying
    @Transactional
    @Query("UPDATE Ticket T SET T.state = :newState WHERE T.id = :id")
    void updateState(@Param("id")long id, @Param("newState")String newState);

    @Modifying
    @Transactional
    @Query("DELETE FROM Ticket T WHERE T.id =:id")
    void deleteTicket(@Param("id")long id);
}
