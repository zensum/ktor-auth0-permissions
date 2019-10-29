package se.zensum.ktor

import com.auth0.jwt.interfaces.Claim
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.Instant
import java.util.*

data class JsonNodeClaim(private val node: JsonNode): Claim {
    constructor(map: Map<String, Any?>):
        this(jacksonObjectMapper().convertValue<JsonNode>(map))

    constructor(vararg values: Pair<String, Any?>): this(values.toMap())

    override fun isNull(): Boolean = node.isNull

    override fun asBoolean(): Boolean? =
        if(node.isBoolean) node.asBoolean() else null

    override fun asInt(): Int? = if(node.isInt) node.asInt() else null

    override fun asLong(): Long? =
        if(node.isLong || node.isInt) node.asLong() else null

    override fun asDouble(): Double? =
        if(node.isDouble) node.asDouble() else null

    private inline fun <T> tryParse(parse: () -> T?): T? {
        return try {
            parse()
        } catch(e: JsonProcessingException) {
            null
        }
    }

    override fun <T> `as`(clazz: Class<T>): T? {
        return if(node.isObject) {
            tryParse {
                val parser: JsonParser = jacksonObjectMapper().treeAsTokens(node)
                parser.readValueAs(clazz)
            }
        } else {
            null
        }
    }

    override fun asMap(): Map<String, Any?>? {
        return if(node.isObject) {
            tryParse {
                val parser: JsonParser = jacksonObjectMapper().treeAsTokens(node)
                parser.readValueAs<Map<String, Any?>>(mapType)
            }
        } else {
            null
        }
    }

    override fun asString(): String? =
        if(node.isTextual) node.textValue() else null

    fun asInstant(): Instant? = asLong()?.let {
        Instant.ofEpochSecond(it)
    }

    override fun asDate(): Date? = asInstant()?.let {
        Date.from(it)
    }

    override fun <T> asList(tClazz: Class<T>): List<T>? {
        return if(node.isArray) {
            tryParse {
                node.asSequence()
                    .map { jacksonObjectMapper().treeToValue(it, tClazz) }
                    .toList()
            }
        } else {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: Any?> asArray(tClazz: Class<T>): Array<T>? {
        return if(node.isArray) {
            tryParse {
                val array = Array<Any?>(node.size()) { i ->
                    val value: JsonNode = node.get(i)
                    jacksonObjectMapper().convertValue(value, tClazz)
                }
                array as? Array<T>
            }
        } else {
            null
        }
    }

    companion object {
        private val mapType = object: TypeReference<Map<String, Any?>>() {}

        fun fromObject(obj: Any): JsonNodeClaim {
            val node: JsonNode = jacksonObjectMapper().convertValue(obj)
            return JsonNodeClaim(node)
        }
    }
}