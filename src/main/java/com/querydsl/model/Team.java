package com.querydsl.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class Team {

	public Team(String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Member> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<Member> memberList) {
		this.memberList = memberList;
	}

	@OneToMany(mappedBy = "team")
	List<Member> memberList = new ArrayList<>();

	public void addMember(Member member){
		if(this.memberList == null){
			this.memberList = new ArrayList<>();
		}
		member.setTeam(this);
		memberList.add(member);
	}


}

