package my.slowfixxit.salarystats.data.repository

import my.slowfixxit.salarystats.domain.models.DefaultServiceTypes
import my.slowfixxit.salarystats.domain.models.Visit
import my.slowfixxit.salarystats.domain.models.VisitService
import my.slowfixxit.salarystats.domain.repository.IVisitRepository
import java.time.LocalDate

class FakeVisitRepository : IVisitRepository {
    private val visits: MutableList<Visit> = mutableListOf(
        Visit(
            clientName = "Иванова А.В.",
            date = LocalDate.now(),
            services = listOf(
                VisitService(DefaultServiceTypes[0], 8500.0), // Массаж
                VisitService(DefaultServiceTypes[1], 2500.0)  // СПА
            )
        ),
        Visit(
            clientName = "Петрова М.С.",
            date = LocalDate.now(),
            services = listOf(
                VisitService(DefaultServiceTypes[0], 6500.0)  // Массаж
            )
        ),
        Visit(
            clientName = "Сидорова К.Н.",
            date = LocalDate.of(2026, 4, 17),
            services = listOf(
                VisitService(DefaultServiceTypes[0], 8500.0),
                VisitService(DefaultServiceTypes[2], 1500.0)  // Доп. услуга
            )
        )
    )

    override fun getAll(): List<Visit> {
        return visits
    }

    override fun get(id: String): Visit? {
        return visits.firstOrNull { it.id == id }
    }

    override fun add(visit: Visit) {
        visits.add(visit)
    }

    override fun update(visit: Visit) {
        val index = visits.indexOfFirst { it.id == visit.id }
        if (index != -1) {
            visits[index] = visit
        }
    }

    override fun remove(id: String) {
        visits.removeIf { it.id == id }
    }
}