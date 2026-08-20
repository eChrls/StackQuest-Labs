package com.lab3.service;
import com.lab3.dto.MerchantDto;
import com.lab3.repository.MerchantRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service public class MerchantService {
 private final MerchantRepository repository;
 public MerchantService(MerchantRepository repository){this.repository=repository;}
 public List<MerchantDto> list(){return repository.findAll().stream().map(m->new MerchantDto(m.getId(),m.getName(),m.isActive())).toList();}
}
