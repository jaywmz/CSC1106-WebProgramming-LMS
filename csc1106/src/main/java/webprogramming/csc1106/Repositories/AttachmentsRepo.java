package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import webprogramming.csc1106.Entities.PostAttachments;

public interface AttachmentsRepo extends JpaRepository<PostAttachments, Integer>{
    
}
