package com.lolduo.duo.repository;

import com.lolduo.duo.entity.LoLUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoLUserRepository extends JpaRepository<LoLUserEntity, String> {
    @Query(value = "select LE.puuid from LoLUserEntity LE where LE.tier = ?1")
    List<String> findPuuidsByLeague (String league);

}
