package jp.co.soramitsu.xnetworking.basic.datasources.polkaswapconfig.impl.models

import jp.co.soramitsu.xnetworking.basic.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.models.AbstractRestServerRequest

internal data class InternalGetRequest(
    private val requestUrl: String
): AbstractRestServerRequest() {

    override fun getUrl(): String {
        return requestUrl
    }

    override fun getResponseContentType(): RestClient.ContentType {
        return RestClient.ContentType.JSON
    }

}