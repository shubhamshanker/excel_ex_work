package com.example.excel_ex_work.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PilotDetailDto {
    private String pilotCode;
    private Long kmLast;
    private Long fuelinglitres;
}
