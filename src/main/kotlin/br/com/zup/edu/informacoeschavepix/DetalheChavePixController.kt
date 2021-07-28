package br.com.zup.edu.informacoeschavepix

import br.com.zup.edu.DadosChavePixRequest
import br.com.zup.edu.KeyManagerDadosGrpcServiceGrpc
import br.com.zup.edu.deletachavepix.DetalheChavePixDto
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import org.slf4j.LoggerFactory

@Controller("/api/v1/clientes/{clienteId}")
class DetalheChavePixController(
    val grpcClient: KeyManagerDadosGrpcServiceGrpc.KeyManagerDadosGrpcServiceBlockingStub,
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Get("/pix/{pixId}")
    fun deleta(@PathVariable clienteId: String, @PathVariable pixId: String): HttpResponse<Any> {
        LOGGER.info("detalhando a  chave pix = $pixId para o cliente com id = $clienteId")

        val responseGrpc =
            grpcClient.dadosChavePix(DadosChavePixRequest.newBuilder().setPixId(
                DadosChavePixRequest.PixId.newBuilder()
                    .setPixId(pixId)
                    .setIdCliente(clienteId)
                    .build()
            ).build())

        val detalheChavePixDto = DetalheChavePixDto(responseGrpc)

        return HttpResponse.ok(detalheChavePixDto)
    }
}