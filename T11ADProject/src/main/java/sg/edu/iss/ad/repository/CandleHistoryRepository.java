package sg.edu.iss.ad.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sg.edu.iss.ad.model.CandleHistory;

public interface CandleHistoryRepository extends JpaRepository<CandleHistory, Long> {
	@Query("Select ch from CandleHistory ch WHERE ch.Stock.StockTicker=:stockticker")
	public List<CandleHistory> getcandlehistory(@Param("stockticker") String stockticker);
}
