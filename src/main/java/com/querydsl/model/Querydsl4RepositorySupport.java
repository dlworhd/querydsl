//package com.querydsl.model;
//
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import jakarta.persistence.EntityManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.jpa.repository.support.JpaEntityInformation;
//import org.springframework.data.jpa.repository.support.Querydsl;
//import org.springframework.stereotype.Repository;
//import org.springframework.util.Assert;
//
//@Repository
//public class Querydsl4RepositorySupport {
//
//	private final Class domainClass;
//	private Querydsl querydsl;
//	private EntityManager em;
//	private JPAQueryFactory factory;
//
//	public Querydsl4RepositorySupport(Class domainClass) {
//		Assert.notNull(domainClass, "Domain class must not be null!");
//		this.domainClass = domainClass;
//	}
//
//	@Autowired
//	public void setEntityManager(EntityManager em){
//		Assert.notNull(em, "EntityManager must not be null!");
//		JpaEntityInformation entityInformation =
//
//	}
//}
