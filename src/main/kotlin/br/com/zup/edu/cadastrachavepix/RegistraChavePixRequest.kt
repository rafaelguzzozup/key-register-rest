package br.com.zup.edu.cadastrachavepix

import br.com.zup.edu.NovaChavePixRequest
import br.com.zup.edu.TipoChavePix

import br.com.zup.edu.novachavepix.validador.ChavePixValida
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import br.com.zup.edu.TipoConta  as TipoContaGrpc

@ChavePixValida
@Introspected
class RegistraChavePixRequest(
    @field:NotNull val tipoChave: TipoChave,
    @field:Size(max = 77) val valor: String,
    @field:NotNull val tipoConta: TipoConta,
) {

    fun converteParaRequestGrpc(clientId: String): NovaChavePixRequest {
        return NovaChavePixRequest.newBuilder()
            .setIdCliente(clientId)
            .setTipoConta(tipoConta.atributoGrpc ?: TipoContaGrpc.TIPO_CONTA_DESCONHECIDO)
            .setTipoChave(tipoChave.atributoGrpc ?: TipoChavePix.TIPO_CHAVE_DESCONHECIDO)
            .setValor(valor ?: "")
            .build()
    }
}

