package com.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.List;

import static com.querydsl.model.QMember.member;
import static com.querydsl.model.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@Commit
public class QueryDSLTest {

	@PersistenceContext
	EntityManager em;

	@PersistenceUnit
	EntityManagerFactory emf;

	JPAQueryFactory factory;

	@BeforeEach
	@Test
	public void befor() throws Exception {
		factory = new JPAQueryFactory(em);
	}

	@Test
	public void ttt() {


//		for (long i = 1; i <= 100; i++) {
//			Member member = new Member();
//			member.setAge((int)(Math.random() * 100 + 1));
//			member.setName("TEST" + i);
//			em.persist(member);
//		}

//		for (int i = 1; i <= 10; i++) {
//			Team team = new Team();
//			team.setName("Team" + i);
//			em.persist(team);
//		}

		for (long i = 1; i <= 100; i++) {
			Member member = em.find(Member.class, i);
			Team team = em.find(Team.class, (long) (Math.random() * 10 + 1));
			member.setTeam(team);
		}

	}

	@Test
	public void findMember() throws Exception {
		Member findMember = factory
				.select(member)
				.from(member)
				.where(member.name.eq("TEST1"))
				.fetchOne();

		System.out.println(findMember.toString());
	}

	@Test
	public void expression() {

		List<Tuple> fetch = factory
				.select(team.name, member.age.avg())
				.from(member)
				.groupBy(team.name)
//				.having(team.name.like("%1%"))
//				.where(member.name.like("%1%"))
//				.where(member.name.contains("TEST"))
//				.where(member.name.startsWith("T"))
//				.where(member.age.between(10, 30))
//				.orderBy(member.age.desc().nullsLast())
//				.offset(0)
//				.limit(10)
				.fetch();

		for (Tuple tuple : fetch) {
			tuple.get(team.name);
			tuple.get(member.age.avg());
		}

		System.out.println(fetch);

//		for (Member findMember : fetch) {
//			System.out.println(findMember.toString());
//		}

	}


	@Test
	public void join() {

		List<Member> members = factory
				.select(member)
				.from(member)
				.join(member.team, team)
				.where(team.name.eq("Team1"))
				.fetch();

//		for (Tuple tuple : tupleList) {
//			System.out.println(tuple.get(member));
//			System.out.println(tuple.get(team.name));
//		}

		assertThat(members)
				.extracting("name")
				.containsExactly("TEST4", "TEST20", "TEST65", "TEST84");


//		for (Member findMember : memberList) {
//			System.out.println(findMember.toString());
//		}
	}

	@Test
	public void theta_join() {

		List<Member> fetch = factory
				.select(member)
				.from(member, team)
				.where(member.name.eq(team.name))
				.fetch();

		for (Member findMember : fetch) {
			System.out.println(findMember);
		}
	}

	@Test
	public void on_join() {

//		List<Tuple> memberList = factory.select(member, team).from(member).leftJoin(member.team, team).on(team.name.eq("Team1")).fetch();
//		for (Tuple tuple : memberList) {
//			System.out.println(tuple);
//		}

		List<Tuple> fetch = factory.select(member, team).from(member).leftJoin(team).on(member.name.eq(team.name)).fetch();

		for (Tuple tuple : fetch) {
			System.out.println(tuple);
		}

	}


