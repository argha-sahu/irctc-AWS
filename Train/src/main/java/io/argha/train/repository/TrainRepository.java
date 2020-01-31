package io.argha.train.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.argha.train.entity.Train;

public interface TrainRepository extends JpaRepository<Train, String> {
	
}
