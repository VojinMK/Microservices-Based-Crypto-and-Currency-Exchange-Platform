package TradeService;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeServiceRepository extends JpaRepository<TradeServiceModel, Integer> {

	TradeServiceModel findByFromAndTo(String from,String to);
}
