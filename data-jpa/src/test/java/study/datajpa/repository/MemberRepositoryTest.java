package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired MemberJpaRepository memberJpaRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;


    @Test
    public void testMember(){
        System.out.println("memberRepository = " + memberRepository.getClass());
        Member member = new Member("memberB");
        Member savedMember = memberRepository.save(member);

        //optional의 orElse와 orElseGet의 사용법을 알아볼것.
        Member findMember = memberRepository.findById(savedMember.getId()).orElse(null);

        assert findMember != null;
        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);


        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }


    @Test
    public void findByUsernameAndAgeGreaterThan(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 10);

//        System.out.println("result.get(0).getUsername() = " + result.get(0).getUsername());
//        memberRepository.save(m1);
//        memberRepository.save(m2);
//
//        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
//
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNameQuery(){
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("BBB", 20);
    memberJpaRepository.save(m1);
    memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);

    }

    @Test
    public void testNamedQuery(){
        Member m1 =new Member("AAA", 10);
        Member m2 = new Member("BBB", 20 );
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void testFindUsernameList(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String member : usernameList) {
            //System.out.println("member = " + member);
            assertThat(usernameList).contains(member);
        }

        }
    @Test
    public void findMEmberDto() {
        Team team= new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }

    }

    @Test
    public void findByNames(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType(){

        Member m1 =new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

/*        List<Member> aaa = memberRepository.findListByUsername("AAA");
        System.out.println("findMember = " + aaa);
        Member findMember = memberRepository.findMemberByUsername("AAA");
        System.out.println("findMember = " + findMember);
        Optional<Member> optional = memberRepository.findOptionalByUsername("AAA");
        */

        Optional<Member> findMember = memberRepository.findOptionalByUsername("asdfasdee");
        System.out.println("findMember = " + findMember);

    }

    @Test
    public void pageing(){
        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));
        int age =10;
        int offset=1; //현재 페이지
        int limit=3;
        //when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        Long totalCount = memberJpaRepository.totalCount(age);

        //페이지 계산 공식 적용 ...
        // totalPage = totalCount/size ...
        // 마지막페이지 ...
        // 최초 페이지 ...

        //then
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    public void pagin(){

            //given
            memberRepository.save(new Member("member1", 10));
            memberRepository.save(new Member("member2", 10));
            memberRepository.save(new Member("member3", 10));
            memberRepository.save(new Member("member4", 10));
            memberRepository.save(new Member("member5", 10));

        //페이지는 1부터가 아니라 0부터 시작이다.
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));


            int age =10;
            int offset=1; //현재 페이지
            int limit=3;
            //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //stream map을 이용하여 dto변환
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));


        //페이지 계산 공식 적용 ...
            // totalPage = totalCount/size ...
            // 마지막페이지 ...
            // 최초 페이지 ...

            //then
        //Page객체로 받아서 쓸때
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();


        //Slice객체를 받아서 쓸때
       /* List<Member> content = page.getContent();
        assertThat(content.size()).isEqualTo(3);
        //assertThat(page.getTotalElements()).isEqualTo(5);  //Slice 객체에서는 처리 안해줌
        assertThat(page.getNumber()).isEqualTo(0);
        //assertThat(page.getTotalPages()).isEqualTo(2);     //Slice 객체에서는 처리 안해줌
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();*/
    }

    @Test
    public void bulkUpdateJpa(){
        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 15));
        memberJpaRepository.save(new Member("member3", 19));
        memberJpaRepository.save(new Member("member4", 20));
        memberJpaRepository.save(new Member("member5", 21));
        memberJpaRepository.save(new Member("member6", 40));

        //when
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void bulkUpdate(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 15));
        memberRepository.save(new Member("member3", 19));
        memberRepository.save(new Member("member4", 20));
        memberRepository.save(new Member("member5", 21));
        memberRepository.save(new Member("member6", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20); //벌크 연산 메소드(JPA Data에서 자동으로 클리어 해주는 기능이 있음)
        //기억하자 벌크 연산 후에는 반드시 영속성을 초기화하고 다시 데이터를 불러와야한다는것을..
//        em.flush();
//        em.clear();
        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);
        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy(){
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

//        //when  N+1발생
//        List<Member> members = memberRepository.findAll();
//        for (Member member : members) {
//            System.out.println("member = " + member.getUsername());
//            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
//            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
//        }
        //when
//        List<Member> members = memberRepository.findMemberFetchJoin();
//        for (Member member : members) {
//            System.out.println("member = " + member.getUsername());
//            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
//            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
//        }
//       //when
//        List<Member> members = memberRepository.findAll();
//        for (Member member : members) {
//            System.out.println("member = " + member.getUsername());
//            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
//            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
//        }

        //when
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint(){
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");
        em.flush();
    }

    @Test
    public void lock(){
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom(){
        List<Member> result = memberRepository.findMemberCustom();
        for (Member result2 : result) {
            System.out.println("result2 = " + result2);
        }
    }

    @Test
    public void JpaEventBaseEntity() throws InterruptedException {
        //given
        Member member = new Member("member1");
        memberRepository.save(member); // @PrePersist
        Thread.sleep(1000);
        member.setUsername("member2");

        em.flush(); //@PreUpdate
        em.clear();
        //when
        Member findMember = memberRepository.findById(member.getId()).get();

        //then
        System.out.println("findMember.getCreateDate = " + findMember.getCreateDate());
        System.out.println("findMember.getUpdateDate = " + findMember.getUpdateDate());
    }
}

