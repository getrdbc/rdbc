/*
 * Copyright 2016 rdbc contributors
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

import java.time.{LocalDate, LocalDateTime}

import io.rdbc.sapi.TypeConverter
import io.rdbc.sapi.exceptions.ConversionException

object LocalDateTimeConverter extends TypeConverter[LocalDateTime] {
  val cls = classOf[LocalDateTime]

  override def fromAny(any: Any): LocalDateTime = any match {
    case ldt: LocalDateTime => ldt
    case ld: LocalDate => ld.atStartOfDay()
    case _ => throw new ConversionException(any, classOf[LocalDateTime])
  }
}
