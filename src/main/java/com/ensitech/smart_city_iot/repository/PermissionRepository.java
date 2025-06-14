package com.ensitech.smart_city_iot.repository;

import com.ensitech.smart_city_iot.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
