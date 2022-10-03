package {{packageName}};

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import reactor.blockhound.BlockingOperationError;
import reactor.core.scheduler.Schedulers;

@IntegrationTest
class ApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void blockHoundWorks() throws InterruptedException, TimeoutException {
		try {
			FutureTask<?> task = new FutureTask<>(() -> {
				Thread.sleep(0);
				return "";
			});
			Schedulers.parallel().schedule(task);
			task.get(10, TimeUnit.SECONDS);
			fail("should fail");
		} catch (ExecutionException e) {
			assertTrue("detected", e.getCause() instanceof BlockingOperationError);
		}
	}
}
