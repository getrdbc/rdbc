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

package io.rdbc.core.api.exceptions

sealed abstract class ResultProcessingException(msg: String) extends RdbcException(msg)

object ResultProcessingException {

  case class ConversionException(any: Any, targetType: Class[_]) extends ResultProcessingException(s"Value '$any' could not be converted to ${targetType.getCanonicalName}")

  case class MissingColumnException(column: String) extends ResultProcessingException(s"Requested column '$column' is not present in the row")

  case class NoSuitableConverterFoundException(any: Any) extends ResultProcessingException(s"No suitable converter was found for value '$any' of type ${any.getClass}")

  case class UnsupportedDbTypeException(dbTypeDesc: String) extends ResultProcessingException(s"Database native type '$dbTypeDesc' is not supported")
}