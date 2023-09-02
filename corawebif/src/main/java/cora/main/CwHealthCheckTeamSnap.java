package cora.main;

import com.codahale.metrics.health.HealthCheck;

/**
 * Dummy health check now to suppress warning
 * @author andrewmcgregor
 *
 */
public class CwHealthCheckTeamSnap extends HealthCheck {

	@Override
	protected Result check() throws Exception {
        return Result.healthy();
	}

}
