package com.suken27.humanfactorsjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suken27.humanfactorsjava.model.DeletedTeamMember;

public interface DeletedTeamMemberRepository extends JpaRepository<DeletedTeamMember, Long> {
    
}
