package com.ecom.ecommerce_backend.service;

import com.ecom.ecommerce_backend.api.model.LoginBody;
import com.ecom.ecommerce_backend.api.model.RegistrationBody;
import com.ecom.ecommerce_backend.exception.UserAlreadyExistsException;
import com.ecom.ecommerce_backend.model.LocalUser;
import com.ecom.ecommerce_backend.model.dao.LocalUserDAO;
import jakarta.validation.Valid;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private LocalUserDAO localUserDAO;
    private EncryptionService encryptionService;
    private JWTService jwtService;

    public UserService(LocalUserDAO localUserDAO, EncryptionService encryptionService, JWTService jwtService){
        this.localUserDAO = localUserDAO;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException {
        if (localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
            || localUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()){
            throw new UserAlreadyExistsException();
        }
        LocalUser user = new LocalUser();
        user.setEmail(registrationBody.getEmail());
        user.setFirst_name(registrationBody.getFirst_name());
        user.setLast_name(registrationBody.getLast_name());
        user.setUsername(registrationBody.getUsername());
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));

        user = localUserDAO.save(user);
        return user;
    }

    public String loginUser(LoginBody loginBody){
        Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(loginBody.getUsername());
        if(opUser.isPresent()){
            LocalUser user = opUser.get();
            if(encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())){
                return jwtService.generateJWT(user);
            }
        }
        return null;
    }
}
