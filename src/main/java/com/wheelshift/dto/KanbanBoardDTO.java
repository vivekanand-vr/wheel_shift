package com.wheelshift.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KanbanBoardDTO {
    private List<TaskDTO> tasks;
    private List<ColumnDTO> columns;
    private List<String> columnOrder;
}
