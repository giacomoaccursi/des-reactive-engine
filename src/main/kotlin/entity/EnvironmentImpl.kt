/*
 * Copyright (c) 2023. Accursi Giacomo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package entity

import flow.CustomMutableStateFlow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * An implementation of the environment.
 */
class EnvironmentImpl : Environment {
    override val nodes: CustomMutableStateFlow<List<Node>> =
        CustomMutableStateFlow(MutableStateFlow(emptyList()))
    override val neighborhoods: CustomMutableStateFlow<Map<Int, Neighborhood>> =
        CustomMutableStateFlow(MutableStateFlow(emptyMap()))
    override val nodesToPosition: CustomMutableStateFlow<Map<Int, Position>> =
        CustomMutableStateFlow(MutableStateFlow(emptyMap()))
    private lateinit var linkingRule: LinkingRule

    override suspend fun addNode(node: Node, position: Position) {
        nodes.emit(nodes.value + node)
        nodesToPosition.emit(nodesToPosition.value + Pair(node.id, position))
    }

    override suspend fun updateNeighborhoods(neighborhoods: Map<Int, Neighborhood>) {
        this.neighborhoods.emit(neighborhoods)
    }

    override fun setLinkingRule(linkingRule: LinkingRule) {
        this.linkingRule = linkingRule
    }

    override suspend fun removeNode(node: Node) {
        nodes.emit(nodes.value - node)
        nodesToPosition.emit(nodesToPosition.value.minus(node.id))
    }

    override suspend fun moveNode(node: Node, position: Position) {
        nodesToPosition.emit(nodesToPosition.value + Pair(node.id, position))
    }

    override fun getNodePosition(node: Node): Position {
        nodesToPosition.value[node.id].also {
            if (it == null) {
                val nodeExists = nodes.value.contains(node)
                check(!nodeExists) {
                    ("Node $node is registered in the environment, but it has no position.")
                }
                throw IllegalArgumentException("Node ${node.id} does not exist in the environment.")
            } else {
                return it
            }
        }
    }

    override fun getNeighborhood(node: Node): Neighborhood? {
        return neighborhoods.value[node.id]
    }

    override fun getNodeFromId(id: Int) = nodes.value.first { it.id == id }

    override fun getAllNodes() = nodes.value.filter {
        // it ensures that all nodes returned have a position.
        it.id in nodesToPosition.value.keys
    }

    /*private fun updateNeighborhood(node: Node) {
        val newNeighborhood = SimpleNeighborhood(node, linkingRule.computeNeighbors(node, this))
        val oldNeighborhood = getNeighborhood(node)
        var eventToNotify = 0
        if (oldNeighborhood != null) {
            val lostNeighbors = oldNeighborhood.neighbors - newNeighborhood.neighbors
            val gainedNeighbors = newNeighborhood.neighbors - oldNeighborhood.neighbors
            eventToNotify += lostNeighbors.flatMap { it.events.value }
                .count() + gainedNeighbors.flatMap { it.events.value }
                .count()
            lostNeighbors.forEach { neighbor ->
                val nodeNeighborhood = getNeighborhood(neighbor)
                if (nodeNeighborhood != null) {
                    neighborhoods.value += Pair(neighbor.id, nodeNeighborhood.removeNeighbor(node))
                }
            }
            gainedNeighbors.forEach { neighbor ->
                val nodeNeighborhood = getNeighborhood(neighbor)
                if (nodeNeighborhood != null) {
                    neighborhoods.value += Pair(neighbor.id, nodeNeighborhood.addNeighbor(node))
                }
            }
        } else {
            newNeighborhood.neighbors.forEach { neighbor ->
                val nodeNeighborhood = getNeighborhood(neighbor)
                if (nodeNeighborhood != null) {
                    neighborhoods.value += Pair(neighbor.id, nodeNeighborhood.addNeighbor(node))
                }
            }
        }
        neighborhoods.value += Pair(node.id, newNeighborhood)
        eventToNotify += node.events.value.count()
        println(eventToNotify)
    }*/
}
