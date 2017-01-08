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

package io.rdbc.sapi

import io.rdbc._
import org.reactivestreams.Publisher

import scala.concurrent.Future

/** Represents a statement result that allows rows streaming.
  *
  * @define futureCompleteNote
  * Note that resulting future may not complete until a `rows` Publisher is
  * complete. To complete this future, clients must either read the rows stream
  * until it is complete or cancel the subscription.
  */
trait ResultStream {

  /** A number of rows that were affected by the statement.
    *
    * Clients can safely assume that a database is ready to accept new queries
    * after this future completes.
    *
    * $futureCompleteNote
    */
  def rowsAffected: Future[Long]

  /** A sequence of warnings that were emitted during processing the statement.
    *
    * Clients can safely assume that a database is ready to accept new
    * queries after this future completes.
    *
    * $futureCompleteNote
    */
  def warnings: Future[ImmutSeq[Warning]]

  /** A meta data of columns of this result set */
  def metadata: RowMetadata

  /** A reactive streams specification's `Publisher` giving access to the rows.
    *
    * When this publisher signals that it is complete clients can safely assume
    * that a database is ready to accept new queries. If subscription is
    * cancelled, however, clients have to wait for [[Connection.watchForIdle]]
    * future to complete before issuing another query.
    */
  def rows: Publisher[Row]
}
