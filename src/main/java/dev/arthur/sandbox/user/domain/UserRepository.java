package dev.arthur.sandbox.user.domain;

import java.util.List;

public interface UserRepository {

    List<User> findAll();

    User findById(long id);
}
