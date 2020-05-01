package com.github.adenza.xmlrpc.client

import java.time._
import java.time.format.DateTimeFormatter
import java.util.{Date, GregorianCalendar}

import com.github.adenza.xmlrpc.exceptions.XmlRpcDeserializationException

import scala.reflect.runtime.universe._
import scala.reflect.{ClassTag, classTag}
import scala.util.{Failure, Success}

/**
  * MicroFramework to Serialize XmlRpc parameters and deserialize responses to Case Classes
  *
  * it works with org.apache.xmlrpc client
  *
  */
object XmlRpcSerializer {

  def zoneId: ZoneId = ZoneId.systemDefault()

  /**
    * Serialize Case class to xmlrpc client compatible parameters
    *
    * @param caseClass Tuple or case class with ordered parameters
    * @return
    */
  def toParams(caseClass: Product): Array[AnyRef] =
    caseClass.productIterator.to.map {
      case localDate: LocalDate =>
        java.util.Date.from(localDate.atStartOfDay(zoneId).toInstant).asInstanceOf[AnyRef]
      case localDateTime: LocalDateTime =>
        java.util.Date.from(localDateTime.atZone(zoneId).toInstant).asInstanceOf[AnyRef]
      case offsetDateTime: OffsetDateTime =>
        java.util.Date.from(offsetDateTime.toInstant).asInstanceOf[AnyRef]
      case x => x.asInstanceOf[AnyRef]
    }.toArray

  /**
    * Convert org.apache.xmlrpc client response representable as nested hashMap into case class
    *
    * @param obj xmlrpc client response type
    * @tparam T root case class type
    * @return
    */
  def fromResponse[T: TypeTag: ClassTag](obj: java.lang.Object): T = {
    val rm = runtimeMirror(classTag[T].runtimeClass.getClassLoader)
    val tagType = typeOf[T]
    val result = fromJavaObjType(rm, tagType.typeSymbol, obj)
    result.asInstanceOf[T]
  }

  /**
    * Internal helper to work with nested values
    *
    * @param rm    runtime universe to create instances from
    * @param param symbol of current parameter type
    * @param value matched response hashMap
    * @return instance of anytype
    */
  private def fromJavaObjType(rm: Mirror, param: Symbol, value: java.lang.Object): Any = {

    /*
     * Internal helper which help to decide retrieve case class value if param
     * is type of Product or calculate actual value if type is simple
     *
     * @param param param Type
     * @param value raw value
     * @return
     */
    def productOrVal(paramName: String, paramType: Type, value: AnyRef): Any =
      scala.util.Try(
        if (paramType <:< typeOf[Product]) {
          fromJavaObjType(rm, paramType.typeSymbol, value)
        } else if (paramType <:< typeOf[OffsetDateTime]) {
          value match {
            case calendar: GregorianCalendar =>
              OffsetDateTime.ofInstant(Instant.ofEpochMilli(calendar.getTimeInMillis), zoneId)
            case _ => OffsetDateTime.ofInstant(value.asInstanceOf[Date].toInstant, zoneId)
          }
        } else if (paramType <:< typeOf[LocalDateTime]) {
          LocalDateTime.ofInstant(value.asInstanceOf[java.util.Date].toInstant, zoneId)
        } else if (paramType <:< typeOf[LocalDate]) {
          LocalDateTime.ofInstant(value.asInstanceOf[java.util.Date].toInstant, zoneId).toLocalDate
        } else if (paramType <:< typeOf[Int]) {
          value.asInstanceOf[Int]
        } else if (paramType <:< typeOf[Boolean]) {
          value.asInstanceOf[Boolean]
        } else if (paramType <:< typeOf[Double]) {
          value.asInstanceOf[Double]
        } else if (paramType <:< typeOf[Array[Byte]]) {
          value.asInstanceOf[Array[Byte]]
        } else if (paramType <:< typeOf[Byte]) {
          value.asInstanceOf[Byte]
        } else if (paramType <:< typeOf[Float]) {
          value.asInstanceOf[Float]
        } else if (paramType <:< typeOf[Long]) {
          value.asInstanceOf[Long]
        } else if (paramType <:< typeOf[Short]) {
          value.asInstanceOf[Short]
        } else if (paramType <:< typeOf[BigDecimal]) {
          scala.math.BigDecimal(value.asInstanceOf[java.math.BigDecimal])
        } else if (paramType <:< typeOf[BigInt]) {
          scala.math.BigInt(value.asInstanceOf[java.math.BigInteger])
        } else if (paramType <:< typeOf[java.util.UUID]) {
          java.util.UUID.fromString(value.toString.toLowerCase)
        } else if (paramType <:< typeOf[Iterable[_]]) {
          val javaArray = value.asInstanceOf[Array[_]]
          javaArray.map { i =>
            productOrVal("", paramType.typeArgs.head, i.asInstanceOf[AnyRef])
          }.toList
        } else {
          Class.forName(paramType.typeSymbol.fullName).cast(value)
        }
      ) match {
        case Success(value) => value
        case Failure(exception) =>
          throw new XmlRpcDeserializationException(s"Casting error [$param.$paramName]: ${exception.getMessage}")
      }

    val scalaMap = scala.collection.JavaConverters.mapAsScalaMap[String, AnyRef](
      value.asInstanceOf[java.util.HashMap[String, AnyRef]]
    )
    val symbol = param.typeSignature.typeSymbol
    val classMirror = rm.reflectClass(symbol.asClass)
    val constructor = symbol.typeSignature.decl(termNames.CONSTRUCTOR).asMethod
    val constructorMirror = classMirror.reflectConstructor(constructor)

    val constructorArgs = constructor.paramLists.flatten.map((constructorParam: Symbol) => {
      val constructorParamName = constructorParam.name.toString
      // TODO: extract to the next level to the top as here it is a mess
      // TODO: cover scenarios: Option <- Map, Option <- Array, Seq <- Array, List <- Array
      // Check for Option
      if (constructorParam.typeSignature <:< typeOf[Option[Any]]) {
        val constructorParamValue = scalaMap.get(constructorParamName)
        constructorParamValue
          .flatMap { value =>
            if (value == null) None
            else if ("java.lang.Object[]".equals(value.getClass.getTypeName))
              value.asInstanceOf[Array[java.lang.Object]].headOption
            else Option(value)
          }
          .map(value => productOrVal(constructorParamName, constructorParam.typeSignature.typeArgs.head, value))
        // Check for Seq/List
      } else if (constructorParam.typeSignature <:< typeOf[Iterable[_]]) {
        val result = scalaMap
          .getOrElse(constructorParamName, Array.empty)
          .asInstanceOf[Array[java.lang.Object]]
          .map { constructorParamValue =>
            productOrVal(constructorParamName, constructorParam.typeSignature.typeArgs.head, constructorParamValue)
          }
        if (constructorParam.typeSignature <:< typeOf[List[_]]) result.toList
        else result.toList
        // Check for value
      } else {
        val constructorParamValue = scalaMap.getOrElse(
          constructorParamName,
          throw new XmlRpcDeserializationException(
            s"Required parameter $constructor.$constructorParamName is missing in the response"
          )
        )
        productOrVal(constructorParamName, constructorParam.typeSignature, constructorParamValue)
      }
    })
    constructorMirror(constructorArgs: _*)
  }

}
