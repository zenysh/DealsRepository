package com.data.Repository;

import com.data.Model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency,Long> {
  Currency findCurrencyByCurrencycode(String code);
}
