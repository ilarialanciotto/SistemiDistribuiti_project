package org.ilaria.progettosistemidistribuiti.Repository;


import jakarta.transaction.Transactional;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Note;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Note N SET N.content = :newContent WHERE N.id = :id")
    void updateContent(@Param("id")long id, @Param("newContent")String newContent);

    @Modifying
    @Transactional
    @Query("DELETE FROM Note N WHERE N.id =:id")
    void deleteNote(@Param("id")long id);

}
