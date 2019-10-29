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

    override fun <T: Any> asArray(tClazz: Class<T>): Array<T>? =
        asList(tClazz)?.toArray(tClazz)

    companion object {
        private val mapType = object: TypeReference<Map<String, Any?>>() {}

        fun fromObject(obj: Any): JsonNodeClaim {
            val node: JsonNode = jacksonObjectMapper().convertValue(obj)
            return JsonNodeClaim(node)
        }
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <T: Any> List<T>.toArray(clazz: Class<T>): Array<T> {
    val actualClass: Class<T> = clazz.kotlin.javaObjectType
    val array: Array<T> = java.lang.reflect.Array.newInstance(actualClass, size) as Array<T>
    this.forEachIndexed { index: Int, obj: T ->
        array[index] = obj
    }

    return array
}