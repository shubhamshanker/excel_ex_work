package com.example.excel_ex_work.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExcelReadErrorWrapper {
    private Throwable th;
    private Object object;
    private String columnTitle;
}
