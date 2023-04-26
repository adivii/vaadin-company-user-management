package com.adivii.companymanagement.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adivii.companymanagement.data.entity.Avatar;

public interface AvatarRepository extends JpaRepository<Avatar, Integer> {
    public Optional<Avatar> findByUri(String uri);
}
