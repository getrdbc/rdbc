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

package io.rdbc.implbase

import io.rdbc.sapi._

import scala.concurrent.{ExecutionContext, Future}

trait ConnectionPartialImpl extends Connection {

  implicit protected def ec: ExecutionContext

  def delete(sql: String): Future[Delete] = statement(sql).map(new DeleteImpl(_))

  def insert(sql: String): Future[Insert] = statement(sql).map(new InsertImpl(_))

  def select(sql: String): Future[Select] = statement(sql).map(new SelectImpl(_))

  def update(sql: String): Future[Update] = statement(sql).map(new UpdateImpl(_))

  def delete(sqlWithParams: SqlAndParams): Future[ParametrizedDelete] = {
    parametrizedStmt(delete)(sqlWithParams)
  }

  def insert(sqlWithParams: SqlAndParams): Future[ParametrizedInsert] = {
    parametrizedStmt(insert)(sqlWithParams)
  }

  def select(sqlWithParams: SqlAndParams): Future[ParametrizedSelect] = {
    parametrizedStmt(select)(sqlWithParams)
  }

  def update(sqlWithParams: SqlAndParams): Future[ParametrizedUpdate] = {
    parametrizedStmt(update)(sqlWithParams)
  }

  def returningInsert(sqlWithParams: SqlAndParams): Future[ParametrizedReturningInsert] = {
    parametrizedStmt(returningInsert)(sqlWithParams)
  }

  def statement(sqlWithParams: SqlAndParams): Future[AnyParametrizedStatement] = {
    parametrizedStmt(statement)(sqlWithParams)
  }

  protected def parametrizedStmt[T](bindable: String => Future[Bindable[T]])(sqlWithParams: SqlAndParams): Future[T] = {
    bindable(sqlWithParams.sql).map(_.bindByIdx(sqlWithParams.params: _*))
  }
}
