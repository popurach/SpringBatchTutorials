package com.example.springbatchtuto.core.domain.member.repository;

import com.example.springbatchtuto.core.domain.member.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
