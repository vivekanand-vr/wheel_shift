package com.wheelshift.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnDTO {
    private String id;
    private String title;
    private List<String> taskIds;
}