	@Test
	public void isloaded() {

		Member findMember = factory.select(member).from(member).where(member.name.eq("TEST10")).fetchOne();

		assertThat(emf.getPersistenceUnitUtil().isLoaded(findMember)).isTrue();
		assertThat(emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam())).isFalse();
	}

	@Test
	public void fetch_join() {

		List<Member> fetch = factory.select(member).from(member).fetch();
		for (Member findMember : fetch) {
			System.out.println(findMember.getTeam().getName());
		}
	}

	@Test
	public void subQuery() {

//		List<Member> fetch = factory.select(member)
//				.from(member)
//				.where(member.age.goe(JPAExpressions
//						.select(member.age.avg())
//						.from(member)))
//				.fetch();
//
//		for (Member findMember : fetch) {
//			System.out.println(findMember);
//		}


		List<Tuple> fetch = factory
				.select(member.name, JPAExpressions.select(member.age.avg())
						.from(member))
				.from(member)
				.fetch();
		for (Tuple tuple : fetch) {
			System.out.println(tuple);
		}
	}


	@Test
	public void case_expression() {

//		List<String> fetch = factory.select(member.age
//						.when(10).then("열 살")
//						.when(20).then("스무 살")
//						.otherwise("늙은이들"))
//				.from(member)
//				.fetch();

		List<String> fetch = factory.select(new CaseBuilder()
						.when(member.age.between(10, 20)).then("10대")
						.when(member.age.between(20, 30)).then("20대")
						.otherwise("나머지"))
				.from(member)
				.fetch();


		for (String s : fetch) {
			System.out.println(s);
		}
	}

	@Test
	public void hard_case() {

		NumberExpression<Integer> expression = new CaseBuilder()
				.when(member.age.between(10, 20)).then(2)
				.when(member.age.between(20, 30)).then(3)
				.otherwise(1);

		List<Tuple> fetch = factory
				.select(member, expression)
				.from(member)
				.orderBy(expression.desc())
				.fetch();
		for (Tuple tuple : fetch) {
			System.out.println(tuple);
		}
	}

	@Test
	public void constant() {

//		List<Tuple> tuples = factory.select(member, Expressions.constant("A"))
//				.from(member)
//				.fetch();
//
//		for (Tuple tuple : tuples) {
//			System.out.println(tuple);
//		}

		List<String> concat = factory
				.select(member.name.concat("_" + Expressions.constant("A")))
				.from(member)
				.fetch();

		for (String s : concat) {
			System.out.println(s);
		}
	}


	// 문법 중급


	@Test
	public void projection_bean() {

		List<MemberDto> fetch = factory
				.select(Projections.bean(MemberDto.class, member.name, member.age))
				.from(member)
				.fetch();
		for (MemberDto memberDto : fetch) {
			System.out.println(memberDto);
		}
	}

	@Test
	public void projection_field() {
		List<MemberDto> fetch = factory
				.select(Projections.fields(MemberDto.class, member.name, member.age))
				.from(member)
				.fetch();
		for (MemberDto memberDto : fetch) {
			System.out.println(memberDto);
		}
	}

	@Test
	public void projection_as() {
		List<MemberDto> fetch = factory
				.select(Projections
						.fields(MemberDto.class, member.name.as("name"),
								ExpressionUtils.as(JPAExpressions.select(member.age.max()).from(member), "age"))
				).from(member)
				.fetch();
		for (MemberDto memberDto : fetch) {
			System.out.println(memberDto);
		}
	}

	@Test
	public void projection_constructor() {
		List<MemberDto> fetch = factory
				.select(Projections.constructor(MemberDto.class, member.name, member.age))
				.from(member)
				.fetch();
		for (MemberDto memberDto : fetch) {
			System.out.println(memberDto.toString());
		}
	}

	@Test
	public void projection_QMemberDto_constructor() {
		List<MemberDto> fetch = factory
				.select(new QMemberDto(member.name, member.team.name, member.age))
				.from(member)
				.fetch();
		for (MemberDto memberDto : fetch) {
			System.out.println(memberDto.toString());
		}
	}

	@Test
	public void distinct() {

		List<Member> fetch = factory.select(member).distinct().from(member).fetch();

		for (Member findMember : fetch) {
			System.out.println(findMember);
		}
	}

	@Test
	public void builderTest() {

		List<Member> members = searchMember1("TEST49", 80);
		for (Member findMember : members) {
			System.out.println(findMember.toString());
		}

	}

	public List<Member> searchMember1(String nameCond, Integer ageCond) {
		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (nameCond != null) {
			booleanBuilder.and(member.name.eq(nameCond));
		}

		if (ageCond != null) {
			booleanBuilder.and(member.age.eq(ageCond));
		}

		return factory.select(member).from(member).where(booleanBuilder).fetch();
	}

	public List<Member> searchMember2(String name, Integer age) {
		return factory.selectFrom(member).where(nameEq(name), ageEq(age)).fetch();
	}

	public List<Member> searchMember3(String name, Integer age) {
		return factory.selectFrom(member).where(allEq(name, age)).fetch();
	}

	public BooleanExpression nameEq(String nameCond) {
		return nameCond != null ? member.name.eq(nameCond) : null;
	}

	public BooleanExpression ageEq(Integer ageCond) {
		return ageCond != null ? member.age.eq(ageCond) : null;
	}

	public BooleanExpression allEq(String name, Integer ageCond) {
		return nameEq(name).and(ageEq(ageCond));
	}


	@Test
	public void bulk() {
		long execute = factory
				.update(member)
				.set(member.name, member.name.concat("+"))
				.where(member.name.like("%2"))
				.execute();

		List<Member> fetch = factory.selectFrom(member).fetch();
		for (Member findMember : fetch) {
			System.out.println(findMember.toString());
		}
	}


	@Test
	public void condition() {
		MemberSearchCondition cond = new MemberSearchCondition();
		cond.setAgeGoe(0);
		cond.setAgeLoe(100);
		cond.setTeamname("Team1");

		List<MemberDto> fetch = factory
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

		for (MemberDto memberDto : fetch) {
			System.out.println(memberDto.toString());
		}


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