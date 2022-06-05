/*
 * Copyright 2021 Scala.js (https://www.scala-js.org/)
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

package org.scalajs.macrotaskexecutor

import org.junit.Test
import org.scalajs.dom.MessageEvent
import org.scalajs.dom.Worker

import scala.concurrent.Future
import scala.concurrent.Promise
import scala.util.Failure
import scala.util.Success
import scala.util.Try

class WebWorkerMacrotaskTests {

  val worker = new Worker(
    s"file://${BuildInfo.workerDir}/main.js"
  )

  def runTest(name: String): Future[Try[Unit]] = {
    val p = Promise[Try[Unit]]()
    worker.onmessage = { (event: MessageEvent) =>
      event.data match {
        case true => p.success(Success(()))
        case _ => p.success(Failure(new AssertionError))
      }
    }
    worker.postMessage(name)
    p.future
  }

  @Test
  def `sequence a series of 10,000 recursive executions without clamping` =
    runTest("sequence a series of 10,000 recursive executions without clamping")

  @Test
  def `preserve fairness with setTimeout` =
    runTest("preserve fairness with setTimeout")

  @Test
  def `execute a bunch of stuff in 'parallel' and ensure it all runs` =
    runTest("execute a bunch of stuff in 'parallel' and ensure it all runs")

}
