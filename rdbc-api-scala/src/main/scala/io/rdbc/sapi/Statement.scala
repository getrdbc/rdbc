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

import io.rdbc.ImmutIndexedSeq
import org.reactivestreams.Publisher

import scala.concurrent.Future

/**
  * Represent a SQL statement
  *
  * Methods of this trait allow to bind argument values to parameters
  * either by name or index. Classes extending this trait can be used
  * as an alternative to `sql` string interpolator - [[SqlInterpolator]].
  *
  * @groupname primary Primary methods
  * @groupprio primary 0
  *
  * @groupname fut Future utils
  * @groupprio fut 10
  *
  * @define bindExceptions
  *  - [[io.rdbc.api.exceptions.MissingParamValException MissingParamValException]]
  *  when some parameter value was not provided
  *  - [[io.rdbc.api.exceptions.NoSuitableConverterFoundException NoSuitableConverterFoundException]]
  *  when some parameter value's type is not convertible to a database type
  */
trait Statement {

  /** Binds value to each parameter by name.
    *
    * Example:
    * {{{
    * val insert = conn.insert("insert into table(p1, p2) values (:p1, :p2)")
    * insert.map(_.bind("p1" -> "str", "p2" -> 10L)).foreach(_.execute())
    * }}}
    *
    * Throws:
    * $bindExceptions
    *
    * @group primary
    */
  def bind(args: (String, Any)*): ExecutableStatement

  /** Binds value to each parameter by name and wraps a result in a Future.
    *
    * This is not an asynchronous operation, it's only an utility method
    * that wraps `bind` result with a [[scala.concurrent.Future Future]]
    * to allow chaining bind operations with asynchronous operations.
    *
    * Example:
    * {{{
    * for {
    *   insert <- conn.insert("insert into table(p1, p2) values (:p1, :p2)")
    *   bound <- insert.bindF("p1" -> "str", "p2" -> 10L)
    *   _ <- bound.execute()
    * } yield ()
    * }}}
    *
    * Resulting future can fail with:
    * $bindExceptions
    *
    * @group fut
    */
  def bindF(args: (String, Any)*): Future[ExecutableStatement]

  /** Binds value to each parameter by index.
    *
    * Parameters are ordered, each value in `params` sequence will be bound
    * to the corresponding parameter.
    *
    * Example:
    * {{{
    * val insert = conn.insert("insert into table(p1, p2) values (:p1, :p2)")
    * insert.map(_.bind("str", 10L)).foreach(_.execute())
    * }}}
    *
    * Throws:
    * $bindExceptions
    *
    * @group primary
    */
  def bindByIdx(args: Any*): ExecutableStatement

  /** Binds value to each parameter by index and wraps a result in a Future.
    *
    * Parameters are ordered, each value in `params` sequence will be bound
    * to the corresponding parameter.
    *
    * This is not an asynchronous operation, it's only an utility method
    * that wraps `bindByIdx` result with a [[scala.concurrent.Future Future]]
    * to allow chaining bind operations with asynchronous operations.
    *
    * Example:
    * {{{
    * for {
    *   insert <- conn.insert("insert into table(p1, p2) values (:p1, :p2)")
    *   bound <- insert.bindByIdxF("str", 10L)
    *   _ <- bound.execute()
    * } yield ()
    * }}}
    *
    * Resulting future can fail with:
    * $bindExceptions
    *
    * @group fut
    */
  def bindByIdxF(args: Any*): Future[ExecutableStatement]

  /** Creates an executable version of the statement object without
    * providing any arguments.
    *
    * Example:
    * {{{
    * val insert = conn.statement("insert into table(p1, p2) values ('str', 10)")
    * insert.map(_.noArgs).foreach(_.execute())
    * }}}
    *
    * @group primary
    */
  def noArgs: ExecutableStatement

  /** Creates an executable version of the statement object without providing
    * any arguments and wraps a result in a future
    *
    * This is not an asynchronous operation, it's only an utility method
    * that wraps `noArgs` result with a [[scala.concurrent.Future Future]]
    * to allow chaining bind operations with asynchronous operations.
    *
    * Example:
    * {{{
    * for {
    *   insert     <- conn.statement("insert into table(p1, p2) values ('str', 10)")
    *   executable <- insert.noArgsF
    *   _ <- bound.execute()
    * } yield ()
    * }}}
    *
    * @group fut
    */
  def noArgsF: Future[ExecutableStatement]

  /** Streams statement named arguments to a database.
    *
    * This method can be used to repeatedly execute this statement with
    * published arguments by leveraging Reactive Streams specification's
    * `Publisher` with a back-pressure. Each published element is a map
    * containing all arguments required by this statement.
    *
    * Resulting future can fail with:
    *  - [[io.rdbc.api.exceptions.MissingParamValException MissingParamValException]]
    * when some parameter value was not provided
    *  - [[io.rdbc.api.exceptions.NoSuitableConverterFoundException NoSuitableConverterFoundException]]
    * when some parameter value's type is not convertible to a database type
    */
  def streamArgs(argsPublisher: Publisher[Map[String, Any]]): Future[Unit]

  /** Streams statement arguments to a database.
    *
    * This method can be used to repeatedly execute this statement with
    * published arguments by leveraging Reactive Streams specification's
    * `Publisher` with a back-pressure. Each published element is an indexed
    * sequence containing all arguments required by this statement.
    *
    * Resulting future can fail with:
    *  - [[io.rdbc.api.exceptions.MissingParamValException MissingParamValException]]
    * when some parameter value was not provided
    *  - [[io.rdbc.api.exceptions.NoSuitableConverterFoundException NoSuitableConverterFoundException]]
    * when some parameter value's type is not convertible to a database type
    */
  def streamArgsByIdx(argsPublisher: Publisher[ImmutIndexedSeq[Any]]): Future[Unit]
}
