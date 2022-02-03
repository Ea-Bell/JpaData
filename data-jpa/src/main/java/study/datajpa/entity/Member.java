package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String username;

    //JPA는 기본 생성자가 있어야 한다. protected를 설정하면 아무데서나 불러올수 없게 만들기 위해서
    protected Member(){}

    public Member(String username) {
        this.username = username;
    }
}
