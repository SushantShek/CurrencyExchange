package com.marcura.currency.repository;

import com.marcura.currency.entity.Rates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RatesRepository extends JpaRepository<Rates, Long> {

    Rates findFirstByOrderByIdDesc();

    @Query("SELECT e FROM Rates e WHERE e.id = (:date)")
    Rates findById(@Param("date") String date);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Rates c SET c.counter = :address WHERE c.id = :companyId")
    int updateAddress(@Param("companyId") int companyId, @Param("address") String address);
}
