package br.com.zup.edu.listachavepix

import br.com.zup.edu.KeyManagerListaGrpcServiceGrpc
import br.com.zup.edu.ListaChavesPixRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import org.slf4j.LoggerFactory

@Controller("/api/v1/clientes/{clienteId}")
class ListaChavePixController(
    val grpcClient: KeyManagerListaGrpcServiceGrpc.KeyManagerListaGrpcServiceBlockingStub,
) {
    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Get("/pix")
    fun cadastrar(@PathVariable clienteId: String): HttpResponse<Any> {
        LOGGER.info("criando uma chave pix para o cliente com id = $clienteId")

        val responseGrpc =
            grpcClient.listaChavesPix(ListaChavesPixRequest.newBuilder().setClienteId(clienteId).build())

        val listaDeChaves = responseGrpc.chavePixList.map { ChavePixResponse(it) }

        return HttpResponse.ok(listaDeChaves)
    }
}