package com.ps.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ps.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
	
	List<Admin> findByEmailOrPhone(String email, String phone);
	
	Optional<Admin> findByEmail(String email);
	

	@Query("SELECT a.id FROM Admin a WHERE a.email = :email")
	Optional<Integer> findIdByEmail(@Param("email")String email);
	
	@Query("SELECT a.profilePicPath FROM Admin a WHERE a.email = :email")
	String getProfilePicName(@Param("email") String email);
	
	@Query("UPDATE Admin a SET a.password=:password WHERE a.email=:email")
	@Modifying
	int updatePassword(@Param("password") String password, @Param("email") String email);
	
	@Query("UPDATE Admin a SET a.profilePicPath=:profilePicPath WHERE a.email=:email")
	@Modifying
	int updateprofilePicPath(@Param("profilePicPath") String profilePicPath, @Param("email") String email);
	
	@Query("UPDATE Admin a SET a.name=:name WHERE a.email=:email")
	@Modifying
	int updateName(@Param("name") String name, @Param("email") String email);
}
