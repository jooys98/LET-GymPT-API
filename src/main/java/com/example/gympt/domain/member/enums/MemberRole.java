package com.example.gympt.domain.member.enums;

public enum MemberRole {
    USER, ADMIN , PREPARATION_TRAINER , TRAINER
//   0   ,  1   ,     2     ,             3
}
//가입시에 TRAINER 입력하면 PREPARATION_TRAINER 로 인서트되게
//ADMIN 의 허용 뒤에 TRAINER 가 될 수 있게 로직 구현