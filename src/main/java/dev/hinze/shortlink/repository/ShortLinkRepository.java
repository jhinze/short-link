package dev.hinze.shortlink.repository;

import dev.hinze.shortlink.model.ShortLink;
import org.springframework.data.repository.CrudRepository;

public interface ShortLinkRepository extends CrudRepository<ShortLink, String> {

}
