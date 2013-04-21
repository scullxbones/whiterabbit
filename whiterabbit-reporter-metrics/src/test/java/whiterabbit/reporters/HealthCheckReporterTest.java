package whiterabbit.reporters;

import static org.mockito.Mockito.*;
import static org.fest.assertions.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.yammer.metrics.health.HealthCheckRegistry;
import com.yammer.metrics.health.HealthCheck.Result;

public class HealthCheckReporterTest {

	HealthCheckRegistry registry = mock(HealthCheckRegistry.class);
	HealthCheckReporter underTest;
	
	@Before
	public void setUp() {
		underTest = new HealthCheckReporter(registry);
		underTest.setHealthyPercentage(BigDecimal.valueOf(50));
	}
	
	@Test
	public void correctlyTracksRatioOfTimeoutToTotalExactPercentage() throws Exception {
		underTest.reportCancellation(null);
		underTest.reportTimeout(null);
		assertThat(underTest.check()).isEqualTo(Result.healthy());
	}

	@Test
	public void correctlyTracksRatioOfTimeoutToTotalAbovePercentage() throws Exception {
		underTest.reportCancellation(null);
		underTest.reportCancellation(null);
		underTest.reportTimeout(null);
		assertThat(underTest.check()).isEqualTo(Result.healthy());
	}

	@Test
	public void correctlyTracksRatioOfTimeoutToTotalBelowPercentage() throws Exception {
		underTest.reportCancellation(null);
		underTest.reportTimeout(null);
		underTest.reportTimeout(null);
		assertThat(underTest.check().isHealthy()).isEqualTo(false);
	}

}
