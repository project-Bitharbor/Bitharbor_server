package developer.domain.member.repository;

import developer.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Member findByMemberId(Long memberId);
    Optional<Member> findByPhoneNumber(String phoneNumber);
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Member> findUserByEmailAndProvider(String email, String provider);
}