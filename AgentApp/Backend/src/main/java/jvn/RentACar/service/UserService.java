package jvn.RentACar.service;

import jvn.RentACar.model.User;

public interface UserService {

    User findByEmail(String email);
}
