package jvn.Renting.repository;

import jvn.Renting.enumeration.CommentStatus;
import jvn.Renting.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findOneById(Long id);

    List<Comment> findAllByStatus(CommentStatus status);

}
