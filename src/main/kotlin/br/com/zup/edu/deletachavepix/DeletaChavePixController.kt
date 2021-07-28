package br.com.zup.edu.deletachavepix

import br.com.zup.edu.DeletaChavePixRequest
import br.com.zup.edu.KeyManagerRemoveGrpcServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory

@Controller("/api/v1/clientes/{clienteId}")
class DeletaChavePixController(
    val grpcClient: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub,
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Delete("/pix/{pixId}")
    fun deleta(@PathVariable clienteId: String, @PathVariable pixId: String): HttpResponse<Any> {


        LOGGER.info("removendo chave pix = $pixId para o cliente com id = $clienteId")

        val grpcRequest = DeletaChavePixRequest.newBuilder().setPixId(pixId).setIdCliente(clienteId).build()
        val grpcResponse = grpcClient.deletarChavePix(grpcRequest)

        return HttpResponse.noContent()
    }
}