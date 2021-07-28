package br.com.zup.edu.listachavepix

import br.com.zup.edu.ListaChavesPixResponse
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class ChavePixResponse(chavePixResponse: ListaChavesPixResponse.ChavePixResponse) {

    val id = chavePixResponse.pixId
    val chave = chavePixResponse.valor
    val tipo = chavePixResponse.tipoChave
    val tipoDeConta = chavePixResponse.tipoConta
    val criadaEm = chavePixResponse.criadaEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }
}