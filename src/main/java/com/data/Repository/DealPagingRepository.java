package com.data.Repository;

import com.data.Model.Deals;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealPagingRepository extends PagingAndSortingRepository<Deals,Long> {
    List<Deals> findAllByDealtimeBetween(String from, String to, Pageable pageable);
}
