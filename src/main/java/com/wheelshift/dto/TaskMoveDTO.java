package com.wheelshift.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskMoveDTO {
    private String taskId;
    private String sourceColumnId;
    private int sourceIndex;
    private String destinationColumnId;
    private int destinationIndex;
}