package com.querydsl.model;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {

	private String name;
	private String teamName;
	private int age;

	@QueryProjection
	public MemberDto(String name, String teamName, int age) {
		this.name = name;
		this.teamName = teamName;
		this.age = age;
	}


	@Override
	public String toString() {
		return "MemberDto{" +
				"name='" + name + '\'' +
				", age=" + age +
				'}';
	}

	public String getName() {
		return name;
	}
}
