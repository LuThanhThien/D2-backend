package com.dainam.D2.repository.auth;

import com.dainam.D2.models.auth.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITokenRepository extends JpaRepository<Token, Long> {
    @Query(value = """
            SELECT t FROM Token t INNER JOIN User u
            ON t.user.id = u.id
            WHERE u.id = :id AND (t.expired = false OR t.revoked = false)
            ORDER BY t.createdDatetime DESC
            """)
    List<Token> findAllValidTokenByUserId(@Param("id") Long id);


    @Query(value = """
                    SELECT t FROM Token t INNER JOIN User u 
                    ON t.user.id = u.id
                    WHERE u.id = :id AND (t.expired = true OR t.revoked = true)
                    ORDER BY t.createdDatetime DESC
                    """
    )
    List<Token> findAllInvalidTokenByUserId(@Param("id") Long id);

    Optional<Token> findByToken(String token);
}
