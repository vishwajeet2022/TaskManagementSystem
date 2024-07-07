package com.tms.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class AuditAwareImpl implements AuditorAware<String>{

	public Optional<String> getCurrentAuditor()
	{
		return Optional.of("TMS");
	}
}
