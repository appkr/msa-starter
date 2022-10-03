package {{packageName}};

import com.google.auto.service.AutoService;
import java.io.FilterInputStream;
import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;

@AutoService(TestBlockHoundIntegration.class)
public class TestBlockHoundIntegration implements BlockHoundIntegration {

  @Override
  public void applyTo(BlockHound.Builder builder) {
    builder.allowBlockingCallsInside("reactor.core.scheduler.BoundedElasticScheduler$BoundedState", "dispose");
    builder.allowBlockingCallsInside("reactor.core.scheduler.BoundedElasticScheduler", "schedule");
    builder.allowBlockingCallsInside("org.springframework.validation.beanvalidation.SpringValidatorAdapter", "validate");
    // r2dbc initScript FilterInputStream 처리 시 RandomAccessFile.readBytes blocking 허용
    builder.allowBlockingCallsInside(FilterInputStream.class.getName(), "read");
  }
}
