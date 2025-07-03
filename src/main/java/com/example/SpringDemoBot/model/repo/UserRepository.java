package com.example.SpringDemoBot.model.repo;

import com.example.SpringDemoBot.model.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
