package cphbusiness.noInPuts.authService.repository;

import cphbusiness.noInPuts.authService.model.SpamCheck;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface SpamCheckRepository extends CrudRepository<SpamCheck, Long> {
    List<SpamCheck> findAllByIpAndTimestampIsGreaterThan(String ip, Date timestamp);
}
