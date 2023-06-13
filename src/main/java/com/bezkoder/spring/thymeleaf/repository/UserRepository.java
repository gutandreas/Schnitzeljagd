package com.bezkoder.spring.thymeleaf.repository;

import com.bezkoder.spring.thymeleaf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
  List<User> findByVornameIgnoreCase(String keyword);
  List<User> findByNachnameIgnoreCase(String keyword);

  void deleteByCode(String code);
  List<User> findByCode(String code);
  boolean existsByCode(String code);

  @Query("SELECT u FROM User u ORDER BY u.id DESC")
  List<User> findNewestUsers();

  @Query("SELECT u FROM User u WHERE u.duration IS NOT NULL ORDER BY u.duration")
  List<User> getRanking();





}
