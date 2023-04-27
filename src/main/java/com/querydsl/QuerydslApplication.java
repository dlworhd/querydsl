package com.querydsl;

import com.querydsl.model.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuerydslApplication {


	public static void main(String[] args) {
		SpringApplication.run(QuerydslApplication.class, args);



	}


}
