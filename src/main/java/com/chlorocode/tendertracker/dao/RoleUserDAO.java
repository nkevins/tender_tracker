package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by andy on 22/7/2017.
 */
public interface RoleUserDAO extends JpaRepository<RoleUser, Integer> {

}
