package webprogramming.csc1106.Entities;

import org.springframework.data.repository.CrudRepository;

public interface ThreadReplyRepo extends CrudRepository<ThreadReply, Integer>{
    Iterable<ThreadReply> findByThreadID(int threadId);
}
