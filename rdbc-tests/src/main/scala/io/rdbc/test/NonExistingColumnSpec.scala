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

package io.rdbc.test

import io.rdbc.api.exceptions.InvalidQueryException
import io.rdbc.sapi._
import io.rdbc.test.util.Subscribers

import scala.concurrent.Future

trait NonExistingColumnSpec
  extends RdbcSpec
    with TableSpec {

  protected def arbitraryDataType: String
  override protected def columnsDefinition = s"col $arbitraryDataType"

  final case class WrongStatement(stmt: Future[ExecutableStatement], errPos: Int)

  "Error should be returned when referencing a non-existent column when" - {
    stmtTest("Select", (c, t) =>
      WrongStatement(
        c.statement(sql"select nonexistent from #$t"),
        errPos = 8
      )
    )

    stmtTest("Insert", (c, t) =>
      WrongStatement(
        c.statement(sql"insert into #$t(nonexistent) values (1)"),
        errPos = 14 + t.length
      )
    )

    stmtTest("Returning insert", (c, t) =>
      WrongStatement(
        c.statement(sql"insert into #$t(nonexistent) values (1)", StatementOptions.ReturnGenKeys),
        errPos = 14 + t.length
      )
    )

    stmtTest("Delete", (c, t) =>
      WrongStatement(
        c.statement(sql"delete from #$t where nonexistent = 1"),
        errPos = 20 + t.length)
    )

    stmtTest("Update", (c, t) =>
      WrongStatement(
        c.statement(sql"update #$t set nonexistent = 1"),
        errPos = 13 + t.length)
    )

    stmtTest("DDL", (c, t) =>
      WrongStatement(
        c.statement(sql"alter table #$t drop column nonexistent"),
        errPos = 26 + t.length)
    )
  }

  private def stmtTest(stmtType: String, stmt: (Connection, String) => WrongStatement): Unit = {
    s"executing a $stmtType for" - {
      executedFor("nothing", _.execute())
      executedFor("set", _.executeForSet())
      executedFor("value", _.executeForValue(_.int(1)))
      executedFor("first row", _.executeForFirstRow())
      executedFor("optional value", _.executeForValueOpt(_.intOpt(1)))
      executedFor("generated key", _.executeForKey[String])
      executedFor("stream", _.executeForStream().flatMap { rs =>
        val subscriber = Subscribers.eager()
        rs.rows.subscribe(subscriber)
        subscriber.rows
      })

      def executedFor[A](executorName: String, executor: ExecutableStatement => Future[A]): Unit = {
        s"executed for $executorName" in { c =>
          withTable(c) { t =>
            val wrongStmt = stmt(c, t)
            assertInvalidQueryThrown(wrongStmt.errPos) {
              wrongStmt.stmt.flatMap(executor)
            }
          }
        }
      }
    }
  }

  private def assertInvalidQueryThrown(errPos: Int)(body: => Future[Any]): Unit = {
    val e = intercept[InvalidQueryException] {
      body.get
    }
    e.errorPosition.fold(alert("non-fatal: no error position reported")) {
      pos =>
        pos shouldBe errPos
    }
  }
}
