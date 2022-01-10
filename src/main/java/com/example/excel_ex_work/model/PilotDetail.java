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
import java.io.Serializable;

@Entity
@Table(name = "pilot_detail")
@Getter
@Setter
@ToString
public class PilotDetail extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "vendor_code")
    private String vendorCode;

    @Column(name = "km_last")
    private Long kmLast;

    @Column(name = "fueling_litres")
    private Long fuelinglitres;


}
