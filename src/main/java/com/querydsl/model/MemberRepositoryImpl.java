package com.querydsl.model;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.querydsl.model.QMember.*;
import static com.querydsl.model.QTeam.team;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

	private final JPAQueryFactory factory;

	public MemberRepositoryImpl(EntityManager em) {
		factory = new JPAQueryFactory(em);
	}

	@Override
	public List<MemberDto> search(MemberSearchCondition cond) {

		return factory
				.select(new QMemberDto(
						member.name,
						team.name,
						member.age
				))
				.from(member)
				.leftJoin(member.team, team)
				.where(
						teamNameEq(cond.getTeamname()),
						nameEq(cond.getUsername()),
						ageGoe(cond.getAgeGoe()),
						ageLoe(cond.getAgeLoe())
				)
				.fetch();
	}

	@Override
	public Page<MemberDto> searchPageSimple(MemberSearchCondition cond, Pageable pageable) {

		QueryResults<MemberDto> results = factory.select(new QMemberDto(member.name, team.name, member.age))
				.from(member)
				.leftJoin(member.team, team)
				.where(
						member.name.eq(cond.getUsername()),
						member.age.loe(cond.getAgeLoe()),
						team.name.eq(cond.getTeamname())
				)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetchResults();

		List<MemberDto> memberDtoList = results.getResults();
		long total = results.getTotal();

		return new PageImpl<>(memberDtoList, pageable, total);

	}

	@Override
	public Page<MemberDto> searchPageComplex(MemberSearchCondition cond, Pageable pageable) {
		List<MemberDto> memberDtoList = factory.select(new QMemberDto(member.name, team.name, member.age))
				.from(member)
				.leftJoin(member.team, team)
				.where(
						member.name.eq(cond.getUsername()),
						member.age.loe(cond.getAgeLoe()),
						team.name.eq(cond.getTeamname())
				)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

//		long count = factory.select(member.count())
//				.from(member)
//				.leftJoin(member.team, team)
//				.where(
//						member.name.eq(cond.getUsername()),
//						member.age.loe(cond.getAgeLoe()),
//						team.name.eq(cond.getTeamname())
//				)
//				.fetchOne();
//		return new PageImpl<>(memberDtoList, pageable, count);



		//최적화
		JPAQuery<Member> jpaQuery = factory.select(member)
				.from(member)
				.leftJoin(member.team, team)
				.where(
						member.name.eq(cond.getUsername()),
						member.age.loe(cond.getAgeLoe()),
						team.name.eq(cond.getTeamname()));

		// 페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
		// 마지막 페이지 일 때 (offset + 컨텐츠 사이즈를 더해서 전체 사이즈 구함)
		return PageableExecutionUtils.getPage(memberDtoList, pageable, jpaQuery::fetchCount);

	}

	public BooleanExpression nameEq(String nameCond) {
		return nameCond != null ? member.name.eq(nameCond) : null;
	}

	public BooleanExpression teamNameEq(String teamNameCond) {
		return teamNameCond != null ? team.name.eq(teamNameCond) : null;
	}

	public BooleanExpression ageGoe(Integer ageCond) {
		return ageCond != null ? member.age.goe(ageCond) : null;
	}

	public BooleanExpression ageLoe(Integer ageCond) {
		return ageCond != null ? member.age.loe(ageCond) : null;
	}

}
