package com.data.Repository;

import com.data.Model.Deals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealRepository extends JpaRepository<Deals,Long> {
    Deals findByDealid(String dealId);

}
