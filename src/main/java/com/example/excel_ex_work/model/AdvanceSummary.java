package com.example.excel_ex_work.model;

import com.example.excel_ex_work.model.pojo.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "advance_summary")
@Getter
@Setter
@ToString
public class AdvanceSummary extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "vendor_code")
    private String vendor_code;

    @Column(name = "advance_component")
    private String advanceComponent;

    @Column(name = "cumulative_till_date")
    private Long cumulativeTillDate;

    @Column(name = "previous_open_amount")
    private Long previousOpenAmount;

    @Column(name = "current_adjustment")
    private Long currentAdjustment;

}
