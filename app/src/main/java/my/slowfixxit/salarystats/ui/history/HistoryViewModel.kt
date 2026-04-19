package my.slowfixxit.salarystats.ui.history

import androidx.lifecycle.ViewModel
import my.slowfixxit.salarystats.data.repository.FakeVisitRepository
import my.slowfixxit.salarystats.domain.models.Visit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryViewModel : ViewModel() {
    private val repository = FakeVisitRepository()

    private val _visits = MutableStateFlow<List<Visit>>(emptyList())
    val visits: StateFlow<List<Visit>> = _visits.asStateFlow()

    init {
        loadVisits()
    }

    fun loadVisits() {
        _visits.value = repository.getAll()
    }

    fun addVisit(visit: Visit) {
        repository.add(visit)
        loadVisits()
    }

    fun removeVisit(id: String) {
        repository.remove(id)
        loadVisits()
    }

    fun updateVisit(visit: Visit) {
        repository.update(visit)
        loadVisits()
    }
}