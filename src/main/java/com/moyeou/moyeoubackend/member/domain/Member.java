package com.moyeou.moyeoubackend.member.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = PROTECTED)
public class Member {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "department", nullable = false)
    private String department;

    @Column(name = "student_number", nullable = false)
    private Integer studentNumber;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "point")
    private Double point;

    public Member(String email, String department, Integer studentNumber, String nickname, String password) {
        this.email = email;
        this.department = department;
        this.studentNumber = studentNumber;
        this.nickname = nickname;
        this.password = password;
    }
}
