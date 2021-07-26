package br.com.zup.edu.cadastrachavepix

import br.com.zup.edu.TipoConta as TipoContaGrpc

enum class TipoConta(val atributoGrpc: TipoContaGrpc) {

    CONTA_CORRENTE(TipoContaGrpc.CONTA_CORRENTE),

    CONTA_POUPANCA(TipoContaGrpc.CONTA_POUPANCA)
}