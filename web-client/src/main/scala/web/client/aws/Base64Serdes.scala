package fun.web.client.aws

import chameleon.{Serializer, Deserializer}
import cats.implicits._
import java.nio.ByteBuffer
import com.github.marklister.base64.Base64._

object Base64Serdes {

  implicit def serializer[T: Serializer[*, ByteBuffer]]: Serializer[T, String] =
    Serializer[T, ByteBuffer].mapSerialize { bytes =>
      val byteArray =
        if (bytes.hasArray) bytes.array
        else {
          val array = new Array[Byte](bytes.remaining)
          bytes.rewind()
          bytes.get(array)
          array
        }
      byteArray.toBase64
    }

  implicit def deserializer[T: Deserializer[*, ByteBuffer]]: Deserializer[T, String] =
    Deserializer[T, ByteBuffer].flatmapDeserialize { base64 =>
      Either.catchNonFatal(base64.toByteArray).map(ByteBuffer.wrap)
    }
}
