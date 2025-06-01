package com.dsi.project.phoneBook.dao;

import com.dsi.project.phoneBook.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Integer> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    public  User getUserByEmail(@Param("email") String email);
}
