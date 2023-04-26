package com.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.model.Member;
import com.querydsl.model.QMember;
import com.querydsl.model.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Commit;

import static com.querydsl.model.QMember.*;

@Transactional
@SpringBootTest
@Commit
public class QueryDSLTest {

	@PersistenceContext
	EntityManager em;

	JPAQueryFactory factory;

	@BeforeEach
	@Test
	public void befor() throws Exception{
		factory = new JPAQueryFactory(em);
	}


	@Test
	public void test() throws Exception{
		Member findMember = factory
				.select(member)
				.from(member)
				.where(member.name.eq("TEST1"))
				.fetchOne();

		System.out.println(findMember.toString());

	}
}
