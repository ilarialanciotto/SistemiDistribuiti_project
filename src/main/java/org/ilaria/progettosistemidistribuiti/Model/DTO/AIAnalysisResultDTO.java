package org.ilaria.progettosistemidistribuiti.Model.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AIAnalysisResultDTO {
    private String keyword;
    private String category;
    private int priority_level;
}