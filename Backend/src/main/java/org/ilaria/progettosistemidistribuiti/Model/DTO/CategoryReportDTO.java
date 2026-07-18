package org.ilaria.progettosistemidistribuiti.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryReportDTO {

    private String category;
    private long count;
    private int percentage;

}
