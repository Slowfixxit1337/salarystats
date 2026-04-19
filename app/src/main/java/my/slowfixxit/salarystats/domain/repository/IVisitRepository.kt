package my.slowfixxit.salarystats.domain.repository

import my.slowfixxit.salarystats.domain.models.Visit

interface IVisitRepository {
    fun getAll(): List<Visit>
    fun get(id: String): Visit?
    fun add(visit: Visit)
    fun update(visit: Visit)
    fun remove(id: String)
}