package com.financemanager.repository;
import com.financemanager.entity.Account; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface AccountRepository extends JpaRepository<Account,Long>{ List<Account> findByUserId(Long userId); java.util.Optional<Account> findByIdAndUserId(Long id,Long userId); }
