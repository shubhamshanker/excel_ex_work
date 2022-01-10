package com.example.excel_ex_work.model.pojo;

import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public abstract class AbstractEntity {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, name = "created_date")
    protected DateTime createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, name = "last_updated_date")
    protected DateTime lastUpdatedDate;

    @PrePersist
    protected void onCreate() {
        this.lastUpdatedDate = this.createdDate = DateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedDate = DateTime.now();
    }
}
