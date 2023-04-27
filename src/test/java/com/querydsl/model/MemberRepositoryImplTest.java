package com.querydsl.model;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@Transactional
@SpringBootTest
class MemberRepositoryImplTest {

	@Autowired
	EntityManager em;

	@Autowired
	JPAQueryFactory factory;

	@Autowired
	MemberRepository memberRepository;

	@Test
	void test(){

		Team team11 = new Team("Team11");
		Team team12 = new Team("Team12");

		em.persist(team11);
		em.persist(team12);


		Member member1 = new Member("member1", 10, team11);
		Member member2 = new Member("member2", 20, team11);
		Member member3 = new Member("member3", 30, team12);
		Member member4 = new Member("member4", 40, team12);

		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);

		MemberSearchCondition cond = new MemberSearchCondition();

		cond.setAgeGoe(0);
		cond.setAgeLoe(100);
		cond.setTeamname("Team1");

		List<MemberDto> search = memberRepository.search(cond);

		for (MemberDto memberDto : search) {
			System.out.println(memberDto.toString());
		}
	}


}