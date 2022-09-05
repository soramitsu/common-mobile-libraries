package jp.co.soramitsu.xnetworking.sorawallet.envbuilder

import jp.co.soramitsu.xnetworking.networkclient.SoramitsuNetworkClient
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SoraEnv(
    @SerialName("DEFAULT_NETWORKS")
    val nodes: List<NodeInfo>
)

@Serializable
data class NodeInfo(
    val chain: String,
    val name: String,
    val address: String,
)

class SoraEnvBuilder(
    private val client: SoramitsuNetworkClient,
    private val baseUrl: String
) {

    suspend fun getSoraEnv(): SoraEnv =
        client.createJsonRequest(baseUrl)
}
