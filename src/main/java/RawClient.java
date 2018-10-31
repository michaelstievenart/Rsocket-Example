import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

public final class RawClient {

    public static void main(String[] args) {
        RSocket rSocket = RSocketFactory.connect()
                .transport(TcpClientTransport.create(8081))
                .start()
                .block();

        assert rSocket != null;
        Flux.range(0, 10)
                .doOnNext(i -> System.out.printf("Sending: %d%n", i))
                .map(i -> DefaultPayload.create(String.valueOf(i)))
                .flatMap(rSocket::requestStream)
                .doOnNext(response -> System.out.printf("Received: %s%n", response.getDataUtf8()))
                .blockLast();
    }
}
