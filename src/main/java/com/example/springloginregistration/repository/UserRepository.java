package com.example.springloginregistration.repository;

import com.example.springloginregistration.entity.UserEntity;
import com.example.springloginregistration.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Integer> {

    UserEntity findByEmail(String email);


}
