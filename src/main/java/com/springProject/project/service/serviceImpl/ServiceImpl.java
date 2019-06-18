package com.springProject.project.service.serviceImpl;

import com.springProject.project.exceptions.UserServiceException;
import com.springProject.project.UserRepository;
import com.springProject.project.io.entity.PasswordResetTokenEntity;
import com.springProject.project.io.entity.UserEntity;
import com.springProject.project.io.repositories.PasswordResetTokenRepository;
import com.springProject.project.iu.model.response.ErrorMessages;
import com.springProject.project.service.UserService;
import com.springProject.project.shared.AmazonSES;
import com.springProject.project.shared.Utils;
import com.springProject.project.shared.dto.AddressDto;
import com.springProject.project.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


@org.springframework.stereotype.Service
public class ServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    Utils utils;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    AmazonSES amazonSES;


    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()) != null)
            throw new UserServiceException("Record already exists");

        for (int i = 0; i < userDto.getAddresses().size(); i++) {
            AddressDto address = userDto.getAddresses().get(i);
            address.setUserDetails(userDto);
            address.setAddressId(utils.generateAddressId(30));
            userDto.getAddresses().set(i, address);
        }

        ModelMapper mapper = new ModelMapper();
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

        String userId = utils.generateUserId(30);
        userEntity.setUserId(userId);
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(userId));

        UserEntity returnEntity = userRepository.save(userEntity);
        UserDto returnDto = mapper.map(returnEntity, UserDto.class);

        amazonSES.verifyEmail(returnDto);
        return returnDto;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);
        UserDto returnDto = new UserDto();
        BeanUtils.copyProperties(userEntity, returnDto);
        return returnDto;
    }

    @Override
    public UserDto getUserByUserId(String id) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null) throw new UsernameNotFoundException(id);
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        UserEntity updatedUser = userRepository.save(userEntity);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String id) {
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();
        if (page > 0) page--;
        Pageable pageRequest = PageRequest.of(page, limit);
        Page<UserEntity> pages = userRepository.findAll(pageRequest);
        List<UserEntity> users = pages.getContent();
        for (UserEntity entity : users) {
            UserDto dto = new UserDto();
            BeanUtils.copyProperties(entity, dto);
            returnValue.add(dto);
        }
        return returnValue;
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnValue = false;
        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);
        if (userEntity != null) {
            boolean hasTokenExpired = utils.hasTokenExpired(token);
            if (!hasTokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                returnValue = true;
            }
        }

        return returnValue;
    }

    @Override
    public boolean requestPasswordReset(String email) {
        boolean returnValue=false;
        UserEntity userEntity=userRepository.findByEmail(email);
        if (userEntity==null){
            return returnValue;
        }
        String token=utils.generatePasswordResetToken(userEntity.getUserId());

        PasswordResetTokenEntity passwordResetTokenEntity=new PasswordResetTokenEntity();
        passwordResetTokenEntity.setToken(token);
        passwordResetTokenEntity.setUserDetails(userEntity);
        passwordResetTokenRepository.save(passwordResetTokenEntity);

        returnValue=new AmazonSES().sendPasswordResetRequest(userEntity.getFirstName(),userEntity.getEmail(),token);

        return returnValue;
    }

    @Override
    public boolean resetPassword(String token, String password) {
        boolean returnValue=false;

        if (utils.hasTokenExpired(token)){
            return returnValue;
        }
        PasswordResetTokenEntity passwordResetTokenEntity=passwordResetTokenRepository.findByToken(token);

        if(passwordResetTokenEntity == null){
            return returnValue;
        }
        String encodedPassword=bCryptPasswordEncoder.encode(password);
        UserEntity userEntity=passwordResetTokenEntity.getUserDetails();
        userEntity.setEncryptedPassword(encodedPassword);
        UserEntity savedUserEntity=userRepository.save(userEntity);

        if (savedUserEntity!=null &&savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)){
            returnValue=true;
        }

        passwordResetTokenRepository.delete(passwordResetTokenEntity);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);
        //return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
                userEntity.getEmailVerificationStatus(),
                true,true,
                true,new ArrayList<>());
    }
}
