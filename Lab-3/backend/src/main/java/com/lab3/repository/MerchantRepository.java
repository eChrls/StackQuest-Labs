package com.lab3.repository;
import com.lab3.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MerchantRepository extends JpaRepository<Merchant,String> {}
