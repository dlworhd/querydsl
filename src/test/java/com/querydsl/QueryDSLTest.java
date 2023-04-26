package com.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.model.Member;
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
	public void constant(){

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
}