package org.ilaria.progettosistemidistribuiti.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttachmentDTO {
    private String name;
    private String content;
    private String format;
}
