package com.contactmanager.smartcontactmanager.entities;

import org.hibernate.annotations.ManyToAny;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
private int cid;
private String name;
private String secondName;
private String email;
private String work;
private String phone;
private String imageUrl;
@Column(length = 1000)
private String description;

@ManyToOne
@JsonIgnore
private User user;
public User getUser() {
	return user;
}

public String getEmail() {
	return email;
}

public void setEmail(String email) {
	this.email = email;
}

public String getWork() {
	return work;
}

public void setWork(String work) {
	this.work = work;
}

public void setUser(User user) {
	this.user = user;
}
public int getCid() {
	return cid;
}
public void setCid(int cid) {
	this.cid = cid;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getSecondName() {
	return secondName;
}
public void setSecondName(String secondName) {
	this.secondName = secondName;
}
public String getPhone() {
	return phone;
}
public void setPhone(String phone) {
	this.phone = phone;
}
public String getImageUrl() {
	return imageUrl;
}
public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
}
public String getDescription() {
	return description;
}
public void setDescription(String description) {
	this.description = description;
}

@Override
public boolean equals(Object obj) {
	return this.cid==((Contact)obj).getCid();
}



}
