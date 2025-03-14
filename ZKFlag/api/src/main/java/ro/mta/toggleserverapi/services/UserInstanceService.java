package ro.mta.toggleserverapi.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.entities.*;
import ro.mta.toggleserverapi.exceptions.UserNotFoundException;
import ro.mta.toggleserverapi.repositories.InstanceRepository;
import ro.mta.toggleserverapi.repositories.UserInstanceRepository;
import ro.mta.toggleserverapi.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class UserInstanceService {
    private final UserInstanceRepository userInstanceRepository;
    private final UserRepository userRepository;
    private final InstanceRepository instanceRepository;

    public UserInstance fetchByInstanceAndUserId(Instance instance, Long userId) {
        return userInstanceRepository.findByInstanceAndUserId(instance, userId)
                .orElseThrow(() -> new UserNotFoundException(userId, instance.getId()));
    }

    public void saveUserInstance(User user, Instance instance) {
        UserInstanceKey userInstanceKey = new UserInstanceKey();
        userInstanceKey.setUserId(user.getId());
        userInstanceKey.setInstanceId(instance.getId());

        UserInstance userInstance = new UserInstance();
        userInstance.setId(userInstanceKey);
        userInstance.setUser(user);
        userInstance.setInstance(instance);
        userInstance.setAddedAt(LocalDateTime.now());

        userInstanceRepository.save(userInstance);
    }

    public void addAccessToInstance(User user, Instance instance) {
        saveUserInstance(user, instance);
    }

    @Transactional
    public void removeAccessFromInstance(Instance instance, Long userId) {
        userInstanceRepository.deleteByInstanceAndUserId(instance, userId);
    }

    public List<UserInstance> getUserInstanceByInstanceId(Long instanceId) {
        return userInstanceRepository.findAllByInstanceId(instanceId);
    }

    public List<UserInstance> getUserInstancesByUserId(Long userId) {
        return userInstanceRepository.findByUserId(userId);
    }
}