package com.querydsl.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {
	List<MemberDto> search(MemberSearchCondition cond);

	Page<MemberDto> searchPageSimple(MemberSearchCondition cond, Pageable pageable);
	Page<MemberDto> searchPageComplex(MemberSearchCondition cond, Pageable pageable);
}
