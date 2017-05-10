/*
 * Copyright 2016-2017 Krzysztof Pado
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

package io.rdbc.sapi

import io.rdbc.ImmutSeq

/** Specification of columns that generate keys */
sealed trait KeyColumns

object KeyColumns {
  /** Return keys from the column names given */
  def named(cols: String*): KeyColumns.Named = KeyColumns.Named(cols.toVector)

  /** Return all keys */
  case object All extends KeyColumns

  /** Don't return any keys */
  case object None extends KeyColumns

  /** Return keys from the column names given */
  final case class Named(cols: ImmutSeq[String]) extends KeyColumns

}

object StatementOptions {

  /** Default statement options */
  val Default = StatementOptions(
    generatedKeyCols = KeyColumns.None
  )

  /** Options that make statement return all generated keys */
  val ReturnGenKeys: StatementOptions = Default.copy(generatedKeyCols = KeyColumns.All)
}

/**
  * Statement options.
  *
  * @param generatedKeyCols says what keys generated by the database should be returned
  */
final case class StatementOptions(generatedKeyCols: KeyColumns)
