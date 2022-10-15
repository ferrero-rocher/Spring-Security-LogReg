package com.example.springloginregistration.repository;

import com.example.springloginregistration.entity.VerificationURLEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationURLRepository extends JpaRepository<VerificationURLEntity,Integer> {

    public VerificationURLEntity findByToken(String token);


    VerificationURLEntity findByUserId(int user_id);
}


