import org.junit.Assert;
import org.junit.Test;
import weihui.bcss.support.dtp.core.monitor.transaction.TransactionStatisticsValue;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @Description
 * @Author liulei
 * @Date 2021/6/8 15:53
 **/
public class AtomicReferenceTest {

    @Test
    public void test() {
        AtomicReference<TransactionStatisticsValue> ar = new AtomicReference<TransactionStatisticsValue>();
        Assert.assertNull(ar.get());

        TransactionStatisticsValue value1 = new TransactionStatisticsValue();
        value1.setSuccess(1);
        ar.set(value1);
        Assert.assertEquals(ar.get().getSuccess(), 1);

        TransactionStatisticsValue value2 = new TransactionStatisticsValue();
        value2.setSuccess(2);
        ar.compareAndSet(value1, value2);
        Assert.assertEquals(ar.get().getSuccess(), 2);

        TransactionStatisticsValue value3 = new TransactionStatisticsValue();
        value3.setSuccess(3);
        ar.compareAndSet(value1, value3);
        Assert.assertEquals(ar.get().getSuccess(), 2);

        ar.compareAndSet(value2, value3);
        Assert.assertEquals(ar.get().getSuccess(), 3);
    }
}
