package de.codecentric.etcd;

import mousio.client.promises.ResponsePromise;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * Wird innerhalb der Tests verwendet um dort als Default-Handler verwendet zu werden.
 * Der Context verwaltet eine CountDownLatch um auf das Auslösen des Handlers warten zu können.
 */
class AsyncTestContext<T> implements ResponsePromise.IsSimplePromiseResponseHandler<T> {
    private CountDownLatch cl = new CountDownLatch(1);
    private ResponsePromise<T> response;

    @Override
    public void onResponse(ResponsePromise<T> response) {
        this.response = response;
        cl.countDown();
    }

    /**
     * Setzt den Context zurück indem er das Ergebnis vom vorherigen Lauf löscht und eine neue Latch erzeugt.
     * @return
     */
    public AsyncTestContext<T> reset() {
        cl = new CountDownLatch(1);
        response = null;
        return this;
    }

    /**
     * Warte 1000 ms bevor ein AssertionError geworfen wird.
     * @throws InterruptedException
     */
    public void await() throws InterruptedException {
        assertThat(cl.await(1000, TimeUnit.MILLISECONDS)).as("Timeout while waiting").isTrue();
    }

    /**
     * Warte eine festgesetzte Zahl an ms bevor ein AssertionError geworfen wird.
     * @param timeInMs
     * @throws InterruptedException
     */
    public void await(long timeInMs) throws InterruptedException {
        assertThat(cl.await(timeInMs, TimeUnit.MILLISECONDS)).as("Timeout while waiting").isTrue();
    }

    /**
     * Antwortobjekt des letzten Handleraufrufs.
     * @return
     */
    public ResponsePromise<T> response() {
        return response;
    }
}
