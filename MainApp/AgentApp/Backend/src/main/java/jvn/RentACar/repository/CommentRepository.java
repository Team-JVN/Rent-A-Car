package jvn.RentACar.repository;

import jvn.RentACar.enumeration.CommentStatus;
import jvn.RentACar.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findOneById(Long id);

    List<Comment> findAllByStatus(CommentStatus status);

    List<Comment> findBySenderIdAndRentInfoId(Long userId, Long rentInfoId);

    Comment findByMainAppId(Long id);

}
