package com.suken27.humanfactorsjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suken27.humanfactorsjava.model.TeamMember;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    
    public TeamMember findByEmail(String email);
    
}
