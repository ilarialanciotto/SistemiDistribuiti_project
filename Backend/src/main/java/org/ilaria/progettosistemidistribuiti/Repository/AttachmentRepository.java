package org.ilaria.progettosistemidistribuiti.Repository;

import org.ilaria.progettosistemidistribuiti.Model.Entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
