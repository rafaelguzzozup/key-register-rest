package br.com.zup.edu.cadastrachavepix

import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.validation.Valid

@Validated
@Controller("/api/v1/clientes/{clienteId}")
class CadastraChavePixController(
    val grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub,
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Post("/pix")
    fun cadastrar(@PathVariable clienteId: String, @Valid @Body request: RegistraChavePixRequest): HttpResponse<Any> {
        LOGGER.info("criando uma chave pix para o cliente com id = $clienteId")

        val grpcResponse = grpcClient.cadastrarChavePix(request.converteParaRequestGrpc(clienteId))

        val uri = HttpResponse.uri("/api/v1/clientes/${clienteId}/pix/${grpcResponse.pixId}")
        return HttpResponse.created(uri)
    }
}