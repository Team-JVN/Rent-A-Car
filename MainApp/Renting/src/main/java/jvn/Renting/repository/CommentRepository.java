package jvn.Renting.repository;

import jvn.Renting.enumeration.CommentStatus;
import jvn.Renting.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findOneById(Long id);

    List<Comment> findAllByStatus(CommentStatus status);

    List<Comment> findBySenderIdAndRentInfoId(Long userId, Long rentInfoId);

    List<Comment> findByRentInfoId(Long rentInfoId);



}
