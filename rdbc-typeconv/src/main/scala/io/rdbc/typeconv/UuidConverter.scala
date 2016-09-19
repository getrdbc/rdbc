/*
 * Copyright 2016 Krzysztof Pado
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rdbc.typeconv

import java.util.UUID

import io.rdbc.api.exceptions.ResultProcessingException.ConversionException
import io.rdbc.sapi.TypeConverter

object UuidConverter extends TypeConverter[UUID] {
  val cls = classOf[UUID]

  override def fromAny(any: Any): UUID = any match {
    case uuid: UUID => uuid
    case str: String =>
      try {
        UUID.fromString(str)
      } catch {
        case _: IllegalArgumentException => throw ConversionException(any, classOf[UUID])
      }

    case _ => throw ConversionException(any, classOf[UUID])
  }
}
