package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import webprogramming.csc1106.Entities.Post;
import webprogramming.csc1106.Entities.PostAttachments;

public interface AttachmentsRepo extends JpaRepository<PostAttachments, Integer>{
    @Transactional
    void deleteByPost(Post post);
}
