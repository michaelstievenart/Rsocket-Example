import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class RawServer {

    public static void main(String[] args) {

        RSocketFactory.receive()
                .acceptor(((setup, sendingSocket) -> Mono.just(new DefaultSimpleService())))
                .transport(TcpServerTransport.create(8081))
                .start()
                .block()
                .onClose()
                .block();
    }

    private static final class DefaultSimpleService extends AbstractRSocket {

        @Override
        public Flux<Payload> requestStream(Payload payload) {

            return Mono.just(Integer.valueOf(payload.getDataUtf8()))
                    .doOnNext(i -> System.out.printf("Received: %d%n", i))
                    .flatMapMany(i -> Flux.range(0, i))
                    .doOnNext(i -> System.out.printf("Sending: %d%n", i))
                    .map(i -> DefaultPayload.create(String.valueOf(i)));
        }
    }
}
