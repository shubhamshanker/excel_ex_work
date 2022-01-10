package com.example.excel_ex_work.repository;

import com.example.excel_ex_work.model.PilotDetail;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;


@Repository
public interface PilotRepository extends CrudRepository<PilotDetail, Long>{
}
