/*
 * Copyright (c) 2023. Giacomo Accursi
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

import control.EngineImpl
import control.ListScheduler
import entity.ContentImpl
import entity.DoubleTime
import entity.DummyCondition
import entity.EnvironmentImpl
import entity.EventImpl
import entity.MoveNodeAction
import entity.NodeImpl
import entity.Position
import entity.PositionLinkingRule
import entity.SumAction
import kotlinx.coroutines.runBlocking

/**
 * Max num of steps in simulation.
 */
const val MAX_STEPS = 10

/**
 * Main Function.
 */
fun main() {
    runBlocking {
        // Simple initialization of a simulation.
        val time = DoubleTime(0.0)
        val condition = DummyCondition()
        val linkingRule = PositionLinkingRule(1.0)

        val environment = EnvironmentImpl(linkingRule)
        val node1 = NodeImpl(1)
        node1.addContent(ContentImpl(1))
        val node2 = NodeImpl(2)
        node2.addContent(ContentImpl(1))
        val node3 = NodeImpl(3)
        node3.addContent(ContentImpl(1))
        val node4 = NodeImpl(4)
        node4.addContent(ContentImpl(1))
        listOf(node1, node2, node3, node4).forEach { node ->
            environment.addNode(
                node,
                Position(0.0, 0.0),
            )
        }
        val moveAction = MoveNodeAction(environment)
        val sumAction = SumAction(environment)

        val scheduler = ListScheduler()
        val engine = EngineImpl(environment, scheduler, MAX_STEPS, time)

        val event1 = EventImpl(
            node1,
            arrayListOf(condition),
            arrayListOf(moveAction, sumAction),
            engine = engine,
            environment = environment,
        )
        val event2 = EventImpl(
            node1,
            arrayListOf(condition),
            arrayListOf(moveAction, sumAction),
            engine = engine,
            environment = environment,
        )
        node1.addEvent(event1)
        node1.addEvent(event2)

        val event3 = EventImpl(
            node2,
            arrayListOf(condition),
            arrayListOf(moveAction),
            engine = engine,
            environment = environment,
        )
        val event4 = EventImpl(
            node2,
            arrayListOf(condition),
            arrayListOf(moveAction),
            engine = engine,
            environment = environment,
        )
        node2.addEvent(event3)
        node2.addEvent(event4)

        val event5 = EventImpl(
            node3,
            arrayListOf(condition),
            arrayListOf(moveAction),
            engine = engine,
            environment = environment,
        )
        val event6 = EventImpl(
            node3,
            arrayListOf(condition),
            arrayListOf(moveAction),
            engine = engine,
            environment = environment,
        )
        node3.addEvent(event5)
        node3.addEvent(event6)

        val event7 = EventImpl(
            node4,
            arrayListOf(condition),
            arrayListOf(moveAction),
            engine = engine,
            environment = environment,
        )
        val event8 = EventImpl(
            node4,
            arrayListOf(condition),
            arrayListOf(moveAction),
            engine = engine,
            environment = environment,
        )
        val event9 = EventImpl(
            node4,
            arrayListOf(condition),
            arrayListOf(moveAction),
            engine = engine,
            environment = environment,
        )
        node4.addEvent(event7)
        node4.addEvent(event8)
        node4.addEvent(event9)
        engine.start()
    }
}
